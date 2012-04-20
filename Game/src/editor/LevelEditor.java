package editor;

import game.Main;
import javax.swing.JFrame;

/**
 * @author Ross-Desktop
 */
public class LevelEditor {

    static EditFrame eFrame;
    
    public static void main(String[] args) {
        eFrame = new EditFrame();
        eFrame.setTitle("Level Editor, Bobert the Dragon (c) 2012 BlockTwo Studios");
        eFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        eFrame.setSize(Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        eFrame.setResizable(false);
        eFrame.setLocationRelativeTo(null);
        eFrame.setVisible(true);
    }
}
