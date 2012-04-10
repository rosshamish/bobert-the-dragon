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
    
    @Override
    public void draw(Graphics2D currentGraphics2DContext, int floorX) {
        currentGraphics2DContext.drawImage(this.getImage(), 
                floorX+this.drawBox.x, this.drawBox.y,
                this.drawBox.width, this.drawBox.height, 
                null);
    }
    
    @Override
    public void drawDebug(Graphics2D currentGraphics2DContext, int floorX) {
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
