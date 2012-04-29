package game;

import interfaces.Drawable;
import java.awt.Rectangle;
import java.util.ArrayList;

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
    
    public Enemy(Enemy _e) {
        this.hitBox = new Rectangle(_e.hitBox);
        this.drawBox = new Rectangle(_e.drawBox);
        this.futureHitBox = new Rectangle(_e.hitBox);
        this.setImage(_e.getImageLocation());
        this.worldObjectType = _e.worldObjectType;
        this.collisionType = _e.collisionType;
        this.drawHitOffset = _e.drawHitOffset;
        if (_e.name != null) {
            this.name = _e.name;
        }
        this.movementDistance = _e.movementDistance;
        this.startX = _e.startX;
        this.horizVelocity = defaultMoveSpeed;
    }
    
    public Enemy(Rectangle collisionRect, ArrayList<String> imgPaths, int howFarCanMoveRightFromLocation) {
        name = "DefaultName";
        
        horizVelocity = defaultMoveSpeed;
        movementDistance = howFarCanMoveRightFromLocation;
        hitBox = new Rectangle(collisionRect);
        startX = collisionRect.x;
        this.initBoxes(collisionRect);
        worldObjectType = WorldObjectType.ENEMY;
        collisionType = CollisionType.IMPASSABLE;
    }
    
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
