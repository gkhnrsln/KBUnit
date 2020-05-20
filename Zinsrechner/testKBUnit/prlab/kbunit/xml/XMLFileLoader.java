package prlab.kbunit.xml;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import prlab.kbunit.enums.Variables;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This {@code XMLFileLoader} class provides methods
 * for .XML-file loading, retrieving the text 
 * values of its child-Elements or Attributes and
 * also updating the {@link Variables#CNFG_ELEM_DATE} and
 * {@link Variables#CNFG_ELEM_TIME} Elements.<br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 *  
 * @author Alexander Georgiev
 *
 */
public class XMLFileLoader {
	
	// contains the xmlFile, which has been loaded
	private File xmlFile;
	// Document-Object, of the loaded xmlFile
	private Document xmlDoc;
	
	/**
	 * On creation a .XML-file with given path will be loaded.
	 * The same .XMl-file will be used <br>for updating the 
	 * {@link Variables#CNFG_ELEM_DATE} and {@link Variables#CNFG_ELEM_TIME} 
	 * Elements via {@link XMLFileLoader#setDateAndTime(String, String)}
	 * 
	 * @param xmlFilePath the path of the .XML-file to be loaded
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public XMLFileLoader(String xmlFilePath) throws JDOMException, IOException {
		this.xmlFile = new File(xmlFilePath);
		this.xmlDoc  = loadXML(this.xmlFile);
	}
	
	/**
     * Returns the child's value of a searched parameter,
     * which path has to be represented in the form of 
     * child-Elements separated by '.' (i.e. database.host),
     * where "host" is child-Element or Attribute of "database".
     * <br>The parameter path might contain any number of nested children.
     * 
     * @param xmlParameterPath the path of the searched parameter
     * @return child's normalized text 
     */
    public String getConfigParameterValue(String xmlParameterPath) {
  
        Element paramNode = xmlDoc.getRootElement();
        String  path      = xmlParameterPath;
        
        while(path.contains(".")){
        	
            int index        = path.indexOf(".");
            String childName = path.substring(0, index);
            paramNode        = paramNode.getChild(childName);
            path             = path.substring(index+1);
        }
        
        return paramNode.getChildTextNormalize(path);
    }
    
    /**
     * returns the attribut of the root element.
     *
     * @String attributname the name of the attribut
     * @return text of the attribut
     */
    public String getAttributValueOfRootElement(String attributname) {  
        Element rootNode = xmlDoc.getRootElement();
        return rootNode.getAttributeValue(attributname);
    }
      
    /**
     * sets new values for {@link Variables#CNFG_ELEM_DATE} and
     * {@link Variables#CNFG_ELEM_TIME}, which are child-Elements
     * of {@link Variables#CNFG_NODE_UPDATE} in the .XML-
     * configuration-file {@link Variables#CNFG_FILE_PATH}
     * @param date to be set
     * @param time to be set
     */
    public void setDateAndTime(String date, String time){
     	Element rootNode  = xmlDoc.getRootElement();
    	Element updateNode = rootNode.getChild(Variables.CNFG_NODE_UPDATE);
    	updateNode.getChild(Variables.CNFG_ELEM_DATE).setText(date);
    	updateNode.getChild(Variables.CNFG_ELEM_TIME).setText(time);
		try {
			new XMLOutputter().output(xmlDoc, new FileWriter(this.xmlFile));
		} 
		catch (Exception e) {
			System.out.println("Unable to allocate and modify: "
		         + Variables.CNFG_FILE_PATH);
			System.exit(1);
		}    	
    }
    
    /**
     * Builds a {@link Document} from the supplied filename. 
     * 
     * @param xmlFile {@code File} to read from
     * @return {@code Document} resultant Document object
     */
    public static Document loadXML(File xmlFile) 
        throws JDOMException, IOException {
	   	Document doc = (Document) new SAXBuilder().build(xmlFile);
		return doc;
    }

	public Document getXmlDoc() {
		return xmlDoc;
	}
}
