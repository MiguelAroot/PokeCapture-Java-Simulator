public class Doce extends Item implements Usavel{
    public Doce(){
        super("Doce Raro", 100);
    }

    @Override
    public String getDetalhes(){
        return "Doce Raro";
    }

    @Override
    public String getDetalhesLoja(){
        return nome + " | preço: " + preco;
    }

    @Override
    public boolean usar(Pokemon alvo) {
        if(alvo.getEstagio() != 1){
            System.out.println("O pokemon " + alvo.getNome() + " não pode evoluir por meio desse Doce");
            return false;
        }else{
            System.out.println("O seu " + alvo.getNome() + " está evoluindo...");
            return true;
        }
    }


    
}
