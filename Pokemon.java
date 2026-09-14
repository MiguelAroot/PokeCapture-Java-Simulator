public class Pokemon {
    private String nome;
    private int estagio; //estagio evolutivo
    private String habilidade;
    private int numero;
    private TipoPokemon tipo; //PRINCIPAL, uma chatura mexer com 2 tipos
    private ItemEvolutivo item; //item evolutivo

    public Pokemon(String nome, int estagio, String habilidade, TipoPokemon tipo, int numero, ItemEvolutivo item){
        this.nome = nome;
        this.estagio = estagio;
        this.habilidade = habilidade;
        this.tipo = tipo;
        this.numero = numero;
        this.item = item;
    }
    public void atacar(){
        System.out.println("O pokemon usa Ataque");
    }
    public String getNome(){
       return nome;
    }
    public int getEstagio(){
       return estagio;
    }
    public String getHab(){
       return habilidade;
    }
    public TipoPokemon getTipo(){
       return tipo;
    }
     public int getNumero(){
       return numero;
    }
    public ItemEvolutivo getItem(){
       return item;
    }
    
}
