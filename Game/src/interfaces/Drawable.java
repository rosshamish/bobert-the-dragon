package interfaces;

import game.Camera;
import java.awt.Graphics2D;

/**
 *
 * @author Ross
 */
public interface Drawable {
    void draw(Graphics2D currentGraphics2DContext, Camera cam);
    void drawDebug(Graphics2D currentGraphics2DContext, Camera cam);
}
