package game;

import java.awt.GridLayout;
import java.awt.TextField;
import javax.swing.JFrame;

/**
 *
 * @author Ross-Desktop
 */
public class BobertFrame extends JFrame {
    
    static BobertPanel bPanel;
    
    public BobertFrame() {
        bPanel = new BobertPanel(this);
        bPanel.setLayout(new GridLayout(1,1,0,0));
        
        add(bPanel);
    }
}