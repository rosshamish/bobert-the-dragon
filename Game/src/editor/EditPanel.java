package editor;

import game.*;
import game.Collidable.CollisionType;
import game.WorldObject.WorldObjectType;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import rosslib.RossLib;

/**
 *
 * @author Ross-Desktop
 */
public class EditPanel extends JPanel
                       implements Runnable, 
                       MouseListener, MouseMotionListener, 
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
    
    // Readable names for keys
    public static int keyLeft = KeyEvent.VK_A;
    public static int keyRight = KeyEvent.VK_D;
    public static int keyUp = KeyEvent.VK_W;
    public static int keyDown = KeyEvent.VK_S;
    
    public static Collidable heldObject;
    public static Collidable selectedObject;
    
    public static Icon collidableIcon;
    
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
                    "Name your fancy new level! Only use letters and spaces or this program will crash.",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.QUESTION_MESSAGE);
            if (name.isEmpty()) {
                System.exit(0);
            } else {
                level = new GameLevel(name, true);
            }
        } else {
            File dir = new File("resources/levels/");
            File[] levelFiles = dir.listFiles();
            String[] fileNames = new String[levelFiles.length];
            for (int i = 0; i < levelFiles.length; i++) {
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
        // Now that the level is definitely init'ed, init the button label's values.
        eFrame.fileNameLabel.setText(level.levelName);
        eFrame.sliderLevelWidth.setValue(level.background.drawBox.width);
        eFrame.sliderLevelHeight.setValue(level.background.drawBox.height);
        gameThread = new Thread(this);
        gameThread.start();
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
                    Enemy cur = level.enemies.get(i);
                    g2d.setColor(Color.green);
                    g2d.drawLine(cur.middleHorizontally(), cur.middleVertically(), 
                            cur.middleHorizontally()+cur.movementDistance, cur.middleVertically());
                    g2d.drawRect(cur.xPositionInCam(editCam)+cur.movementDistance, 
                            cur.yPositionInCam(editCam), 
                            cur.getWidth(), 
                            cur.getHeight());
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
            eFrame.fileNameLabel.setText(level.levelName);
            repaint();
        }
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
                    eFrame.sliderSelectedObjWidth.setValue(selectedObject.getWidth());
                    eFrame.sliderSelectedObjHeight.setValue(selectedObject.getHeight());
                    return;
                }
            }
        }
        if (level.enemies != null) {
            for (int i = level.enemies.size()-1; i >= 0; i--) {
                if (level.enemies.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    selectedObject = (Collidable) level.enemies.get(i);
                    eFrame.sliderSelectedObjWidth.setValue(selectedObject.getWidth());
                    eFrame.sliderSelectedObjHeight.setValue(selectedObject.getHeight());
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
                    eFrame.sliderSelectedObjWidth.setValue(selectedObject.getWidth());
                    eFrame.sliderSelectedObjHeight.setValue(selectedObject.getHeight());
                    return;
                }
            }
        }
        if (level.enemies != null) {
            for (int i = level.enemies.size()-1; i >= 0; i--) {
                if (level.enemies.get(i).hitBoxInCam(editCam).contains(mousePosInPanel)) {
                    heldObject = (Collidable) level.enemies.get(i);
                    selectedObject = (Collidable) level.enemies.get(i);
                    eFrame.sliderSelectedObjWidth.setValue(selectedObject.getWidth());
                    eFrame.sliderSelectedObjHeight.setValue(selectedObject.getHeight());
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
    
    /*
     * Use Ctrl-F (or Cmd-F, you filthy Mac users :) ) to search for the button 
     * you want to view or change or whatever.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equalsIgnoreCase("new")) {
            selectedObject = null;
            heldObject = null;
            int optionChosen = JOptionPane.showConfirmDialog(eFrame, 
                    "Save this level before starting a new one?", "Bobert the Dragon Level Editor (c) BlockTwo Studios",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (optionChosen == JOptionPane.CLOSED_OPTION) {
                return; // Just get out of here.
            } else if (optionChosen == JOptionPane.YES_OPTION) {
                String name = JOptionPane.showInputDialog("Save as: ", level.levelName);
                level.levelName = name;
                RossLib.writeLevelData(level);
                String newLevelName = JOptionPane.showInputDialog("New level name: ", "");
                level = new GameLevel(newLevelName, true);
            } else if (optionChosen == JOptionPane.NO_OPTION) {
                String newLevelName = JOptionPane.showInputDialog("New level name: ", "");
                level = new GameLevel(newLevelName, true);
            }
        } else if (action.equalsIgnoreCase("save")) {
            RossLib.writeLevelData(level);
        } else if (action.equalsIgnoreCase("save as")) {
            String name = JOptionPane.showInputDialog("Save as: ", level.levelName);
            level.levelName = name;
            RossLib.writeLevelData(level);
        } else if (action.equalsIgnoreCase("open")) {
            selectedObject = null;
            heldObject = null;
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
        } else if (action.equalsIgnoreCase("Test")) {
            String[] curLevel = {level.levelName};
            Main.main(curLevel); // Call the game's main method.
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
            File[] platformFiles = dir.listFiles();
            String[] fileNames = new String[platformFiles.length];
            for (int i=0; i<platformFiles.length; i++) {
                fileNames[i] = platformFiles[i].getName();
            }
            Object chosenPlatform = JOptionPane.showInputDialog(eFrame, 
                    "Choose a platform:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenPlatform == null) {
                System.out.println("chosenPlatform is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                Rectangle platRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50,
                        120, 70);
                level.collidables.add(new Collidable(platRect, WorldObjectType.PLATFORM, CollisionType.PLATFORM, platformsPath + (String) (chosenPlatform) ));
            }
        } else if (action.equalsIgnoreCase("Add Obstacle")) {
            String obstaclesPath = "resources/collidables/obstacles/";
            File dir = new File(obstaclesPath);
            File[] obstacleFiles = dir.listFiles();
            String[] fileNames = new String[obstacleFiles.length];
            for (int i=0; i<obstacleFiles.length; i++) {
                fileNames[i] = obstacleFiles[i].getName();
            }
            Object chosenObstacle = JOptionPane.showInputDialog(eFrame, 
                    "Choose an obstacle:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenObstacle == null) {
                System.out.println("chosenObstacle is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                Rectangle platRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50,
                        120, 70);
                level.collidables.add(new Collidable(platRect, WorldObjectType.OBSTACLE, CollisionType.IMPASSABLE, obstaclesPath + (String) (chosenObstacle) ));
            }
        } else if (action.equalsIgnoreCase("Add Collectable")) {
            String collectablesPath = "resources/collidables/collectables/";
            File dir = new File(collectablesPath);
            File[] collectableFiles = dir.listFiles();
            String[] fileNames = new String[collectableFiles.length];
            for (int i=0; i<collectableFiles.length; i++) {
                fileNames[i] = collectableFiles[i].getName();
            }
            Object chosenCollectable = JOptionPane.showInputDialog(eFrame, 
                    "Choose a collectable:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenCollectable == null) {
                System.out.println("chosenLevel is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                Rectangle platRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50,
                        120, 70);
                level.collidables.add(new Collidable(platRect, WorldObjectType.COLLECTABLE, CollisionType.PASSABLE, collectablesPath + (String) (chosenCollectable) ));
            }
        } else if (action.equalsIgnoreCase("Add Enemy")) {
            String enemiesPath = "resources/enemies/";
            File dir = new File(enemiesPath);
            File[] enemyFiles = dir.listFiles();
            String[] fileNames = new String[enemyFiles.length];
            for (int i=0; i<enemyFiles.length; i++) {
                fileNames[i] = enemyFiles[i].getName();
            }
            Object chosenEnemy = JOptionPane.showInputDialog(eFrame, 
                    "Choose an enemy:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenEnemy == null) {
                System.out.println("chosenEnemy is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                String moveDistance = JOptionPane.showInputDialog("How far should he be able to move? Please enter a number, don't play games here.", 
                        "5");
                Rectangle enemCollisRect = new Rectangle(50, 50, 100, 100);
                level.enemies.add(new Enemy(enemCollisRect, enemiesPath+(String)chosenEnemy, Integer.parseInt(moveDistance)));
            }
        } else if (action.equalsIgnoreCase("Change Image")) {
            String imagePath = "";
            String inputDialogMessage = "Available images: ";
            if (selectedObject.worldObjectType == WorldObjectType.PLATFORM) {
                imagePath = "resources/collidables/platforms/";
                inputDialogMessage = "Available platforms: ";
            } else if (selectedObject.worldObjectType == WorldObjectType.OBSTACLE) {
                imagePath = "resources/collidables/obstacles/";
                inputDialogMessage = "Available obstacles: ";
            } else if (selectedObject.worldObjectType == WorldObjectType.COLLECTABLE) {
                imagePath = "resources/collidables/collectables/";
                inputDialogMessage = "Available collectables: ";
            } else if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                imagePath = "resources/enemies/";
                inputDialogMessage = "Available enemies: ";
            }
            File dir = new File(imagePath);
            File[] imageFiles = dir.listFiles();
            String[] fileNames = new String[imageFiles.length];
            for (int i=0; i<imageFiles.length; i++) {
                fileNames[i] = imageFiles[i].getName();
            }
            Object chosenImagePath = JOptionPane.showInputDialog(eFrame, 
                    inputDialogMessage,
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            if (chosenImagePath == null) {
                System.out.println("background is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                selectedObject.setImage(imagePath+(String)chosenImagePath);
            }
        } else if (action.equalsIgnoreCase("Delete Object")) {
            int youSureBro = JOptionPane.showConfirmDialog(eFrame, 
                                            "You sure bro?", 
                                            "Bobert Level Editor - BlockTwo Studios", 
                                            JOptionPane.OK_CANCEL_OPTION);
            if (youSureBro == JOptionPane.OK_OPTION) {
                if (selectedObject.worldObjectType == WorldObjectType.PLATFORM) {
                    for (int i=0; i<level.collidables.size(); i++) {
                        if (level.collidables.get(i).equals(selectedObject)) {
                            level.collidables.remove(i);
                        }
                    }
                } else if (selectedObject.worldObjectType == WorldObjectType.OBSTACLE) {
                    for (int i=0; i<level.collidables.size(); i++) {
                        if (level.collidables.get(i).equals(selectedObject)) {
                            level.collidables.remove(i);
                        }
                    }
                } else if (selectedObject.worldObjectType == WorldObjectType.COLLECTABLE) {
                    for (int i=0; i<level.collidables.size(); i++) {
                        if (level.collidables.get(i).equals(selectedObject)) {
                            level.collidables.remove(i);
                        }
                    }
                } else if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                    for (int i=0; i<level.enemies.size(); i++) {
                        if (level.enemies.get(i).equals(selectedObject)) {
                            level.enemies.remove(i);
                        }
                    }
                }
                // We can't be selecting anything anymore because the current selection just got deleted.
                selectedObject = null; 
            } else {
                return; // They hit cancel, get the hell out of this function eh.
            }
        } else {
            System.out.println("Action \"" + action + "\" not implemented yet!");
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        String name = source.getName();
        if (name == null) {
            System.out.println("name is null");
        }
        
        if (source.getName().equalsIgnoreCase("Level Width")) {
            if (level.background != null) {
                level.background.drawBox.width = source.getValue();
            }
        } else if (name.equalsIgnoreCase("Level Height")) {
            if (level.background != null) {
                level.background.drawBox.height = source.getValue();
            }
        } else if (name.equalsIgnoreCase("Selected Object Width")) {
            if (selectedObject != null) {
                selectedObject.setWidth(source.getValue());
            }
        } else if (name.equalsIgnoreCase("Selected Object Height")) {
            if (selectedObject != null) {
                selectedObject.setHeight(source.getValue());
            }
        } else if (name.equalsIgnoreCase("Selected Enemy Movement Distance")) {
            if (selectedObject != null) {
                if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                    Enemy enem = (Enemy) selectedObject;
                    enem.movementDistance = source.getValue();
                }
            }
        } else {
            System.out.println("Slider "+name+" doesn't have behaviour set up yet!");
        }
    }
}
