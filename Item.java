public abstract class Item{
    protected String nome;
    protected int preco;

    public Item(String nome, int preco){
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome(){
        return nome;
    }
    public int getPreco(){
        return preco;
    }

    public abstract String getDetalhes(); //aparece na batalha sem aparecer preço
    public abstract String getDetalhesLoja(); //aparece na loja com preço
}