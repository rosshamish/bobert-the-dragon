package game;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Ross-Desktop
 */
public class Main {
    
    static final int B_WINDOW_HEIGHT = 700;
    static final int B_WINDOW_BAR_HEIGHT = 26;
    static final int B_WINDOW_CANVAS_HEIGHT = B_WINDOW_HEIGHT - B_WINDOW_BAR_HEIGHT;
    static final int B_WINDOW_WIDTH = 900;
    static BobertFrame bFrame;
    
    public static void main(String[] args) {
        bFrame = new BobertFrame();
        bFrame.setTitle("Bobert the Dragon ... (c) 2012 Bobert-the-Dragon Productions");
        bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bFrame.setSize(B_WINDOW_WIDTH, B_WINDOW_HEIGHT);
        bFrame.setResizable(false);
        bFrame.setLocationRelativeTo(null);
        bFrame.setVisible(true);
    }
}
