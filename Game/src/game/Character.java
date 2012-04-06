package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author Ross-Desktop
 */
public class Character {
    
    static final String defaultPathStem = "resources/character/";
    static final int defaultWidth  = 120;
    static final int defaultHeight = 200;
    static final int defaultX = (int) (Main.B_WINDOW_WIDTH / 2);
    static final int defaultY = (int) (Main.B_WINDOW_CANVAS_HEIGHT / 2);
    
    int imageCount = 0;
    public int numImages;
    
    private Image image;
    public ArrayList<String> imageLocations;
    public Rectangle hitBox;
    // Used for collision detection
    public Rectangle futureHitBox;
    public int vertVelocity;
    public static final int vertVelocityJump = -15;
    public int moveSpeed = 2;
    public boolean isInAir;

    // Vars to check the left-right direction of the character
    // Which way the character is facing
    public boolean facingRight;
    public boolean facingLeft;
    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
    // Giving more readable names for keys on the keyboard
    public int keyLeft = KeyEvent.VK_A;
    public int keyRight = KeyEvent.VK_D;
    public int keyJump = KeyEvent.VK_W;
    public int keyShoot = KeyEvent.VK_SPACE;
    
    public Character() {
        try {
            ImageIcon iiP = new ImageIcon(defaultPathStem + imageCount + ".png");
            imageCount++;
            if (imageCount >= numImages) {
                imageCount = 0;
            }
            image = iiP.getImage();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        hitBox = new Rectangle(defaultX, defaultY,
                defaultWidth, defaultHeight);
        keyLeft = KeyEvent.VK_A;
        keyRight = KeyEvent.VK_D;
        keyJump = KeyEvent.VK_W;
        keyShoot = KeyEvent.VK_SPACE;
    }
    
    public Character(String imagePath, int x, int y,
            int width, int height) {
        ImageIcon iiP = new ImageIcon(imagePath);
        image = iiP.getImage();
        hitBox = new Rectangle(x, y,
                width, height);
        keyLeft = KeyEvent.VK_A;
        keyLeft = KeyEvent.VK_D;
        keyJump = KeyEvent.VK_W;
        keyShoot = KeyEvent.VK_SPACE;
    }
    
    public void draw(Graphics2D currentGraphics2DContext) {
        if (facingLeft) {
//            currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
//                    this.hitBox.width, this.hitBox.height, null);
            currentGraphics2DContext.drawImage(image,
                    hitBox.x, hitBox.y,
                    hitBox.x+hitBox.width, hitBox.y+hitBox.height,
                    image.getWidth(null), 0,
                    0, image.getHeight(null),
                    null);
        } else {
//            currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
//                    this.hitBox.width, this.hitBox.height, null);
            currentGraphics2DContext.drawImage(image,
                    hitBox.x, hitBox.y,
                    hitBox.x+hitBox.width, hitBox.y+hitBox.height,
                    0, 0,
                    image.getWidth(null), image.getHeight(null),
                    null);
        }
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    public void setImage(String path) {
        ImageIcon ii = new ImageIcon(path);
        if (ii.getIconHeight() == 0) { 
            System.out.println("You messed up, the file at "+
                path+" doesn't exist, bro.");
        } else {
            this.image = ii.getImage();
        }
    }
}
