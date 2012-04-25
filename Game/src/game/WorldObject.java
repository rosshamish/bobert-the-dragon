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
        OBSTACLE,
        PLATFORM,
        FLOOR,
        CHARACTER,
        PROJECTILE,
        ENEMY,
        COLLECTABLE,
        TRIGGER
    };
    
    static String resourcesPathStem = "resources/";
    public static String defaultImgPath = "resources/default.jpg";
    public Rectangle drawBox;
    
    public String name;
    protected Image image;
    protected String imageLocation;
    public ArrayList<String> imagePaths;
    
    public WorldObject() {
        
    }
    
    public WorldObject(Rectangle drawRect) {
        drawBox = drawRect;
    }
    
    public static WorldObjectType parseWorldObjectType(String _type) {
        if (_type.equalsIgnoreCase("Obstacle")) {
            return WorldObjectType.OBSTACLE;
        } else if (_type.equalsIgnoreCase("Platform")) {
            return WorldObjectType.PLATFORM;
        } else if (_type.equalsIgnoreCase("Floor")) {
            return WorldObjectType.FLOOR;
        } else if (_type.equalsIgnoreCase("Character")) {
            return WorldObjectType.CHARACTER;
        } else if (_type.equalsIgnoreCase("Projectile")) {
            return WorldObjectType.PROJECTILE;
        } else if (_type.equalsIgnoreCase("Enemy")) {
            return WorldObjectType.ENEMY;
        } else if (_type.equalsIgnoreCase("Collectable")) {
            return WorldObjectType.COLLECTABLE;
        } else if (_type.equalsIgnoreCase("Trigger")) {
            return WorldObjectType.TRIGGER;
        } else {
            return WorldObjectType.OBSTACLE;
        }
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
        if (isInViewOf(cam)) {
            currentGraphics2DContext.drawImage(getImage(),
                    xPositionInCam(cam), yPositionInCam(cam),
                    drawBox.width, drawBox.height,
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
    
    public void setImage(String _path) {
        ImageIcon ii = new ImageIcon(_path);
        if (ii.getIconHeight() == 0) { 
            System.out.println("You messed up, the file at "+
                _path+" doesn't exist, bro.");
        } else {
            imageLocation = _path;
            this.image = ii.getImage();
        }
    }
    
    public String getImageLocation() {
        if (imageLocation != null) {
            return imageLocation;
        } else {
            return "";
        }
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "";
        }
    }
    
    public int getWidth() {
        return drawBox.width;
    }
    
    public int getHeight() {
        return drawBox.height;
    }
    
    
}
