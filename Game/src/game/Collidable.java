/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author r.anderson8
 */
public class Collidable {
    
    private Rectangle drawBox;
    static final int defaultDrawHeight = 20;
    static final int defaultDrawWidth = 20;
    
    private Rectangle hitBox;
    static final int defaultCollisionHeight = defaultDrawHeight;
    static final int defaultCollisionWidth = defaultDrawWidth;
    
    private Image image;
    
    public Collidable() {
        System.out.println("You shouldn't call this default constructor bro.");
        System.out.println("It doesn't do anything.\n");
    }
    public Collidable(Rectangle drawRect, Rectangle collisionRect, 
            String imagePath) {
        drawBox = drawRect;
        hitBox = collisionRect;
        ImageIcon ii = new ImageIcon(imagePath);
        image = ii.getImage();
    }
    
    public void draw(Graphics2D currentGraphics2DContext) {
        currentGraphics2DContext.drawImage(this.getImage(), this.drawBox.x, this.drawBox.y,
                this.drawBox.width, this.drawBox.height, null);
    }
    
    public Rectangle getDrawBox() {
        return drawBox;
    }

    public void setDrawBox(Rectangle drawBox) {
        this.drawBox = drawBox;
    }

    public Rectangle getHitBox() {
        return hitBox;
    }

    public void setHitBox(Rectangle hitBox) {
        this.hitBox = hitBox;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
