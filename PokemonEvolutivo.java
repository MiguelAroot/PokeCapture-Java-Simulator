public class PokemonEvolutivo extends Pokemon {

    private int linhaEvolutiva;


    public PokemonEvolutivo(String nome, int estagio, String habilidade, TipoPokemon tipo, int numero, ItemEvolutivo item, int linhaEvolutiva) {

        super(nome, estagio, habilidade, tipo, numero, item);
        this.linhaEvolutiva = linhaEvolutiva;
    }


    public int getLinhaEvolutiva(){
        return linhaEvolutiva;
    }


    public void evoluir(){

        if(getEstagio() == 3){

            System.out.println(getNome() + " já está no estágio máximo!");

        }
        else{

            System.out.println(getNome() + " pode evoluir!");

        }
    }
}
