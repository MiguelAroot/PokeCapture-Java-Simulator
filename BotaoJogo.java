public class BotaoJogo {
    public final float x, y, w, h;
    public final String texto;
    public final Runnable acao;
    public boolean ativo = true;

    public BotaoJogo(float x, float y, float w, float h, String texto, Runnable acao) {
        this.x = x; this.y = y; this.w = w; this.h = h;
        this.texto = texto; this.acao = acao;
    }

    public boolean contem(float mx, float my) {
        return ativo && mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
