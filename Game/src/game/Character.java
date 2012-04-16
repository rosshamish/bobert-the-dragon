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
    
    static int vertVelocityJump = -13;
    public boolean hasDoubleJumped = false;
    
    static int imageCount = 0;
    static int numImages;
    
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
        moveSpeed = Integer.parseInt(RossLib.parseXML(dataPath, "character", "bobert", "speed"));
        int defaultX = (int) (Main.B_WINDOW_WIDTH * 0.5);
        int defaultY = 350;
        initBoxes(new Rectangle(defaultX, defaultY,
                                width, height)
                  );
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
//        currentGraphics2DContext.drawImage(this.getImage(), 
//                floorX+this.this.xPositionInCam(cam), this.this.yPositionInCam(cam),
//                this.drawBox.width, this.drawBox.height, 
//                null);
    }
}
