package game;

import interfaces.Drawable;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author Ross-Desktop
 */
public class Enemy extends Sprite
                   implements Drawable {
    // There is no default x because it will always be randomly calculated
    static String resourcesPath = resourcesPathStem + "enemies/";
    static String defaultImgPath = resourcesPath + "0.png";
    
    public int movementDistance = 10;
    public static int defaultMovementDistance = 10;
    public static int defaultMoveSpeed = 7;
    
    public boolean isAlive = true;
    
    
    
    public Enemy(Rectangle collisionRect, String imgPath, int howFarCanMoveRightFromLocation) {
        name = "DefaultName";
        setImage(defaultImgPath);
        moveSpeed = defaultMoveSpeed;
        movementDistance = howFarCanMoveRightFromLocation;
        hitBox = new Rectangle(collisionRect);
        this.initBoxes(collisionRect);
        worldObjectType = WorldObjectType.ENEMY;
        collisionType = CollisionType.IMPASSABLE;
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
