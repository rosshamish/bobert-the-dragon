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
    public void moveRightBy(int distanceRight) {
        x += distanceRight;
    }
    
    /**
     * Moves a certain distance to the left.
     * @param distanceLeft 
     */
    public void moveLeftBy(int distanceLeft) {
        x -= distanceLeft;
    }
    
    public void moveVerticallyBy(int distanceDown) {
        y += distanceDown;
    }
    
    public void moveVerticallyTowards(Collidable obj) {
        if (this.getY() + this.getHeight()*0.5 <= obj.middleVertically()) {
            // if this camera's centre is higher on screen than the object's centre
            this.moveVerticallyBy(+3);
        } else {
            this.moveVerticallyBy(-3);
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
