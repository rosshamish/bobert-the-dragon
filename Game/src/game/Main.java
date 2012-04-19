package game;

import java.io.File;
import javax.sound.sampled.*;
import javax.swing.JFrame;

/**
 * @author Ross-Desktop
 */
public class Main {

    public static final int B_WINDOW_HEIGHT = 700;
    public static final int B_WINDOW_BAR_HEIGHT = 24;
    public static final int B_WINDOW_CANVAS_HEIGHT = B_WINDOW_HEIGHT - B_WINDOW_BAR_HEIGHT;
    public static final int B_WINDOW_WIDTH = 1200;
    static BobertFrame bFrame;

    public static void main(String[] args) {
        bFrame = new BobertFrame();
        bFrame.setTitle("Bobert the Dragon ... (c) 2012 BlockTwo Studios");
        bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bFrame.setSize(B_WINDOW_WIDTH, B_WINDOW_HEIGHT);
        bFrame.setResizable(false);
        bFrame.setLocationRelativeTo(null);
        bFrame.setVisible(true);

    }
}
