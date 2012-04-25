package game;

import interfaces.Drawable;

/**
 *
 * @author Ross-Desktop
 */
public abstract class Sprite extends Collidable
                    implements Drawable {
    
    public int vertVelocity = 0;
    public int horizVelocity = 0;
    public boolean isInAir;
    
    // Vars to check the left-right direction of the sprite
    // Which way the character is facing
    public boolean facingRight;
    public boolean facingLeft;
    // Which way the character is moving
    public boolean movingRight;
    public boolean movingLeft;
    
    public void switchHorizontalDirection() {
        if (movingRight) {
            movingRight = false;
            movingLeft = true;
        } else if (movingLeft) {
            movingLeft = false;
            movingRight = true;
        }
    }
    
}
