package game;

import javax.swing.JFrame;

/**
 * @author Ross-Desktop
 */
public class Main {
    
    static final int B_WINDOW_HEIGHT = 700;
    static final int B_WINDOW_BAR_HEIGHT = 26;
    static final int B_WINDOW_CANVAS_HEIGHT = B_WINDOW_HEIGHT - B_WINDOW_BAR_HEIGHT;
    static final int B_WINDOW_WIDTH = 900;
    static BobertFrame bFrame;
    
    public static void main(String[] args) {
        bFrame = new BobertFrame();
        bFrame.setTitle("Bobert the Dragon ... (c) 2012 Bobert-the-Dragon Productions");
        bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bFrame.setSize(B_WINDOW_WIDTH, B_WINDOW_HEIGHT);
        bFrame.setResizable(false);
        bFrame.setLocationRelativeTo(null);
        bFrame.setVisible(true);
    }
}
