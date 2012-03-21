package game;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class BobertPanel extends JPanel implements Runnable,
        KeyListener {

    //<editor-fold defaultstate="collapsed" desc="Class Variables">
    
    // Character vars. 
    static Character bobert;
    
    // Background display vars.
    static ImageIcon iiBackground;
    static Image imgBackground;
    
    // Floor/Ground vars. Rectangle is its hit box.
    static Rectangle floor;
    static Image imgFloor;
    static ImageIcon iiFloor;
    static final int floorWidth = Main.B_WINDOW_WIDTH * 4;
    static final int floorDrawHeight = 24;
    static int floorCollisionHeight = Main.B_WINDOW_HEIGHT - floorDrawHeight;
    
    // Projectile ArrayList
    static ArrayList<Projectile> projectiles;
    static Projectile defaultProjectile;
    // More Projectile vars.
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
    public static final long FPSDelayPerFrame = 2;
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
    
    // Desired frames per second. I think this value is bullshit, and seems to
    // not be very helpful. Just leave it at 1000.
    static int fps = 1000;
    
    // Giving more readable names for keys on the keyboard
    static int keyLeft = KeyEvent.VK_A;
    static int keyRight = KeyEvent.VK_D;
    static int keyJump = KeyEvent.VK_W;
    static int keyCrouch = KeyEvent.VK_S;
    static int keyShoot = KeyEvent.VK_SPACE;
    //</editor-fold>
    
    public BobertPanel(BobertFrame frame) {
        setBackground(new Color(103, 187, 241));
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
        bobert = new Character();
        
        // This needs to be a jpg because the image files is HUGE and it doesn't
        // need transparency.
        iiBackground = new ImageIcon("resources/background.jpg");
        imgBackground = iiBackground.getImage();
        
        // TODO compress this file.
        iiFloor = new ImageIcon("resources/foreground.png");
        imgFloor = iiFloor.getImage();
        floor = new Rectangle(-10, floorCollisionHeight,
                floorWidth + 10, floorDrawHeight);
        // Initialize the list of projectiles
        projectiles = new ArrayList<Projectile>();
        projectiles.ensureCapacity(10);

        defaultProjectile = new Projectile();
        defaultProjectile.getHitBox().x = bobert.getHitBox().x;
        defaultProjectile.getHitBox().y = bobert.getHitBox().y;

        gameRunning = true;
        objectsDefined = true;
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        if (objectsDefined) {

            // **Draw background
            // -20 because it the top of the window (the bar) is about 20 pixels
            g2d.drawImage(imgBackground, floor.x, -20,
                    floorWidth, Main.B_WINDOW_HEIGHT,
                    null);

            // **Draw character
            // Draw the character rectangle for debugging
//            g.setColor(Color.BLACK);
//            g.fillRect(character.x, character.y, 
//                    character.width, character.height);
            bobert.draw(g2d);

            // **Draw Projectile
            if (shootingProjectile) {
                for (int i = 0; i < projectiles.size(); i++) {
                    projectiles.get(i).draw(g2d);
                }
            }

            // **Draw floor
            // Draw the floor rectangle for debugging
//            g.setColor(Color.DARK_GRAY);
//            g.fillRect(floor.x, floor.y, 
//                    floor.width, floor.height);
            // Draw the ground image
            // -20 because it the top of the window (the bar) is about 20 pixels
            g2d.drawImage(imgFloor, -10, -20,
                    Main.B_WINDOW_WIDTH + 20, Main.B_WINDOW_HEIGHT,
                    null);

            // **Debugging values on screen
            g2d.setColor(Color.BLACK);
//            g2d.drawString("projectileTimer: " + projectileTimer, 0, debugTextHeight * 1);
//            g2d.drawString("projectileTimerDelay: " + projectileTimerDelay, 0, debugTextHeight * 2);
//            g2d.drawString("bobert.vertVelocity:  "+ bobert.vertVelocity, 0, debugTextHeight*3);
//            g2d.drawString("projectile.getHitBox().height:"+projectile.getHitBox().height, 0, debugTextHeight*4);
//            g2d.drawString("shootingProjectile:  " + shootingProjectile, 0, debugTextHeight * 5);
//            g2d.drawString("movingLeft:     "+movingLeft, 0, debugTextHeight*6);
//            g2d.drawString("facingLeft:     "+facingLeft, 0, debugTextHeight*7);
//            g2d.drawString("projectileVertVelocity: "+projectileVertVelocity, 0, debugTextHeight*9);
        }

    }

    @Override
    public void run() {

        while (gameRunning) {
            FPSStartOfLoop();

            // **For Collision Detection
            // Create points that represent where the the character's feet will 
            // be after it moves. We can use these points later to check whether
            // the character is on the ground or not
            Point newBotLeft = new Point(bobert.getHitBox().x,
                    bobert.getHitBox().y + bobert.getHitBox().height + 
                    bobert.vertVelocity + gravity);
            Point newBotRight = new Point(bobert.getHitBox().x + bobert.getHitBox().width,
                    bobert.getHitBox().y + bobert.getHitBox().height + 
                    bobert.vertVelocity + gravity);

            // **Falling/In the Air
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
                if ((floor.contains(newBotLeft) || floor.contains(newBotRight))) {
                    bobert.isInAir = false;
                    bobert.getHitBox().y = floor.y - bobert.getHitBox().height;
                    // The default projectile should move along with the character.
                    defaultProjectile.getHitBox().y = floor.y - bobert.getHitBox().height;
                    // Set the character's vertical velocity to 0 because we are
                    // on the ground, goddamn it, and we aren't jumping, goddamn it.
                    bobert.vertVelocity = 0;
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
                    bobert.getHitBox().y += bobert.vertVelocity;
                    // The default projectile should move along with the character.
                    defaultProjectile.getHitBox().y += bobert.vertVelocity;
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

            // **Movement.
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
                if (bobert.getHitBox().x > 0 && 
                        bobert.getHitBox().x + bobert.getHitBox().width < Main.B_WINDOW_WIDTH) {
                    if (bobert.movingRight) {
                        if (bobert.getHitBox().x+bobert.getHitBox().width >= Main.B_WINDOW_WIDTH * 0.65 && 
                                Math.abs(floor.x) + Main.B_WINDOW_WIDTH < floorWidth) {
                            floor.x--;
                        } else {
                            bobert.getHitBox().x++;
                            // The default projectile should move along with the character.
                            defaultProjectile.getHitBox().x++;
                        }


                    }
                    // If the character is moving to the left (as set in the
                    // keyPressed and keyReleased methods), then move it to the left.
                    // Duh.
                    if (bobert.movingLeft) {
                        if (bobert.getHitBox().x < Main.B_WINDOW_WIDTH * 0.35 && 
                                floor.x < 0) {
                            floor.x++;
                        } else {
                            bobert.getHitBox().x--;
                            // The default projectile should move along with the character.
                            defaultProjectile.getHitBox().x--;
                        }
                        // If the projectile has yet to be shot (i.e. it isn't bouncing
                        // around), then it should move along with the character.

                    }
                } else {
                    if (bobert.getHitBox().x <= 0) {
                        bobert.getHitBox().x++;
                        // The default projectile should move along with the character.
                        defaultProjectile.getHitBox().x++;
                    } else {
                        bobert.getHitBox().x--;
                        // The default projectile should move along with the character.
                        defaultProjectile.getHitBox().x--;
                    }
                }
                // Set frame to 0 to reset counter, blah blah, see explanation in
                // inAir
                movementFrame = 0;
            } else {
                // Increase skipped frame counter
                movementFrame++;
            }

            // **Projectile.
            // shootingProjectile is set to true in keyPressed 
            // and is set back to false in "if(projectileDestroyed)"
            if (shootingProjectile) {
                // See explanation of Frame-Delay up at the inAir block ^^
                if (projectileFrame >= projectileDelay) {

                    // **Horizontal Movement
                    // Move the projectile in the direction the character 
                    // is facing
                    // If the character was moving at the time it shot the projectile,
                    // move the projectile twice as fast.
                    for (int i = 0; i < projectiles.size(); i++) {
                        Projectile temp = projectiles.get(i);
                        if (temp.movingRight) {
                            temp.getHitBox().x += 5;
                            // wasMovingRight is set to true in keyPressed and is set
                            // back to false in keyReleased (if the projectile isn't
                            // being shot), and is set back to false in projectileDestroyed
                            // as well.
                            if (temp.movingDoubleSpeed) {
                                temp.getHitBox().x += 5;
                            }
                        }
                        // If is moving left
                        if (!temp.movingRight) {
                            temp.getHitBox().x -= 5;
                            // See wasMovingRight for explanation
                            if (temp.movingDoubleSpeed) {
                                temp.getHitBox().x -= 5;
                            }
                        }
                        // **Vertical Movement
                        // If the space it will move into is in the floor,
                        // set the projectile to sit just above the floor.
                        // Otherwise, add gravity to velocity and then velocity to 
                        // the projectile's y value normally.
                        if (temp.getHitBox().y + gravity + temp.vertVelocity + temp.getHitBox().height >= floor.y) {
                            temp.getHitBox().y = floor.y - temp.getHitBox().height;
                            temp.vertVelocity = Projectile.vertVelocityBounce;
                        } else {
                            temp.vertVelocity += gravity;
                            temp.getHitBox().y += temp.vertVelocity;
                        }


                        // Bounds check to see if the projectile is off the screen
                        if (temp.getHitBox().y < 0 || temp.getHitBox().y > Main.B_WINDOW_HEIGHT) {
                            temp.destroyed = true;
                        }
                        if (temp.getHitBox().x < 0 || temp.getHitBox().x > Main.B_WINDOW_WIDTH) {
                            temp.destroyed = true;
                        }
                        
                        if (temp.destroyed) {
                            projectiles.remove(i);
                            i--;
                            if (projectiles.isEmpty()) {
                                shootingProjectile = false;
                            }
                        } else {
                            projectiles.set(i, temp);
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
            projectileTimer++;
            // To protect against data overflows
            if (projectileTimer > projectileTimerDelay+50) {
                projectileTimer = projectileTimerDelay+50;
            }

            // Sleeps the thread for a short period of time to keep the game
            // (hopefully) running at 1000fps. Does this actually keep the
            // frame rate constant? I doubt it.
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
        if (e.getKeyCode() == keyLeft) {
            bobert.movingLeft = true;
            bobert.facingLeft = true;
            bobert.facingRight = false;
        }
        if (e.getKeyCode() == keyRight) {
            bobert.movingRight = true;
            bobert.facingLeft = false;
            bobert.facingRight = true;
        }
        if (e.getKeyCode() == keyJump) {
            // Make sure we aren't already jumping, then set isInAir to true
            // as a flag that we are jumping, and then set the vertical 
            // velocity to jumping.
            if (!bobert.isInAir) {
                bobert.isInAir = true;
                bobert.vertVelocity = Character.vertVelocityJump;
            }
        }
        if (e.getKeyCode() == keyShoot) {
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
                projectiles.add(newProjectile);
                defaultProjectile = new Projectile();
                defaultProjectile.getHitBox().x = bobert.getHitBox().x;
                defaultProjectile.getHitBox().y = bobert.getHitBox().y;
            }
            shootingProjectile = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == keyLeft) {
            bobert.movingLeft = false;
        }
        if (e.getKeyCode() == keyRight) {
            bobert.movingRight = false;
        }
    }
    
}
