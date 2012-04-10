package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

/**
 *
 * @author r.anderson8
 */
public class Collidable extends WorldObject 
                                 implements Drawable {
    public Rectangle hitBox;
    public Rectangle futureHitBox; // For collision detection
    public int drawHitOffset = 5;
    protected WorldObjectType worldObjectType;
    
    public Collidable() {
        this.hitBox = new Rectangle();
        this.worldObjectType = WorldObjectType.NEUTRAL;
        this.drawBox = hitBox;
    }
    
    public Collidable(Rectangle collisionRect, WorldObjectType objType, String imageLocation) {
        this.hitBox = collisionRect;
        this.worldObjectType = objType;
        this.setImage(imageLocation);
    }
    
    public Collidable(Rectangle collisionRect, WorldObjectType objType, ArrayList<String> imageLocations) {
        this.hitBox = collisionRect;
        this.worldObjectType = objType;
        this.imagePaths = imageLocations;
    }
    /**
     * Uses the current hit box 
     * @param object -> Object to compare with.
     * @return -> Whether or not the current hit boxes are colliding.
     */
    boolean isCollidingWith(Collidable object) {
        if (this.hitBox.intersects(object.hitBox)) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * Uses the future hit box instead of the current one
     * @param object
     * @return -> Whether or not this will collide with the object in the next frame.
     */
    boolean willCollideWith(Collidable object) {
        if (this.futureHitBox.intersects(object.hitBox)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Sets the x value of the hit box, and updates the draw box accordingly.
     * @param newX -> The new x value of the collision box.
     */
    void setX(int newX) {
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
    void setY(int newY) {
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
    void moveRightBy(int distanceRight) {
        hitBox.x += distanceRight;
        drawBox.x += distanceRight;
    }
    
    /**
     * Moves a certain distance to the left.
     * @param distanceLeft 
     */
    void moveLeftBy(int distanceLeft) {
        hitBox.x -= distanceLeft;
        drawBox.x -= distanceLeft;
    }
    /**
     * Moves the object a certain distance up or down.
     * @param distanceDown -> the distance to move. Positive indicates motion DOWN,
     *                    Negative indicates motion UP.
     */
    void moveVerticallyBy(int distanceDown) {
        hitBox.y += distanceDown;
        drawBox.y += distanceDown;
    }
    
    /**
     * Initializes the collision and drawing rectangles.
     * @param x -> X position in the game world.
     * @param y -> Y position in the game world.
     * @param width -> Width
     * @param height -> Height
     * @param drawHitOffset -> The offset between the drawn object and the rectangle
     *                         that will be considered a collision.
     * @param SpriteType -> Whether the sprite is friendly, neutral, character or hostile. This is
     *                      because hostile sprites will have slightly larger 
     *                      collision boxes than their drawn size, and friendly sprites
     *                      will have slightly smaller collision boxes than their
     *                      drawn size. Neutral and character sprites will have
     *                      equivalent of both.
     */
    void initBoxes(Rectangle collisionBox) {
        hitBox = collisionBox;
        futureHitBox = hitBox;
        if (this.worldObjectType == null) {
            this.worldObjectType = WorldObjectType.NEUTRAL;
        }
        if (this.worldObjectType == WorldObjectType.FRIENDLY || 
                this.worldObjectType == WorldObjectType.PROJECTILE) {
            drawBox.x = collisionBox.x + this.drawHitOffset;
            drawBox.y = collisionBox.y + this.drawHitOffset;
            drawBox.width = collisionBox.width - (this.drawHitOffset*2); // Subtract 1 for each side of the box.
            drawBox.height = collisionBox.height - (this.drawHitOffset); // The feet need to touch ground.
            return;
        }
        if (this.worldObjectType == WorldObjectType.HOSTILE) {
            drawBox.x = collisionBox.x - this.drawHitOffset;
            drawBox.y = collisionBox.y - this.drawHitOffset;
            drawBox.width = collisionBox.width + (this.drawHitOffset*2); // Subtract 1 for each side of the box.
            drawBox.height = collisionBox.height + (this.drawHitOffset); // The feet need to touch ground.
            return;
        }
        if (this.worldObjectType == WorldObjectType.CHARACTER || 
                this.worldObjectType == WorldObjectType.NEUTRAL) {
            // Just return. The boxes are the same for each of these types.
            drawBox = hitBox;
            return;
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
