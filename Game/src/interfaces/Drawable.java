package interfaces;

import java.awt.Graphics2D;

/**
 *
 * @author Ross
 */
public interface Drawable {
    void draw(Graphics2D currentGraphics2DContext, int floorX);
    void drawDebug(Graphics2D currentGraphics2DContext, int floorX);
}
