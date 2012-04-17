package editor;

import game.Camera;
import game.GameLevel;
import game.Main;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import javax.swing.JPanel;

/**
 *
 * @author Ross-Desktop
 */
public class EditPanel extends JPanel
                       implements Runnable, KeyListener, MouseListener, MouseMotionListener {
    static EditFrame eFrame;
    
    public static boolean gameRunning;
    public static Thread gameThread;
    static boolean objectsDefined = false;
    
    public static GameLevel level;
    public static Camera editCam;
    
    public static int mouseX;
    public static int mouseY;
    
    public EditPanel(EditFrame frame) {
        setBackground(new Color(200, 200, 200));
        this.setDoubleBuffered(true);
        // Need to assign the BobertFrame to a static variable so that we 
        // can add Listeners to it in addNotify(). Explanation is in addNotify().
        eFrame = frame;
        
        defineObjects();
    }
    
    public static void defineObjects() {
        editCam = new Camera(0, 0,
                Main.B_WINDOW_WIDTH, Main.B_WINDOW_HEIGHT);
        
        level = new GameLevel("Cabs");
        
        gameRunning = true;
        objectsDefined = true;
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        gameThread = new Thread(this);
        gameThread.start();
        eFrame.addKeyListener(this);
        eFrame.addMouseListener(this);
        eFrame.addMouseMotionListener(this);
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        if (objectsDefined) {
            
            if (level.background != null) {
                level.background.draw(g2d, editCam);
            }
            g2d.setColor(Color.black);
            g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            g2d.drawString("Mouse X: "+String.valueOf(mouseX), 10, 10);
            g2d.drawString("Mouse Y: "+String.valueOf(mouseY), 10, 20);
            
            
        }
    }
    
    @Override
    public void run() {
        while (gameRunning) {
            repaint();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        // Is this unnecessary? Not sure.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Not implemented yet.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not implemented yet.
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
