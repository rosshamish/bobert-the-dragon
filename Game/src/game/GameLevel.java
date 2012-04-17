package game;

import game.Collidable.CollisionType;
import game.WorldObject.WorldObjectType;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import rosslib.RossLib;

/**
 *
 * @author Ross
 */
public class GameLevel {

    public String resourcesPath;
    public String levelName;
    public WorldObject background;
    public Collidable floor;
    public ArrayList<Collidable> collidables;
    public ArrayList<Enemy> enemies;

    public GameLevel(String _levelName) {
        levelName = _levelName;
        background = new WorldObject();
        background.name = "Tester Background";
        background.drawBox = new Rectangle(0, 0,
                420, 555);
        RossLib.writeLevelData(this);
    }
    
    /*
     * // Create folders for resource types of format "resources/levels/_folderName/resourceType/"
            String[] resourceTypes = {"backgrounds", "collidables", "enemies"};
            for (int i = 0; i < resourceTypes.length; i++) {
                if (!new File(totalFolderPath + resourceTypes[i]).exists()) {
                    success = new File(totalFolderPath + resourceTypes[i]).mkdir();
                    if (!success) {
                        System.err.println("Directory creation error in GameLevel.java with "
                                + "inputted level name \"" + _folderName + "\" while creating folder \""
                                + resourceTypes[i] + "\".");
                    }
                } else { // If the folder exists
                    System.err.println("Directory \"" + totalFolderPath + resourceTypes[i] + "\" already exists!");
                }
                // The folder exists at this point.
                try {
                    // Get the path to the new file
                    String dFileName = totalFolderPath + resourceTypes[i] + "/" + resourceTypes[i] + "_data.xml";
                    File dFile = new File(dFileName);
                    if (!dFile.exists()) {
                        // Create the file, since it doesn't exist
                        dFile.createNewFile();
                        // Set up the file writers
                        FileWriter dFileWriter = new FileWriter(dFileName);
                        BufferedWriter dBuffWriter = new BufferedWriter(dFileWriter);
                        // Write the header
                        String xmlHeader = "<" + resourceTypes[i] + "_data>";
                        dBuffWriter.write(xmlHeader, 0, xmlHeader.length());
                        // Write a few newlines
                        for (int j = 0; j < 3; j++) {
                            dBuffWriter.newLine();
                        }
                        // Write the footer
                        String xmlFooter = "</" + resourceTypes[i] + "_data>";
                        dBuffWriter.write(xmlFooter, 0, xmlFooter.length());

                        // Close stream
                        dBuffWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
     */
    
    public GameLevel(String _levelName, boolean _newLevel) {
        resourcesPath = "resources/levels/" + _levelName + "/";
        levelName = _levelName;
        String collidablesDataPath = resourcesPath + "collidables/collidables_data.xml";
        String enemyDataPath = resourcesPath + "enemies/enemies_data.xml";
        String backgroundsDataPath = resourcesPath + "backgrounds/backgrounds_data.xml";
        // This needs to be a jpg because the image file is HUGE and it doesn't
        // need transparency.
        int bgHeight = Integer.parseInt(RossLib.parseXML(backgroundsDataPath, "background", 0, "height"));
        int bgWidth = Integer.parseInt(RossLib.parseXML(backgroundsDataPath, "background", 0, "width"));
        background = new WorldObject(new Rectangle(0, Main.B_WINDOW_HEIGHT - bgHeight,
                bgWidth, bgHeight));
        String bgImgPath = resourcesPath + RossLib.parseXML(backgroundsDataPath, "background", 0, "location");
        background.setImage(bgImgPath);
        // TODO compress this file.
        // ALWAYS DEFINE THE collidables FIRST
        collidables = new ArrayList<Collidable>();
        int floorWidth = bgWidth;
        Rectangle floorCollisionRect = new Rectangle(0, Main.B_WINDOW_CANVAS_HEIGHT,
                floorWidth, 100);
        String floorImgPath = resourcesPath + RossLib.parseXML(backgroundsDataPath, "floor", 0, "location");
        floor = new Collidable(floorCollisionRect,
                WorldObjectType.FLOOR, CollisionType.IMPASSABLE,
                floorImgPath);
        floor.name = RossLib.parseXML(backgroundsDataPath, "floor", 0, "name");
        floor.initBoxes(floor.hitBox);
        collidables.add(floor);

        Collidable platform;
        Rectangle platCollisRect;
        int numCollidables = RossLib.parseXML(collidablesDataPath, "collidable");
        for (int i = 0; i < numCollidables; i++) {
            int collHeight = Integer.parseInt(RossLib.parseXML(collidablesDataPath, "collidable", i, "height"));
            int collWidth = Integer.parseInt(RossLib.parseXML(collidablesDataPath, "collidable", i, "width"));
            int collX = Integer.parseInt(RossLib.parseXML(collidablesDataPath, "collidable", i, "x"));
            int collY = Integer.parseInt(RossLib.parseXML(collidablesDataPath, "collidable", i, "y"));
            String collImgPath = resourcesPath + RossLib.parseXML(collidablesDataPath, "collidable", i, "location");
            CollisionType collType = Collidable.parseCollisionType(
                    RossLib.parseXML(collidablesDataPath, "collidable", i, "type"));
            platCollisRect = new Rectangle(collX, collY,
                    collWidth, collHeight);
            platform = new Collidable(platCollisRect,
                    WorldObjectType.NEUTRAL, collType,
                    collImgPath);
            platform.name = RossLib.parseXML(collidablesDataPath, "collidable", i, "name");
            platform.initBoxes(platform.hitBox);
            collidables.add(platform);
        }
        //** Enemies
        enemies = new ArrayList<Enemy>();
        int numEnemies = RossLib.parseXML(enemyDataPath, "enemy");
        for (int i = 0; i < numEnemies; i++) {
            enemies.add(new Enemy(this, _levelName));
        }
    }
}
