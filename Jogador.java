import java.util.ArrayList;
import java.util.List;

//PADRÃO DE PROJETO SINGLETON
public class Jogador {

    private static Jogador instancia;

    private double saldo;
    private List<Item> inventario;
    private List<Pokemon> estoque;
    private List<Integer> pokedex;

    // construtor privado: ninguém de fora consegue instanciar Jogador com "new"
    private Jogador() {
        this.saldo = 1000;
        this.inventario = new ArrayList<>();
        this.estoque = new ArrayList<>();
        this.pokedex = new ArrayList<>();
    }

    public static Jogador getInstancia() {
        if (instancia == null) {
            instancia = new Jogador();
        }
        return instancia;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<Item> getInventario() {
        return inventario;
    }

    public List<Pokemon> getEstoque() {
        return estoque;
    }

    public List<Integer> getPokedex() {
        return pokedex;
    }

    //quando tem save o jogador volta com os itens que tem la, se for jogo novo, nasce com isso
    public void resetarParaEstadoInicial() {
        this.saldo = 100000;
        this.inventario.clear();
        this.estoque.clear();
        this.pokedex.clear();

        inventario.add(new PokebolaComum());
        inventario.add(new PokebolaComum());
        inventario.add(new PokebolaComum());
        inventario.add(new PedraEvolucao());
        inventario.add(new PedraEvolucao());
        inventario.add(new PedraEvolucao());
        inventario.add(new PedraEvolucao());
        inventario.add(new PedraEvolucao());
        inventario.add(new PedraEvolucao());
        inventario.add(new Doce());
        inventario.add(new Doce());
        inventario.add(new Doce());
        inventario.add(new Doce());
        inventario.add(new Doce());
        inventario.add(new Doce());
    }
}
