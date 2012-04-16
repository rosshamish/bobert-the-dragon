/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import game.Collidable.CollisionType;
import game.WorldObject.WorldObjectType;
import java.awt.Rectangle;
import java.util.ArrayList;
import rosslib.RossLib;

/**
 *
 * @author Ross
 */
public class GameLevel {

    String resourcesPath;
    String levelName;
    WorldObject background;
    Collidable floor;
    ArrayList<Collidable> collidables;
    ArrayList<Enemy> enemies;

    public GameLevel(String _levelName, Camera cam) {
        resourcesPath = "resources/levels/" + _levelName + "/";
        levelName = _levelName;
        String collidablesDataPath = resourcesPath + "collidables/collidable_data.xml";
        String enemyDataPath = resourcesPath + "enemies/enemy_data.xml";
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
