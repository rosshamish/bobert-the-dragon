package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;

/**
 *
 * @author Ross-Desktop
 */
public class Enemy {
    static final String defaultPathStem = "resources/enemies/";
    static final int defaultWidth  = 60;
    static final int defaultHeight = 110;
    // There is no default x because it will always be randomly calculated
    static final int defaultY = (int) (Main.B_WINDOW_CANVAS_HEIGHT - defaultHeight);
    
    static int enemyImageCount = 0;
    static final int numEnemyImages = 1;
    
    private Image image;
    public Rectangle hitBox;
    // Used for collision detection
    public Rectangle futureHitBox;
    public int vertVelocity;
    public static final int vertVelocityJump = -15;
    public boolean isInAir;

    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
    public Enemy() {
        try {
            ImageIcon iiP = new ImageIcon(defaultPathStem + enemyImageCount + ".png");
            enemyImageCount++;
            if (enemyImageCount >= numEnemyImages) {
                enemyImageCount = 0;
            }
            image = iiP.getImage();
        } catch (Exception e) {
            System.out.println("Exception: "+e.getMessage());
        }
        Random rand = new Random();
        movingRight = rand.nextBoolean();
        if (movingRight) {
            movingLeft = false;
        } else {
            movingLeft = true;
        }
        int randomX = rand.nextInt(Main.B_WINDOW_WIDTH*4);
        hitBox = new Rectangle(randomX, defaultY,
                defaultWidth, defaultHeight);
    }
    
    public Enemy(String imagePath, int x, int y,
            int width, int height, boolean moveRight) {
        ImageIcon iiP = new ImageIcon(imagePath);
        image = iiP.getImage();
        hitBox = new Rectangle(x, y,
                width, height);
        if (moveRight) {
            movingRight = true;
            movingLeft = false;
        } else {
            movingRight = false;
            movingLeft = true;
        }
    }
    
    public void draw(Graphics2D currentGraphics2DContext, int floorX) {
        currentGraphics2DContext.drawImage(this.getImage(), 
                floorX+this.hitBox.x, this.hitBox.y,
                this.hitBox.width, this.hitBox.height, null);
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
