package game;

import interfaces.Drawable;
import java.awt.Rectangle;

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
    public static int defaultMoveSpeed = 1;
    
    public int startX;
    
    public boolean isAlive = true;
    
    public Enemy(Rectangle collisionRect, String imgPath, int howFarCanMoveRightFromLocation) {
        name = "DefaultName";
        setImage(imgPath);
        horizVelocity = defaultMoveSpeed;
        movementDistance = howFarCanMoveRightFromLocation;
        hitBox = new Rectangle(collisionRect);
        startX = collisionRect.x;
        this.initBoxes(collisionRect);
        worldObjectType = WorldObjectType.ENEMY;
        collisionType = CollisionType.IMPASSABLE;
    }
    
}
