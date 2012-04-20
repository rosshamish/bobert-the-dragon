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

    public String resourcesPath;
    public String levelName;
    public WorldObject background;
    public Collidable floor;
    public ArrayList<Collidable> collidables;
    public ArrayList<Enemy> enemies;
    
    public GameLevel (String _levelName, boolean _newLevel) {
        
       Audio.BACKGROUNDMUSIC.loop();
        
        if (!_newLevel) { // If this is an old level, then load it from its data.
            resourcesPath = "resources/levels/" + _levelName + "/";
            levelName = _levelName;
            String collidablesDataPath = resourcesPath + "collidables/collidables_data.xml";
            String enemiesDataPath = resourcesPath + "enemies/enemies_data.xml";
            String backgroundsDataPath = resourcesPath + "backgrounds/backgrounds_data.xml";
            // This needs to be a jpg because the image file is HUGE and it doesn't
            // need transparency.
            int bgHeight = Integer.parseInt(RossLib.parseXML(backgroundsDataPath, "background", 0, "height"));
            int bgWidth = Integer.parseInt(RossLib.parseXML(backgroundsDataPath, "background", 0, "width"));
            background = new WorldObject(new Rectangle(0, Main.B_WINDOW_HEIGHT - bgHeight,
                    bgWidth, bgHeight));
            String bgImgPath = RossLib.parseXML(backgroundsDataPath, "background", 0, "location");
            background.setImage(bgImgPath);

            collidables = new ArrayList<Collidable>();
            int floorWidth = bgWidth;
            Rectangle floorCollisionRect = new Rectangle(0, Main.B_WINDOW_CANVAS_HEIGHT,
                    floorWidth, 100);
            String floorImgPath = RossLib.parseXML(backgroundsDataPath, "floor", 0, "location");
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
                String collImgPath = RossLib.parseXML(collidablesDataPath, "collidable", i, "location");
                CollisionType collType = Collidable.parseCollisionType(
                        RossLib.parseXML(collidablesDataPath, "collidable", i, "type"));
                platCollisRect = new Rectangle(collX, collY,
                        collWidth, collHeight);
                WorldObjectType objType = WorldObjectType.OBSTACLE;
                if (collType == CollisionType.PLATFORM) {
                    objType = WorldObjectType.PLATFORM;
                } else if (collType == CollisionType.IMPASSABLE) {
                    objType = WorldObjectType.OBSTACLE;
                } else if (collType == CollisionType.PASSABLE) {
                    objType = WorldObjectType.COLLECTABLE;
                }
                platform = new Collidable(platCollisRect,
                        objType, collType,
                        collImgPath);
                platform.name = RossLib.parseXML(collidablesDataPath, "collidable", i, "name");
                platform.initBoxes(platform.hitBox);
                collidables.add(platform);
            }
            //** Enemies
            enemies = new ArrayList<Enemy>();
            int numEnemies = RossLib.parseXML(enemiesDataPath, "enemy");
            if (numEnemies > 0) {
                for (int i = 0; i < numEnemies; i++) {
                int x = Integer.parseInt(RossLib.parseXML(enemiesDataPath, "enemy", i, "x"));
                int y = Integer.parseInt(RossLib.parseXML(enemiesDataPath, "enemy", i, "y"));
                int width = Integer.parseInt(RossLib.parseXML(enemiesDataPath, "enemy", i, "width"));
                int height = Integer.parseInt(RossLib.parseXML(enemiesDataPath, "enemy", i, "height"));
                Rectangle enemCollisRect = new Rectangle(x, y, width, height);
                String imgPath = RossLib.parseXML(enemiesDataPath, "enemy", i, "location");
                int movementDistance = Integer.parseInt(RossLib.parseXML(enemiesDataPath, "enemy", i, "movementDistance"));
                Enemy newEnem = new Enemy(enemCollisRect, imgPath, movementDistance);
                newEnem.initBoxes(enemCollisRect);
                if (movementDistance >= 0) {
                    newEnem.movingRight = true;
                    newEnem.movingLeft = false;
                } else {
                    newEnem.movingRight = false;
                    newEnem.movingLeft = true;
                }
                enemies.add(newEnem);
                }
            }
            
        } else { // This is a new level, ,so create it.
            levelName = _levelName;
            this.background = new WorldObject(new Rectangle(0, 0,
                    (int) (Main.B_WINDOW_WIDTH*1.2), (int) (Main.B_WINDOW_HEIGHT*1.2)));
            this.background.setImage(WorldObject.defaultImgPath);
            this.floor = new Collidable(new Rectangle(0, Main.B_WINDOW_CANVAS_HEIGHT,
                    background.getWidth(), 100), 
                    WorldObjectType.FLOOR, CollisionType.PLATFORM, WorldObject.defaultImgPath);
            this.floor.initBoxes(this.floor.hitBox);
            
            this.collidables = new ArrayList<Collidable>();
            this.enemies = new ArrayList<Enemy>();
            // ONLY DO THIS AT THE VERY END.
            boolean success = RossLib.writeLevelData(this);
            if (!success) {
                System.err.println("writeLevelData failed in GameLevel constructor, with"
                        + "parameters @_levelName: "+_levelName+" and "
                        + "           @_newLevel:  "+_newLevel+".");
            }
        }
    }
}
