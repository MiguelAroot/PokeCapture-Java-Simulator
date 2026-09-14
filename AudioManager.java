import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioManager {
    public Clip tocar(String caminho, boolean loop) {
        try {
            File arquivo = new File(caminho);
            if (!arquivo.exists()) return null;
            AudioInputStream audio = AudioSystem.getAudioInputStream(arquivo);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);
            else clip.start();
            return clip;
        } catch (Exception e) {
            System.out.println("Áudio indisponível: " + caminho + " (" + e.getMessage() + ")");
            return null;
        }
    }

    public void parar(Clip clip) {
        if (clip == null) return;
        try {
            clip.stop();
            clip.close();
        } catch (Exception ignored) {}
    }

    public long duracaoMs(Clip clip) {
        return clip == null ? 0L : clip.getMicrosecondLength() / 1000L;
    }
}
