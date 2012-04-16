package game;

import interfaces.Drawable;
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
    static int numEnemies = 420;
    
    public boolean alive = true;
    
    
    
    public Enemy(GameLevel level, String _levelName) {
        String enemyDataPath = "resources/levels/"+_levelName+"/enemies/enemy_data.xml";
        String locationRoot = "resources/levels/"+_levelName+"/enemies/";
        numEnemies = RossLib.parseXML(enemyDataPath, "enemy");
        imageCount++;
        if (imageCount >= numEnemies) {
            imageCount = 0;
        }
        //name
        name = RossLib.parseXML(enemyDataPath, "enemy", imageCount, "name");
        //location
        String location = locationRoot + RossLib.parseXML(enemyDataPath, "enemy", imageCount, "location");
        setImage(location);
        worldObjectType = WorldObjectType.HOSTILE;
        collisionType = CollisionType.IMPASSABLE;
        moveSpeed = Integer.parseInt(RossLib.parseXML(enemyDataPath, "enemy", imageCount, "speed"));
        //width
        int width = Integer.parseInt(RossLib.parseXML(enemyDataPath, "enemy", imageCount, "width"));
        //height
        int height = Integer.parseInt(RossLib.parseXML(enemyDataPath, "enemy", imageCount, "height"));
        Random rn = new Random();
        int randX = rn.nextInt(level.floor.hitBox.width)+level.floor.hitBox.x;
        // TODO actually parse a logical y value
        initBoxes(new Rectangle(randX, level.floor.hitBox.y-this.hitBox.height,
                                width, height)
                  );
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
}
