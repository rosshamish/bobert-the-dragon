/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import interfaces.Drawable;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import rosslib.RossLib;
/**
 *
 * @author r.anderson8
 */
public class Projectile extends Sprite 
                        implements Drawable {
    
    static String resourcesPath = resourcesPathStem + "projectiles/";
    static String dataPath = resourcesPath + "projectile_data.xml";
    
    static int imageCount = 0;
    static int numImages;
    
    static final int defaultSpeed = 7;
    static ArrayList<Projectile>availableProjectiles = new ArrayList<Projectile>();
    
    public boolean destroyed;
    public boolean movingDoubleSpeed;
    
    
    // Should be just high enough of a bounce that there's a possibility it could
    // bounce too high.
    public static final int vertVelocityBounce = -5;

    public Projectile() {
        String filePath = Projectile.dataPath;        
        imageCount++;
        if (imageCount >= numImages) {
            imageCount = 0;
        }
        //name
        name = RossLib.parseXML(filePath, "projectile", imageCount, "name");
        //location
        String location = RossLib.parseXML(filePath, "projectile", imageCount, "location");
        ImageIcon ii = new ImageIcon(location);
        image = ii.getImage();
        worldObjectType = WorldObjectType.PROJECTILE;
        //width
        int width = Integer.parseInt(RossLib.parseXML(filePath, "projectile", imageCount, "width"));
        //height
        int height = Integer.parseInt(RossLib.parseXML(filePath, "projectile", imageCount, "height"));
        this.moveSpeed = Integer.parseInt(RossLib.parseXML(filePath, "projectile", imageCount, "speed"));
        initBoxes(new Rectangle(0, 0,
                                width, height)
                  );
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
    
}
