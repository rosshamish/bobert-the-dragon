package game;
/**
 *
 * @author Ross-Desktop
 */
public class Camera {
    private int x;
    private int y;
    private int width;
    private int height;
    public static int scrollFactor = 2;
    
    public Camera(int inputX, int inputY, int inputWidth, int inputHeight) {
        x = inputX;
        y = inputY;
        width = inputWidth;
        height = inputHeight;
    }
    
    /**
     * Moves a certain distance to the right.
     * @param distanceRight 
     */
    void moveRightBy(int distanceRight) {
        x += distanceRight;
    }
    
    /**
     * Moves a certain distance to the left.
     * @param distanceLeft 
     */
    void moveLeftBy(int distanceLeft) {
        x -= distanceLeft;
    }
    
    void moveVerticallyBy(int distanceDown) {
        y += distanceDown;
    }
    
    void moveVerticallyTowards(Collidable obj) {
        if (this.getY() + this.getHeight()*0.5 <= obj.hitBox.y + obj.hitBox.height*0.5) {
            // if this camera's centre is higher on screen than the object's centre
            this.moveVerticallyBy(-2);
        } else {
            this.moveVerticallyBy(+2);
        }
    }
    
    public void setX(int inputX) {
        x = inputX;
    }
    
    public void setY(int inputY) {
        y = inputY;
    }
    
    public void setWidth(int inputWidth) {
        width = inputWidth;
    }
    
    public void setHeight(int inputHeight) {
        height = inputHeight;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
