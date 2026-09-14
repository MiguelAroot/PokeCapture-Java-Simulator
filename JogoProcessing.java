import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PFont;
import processing.event.MouseEvent;

import javax.sound.sampled.Clip;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Camada visual em Processing para o projeto Pokémon.
 * As regras de negócio continuam nas classes Java originais.
 */
public class JogoProcessing extends PApplet {
    // A interface é desenhada em uma resolução virtual fixa e escalada para o monitor.
    private static final int BASE_W = 1400;
    private static final int BASE_H = 800;
    private float escalaUI = 1f;
    private float offsetUIX = 0f;
    private float offsetUIY = 0f;
    private float mouseUIX = 0f;
    private float mouseUIY = 0f;
    private enum Tela { SAVES, NOVO_SAVE, MENU, STATUS, CAPTURA, INVENTARIO, ESCOLHER_POKEMON,
        LOJA, POKEDEX, DETALHE_POKEDEX, EVOLUCAO, RESULTADO_EVOLUCAO, MENSAGEM }

    private Tela tela = Tela.SAVES;
    private Tela telaAnterior = Tela.SAVES;

    private final RandomCompat rand = new RandomCompat();
    private final AudioManager audio = new AudioManager();
    private Clip musicaTitulo;
    private Clip musicaMenu;
    private Clip musicaBatalha;
    private Clip efeitoAtual;
    private Clip gritoEvolucao;
    // 0 = grito da forma atual, 1 = música/animação, 2 = grito da nova forma
    private int faseEvolucao = -1;

    private final List<Pokemon> banco = BancoPokemon.criarBanco();
    private final List<Pokemon> iniciais = new ArrayList<>();
    private final List<Item> loja = new ArrayList<>();
    private final Jogador jogador = Jogador.getInstancia();
    private final List<Item> inventario = jogador.getInventario();
    private final List<Pokemon> estoque = jogador.getEstoque();
    private final List<Integer> pokedex = jogador.getPokedex();

    private final List<BotaoJogo> botoes = new ArrayList<>();
    private final Map<Integer, PImage> sprites = new HashMap<>();
    private final Map<Integer, PImage> silhuetas = new HashMap<>();
    private final Map<String, PImage> imagens = new HashMap<>();

    private List<File> saves = new ArrayList<>();
    private int saveSelecionado = 0;
    private int scroll = 0;
    private String nomeNovoSave = "";
    private boolean campoSaveAtivo = false;

    private Pokemon pokemonSorteado;
    private Item itemSelecionado;
    private Pokemon detalhePokemon;
    private Pokemon evoAtual;
    private Pokemon evoProxima;
    private int evoIndiceEstoque = -1;
    private long momentoTela = 0L;
    private long retornoAutomaticoEm = -1L;
    private String mensagem = "";
    private String tituloMensagem = "AVISO";
    private Tela retornoMensagem = Tela.MENU;

    // Avisos agora aparecem como modal sobre a tela atual, sem trocar de cena.
    private boolean modalAtivo = false;
    private String tituloModal = "AVISO";
    private String mensagemModal = "";

    private PFont fonte;
    private PFont fonteGrande;

    // paleta inspirada em interfaces de RPG portáteis, sem copiar layout exato
    private final int COR_FUNDO = colorHex(0x263238);
    private final int COR_PAINEL = colorHex(0xF6EBC3);
    private final int COR_PAINEL2 = colorHex(0xFFF8DD);
    private final int COR_VERMELHO = colorHex(0xB83E32);
    private final int COR_VERMELHO_ESCURO = colorHex(0x702922);
    private final int COR_BOTAO = colorHex(0x5B2C27);
    private final int COR_BOTAO_HOVER = colorHex(0x7A3931);
    private final int COR_TEXTO = colorHex(0x20282D);
    private final int COR_VERDE = colorHex(0x46794F);

    public void settings() {
        // Tela cheia real. A composição continua em 1400x800 e é escalada no draw().
        fullScreen(JAVA2D);
        pixelDensity(displayDensity());
    }

    public void setup() {
        surface.setTitle("Pokémon OOP Java - Processing");
        frameRate(60);
        smooth(8);
        // Fontes são rasterizadas em tamanho maior e reduzidas no desenho.
        // Isso deixa o texto mais nítido quando a interface é escalada para tela cheia.
        fonte = createFont("Segoe UI", 36, true);
        fonteGrande = createFont("Segoe UI Semibold", 48, true);
        textFont(fonte);
        textSize(18);
        imageMode(CENTER);

        for (Pokemon p : banco) if (p.getEstagio() == 1) iniciais.add(p);
        loja.add(new PokebolaComum());
        loja.add(new SuperPokebola());
        loja.add(new UltraPokebola());
        loja.add(new Doce());
        loja.add(new PedraEvolucao());

        carregarImagensFixas();
        atualizarSaves();
        mudarTela(Tela.SAVES);
    }

    public void draw() {
        background(COR_FUNDO);
        botoes.clear();
        atualizarEscalaUI();

        // Tudo abaixo é desenhado no sistema virtual 1400x800.
        pushMatrix();
        translate(offsetUIX, offsetUIY);
        scale(escalaUI);

        switch (tela) {
            case SAVES -> desenharSaves();
            case NOVO_SAVE -> desenharNovoSave();
            case MENU -> desenharMenu();
            case STATUS -> desenharStatus();
            case CAPTURA -> desenharCaptura();
            case INVENTARIO -> desenharInventario();
            case ESCOLHER_POKEMON -> desenharEscolherPokemon();
            case LOJA -> desenharLoja();
            case POKEDEX -> desenharPokedex();
            case DETALHE_POKEDEX -> desenharDetalhePokedex();
            case EVOLUCAO -> desenharEvolucao();
            case RESULTADO_EVOLUCAO -> desenharResultadoEvolucao();
            case MENSAGEM -> desenharMensagem();
        }
        if (modalAtivo) desenharModal();
        popMatrix();

        if (retornoAutomaticoEm > 0 && millis() >= retornoAutomaticoEm) {
            retornoAutomaticoEm = -1;
            if (efeitoAtual != null) { audio.parar(efeitoAtual); efeitoAtual = null; }
            mudarTela(Tela.MENU);
        }
    }

    private void atualizarEscalaUI() {
        escalaUI = Math.min(width / (float) BASE_W, height / (float) BASE_H);
        offsetUIX = (width - BASE_W * escalaUI) / 2f;
        offsetUIY = (height - BASE_H * escalaUI) / 2f;
        mouseUIX = (mouseX - offsetUIX) / escalaUI;
        mouseUIY = (mouseY - offsetUIY) / escalaUI;
    }

    private float mouseVirtualX() {
        return (mouseX - offsetUIX) / escalaUI;
    }

    private float mouseVirtualY() {
        return (mouseY - offsetUIY) / escalaUI;
    }

    // --------------------------- NAVEGAÇÃO E ÁUDIO ---------------------------

    private void mudarTela(Tela nova) {
        telaAnterior = tela;
        tela = nova;
        scroll = 0;
        momentoTela = millis();

        if (nova == Tela.SAVES || nova == Tela.NOVO_SAVE) {
            pararMusicaMenu();
            pararBatalha();
            iniciarMusicaTitulo();
        } else if (nova == Tela.CAPTURA) {
            pararMusicaTitulo();
            pararMusicaMenu();
        } else if (nova == Tela.EVOLUCAO || nova == Tela.RESULTADO_EVOLUCAO) {
            pararMusicaTitulo();
            pararMusicaMenu();
            pararBatalha();
        } else {
            pararMusicaTitulo();
            pararBatalha();
            iniciarMusicaMenu();
        }
    }

    private void iniciarMusicaTitulo() {
        if (musicaTitulo == null) musicaTitulo = audio.tocar("assets/audio/title_screen.wav", true);
    }
    private void pararMusicaTitulo() { audio.parar(musicaTitulo); musicaTitulo = null; }
    private void iniciarMusicaMenu() {
        if (musicaMenu == null) musicaMenu = audio.tocar("assets/audio/menu.wav", true);
    }
    private void pararMusicaMenu() { audio.parar(musicaMenu); musicaMenu = null; }
    private void iniciarBatalha() {
        if (musicaBatalha == null) musicaBatalha = audio.tocar("assets/audio/batalha.wav", true);
    }
    private void pararBatalha() { audio.parar(musicaBatalha); musicaBatalha = null; }

    // --------------------------- COMPONENTES VISUAIS ---------------------------

    private void cabecalho(String titulo) {
        noStroke(); fill(COR_VERMELHO_ESCURO); rect(18, 16, BASE_W - 36, 72, 8);
        fill(COR_VERMELHO); rect(23, 21, BASE_W - 46, 62, 5);
        fill(255); textFont(fonteGrande); textSize(32); textAlign(LEFT, CENTER); text(titulo, 48, 52);
        textFont(fonte); textSize(18);
    }

    private void painel(float x, float y, float w, float h) {
        stroke(20); strokeWeight(4); fill(COR_PAINEL); rect(x, y, w, h, 8);
    }

    private void painelClaro(float x, float y, float w, float h) {
        stroke(95, 84, 62); strokeWeight(2); fill(COR_PAINEL2); rect(x, y, w, h, 6);
    }

    private BotaoJogo botao(float x, float y, float w, float h, String texto, Runnable acao) {
        BotaoJogo b = new BotaoJogo(x, y, w, h, texto, acao);
        botoes.add(b);
        boolean hover = b.contem(mouseUIX, mouseUIY);
        stroke(20); strokeWeight(3); fill(hover ? COR_BOTAO_HOVER : COR_BOTAO); rect(x, y, w, h, 7);
        fill(255); textFont(fonte); textSize(16); textAlign(CENTER, CENTER); text(texto, x + w / 2, y + h / 2);
        return b;
    }

    private void voltarMenu() {
        botao(BASE_W - 250, BASE_H - 62, 220, 42, "◀ VOLTAR AO MENU", () -> mudarTela(Tela.MENU));
        fill(230); textAlign(LEFT, CENTER); textSize(12); text("ESC também volta", 28, BASE_H - 42);
    }

    private void desenharSprite(Pokemon p, float cx, float cy, float maxW, float maxH) {
        PImage img = sprite(p);
        if (img == null) {
            fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(18);
            text("[ " + p.getNome().toUpperCase() + " ]", cx, cy);
            return;
        }
        float esc = Math.min(maxW / img.width, maxH / img.height);
        float w = img.width * esc;
        float h = img.height * esc;
        // Os arquivos em pokemon_hd já foram ampliados com reamostragem de alta qualidade.
        // Mantemos o smoothing ligado para evitar o aspecto quadriculado ao exibir em tela cheia.
        smooth(8);
        image(img, cx, cy, w, h);
    }

    private PImage sprite(Pokemon p) {
        if (p == null) return null;
        if (sprites.containsKey(p.getNumero())) return sprites.get(p.getNumero());
        String n = String.format("%03d", p.getNumero());
        // Prioriza a cópia ampliada para tela cheia; se ela não existir, usa o sprite original.
        PImage img = carregarImagem("assets/pokemon_hd/" + n + ".png");
        if (img == null) img = carregarImagem("assets/pokemon/" + n + ".png");
        sprites.put(p.getNumero(), img);
        return img;
    }

    private PImage carregarImagem(String caminho) {
        File f = new File(caminho);
        if (!f.exists()) return null;
        try { return loadImage(f.getAbsolutePath()); }
        catch (Exception e) { return null; }
    }

    private void carregarImagensFixas() {
        imagens.put("pokebola", carregarImagem("assets/itens/pokebola.png"));
        imagens.put("super", carregarImagem("assets/itens/super_pokebola.png"));
        imagens.put("ultra", carregarImagem("assets/itens/ultra_pokebola.png"));
        imagens.put("doce", carregarImagem("assets/itens/doce.png"));
        imagens.put("pedra", carregarImagem("assets/itens/pedra_evolucao.png"));
        imagens.put("grama", carregarImagem("assets/cenarios/grama.jpg"));
    }

    private PImage imagemItem(Item item) {
        if (item instanceof PokebolaComum) return imagens.get("pokebola");
        if (item instanceof SuperPokebola) return imagens.get("super");
        if (item instanceof UltraPokebola) return imagens.get("ultra");
        if (item instanceof Doce) return imagens.get("doce");
        if (item instanceof PedraEvolucao) return imagens.get("pedra");
        return null;
    }

    private void desenharIconeItem(Item item, float x, float y, float tam) {
        PImage img = imagemItem(item);
        if (img == null) return;
        float e = Math.min(tam / img.width, tam / img.height);
        noSmooth(); image(img, x, y, img.width * e, img.height * e); smooth(8);
    }

    /**
     * Indicador visual de rolagem. Não altera a lógica da lista: roda do mouse e
     * setas ↑/↓ continuam controlando a posição, mas agora fica evidente quando
     * existem itens acima ou abaixo da área visível.
     */
    private void desenharBarraRolagem(int total, int visiveis, int inicio,
                                      float x, float y, float h) {
        if (total <= visiveis || visiveis <= 0) return;
        int maxInicio = Math.max(1, total - visiveis);
        inicio = Math.max(0, Math.min(inicio, maxInicio));

        // trilho
        noStroke();
        fill(205, 194, 155);
        rect(x, y, 12, h, 6);

        // cursor proporcional ao conteúdo
        float thumbH = Math.max(42, h * (visiveis / (float) total));
        float livre = h - thumbH;
        float thumbY = y + livre * (inicio / (float) maxInicio);
        fill(COR_VERMELHO_ESCURO);
        rect(x + 1, thumbY, 10, thumbH, 5);

        fill(COR_TEXTO);
        textAlign(CENTER, CENTER);
        textSize(13);
        if (inicio > 0) text("▲", x + 6, y - 12);
        if (inicio < maxInicio) text("▼", x + 6, y + h + 13);
    }

    private void dicaRolagem(float x, float y) {
        fill(105);
        textAlign(RIGHT, CENTER);
        textSize(11);
        text("Roda do mouse ou ↑ ↓ para rolar", x, y);
    }

    // --------------------------- SAVES ---------------------------

    private void atualizarSaves() {
        saves = GerenciadorPersistencia.listarSaves();
        if (saveSelecionado >= saves.size()) saveSelecionado = Math.max(0, saves.size() - 1);
    }

    private void desenharSaves() {
        cabecalho("POKÉMON - ESCOLHA SEU JOGO");
        painel(25, 105, BASE_W - 50, BASE_H - 130);
        fill(COR_TEXTO); textAlign(LEFT, TOP); textSize(16);
        text("Selecione um save existente ou comece um novo jogo.", 50, 128);

        float listX = 50, listY = 165, listW = BASE_W - 100, listH = BASE_H - 330;
        painelClaro(listX, listY, listW, listH);
        int visiveis = Math.max(1, (int)((listH - 16) / 54));
        int inicio = Math.min(scroll, Math.max(0, saves.size() - visiveis));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < visiveis && inicio + i < saves.size(); i++) {
            int idx = inicio + i;
            File f = saves.get(idx);
            float y = listY + 9 + i * 54;
            boolean sel = idx == saveSelecionado;
            noStroke(); fill(sel ? color(215, 192, 124) : color(255, 250, 220));
            rect(listX + 8, y, listW - 16, 46, 4);
            fill(COR_TEXTO); textAlign(LEFT, CENTER); textSize(15);
            String nome = f.getName().equals("save.txt") ? "SAVE ANTIGO" : f.getName().replaceFirst("\\.txt$", "");
            text(nome + "     |     " + sdf.format(new Date(f.lastModified())), listX + 24, y + 23);
            BotaoJogo linha = new BotaoJogo(listX + 8, y, listW - 16, 46, "", () -> {});
            botoes.add(linha);
        }

        float by = BASE_H - 142;
        botao(50, by, 180, 48, "CONTINUAR", this::continuarSave);
        botao(245, by, 180, 48, "NOVO JOGO", () -> { nomeNovoSave = ""; campoSaveAtivo = true; mudarTela(Tela.NOVO_SAVE); });
        botao(440, by, 180, 48, "EXCLUIR", this::excluirSave);
        botao(635, by, 180, 48, "ATUALIZAR", this::atualizarSaves);
        botao(BASE_W - 230, by, 180, 48, "SAIR", this::sairSeguro);
    }

    private void continuarSave() {
        if (saves.isEmpty() || saveSelecionado < 0 || saveSelecionado >= saves.size()) {
            aviso("SAVE", "Nenhum save selecionado.", Tela.SAVES); return;
        }
        GerenciadorPersistencia.definirArquivoSave(saves.get(saveSelecionado));
        carregarJogoSelecionado();
    }

    private void carregarJogoSelecionado() {
        try {
            if (GerenciadorPersistencia.existeSave()) GerenciadorPersistencia.carregar(jogador, banco);
            else jogador.resetarParaEstadoInicial();
            mudarTela(Tela.MENU);
        } catch (PersistenciaException e) {
            aviso("ERRO DE SAVE", e.getMessage(), Tela.SAVES);
        }
    }

    private void desenharNovoSave() {
        cabecalho("NOVO JOGO");
        painel(BASE_W / 2f - 400, 160, 800, 390);
        fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(21);
        text("Digite um nome para o novo save:", BASE_W / 2f, 225);

        float campoX = BASE_W / 2f - 310;
        float campoY = 270;
        float campoW = 620;
        float campoH = 74;
        stroke(campoSaveAtivo ? COR_VERDE : color(95, 84, 62));
        strokeWeight(campoSaveAtivo ? 4 : 2);
        fill(255);
        rect(campoX, campoY, campoW, campoH, 8);

        String exibido = nomeNovoSave;
        if (campoSaveAtivo && (millis() / 500) % 2 == 0) exibido += "|";
        if (nomeNovoSave.isEmpty() && !campoSaveAtivo) exibido = "Clique aqui e digite...";
        fill(nomeNovoSave.isEmpty() && !campoSaveAtivo ? color(135) : COR_TEXTO);
        textAlign(LEFT, CENTER);
        textSize(24);
        text(exibido, campoX + 22, campoY + campoH / 2f);

        fill(100); textAlign(CENTER, CENTER); textSize(14);
        text("ENTER confirma  •  BACKSPACE apaga  •  ESC cancela", BASE_W / 2f, 370);

        botao(BASE_W / 2f - 230, 430, 210, 58, "CRIAR", this::criarNovoSave);
        botao(BASE_W / 2f + 20, 430, 210, 58, "CANCELAR", () -> { campoSaveAtivo = false; mudarTela(Tela.SAVES); });
    }

    private void criarNovoSave() {
        File novo = GerenciadorPersistencia.criarArquivoNovoSave(nomeNovoSave);
        GerenciadorPersistencia.definirArquivoSave(novo);
        jogador.resetarParaEstadoInicial();
        try { GerenciadorPersistencia.salvar(jogador); }
        catch (PersistenciaException e) { aviso("ERRO", e.getMessage(), Tela.SAVES); return; }
        atualizarSaves();
        mudarTela(Tela.MENU);
    }

    private void excluirSave() {
        if (saves.isEmpty() || saveSelecionado >= saves.size()) { aviso("SAVE", "Nenhum save selecionado.", Tela.SAVES); return; }
        File f = saves.get(saveSelecionado);
        if (!GerenciadorPersistencia.excluirSave(f)) aviso("ERRO", "Não foi possível excluir o save.", Tela.SAVES);
        atualizarSaves();
    }

    // --------------------------- MENU ---------------------------

    private void desenharMenu() {
        cabecalho("POKÉMON - MENU");
        painel(25, 105, BASE_W - 50, BASE_H - 130);
        fill(COR_TEXTO); textAlign(LEFT, TOP); textSize(20);
        text("Saldo: $" + saldo(jogador.getSaldo()), 55, 135);
        textSize(15); text("Pokémon no estoque: " + estoque.size() + "   |   Pokédex: " + pokedex.size() + "/" + banco.size(), 55, 170);

        float bw = 280, bh = 82, gx = 32, gy = 26;
        float sx = BASE_W / 2f - bw - gx / 2f;
        float sy = 245;
        botao(sx, sy, bw, bh, "STATUS", () -> mudarTela(Tela.STATUS));
        botao(sx + bw + gx, sy, bw, bh, "CAPTURAR", this::iniciarCaptura);
        botao(sx, sy + bh + gy, bw, bh, "INVENTÁRIO", () -> mudarTela(Tela.INVENTARIO));
        botao(sx + bw + gx, sy + bh + gy, bw, bh, "LOJA", () -> mudarTela(Tela.LOJA));
        botao(sx, sy + 2 * (bh + gy), bw, bh, "POKÉDEX", () -> mudarTela(Tela.POKEDEX));
        botao(sx + bw + gx, sy + 2 * (bh + gy), bw, bh, "SALVAR E VOLTAR", this::salvarEVoltar);

        fill(90); textAlign(CENTER, CENTER); textSize(12);
        text("Versão Processing • tela cheia adaptável • ESC volta", BASE_W / 2f, BASE_H - 42);
    }

    // --------------------------- STATUS ---------------------------

    private void desenharStatus() {
        cabecalho("STATUS DO JOGADOR");
        painel(25, 105, BASE_W - 50, BASE_H - 185);
        fill(COR_TEXTO); textAlign(LEFT, TOP); textSize(18);
        text("Saldo: $" + saldo(jogador.getSaldo()), 55, 130);
        textSize(16); text("Você capturou " + estoque.size() + " Pokémon(s):", 55, 165);

        int linhaH = 82;
        int visiveis = Math.max(1, (BASE_H - 310) / linhaH);
        int inicio = Math.min(scroll, Math.max(0, estoque.size() - visiveis));
        for (int i = 0; i < visiveis && inicio + i < estoque.size(); i++) {
            Pokemon p = estoque.get(inicio + i);
            float y = 205 + i * linhaH;
            painelClaro(50, y, BASE_W - 100, 70);
            desenharSprite(p, 95, y + 35, 58, 58);
            fill(COR_TEXTO); textAlign(LEFT, CENTER); textSize(16);
            text(p.getNome() + "  (Nº " + p.getNumero() + ")   Estágio " + p.getEstagio(), 145, y + 35);
        }
        if (estoque.isEmpty()) { fill(COR_TEXTO); text("Nenhum Pokémon capturado ainda.", 55, 220); }
        voltarMenu();
    }

    // --------------------------- CAPTURA ---------------------------

    private void iniciarCaptura() {
        if (iniciais.isEmpty()) { aviso("CAPTURA", "Não existem Pokémon iniciais disponíveis.", Tela.MENU); return; }
        if (pokebolasNoInventario().isEmpty()) { aviso("SEM POKÉBOLAS", "Você não tem Pokébolas. Vá à loja.", Tela.MENU); return; }
        pokemonSorteado = iniciais.get(rand.nextInt(iniciais.size()));
        mensagem = "Escolha qual Pokébola usar.";
        retornoAutomaticoEm = -1;
        mudarTela(Tela.CAPTURA);
        iniciarBatalha();
    }

    private void desenharCaptura() {
        cabecalho("CAPTURA");
        painel(25, 105, BASE_W - 50, BASE_H - 185);
        if (pokemonSorteado == null) { mudarTela(Tela.MENU); return; }

        // Mesma largura da caixa inferior para manter a composição equilibrada.
        painelClaro(55, 128, 955, 54);
        fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(19);
        text("Um " + pokemonSorteado.getNome() + " selvagem apareceu!", 55 + 955/2f, 155);

        float arenaX = 55, arenaY = 194, arenaW = 955, arenaH = 335;
        stroke(70, 95, 65); strokeWeight(4); fill(190, 215, 150); rect(arenaX, arenaY, arenaW, arenaH, 7);
        PImage grama = imagens.get("grama");
        if (grama != null) {
            imageMode(CORNER); image(grama, arenaX + 4, arenaY + 4, arenaW - 8, arenaH - 8); imageMode(CENTER);
        } else {
            noStroke(); fill(155, 195, 120); ellipse(arenaX + arenaW/2f, arenaY + arenaH*0.75f, 420, 85);
        }
        desenharSprite(pokemonSorteado, arenaX + arenaW/2f, arenaY + arenaH/2f - 10, 270, 270);

        painelClaro(55, 540, 955, 72);
        fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(15); text(mensagem, 55 + 955/2f, 576);

        // BOLSA: mesma ideia visual do inventário. Cada Pokébola aparece como um
        // item independente, sem agrupar em "x quantidade".
        float bx = 1035;
        float listaX = bx;
        float listaY = 176;
        float listaW = 330;
        float listaH = 390;
        fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(19); text("BOLSA", bx + listaW/2f, 145);
        painelClaro(listaX, listaY, listaW, listaH);

        List<Pokebola> bolas = pokebolasNoInventario();
        int linhaH = 82;
        int visiveis = Math.max(1, (int)((listaH - 28) / linhaH));
        int inicio = Math.min(scroll, Math.max(0, bolas.size() - visiveis));

        if (bolas.isEmpty()) {
            fill(COR_TEXTO); textAlign(CENTER, CENTER); textSize(14);
            text("Você não possui Pokébolas.\nVisite a loja.", listaX + listaW/2f, listaY + listaH/2f);
        }

        for (int i = 0; i < visiveis && inicio + i < bolas.size(); i++) {
            Pokebola bola = bolas.get(inicio + i);
            float y = listaY + 12 + i * linhaH;
            stroke(120); strokeWeight(1); fill(255, 250, 225); rect(listaX + 10, y, listaW - 34, 70, 6);
            desenharIconeItem(bola, listaX + 45, y + 35, 48);
            fill(COR_TEXTO); textAlign(LEFT, CENTER); textSize(12);
            text(bola.getDetalhes(), listaX + 78, y + 24);
            BotaoJogo b = botao(listaX + 205, y + 39, 82, 24, "USAR", () -> tentarCaptura(bola));
            if (retornoAutomaticoEm > 0) b.ativo = false;
        }
        desenharBarraRolagem(bolas.size(), visiveis, inicio, listaX + listaW - 16, listaY + 22, listaH - 56);
        dicaRolagem(listaX + listaW - 16, listaY + listaH + 18);

        botao(BASE_W - 250, BASE_H - 62, 220, 42, "◀ FUGIR / VOLTAR", () -> { retornoAutomaticoEm=-1; mudarTela(Tela.MENU); });
    }

    private void tentarCaptura(Pokebola bola) {
        if (retornoAutomaticoEm > 0) return;
        if (bola == null || !inventario.contains(bola) || pokemonSorteado == null) return;
        mensagem = "Jogando " + bola.getNome() + "...";
        boolean capturado = bola.usar(pokemonSorteado);
        inventario.remove(bola); // mantém a regra original: a bola é consumida em qualquer tentativa
        pararBatalha();

        if (capturado) {
            estoque.add(pokemonSorteado);
            if (!pokedex.contains(pokemonSorteado.getNumero())) pokedex.add(pokemonSorteado.getNumero());
            jogador.setSaldo(jogador.getSaldo() + 800);
            mensagem = "YES! " + pokemonSorteado.getNome() + " foi capturado!   +$800";
            efeitoAtual = audio.tocar("assets/audio/capturado.wav", false);
        } else {
            mensagem = pokemonSorteado.getNome() + " saiu da bola e fugiu!";
            efeitoAtual = audio.tocar("assets/audio/fugiu.wav", false);
        }
        long dur = audio.duracaoMs(efeitoAtual);
        long atraso = dur > 0 ? Math.max(500, dur - 3000) : 900;
        retornoAutomaticoEm = millis() + atraso;
    }

    private List<Pokebola> pokebolasNoInventario() {
        List<Pokebola> out = new ArrayList<>();
        for (Item i : inventario) if (i instanceof Pokebola) out.add((Pokebola)i);
        return out;
    }
    private Pokebola primeiraPokebola(Class<?> c) {
        for (Item i : inventario) if (c.isInstance(i) && i instanceof Pokebola) return (Pokebola)i;
        return null;
    }
    private int contarClasse(Class<?> c) { int n=0; for (Item i:inventario) if(c.isInstance(i)) n++; return n; }

    // --------------------------- INVENTÁRIO / EVOLUÇÃO ---------------------------

    private void desenharInventario() {
        cabecalho("INVENTÁRIO");
        painel(25, 105, BASE_W - 50, BASE_H - 185);
        if (inventario.isEmpty()) {
            fill(COR_TEXTO); textAlign(LEFT, TOP); textSize(16); text("Inventário está vazio.", 55, 135);
        }
        int linhaH = 82;
        int visiveis = Math.max(1, (BASE_H - 300) / linhaH);
        int inicio = Math.min(scroll, Math.max(0, inventario.size() - visiveis));
        for (int i=0; i<visiveis && inicio+i<inventario.size(); i++) {
            Item item = inventario.get(inicio+i);
            float y=125+i*linhaH;
            painelClaro(50,y,BASE_W-120,70);
            desenharIconeItem(item,88,y+35,50);
            fill(COR_TEXTO); textAlign(LEFT,CENTER); textSize(15); text(item.getDetalhes(),130,y+35);
            if (item instanceof Doce || item instanceof PedraEvolucao) {
                botao(BASE_W-210,y+13,120,44,"USAR",()->{ itemSelecionado=item; mudarTela(Tela.ESCOLHER_POKEMON); });
            } else {
                fill(120); textAlign(RIGHT,CENTER); textSize(12); text("Usar na captura",BASE_W-95,y+35);
            }
        }

        // Barra sempre visível quando existe conteúdo além da área mostrada.
        float barraY = 130;
        float barraH = visiveis * linhaH - 16;
        desenharBarraRolagem(inventario.size(), visiveis, inicio, BASE_W - 62, barraY, barraH);
        if (inventario.size() > visiveis) dicaRolagem(BASE_W - 72, BASE_H - 92);
        voltarMenu();
    }

    private void desenharEscolherPokemon() {
        cabecalho("ESCOLHA UM POKÉMON");
        painel(25,105,BASE_W-50,BASE_H-185);
        if (itemSelecionado == null) { mudarTela(Tela.INVENTARIO); return; }
        fill(COR_TEXTO); textAlign(LEFT,TOP); textSize(16); text("Usar " + itemSelecionado.getNome() + " em:",55,130);
        int linhaH=82;
        int visiveis=Math.max(1,(BASE_H-330)/linhaH);
        int inicio=Math.min(scroll,Math.max(0,estoque.size()-visiveis));
        for(int i=0;i<visiveis && inicio+i<estoque.size();i++){
            Pokemon p=estoque.get(inicio+i); float y=175+i*linhaH;
            painelClaro(50,y,BASE_W-100,70); desenharSprite(p,90,y+35,55,55);
            fill(COR_TEXTO); textAlign(LEFT,CENTER); textSize(15); text(p.getNome()+"  Nº "+p.getNumero()+"  estágio "+p.getEstagio(),135,y+35);
            botao(BASE_W-190,y+13,120,44,"ESCOLHER",()->usarItemEvolutivo(itemSelecionado,p));
        }
        if(estoque.isEmpty()){ fill(COR_TEXTO); text("Você ainda não capturou nenhum Pokémon.",55,180); }
        botao(BASE_W-250,BASE_H-62,220,42,"◀ VOLTAR",()->mudarTela(Tela.INVENTARIO));
    }

    private void usarItemEvolutivo(Item item, Pokemon alvo) {
        if (!(alvo instanceof PokemonEvolutivo)) {
            aviso("EVOLUÇÃO", alvo.getNome()+" não possui linha evolutiva cadastrada.", Tela.INVENTARIO); return;
        }
        boolean pode=((Usavel)item).usar(alvo);
        if(!pode){
            String regra=item instanceof Doce?"O Doce Raro só funciona em Pokémon no estágio 1.":"A Pedra da Evolução só funciona em Pokémon no estágio 2.";
            aviso("EVOLUÇÃO", alvo.getNome()+" não pode evoluir com este item.\n"+regra,Tela.INVENTARIO); return;
        }
        PokemonEvolutivo atual=(PokemonEvolutivo)alvo;
        Pokemon prox=null;
        for(Pokemon p:banco){
            if(p instanceof PokemonEvolutivo pe && pe.getLinhaEvolutiva()==atual.getLinhaEvolutiva() && pe.getEstagio()==atual.getEstagio()+1){ prox=pe; break; }
        }
        if(prox==null){ aviso("EVOLUÇÃO","Não há próxima evolução cadastrada para "+atual.getNome()+".",Tela.INVENTARIO); return; }
        evoAtual=atual; evoProxima=prox; evoIndiceEstoque=estoque.indexOf(alvo); itemSelecionado=item;
        mudarTela(Tela.EVOLUCAO);
        // Sequência: grito da forma atual -> música/animação -> grito da nova forma -> parabéns.
        faseEvolucao = 0;
        efeitoAtual = tocarGritoPokemon(evoAtual);
        momentoTela = millis();
    }

    private String nomeAudioPokemon(Pokemon p) {
        if (p == null || p.getNome() == null) return "";
        String n = p.getNome().toLowerCase(java.util.Locale.ROOT)
                .replace("♀", "f").replace("♂", "m")
                .replaceAll("[^a-z0-9]", "");
        // Os arquivos enviados usam nidoranf / nidoranm.
        if (n.equals("nidoranfem")) return "nidoranf";
        if (n.equals("nidoranmasc")) return "nidoranm";
        return n;
    }

    private Clip tocarGritoPokemon(Pokemon p) {
        String nome = nomeAudioPokemon(p);
        if (nome.isEmpty()) return null;
        return audio.tocar("assets/audio/cries/" + nome + ".wav", false);
    }

    private void desenharEvolucao() {
        // Antes da música, toca o grito do Pokémon que está evoluindo.
        if (faseEvolucao == 0) {
            background(5, 6, 9);
            desenharSprite(evoAtual, BASE_W/2f, 320, 275, 275);
            caixaDialogoEvolucao("O quê?\n" + evoAtual.getNome() + " está evoluindo!");
            if (efeitoAtual == null || !efeitoAtual.isRunning()) {
                audio.parar(efeitoAtual);
                efeitoAtual = audio.tocar("assets/audio/evoluindo.wav", false);
                faseEvolucao = 1;
                momentoTela = millis();
            }
            return;
        }

        // Depois da música, toca o grito da NOVA forma no espaço antes de "Parabéns".
        if (faseEvolucao == 2) {
            fundoEvolucaoClaro();
            desenharSprite(evoProxima, BASE_W/2f, 315, 310, 310);
            caixaDialogoEvolucao(evoProxima.getNome() + "!");
            if (gritoEvolucao == null || !gritoEvolucao.isRunning()) {
                audio.parar(gritoEvolucao); gritoEvolucao = null;
                concluirEvolucaoDepoisDoGrito();
            }
            return;
        }

        // V6: ritmo sincronizado ao áudio extraído do vídeo de referência do usuário.
        // Duração aproximada: 17,1 s. A troca de formas acelera progressivamente.
        long t = millis() - momentoTela;
        long dur = audio.duracaoMs(efeitoAtual);
        if (dur <= 0) dur = 17100;

        background(5, 6, 9);

        // 0–2,7 s: forma original visível, sem flashes no fundo.
        if (t < 3200) {
            desenharSprite(evoAtual, BASE_W/2f, 320, 275, 275);
            caixaDialogoEvolucao("O quê?\n" + evoAtual.getNome() + " está evoluindo!");
        }
        // 2,7–14,8 s: silhuetas alternam. O intervalo cai de ~650 ms para ~90 ms,
        // reproduzindo a sensação de aceleração rápida do vídeo de referência.
        else if (t < 14800) {
            long e = t - 3200;
            float q = constrain(e / 11600f, 0f, 1f);
            float intervalo = lerp(650f, 90f, pow(q, 0.72f));
            int fase = (int)(e / Math.max(1f, intervalo));
            boolean nova = (fase % 2) == 1;
            Pokemon forma = nova ? evoProxima : evoAtual;
            PImage sil = silhueta(forma);

            // Mudança quase instantânea entre as duas formas, sem morph lento.
            float tam = nova ? 292 : 270;
            if (sil != null) {
                imageMode(CENTER); noSmooth();
                image(sil, BASE_W/2f, 320, tam, tam);
                smooth(8);
            } else {
                desenharSprite(forma, BASE_W/2f, 320, tam, tam);
            }
            caixaDialogoEvolucao("O quê?\n" + evoAtual.getNome() + " está evoluindo!");
        }
        // 14,8–16,1 s: fixa rapidamente na silhueta da nova forma.
        else if (t < 16100) {
            PImage sil = silhueta(evoProxima);
            if (sil != null) {
                imageMode(CENTER); noSmooth(); image(sil, BASE_W/2f, 320, 300, 300); smooth(8);
            } else desenharSprite(evoProxima, BASE_W/2f, 320, 300, 300);
            caixaDialogoEvolucao("O quê?\n" + evoAtual.getNome() + " está evoluindo!");
        }
        // Final: revela a nova forma antes de entrar na tela de resultado.
        else {
            fundoEvolucaoClaro();
            desenharSprite(evoProxima, BASE_W/2f, 315, 310, 310);
            caixaDialogoEvolucao(evoProxima.getNome() + "!");
        }

        // Pequenos brilhos sincronizados com os "plin-plin" do áudio.
        // Eles ficam ao redor do Pokémon; a tela inteira nunca pisca.
        desenharPlinsEvolucao(t);

        if (t >= dur) iniciarGritoNovaForma();
    }

    private void desenharPlinsEvolucao(long t) {
        final int[] plins = {1050, 1600, 2100, 15600, 16050, 16550};
        noStroke();
        for (int i=0; i<plins.length; i++) {
            long dt = t - plins[i];
            if (dt < 0 || dt > 520) continue;
            float p = dt / 520f;
            float a = 255f * (1f-p);
            float r = 82f + 150f*p;
            // quatro pontos por plin, em posições determinísticas ao redor do sprite
            for (int k=0; k<4; k++) {
                float ang = (i*0.91f + k*1.5707963f) + p*0.55f;
                float x = BASE_W/2f + cos(ang)*r;
                float y = 320 + sin(ang)*r*0.72f;
                float sz = 15f + 13f*(1f-p);
                fill(255, 250, 190, a);
                // estrela/cruz suave, sem flash de fundo
                rect(x - sz/2f, y - 1.75f, sz, 3.5f, 2);
                rect(x - 1.75f, y - sz/2f, 3.5f, sz, 2);
                ellipse(x, y, 5, 5);
            }
        }
    }

    private void fundoEvolucaoClaro() {
        background(224, 241, 221);
        noStroke();
        for (int y=0; y<BASE_H-170; y+=10) {
            fill((y/10)%2==0 ? color(225,242,225) : color(214,235,218));
            rect(0,y,BASE_W,10);
        }
    }

    private void caixaDialogoEvolucao(String texto) {
        float x=120, y=590, w=BASE_W-240, h=150;
        stroke(105,55,55); strokeWeight(6); fill(129,172,168); rect(x,y,w,h,14);
        stroke(230); strokeWeight(3); noFill(); rect(x+8,y+8,w-16,h-16,9);
        noStroke(); fill(255); textAlign(LEFT,TOP); textFont(fonteGrande); textSize(26);
        text(texto, x+28, y+25);
        textFont(fonte);
    }

    private PImage silhueta(Pokemon p) {
        if (p == null) return null;
        PImage pronta = silhuetas.get(p.getNumero());
        if (pronta != null) return pronta;
        PImage src = sprite(p);
        if (src == null) return null;
        PImage out = createImage(src.width, src.height, ARGB);
        src.loadPixels(); out.loadPixels();
        for (int i=0;i<src.pixels.length;i++) {
            int a = (src.pixels[i] >>> 24) & 0xff;
            out.pixels[i] = (a << 24) | 0x00FFFFFF;
        }
        out.updatePixels(); silhuetas.put(p.getNumero(), out); return out;
    }

    private void iniciarGritoNovaForma(){
        if (faseEvolucao != 1) return;
        audio.parar(efeitoAtual); efeitoAtual=null;
        faseEvolucao = 2;
        gritoEvolucao = tocarGritoPokemon(evoProxima);
        momentoTela = millis();
    }

    private void concluirEvolucaoDepoisDoGrito(){
        if(evoAtual==null||evoProxima==null||evoIndiceEstoque<0){ faseEvolucao=-1; mudarTela(Tela.INVENTARIO); return; }
        estoque.set(evoIndiceEstoque,evoProxima);
        inventario.remove(itemSelecionado);
        if(!pokedex.contains(evoProxima.getNumero()))pokedex.add(evoProxima.getNumero());
        jogador.setSaldo(jogador.getSaldo()+800);
        faseEvolucao = 3;
        // Só agora, depois do grito da nova forma, toca o "Parabéns/evoluiu".
        efeitoAtual=audio.tocar("assets/audio/parabens_evoluiu.wav",false);
        mudarTela(Tela.RESULTADO_EVOLUCAO);
    }

    private void desenharResultadoEvolucao(){
        fundoEvolucaoClaro();
        if(evoProxima!=null) desenharSprite(evoProxima, BASE_W/2f, 315, 310, 310);
        String antes=evoAtual!=null?evoAtual.getNome():"Seu Pokémon";
        String depois=evoProxima!=null?evoProxima.getNome():"nova forma";
        caixaDialogoEvolucao("Parabéns!\n"+antes+" evoluiu para "+depois+"!   +$800");

        boolean somTerminou = efeitoAtual==null || !efeitoAtual.isRunning();
        if(somTerminou){
            botao(BASE_W-300, 505, 180, 52, "CONTINUAR", this::encerrarResultadoEvolucao);
        } else {
            fill(255); textAlign(RIGHT,CENTER); textSize(14);
            text("Aguarde o som de evolução terminar...", BASE_W-120, 535);
        }
    }

    private void encerrarResultadoEvolucao(){
        audio.parar(efeitoAtual); efeitoAtual=null;
        evoAtual=null; evoProxima=null; evoIndiceEstoque=-1; itemSelecionado=null; faseEvolucao=-1;
        mudarTela(Tela.INVENTARIO);
    }

    // --------------------------- LOJA ---------------------------

    private void desenharLoja(){
        cabecalho("LOJA"); painel(25,105,BASE_W-50,BASE_H-185);
        fill(COR_TEXTO);textAlign(LEFT,TOP);textSize(18);text("Seu saldo: $"+saldo(jogador.getSaldo()),55,128);
        int linhaH=94;
        for(int i=0;i<loja.size();i++){
            Item item=loja.get(i); float y=170+i*linhaH;
            painelClaro(50,y,BASE_W-100,82); desenharIconeItem(item,92,y+41,60);
            fill(COR_TEXTO);textAlign(LEFT,CENTER);textSize(15);text(item.getDetalhesLoja(),145,y+41);
            botao(BASE_W-205,y+18,135,46,"COMPRAR",()->comprar(item));
        }
        voltarMenu();
    }

    private void comprar(Item item){
        if(jogador.getSaldo()<item.getPreco()){
            aviso("LOJA","Saldo insuficiente! Faltam $"+saldo(item.getPreco()-jogador.getSaldo()),Tela.LOJA);return;
        }
        jogador.setSaldo(jogador.getSaldo()-item.getPreco());
        if(item instanceof PokebolaComum)inventario.add(new PokebolaComum());
        else if(item instanceof SuperPokebola)inventario.add(new SuperPokebola());
        else if(item instanceof UltraPokebola)inventario.add(new UltraPokebola());
        else if(item instanceof Doce)inventario.add(new Doce());
        else if(item instanceof PedraEvolucao)inventario.add(new PedraEvolucao());
        aviso("COMPRA CONCLUÍDA",item.getNome()+" foi colocado no inventário.",Tela.LOJA);
    }

    // --------------------------- POKÉDEX ---------------------------

    private void desenharPokedex(){
        cabecalho("POKÉDEX"); painel(25,105,BASE_W-50,BASE_H-185);
        fill(COR_TEXTO);textAlign(LEFT,TOP);textSize(16);text("Registrados: "+pokedex.size()+"/"+banco.size(),55,128);
        int linhaH=72; int visiveis=Math.max(1,(BASE_H-320)/linhaH);
        int inicio=Math.min(scroll,Math.max(0,banco.size()-visiveis));
        for(int i=0;i<visiveis && inicio+i<banco.size();i++){
            Pokemon p=banco.get(inicio+i); boolean reg=pokedex.contains(p.getNumero()); float y=165+i*linhaH;
            stroke(95,84,62);strokeWeight(2);fill(reg?COR_PAINEL2:color(190));rect(50,y,BASE_W-100,62,6);
            if(reg)desenharSprite(p,90,y+31,48,48); else {fill(80);textAlign(CENTER,CENTER);text("?",90,y+31);}
            fill(COR_TEXTO);textAlign(LEFT,CENTER);textSize(15);String nome=reg?p.getNome():"????????";
            text(String.format("Nº %03d - %s",p.getNumero(),nome),135,y+31);
            if(reg)botao(BASE_W-205,y+9,135,44,"DETALHES",()->{detalhePokemon=p;mudarTela(Tela.DETALHE_POKEDEX);});
        }
        voltarMenu();
    }

    private void desenharDetalhePokedex(){
        cabecalho("POKÉDEX - DETALHES"); painel(80,130,BASE_W-160,BASE_H-230);
        if(detalhePokemon==null){mudarTela(Tela.POKEDEX);return;}
        desenharSprite(detalhePokemon,270,BASE_H/2f,260,260);
        fill(COR_TEXTO);textAlign(LEFT,TOP);textSize(18);
        float x=470,y=190;
        text(String.format("Nº %03d - %s",detalhePokemon.getNumero(),detalhePokemon.getNome()),x,y);y+=48;
        text("Tipo: "+detalhePokemon.getTipo(),x,y);y+=38;
        text("Habilidade: "+detalhePokemon.getHab(),x,y);y+=38;
        text("Estágio: "+detalhePokemon.getEstagio(),x,y);y+=38;
        text("Item evolutivo: "+(detalhePokemon.getItem()!=null?detalhePokemon.getItem():"Nenhum"),x,y);y+=38;
        if(detalhePokemon instanceof PokemonEvolutivo pe) text("Linha evolutiva: "+pe.getLinhaEvolutiva(),x,y);
        botao(BASE_W/2f-110,BASE_H-82,220,46,"VOLTAR À POKÉDEX",()->mudarTela(Tela.POKEDEX));
    }

    // --------------------------- MENSAGENS / SAVE ---------------------------

    private void aviso(String titulo,String texto,Tela retorno){
        // Mantém a tela da ação visível e abre somente uma caixa sobre ela.
        tituloModal = titulo;
        mensagemModal = texto;
        retornoMensagem = retorno;
        modalAtivo = true;
    }

    // Mantido apenas por compatibilidade com saves/versões antigas; novos avisos usam modal.
    private void desenharMensagem(){
        if (!modalAtivo) { mudarTela(retornoMensagem); return; }
        desenharModal();
    }

    private void desenharModal(){
        // Véu translúcido: a ação atual continua visível atrás da mensagem.
        noStroke(); fill(0, 0, 0, 115); rect(0, 0, BASE_W, BASE_H);

        float w = 720, h = 300;
        float x = BASE_W/2f - w/2f, y = BASE_H/2f - h/2f;
        stroke(20); strokeWeight(4); fill(COR_PAINEL); rect(x, y, w, h, 10);
        noStroke(); fill(COR_VERMELHO); rect(x+4, y+4, w-8, 62, 7);

        fill(255); textFont(fonteGrande); textSize(28); textAlign(CENTER,CENTER);
        text(tituloModal, BASE_W/2f, y+35);
        textFont(fonte); textSize(17); fill(COR_TEXTO);
        String[] linhas = mensagemModal.split("\\n");
        float inicioY = y + 118 - (linhas.length-1)*16f;
        for(int i=0;i<linhas.length;i++) text(linhas[i], BASE_W/2f, inicioY+i*34);

        botao(BASE_W/2f-100, y+h-78, 200, 52, "OK", () -> modalAtivo=false);
    }

    private void salvarEVoltar(){
        try{
            GerenciadorPersistencia.salvar(jogador);
            atualizarSaves();
            mudarTela(Tela.SAVES);
        }catch(PersistenciaException e){aviso("ERRO AO SALVAR",e.getMessage(),Tela.MENU);}
    }

    private void sairSeguro(){
        pararMusicaTitulo();pararMusicaMenu();pararBatalha();audio.parar(efeitoAtual);exit();
    }

    // --------------------------- INPUT ---------------------------

    public void mousePressed(){
        atualizarEscalaUI();
        float mx = mouseVirtualX();
        float my = mouseVirtualY();

        if (modalAtivo) {
            float w = 720, h = 300;
            float x = BASE_W/2f - w/2f, y = BASE_H/2f - h/2f;
            float okX = BASE_W/2f - 100, okY = y+h-78;
            if (mx >= okX && mx <= okX+200 && my >= okY && my <= okY+52) modalAtivo = false;
            return;
        }
        if(tela==Tela.NOVO_SAVE){
            float campoX = BASE_W / 2f - 310;
            float campoY = 270;
            float campoW = 620;
            float campoH = 74;
            campoSaveAtivo = mx >= campoX && mx <= campoX + campoW
                    && my >= campoY && my <= campoY + campoH;
        }

        // Linhas de save são tratadas primeiro para seleção.
        if(tela==Tela.SAVES){
            float listX=50,listY=165,listW=BASE_W-100,listH=BASE_H-330;
            if(mx>=listX+8&&mx<=listX+listW-8&&my>=listY&&my<=listY+listH){
                int visiveis=Math.max(1,(int)((listH-16)/54));
                int inicio=Math.min(scroll,Math.max(0,saves.size()-visiveis));
                int idx=inicio+(int)((my-listY-9)/54);
                if(idx>=0&&idx<saves.size()){saveSelecionado=idx;return;}
            }
        }
        for(int i=botoes.size()-1;i>=0;i--){BotaoJogo b=botoes.get(i);if(b.contem(mx,my)){b.acao.run();return;}}
    }

    // Caracteres imprimíveis são tratados em keyTyped(), que é mais confiável no
    // Processing para campos de texto e também lida melhor com acentos.
    public void keyTyped(){
        if(tela != Tela.NOVO_SAVE || !campoSaveAtivo) return;
        if(key == ENTER || key == RETURN || key == BACKSPACE || key == DELETE || key == ESC) return;
        if(Character.isISOControl(key) || nomeNovoSave.length() >= 28) return;
        String proibidos = "<>:\\|?*/\"";
        if(proibidos.indexOf(key) >= 0) return;
        nomeNovoSave += key;
    }

    public void keyPressed(){
        if (modalAtivo) {
            if (key==ESC || key==ENTER || key==RETURN) { key=0; modalAtivo=false; }
            return;
        }
        if(tela==Tela.NOVO_SAVE){
            if(key==ENTER||key==RETURN){criarNovoSave();return;}
            if(key==ESC){key=0;campoSaveAtivo=false;mudarTela(Tela.SAVES);return;}
            if(key==BACKSPACE&&campoSaveAtivo&&nomeNovoSave.length()>0){
                nomeNovoSave=nomeNovoSave.substring(0,nomeNovoSave.length()-1);return;
            }
            if(key==DELETE&&campoSaveAtivo){nomeNovoSave="";return;}
        }
        if(key==ESC){
            key=0;
            if(tela==Tela.SAVES) return;
            if(tela==Tela.DETALHE_POKEDEX){mudarTela(Tela.POKEDEX);return;}
            if(tela==Tela.ESCOLHER_POKEMON){mudarTela(Tela.INVENTARIO);return;}
            if(tela==Tela.RESULTADO_EVOLUCAO){
                if(efeitoAtual==null || !efeitoAtual.isRunning()) encerrarResultadoEvolucao();
                return;
            }
            if(tela!=Tela.EVOLUCAO)mudarTela(Tela.MENU);
        }
        if(keyCode==UP)scroll=Math.max(0,scroll-1);
        if(keyCode==DOWN)scroll++;
    }

    public void mouseWheel(MouseEvent event){
        int d=(int)Math.signum(event.getCount());
        scroll=Math.max(0,scroll+d);
    }


    // --------------------------- AUXILIARES ---------------------------

    private String saldo(double v){return v==Math.floor(v)?String.format("%.0f",v):String.format("%.2f",v);}
    private static int colorHex(int rgb){return 0xFF000000|rgb;}

    // Random encapsulado para não conflitar com PApplet.random().
    private static class RandomCompat{
        private final java.util.Random r=new java.util.Random();
        int nextInt(int bound){return r.nextInt(bound);}
    }
}
