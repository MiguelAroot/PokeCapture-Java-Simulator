import java.util.Random;
public abstract class Pokebola extends Item implements Usavel{
    private Random rand = new Random();
    protected double chancedeCaptura;

    public Pokebola(String nome, int preco, double chancedeCaptura){
        super(nome, preco);
        this.chancedeCaptura = chancedeCaptura;
    }

    public double getChance(){
        return chancedeCaptura;
    }

    @Override
    public String getDetalhes(){
        return nome + " | Chance: " + (chancedeCaptura * 100) + "%";
    }
    
    @Override
    public String getDetalhesLoja(){
        return nome + " | Preço: $" + preco + " | Chance: " + (chancedeCaptura * 100) + "%";
    }

    @Override
    public boolean usar(Pokemon alvo){
        double sorteio = rand.nextDouble(0, 1);
        return sorteio <= chancedeCaptura;
    }    
}


