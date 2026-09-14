public class PedraEvolucao extends Item implements Usavel{
    public PedraEvolucao(){
        super("Pedra da Evolução", 200);
    }

    @Override
    public String getDetalhes(){
        return "Pedra da Evolução";
    }

    @Override
    public String getDetalhesLoja(){
        return nome + " | preço: " + preco;
    }

    @Override
    public boolean usar(Pokemon alvo) {
        if(alvo.getEstagio() != 2){
            System.out.println("O pokemon " + alvo.getNome() + " não pode ser evoluido por meio dessa Pedra");
            return false;
        }else{
            System.out.println("O seu " + alvo.getNome() + " está evoluindo...");
            return true;
        }
    }


    
}
