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
public class Projectile {
    
    static final String defaultPathStem = "resources/projectiles/";
    static final int defaultX = 0;
    static final int defaultY = 0;
    static final int defaultWidth  = 40;
    static final int defaultHeight = 40;
    
    static final int numProjectileImages = 7;
    static int projectileImageCount = 0;    
    
    private Image image;
    public Rectangle hitBox;
    public boolean destroyed;
    public boolean movingRight;
    public boolean movingDoubleSpeed;
    public int vertVelocity;
    // Should be just high enough of a bounce that there's a possibility it could
    // bounce too high.
    public static final int vertVelocityBounce = (int) (Enemy.defaultHeight*-0.12);

    
    public Projectile() {
            try {
                ImageIcon iiP = new ImageIcon(defaultPathStem + projectileImageCount + ".png");
                projectileImageCount++;
                if (projectileImageCount >= numProjectileImages) {
                    projectileImageCount = 0;
                }
                image = iiP.getImage();
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        hitBox = new Rectangle(defaultX, defaultY,
                defaultWidth, defaultHeight);
    }
    
    public Projectile(String imagePath, int x, int y,
            int width, int height) {
        ImageIcon iiP = new ImageIcon(imagePath);
        image = iiP.getImage();
        hitBox = new Rectangle(x, y,
                width, height);
    }
    
    void draw(Graphics2D currentGraphics2DContext) {
        currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
                this.hitBox.width, this.hitBox.height, null);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    public void setImage(String path) {
        ImageIcon ii = new ImageIcon(path);
        if (ii.getIconHeight() == 0) { 
            System.out.println("You messed up, the file at "+
                path+" doesn't exist, bro.");
        } else {
            this.image = ii.getImage();
        }
    }
}
