package editor;

import game.Main;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.*;

/**
 *
 * @author Ross-Desktop
 */
public class EditFrame extends JFrame {
    
    static EditPanel ePanel;
    
    public static int buttonPanelWidth = 160;
    
    public JLabel labelFileName;
    public JSlider sliderLevelWidth;
    public JSlider sliderLevelHeight;
    public JSlider sliderSelectedObjWidth;
    public JSlider sliderSelectedObjHeight;
    public JLabel labelSelectedEnemyMoveDistance;
    public JSlider sliderSelectedEnemyMoveDistance;
    
    public EditFrame() {
        // Create game panel
        ePanel = new EditPanel(this);
        ePanel.setLayout(new GridLayout(1,1,0,0));
        // Create button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(160, Main.B_WINDOW_CANVAS_HEIGHT));
        String[] buttonPanelComponents = {
            "Label File",
            "New",
            "Save",
            "Save as",
            "Open",
            "Save & Test",
            "",
            "Change Background",
            "Label Level Width",
            "Slider Level Width",
            "Label Level Height",
            "Slider Level Height",
            "",
            "Label ----World Objects----",
            "Add Platform",
            "Add Obstacle",
            "Add Collectable",
            "Add Enemy",
            "Add Trigger",
            "",
            "Label ----Selected Object----",
            "Change Image",
            "Delete Object",
            "Label Width",
            "Slider Selected Object Width",
            "Label Height",
            "Slider Selected Object Height",
            "Label Movement Distance",
            "Slider Selected Enemy Movement Distance"
        };
        int buttonWidth = buttonPanelWidth;
        int buttonHeight = 23;
        int buttonFontSize = 12;
        
        int levelWidthMin = Main.B_WINDOW_WIDTH;
        int levelWidthMax = Main.B_WINDOW_WIDTH*30;
        int levelHeightMin = (int)(Main.B_WINDOW_HEIGHT*1.3); // Have faith. 
        int levelHeightMax = Main.B_WINDOW_HEIGHT*20;
        
        int generalWidthMin = 10;
        int generalWidthMax = (int)(Main.B_WINDOW_WIDTH*0.5);
        int generalHeightMin = 10;
        int generalHeightMax = (int)(Main.B_WINDOW_HEIGHT*0.5);
        
        int enemyMovementMin = (-1*(int)(Main.B_WINDOW_WIDTH*0.7));
        int enemyMovementMax = (int)(Main.B_WINDOW_WIDTH*0.7);
        for (int i=0; i<buttonPanelComponents.length; i++) {
            if (buttonPanelComponents[i].contains("slider") || buttonPanelComponents[i].contains("Slider")) {
                String sliderName = buttonPanelComponents[i].substring("slider ".length());
                if (sliderName.equalsIgnoreCase("Level Width")) {
                    sliderLevelWidth = new JSlider(JSlider.HORIZONTAL,
                            levelWidthMin, levelWidthMax, (int) ((levelWidthMin + levelWidthMax) * 0.5));
                    sliderLevelWidth.setMajorTickSpacing((int) ((levelWidthMax - levelWidthMin) * 0.05));
                    sliderLevelWidth.setName(sliderName);
                    sliderLevelWidth.setPaintTicks(true);
                    sliderLevelWidth.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderLevelWidth.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderLevelWidth.setAlignmentX(LEFT_ALIGNMENT);
                    sliderLevelWidth.addChangeListener(ePanel);
                    buttonPanel.add(sliderLevelWidth);
                } else if (sliderName.equalsIgnoreCase("Level Height")) {
                    sliderLevelHeight = new JSlider(JSlider.HORIZONTAL,
                            levelHeightMin, levelHeightMax, (int) ((levelHeightMin + levelHeightMax) * 0.5));
                    sliderLevelHeight.setMajorTickSpacing((int) ((levelHeightMax - levelHeightMin) * 0.05));
                    sliderLevelHeight.setName(sliderName);
                    sliderLevelHeight.setPaintTicks(true);
                    sliderLevelHeight.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderLevelHeight.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderLevelHeight.setAlignmentX(LEFT_ALIGNMENT);
                    sliderLevelHeight.addChangeListener(ePanel);
                    buttonPanel.add(sliderLevelHeight);
                } else if (sliderName.equalsIgnoreCase("Selected Object Width")) {
                    sliderSelectedObjWidth = new JSlider(JSlider.HORIZONTAL,
                            generalWidthMin, generalWidthMax, (int) ((generalWidthMin + generalWidthMax) * 0.5));
                    sliderSelectedObjWidth.setMajorTickSpacing((int) ((generalWidthMax - generalWidthMin) * 0.05));
                    sliderSelectedObjWidth.setName(sliderName);
                    sliderSelectedObjWidth.setPaintTicks(true);
                    sliderSelectedObjWidth.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedObjWidth.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedObjWidth.setAlignmentX(LEFT_ALIGNMENT);
                    sliderSelectedObjWidth.addChangeListener(ePanel);
                    buttonPanel.add(sliderSelectedObjWidth);
                } else if (sliderName.equalsIgnoreCase("Selected Object Height")) {
                    sliderSelectedObjHeight = new JSlider(JSlider.HORIZONTAL,
                            generalHeightMin, generalHeightMax, (int) ((generalHeightMin + generalHeightMax) * 0.5));
                    sliderSelectedObjHeight.setMajorTickSpacing((int) ((generalHeightMax - generalHeightMin) * 0.05));
                    sliderSelectedObjHeight.setName(sliderName);
                    sliderSelectedObjHeight.setPaintTicks(true);
                    sliderSelectedObjHeight.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedObjHeight.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedObjHeight.setAlignmentX(LEFT_ALIGNMENT);
                    sliderSelectedObjHeight.addChangeListener(ePanel);
                    buttonPanel.add(sliderSelectedObjHeight);
                } else if (sliderName.equalsIgnoreCase("Selected Enemy Movement Distance")) {
                    sliderSelectedEnemyMoveDistance = new JSlider(JSlider.HORIZONTAL,
                            enemyMovementMin, enemyMovementMax, (int) ((enemyMovementMin + enemyMovementMax) * 0.5));
                    sliderSelectedEnemyMoveDistance.setMajorTickSpacing((int) ((enemyMovementMax - enemyMovementMin) * 0.05));
                    sliderSelectedEnemyMoveDistance.setName(sliderName);
                    sliderSelectedEnemyMoveDistance.setPaintTicks(true);
                    sliderSelectedEnemyMoveDistance.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedEnemyMoveDistance.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    sliderSelectedEnemyMoveDistance.setAlignmentX(LEFT_ALIGNMENT);
                    sliderSelectedEnemyMoveDistance.addChangeListener(ePanel);
                    sliderSelectedEnemyMoveDistance.setVisible(false);
                    sliderSelectedEnemyMoveDistance.setToolTipText("Change where this enemy moves back and forth to (Green box is where they will move to)");
                    buttonPanel.add(sliderSelectedEnemyMoveDistance);
                } else {
                    JSlider slider = new JSlider(JSlider.HORIZONTAL,
                            generalHeightMin, generalHeightMax, (int) ((generalHeightMin + generalHeightMax) * 0.5));
                    slider.setMajorTickSpacing((int) ((generalHeightMax - generalHeightMin) * 0.05));
                    slider.setName("Unspecified Slider Name");
                    slider.setPaintTicks(true);
                    slider.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    slider.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    slider.setAlignmentX(LEFT_ALIGNMENT);
                    slider.addChangeListener(ePanel);
                    buttonPanel.add(slider);
                }
                
            } else if (buttonPanelComponents[i].contains("label") || buttonPanelComponents[i].contains("Label")) {
                String labelName = buttonPanelComponents[i].substring("label".length());
                JLabel label = new JLabel(labelName);
                if (labelName.trim().equalsIgnoreCase("File")) {
                    labelFileName = new JLabel(labelName);
                    labelFileName.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    labelFileName.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    labelFileName.setHorizontalAlignment(SwingConstants.CENTER);
                    buttonPanel.add(labelFileName);
                } else if (labelName.trim().equalsIgnoreCase("Movement Distance")) {
                    labelSelectedEnemyMoveDistance = new JLabel("Enemy Movement");
                    labelSelectedEnemyMoveDistance.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    labelSelectedEnemyMoveDistance.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    labelSelectedEnemyMoveDistance.setHorizontalAlignment(SwingConstants.LEFT);
                    labelSelectedEnemyMoveDistance.setVisible(false);
                    buttonPanel.add(labelSelectedEnemyMoveDistance);
                } else {
                    label.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
                    label.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    buttonPanel.add(label);
                }
            } else if (!buttonPanelComponents[i].isEmpty()) {
                JButton button = new JButton(buttonPanelComponents[i]);
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
        InputMap inputMap = buttonPanel.getInputMap();

        //Ctrl-b to go backward one character
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);
        inputMap.put(key, EditPanel.cut());
 
        //Ctrl-f to go forward one character
        key = KeyStroke.getKeyStroke("c");
        inputMap.put(key, EditPanel.copy());

        //Ctrl-p to go up one line
        key = KeyStroke.getKeyStroke("v");
        inputMap.put(key, EditPanel.paste());
        add(buttonPanel, BorderLayout.EAST);
        add(ePanel);
    }
}
