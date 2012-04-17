package game;

import game.Collidable.CollisionType;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rosslib.RossLib;

public class BobertPanel extends JPanel implements Runnable,
        KeyListener {

    //<editor-fold defaultstate="collapsed" desc="Class Variables">
    
    // Character vars.
    static Character bobert;
    static Camera screenCam;
    static GameLevel level;
    
    // Animation vars
    static int animationFrame = 0;
    static int animationDelay = 25;
        
    // Projectile
    static ArrayList<Projectile> onScreenProjectiles;
    static Projectile defaultProjectile;
    static boolean shootingProjectile = false;
    static boolean canShootProjectileBasedOnTimer = true;
    static int projectileTimer = 0;
    static int projectileTimerDelay = 200;
    static int projectileFrame = 0;
    static final int projectileDelay = 8;
    
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
        
        screenCam = new Camera(0, 0,
                Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        
        level = new GameLevel("menu", screenCam);
        
        bobert = new Character();
        bobert.imagePaths = new ArrayList<String>();
        String amtImages = RossLib.parseXML(Character.resourcesPath + "character_data.xml", 
                "character", "Bobert", "numberOfImages");
        if (!amtImages.isEmpty()) {
            Character.numImages = Integer.parseInt(amtImages); 
        }
        String defaultImage = RossLib.parseXML(Character.resourcesPath + "character_data.xml", 
                "character", "Bobert", "defaultImage");
        if (!defaultImage.isEmpty()) {
            Character.imageCount = Integer.parseInt(defaultImage);
        }
        for (int i=0; i<Character.numImages; i++) {
            bobert.imagePaths.add(Character.resourcesPath+i+".png");
        }
        
        // Initialize the lists of projectiles
        onScreenProjectiles = new ArrayList<Projectile>();
        defaultProjectile = new Projectile();
        defaultProjectile.setX(bobert.hitBox.x);
        defaultProjectile.setY(bobert.hitBox.y);
        Projectile.numImages = RossLib.parseXML(Projectile.dataPath, "projectile");

        gameRunning = true;
        objectsDefined = true;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        
        if (objectsDefined) {

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
            if (shootingProjectile) {
                for (int i = 0; i < onScreenProjectiles.size(); i++) {
                    onScreenProjectiles.get(i).draw(g2d, screenCam);
                    if (showDebugBoxes) 
                        onScreenProjectiles.get(i).drawDebug(g2d, screenCam);
                }
            }
            // **Draw collidables
            for (int i=0; i<level.collidables.size(); i++) {
                level.collidables.get(i).draw(g2d, screenCam);
                if (showDebugBoxes) 
                    level.collidables.get(i).drawDebug(g2d, screenCam);
            }
            
            // **Draw bobert
            bobert.draw(g2d, screenCam);
            if (showDebugBoxes) 
                bobert.drawDebug(g2d, screenCam);
            
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
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
//            g2d.drawString("onScreenProjectiles.size(): " + onScreenProjectiles.size(), 0, debugTextHeight * 1);
//            g2d.drawString("shootingProjectile: " + shootingProjectile, 0, debugTextHeight * 2);
//            g2d.drawString("projectileTimer: "+ projectileTimer, 0, debugTextHeight*3);
//            g2d.drawString("projectileTimerDelay: "+ this.projectileTimerDelay, 0, debugTextHeight*4);
//            g2d.drawString("defaultProjectile.hitBox.x:  " + defaultProjectile.hitBox.x, 0, debugTextHeight * 5);
//            g2d.drawString("bobert.isAbove(collidables.get(i):  "+ bobert.isAbove(collidables.get(1)), 0, debugTextHeight*6);
//            g2d.drawString("bobert.movingRight:     "+bobert.movingRight, 0, debugTextHeight*7);
//            g2d.drawString("bobert.movingLeft: "+bobert.movingLeft, 0, debugTextHeight*8);
        }

    }

    @Override
    public void run() {

        while (gameRunning) {
            FPSStartOfLoop();
            
            /* TODO handle collision detection better. Update his future hit box
             * once at the beginning of the game loop (here), and then keep udpating
             * it throughout the loop. If, at the end, it's an invalid place, move
             * him back to where he was BUT KEEP APPLYING GRAVITY.
             */
            
            // **Handles all characters which are in the air.
            //<editor-fold defaultstate="collapsed" desc="In Air">
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
                bobert.futureHitBox.y += bobert.vertVelocity;
                for (int i = 0; i < level.collidables.size(); i++) {
                    if (bobert.willCollideWith(level.collidables.get(i))) {
                        if (level.collidables.get(i).collisionType == CollisionType.PLATFORM) {
                            if (bobert.vertVelocity < 0) {
                                // if bobert is moving upwards, he can't collide
                                // because you have to FALL onto a platform, bro.
                                break; 
                            }
                        }
                        bobert.isInAir = false;
                        bobert.hasDoubleJumped = false;
                        bobert.setY(level.collidables.get(i).drawBox.y - bobert.drawBox.height);
                        defaultProjectile.setY(level.collidables.get(i).drawBox.y - bobert.drawBox.height);
                        // Set the character's vertical velocity to 0 because we are
                        // on the ground, goddamn it, and we aren't jumping, goddamn it.
                        bobert.vertVelocity = 1;
                        break;
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
                    bobert.vertVelocity += gravity;
                    bobert.moveVerticallyBy(bobert.vertVelocity);
                    // defaultProjectile.moveVerticallyBy(bobert.vertVelocity);
                }
                
                // DO ALL THIS AGAIN FOR EACH ENEMY
                // If the character is about to be inside/below the floor, then
                // we should set the character to be standing directly on top of
                // the floor, and set isInAir to false (because the character is
                // on the ground now.
                for (int i = 0; i < level.enemies.size(); i++) {
                    for (int j = 0; j < level.collidables.size(); j++) {
                        Enemy currentEnemy = level.enemies.get(i);
                        currentEnemy.futureHitBox.y += currentEnemy.vertVelocity;
                        if (currentEnemy.willCollideWith(level.collidables.get(j))) {
                            currentEnemy.isInAir = false;
                            currentEnemy.setY(level.collidables.get(j).hitBox.y - currentEnemy.hitBox.height);
                            // Set the character's vertical velocity to 0 because we are
                            // on the ground, goddamn it, and we aren't jumping, goddamn it.
                            currentEnemy.vertVelocity = 2;
                            break;
                        } else {
                            // If we aren't going to be on the ground right away, then
                            // we must be in the air. Set isInAir to true so that we can
                            // know whether or not we are jumping.
                            currentEnemy.isInAir = true;
                        }
                        // If the character is in the air, add some gravity to our current
                        // velocity, and then add the new velocity to the character's
                        // height.
                        if (currentEnemy.isInAir) {
                            currentEnemy.vertVelocity += gravity;
                            currentEnemy.moveVerticallyBy(currentEnemy.vertVelocity);
                        }
                        level.enemies.set(i, currentEnemy);
                    }
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
            //<editor-fold defaultstate="collapsed" desc="Movement">
            
            // See the explanation of Frame-Delay up in the inAir section ^^
            if (movementFrame >= movementDelay) {
                // If the character is moving to the right (as set in the
                // keyPressed and keyReleased methods), then move it to the right.
                // Duh.
                if (bobert.hitBox.x > 0
                        && bobert.hitBox.x + bobert.hitBox.width < level.background.drawBox.width) {
                    if (bobert.movingRight) {
                        bobert.moveRightBy(bobert.moveSpeed);
                    }
                    // If the character is moving to the left (as set in the
                    // keyPressed and keyReleased methods), then move it to the left.
                    // Duh.
                    if (bobert.movingLeft) {
                        bobert.moveLeftBy(bobert.moveSpeed);
                    }
                } else {
                    if (bobert.hitBox.x <= 0) {
                        bobert.moveRightBy(bobert.moveSpeed);
                    } else {
                        bobert.moveLeftBy(bobert.moveSpeed);
                    }
                }
                // The default projectile needs to be positioned in the same
                // spot as the character!
                defaultProjectile.setX(bobert.hitBox.x);
                defaultProjectile.setY(bobert.hitBox.y);

                //**Enemy movement
                for (int i = 0; i < level.enemies.size(); i++) {
                    Enemy currentEnemy = level.enemies.get(i);
                    if (currentEnemy.alive != true) {
                        level.enemies.remove(i);
                        i--;
                        break;
                    }
                    
                    // Randomly decide if the enemy should turn around or not.
                    int changePos = (int) ((Math.random() * 1000));
                    if (changePos == 1) {
                        currentEnemy.movingRight = true;
                        currentEnemy.movingLeft = false;
                    }
                    if (changePos == 2) {
                        currentEnemy.movingRight = false;
                        currentEnemy.movingLeft = true;
                    }
                    if (currentEnemy.movingRight) {
                        if (currentEnemy.hitBox.x+currentEnemy.hitBox.width < level.floor.hitBox.width) {
                            currentEnemy.moveRightBy(currentEnemy.moveSpeed);
                        } else {
                            currentEnemy.movingRight = false;
                            currentEnemy.movingLeft = true;
                        }
                    }
                    if (currentEnemy.movingLeft) {
                        if (currentEnemy.hitBox.x > 0) {
                            currentEnemy.moveLeftBy(currentEnemy.moveSpeed);
                        } else {
                            currentEnemy.movingRight = true;
                            currentEnemy.movingLeft = false;
                        }
                    }
                    level.enemies.set(i, currentEnemy);
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
            if (animationFrame >= animationDelay) {
                // Right-facing images: 0-15
                // Left-facing images: 16-30
                if (bobert.movingRight || bobert.movingLeft) {
                    Character.imageCount++;
                } else if (bobert.facingRight || bobert.facingLeft) {
                    Character.imageCount = 12;
                }
                if (Character.imageCount >= bobert.imagePaths.size()) {
                    Character.imageCount = 0;
                }
                bobert.setImage(bobert.imagePaths.get(Character.imageCount));
                
                animationFrame = 0;
            } else {
                animationFrame++;
            }
            
            //</editor-fold>

            // **Handles all onScreenProjectiles, as well as collision detection for each
            // projectile.
            //<editor-fold defaultstate="collapsed" desc="Projectiles">
            // shootingProjectile is set to true in keyPressed
            // and is set back to false in "if(projectileDestroyed)"
            if (shootingProjectile) {
                // See explanation of Frame-Delay up at the inAir block ^^
                if (projectileFrame >= projectileDelay) {

                    // **Horizontal Movement
                    // Move the projectile in the direction it's moving.
                    // If the character was moving at the time it shot the projectile,
                    // move the projectile twice as fast.
                    for (int i = 0; i < onScreenProjectiles.size(); i++) {
                        final Projectile currentProjectile = onScreenProjectiles.get(i);
                        if (currentProjectile.movingRight) {
                            currentProjectile.moveRightBy(currentProjectile.moveSpeed);
                            // wasMovingRight is set to true in keyPressed and is set
                            // back to false in keyReleased (if the projectile isn't
                            // being shot), and is set back to false in projectileDestroyed
                            // as well.
                            if (currentProjectile.movingQuickerSpeed) {
                                currentProjectile.moveRightBy((int)(currentProjectile.moveSpeed*0.5));
                            }
                        }
                        // If is moving left
                        if (!currentProjectile.movingRight) {
                            currentProjectile.moveLeftBy(currentProjectile.moveSpeed);
                            // See wasMovingRight for explanation
                            if (currentProjectile.movingQuickerSpeed) {
                                currentProjectile.moveLeftBy((int)(currentProjectile.moveSpeed*0.5));
                            }
                        }
                        // **Vertical Movement
                        // If the space it will move into is in the floor,
                        // set the projectile to sit just above the floor.
                        // Otherwise, add gravity to velocity and then velocity to
                        // the projectile's y value normally.
                        if (currentProjectile.isCollidingWith(level.floor)) {
                            currentProjectile.setY(level.floor.hitBox.y - currentProjectile.hitBox.height);
                            currentProjectile.vertVelocity = Projectile.vertVelocityBounce;
                        } else {
                            currentProjectile.vertVelocity += gravity;
                            currentProjectile.moveVerticallyBy(currentProjectile.vertVelocity);
                        }
                        // Bounds check to see if the projectile is off the screen
                        if (currentProjectile.hitBox.x+currentProjectile.hitBox.width < level.floor.hitBox.x 
                                || currentProjectile.hitBox.x > level.floor.hitBox.width) {
                            
                            currentProjectile.destroyed = true;
                        }
                        for (int j=0; j<level.enemies.size(); j++) {
                            if (currentProjectile.isCollidingWith(level.enemies.get(j))) {
                                final Enemy enem = level.enemies.get(j);
                                currentProjectile.setImage(Projectile.resourcesPath+"explosion.png");
                                
                                final Timer destroyTimer = new Timer(currentProjectile.toString());
                                destroyTimer.schedule(new TimerTask() {
                                    final Projectile proj = currentProjectile;
                                    @Override
                                    public void run() {
                                        enem.alive = false;
                                        proj.destroyed = true;
                                        destroyTimer.cancel();
                                    }
                                }
                                , 200);
                                
                                break;
                            }
                        }

                        if (currentProjectile.destroyed) {
                            onScreenProjectiles.remove(i);
                            i--;
                            if (onScreenProjectiles.isEmpty()) {
                                shootingProjectile = false;
                            }
                        } else {
                            onScreenProjectiles.set(i, currentProjectile);
                        }
                    }
                    // Reset frame
                    projectileFrame = 0;
                } else {
                    // Increment frame counter
                    projectileFrame++;
                }
            }
            // Increment projectile shooting timer
            // TODO modify this timer so that it will keep one keypress in memory
            // i.e. if you press it and the timer is still active, it will fire
            // once the timer runs all the way, then stop.
            // If statement is to protect against data overflows
            if (projectileTimer < projectileTimerDelay) {
                projectileTimer++;
            }
            //</editor-fold>
            
            // **Handles the camera movement around bobert
            //            //<editor-fold defaultstate="collapsed" desc="Camera">
//            if (bobert.yPositionInCam(screenCam) <= (screenCam.getHeight() * 0.5)) {
//                screenCam.moveVerticallyBy(bobert.vertVelocity * 2);
//            } else if (bobert.yPositionInCam(screenCam) >= (screenCam.getHeight() * 0.5)
//                    && screenCam.getY() + screenCam.getHeight() <= Main.B_WINDOW_HEIGHT) {
//                screenCam.moveVerticallyBy(bobert.vertVelocity * 2);
//            }
            if (screenCam.getX() + screenCam.getWidth() < level.floor.hitBox.width
                    && bobert.xPositionInCam(screenCam) > screenCam.getWidth() * 0.5) {
                screenCam.moveRightBy(bobert.moveSpeed * Camera.scrollFactor);
            }
            if (screenCam.getX() > level.floor.hitBox.x
                    && bobert.xPositionInCam(screenCam) < screenCam.getWidth() * 0.5) {
                screenCam.moveLeftBy(bobert.moveSpeed * Camera.scrollFactor);
            }
            if (screenCam.getY() < 0 
                    && bobert.yPositionInCam(screenCam) > screenCam.getY() + (screenCam.getHeight()*0.5)){
                screenCam.moveVerticallyBy(3);
            }
            if (screenCam.getY() > level.background.drawBox.y 
                    && bobert.yPositionInCam(screenCam) < screenCam.getY() + (screenCam.getHeight()*0.5)) {
                screenCam.moveVerticallyBy(-3);
            }
            
//            if (bobert.isInAir) {
//                screenCam.moveVerticallyBy(bobert.vertVelocity * Camera.scrollFactor);
//            } else {
//                screenCam.moveVerticallyBy()
//            }
            
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
        if (!typingConsoleCommand) {
            if (e.getKeyCode() == bobert.keyLeft) {
                bobert.movingLeft = true;
                bobert.facingLeft = true;
                bobert.facingRight = false;
            }
            if (e.getKeyCode() == bobert.keyRight) {
                bobert.movingRight = true;
                bobert.facingLeft = false;
                bobert.facingRight = true;
            }
            if (e.getKeyCode() == bobert.keyJump) {
                // Make sure we aren't already jumping, then set isInAir to true
                // as a flag that we are jumping, and then set the vertical 
                // velocity to jumping.
                if (!bobert.isInAir) {
                    bobert.isInAir = true;
                    bobert.vertVelocity = Character.vertVelocityJump;
                } else if (!bobert.hasDoubleJumped) {
                    bobert.isInAir = true;
                    bobert.vertVelocity = Character.vertVelocityJump;
                    bobert.hasDoubleJumped = true;
                }
            }
            if (e.getKeyCode() == bobert.keyShoot) {
                // Make sure we aren't already shooting the projetile, then set
                // shootingProjectile to true and set the projectile velocity
                // to bouncing.
                if (projectileTimer >= projectileTimerDelay) {
                    projectileTimer = 0;
                    Projectile newProjectile = defaultProjectile;
                    newProjectile.destroyed = false;
                    if (bobert.facingRight) {
                        newProjectile.movingRight = true;
                        if (bobert.movingRight) {
                            newProjectile.movingQuickerSpeed = true;
                        }
                    } else {
                        newProjectile.movingRight = false;
                        if (bobert.movingLeft) {
                            newProjectile.movingQuickerSpeed = true;
                        }
                    }
                    onScreenProjectiles.add(newProjectile);
                    defaultProjectile = new Projectile();
                    defaultProjectile.setX(bobert.hitBox.x - level.floor.hitBox.x);
                    defaultProjectile.setY(bobert.hitBox.y);
                }
                shootingProjectile = true;
            } // Dev commands, debug commands
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                consoleCommand = ""; // Clear the input for the command
                typingConsoleCommand = true; // Start grabbing characters
            }
        } else {
            // Dev/debug commands
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                // deal with the console command, then clear it.
                if (consoleCommand.isEmpty()) return; // don't deal with it if it's empty
                // String to hold the second word in a command, 
                // i.e. hold the "enemy" in "add enemy"
                String cmdRemaining = consoleCommand; 
                
                if (consoleCommand.equalsIgnoreCase("reset") || consoleCommand.equalsIgnoreCase("r")) {
                    // reset the current level, reloading everything from the XML files
                    BobertPanel.defineObjects();
                    level = new GameLevel(level.levelName, screenCam);
                } else if (consoleCommand.length() >= 3) { // if it has a command
                    if (consoleCommand.substring(0, 4).equalsIgnoreCase("save")) {
                        cmdRemaining = cmdRemaining.substring(5);
                        // save stuff
                        if (cmdRemaining.substring(0, 5).equalsIgnoreCase("level")) {
                            cmdRemaining = cmdRemaining.substring(6);
                            // save the level
                            /*
                             * TODO
                             * 1)Create a folder named whatever is left in cmdRemaining
                             *   i.e. save level MyLevelName
                             * 2)Write backgrounds_data.xml with whatever is the background and floor
                             * 3)Write collidable_data.xml with whatever is in collidables
                             * 4)Write enemy_data.xml with whatever is in enemies
                             */
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
                            level.enemies.add(new Enemy(level, level.levelName));
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
            } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
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
        if (e.getKeyCode() == bobert.keyLeft) {
            bobert.movingLeft = false;
        }
        if (e.getKeyCode() == bobert.keyRight) {
            bobert.movingRight = false;
        }
    }
    
}