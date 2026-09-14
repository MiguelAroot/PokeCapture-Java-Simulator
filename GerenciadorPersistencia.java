import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GerenciadorPersistencia {

    private static File arquivoSaveAtual = new File("save.txt");
    private static final File PASTA_SAVES = new File("saves");

    public static void definirArquivoSave(File arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("Arquivo de save não pode ser nulo.");
        }
        arquivoSaveAtual = arquivo;
    }

    public static File getArquivoSaveAtual() {
        return arquivoSaveAtual;
    }

    public static File getPastaSaves() {
        if (!PASTA_SAVES.exists()) {
            PASTA_SAVES.mkdirs();
        }
        return PASTA_SAVES;
    }

    public static List<File> listarSaves() {
        List<File> saves = new ArrayList<>();

        File saveAntigo = new File("save.txt");
        if (saveAntigo.isFile()) {
            saves.add(saveAntigo);
        }

        File pasta = getPastaSaves();
        File[] arquivos = pasta.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".txt"));
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isFile()) saves.add(arquivo);
            }
        }

        saves.sort(Comparator.comparingLong(File::lastModified).reversed());
        return saves;
    }

    public static File criarArquivoNovoSave(String nome) {
        String seguro = nome == null ? "" : nome.trim();
        seguro = seguro.replaceAll("[^\\p{L}\\p{N}_ -]", "");
        seguro = seguro.replaceAll("\\s+", "_");
        if (seguro.isBlank()) seguro = "novo_jogo";

        File pasta = getPastaSaves();
        File candidato = new File(pasta, seguro + ".txt");
        int contador = 2;
        while (candidato.exists()) {
            candidato = new File(pasta, seguro + "_" + contador + ".txt");
            contador++;
        }
        return candidato;
    }

    public static boolean excluirSave(File arquivo) {
        return arquivo != null && arquivo.isFile() && arquivo.delete();
    }

    public static boolean existeSave() {
        return arquivoSaveAtual.exists();
    }

    public static void salvar(Jogador jogador) throws PersistenciaException {
        File pai = arquivoSaveAtual.getParentFile();
        if (pai != null && !pai.exists() && !pai.mkdirs()) {
            throw new PersistenciaException("Não foi possível criar a pasta do save.");
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(arquivoSaveAtual))) {
            bw.write("SALDO=" + jogador.getSaldo());
            bw.newLine();
            bw.newLine();

            bw.write("INVENTARIO");
            bw.newLine();
            Map<String, Integer> contagemItens = new LinkedHashMap<>();
            for (Item item : jogador.getInventario()) {
                String chave = item.getClass().getSimpleName();
                contagemItens.put(chave, contagemItens.getOrDefault(chave, 0) + 1);
            }
            for (Map.Entry<String, Integer> entrada : contagemItens.entrySet()) {
                bw.write(entrada.getKey() + "=" + entrada.getValue());
                bw.newLine();
            }
            bw.newLine();

            bw.write("POKEDEX");
            bw.newLine();
            bw.write(juntarComVirgula(jogador.getPokedex()));
            bw.newLine();
            bw.newLine();

            bw.write("ESTOQUE");
            bw.newLine();
            StringBuilder sbEstoque = new StringBuilder();
            List<Pokemon> estoque = jogador.getEstoque();
            for (int i = 0; i < estoque.size(); i++) {
                if (i > 0) sbEstoque.append(",");
                sbEstoque.append(estoque.get(i).getNumero());
            }
            bw.write(sbEstoque.toString());
            bw.newLine();

        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível salvar o jogo: " + e.getMessage(), e);
        }
    }

    public static void carregar(Jogador jogador, List<Pokemon> sorteio) throws PersistenciaException {
        File arquivo = arquivoSaveAtual;
        if (!arquivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            jogador.getInventario().clear();
            jogador.getEstoque().clear();
            jogador.getPokedex().clear();

            String linha;
            String secaoAtual = "";

            while ((linha = br.readLine()) != null) {
                linha = linha.trim();
                if (linha.isEmpty()) continue;

                if (linha.startsWith("SALDO=")) {
                    jogador.setSaldo(Double.parseDouble(linha.substring("SALDO=".length())));
                    continue;
                }

                if (linha.equals("INVENTARIO") || linha.equals("POKEDEX") || linha.equals("ESTOQUE")) {
                    secaoAtual = linha;
                    continue;
                }

                switch (secaoAtual) {
                    case "INVENTARIO":
                        processarLinhaInventario(jogador, linha);
                        break;
                    case "POKEDEX":
                        for (String numeroStr : linha.split(",")) {
                            if (!numeroStr.isBlank()) jogador.getPokedex().add(Integer.parseInt(numeroStr.trim()));
                        }
                        break;
                    case "ESTOQUE":
                        for (String numeroStr : linha.split(",")) {
                            if (!numeroStr.isBlank()) {
                                int numero = Integer.parseInt(numeroStr.trim());
                                Pokemon encontrado = buscarPorNumero(sorteio, numero);
                                if (encontrado != null) jogador.getEstoque().add(encontrado);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }

        } catch (IOException e) {
            throw new PersistenciaException("Não foi possível ler o arquivo de save: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new PersistenciaException("Arquivo de save corrompido ou em formato inválido: " + e.getMessage(), e);
        }
    }

    private static void processarLinhaInventario(Jogador jogador, String linha) throws PersistenciaException {
        String[] partes = linha.split("=");
        if (partes.length != 2) {
            throw new PersistenciaException("Linha de inventário inválida no save: " + linha);
        }
        String tipoItem = partes[0].trim();
        int quantidade;
        try {
            quantidade = Integer.parseInt(partes[1].trim());
        } catch (NumberFormatException e) {
            throw new PersistenciaException("Quantidade inválida no save para o item " + tipoItem);
        }

        for (int i = 0; i < quantidade; i++) {
            jogador.getInventario().add(criarItemPorNomeClasse(tipoItem));
        }
    }

    private static Item criarItemPorNomeClasse(String nomeClasse) {
        switch (nomeClasse) {
            case "PokebolaComum": return new PokebolaComum();
            case "SuperPokebola": return new SuperPokebola();
            case "UltraPokebola": return new UltraPokebola();
            case "PedraEvolucao": return new PedraEvolucao();
            case "Doce": return new Doce();
            default: throw new ItemInexistenteException("Item desconhecido encontrado no save: " + nomeClasse);
        }
    }

    private static Pokemon buscarPorNumero(List<Pokemon> sorteio, int numero) {
        for (Pokemon p : sorteio) {
            if (p.getNumero() == numero) return p;
        }
        return null;
    }

    private static String juntarComVirgula(List<Integer> numeros) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeros.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(numeros.get(i));
        }
        return sb.toString();
    }
}
