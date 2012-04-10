package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author Ross-Desktop
 */
public abstract class Sprite extends Collidable
                    implements Drawable {
    
    public int vertVelocity;
    public int moveSpeed;
    public boolean isInAir;
    
    public String name;

    // Vars to check the left-right direction of the sprite
    // Which way the character is facing
    public boolean facingRight;
    public boolean facingLeft;
    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
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
