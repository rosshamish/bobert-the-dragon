package editor;

import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 *
 * @author Ross-Desktop
 */
public class EditFrame extends JFrame {
    
    static EditPanel ePanel;

    public EditFrame() {
        ePanel = new EditPanel(this);
        ePanel.setLayout(new GridLayout(1,1,0,0));
        
        add(ePanel);
    }
}
