package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;
import rosslib.RossLib;

/**
 *
 * @author Ross-Desktop
 */
public class Enemy {
    static final String defaultPathStem = "resources/enemies/";
    static final int defaultWidth  = 80;
    static final int defaultHeight = 130;
    // There is no default x because it will always be randomly calculated
    static final int defaultY = (int) (Main.B_WINDOW_CANVAS_HEIGHT - defaultHeight);
    
    static int imageCount = 0;
    static int numEnemies = 1;
    
    private Image image;
    public String name;
    public Rectangle drawBox;
    public Rectangle hitBox;
    static int drawHitOffset = 5;
    // Used for collision detection
    public Rectangle futureHitBox;
    public int vertVelocity;
    public static final int vertVelocityJump = -15;
    public int moveSpeed = 1;
    public boolean isInAir;

    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
    public Enemy() {
        String filePath = defaultPathStem + "enemy_data.xml";
        numEnemies = RossLib.parseXML(filePath, "enemy");
        imageCount++;
        if (imageCount >= numEnemies) {
            imageCount = 0;
        }
        //name
        name = RossLib.parseXML(filePath, "enemy", imageCount, "name");
        //location
        String location = RossLib.parseXML(filePath, "enemy", imageCount, "location");
        ImageIcon ii = new ImageIcon(location);
        image = ii.getImage();
        //width
        int width = Integer.parseInt(RossLib.parseXML(filePath, "enemy", imageCount, "width"));
        //height
        int height = Integer.parseInt(RossLib.parseXML(filePath, "enemy", imageCount, "height"));
        Random rn = new Random();
        int randX = rn.nextInt(BobertPanel.floor.hitBox.width)+BobertPanel.floor.hitBox.x;
        hitBox = new Rectangle(randX, defaultY,
                width, height);
        drawBox = new Rectangle(randX-drawHitOffset, defaultY-drawHitOffset,
                width+drawHitOffset*2, height+drawHitOffset*1);
        // defaultHeight-drawHitOffset*1 is only multiplied by 1 because the
        // feet need to touch the floor otherwise it doesn't make sense.
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
    
    /**
     * @param currentGraphics2DContext -> the parameter passed by paint()
     * @param floorX -> the current x-coordinate of the floor
     */
    public void draw(Graphics2D currentGraphics2DContext, int floorX) {
        currentGraphics2DContext.drawImage(this.getImage(), 
                floorX+this.drawBox.x, this.drawBox.y,
                this.drawBox.width, this.drawBox.height, 
                null);
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
