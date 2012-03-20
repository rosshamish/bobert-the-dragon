//package game;
///**
// *
// * @author Ross-Desktop
// */
//public class Character {
//    
//    static final String defaultPathStem = "resources/character";
//    static final int defaultX = (int) Main.B_WINDOW_WIDTH / 2;
//    static final int defaultY = (int) Main.B_WINDOW_HEIGHT - BobertPanel.floorCollisionHeight;
//    static final int defaultWidth  = 25;
//    static final int defaultHeight = 25;
//    
//    static Rectangle character;
//    static Image imgChar;
//    static ImageIcon iiChar;
//    static int characterWidth = 120;
//    static final int characterHeightReset = 200;
//    static int characterHeight = characterHeightReset;
//    static int characterHeightCrouch = characterHeightReset / 2;
//    
//    static final int numProjectileImages = 2;
//    static int projectileImageCount = 0;    
//    
//    private Image image;
//    private Rectangle hitBox;
//    public boolean destroyed;
//    public boolean movingRight;
//    public boolean movingDoubleSpeed;
//    public int vertVelocity;
//    public static final int vertVelocityBounce = (int) (defaultHeight*-0.7);
//
//    
//    public Projectile() {
//        try {
//            ImageIcon iiP = new ImageIcon(defaultPathStem + projectileImageCount + ".png");
//            projectileImageCount++;
//            if (projectileImageCount >= numProjectileImages) {
//                projectileImageCount = 0;
//            }
//            image = iiP.getImage();
//        } catch (Exception e) {
//            System.out.println("Exception: "+e.getMessage());
//        }
//        hitBox = new Rectangle(defaultX, defaultY,
//                defaultWidth, defaultHeight);
//    }
//    
//    public Projectile(String imagePath, int x, int y,
//            int width, int height) {
//        ImageIcon iiP = new ImageIcon(imagePath);
//        image = iiP.getImage();
//        hitBox = new Rectangle(x, y,
//                width, height);
//    }
//    
//    void draw(Graphics2D currentGraphics2DContext) {
//        currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
//                this.hitBox.width, this.hitBox.height, null);
//    }
//    
//    public Rectangle getHitBox() {
//        return hitBox;
//    }
//
//    public void setHitBox(Rectangle hitBox) {
//        this.hitBox = hitBox;
//    }
//
//    public Image getImage() {
//        return image;
//    }
//
//    public void setImage(Image image) {
//        this.image = image;
//    }
//    
//    public void setImage(String path) {
//        ImageIcon ii = new ImageIcon(path);
//        if (ii.getIconHeight() == 0) { 
//            System.out.println("You messed up, the file at "+
//                path+" doesn't exist, bro.");
//        } else {
//            this.image = ii.getImage();
//        }
//    }
//}
