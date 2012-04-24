package editor;

import game.GameLevel;
import game.Main;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import rosslib.RossLib;

/**
 * @author Ross-Desktop
 */
public class LevelEditor {

    static EditFrame eFrame;
    
    public static void main(String[] args) {
        eFrame = new EditFrame();
        eFrame.setTitle("Level Editor, Bobert the Dragon (c) 2012 BlockTwo Studios");
        eFrame.setSize(Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        eFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        eFrame.setResizable(false);
        eFrame.setLocationRelativeTo(null);
        
        eFrame.addWindowListener(new WindowListener() {

            @Override
            public void windowClosing(WindowEvent e) {
                int optionChosen = JOptionPane.showConfirmDialog(eFrame,
                        "Save changes?", "Bobert the Dragon Level Editor (c) BlockTwo Studios",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                if (optionChosen == JOptionPane.CLOSED_OPTION) {
                    // Do nothing
                } else if (optionChosen == JOptionPane.YES_OPTION) {
                    String name = JOptionPane.showInputDialog("Save as: ", EditPanel.level.levelName);
                    if (name != null) {
                        EditPanel.level.levelName = name;
                        RossLib.writeLevelData(EditPanel.level);
                    }
                    System.exit(0);
                } else if (optionChosen == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
                
            }

            //<editor-fold defaultstate="collapsed" desc="Unused WindowListener stuff">
            @Override
            public void windowOpened(WindowEvent e) {
                // Unused
            }
            
            @Override
            public void windowClosed(WindowEvent e) {
                // Unused
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
                // Unused
            }
            
            @Override
            public void windowDeiconified(WindowEvent e) {
                // Unused
            }
            
            @Override
            public void windowActivated(WindowEvent e) {
                // Unused
            }
            
            @Override
            public void windowDeactivated(WindowEvent e) {
                // Unused
            }
            //</editor-fold>
        });
        eFrame.setVisible(true);
    }
}
