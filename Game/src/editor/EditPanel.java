package editor;

import game.Camera;
import game.Collidable;
import game.Collidable.CollisionType;
import game.GameLevel;
import game.Main;
import game.WorldObject.WorldObjectType;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import rosslib.RossLib;

/**
 *
 * @author Ross-Desktop
 */
public class EditPanel extends JPanel
                       implements Runnable, 
                       KeyListener, MouseListener, MouseMotionListener, 
                       ActionListener, ChangeListener {
    static EditFrame eFrame;
    
    public static boolean gameRunning;
    public static Thread gameThread;
    static boolean objectsDefined = false;
    
    public static GameLevel level;
    public static Camera editCam;
    
    public static int mouseX;
    public static int mouseDeltaX;
    public static int mouseY;
    public static int mouseDeltaY;
    
    public static Collidable heldObject;
    public static Collidable selectedObject;
    
    public EditPanel(EditFrame frame) {
        setBackground(new Color(200, 200, 200));
        this.setDoubleBuffered(true);
        // Need to assign the BobertFrame to a static variable so that we 
        // can add Listeners to it in addNotify(). Explanation is in addNotify().
        eFrame = frame;
        
        defineObjects();
    }
    
    public static void defineObjects() {
        editCam = new Camera(0, 0,
                Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        
        String[] options = {
            "New Level",
            "Load Level"};
        int newLevelOption = JOptionPane.showOptionDialog(eFrame,
                "Woohoo you're using the level editor! Make something awesome!",
                "Bobert Level Editor - BlockTwo Studios",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        boolean newLevel = true;
        switch (newLevelOption) {
            case JOptionPane.CLOSED_OPTION:
                System.exit(0);
                break;
            case JOptionPane.YES_OPTION:
                newLevel = true;
                break;
            case JOptionPane.NO_OPTION:
                newLevel = false;
                break;
            default:
                newLevel = true;
                break;
        }
        if (newLevel) {
            String name = JOptionPane.showInputDialog(eFrame, 
                    "Name your fancy new level!",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.QUESTION_MESSAGE);
            level = new GameLevel(name, true);
        } else {
            File dir = new File("resources/levels/");
            File[] levelFiles = dir.listFiles();
            String[] fileNames = new String[levelFiles.length];
            for (int i=0; i<levelFiles.length; i++) {
                fileNames[i] = levelFiles[i].getName();
            }
            Object chosenLevel = JOptionPane.showInputDialog(eFrame, 
                    "Which level would you like to load?",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenLevel == null) {
                System.out.println("chosenLevel is null");
                System.exit(0);
            } else {
                level = new GameLevel((String) chosenLevel, false);
            }
        }
        
        gameRunning = true;
        objectsDefined = true;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        gameThread = new Thread(this);
        gameThread.start();
        eFrame.addKeyListener(this);
        eFrame.addMouseListener(this);
        eFrame.addMouseMotionListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        if (objectsDefined) {
            
            if (level.background != null) {
                level.background.draw(g2d, editCam);
            }
            
            if (level.enemies != null) {
                for (int i=0; i<level.enemies.size(); i++) {
                    level.enemies.get(i).draw(g2d, editCam);
                }
            }
            
            if (level.collidables != null) {
                for (int i=0; i<level.collidables.size(); i++) {
                    level.collidables.get(i).draw(g2d, editCam);
                }
            }
            
            if (selectedObject != null) {
                g2d.setColor(new Color(21, 79, 140, 230));
                g2d.drawRoundRect(selectedObject.hitBoxInCam(editCam).x, selectedObject.hitBoxInCam(editCam).y, 
                        selectedObject.hitBox.width, selectedObject.hitBox.height, 
                        20, 20);
            }
            
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2d.drawString("Mouse X: "+String.valueOf(mouseX), 10, 10);
            g2d.drawString("Mouse Y: "+String.valueOf(mouseY), 10, 20);
            g2d.drawString("Mouse dX: "+String.valueOf(mouseDeltaX), 10, 30);
            g2d.drawString("Mouse dY: "+String.valueOf(mouseDeltaY), 10, 40); 
            
            g2d.drawString("heldObject: "+String.valueOf(heldObject), 10, 60);
            g2d.drawString("selectedObject: "+String.valueOf(selectedObject), 10, 70);
        }
    }
    
    @Override
    public void run() {
        while (gameRunning) {
            repaint();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Is this unnecessary? Not sure.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not implemented yet.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not implemented yet.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Check what we're clicking on.
        Point mousePosInPanel = new Point(e.getPoint());
        mousePosInPanel.y -= Main.B_WINDOW_BAR_HEIGHT;
        
        // This loop goes the reverse direction from the paintComponent loop
        // because we should grab the topmost objects if two are on top of
        // one another.
        if (level.collidables != null) {
            for (int i = level.collidables.size()-1; i >= 0; i--) {
                if (level.collidables.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    selectedObject = (Collidable) level.collidables.get(i);
                    return;
                }
            }
        }
        if (level.enemies != null) {
            for (int i = level.enemies.size()-1; i >= 0; i--) {
                if (level.enemies.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    selectedObject = (Collidable) level.enemies.get(i);
                    return;
                }
            }
        }
        // If we aren't actually clicking on an object, then set selectedObject to null
        selectedObject = null;
        return;
    }

    /*
     * When the mouse is clicked down.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Check what we're clicking on.
        Point mousePosInPanel = new Point(e.getPoint());
        mousePosInPanel.y -= Main.B_WINDOW_BAR_HEIGHT;
        
        // This loop goes the reverse direction from the paintComponent loop
        // because we should grab the topmost objects if two are on top of
        // one another.
        if (level.collidables != null) {
            for (int i = level.collidables.size()-1; i >= 0; i--) {
                if (level.collidables.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    heldObject = (Collidable) level.collidables.get(i);
                    selectedObject = (Collidable) level.collidables.get(i);
                    return;
                }
            }
        }
        if (level.enemies != null) {
            for (int i = level.enemies.size()-1; i >= 0; i--) {
                if (level.enemies.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    heldObject = (Collidable) level.enemies.get(i);
                    selectedObject = (Collidable) level.enemies.get(i);
                    return;
                }
            }
        }
    }

    /*
     * When the mouse is unclicked (let up).
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        heldObject = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseDeltaX = e.getX() - mouseX;
        mouseDeltaY = e.getY() - mouseY;
        
        // Move the x coordinate by the change in x.
        if (heldObject != null) {
            heldObject.moveRightBy(mouseDeltaX);
            heldObject.moveVerticallyBy(mouseDeltaY);
        }
        
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseDeltaX = e.getX() - mouseX;
        mouseDeltaY = e.getY() - mouseY;
        
        if (heldObject != null) {
            heldObject.moveRightBy(mouseDeltaX);
            heldObject.moveVerticallyBy(mouseDeltaY);
        }
        
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equalsIgnoreCase("save")) {
            RossLib.writeLevelData(level);
        } else if (action.equalsIgnoreCase("save as")) {
            String name = JOptionPane.showInputDialog("Level Name: ", level.levelName);
            level.levelName = name;
            RossLib.writeLevelData(level);
        } else if (action.equalsIgnoreCase("open")) {
            File dir = new File("resources/levels/");
            File[] levelFiles = dir.listFiles();
            String[] fileNames = new String[levelFiles.length];
            for (int i=0; i<levelFiles.length; i++) {
                fileNames[i] = levelFiles[i].getName();
            }
            Object chosenLevel = JOptionPane.showInputDialog(eFrame, 
                    "Open File:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenLevel == null) {
                System.out.println("chosenLevel is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                level = new GameLevel((String) chosenLevel, false);
            }
        } else if (action.equalsIgnoreCase("Change Background")) {
            String backgroundsPath = "resources/backgrounds/";
            File dir = new File(backgroundsPath);
            File[] backgroundFiles = dir.listFiles();
            String[] fileNames = new String[backgroundFiles.length];
            for (int i=0; i<backgroundFiles.length; i++) {
                fileNames[i] = backgroundFiles[i].getName();
            }
            Object chosenBackground = JOptionPane.showInputDialog(eFrame, 
                    "Choose a background:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenBackground == null) {
                System.out.println("background is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                level.background.setImage(backgroundsPath + (String) chosenBackground);
            }
        } else if (action.equalsIgnoreCase("Add Platform")) {
            String platformsPath = "resources/collidables/platforms/";
            File dir = new File(platformsPath);
            File[] levelFiles = dir.listFiles();
            String[] fileNames = new String[levelFiles.length];
            for (int i=0; i<levelFiles.length; i++) {
                fileNames[i] = levelFiles[i].getName();
            }
            Object chosenPlatform = JOptionPane.showInputDialog(eFrame, 
                    "Choose a platform:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenPlatform == null) {
                System.out.println("chosenLevel is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                Rectangle platRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50,
                        120, 70);
                level.collidables.add(new Collidable(platRect, WorldObjectType.NEUTRAL, CollisionType.PLATFORM, platformsPath + (String) (chosenPlatform) ));
            }
        } else {
            System.out.println("Action \"" + action + "\" not implemented yet!");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        String name = source.getName();
        if (name.equalsIgnoreCase("Level Width")) {
            level.background.drawBox.width = source.getValue();
        } else if (name.equalsIgnoreCase("Level Height")) {
            level.background.drawBox.height = source.getValue();
        } else if (name.equalsIgnoreCase("Selected Object Width")) {
            selectedObject.setWidth(source.getValue());
        } else if (name.equalsIgnoreCase("Selected Object Height")) {
            selectedObject.setHeight(source.getValue());
        } else {
            System.out.println("Slider "+name+" doesn't have behaviour set up yet!");
        }
    }
}
