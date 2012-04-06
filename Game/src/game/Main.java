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
    
    /*
     * @return -> Returns a String containing the specified value.
     * @param xmlFilePath -> the file path to the xml data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * @param inputHeaderXMLName -> the value of the name field in the block you want eg bobert, tire
     * @param inputElement -> the name of the element whose value should be returned
     *              
     */
    public static String parseXML(String xmlFilePath, 
        String inputHeader, String inputHeaderXMLName, String inputElement) {
        
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(
                    new File(xmlFilePath));

            // normalize text representation
            doc.getDocumentElement().normalize();


            NodeList listOfHeaders = doc.getElementsByTagName(inputHeader);
            int totalHeaders = listOfHeaders.getLength();

            for (int i = 0; i < totalHeaders; i++) {

                System.out.printf("Total %ss: %d\n", inputHeader, totalHeaders);
                Node headerNode = listOfHeaders.item(i);
                if (headerNode.getNodeType() == Node.ELEMENT_NODE) {


                    Element headerElement = (Element) headerNode;
                    //-------
                    NodeList nameList = headerElement.getElementsByTagName("name");
                    Element nameValue = (Element) nameList.item(0);

                    NodeList textNAMEList = nameValue.getChildNodes();
                    System.out.printf("%s name: %s\n", inputHeader,((Node) textNAMEList.item(0)).getNodeValue().trim());
                    if (((Node) textNAMEList.item(0)).getNodeValue().trim().equalsIgnoreCase(inputHeaderXMLName)) {
                        
                        NodeList elementList = headerElement.getElementsByTagName(inputElement);
                        Element elementValue = (Element) elementList.item(0);

                        NodeList textELEMENTList = elementValue.getChildNodes();
                        return ((Node) textELEMENTList.item(0)).getNodeValue().trim();
                    }
                     
                    //-------
                }
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.err.printf("RossLib error: parseXML(%s,%s,%s,%s)", xmlFilePath, 
                inputHeader, 
                inputHeaderXMLName, inputElement);
        return "";
    }
    
    /*
     * @return -> Returns a String containing the specified value.
     * @param xmlFilePath -> the file path to the xml data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * @param inputHeaderNumber -> the value of the header's place in order, counting from 0
     * @param inputElement -> the name of the element whose value should be returned
     *              
     */
    public static String parseXML(String xmlFilePath, 
        String inputHeader, int inputHeaderIndex, String inputElement) {
        
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(
                    new File(xmlFilePath));

            // normalize text representation
            doc.getDocumentElement().normalize();


            NodeList listOfHeaders = doc.getElementsByTagName(inputHeader);
            int totalHeaders = listOfHeaders.getLength();

//            for (int i = 0; i < totalHeaders; i++) {
            if (inputHeaderIndex > totalHeaders) {
                return "";
            }
                System.out.printf("Total %ss: %d\n", inputHeader, totalHeaders);
                Node headerNode = listOfHeaders.item(inputHeaderIndex);
                if (headerNode.getNodeType() == Node.ELEMENT_NODE) {


                    Element headerElement = (Element) headerNode;
                    NodeList elementList = headerElement.getElementsByTagName(inputElement);
                    Element elementValue = (Element) elementList.item(0);

                    NodeList textELEMENTList = elementValue.getChildNodes();
                    String retVal = ((Node) textELEMENTList.item(0)).getNodeValue().trim();
                    if (!retVal.isEmpty()) {
                        return retVal;
                    } else {
                        return "";
                    }
                    
                    //-------
                }//end if
//            }//end for
    
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.err.printf("RossLib error: parseXML(%s,%s,%s,%s)", xmlFilePath, 
                inputHeader, 
                ""+inputHeaderIndex, inputElement);
        return "";
    }
    
    /*
     * @return -> Returns an int representing the numbers of matching headers in the XML file
     * @param xmlFilePath -> the file path to the xml data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * 
     *              
     */
    public static int parseXML(String xmlFilePath, String inputHeader) {
        
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = docBuilder.parse(
                    new File(xmlFilePath));

            // normalize text representation
            doc.getDocumentElement().normalize();


            NodeList listOfHeaders = doc.getElementsByTagName(inputHeader);
            int totalHeaders = listOfHeaders.getLength();
            return totalHeaders;
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.err.printf("RossLib error: parseXML(%s,%s)", xmlFilePath, 
                inputHeader);
        return -1;
    }
}
