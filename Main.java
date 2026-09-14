import processing.core.PApplet;

public class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        PApplet.main(JogoProcessing.class.getName());
    }
}
