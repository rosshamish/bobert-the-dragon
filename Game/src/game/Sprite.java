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
    
}
