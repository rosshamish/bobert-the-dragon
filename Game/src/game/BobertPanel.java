package game;

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
    static ArrayList<Enemy> enemies;
    static Character bobert;
    
    // Animation vars
    static int animationFrame = 0;
    static int animationDelay = 25;
    
    // Background display vars.
    static WorldObject background;
//    static ImageIcon iiBackground;
//    static Image imgBackground;
    
    // Floor/Ground vars. Rectangle is its hit box.
    static Collidable floor;
    
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
    static final int movementDelayReset = 0;
    static final int movementDelayInAir = 0;
    static int movementDelay = movementDelayReset;    
    
    //</editor-fold>
    
    public BobertPanel(BobertFrame frame) {
        setBackground(new Color(103, 187, 241));
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
        // LISTENERS -- They do exactly what their name says. They sit around and
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
               
        // TODO compress this file.
        // ALWAYS DEFINE THE FLOOR FIRST
        int floorWidth = Main.B_WINDOW_WIDTH*3;
        Rectangle floorCollisionRect = new Rectangle(0, Main.B_WINDOW_CANVAS_HEIGHT-50,
                floorWidth, 100);
        floor = new Collidable(floorCollisionRect, WorldObjectType.NEUTRAL, Collidable.resourcesPathStem + "floor.png");
        floor.initBoxes(floor.hitBox);
        
        // This needs to be a jpg because the image file is HUGE and it doesn't
        // need transparency.
        background = new WorldObject(new Rectangle(floor.drawBox.x, 0,
                floor.drawBox.width, Main.B_WINDOW_HEIGHT));
        background.setImage("resources/background.jpg");
//        iiBackground = new ImageIcon("resources/background.jpg");
//        imgBackground = iiBackground.getImage();
        
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
        
        enemies = new ArrayList<Enemy>();
        int numTotalEnemies = RossLib.parseXML(Enemy.dataPath, "enemy");
        for (int i=0; i<numTotalEnemies; i++) {
            enemies.add(new Enemy());
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
            background.draw(g2d, floor.hitBox.x);

            // **Draw bobert
            bobert.draw(g2d, floor.hitBox.x);
            // **Draw enemies
            for (int i=0; i<enemies.size();i++) {
                enemies.get(i).draw(g2d, floor.hitBox.x);
            }
            

            // **Draw Projectile
            if (shootingProjectile) {
                for (int i = 0; i < onScreenProjectiles.size(); i++) {
//                    onScreenProjectiles.get(i).draw(g2d, floor.hitBox.x);
                    onScreenProjectiles.get(i).drawDebug(g2d, floor.hitBox.x);
                }
            }
            // **Draw the currently held projectile's name
            int fontSize = 20;
            g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
            g2d.drawString("Holding: "+defaultProjectile.name, 50, Main.B_WINDOW_CANVAS_HEIGHT-fontSize);

            // **Draw floor
//            floor.draw(g2d, floor.drawBox.x);
            floor.drawDebug(g2d, floor.hitBox.x);

            // **Debugging values on screen
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font(Font.DIALOG, Font.PLAIN, 15));
            g2d.drawString("bobert.drawBox.x: " + bobert.drawBox.x, 0, debugTextHeight * 1);
            g2d.drawString("bobert.hitBox.x: " + bobert.hitBox.x, 0, debugTextHeight * 2);
            g2d.drawString("floor.image:  "+ floor.image, 0, debugTextHeight*3);
            g2d.drawString("floor.hitBox.x: "+ floor.hitBox.x, 0, debugTextHeight*4);
            g2d.drawString("floor.hitBox.x + floor.hitBox.width:  " + (floor.hitBox.x + floor.hitBox.width), 0, debugTextHeight * 5);
//            g2d.drawString("enemies.get(0).hitBox.height:  "+enemies.get(0).hitBox.height, 0, debugTextHeight*6);
//            g2d.drawString("facingLeft:     "+facingLeft, 0, debugTextHeight*7);
//            g2d.drawString("projectileVertVelocity: "+projectileVertVelocity, 0, debugTextHeight*9);
        }

    }

    @Override
    public void run() {

        while (gameRunning) {
            FPSStartOfLoop();

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
                bobert.futureHitBox = bobert.hitBox;
                bobert.futureHitBox.y += bobert.vertVelocity;
                if (bobert.willCollideWith(floor)) {
                    bobert.isInAir = false;
                    bobert.setY(floor.hitBox.y - bobert.hitBox.height);
                    // The default projectile should move along with the character.
                    defaultProjectile.setY(floor.drawBox.y - bobert.hitBox.height);
                    // Set the character's vertical velocity to 0 because we are
                    // on the ground, goddamn it, and we aren't jumping, goddamn it.
                    bobert.vertVelocity = 2;
                } else {
                    // If we aren't going to be on the ground right away, then
                    // we must be in the air. Set isInAir to true so that we can
                    // know whether or not we are jumping.
                    bobert.isInAir = true;
                }
                // If the character is in the air, add some gravity to our current
                // velocity, and then add the new velocity to the character's
                // height.
                if (bobert.isInAir) {
                    bobert.vertVelocity += gravity;
                    bobert.moveVerticallyBy(bobert.vertVelocity);
                    // The default projectile should move along with the character.
                    defaultProjectile.moveVerticallyBy(bobert.vertVelocity);
                }
                // DO ALL THIS AGAIN FOR EACH ENEMY
                // If the character is about to be inside/below the floor, then
                // we should set the character to be standing directly on top of
                // the floor, and set isInAir to false (because the character is
                // on the ground now.
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy currentEnemy = enemies.get(i);
                    currentEnemy.futureHitBox.y += currentEnemy.vertVelocity;
                    if (currentEnemy.willCollideWith(floor)) {
                        currentEnemy.isInAir = false;
                        currentEnemy.setY(floor.hitBox.y - currentEnemy.hitBox.height);
                        // Set the character's vertical velocity to 0 because we are
                        // on the ground, goddamn it, and we aren't jumping, goddamn it.
                        currentEnemy.vertVelocity = 2;
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
                    enemies.set(i, currentEnemy);
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
            // If the character is in the air, he should move a little bit slower
            // than if he was on the ground. Hence, this if-else statement.
            // It should be noted that movementDelayInAir is larger than
            // movementDelayReset
            if (bobert.isInAir) {
                movementDelay = movementDelayInAir;
            } else {
                movementDelay = movementDelayReset;
            }
            // See the explanation of Frame-Delay up in the inAir section ^^
            if (movementFrame >= movementDelay) {
                // If the character is moving to the right (as set in the
                // keyPressed and keyReleased methods), then move it to the right.
                // Duh.
                
                if (bobert.hitBox.x > 0
                        && bobert.hitBox.x + bobert.hitBox.width < Main.B_WINDOW_WIDTH) {
                    if (bobert.movingRight) {
                        if (bobert.hitBox.x + bobert.hitBox.width >= Main.B_WINDOW_WIDTH * 0.65
                                && Math.abs(floor.drawBox.x) + Main.B_WINDOW_WIDTH < floor.drawBox.width) {
                            floor.moveLeftBy(bobert.moveSpeed);
                        } else {
                            bobert.moveRightBy(bobert.moveSpeed);
                        }


                    }
                    // If the character is moving to the left (as set in the
                    // keyPressed and keyReleased methods), then move it to the left.
                    // Duh.
                    if (bobert.movingLeft) {
                        if (bobert.hitBox.x < Main.B_WINDOW_WIDTH * 0.35
                                && floor.drawBox.x < 0) {
                            floor.moveRightBy(bobert.moveSpeed);
                        } else {
                            bobert.moveLeftBy(bobert.moveSpeed);
                        }
                        // If the projectile has yet to be shot (i.e. it isn't bouncing
                        // around), then it should move along with the character.

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
                defaultProjectile.setX(bobert.hitBox.x - floor.hitBox.x);
                defaultProjectile.setY(bobert.hitBox.y);

                //**Enemy movement
                for (int i = 0; i < enemies.size(); i++) {
                    Enemy currentEnemy = enemies.get(i);
                    
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
                        if (currentEnemy.hitBox.x+currentEnemy.hitBox.width < floor.hitBox.width) {
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
                    enemies.set(i, currentEnemy);
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
                        Projectile currentProjectile = onScreenProjectiles.get(i);
                        if (currentProjectile.movingRight) {
                            currentProjectile.moveRightBy(currentProjectile.moveSpeed);
                            // wasMovingRight is set to true in keyPressed and is set
                            // back to false in keyReleased (if the projectile isn't
                            // being shot), and is set back to false in projectileDestroyed
                            // as well.
                            if (currentProjectile.movingDoubleSpeed) {
                                currentProjectile.moveRightBy(currentProjectile.moveSpeed);
                            }
                        }
                        // If is moving left
                        if (!currentProjectile.movingRight) {
                            currentProjectile.moveLeftBy(currentProjectile.moveSpeed);
                            // See wasMovingRight for explanation
                            if (currentProjectile.movingDoubleSpeed) {
                                currentProjectile.moveLeftBy(currentProjectile.moveSpeed);
                            }
                        }
                        // **Vertical Movement
                        // If the space it will move into is in the floor,
                        // set the projectile to sit just above the floor.
                        // Otherwise, add gravity to velocity and then velocity to
                        // the projectile's y value normally.
                        if (currentProjectile.isCollidingWith(floor)) {
                            currentProjectile.setY(floor.hitBox.y - currentProjectile.hitBox.height);
                            currentProjectile.vertVelocity = Projectile.vertVelocityBounce;
                        } else {
                            currentProjectile.vertVelocity += gravity;
                            currentProjectile.moveVerticallyBy(currentProjectile.vertVelocity);
                        }

                        // Bounds check to see if the projectile is off the screen
                        if (currentProjectile.hitBox.x+currentProjectile.hitBox.width < floor.hitBox.x 
                                || currentProjectile.hitBox.x > floor.hitBox.width) {
                            
                            currentProjectile.destroyed = true;
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
                        newProjectile.movingDoubleSpeed = true;
                    }
                } else {
                    newProjectile.movingRight = false;
                    if (bobert.movingLeft) {
                        newProjectile.movingDoubleSpeed = true;
                    }
                }
                onScreenProjectiles.add(newProjectile);
                defaultProjectile = new Projectile();
                defaultProjectile.setX(bobert.hitBox.x - floor.hitBox.x);
                defaultProjectile.setY(bobert.hitBox.y);
            }
            shootingProjectile = true;
        }
    }

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