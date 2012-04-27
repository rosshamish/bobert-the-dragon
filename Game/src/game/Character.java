package game;

import interfaces.Drawable;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import rosslib.RossLib;

/**
 *
 * @author Ross-Desktop
 */
public class Character extends Sprite 
                       implements Drawable {
    
    static String resourcesPath = resourcesPathStem + "character/";
    static String dataPath = resourcesPath + "character_data.xml";
    
    // Readable names for keyboard keys
    public int keyLeft = KeyEvent.VK_A;
    public int keyRight = KeyEvent.VK_D;
    public int keyJump = KeyEvent.VK_W;
    public int keyShoot = KeyEvent.VK_SPACE;
    
    public int horizAcceleration = 1;
    public int horizMaxVelocity = 2;
    public static int horizAccelDelay = 60;
    public int horizAccelFrame = 0;
    public boolean shouldAccelLeft = false;
    public boolean shouldAccelRight = false;
    
    
    static int vertVelocityJump = -20;
    static int vertVelocityDoubleJump = (int) (vertVelocityJump * 0.85);
    public boolean hasJumped = false;
    public boolean hasDoubleJumped = false;
    
    public int numCollected = 0;
    public int totalCollected = 0;
    
    public Character() {
        try {
            ImageIcon iiP = new ImageIcon(resourcesPath + imageCount + ".png");
            this.image = iiP.getImage();
            image = iiP.getImage();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        
        int width = Integer.parseInt(
                RossLib.parseXML(resourcesPath + "character_data.xml",
                "character", "bobert", "width")
                );
        int height = Integer.parseInt(
                RossLib.parseXML(resourcesPath + "character_data.xml",
                "character", "bobert", "height")
                );
        name = RossLib.parseXML(dataPath, "character", "bobert", "name");
        worldObjectType = WorldObjectType.CHARACTER;
        collisionType = CollisionType.IMPASSABLE;
        horizVelocity = 0;
        int defaultX = (int) (Main.B_WINDOW_WIDTH * 0.5);
        int defaultY = 350;
        initBoxes(new Rectangle(defaultX, defaultY,
                                width, height)
                  );
    }
    
    public void moveRight() {
        moveRightBy(Math.abs(horizVelocity));
    }
    
    public void moveLeft() {
        moveLeftBy(Math.abs(horizVelocity));
    }
    
    public void accelerateLeft() {
        if (Math.abs(horizVelocity) < horizMaxVelocity) 
            horizVelocity -= horizAcceleration;
    }
    
    public void accelerateRight() {
        if (horizVelocity < horizMaxVelocity)
            horizVelocity += horizAcceleration;
    }
    
    public void decelerate() {
        if (horizVelocity < 0) {
            horizVelocity += horizAcceleration;
        } else if (horizVelocity > 0) {
            horizVelocity -= horizAcceleration;
        }
    }
    
    public void decelerateCompletely() {
        horizVelocity = 0;
    }
    
    @Override
    public void draw(Graphics2D currentGraphics2DContext, Camera cam) {
        if (this.isInViewOf(cam)) {
            if (facingLeft) {
//            currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
//                    this.hitBox.width, this.hitBox.height, null);
                currentGraphics2DContext.drawImage(image,
                        this.xPositionInCam(cam), this.yPositionInCam(cam),
                        this.xPositionInCam(cam) + drawBox.width, this.yPositionInCam(cam) + drawBox.height,
                        image.getWidth(null), 0,
                        0, image.getHeight(null),
                        null);
            } else {
//            currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
//                    this.hitBox.width, this.hitBox.height, null);
                currentGraphics2DContext.drawImage(image,
                        this.xPositionInCam(cam), this.yPositionInCam(cam),
                        this.xPositionInCam(cam) + drawBox.width, this.yPositionInCam(cam) + drawBox.height,
                        0, 0,
                        image.getWidth(null), image.getHeight(null),
                        null);
            }
        }
    }

}
