/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.text.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
/**
 *
 * @author r.anderson8
 */
public class Projectile {
    
    static final String defaultPathStem = "resources/projectiles/";
    static final int defaultX = 0;
    static final int defaultY = 0;
    static final int defaultWidth  = 40;
    static final int defaultHeight = 40;
    static ArrayList<Projectile>availableProjectiles = new ArrayList<Projectile>();
    
    static int numProjectileImages = 2;
    static int projectileImageCount = 0;    
    
    private Image image;
    public Rectangle hitBox;
    public String name;
    public boolean destroyed;
    public boolean movingRight;
    public boolean movingDoubleSpeed;
    public int vertVelocity;
    // Should be just high enough of a bounce that there's a possibility it could
    // bounce too high.
    public static final int vertVelocityBounce = (int) (Enemy.defaultHeight*-0.12);

    
//    public Projectile() {
//        Projectile newProj = availableProjectiles.get(projectileImageCount);
//        projectileImageCount++;
//        if (projectileImageCount >= numProjectileImages) {
//            projectileImageCount = 0;
//        }
//        image = newProj.image;
//        hitBox = newProj.hitBox;
//        vertVelocity = vertVelocityBounce;
//    }
    public Projectile() {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(
                    new File("resources/projectiles/projectile_data.xml") );
            
            // normalize text representation
            doc.getDocumentElement().normalize ();
            
            
            NodeList listOfProjectiles = doc.getElementsByTagName("projectile");
            int totalProjectiles = listOfProjectiles.getLength();
            numProjectileImages = totalProjectiles;
            
//            for(int i=0; i<totalProjectiles; i++){
                
                
                Node projectileNode = listOfProjectiles.item(projectileImageCount);
                if(projectileNode.getNodeType() == Node.ELEMENT_NODE){
                    
                    
                    Element projectileElement = (Element)projectileNode;
                    //-------
                    NodeList nameList = projectileElement.getElementsByTagName("name");
                    Element nameElement = (Element)nameList.item(0);
                    
                    NodeList textNAMEList = nameElement.getChildNodes();
                    name =  ((Node)textNAMEList.item(0)).getNodeValue().trim();
                    //-------
                    NodeList locationList = projectileElement.getElementsByTagName("location");
                    Element locationElement = (Element)locationList.item(0);
                    
                    NodeList textLOCATIONList = locationElement.getChildNodes();
                    String parsedImagePath = ((Node)textLOCATIONList.item(0)).getNodeValue().trim();
                    //-------
                    NodeList widthList = projectileElement.getElementsByTagName("width");
                    Element widthElement = (Element)widthList.item(0);
                    
                    NodeList textWIDTHList = widthElement.getChildNodes();
                    String parsedImageWidthString = ((Node)textWIDTHList.item(0)).getNodeValue().trim();
                    int parsedImageWidth = Integer.parseInt(parsedImageWidthString);
                    //-------
                    NodeList heightList = projectileElement.getElementsByTagName("height");
                    Element heightElement = (Element)heightList.item(0);
                    
                    NodeList textHEIGHTList = heightElement.getChildNodes();
                    String parsedImageHeightString = ((Node)textHEIGHTList.item(0)).getNodeValue().trim();
                    int parsedImageHeight = Integer.parseInt(parsedImageHeightString);
                    //------
                    /*
                     * Finally using the information parsed from the xml file.
                     * IMPORTANT: The info is parsed every time a new projectile
                     * is thrown, NOT when it is compiled. Because of this, you
                     * can draw things, change size, etc, while the game is running
                     * without having to restart the whole game. FUCKING AWESOME.
                     */
                    ImageIcon ii = new ImageIcon(parsedImagePath);
                    image = ii.getImage();
                    hitBox = new Rectangle(defaultX, defaultY,
                            parsedImageWidth, parsedImageHeight);
                    projectileImageCount++;
                    if (projectileImageCount >= numProjectileImages) {
                        projectileImageCount = 0;
                    }
                }//end if
              
              
                
//            }//end for (i)
            
            
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                    + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
            
        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
            
        }catch (Throwable t) {
            t.printStackTrace ();
        }
    }
    
    /*
     * @return -> Returns an ArrayList of all available projectiles, taking
     *            its information from the xml file "projectile_data.xml" inside
     *            of "Game/resources/proctiles/"
     */
    public static void parseXML() {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(
                    new File("resources/projectiles/projectile_data.xml") );
            
            // normalize text representation
//            doc.getDocumentElement ().normalize ();
            System.out.println ("Root element of the doc is " +
                    doc.getDocumentElement().getNodeName());
            
            
            NodeList listOfProjectiles = doc.getElementsByTagName("projectile");
            int totalProjectiles = listOfProjectiles.getLength();
            numProjectileImages = totalProjectiles;
            System.out.println("Total no of projectile images: " + totalProjectiles);
            
            for(int i=0; i<totalProjectiles; i++){
                
                
                Node projectileNode = listOfProjectiles.item(i);
                if(projectileNode.getNodeType() == Node.ELEMENT_NODE){
                    
                    
                    Element projectileElement = (Element)projectileNode;
                    //-------
                    NodeList nameList = projectileElement.getElementsByTagName("name");
                    Element nameElement = (Element)nameList.item(0);
                    
                    NodeList textNAMEList = nameElement.getChildNodes();
                    System.out.println("Name: " +
                            ((Node)textNAMEList.item(0)).getNodeValue().trim());
                    //-------
                    NodeList locationList = projectileElement.getElementsByTagName("location");
                    Element locationElement = (Element)locationList.item(0);
                    
                    NodeList textLOCATIONList = locationElement.getChildNodes();
                    String parsedImagePath = ((Node)textLOCATIONList.item(0)).getNodeValue().trim();
                    System.out.println("Location: " +
                            ((Node)textLOCATIONList.item(0)).getNodeValue().trim());
                    //-------
                    NodeList widthList = projectileElement.getElementsByTagName("width");
                    Element widthElement = (Element)widthList.item(0);
                    
                    NodeList textWIDTHList = widthElement.getChildNodes();
                    String parsedImageWidthString = ((Node)textWIDTHList.item(0)).getNodeValue().trim();
                    int parsedImageWidth = Integer.parseInt(parsedImageWidthString);
                    System.out.println("Width: " +
                            ((Node)textWIDTHList.item(0)).getNodeValue().trim());
                    //-------
                    NodeList heightList = projectileElement.getElementsByTagName("height");
                    Element heightElement = (Element)heightList.item(0);
                    
                    NodeList textHEIGHTList = heightElement.getChildNodes();
                    String parsedImageHeightString = ((Node)textHEIGHTList.item(0)).getNodeValue().trim();
                    int parsedImageHeight = Integer.parseInt(parsedImageHeightString);
                    System.out.println("Height: " +
                            ((Node)textHEIGHTList.item(0)).getNodeValue().trim());
                }//end if
                
                
            }//end for (i)
            
            
        }catch (SAXParseException err) {
            System.out.println ("** Parsing error" + ", line "
                    + err.getLineNumber () + ", uri " + err.getSystemId ());
            System.out.println(" " + err.getMessage ());
            
        }catch (SAXException e) {
            Exception x = e.getException ();
            ((x == null) ? e : x).printStackTrace ();
            
        }catch (Throwable t) {
            t.printStackTrace ();
        }
        //</editor-fold>
    }
    
    public Projectile(String imagePath, int x, int y,
            int width, int height) {
        ImageIcon iiP = new ImageIcon(imagePath);
        image = iiP.getImage();
        hitBox = new Rectangle(x, y,
                width, height);
    }
    
    void draw(Graphics2D currentGraphics2DContext) {
        currentGraphics2DContext.drawImage(this.getImage(), this.hitBox.x, this.hitBox.y,
                this.hitBox.width, this.hitBox.height, null);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    
    public void setImage(String path) {
        ImageIcon ii = new ImageIcon(path);
        if (ii.getIconHeight() == 0) { 
            System.out.println("You messed up, the file at "+
                path+" doesn't exist, bro.");
        } else {
            this.image = ii.getImage();
        }
    }
}
