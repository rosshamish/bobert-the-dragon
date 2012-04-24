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
            background = new WorldObject(new Rectangle(0, 0,
                    bgWidth, bgHeight));
            String bgImgPath = RossLib.parseXML(backgroundsDataPath, "background", 0, "location");
            background.setImage(bgImgPath);

            collidables = new ArrayList<Collidable>();
            int floorWidth = bgWidth;
            Rectangle floorCollisionRect = new Rectangle(background.drawBox.x, background.drawBox.y + background.drawBox.height - Main.B_WINDOW_BAR_HEIGHT,
                    floorWidth, 100);
            String floorImgPath = Collidable.defaultImgPath;
            floor = new Collidable(floorCollisionRect,
                    WorldObjectType.FLOOR, CollisionType.PLATFORM,
                    floorImgPath);
            floor.name = "Floor, eh?";
            floor.initBoxes(floor.hitBox);
            collidables.add(floor);

            Collidable collidable;
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
                    objType = WorldObjectType.TRIGGER;
                }
                collidable = new Collidable(platCollisRect,
                        objType, collType,
                        collImgPath);
                
                collidable.name = RossLib.parseXML(collidablesDataPath, "collidable", i, "name");
                collidable.initBoxes(collidable.hitBox);
                collidables.add(collidable);
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
                System.out.println("enemy #"+(i)+"\n\timage location: "+imgPath);
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
            
            enemies = new ArrayList<Enemy>();
            
            collidables = new ArrayList<Collidable>();
            background = new WorldObject(new Rectangle(0, 0,
                    (int) (Main.B_WINDOW_WIDTH*1.2), (int) (Main.B_WINDOW_HEIGHT*1.0)));
            background.setImage(WorldObject.defaultImgPath);
            floor = new Collidable(new Rectangle(background.drawBox.x, background.drawBox.y + background.drawBox.height - Main.B_WINDOW_BAR_HEIGHT,
                    background.getWidth(), 100), 
                    WorldObjectType.FLOOR, CollisionType.PLATFORM, WorldObject.defaultImgPath);
            floor.initBoxes(this.floor.hitBox);
            collidables.add(floor);
            
            Collidable triggerEndLevel;
            Rectangle triggerEndLevelRect = new Rectangle(100, background.getHeight()*1/2,
                    50, 100);
            triggerEndLevel = new Collidable(triggerEndLevelRect, WorldObjectType.TRIGGER, CollisionType.PASSABLE,
                    "resources/collidables/triggers/green_flag.png");
            triggerEndLevel.name = "end";
            collidables.add(triggerEndLevel);
            
            Collidable triggerStartLocation;
            Rectangle triggerStartLocationRect = new Rectangle(200, background.getHeight()*1/2,
                    50, 100);
            triggerStartLocation = new Collidable(triggerStartLocationRect, WorldObjectType.TRIGGER, CollisionType.PASSABLE,
                    WorldObject.defaultImgPath);
            triggerStartLocation.name = "start";
            collidables.add(triggerStartLocation);
            
//            Collidable triggerStartLocation;
//            Rectangle triggerStartLocationRect = new Rectangle()
            
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
