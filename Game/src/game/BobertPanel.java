package game;

import editor.LevelEditor;
import game.Collidable.CollisionType;
import game.WorldObject.WorldObjectType;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import rosslib.RossLib;

public class BobertPanel extends JPanel implements Runnable,
        KeyListener {

    //<editor-fold defaultstate="collapsed" desc="Class Variables">
    
    // Character vars.
    static Character bobert;
    static Camera screenCam;
    static ArrayList<String> gameLevels = new ArrayList<String>();
    static ArrayList<String> allGameLevels = new ArrayList<String>();
    static GameLevel level;
    static String levelDataPath = "resources/levels/level_order.xml";
    static boolean levelsDefined = false;
    static boolean shouldAdvanceOneLevel = false;
    static boolean loadingLevel = false;
    static Image loadingImage = new ImageIcon("resources/logos/blockTwo_loadScreen.jpg").getImage();
    private static boolean wonGame = false;
    static Image wonImage = new ImageIcon("resources/logos/blockTwo_3.jpg").getImage();
    
    // Animation vars
    static int animationFrame = 0;
    static int animationDelay = 25;
        
    // Projectile
    /*
     * Projectiles have been temporarily removed as a gameplay test. They might
     * make the game too easy. Gameplay might shift towards timing, running and
     * jumping, and avoidance of obstacles/enemies/spike pits. Sigh, so much work
     * lost. But also glad for the learning done while building this portion.
     * -R
     */
//    static ArrayList<Projectile> onScreenProjectiles;
//    static Projectile defaultProjectile;
//    static boolean shootingProjectile = false;
//    static boolean canShootProjectileBasedOnTimer = true;
//    static int projectileTimer = 0;
//    static int projectileTimerDelay = 200;
//    static int projectileFrame = 0;
//    static final int projectileDelay = 8;
    
    // Used for spacing in paintComponent(Graphics g) when displaying
    // text to the screen.
    static int debugTextHeight = 17;
    static boolean showDebugBoxes = false;
    static int consoleCommandTextHeight = 30;
    static boolean typingConsoleCommand = false;
    static String consoleCommand = "";
    
    // Important game vars. objectsDefined gets set to true once all of the
    // necessary obecjts have been initialized. gameRunning is a flag for the
    // main game thread (game loop, gameThread). bFrame is the frame (the window
    // that the game is inside of).
    static boolean objectsDefined = false;
    public static boolean gameRunning;
    public static Thread gameThread;
    public static final long FPSDelayPerFrame = 3;
    public static long FPSStartOfLoopTime = 0;
    public static BobertFrame bFrame;
    
    // Vars for the character being in the air/falling/jumping.
    static int inAirDelay = 6;
    static int inAirFrame = 0;
    static int gravity = 1;
    
    // Vars for the character moving left and right
    static int movementFrame = 0;
    static int movementDelay = 0;
    
    
    //</editor-fold>
    
    public BobertPanel(BobertFrame frame) {
        setBackground(new Color(200, 200, 200));
        this.setDoubleBuffered(true);
        // Need to assign the BobertFrame to a static variable so that we 
        // can add Listeners to it in addNotify(). Explanation is in addNotify().
        bFrame = frame;
        // Initialize game objects so that they have real values, goddamn it.
        defineObjects();
    }

    @Override
    public void addNotify() {
        // addNotify() is a function that we are implementing that is actually a
        // function from JComponent. Basically, this function is called by Java
        // to NOTIFY us that this JPanel (in our case, this BobertPanel) has been
        // added to a JFrame (in our case, a BobertFrame). Check inside of BobertFrame
        // and you'll the see spot where we add a bPanel to the frame. This function
        // basically lets us create our game thread (game loop) and then add all
        // of our Listeners to the bFrame (the window).
        // LISTENERS -- They do exactly what their levelName says. They sit around and
        // wait for important things to happen, e.g. KeyListener waits around until
        // a key is pressed or something, and then the KeyListener will call
        // keyPressed(KeyEvent e) for us. Then we deal with the keyPress however 
        // we want.
        // super.addNotify() just calls a higher-up, more general version of
        // addNotify. It seems like the right thing to do to call it.
        super.addNotify();
        gameThread = new Thread(this);
        gameThread.start();
        bFrame.addKeyListener(this);
        
    }

    public static void defineObjects() {
        
        
        if (!levelsDefined) {
            int numLevels = RossLib.parseXML(levelDataPath, "level");
            gameLevels.clear();
            for (int i = 0; i < numLevels; i++) {
                String levelName = (RossLib.parseXML(levelDataPath, "level", i, "name"));
                gameLevels.add(levelName);
                allGameLevels.add(levelName);
                System.out.println(gameLevels.get(i));
            }
            levelsDefined = true;
        }
        if (Main.curArgs == null) { // If this is regular game run
            if (gameLevels.size() > 0) {
                level = new GameLevel(gameLevels.get(0), false);
            } else {
                wonGame = true;
            }
        } else { // If this is a level editor test
            level = new GameLevel(Main.curArgs[0], false);
        }
        
        bobert = new Character();
        screenCam = new Camera(0, 0, Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        for (int i=0; i<level.collidables.size(); i++) {
            Collidable cur = level.collidables.get(i);
            if (cur.worldObjectType == WorldObjectType.TRIGGER) {
                if (cur.name.equalsIgnoreCase("start")) {
                    bobert.setX(cur.leftEdge());
                    bobert.setY(cur.topEdge());
                } else if (cur.name.equalsIgnoreCase("end")) {
                    screenCam.setX(cur.leftEdge());
                    screenCam.setY(cur.topEdge());
                }
            }
        }
        
        bobert.imagePaths = new ArrayList<String>();
        String amtImages = RossLib.parseXML(Character.resourcesPath + "character_data.xml", 
                "character", "Bobert", "numberOfImages");
        if (!amtImages.isEmpty()) {
            bobert.numImages = Integer.parseInt(amtImages); 
        }
        String defaultImage = RossLib.parseXML(Character.resourcesPath + "character_data.xml", 
                "character", "Bobert", "defaultImage");
        if (!defaultImage.isEmpty()) {
            bobert.imageCount = Integer.parseInt(defaultImage);
        }
        for (int i=0; i<bobert.numImages; i++) {
            bobert.imagePaths.add(Character.resourcesPath+i+".png");
        }
        
        // Initialize the lists of projectiles
//        onScreenProjectiles = new ArrayList<Projectile>();
//        defaultProjectile = new Projectile();
//        defaultProjectile.setX(bobert.hitBox.x);
//        defaultProjectile.setY(bobert.hitBox.y);
//        Projectile.numImages = RossLib.parseXML(Projectile.dataPath, "projectile");

        gameRunning = true;
        objectsDefined = true;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        
        if (objectsDefined) {
            
            if (!loadingLevel && !wonGame) {
                
            
            // **Draw background
            if (showDebugBoxes) {
                level.background.drawDebug(g2d, screenCam);
            } else {
                level.background.draw(g2d, screenCam);
            }

            // **Draw enemies
            for (int i=0; i<level.enemies.size();i++) {
                level.enemies.get(i).draw(g2d, screenCam);
                if (showDebugBoxes) 
                    level.enemies.get(i).drawDebug(g2d, screenCam);
            }

            // **Draw Projectile
//            if (shootingProjectile) {
//                for (int i = 0; i < onScreenProjectiles.size(); i++) {
//                    onScreenProjectiles.get(i).draw(g2d, screenCam);
//                    if (showDebugBoxes) 
//                        onScreenProjectiles.get(i).drawDebug(g2d, screenCam);
//                }
//            }
            
            // **Draw collidables
            for (int i=0; i<level.collidables.size(); i++) {
                if (!level.collidables.get(i).name.equalsIgnoreCase("start")) {
                    level.collidables.get(i).draw(g2d, screenCam);
                }
                if (showDebugBoxes) 
                    level.collidables.get(i).drawDebug(g2d, screenCam);
            }
            
            // **Draw bobert
            bobert.draw(g2d, screenCam);
            if (showDebugBoxes) {
                bobert.drawDebug(g2d, screenCam);
            }
            
            g2d.drawImage(Collidable.collectableIcon, 
                    Main.B_WINDOW_WIDTH - 200, 10,
                    80, 80,
                    null);
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 50));
            g2d.drawString("x "+(bobert.totalCollected+bobert.numCollected),
                    Main.B_WINDOW_WIDTH - 110, 90);
            
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, consoleCommandTextHeight));
            if (typingConsoleCommand) {
                g2d.drawLine((int) (Main.B_WINDOW_WIDTH *0.3 -2), 
                        (int) (Main.B_WINDOW_HEIGHT * 0.2) -consoleCommandTextHeight, 
                        (int) (Main.B_WINDOW_WIDTH * 0.3 -2), 
                        (int) (Main.B_WINDOW_HEIGHT * 0.2));
                g2d.drawString(consoleCommand, 
                        (int) (Main.B_WINDOW_WIDTH * 0.3), 
                        (int) (Main.B_WINDOW_HEIGHT * 0.2));
            }
            
            // **Draw the currently held projectile's levelName
            int fontSize = 20;
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
//            g2d.drawString("Holding: "+defaultProjectile.levelName, 50, Main.B_WINDOW_CANVAS_HEIGHT-fontSize);
            
            // **Debugging values on screen
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
//            g2d.drawString("Fruit Collected this Level: " + bobert.numCollected, 0, debugTextHeight * 2);
//            g2d.drawString("Total Fruit Collected: " + (bobert.numCollected+bobert.totalCollected), 0, debugTextHeight * 4);
//            g2d.drawString("bobert.horizAccelFrame: "+ bobert.horizAccelFrame, 0, debugTextHeight*3);
//            g2d.drawString("loadingLevel: "+ loadingLevel, 0, debugTextHeight*4);
//            g2d.drawString("level.enemies.get(0).hitBox.y:  " + level.enemies.get(0).hitBox.y, 0, debugTextHeight * 5);
//            g2d.drawString("level.enemies.get(0).isAlive:  "+ level.enemies.get(0).isAlive, 0, debugTextHeight*6);
//            g2d.drawString("level.enemies.get(0).isInViewOf(screenCam):     "+level.enemies.get(0).isInViewOf(screenCam), 0, debugTextHeight*7);
//            g2d.drawString("bobert.movingLeft: "+bobert.movingLeft, 0, debugTextHeight*8);
            } else if (loadingLevel && !wonGame) {
                // if loading the level
                g2d.drawImage(loadingImage,
                        0, 0,
                        Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT, null);

            } else if (wonGame) {
                // if the game is won
                g2d.drawImage(wonImage,
                        0, 0,
                        Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT, null);
            }
        }

    }

    @Override
    public void run() {

        while (gameRunning) {
            FPSStartOfLoop();

            // **Moving from level 1 to level 2, for example
            //<editor-fold defaultstate="collapsed" desc="Level Advancement">
            if (shouldAdvanceOneLevel) {
                loadingLevel = true;
                repaint();

                if (Main.curArgs == null) {
                    // If this is a regular game run, boot the next level.
                    shouldAdvanceOneLevel = false;
                    if (gameLevels.size() > 1) {
                        // If there is still a level to remove, then remove it.
                        gameLevels.remove(level.levelName);
                    } else {
                        // Otherwise, reload the level array
                        wonGame = true;
                        repaint();
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(BobertPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        gameLevels.clear();
                        wonGame = false;
                        for (int i=0; i < allGameLevels.size(); i++) {
                            gameLevels.add(allGameLevels.get(i));
                        }
                    }
                    level = new GameLevel(gameLevels.get(0), false);
                } else {
                    // if this is an editor test, end the level
                    gameRunning = false;
                    bFrame.dispose();
                }
                for (int i = 0; i < level.collidables.size(); i++) {
                    Collidable cur = level.collidables.get(i);
                    if (cur.worldObjectType == WorldObjectType.TRIGGER) {
                        if (cur.name.equalsIgnoreCase("start")) {
                            bobert.setX(cur.leftEdge());
                            bobert.setY(cur.topEdge());
                        } else if (cur.name.equalsIgnoreCase("end")) {
                            screenCam.setX(cur.leftEdge());
                            screenCam.setY(cur.topEdge());
                        }
                    }
                }
                bobert.totalCollected += bobert.numCollected;
                bobert.numCollected = 0;
                bobert.vertVelocity = 0;
                bobert.decelerateCompletely();
                bobert.hasJumped = false;
                bobert.hasDoubleJumped = false;
//                onScreenProjectiles.clear();
                loadingLevel = false;
            }
            //</editor-fold>
            
            // **Handles all characters which are in the air.
            //<editor-fold defaultstate="collapsed" desc="Bobert in Air">
            /*
             * Frame-Delay explanation: The exampleFrame variable always starts
             * at 0. exampleFrame's purpose is to count the number of times that
             * the "while(gameRunning)" loop has looped. The exampleDelay
             * variable is the number of times the "while(gameRunning)" loop
             * should loop before any movement or processing occuring. This
             * allows the game to happen at a bearable, normal pace.
             * Essentially, it allows us to set the speed at which movement (or
             * anything else) occurs by increasing or decreasing the number of
             * "skipped" frames that will pass by before a "real" or "movement"
             * frame happens. Every time a "real" or "movement" frame happens,
             * we set exampleFrame back to 0 because we need to know when
             * another exampleDelay frames have passed so that another "real"
             * frame can happen again, and so on.
             */
            if (inAirFrame >= inAirDelay) {
                // If the character is about to be inside/below the floor, then
                // we should set the character to be standing directly on top of
                // the floor, and set isInAir to false (because the character is
                // on the ground now.
                bobert.updateFutureHitBox();
                bobert.vertVelocity += gravity;
                bobert.futureHitBox.y += bobert.vertVelocity;
                for (int i = 0; i < level.collidables.size(); i++) {
                    Collidable cur = level.collidables.get(i);
                    if (bobert.willCollideWith(cur)) {
                        if (cur.collisionType == CollisionType.PLATFORM) {
                            if (bobert.vertVelocity < 0) {
                                // if bobert is moving upwards, he can't collide
                                // because you have to FALL onto a platform, bro.
                                continue;
                            } else {
                                bobert.isInAir = false;
                                bobert.hasJumped = false;
                                bobert.hasDoubleJumped = false;
                                bobert.setY(cur.hitBox.y - bobert.hitBox.height);
                                // Set the character's vertical velocity to 1 because we are
                                // on the ground, goddamn it, and we aren't jumping, goddamn it.
                                bobert.vertVelocity = 1;
                                break;
                            }
                        } else if (cur.collisionType == CollisionType.IMPASSABLE) {
                            if (bobert.isAbove(cur)) {
                                bobert.isInAir = false;
                                bobert.hasJumped = false;
                                bobert.hasDoubleJumped = false;
                                bobert.setY(cur.hitBox.y - bobert.hitBox.height);
                                bobert.vertVelocity = 1;
                                break;
                            } else {
                                bobert.vertVelocity = 1;
                                break;
                            }
                        } else if (cur.collisionType == CollisionType.PASSABLE) {
                            bobert.isInAir = true;
                            continue;
                        }
                    } else {
                        // If we aren't going to be on the ground right away, then
                        // we must be in the air. Set isInAir to true so that we can
                        // know whether or not we are jumping.
                        bobert.isInAir = true;
                    }
                }
                
                // If the character is in the air, add some gravity to our current
                // velocity, and then add the new velocity to the character's
                // height.
                if (bobert.isInAir) {
                    bobert.moveVerticallyBy(bobert.vertVelocity);
//                    defaultProjectile.moveVerticallyBy(bobert.vertVelocity);
                }
                //</editor-fold>
                
            //<editor-fold defaultstate="collapsed" desc="Enemies in Air">
                // DO ALL THIS AGAIN FOR EACH ENEMY
                // If the character is about to be inside/below the floor, then
                // we should set the character to be standing directly on top of
                // the floor, and set isInAir to false (because the character is
                // on the ground now.

                for (int i = 0; i < level.enemies.size(); i++) {
                    Enemy curEnemy = level.enemies.get(i);
                    curEnemy.updateFutureHitBox();
                    curEnemy.futureHitBox.y += curEnemy.vertVelocity;

                    for (int j = 0; j < level.collidables.size(); j++) {
                        Collidable cur = level.collidables.get(j);

                        if (curEnemy.willCollideWith(level.collidables.get(j))) {
                            if (cur.collisionType != CollisionType.PASSABLE) {
                                if (curEnemy.vertVelocity < 0) {
                                    continue;
                                } else {
                                    curEnemy.isInAir = false;
                                    curEnemy.setY(cur.hitBox.y - curEnemy.hitBox.height);
                                    // Set the character's vertical velocity to 0 because we are
                                    // on the ground, goddamn it, and we aren't jumping, goddamn it.
                                    curEnemy.vertVelocity = 1;
                                    break;
                                }
                            } else {
                                curEnemy.isInAir = true;
                                continue;
                            }
                            
                        } else {
                            // If we aren't going to be on the ground right away, then
                            // we must be in the air. Set isInAir to true so that we can
                            // know whether or not we are jumping.
                            curEnemy.isInAir = true;
                        }
                    }
                    // If the character is in the air, add some gravity to our current
                    // velocity, and then add the new velocity to the character's
                    // height.
                    if (curEnemy.isInAir) {
                        curEnemy.vertVelocity += gravity;
                        curEnemy.moveVerticallyBy(curEnemy.vertVelocity);
                    }
                    level.enemies.set(i, curEnemy);
                }

                // Set inAirFrame to 0 because we just finished processing a
                // "real" or "movement" frame, and we need to start counting all
                // the way up to inAirDelay again.
                inAirFrame = 0;
            } else {
                // We are skipping movement/processing this frame, so just add
                // 1 to inAirFrame and move on with our lives, like holy shit.
                inAirFrame++;
            }
            //</editor-fold>

            // **Handles all character movement.
            //<editor-fold defaultstate="collapsed" desc="Bobert Movement">

            // See the explanation of Frame-Delay up in the inAir section ^^
            if (movementFrame >= movementDelay) {
                // If the character is moving to the right (as set in the
                // keyPressed and keyReleased methods), then move it to the right.
                // Duh.
                if (bobert.leftEdge() > level.background.drawBox.x
                        && bobert.rightEdge() < level.background.drawBox.x + level.background.drawBox.width) {

                    // If the character is moving to the left (as set in the
                    // keyPressed and keyReleased methods), then move it to the left.
                    // Duh.
                    if (bobert.horizAccelFrame > Character.horizAccelDelay) {
                        if (bobert.shouldAccelLeft) {
                            bobert.accelerateLeft();
                        } else if (bobert.shouldAccelRight) {
                            bobert.accelerateRight();
                        } else {
                            bobert.decelerate();
                        }
                        bobert.horizAccelFrame = 0;
                    } else {
                        bobert.horizAccelFrame++;
                    }
                    boolean canMove = true;
                    bobert.updateFutureHitBox();
                    bobert.futureHitBox.x += bobert.horizVelocity;
                    if (bobert.movingRight || bobert.movingLeft) {
                        for (int i = 0; i < level.collidables.size(); i++) {
                            Collidable cur = level.collidables.get(i);
                            if (bobert.willCollideWith(cur)) {
                                if (cur.collisionType != CollisionType.PASSABLE) {
                                    canMove = false;
                                    break;
                                }
                            } else {
                                canMove = true;
                            }
                        }
                    }
                    if (canMove) {
                        if (bobert.horizVelocity > 0) {
                            if (bobert.futureHitBox.x + bobert.futureHitBox.width < level.background.drawBox.x + level.background.drawBox.width) {
                                bobert.moveRight();
                            }
                        } else if (bobert.horizVelocity < 0) {
                            if (bobert.futureHitBox.x > level.background.drawBox.x) {
                                bobert.moveLeft();
                            }
                        }
                    } else {
                        bobert.decelerateCompletely();
                    }
                    
                    if (bobert.leftEdge() < level.background.drawBox.x) {
                        bobert.setX(level.background.drawBox.x + bobert.getWidth());
                    } else if (bobert.rightEdge() > level.background.drawBox.x + level.background.drawBox.width) {
                        bobert.setX(level.background.drawBox.x + level.background.drawBox.width - bobert.getWidth());
                    }

                }
                // The default projectile needs to be positioned in the same
                // spot as the character!
//                defaultProjectile.setX(bobert.hitBox.x);
//                defaultProjectile.setY(bobert.hitBox.y);
                
                //</editor-fold>

            //<editor-fold defaultstate="collapsed" desc="Enemies Movement">
                //**Enemy movement
                for (int i = 0; i < level.enemies.size(); i++) {
                    Enemy curEnemy = level.enemies.get(i);
                    if (curEnemy.isAlive != true) {
                        level.enemies.remove(i);
                        i--;
                        break;
                    }
                    
                    // Randomly decide if the enemy should turn around or not.
                    // If movement distance is 0, DON'T MOVE. This is important
                    // so that stationary enemies can exist (spikes, lava pits, etc)
                    if (curEnemy.movementDistance > 0) {
                        if (curEnemy.hitBox.x > curEnemy.startX + curEnemy.movementDistance
                                || curEnemy.hitBox.x < curEnemy.startX) {
                            curEnemy.switchHorizontalDirection();
                        }
                    } else if (curEnemy.movementDistance < 0) {
                        if (curEnemy.hitBox.x < curEnemy.startX + curEnemy.movementDistance
                                || curEnemy.hitBox.x > curEnemy.startX) {
                            curEnemy.switchHorizontalDirection();
                        }
                    }
                    
                    // If there is distance to be moved, then move. Otherwise
                    // stay put.
                    if (Math.abs(curEnemy.movementDistance) > 0) {
                        if (curEnemy.movingRight) {
                            curEnemy.moveRightBy(curEnemy.horizVelocity);
                        } else if (curEnemy.movingLeft) {
                            curEnemy.moveLeftBy(curEnemy.horizVelocity);
                        }
                    }
                    level.enemies.set(i, curEnemy);
                }
                
                // Set frame to 0 to reset counter, blah blah, see explanation in
                // inAir
                movementFrame = 0;
            } else {
                // Increase skipped frame counter
                movementFrame++;
            }
            //</editor-fold>

            // **Handles all animation
            //<editor-fold defaultstate="collapsed" desc="Animation">
            
            // Bobert and standard animation
            if (animationFrame >= animationDelay) {
                // Right-facing images: 0-15
                // Left-facing images: 16-30
                if (bobert.movingRight || bobert.movingLeft) {
                    bobert.imageCount++;
                } else if (bobert.facingRight || bobert.facingLeft) {
                    bobert.imageCount = 12;
                }
                if (bobert.imageCount >= bobert.imagePaths.size()) {
                    bobert.imageCount = 0;
                }
                bobert.setImage(bobert.imagePaths.get(bobert.imageCount));
                
                animationFrame = 0;
            } else {
                animationFrame++;
            }
            
            // Collectable animation
            if (Collidable.collectableAnimationFrame >= Collidable.collectableAnimationDelay) {
                for (int i=0; i < level.collidables.size(); i++) {
                    Collidable cur = level.collidables.get(i);
                    cur.imageCount++;
                    if (cur.imagePaths != null) {
                        // If this is an animated collidable
                        if (cur.imageCount >= cur.numImages) {
                            cur.imageCount = 0;
                        }
                        cur.setImage(cur.imagePaths.get(cur.imageCount));
                    } else {
                        /* If this is a static collidable,
                         * do nothing, don't change the image :)
                         */
                    }
                }
                Collidable.collectableAnimationFrame = 0;
            } else {
                Collidable.collectableAnimationFrame++;
            }
            
            
            //</editor-fold>

            // **Handles all onScreenProjectiles, as well as collision detection for each
            // projectile.
            //<editor-fold defaultstate="collapsed" desc="Projectiles">
            // shootingProjectile is set to true in keyPressed
            // and is set back to false in "if(projectileDestroyed)"
//            if (shootingProjectile) {
//                // See explanation of Frame-Delay up at the inAir block ^^
//                if (projectileFrame >= projectileDelay) {
//
//                    // **Vertical Movement
//                    // If the space it will move into is in the floor,
//                    // set the projectile to sit just above the floor.
//                    // Otherwise, add gravity to velocity and then velocity to
//                    // the projectile's y value normally.
//
//                    for (int i = 0; i < onScreenProjectiles.size(); i++) {
//                        final Projectile curProjectile = onScreenProjectiles.get(i);
//
//                        for (int j = 0; j < level.collidables.size(); j++) {
//                            Collidable curCollidable = level.collidables.get(j);
//                            curProjectile.isInAir = false;
//                            curProjectile.updateFutureHitBox();
//                            curProjectile.futureHitBox.y += curProjectile.vertVelocity;
//                            if (curProjectile.futureHitBox.intersects(curCollidable.hitBox)) {
//                                if (curProjectile.isAbove(curCollidable)) {
//                                    curProjectile.setY(curCollidable.hitBox.y - curProjectile.hitBox.height-5);
//                                    curProjectile.vertVelocity = Projectile.vertVelocityBounce;
//                                    curProjectile.numBounces++;
//                                    curProjectile.isInAir = false;
//                                    break;
//                                }
//                            } else {
//                                curProjectile.isInAir = true;
//                            }
//                            
//                        }
//
//                        // **Horizontal Movement
//                        // Move the projectile in the direction it's moving.
//                        // If the character was moving at the time it shot the projectile,
//                        // move the projectile twice as fast.
//                        
//                        
//                        for (int j = 0; j < level.enemies.size(); j++) {
//                            curProjectile.updateFutureHitBox();
//                            if (curProjectile.willCollideWith(level.enemies.get(j))) {
//                                curProjectile.setImage(Projectile.resourcesPath + "explosion.png");
//                                level.enemies.get(j).isAlive = false;
//
//                                final Timer destroyTimer = new Timer(curProjectile.toString());
//                                destroyTimer.schedule(new TimerTask() {
//
//                                    final Projectile proj = curProjectile;
//
//                                    @Override
//                                    public void run() {
//                                        proj.destroyed = true;
//                                        destroyTimer.cancel();
//                                    }
//                                }, 100);
//
//                                break;
//                            }
//                        }
//                        
//                        if (curProjectile.numBounces > Projectile.numBouncesAllowed) {
//                            curProjectile.setImage(Projectile.resourcesPath + "poof.png");
//                            
//                            final Timer poofTimer = new Timer(curProjectile.toString());
//                            poofTimer.schedule(new TimerTask() {
//                                
//                                final Projectile proj = curProjectile;
//                                
//                                @Override
//                                public void run() {
//                                    proj.destroyed = true;
//                                    poofTimer.cancel();
//                                }
//                            }, 100);
//                        }
//
//                        if (curProjectile.isInAir) {
//                            for (int j = 0; j < level.collidables.size(); j++) {
//                                Collidable curCollidable = level.collidables.get(j);
//                                if (curProjectile.movingRight) {
//                                    if (curCollidable.hitBox.intersectsLine(curProjectile.rightEdge(), curProjectile.topEdge(),
//                                            curProjectile.rightEdge(), curProjectile.bottomEdge())) {
//                                        curProjectile.switchHorizontalDirection();
//                                        curProjectile.numBounces++;
//                                        break;
//                                    }
//                                } else if (curProjectile.movingLeft) {
//                                    if (curCollidable.hitBox.intersectsLine(curProjectile.leftEdge(), curProjectile.topEdge(),
//                                            curProjectile.leftEdge(), curProjectile.bottomEdge())) {
//                                        curProjectile.switchHorizontalDirection();
//                                        curProjectile.numBounces++;
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                        
//                        // Bounds check to see if the projectile is off the screen
//                            if (curProjectile.hitBox.x + curProjectile.hitBox.width < level.background.drawBox.x
//                                    || curProjectile.hitBox.x > level.background.drawBox.x + level.background.drawBox.width) {
//
//                                curProjectile.destroyed = true;
//                            }
//                        
//                        if (curProjectile.isInAir) {
//                            curProjectile.vertVelocity += gravity;
//                            curProjectile.moveVerticallyBy(curProjectile.vertVelocity);
//                        }
//                        
//                        if (curProjectile.movingRight) {
//                            curProjectile.moveRightBy(curProjectile.horizVelocity);
//                            // wasMovingRight is set to true in keyPressed and is set
//                            // back to false in keyReleased (if the projectile isn't
//                            // being shot), and is set back to false in projectileDestroyed
//                            // as well.
//                            if (curProjectile.movingQuickerSpeed) {
//                                curProjectile.moveRightBy((int) (curProjectile.horizVelocity * 0.5));
//                            }
//                        }
//                        // If is moving left
//                        if (curProjectile.movingLeft) {
//                            curProjectile.moveLeftBy(curProjectile.horizVelocity);
//                            // See wasMovingRight for explanation
//                            if (curProjectile.movingQuickerSpeed) {
//                                curProjectile.moveLeftBy((int) (curProjectile.horizVelocity * 0.5));
//                            }
//                        }
//                        if (curProjectile.destroyed) {
//                            onScreenProjectiles.remove(i);
//                            i--;
//                            if (onScreenProjectiles.isEmpty()) {
//                                shootingProjectile = false;
//                            }
//                        } else {
//                            onScreenProjectiles.set(i, curProjectile);
//                        }
//                    }
//                    // Reset frame
//                    projectileFrame = 0;
//                } else {
//                    // Increment frame counter
//                    projectileFrame++;
//                }
//            }
//            
//                    
//                    
//            // Increment projectile shooting timer
//            // TODO modify this timer so that it will keep one keypress in memory
//            // i.e. if you press it and the timer is still active, it will fire
//            // once the timer runs all the way, then stop.
//            // If statement is to protect against data overflows
//            if (projectileTimer < projectileTimerDelay) {
//                projectileTimer++;
//            }
            //</editor-fold>
            
            // **Handles the camera movement around bobert
            //            //<editor-fold defaultstate="collapsed" desc="Camera">
            
            if ( (screenCam.getX() + screenCam.getWidth() < level.background.drawBox.width
                    && bobert.xPositionInCam(screenCam) > screenCam.getWidth() * 0.5)
                                ||
                    (screenCam.getX() < level.background.drawBox.x) ) {
                screenCam.moveRightBy(3 * Camera.scrollFactor);
            }
            
            if ( (screenCam.getX() > level.background.drawBox.x
                    && bobert.xPositionInCam(screenCam) < screenCam.getWidth() * 0.5)
                                ||
                    (screenCam.getX() + screenCam.getWidth() > level.background.drawBox.x + level.background.drawBox.width) ){
                screenCam.moveLeftBy(3 * Camera.scrollFactor);
            }
            
            if ( (screenCam.getY() + screenCam.getHeight() <= level.background.drawBox.y + level.background.drawBox.height
                    && bobert.yPositionInCam(screenCam) > (screenCam.getHeight()*0.5))
                                ||
                    (screenCam.getY() < level.background.drawBox.y) ) {
                screenCam.moveVerticallyBy(3);
            }
            
            if ( (screenCam.getY() > level.background.drawBox.y 
                    && bobert.yPositionInCam(screenCam) < (screenCam.getHeight()*0.5))
                                ||
                    (screenCam.getY() + screenCam.getHeight() > level.background.drawBox.y + level.background.getHeight()) ) {
                screenCam.moveVerticallyBy(-3);
            }
            
                //</editor-fold>
            
            // **Bobert's collision with the enemies
            //<editor-fold defaultstate="collapsed" desc="Character Death">
            for (int i = 0; i < level.enemies.size(); i++) {
                if (bobert.isCollidingWith(level.enemies.get(i))) {
                    level = new GameLevel(level.levelName, false);
                    for (int j = 0; j < level.collidables.size(); j++) {
                        Collidable cur = level.collidables.get(j);
                        if (cur.worldObjectType == WorldObjectType.TRIGGER) {
                            if (cur.name.equalsIgnoreCase("start")) {
                                loadingLevel = true;
                                repaint();
                                bobert.setX(cur.leftEdge());
                                bobert.setY(cur.topEdge());
                                break;
                            }
                        }
                    }
                    bobert.numCollected = 0;
                    bobert.vertVelocity = 0;
                    bobert.decelerateCompletely();
                    bobert.horizAccelFrame = 0;
                    bobert.hasJumped = false;
                    bobert.hasDoubleJumped = false;
//                    onScreenProjectiles.clear();
                    loadingLevel = false;
                    break;
                }
            }
            //</editor-fold>
            
            // **Handles collision with triggers and the actions associated with them
            //<editor-fold defaultstate="collapsed" desc="Triggers and Collectables">
            for (int i=0; i<level.collidables.size(); i++) {
                Collidable cur = level.collidables.get(i);
                if (bobert.isCollidingWith(cur)) {
                    if (cur.worldObjectType == WorldObjectType.TRIGGER) {
                        String action = cur.name;
                        if (action.equalsIgnoreCase("end")) {
                            shouldAdvanceOneLevel = true;
                        } else if (action.equalsIgnoreCase("play")) {
                            shouldAdvanceOneLevel = true;
                        } else if (action.equalsIgnoreCase("editor")) {
                            BobertPanel.gameRunning = false;
                            LevelEditor.main(null);
                        } else if (action.contains("audio")) {
                            level.collidables.remove(i);
                            i--;
                            String audioName = action.substring(5).trim().toLowerCase();
                            if (audioName.equalsIgnoreCase("Narration1")) {
                                Audio.NARRATION1.play(Volume.MEDIUM_HIGH);
                            } else {
                                new SoundEffect(audioName).play(Volume.LOW_MEDIUM);
                            }
                        }
                    } else if (cur.worldObjectType == WorldObjectType.COLLECTABLE) {
                        level.collidables.remove(i);
                        i--;
                        bobert.numCollected++;
                        new SoundEffect("coin_pickup.wav").play(Volume.LOW_MEDIUM);
                    }
                }
            }
            //</editor-fold>

            // Keeps the frame rate constant by delaying the game loop
            FPSEndOfLoop();

            // Redraw the screen. This bascally calls on our paintComponent(Graphics g) 
            // method that we wrote.
            repaint();
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

    @Override
    public void keyTyped(KeyEvent e) {
        // This method is unnecessary for the purposes of this game.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (!typingConsoleCommand) {
            if (key == bobert.keyLeft || key == KeyEvent.VK_LEFT) {
                bobert.movingLeft = true;
                bobert.facingLeft = true;
                bobert.facingRight = false;
                bobert.shouldAccelLeft = true;
                bobert.horizAccelFrame = Character.horizAccelDelay -1;
                
            }
            if (key == bobert.keyRight || key == KeyEvent.VK_RIGHT) {
                bobert.movingRight = true;
                bobert.facingLeft = false;
                bobert.facingRight = true;
                bobert.shouldAccelRight = true;
                bobert.horizAccelFrame = Character.horizAccelDelay -1;
            }
            if (key == bobert.keyJump || key == KeyEvent.VK_UP) {
                // Make sure we aren't already jumping, then set isInAir to true
                // as a flag that we are jumping, and then set the vertical 
                // velocity to jumping.
                if (!bobert.hasJumped) {
                    new SoundEffect("Jumping.wav").play(Volume.LOW_MEDIUM);
                    bobert.isInAir = true;
                    bobert.hasJumped = true;
                    bobert.hasDoubleJumped = false;
                    bobert.vertVelocity = Character.vertVelocityJump;
                } else if (!bobert.hasDoubleJumped) {
                    new SoundEffect("Jumping.wav").play(Volume.LOW_MEDIUM);
                    bobert.isInAir = true;
                    bobert.hasJumped = true;
                    bobert.hasDoubleJumped = true;
                    bobert.vertVelocity = Character.vertVelocityDoubleJump;
                }
            }
//            if (key == bobert.keyShoot) {
//                // Make sure we aren't already shooting the projetile, then set
//                // shootingProjectile to true and set the projectile velocity
//                // to bouncing.
//                if (projectileTimer >= projectileTimerDelay) {
//                    projectileTimer = 0;
//                    Projectile newProjectile = defaultProjectile;
//                    newProjectile.destroyed = false;
//                    if (bobert.facingRight) {
//                        newProjectile.movingRight = true;
//                        if (bobert.movingRight) {
//                            newProjectile.movingQuickerSpeed = true;
//                        }
//                    } else {
//                        newProjectile.movingLeft = true;
//                        if (bobert.movingLeft) {
//                            newProjectile.movingQuickerSpeed = true;
//                        }
//                    }
//                    onScreenProjectiles.add(newProjectile);
//                    defaultProjectile = new Projectile();
//                    defaultProjectile.setX(bobert.hitBox.x - level.floor.hitBox.x);
//                    defaultProjectile.setY(bobert.hitBox.y);
//                }
//                shootingProjectile = true;
//            } 
            // Dev commands, debug commands
            if (key == KeyEvent.VK_ENTER) {
                consoleCommand = ""; // Clear the input for the command
                typingConsoleCommand = true; // Start grabbing characters
            }
        } else {
            // Dev/debug commands
            if (key == KeyEvent.VK_ENTER) {
                // deal with the console command, then clear it.
                if (consoleCommand.isEmpty()) return; // don't deal with it if it's empty
                // String to hold the second word in a command, 
                // i.e. hold the "enemy" in "add enemy"
                String cmdRemaining = consoleCommand; 
                
                if (consoleCommand.equalsIgnoreCase("reset") || consoleCommand.equalsIgnoreCase("r")) {
                    // reset the current level, reloading everything from the XML files
                    objectsDefined = false;
                    BobertPanel.defineObjects();
                } else if (consoleCommand.length() >= 3) { // if it has a command
                    if (consoleCommand.substring(0, 4).equalsIgnoreCase("save")) {
                        cmdRemaining = cmdRemaining.substring(5);
                        // save stuff
                        if (cmdRemaining.substring(0, 5).equalsIgnoreCase("level")) {
                            cmdRemaining = cmdRemaining.substring(6);
                        }
                    } else if (consoleCommand.substring(0, 6).equalsIgnoreCase("toggle")) { 
                        // toggle booleans and stuff
                        cmdRemaining = cmdRemaining.substring(7);
                        if (cmdRemaining.equalsIgnoreCase("debug")) {
                            if (!showDebugBoxes) {
                                showDebugBoxes = true;
                            } else {
                                showDebugBoxes = false;
                            }
                        }
                    }
                      else if (consoleCommand.substring(0, 3).equalsIgnoreCase("add")) {
                        // add stuff
                        cmdRemaining = cmdRemaining.substring(4);
                        if (cmdRemaining.equalsIgnoreCase("enemy")) {
                            // add an enemy
                            level.enemies.add(new Enemy(new Rectangle(0,0,100,100), Enemy.defaultImgPath, Enemy.defaultMovementDistance));
                        }
                    } else if (consoleCommand.substring(0, 7).equalsIgnoreCase("restart")) {
                        // restart stuff
                        cmdRemaining = cmdRemaining.substring(8);
                        if (cmdRemaining.equalsIgnoreCase("music")) {
                            // restart the music
                        }
                    } else if (consoleCommand.substring(0, 4).equalsIgnoreCase("move")) {
                        // move stuff
                        cmdRemaining = cmdRemaining.substring(5);
                        if (cmdRemaining.substring(0, 9).equalsIgnoreCase("platforms")) {
                            cmdRemaining = cmdRemaining.substring(10);
                            if (cmdRemaining.substring(0, 4).equalsIgnoreCase("left")) {
                                cmdRemaining = cmdRemaining.substring(5);
                                for (int i=0; i<level.collidables.size(); i++) {
                                    if (level.collidables.get(i).collisionType == CollisionType.PLATFORM) {
                                        level.collidables.get(i).moveLeftBy(Integer.parseInt(cmdRemaining));
                                    }
                                }
                            } else if (cmdRemaining.substring(0, 5).equalsIgnoreCase("right")) {
                                cmdRemaining = cmdRemaining.substring(6);
                                for (int i=0; i<level.collidables.size(); i++) {
                                    if (level.collidables.get(i).collisionType == CollisionType.PLATFORM) {
                                        level.collidables.get(i).moveRightBy(Integer.parseInt(cmdRemaining));
                                    }
                                }
                            }
                        }
                    }
                }
                typingConsoleCommand = false; // We're done typing.
            } else if (key == KeyEvent.VK_BACK_SPACE) {
                // backspace: delete a character
                consoleCommand = consoleCommand.substring(0, consoleCommand.length()-1);
            } else {
                // if it isn't a key that indicates an actoin, 
                // just add the letter to the String
                consoleCommand += e.getKeyChar();
            }
        } // endif !typingConsoleCommand
    } //endif keyPressed

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == bobert.keyLeft) {
            bobert.movingLeft = false;
            bobert.shouldAccelLeft = false;
            bobert.decelerateCompletely();
        }
        if (key == bobert.keyRight) {
            bobert.movingRight = false;
            bobert.shouldAccelRight = false;
            bobert.decelerateCompletely();
        }
    }
    
}
