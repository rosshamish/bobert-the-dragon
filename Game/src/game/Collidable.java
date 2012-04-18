package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author r.anderson8
 */
public class Collidable extends WorldObject 
                                 implements Drawable {
    public enum CollisionType {
        PASSABLE,
        IMPASSABLE,
        PLATFORM
    };
    public static String resourcesPath = resourcesPathStem + "collidables/";
    public static String dataPath = resourcesPath + "collidables_data.xml";
    
    public Rectangle hitBox;
    public Rectangle futureHitBox; // For collision detection
    public CollisionType collisionType = CollisionType.PASSABLE;
    public int drawHitOffset = 5;
    protected WorldObjectType worldObjectType;
    
    public Collidable() {
        this.hitBox = new Rectangle();
        this.drawBox = hitBox;
        this.futureHitBox = hitBox;
        this.worldObjectType = WorldObjectType.NEUTRAL;
        this.collisionType = CollisionType.IMPASSABLE;
    }
    
    public Collidable(Rectangle collisionRect, WorldObjectType objType, CollisionType colType, String imageLocation) {
        this.hitBox = new Rectangle(collisionRect);
        this.drawBox = new Rectangle(hitBox);
        this.futureHitBox = new Rectangle(hitBox);
        this.worldObjectType = objType;
        this.collisionType = colType;
        this.setImage(imageLocation);
    }
    
    public Collidable(Rectangle collisionRect, WorldObjectType objType, CollisionType colType, ArrayList<String> imageLocations) {
        this.hitBox = collisionRect;
        this.drawBox = hitBox;
        this.futureHitBox = hitBox;
        this.worldObjectType = objType;
        this.collisionType = colType;
        this.imagePaths = imageLocations;
    }
    
    boolean isAbove(Collidable obj) {
        if (this.isHigherThan(obj)) {
            if ( (this.leftEdge() <= obj.rightEdge() && this.leftEdge() >= obj.leftEdge())
                    || this.rightEdge() >= obj.leftEdge() && this.rightEdge() <= obj.rightEdge()
                    || this.rightEdge() >= obj.rightEdge() && this.leftEdge() <= obj.leftEdge()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isHigherThan(Collidable obj) {
        if (this.bottomEdge() <= obj.topEdge()) {
            return true;
        } else {
            return false;
        }
    }
    
    public int topEdge() {
        return this.hitBox.y;
    }
    public int bottomEdge() {
        return this.hitBox.y+this.hitBox.height;
    }
    public int leftEdge() {
        return hitBox.x;
    }
    public int rightEdge() {
        return hitBox.x+hitBox.width;
    }
    public int middleVertically() {
        return (int) (hitBox.y + hitBox.height*0.5);
    }
    public int middleHorizontally() {
        return (int) (hitBox.x + hitBox.width*0.5);
    }
    
    /**
     * Uses the current hit box 
     * @param obj -> Object to compare with.
     * @return -> Whether or not the current hit boxes are colliding.
     */
    boolean isCollidingWith(Collidable obj) {
        if (obj.collisionType == CollisionType.PASSABLE) {
            return false;
        } else if (obj.collisionType == CollisionType.IMPASSABLE) {
            if (this.hitBox.intersects(obj.hitBox)) {
                return true;
            }
        } else if (obj.collisionType == CollisionType.PLATFORM) {
            if (this.isAbove(obj)) {
                if (this.hitBox.intersects(obj.hitBox)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Uses the future hit box instead of the current one
     * @param obj
     * @return -> Whether or not this will collide with the object in the next frame.
     */
    boolean willCollideWith(Collidable obj) {
        if (obj.collisionType == CollisionType.PASSABLE) {
            return false;
        } else if (obj.collisionType == CollisionType.IMPASSABLE) {
            if (this.futureHitBox.intersects(obj.hitBox)) {
                return true;
            }
        } else if (obj.collisionType == CollisionType.PLATFORM) {
            if (this.isAbove(obj)) {
                if (this.futureHitBox.intersects(obj.hitBox)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Sets the x value of the hit box, and updates the draw box accordingly.
     * @param newX -> The new x value of the collision box.
     */
    public void setX(int newX) {
        hitBox.x = newX;
        if (this.worldObjectType == null) {
            this.worldObjectType = WorldObjectType.NEUTRAL;
        }
        if (this.worldObjectType == WorldObjectType.FRIENDLY) {
            drawBox.x = hitBox.x + this.drawHitOffset;
            return;
        }
        if (this.worldObjectType == WorldObjectType.HOSTILE || 
                this.worldObjectType == WorldObjectType.PROJECTILE) {
            drawBox.x = hitBox.x - this.drawHitOffset;
            return;
        }
        if (this.worldObjectType == WorldObjectType.CHARACTER || 
                this.worldObjectType == WorldObjectType.NEUTRAL) {
            drawBox.x = hitBox.x;
            return;
        }
    }
    
    /**
     * Sets the y value of the hit box, and updates the draw box accordingly.
     * @param newY -> The new y value of the collision box.
     */
    public void setY(int newY) {
        hitBox.y = newY;
        if (this.worldObjectType == null) {
            this.worldObjectType = WorldObjectType.NEUTRAL;
        }
        if (this.worldObjectType == WorldObjectType.FRIENDLY) {
            drawBox.y = hitBox.y + this.drawHitOffset;
            return;
        }
        if (this.worldObjectType == WorldObjectType.HOSTILE || 
                this.worldObjectType == WorldObjectType.PROJECTILE) {
            drawBox.y = hitBox.y - this.drawHitOffset;
            return;
        }
        if (this.worldObjectType == WorldObjectType.CHARACTER || 
                this.worldObjectType == WorldObjectType.NEUTRAL) {
            drawBox.y = hitBox.y;
            return;
        }
    }
    
    /**
     * Moves a certain distance to the right.
     * @param distanceRight 
     */
    public void moveRightBy(int distanceRight) {
        hitBox.x += distanceRight;
        drawBox.x += distanceRight;
    }
    
    /**
     * Moves a certain distance to the left.
     * @param distanceLeft 
     */
    public void moveLeftBy(int distanceLeft) {
        hitBox.x -= distanceLeft;
        drawBox.x -= distanceLeft;
    }
    /**
     * Moves the object a certain distance up or down.
     * @param distanceDown -> the distance to move. Positive indicates motion DOWN,
     *                    Negative indicates motion UP.
     */
    public void moveVerticallyBy(int distanceDown) {
        hitBox.y += distanceDown;
        drawBox.y += distanceDown;
    }
    
    public Rectangle hitBoxInCam(Camera _cam) {
        Rectangle inCam = new Rectangle(this.xPositionInCam(_cam), this.yPositionInCam(_cam),
                this.hitBox.width, this.hitBox.height);
        return inCam;
    }
    
    /**
     * Initializes the collision and drawing rectangles.
     * @param x -> X position in the game world.
     * @param y -> Y position in the game world.
     * @param width -> Width
     * @param height -> Height
     * @param drawHitOffset -> The offset between the drawn object and the rectangle
     *                         that will be considered a collision.
     * @param SpriteType -> Whether the sprite is friendly, neutral, character, projectile or hostile. This is
     *                      because hostile sprites will have slightly larger 
     *                      collision boxes than their drawn size, and friendly sprites
     *                      will have slightly smaller collision boxes than their
     *                      drawn size. Neutral and character sprites will have
     *                      equivalent of both.
     */
    void initBoxes(Rectangle collisionBox) {
        hitBox = new Rectangle(collisionBox);
        futureHitBox = new Rectangle(hitBox);
        if (this.worldObjectType == null) {
            this.worldObjectType = WorldObjectType.NEUTRAL;
        }
        if (this.worldObjectType == WorldObjectType.FRIENDLY || 
                this.worldObjectType == WorldObjectType.PROJECTILE) {
            drawBox.x = hitBox.x + this.drawHitOffset;
            drawBox.y = hitBox.y + this.drawHitOffset;
            drawBox.width = hitBox.width - (this.drawHitOffset*2); // Subtract 1 for each side of the box.
            drawBox.height = hitBox.height - (this.drawHitOffset); // The feet need to touch ground.
            return;
        }
        if (this.worldObjectType == WorldObjectType.HOSTILE) {
            drawBox.x = hitBox.x - this.drawHitOffset;
            drawBox.y = hitBox.y - this.drawHitOffset;
            drawBox.width = hitBox.width + (this.drawHitOffset*2); // Subtract 1 for each side of the box.
            drawBox.height = hitBox.height + (this.drawHitOffset); // The feet need to touch ground.
            return;
        }
        if (this.worldObjectType == WorldObjectType.CHARACTER || 
                this.worldObjectType == WorldObjectType.NEUTRAL) {
            // Just return. The boxes are the same for each of these types.
            drawBox = new Rectangle(hitBox);
            return;
        }
        if (this.worldObjectType == WorldObjectType.FLOOR) {
            drawBox.x = hitBox.x;
            drawBox.y = hitBox.y - drawHitOffset;
            drawBox.width = hitBox.width;
            drawBox.height = hitBox.height + drawHitOffset;
            return;
        }
        
    }
    
    void updateFutureHitBox() {
        this.futureHitBox = new Rectangle(this.hitBox);
    }
    int updateDefaultProjectileX() {
        return this.hitBox.x;
    }
    int updateDefaultProjectileY() {
        return this.hitBox.y;
    }
    
    static CollisionType parseCollisionType(String _type) {
        if (_type.equalsIgnoreCase("passable")) {
            return CollisionType.PASSABLE;
        } else if (_type.equalsIgnoreCase("impassable")) {
            return CollisionType.IMPASSABLE;
        } else if (_type.equalsIgnoreCase("platform")) {
            return CollisionType.PLATFORM;
        } else {
            return CollisionType.PASSABLE;
        }
    }
    
    /**
     * @param currentGraphics2DContext -> the parameter passed by paint()
     * @param floorX -> the current x-coordinate of the floor
     */
    @Override
    public void draw(Graphics2D currentGraphics2DContext, Camera cam) {
        // Check to see if this object is inside the camera's view
        if (this.isInViewOf(cam)) {
            currentGraphics2DContext.drawImage(this.getImage(),
                    this.xPositionInCam(cam), this.yPositionInCam(cam),
                    this.drawBox.width, this.drawBox.height,
                    null);
        }
    }
    
    @Override
    public void drawDebug(Graphics2D currentGraphics2DContext, Camera cam) {
        if (this.isInViewOf(cam)) {
            currentGraphics2DContext.setColor(Color.blue);
            currentGraphics2DContext.drawRect(this.xPositionInCam(cam), this.yPositionInCam(cam), 
                    this.drawBox.width, this.drawBox.height);
            currentGraphics2DContext.setColor(Color.black);
            currentGraphics2DContext.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
            if (this.name != null) {
                currentGraphics2DContext.drawString(this.name, xPositionInCam(cam), yPositionInCam(cam));
            } else {
                currentGraphics2DContext.drawString("nullName", xPositionInCam(cam), yPositionInCam(cam));
            }

            currentGraphics2DContext.setColor(Color.red);
            currentGraphics2DContext.drawRect(this.xPositionInCam(cam), this.yPositionInCam(cam), 
                    this.hitBox.width, this.hitBox.height);
        }
    }
    
    
}
