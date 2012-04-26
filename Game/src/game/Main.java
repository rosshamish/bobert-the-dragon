package game;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
    
    public static String[] curArgs;

    public static void main(String[] args) {
        if (args != null) {
            if (args.length != 0) {
                curArgs = args;
            } else {
                args = null;
            }
        }
        bFrame = new BobertFrame();
        bFrame.setTitle("Bobert the Dragon (c) 2012 BlockTwo Studios");
        if (args != null) {
            Point offsetFromGameFrame = new Point(BobertFrame.getFrames()[0].getX(), BobertFrame.getFrames()[0].getY());
            offsetFromGameFrame.x += 15;
            offsetFromGameFrame.y -= 15;
            bFrame.setLocation(offsetFromGameFrame);
            bFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            // This WindowListener is SUPER important, otherwise the game threads
            // never stop, and they keep building up and modifying the same data
            // from different places. Do not delete this :) -Ross
            bFrame.addWindowListener(new WindowListener() {

                @Override
                public void windowClosing(WindowEvent e) {
                    BobertPanel.gameRunning = false;
                }

                //<editor-fold defaultstate="collapsed" desc="Unused WindowListener functions">
                @Override
                public void windowOpened(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                public void windowClosed(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                public void windowIconified(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                public void windowDeiconified(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                public void windowActivated(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                public void windowDeactivated(WindowEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
                }
                //</editor-fold>
            });
        } else {
            bFrame.setLocationByPlatform(true);
            bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        bFrame.setSize(Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
//        bFrame.setAutoRequestFocus(true);
        bFrame.setBackground(new Color(93, 50, 237));
        bFrame.setResizable(false);
        bFrame.setVisible(true);
    }
}
