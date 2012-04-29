package editor;

import game.Collidable.CollisionType;
import game.*;
import game.WorldObject.WorldObjectType;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public static int camMovementFrame = 0;
    public static final int camMovementDelay = 0;
    
    public static boolean movingLeft;
    public static boolean movingUp;
    public static boolean movingRight;
    public static boolean movingDown;
    
    public static int borderSize = 50;
    public static Rectangle borderLeft;
    public static Image borderLeftImg;
    
    public static Rectangle borderTop;
    public static Image borderTopImg;
    
    public static Rectangle borderRight;
    public static Image borderRightImg;
    public static int borderRightFrame = 0;
    public static final int borderRightDelay = 100;
    public static boolean wantsToMoveRight = false;

    public static Rectangle borderBottom;
    public static Image borderBottomImg;
    
    public static int mouseX;
    public static int mouseDeltaX;
    public static int mouseY;
    public static int mouseDeltaY;
    
    // Readable names for keys
    public static int keyLeft = KeyEvent.VK_A;
    public static int keyRight = KeyEvent.VK_D;
    public static int keyUp = KeyEvent.VK_W;
    public static int keyDown = KeyEvent.VK_S;
    public static int keyCut = KeyEvent.VK_CUT; 
    public static int keyCopy = KeyEvent.VK_COPY;
    public static int keyPaste = KeyEvent.VK_PASTE;
    
    public static Collidable heldObject;
    public static Collidable selectedObject;
    public static Collidable copiedObject = null;
    private static long FPSStartOfLoopTime = 0;
    private final static long FPSDelayPerFrame = 1;
    
    
    public EditPanel(EditFrame frame) {
        setBackground(new Color(200, 200, 200));
        this.setDoubleBuffered(true);
        // Need to assign the BobertFrame to a static variable so that we 
        // can add Listeners to it in addNotify(). Explanation is in addNotify().
        eFrame = frame;
        
        defineObjects();
    }
    
    public static void defineObjects() {
        
        borderLeft = new Rectangle(0, Main.B_WINDOW_BAR_HEIGHT,
                borderSize, Main.B_WINDOW_CANVAS_HEIGHT);
        borderLeftImg = new ImageIcon("resources/editor/border_facingLeft.png").getImage();
        borderTop = new Rectangle(0, Main.B_WINDOW_BAR_HEIGHT,
                Main.B_WINDOW_WIDTH, borderSize);
        borderTopImg = new ImageIcon("resources/editor/border_facingUp.png").getImage();
        borderRight = new Rectangle(Main.B_WINDOW_WIDTH-borderSize-EditFrame.buttonPanelWidth, Main.B_WINDOW_BAR_HEIGHT,
                borderSize, Main.B_WINDOW_CANVAS_HEIGHT);
        borderRightImg = new ImageIcon("resources/editor/border_facingRight.png").getImage();
        borderBottom = new Rectangle(0, Main.B_WINDOW_HEIGHT-borderSize,
                Main.B_WINDOW_WIDTH, borderSize);
        borderBottomImg = new ImageIcon("resources/editor/border_facingDown.png").getImage();
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
                options[1]);
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
            if (name == null) {
                System.exit(0);
            } else {
                level = new GameLevel(name, true);
            }
        } else {
            File dir = new File("resources/levels/");
            File[] levelFiles = dir.listFiles();
            String[] fileNames = new String[levelFiles.length-1]; //to account for the xml file
            ArrayList<String> tempFileNames = new ArrayList<String>();
            for (int i = 0; i < levelFiles.length; i++) {
                if (levelFiles[i].isDirectory()) {
                    tempFileNames.add(levelFiles[i].getName());
                }
            }
            for (int i=0; i<tempFileNames.size(); i++) {
                fileNames[i] = tempFileNames.get(i);
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
        editCam = new Camera(0, 0,
                Main.B_WINDOW_WIDTH - EditFrame.buttonPanelWidth, Main.B_WINDOW_HEIGHT);
        for (int i=0; i<level.collidables.size(); i++) {
            Collidable cur = level.collidables.get(i);
            if (cur.worldObjectType == WorldObjectType.TRIGGER) {
                if (cur.name.equalsIgnoreCase("start")) {
                    editCam = new Camera(cur.leftEdge() - Main.B_WINDOW_WIDTH/2, cur.topEdge() - Main.B_WINDOW_HEIGHT/2,
                            Main.B_WINDOW_WIDTH - EditFrame.buttonPanelWidth, Main.B_WINDOW_HEIGHT);
                    break;
                }
            }
        }
        
        
        gameRunning = true;
        objectsDefined = true;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Now that the level is definitely init'ed, init the button label's values.
        eFrame.labelFileName.setText(level.levelName);
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
                    if (selectedObject != null) {
                        if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                            g2d.setColor(Color.green);
                            g2d.drawLine(cur.xPositionInCam(editCam) + cur.getWidth() / 2, cur.yPositionInCam(editCam) + cur.getHeight() / 2,
                                    cur.xPositionInCam(editCam) + cur.getWidth() / 2 + cur.movementDistance, cur.yPositionInCam(editCam) + cur.getHeight() / 2);
                            g2d.drawRect(cur.xPositionInCam(editCam) + cur.movementDistance,
                                    cur.yPositionInCam(editCam),
                                    cur.getWidth(),
                                    cur.getHeight());
                        }
                    }
                    
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
            g2d.setColor(Color.blue);
            AlphaComposite full = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            AlphaComposite med = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
            AlphaComposite low = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f);
            g2d.setComposite(low); // drop the opacity
            g2d.drawImage(borderLeftImg, borderLeft.x, borderLeft.y, borderLeft.width, borderLeft.height, null);
            g2d.drawImage(borderTopImg, borderTop.x, borderTop.y-Main.B_WINDOW_BAR_HEIGHT, borderTop.width, borderTop.height, null);
            g2d.drawImage(borderRightImg, borderRight.x, borderRight.y, borderRight.width, borderRight.height, null);
            g2d.drawImage(borderBottomImg, borderBottom.x, borderBottom.y-Main.B_WINDOW_BAR_HEIGHT, borderBottom.width, borderBottom.height, null);

            // Up the opacity
            g2d.setComposite(med);
            if (movingLeft) {
                g2d.drawImage(borderLeftImg, borderLeft.x, borderLeft.y, borderLeft.width, borderLeft.height, null);
            }
            if (movingUp) {
                g2d.drawImage(borderTopImg, borderTop.x, borderTop.y-Main.B_WINDOW_BAR_HEIGHT, borderTop.width, borderTop.height, null);
            }
            if (movingRight) {
                g2d.drawImage(borderRightImg, borderRight.x, borderRight.y, borderRight.width, borderRight.height, null);
            }
            if (movingDown) {
                g2d.drawImage(borderBottomImg, borderBottom.x, borderBottom.y-Main.B_WINDOW_BAR_HEIGHT, borderBottom.width, borderBottom.height, null);
            }
            // reset the opacity
            g2d.setComposite(full);
            
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
//            g2d.drawString("Selected Object: "+selectedObject, 10, 10);
//            g2d.drawString("Copied Object: "+copiedObject, 10, 20);
//            g2d.drawString("Right Border Frame: "+borderRightFrame, 10, 40);
//            g2d.drawString("Right Border Delay: "+borderRightDelay, 10, 50); 
//            
//            g2d.drawString("heldObject: "+String.valueOf(heldObject), 10, 60);
//            g2d.drawString("selectedObject: "+String.valueOf(selectedObject), 10, 70);
//            
//            g2d.drawString("movingLeft: "+movingLeft, 10, 90);
//            g2d.drawString("movinUp: "+movingUp, 10, 100);
        }
    }
    
    @Override
    public void run() {
        
        while (gameRunning) {
            
            FPSStartOfLoop();
            
            eFrame.labelFileName.setText(level.levelName);
            
            
            if (wantsToMoveRight) {
                borderRightFrame++;
                // int overflow protection. You never know.
                if (borderRightFrame > borderRightDelay + 100)
                    borderRightFrame = borderRightDelay + 10;
            }
            if (camMovementFrame >= camMovementDelay) {
                if (movingRight && (editCam.getX() + editCam.getWidth() < level.background.drawBox.width)) {
                    editCam.moveRightBy(1);
                }
                if (movingLeft && editCam.getX() > level.floor.hitBox.x) {
                    editCam.moveLeftBy(1);
                }
                if (movingDown && 
                        editCam.getY() + editCam.getHeight() < level.background.drawBox.y + level.background.drawBox.height) {
                    editCam.moveVerticallyBy(1);
                }
                if (movingUp && editCam.getY() > level.background.drawBox.y) {
                    editCam.moveVerticallyBy(-1);
                }
                camMovementFrame = 0;
            } else {
                camMovementFrame++;
            }
            level.floor.setY(level.background.getHeight());
            level.floor.setWidth(level.background.getWidth());
            
            repaint();
            
            FPSEndOfLoop();
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
                    eFrame.labelSelectedEnemyMoveDistance.setVisible(false);
                    eFrame.sliderSelectedEnemyMoveDistance.setVisible(false);
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
                    Enemy enem = level.enemies.get(i);
                    eFrame.sliderSelectedEnemyMoveDistance.setValue(enem.movementDistance);
                    eFrame.labelSelectedEnemyMoveDistance.setVisible(true);
                    eFrame.sliderSelectedEnemyMoveDistance.setVisible(true);
                    return;
                }
            }
        }
        // If we aren't actually clicking on an object, then set selectedObject to null
        selectedObject = null;
        eFrame.labelSelectedEnemyMoveDistance.setVisible(false);
        eFrame.sliderSelectedEnemyMoveDistance.setVisible(false);
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
                    eFrame.labelSelectedEnemyMoveDistance.setVisible(false);
                    eFrame.sliderSelectedEnemyMoveDistance.setVisible(false);
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
                    Enemy enem = level.enemies.get(i);
                    eFrame.sliderSelectedEnemyMoveDistance.setValue(enem.movementDistance);
                    eFrame.labelSelectedEnemyMoveDistance.setVisible(true);
                    eFrame.sliderSelectedEnemyMoveDistance.setVisible(true);
                    return;
                }
            }
        }
        eFrame.labelSelectedEnemyMoveDistance.setVisible(false);
        eFrame.sliderSelectedEnemyMoveDistance.setVisible(false);
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
        movingLeft = false;
        movingUp = false;
        movingRight = false;
        movingDown = false;
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
        boolean moving = false;
        if (borderLeft.contains(e.getPoint())) {
            movingLeft = true;
            moving = true;
        } else {
            movingLeft = false;
        }
        if (borderRight.contains(e.getPoint())) {
            if (borderRightFrame >= borderRightDelay) {
                movingRight = true;
            } else {
                // Increment the delay counter, get closer to actually
                // scrolling each time.
                wantsToMoveRight = true;
            }
            moving = true;
        } else {
            // Zero out the delay counter because the user doesn't want to
            // scroll right anymore (cause we aren't touching the side)
            borderRightFrame = 0;
            wantsToMoveRight = false;
            movingRight = false;
        }
        if (borderTop.contains(e.getPoint())) {
            movingUp = true;
            moving = true;
        } else {
            movingUp = false;
        }
        if (borderBottom.contains(e.getPoint())) {
            movingDown = true;
            moving = true;
        } else {
            movingDown = false;
        }
        if (!moving) {
            movingLeft = false;
            movingUp = false;
            movingRight = false;
            movingDown = false;
        }
    }
    
    /*
     * Use Ctrl-F (or Cmd-F, you filthy Mac users :) ) to search for the button 
     * you want to view or change or whatever.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equalsIgnoreCase("new (Ctrl+N)")) {
            newLevel();
        } else if (action.equalsIgnoreCase("save (Ctrl+S)")) {
            saveLevel();
        } else if (action.equalsIgnoreCase("save as (Ctrl+Shift+S)")) {
            saveAsLevel();
        } else if (action.equalsIgnoreCase("open (Ctrl+O)")) {
            openLevel();
        } else if (action.equalsIgnoreCase("Save & Test (Ctrl+T)")) {
            saveAndTestLevel();
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
                Rectangle collectableRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50,
                        70, 70);
                level.collidables.add(new Collidable(collectableRect, WorldObjectType.COLLECTABLE, CollisionType.PASSABLE, collectablesPath + (String) (chosenCollectable) ));
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
                Rectangle enemCollisRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50, 100, 100);
                level.enemies.add(new Enemy(enemCollisRect, enemiesPath+(String)chosenEnemy, Enemy.defaultMovementDistance));
            }
        } else if (action.equalsIgnoreCase("Add Trigger")) {
            String triggersPath = "resources/collidables/triggers/";
            File dir = new File(triggersPath);
            File[] triggerFiles = dir.listFiles();
            String[] fileNames = new String[triggerFiles.length];
            for (int i=0; i<triggerFiles.length; i++) {
                fileNames[i] = triggerFiles[i].getName();
            }
            Object chosenTrigger = JOptionPane.showInputDialog(eFrame, 
                    "Choose a trigger:",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    fileNames,
                    fileNames[0]);
            String strChosenAction = null;
            if (chosenTrigger != null) {
                String[] availableActions = {
                    "start",
                    "end",
                    "play",
                    "audio"
                };
                Object chosenAction = JOptionPane.showInputDialog(eFrame,
                        "Choose an action:",
                        "Bobert Level Editor - BlockTwo Studios",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        availableActions,
                        availableActions[0]);
                strChosenAction = (String) chosenAction;
                if (strChosenAction != null) {
                    if (strChosenAction.equalsIgnoreCase("audio")) {
                        strChosenAction += " " + JOptionPane.showInputDialog(eFrame,
                                "If sound effect, specify the filename. If larger audio, specify its name.",
                                "***.wav OR SoundFile1");
                    }
                }
            }
            if (chosenTrigger == null || strChosenAction == null) {
                System.err.println("your trigger is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                Rectangle trigCollisRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50, 
                        100, 100);
                level.collidables.add(new Collidable(trigCollisRect, WorldObjectType.TRIGGER, CollisionType.PASSABLE,
                        triggersPath + (String)chosenTrigger, strChosenAction));
            }
        } else if (action.equalsIgnoreCase("Change Img (A & D)")) {
            changeSelectedObjectImage();
        } else if (action.equalsIgnoreCase("Delete Object (Delete)")) {
            deleteSelectedObject();
        } else {
            System.out.println("Action \"" + action + "\" not implemented yet!");
        }
    }
    
    public static void newLevel() {
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
    }
    
    public static void openLevel() {
        selectedObject = null;
        heldObject = null;
        File dir = new File("resources/levels/");
        File[] levelFiles = dir.listFiles();
        String[] fileNames = new String[levelFiles.length - 1]; //to account for the xml file
        ArrayList<String> tempFileNames = new ArrayList<String>();
        for (int i = 0; i < levelFiles.length; i++) {
            if (levelFiles[i].isDirectory()) {
                tempFileNames.add(levelFiles[i].getName());
            }
        }
        for (int i = 0; i < tempFileNames.size(); i++) {
            fileNames[i] = tempFileNames.get(i);
        }
        Object chosenLevel = JOptionPane.showInputDialog(eFrame,
                "Open File:",
                "Bobert Level Editor - BlockTwo Studios",
                JOptionPane.PLAIN_MESSAGE,
                null,
                fileNames,
                fileNames[0]);
        if (chosenLevel == null) {
            System.err.println("chosenLevel is null");
            // Just quit the box. Nothing wrong, they just changed their mind.
        } else {
            level = new GameLevel((String) chosenLevel, false);
        }
    }
    
    public static void saveLevel() {
        RossLib.writeLevelData(level);
    }
    
    public static void saveAsLevel() {
        String name = JOptionPane.showInputDialog("Save as: ", level.levelName);
            level.levelName = name;
            RossLib.writeLevelData(level);
    }
    
    public static void saveAndTestLevel() {
        RossLib.writeLevelData(level);
        String[] curLevel = {level.levelName};
        BobertPanel.gameRunning = true;
        Main.main(curLevel); // Call the game's main method.
    }
    
    public static void changeSelectedObjectImage() {
        String imagePath = "";
        String inputDialogMessage = "Available images: ";
        if (selectedObject != null) {
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
            } else if (selectedObject.worldObjectType == WorldObjectType.TRIGGER) {
                imagePath = "resources/collidables/triggers/";
                inputDialogMessage = "Available triggers: ";
            }
            File dir = new File(imagePath);
            File[] imageFiles = dir.listFiles();
            String[] fileNames = new String[imageFiles.length];
            for (int i = 0; i < imageFiles.length; i++) {
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
                selectedObject.setImage(imagePath + (String) chosenImagePath);
            }
        }
    }
    
    public static void tabSelectedObjectImage(boolean forward) {
        String imagePath = "";
        if (selectedObject != null) {
            if (selectedObject.worldObjectType == WorldObjectType.PLATFORM) {
                imagePath = "resources/collidables/platforms/";
            } else if (selectedObject.worldObjectType == WorldObjectType.OBSTACLE) {
                imagePath = "resources/collidables/obstacles/";
            } else if (selectedObject.worldObjectType == WorldObjectType.COLLECTABLE) {
                imagePath = "resources/collidables/collectables/";
            } else if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                imagePath = "resources/enemies/";
            } else if (selectedObject.worldObjectType == WorldObjectType.TRIGGER) {
                imagePath = "resources/collidables/triggers/";
            }
            File dir = new File(imagePath);
            File[] imageFiles = dir.listFiles();
            String[] fileNames = new String[imageFiles.length];
            String nextImage = "";
            for (int i = 0; i < imageFiles.length; i++) {
                if ((imagePath + imageFiles[i].getName()).equalsIgnoreCase(selectedObject.getImageLocation())) {
                    if (forward) {
                        // Moving forward
                        if (i == imageFiles.length-1) {
                            nextImage = imageFiles[0].getName();
                        } else {
                            nextImage = imageFiles[i+1].getName();
                        }
                    } else {
                        // Moving backward
                        if (i == 0) {
                            nextImage = imageFiles[imageFiles.length-1].getName();
                        } else {
                            nextImage = imageFiles[i-1].getName();
                        }
                    }
                    break;
                }
            }
            
            if (nextImage == null) {
                System.out.println("background is null");
                // Just quit the box. Nothing wrong, they just changed their mind.
            } else {
                selectedObject.setImage(imagePath + nextImage);
            }
        }
    }
    
    public static void deleteSelectedObject() {
        if (selectedObject != null) {
            int youSureBro = JOptionPane.showConfirmDialog(eFrame,
                    "You sure bro?",
                    "Bobert Level Editor - BlockTwo Studios",
                    JOptionPane.OK_CANCEL_OPTION);
            if (youSureBro == JOptionPane.OK_OPTION) {
                if (selectedObject.worldObjectType == WorldObjectType.PLATFORM
                        || selectedObject.worldObjectType == WorldObjectType.OBSTACLE
                        || selectedObject.worldObjectType == WorldObjectType.TRIGGER
                        || selectedObject.worldObjectType == WorldObjectType.FLOOR) {
                    for (int i = 0; i < level.collidables.size(); i++) {
                        if (level.collidables.get(i).equals(selectedObject)) {
                            level.collidables.remove(i);
                        }
                    }
                } else if (selectedObject.worldObjectType == WorldObjectType.COLLECTABLE) {
                    for (int i = 0; i < level.collidables.size(); i++) {
                        if (level.collidables.get(i).equals(selectedObject)) {
                            level.collidables.remove(i);
                        }
                    }
                } else if (selectedObject.worldObjectType == WorldObjectType.ENEMY) {
                    for (int i = 0; i < level.enemies.size(); i++) {
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
        }
    }
    
    public static boolean cut() {
        if (selectedObject != null) {
            copiedObject = new Collidable(selectedObject);
            // Now delete the selected object
            if (selectedObject.worldObjectType == WorldObjectType.PLATFORM
                        || selectedObject.worldObjectType == WorldObjectType.OBSTACLE
                        || selectedObject.worldObjectType == WorldObjectType.TRIGGER
                        || selectedObject.worldObjectType == WorldObjectType.FLOOR) {
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
                return true;
        }
        return false;
    }
    
    public static boolean copy() {
        if (selectedObject != null) {
            copiedObject = new Collidable(selectedObject);
            return true;
        }
        return false;
    }
    
    public static boolean paste() {
        if (copiedObject != null) {
        Rectangle pastedCollisRect = new Rectangle(editCam.getX() + 50, editCam.getY() + 50, 
                        copiedObject.getWidth(), copiedObject.getHeight());
                level.collidables.add(new Collidable(pastedCollisRect, 
                        copiedObject.worldObjectType, copiedObject.collisionType, 
                        copiedObject.getImageLocation()));
                return true;
        } 
        return false;
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
                int newWidth = source.getValue();
                level.background.drawBox.width = newWidth;
            }
        } else if (name.equalsIgnoreCase("Level Height")) {
            if (level.background != null) {
                int newHeight = source.getValue();
                level.background.drawBox.height = newHeight;
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
    
    static void FPSStartOfLoop() {
        FPSStartOfLoopTime = System.currentTimeMillis();
    }

    static void FPSEndOfLoop() {
        long sleepTime = FPSDelayPerFrame - (System.currentTimeMillis() - FPSStartOfLoopTime);
        if (sleepTime < 1) {
            sleepTime = 1;
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException ex) {
            Logger.getLogger(BobertPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Object[] getInputAndActionMaps() {
        KeyStroke key = null;
        InputMap inputMap = new InputMap();
        ActionMap actionMap = new ActionMap();
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "new");
        actionMap.put("new", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newLevel();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "open");
        actionMap.put("open", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLevel();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "save");
        actionMap.put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveLevel();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK);
        inputMap.put(key, "saveAs");
        actionMap.put("saveAs", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsLevel();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "saveAndTest");
        actionMap.put("saveAndTest", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAndTestLevel();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0);
        inputMap.put(key, "tabImageForward");
        actionMap.put("tabImageForward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabSelectedObjectImage(true);
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0);
        inputMap.put(key, "tabImageBackward");
        actionMap.put("tabImageBackward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabSelectedObjectImage(false);
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_D, 0);
        inputMap.put(key, "tabImageForward");
        actionMap.put("tabImageForward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabSelectedObjectImage(true);
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_A, 0);
        inputMap.put(key, "tabImageBackward");
        actionMap.put("tabImageBackward", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabSelectedObjectImage(false);
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        inputMap.put(key, "delete");
        actionMap.put("delete", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedObject();
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "cut");
        actionMap.put("cut", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e){
                if (!cut()) {
                    System.err.println("cut() failed...");
                }
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "copy");
        actionMap.put("copy", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!copy()) {
                    System.err.println("copy() failed...");
                }
            }
        });
        
        key = KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK);
        inputMap.put(key, "paste");
        actionMap.put("paste", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paste()) {
                    System.err.println("paste() failed...");
                }
            }
        });
        
        Object[] rets = {inputMap, actionMap};
        return rets;
    }
}
