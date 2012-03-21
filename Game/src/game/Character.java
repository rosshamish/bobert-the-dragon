package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author Ross-Desktop
 */
public class Character {
    
    static final String defaultPathStem = "resources/character";
    static final int defaultWidth  = 120;
    static final int defaultHeight = 200;
    static final int defaultX = (int) (Main.B_WINDOW_WIDTH / 2);
    static final int defaultY = (int) (BobertPanel.floorCollisionHeight - defaultHeight);
    
    static int characterImageCount = 0;
    static final int numCharacterImages = 1;
    
    private Image image;
    private Rectangle hitBox;
    public int vertVelocity;
    public static final int vertVelocityJump = -20;
    public boolean isInAir;

    // Vars to check the left-right direction of the character
    // Which way the character is facing
    public boolean facingRight;
    public boolean facingLeft;
    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
    public Character() {
        try {
            ImageIcon iiP = new ImageIcon(defaultPathStem + characterImageCount + ".png");
            characterImageCount++;
            if (characterImageCount >= numCharacterImages) {
                characterImageCount = 0;
            }
            image = iiP.getImage();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        hitBox = new Rectangle(defaultX, defaultY,
                defaultWidth, defaultHeight);
        vertVelocity = 0;
    }
    
    public Character(String imagePath, int x, int y,
            int width, int height) {
        ImageIcon iiP = new ImageIcon(imagePath);
        image = iiP.getImage();
        hitBox = new Rectangle(x, y,
                width, height);
    }
    
    public void draw(Graphics2D currentGraphics2DContext) {
        currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
                this.hitBox.width, this.hitBox.height, null);
    }
    
    public Rectangle getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle hitBox) {
        this.hitBox = hitBox;
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
