package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;
import javax.swing.ImageIcon;
import rosslib.RossLib;

/**
 *
 * @author Ross-Desktop
 */
public class Enemy extends Sprite
                   implements Drawable {
    // There is no default x because it will always be randomly calculated
    static String resourcesPath = resourcesPathStem + "enemies/";
    static String dataPath = resourcesPath + "enemy_data.xml";
    
    static int imageCount = 0;
    static int numEnemies = 1;
    
    
    
    public Enemy() {
        String filePath = dataPath;
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
        worldObjectType = WorldObjectType.HOSTILE;
        moveSpeed = Integer.parseInt(RossLib.parseXML(filePath, "enemy", imageCount, "speed"));
        //width
        int width = Integer.parseInt(RossLib.parseXML(filePath, "enemy", imageCount, "width"));
        //height
        int height = Integer.parseInt(RossLib.parseXML(filePath, "enemy", imageCount, "height"));
        Random rn = new Random();
        int randX = rn.nextInt(BobertPanel.floor.hitBox.width)+BobertPanel.floor.hitBox.x;
        
        initBoxes(new Rectangle(randX, BobertPanel.floor.hitBox.y,
                                width, height)
                  );
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
    @Override
    public void draw(Graphics2D currentGraphics2DContext, int floorX) {
        currentGraphics2DContext.drawImage(this.getImage(), 
                floorX+this.drawBox.x, this.drawBox.y,
                this.drawBox.width, this.drawBox.height, 
                null);
    }
    
    @Override
    public void drawDebug(Graphics2D currentGraphics2DContext, int floorX) {
        currentGraphics2DContext.setColor(Color.blue);
        currentGraphics2DContext.draw(drawBox);
        
        currentGraphics2DContext.setColor(Color.red);
        currentGraphics2DContext.draw(hitBox);
    }
}
