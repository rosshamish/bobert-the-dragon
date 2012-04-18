package editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.*;

/**
 *
 * @author Ross-Desktop
 */
public class EditFrame extends JFrame {
    
    static EditPanel ePanel;

    public EditFrame() {
        // Create game panel
        ePanel = new EditPanel(this);
        ePanel.setLayout(new GridLayout(1,1,0,0));
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        String[] buttons = {
            "",
            "Save",
            "Save as",
            "Open",
            "",
            "Change Background",
            "Add Platform",
            "Add Hard Object",
            "",
            "Add Collectable",
            "",
            "Add Enemy"
        };
        int buttonWidth = 180;
        int buttonHeight = 30;
        int buttonFontSize = 15;
        for (int i=0; i<buttons.length; i++) {
            if (!buttons[i].isEmpty()) {
                JButton button = new JButton(buttons[i]);
                button.addActionListener(ePanel);
                button.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, buttonFontSize));
                button.setHorizontalAlignment(SwingConstants.LEFT);
                buttonPanel.add(button);
            } else {
                JLabel extraSpace = new JLabel();
                extraSpace.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                extraSpace.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                buttonPanel.add(extraSpace);
            }
        }
        
        // Add all the panels to the frame.
        add(buttonPanel, BorderLayout.EAST);
        add(ePanel);
        
    }
}
