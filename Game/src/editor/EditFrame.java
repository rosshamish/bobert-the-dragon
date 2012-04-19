package editor;

import game.Main;
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
            "Label File",
            "New",
            "Save",
            "Save as",
            "Open",
            "",
            "Change Background",
            "Label Level Width",
            "Slider Level Width",
            "Label Level Height",
            "Slider Level Height",
            "",
            "Add Platform",
            "Add Hard Object",
            "",
            "Add Collectable",
            "",
            "Add Enemy",
            "Label Selected Object Width",
            "Slider Selected Object Width",
            "Label Selected Object Height",
            "Slider Selected Object Height"
        };
        int buttonWidth = 180;
        int buttonHeight = 30;
        int buttonFontSize = 15;
        
        int levelWidthMin = Main.B_WINDOW_WIDTH;
        int levelWidthMax = Main.B_WINDOW_WIDTH*30;
        int levelHeightMin = (int)(Main.B_WINDOW_HEIGHT*1.3); // Have faith.
        int levelHeightMax = Main.B_WINDOW_HEIGHT*20;
        
        int generalWidthMin = 10;
        int generalWidthMax = Main.B_WINDOW_WIDTH*2;
        int generalHeightMin = 10;
        int generalHeightMax = Main.B_WINDOW_HEIGHT*20;
        for (int i=0; i<buttons.length; i++) {
            if (buttons[i].contains("slider") || buttons[i].contains("Slider")) {
                JSlider slider;
                switch (buttons[i].substring("slider ".length())) {
                    case "Level Width":
                        slider = new JSlider(JSlider.HORIZONTAL,
                                levelWidthMin, levelWidthMax, (int) ((levelWidthMin + levelWidthMax) * 0.5));
                        slider.setMajorTickSpacing((int) ((levelWidthMax - levelWidthMin) * 0.05));
                        break;
                    case "Level Height":
                        slider = new JSlider(JSlider.HORIZONTAL,
                                levelHeightMin, levelHeightMax, (int) ((levelHeightMin + levelHeightMax) * 0.5));
                        slider.setMajorTickSpacing((int) ((levelHeightMax - levelHeightMin) * 0.05));
                        break;
                    case "Selected Object Width":
                        slider = new JSlider(JSlider.HORIZONTAL,
                                generalWidthMin, generalWidthMax, (int) ((generalWidthMin + generalWidthMax) * 0.5));
                        slider.setMajorTickSpacing((int) ((generalWidthMax - generalWidthMin) * 0.05));
                        break;
                    case "Selected Object Height":
                        slider = new JSlider(JSlider.HORIZONTAL,
                                generalHeightMin, generalHeightMax, (int) ((generalHeightMin + generalHeightMax) * 0.5));
                        slider.setMajorTickSpacing((int) ((generalHeightMax - generalHeightMin) * 0.05));
                        break;
                    default:
                        slider = new JSlider(JSlider.HORIZONTAL,
                                generalHeightMin, generalHeightMax, (int) ((generalHeightMin + generalHeightMax) * 0.5));
                        slider.setMajorTickSpacing((int) ((generalHeightMax - generalHeightMin) * 0.05));
                        slider.setName("default in the switch case");
                        break;
                }
                slider.setName(buttons[i].substring("slider ".length()));
                slider.setPaintTicks(true);
                slider.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                slider.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                slider.setAlignmentX(LEFT_ALIGNMENT);
                slider.addChangeListener(ePanel);
                buttonPanel.add(slider);
            } else if (buttons[i].contains("label") || buttons[i].contains("Label")) {
                JLabel label = new JLabel(buttons[i].substring("label ".length()));
                label.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                label.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                buttonPanel.add(label);
            } else if (!buttons[i].isEmpty()) {
                JButton button = new JButton(buttons[i]);
                button.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                button.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, buttonFontSize));
                button.setHorizontalAlignment(SwingConstants.LEFT);
                button.addActionListener(ePanel);
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
