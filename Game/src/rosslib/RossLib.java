package rosslib;

import game.Collidable;
import game.Enemy;
import game.GameLevel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author Ross-Desktop
 */
public class RossLib {
    
    /**
     * @param s -> The string to be modified.
     * @return -> Returns string s without any spaces in it.
     */
    
    public static String removeSpaces(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                String a = s.substring(0, i);
                String b = s.substring(i + 1, s.length());
                s = a.concat(b);
            }
        }
        return s;
    }
    
    /**
     * @return -> Asks for input and grabs a line.
     *            returns true if the user enters either a 'y' or a string 
     *            containing "yes",
     *            returns false otherwise.
     */
    public static boolean answerIsYes() {
        String ans;
        Scanner sc = new Scanner(System.in);
        ans = sc.nextLine();

        ans = RossLib.removeSpaces(ans);
        ans.toLowerCase();
        if (ans.contains("yes") || ans.equals("y")) {
            return true;
        } else {
            return false;
        }

    }
    
    /**
     * 
     * XML Functions
     * 
     */
    
    /**
     * Creates a folder with the specified name in the path specified.
     * @param _folderName -> Desired folder name.
     * @param path -> Path leading up to the folder to be created.
     * @return -> false: The folder does not exist after this function call.
     *            true: The folder DOES exist after this function call
     *                  -note: this could mean that it already existed.
     */
    public static boolean createFolder(String _folderName, String path) {
        boolean success = true;
        // Create folder with format "path/_folderName/"
        String totalFolderPath = path + _folderName + "/";
        if (!new File(totalFolderPath).exists()) {
            // If the folder doesn't exist, create it for god's sake.
            success = new File(totalFolderPath).mkdir();
        }
        if (!success) {
            System.err.println("Directory creation error in GameLevel.java with "
                    + "inputted level name \"" + _folderName + "\".");
            return false;
        } else { 
            // If directory "path/_folderName" was created successfully
            return true;
        }
    }
    
    public static boolean createBlankFile(String _fileName, String _path) {
        boolean success = true;
        try {
                // Get the path to the new file
                String dFileName = _path + _fileName;
                File dFile = new File(dFileName);
                if (!dFile.exists()) {
                    // Create the file, since it doesn't exist
                    dFile.createNewFile();
                } else {
                    // Clear the file (delete it, make a new one)
                    dFile.delete();
                    dFile = new File(dFileName);
                    dFile.createNewFile();
                }
            } catch (IOException e) {
                success = false;
                e.printStackTrace();
            }
        return success;
    }
    
    public static boolean writeLevelData(GameLevel _level) {
        String curPath = "resources/levels/";

        if (!RossLib.createFolder(_level.levelName, curPath)) {
            return false;
        }
        String[] resourceTypes = {"backgrounds", "collidables", "enemies"};
        curPath += _level.levelName + "/";
        String levelPath = curPath;
        for (int i = 0; i < resourceTypes.length; i++) {
            if (!RossLib.createFolder(resourceTypes[i], levelPath)) {
                return false;
            }
        }
        // The folders all should exist at this point.
        for (int i = 0; i < resourceTypes.length; i++) {
            curPath = levelPath + resourceTypes[i] + "/";   
            if (!RossLib.createBlankFile(resourceTypes[i]+"_data.xml", curPath)) {
                return false;
            }
            String dFileName = curPath + resourceTypes[i] + "_data.xml";
            String xmlBookName = resourceTypes[i]+"_data";
            
            RossLib.writeXmlHeader(dFileName, xmlBookName);
            
            if (resourceTypes[i].equalsIgnoreCase("backgrounds")) {
                // Backgrounds_data.xml
                if (_level.background != null) {
                    RossLib.writeXmlHeader(dFileName, "background");
                    RossLib.writeXmlTag(dFileName, "name", _level.background.getName());
                    RossLib.writeXmlTag(dFileName, "location", _level.background.getImageLocation());
                    RossLib.writeXmlTag(dFileName, "width", _level.background.getWidth());
                    RossLib.writeXmlTag(dFileName, "height", _level.background.getHeight());
                    RossLib.writeXmlFooter(dFileName, "background");
                }
                
            } else if (resourceTypes[i].equalsIgnoreCase("collidables")) {
                
                if (_level.collidables != null) {
                    for (int coll = 0; coll < _level.collidables.size(); coll++) {
                        Collidable cur = _level.collidables.get(coll);
                        RossLib.writeXmlHeader(dFileName, "collidable");
                        RossLib.writeXmlTag(dFileName, "name", cur.getName());
                        RossLib.writeXmlTag(dFileName, "location", cur.getImageLocation());
                        RossLib.writeXmlTag(dFileName, "x", cur.hitBox.x);
                        RossLib.writeXmlTag(dFileName, "y", cur.hitBox.y);
                        RossLib.writeXmlTag(dFileName, "width", cur.hitBox.width);
                        RossLib.writeXmlTag(dFileName, "height", cur.hitBox.height);
                        RossLib.writeXmlTag(dFileName, "type", cur.collisionType.toString());
                        RossLib.writeXmlFooter(dFileName, "collidable");
                    }
                }
                
            } else if (resourceTypes[i].equalsIgnoreCase("enemies")) {
                if (_level.enemies != null) {
                    for (int enem = 0; enem < _level.enemies.size(); enem++) {
                        Enemy cur = _level.enemies.get(enem);
                        RossLib.writeXmlHeader(dFileName, "enemy");
                        RossLib.writeXmlTag(dFileName, "name", cur.getName());
                        RossLib.writeXmlTag(dFileName, "location", cur.getImageLocation());
                        RossLib.writeXmlTag(dFileName, "x", cur.hitBox.x);
                        RossLib.writeXmlTag(dFileName, "y", cur.hitBox.y);
                        RossLib.writeXmlTag(dFileName, "width", cur.hitBox.width);
                        RossLib.writeXmlTag(dFileName, "height", cur.hitBox.height);
                        RossLib.writeXmlTag(dFileName, "movementDistance", cur.movementDistance);
                        RossLib.writeXmlFooter(dFileName, "enemy");
                    }
                }
            }
            RossLib.writeXmlFooter(dFileName, xmlBookName);
        }


        return true;
    }
    
    public static boolean writeXmlHeader(String _file, String _header) {
        String header = "<"+_header+">";
        // Set up the file writers
        try {
            FileWriter dFileWriter = new FileWriter(_file, true);
            BufferedWriter dBuffWriter = new BufferedWriter(dFileWriter);
            dBuffWriter.newLine();
            dBuffWriter.write(header);
            dBuffWriter.newLine();
            dBuffWriter.newLine();
            dBuffWriter.close();
            dFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean writeXmlFooter(String _file, String _footer) {
        String footer = "</"+_footer+">";
        // Set up the file writers
        try {
            FileWriter dFileWriter = new FileWriter(_file, true);
            BufferedWriter dBuffWriter = new BufferedWriter(dFileWriter);
            dBuffWriter.newLine();
            dBuffWriter.write(footer);
            dBuffWriter.newLine();
            dBuffWriter.newLine();
            dBuffWriter.close();
            dFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static boolean writeXmlTag(String _file, String _tag, int _value) {
        return RossLib.writeXmlTag(_file, _tag, String.valueOf(_value));
    }
    
    public static boolean writeXmlTag(String _file, String _tag, String _value) {
        // Set up the file writers
        String tagOpen = "<"+_tag+"> ";
        String tagClose = " </"+_tag+">";
        try {
            FileWriter dFileWriter = new FileWriter(_file, true);
            BufferedWriter dBuffWriter = new BufferedWriter(dFileWriter);
            dBuffWriter.write(tagOpen);
            dBuffWriter.write(_value);
            dBuffWriter.write(tagClose);
            dBuffWriter.newLine();
            dBuffWriter.close();
            dFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * @param xmlFilePath -> the file path to the XML data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * @param inputHeaderXMLName -> the value of the name field in the block you want eg bobert, tire
     * @param inputElement -> the name of the element whose value should be returned
     * @return -> Returns a String containing the specified value.
     * @laymans -> I'm looking for a value from the XML file at xmlFilePath, 
     *             inside the header inputHeader.
     *             It's in the header whose <name> is inputHeaderXMLName, and 
     *             the element is called inputElement. 
     *             Return me a String of the value that's there.            
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
                Node headerNode = listOfHeaders.item(i);
                if (headerNode.getNodeType() == Node.ELEMENT_NODE) {


                    Element headerElement = (Element) headerNode;
                    //-------
                    NodeList nameList = headerElement.getElementsByTagName("name");
                    Element nameValue = (Element) nameList.item(0);

                    NodeList textNAMEList = nameValue.getChildNodes();
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
        System.err.printf("RossLib error: parseXML(%s, %s, %s, %s)", xmlFilePath, 
                inputHeader, 
                inputHeaderXMLName, inputElement);
        return "";
    }
    
    /**
     * @param xmlFilePath -> the file path to the XML data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * @param inputHeaderNumber -> the value of the header's place in order, counting from 0
     * @param inputElement -> the name of the element whose value should be returned
     * @return -> Returns a String containing the specified value.
     * @laymans -> I'm looking for a value from the XML file at xmlFilePath, 
     *             inside the header inputHeader.
     *             It's in the inputHeaderIndex'th header, and 
     *             the element is called inputElement. 
     *             Return me a String of the value that's there.
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
        System.err.printf("RossLib error: parseXML(%s, %s, %s, %s)", xmlFilePath, 
                inputHeader, 
                ""+inputHeaderIndex, inputElement);
        return "";
    }
    
    /**
     * @param xmlFilePath -> the file path to the XML data
     * @param inputHeader -> the header which the element is under, eg projectile, character
     * @return -> Returns an int representing the numbers of matching headers in the XML file
     * @laymans -> I'm looking for headers from the XML file at xmlFilePath, 
     *             that match the header inputHeader.
     *             Return me the number of headers that match my description.
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
        System.err.printf("RossLib error: parseXML(%s, %s)", xmlFilePath, 
                inputHeader);
        return -1;
    }
}
