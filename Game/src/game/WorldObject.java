package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 *
 * @author Ross
 */
public class WorldObject 
                         implements Drawable {

    public enum WorldObjectType {
        FRIENDLY,
        NEUTRAL,
        FLOOR,
        CHARACTER,
        PROJECTILE,
        HOSTILE
    };
    
    static String resourcesPathStem = "resources/";
    public Rectangle drawBox;
    
    protected Image image;
    public ArrayList<String> imagePaths;
    
    public WorldObject() {
        
    }
    
    public WorldObject(Rectangle drawRect) {
        drawBox = drawRect;
    }
    
    public boolean isInViewOf(Camera cam) {
        if (       // The left side is in the camera
             (drawBox.x >= cam.getX() && drawBox.x <= cam.getX() + cam.getWidth())
                || // The right side is in the camera
             (drawBox.x + drawBox.width >= cam.getX() && drawBox.x + drawBox.width <= cam.getX() + cam.getWidth())
                || // The left side is on the left of the cam, and the right side is on the right of the cam.
             (drawBox.x <= cam.getX() && drawBox.x + drawBox.width >= cam.getX() + cam.getWidth())
            ) {
            return true;
        } else {
            return false;
        }
    }
    
    public int xPositionInCam(Camera cam) {
        return drawBox.x - cam.getX();
    }
    
    public int yPositionInCam(Camera cam) {
        return drawBox.y - cam.getY();
    }
    
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
        currentGraphics2DContext.setColor(Color.green);
        currentGraphics2DContext.draw(drawBox);
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
