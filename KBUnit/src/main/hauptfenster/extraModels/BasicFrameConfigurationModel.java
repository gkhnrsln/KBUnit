package main.hauptfenster.extraModels;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

/**
 * BasicFrameConfigurationModel ist ein weiteres Model des 
 * Hauptfensters der Anwendung. 
 * Es realisiert den Anschluss des Konfigurationsdialogs an config.xml.
 * Es werden die Verbindungseinstellungen fuer die Webresource und fuer
 * die Anwenderdokumentation erstellt.
 * In dem Hauptfenster werden JUnit-Tests gekapselt und ueber diese grafische
 * Benutzeroberflaeche via Reflection zur Bearbeitung zur verfuegung gestellt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */
public final class BasicFrameConfigurationModel 
{
    
	// Name der Konfigurationsdatei
    private static String FILENAME = "config.xml";
    // Objekt, welches die Daten zur Konfiguration kapselt
    private Configuration configuration = new Configuration();
    // das KonfigurationsModel-Objekt
    private static BasicFrameConfigurationModel basicFrameConfigurationModel;
    
    // der Konstruktor fuer das einmalige Kreieren eines 
    // BasicFrameConfigurationModel- Objekts (singleton - pattern)
    private BasicFrameConfigurationModel(){}
    
    /**
     * gibt das einzige BasicFrameConfigurationModel - Objekt heraus 
     * (singleton - Pattern).
     * Falls es noch nicht existiert, wird es erst kreiert.
     * @return das einzige BasicFrameConfiguratioModel - Objekt
     * @throws JDOMException, falls der Zugriff auf die Web - Resource 
     *                        nicht geklappt hat
     * @throws IOException, falls der Zugriff auf die Konfigurationsdatei 
     *                      nicht geklappt hat
     */
    public static BasicFrameConfigurationModel getInstance() 
    	throws JDOMException, IOException
    {
        if(basicFrameConfigurationModel == null)
        {
            basicFrameConfigurationModel = new BasicFrameConfigurationModel();
            basicFrameConfigurationModel.loadWebResourceConfiguration(); 
        }    
        return basicFrameConfigurationModel;
    }    
    
    /**
     * gibt das Configuration-Objekt heraus, welches den Ort des Webservers und der
     * Anwenderdokumentation enthaelt
     * @return Ort des Webservers und der Anwenderdokumentation als Configuration-Objekt
     */
    public Configuration getConfiguration()
    {
    	return this.configuration;
    }
    
    /**
     * laedt die Konfigurationsparameter aus der Konfigurationsdatei 
     *
     * @param filename Name der Konfigurationsdatei
     */
    private String getConfigParameterValue(String fullParameterPath) 
        throws JDOMException, IOException
    {
        Document document = this.loadConfigDocument();
        Element paramNode = document.getRootElement();
        String path = fullParameterPath;
        while(path.contains("."))
        {
            int index = path.indexOf(".");
            String childName = path.substring(0, index);
            paramNode = paramNode.getChild(childName);
            path = path.substring(index+1);
        }
        return paramNode.getChildTextNormalize(path);
    }
    
    // holt die Inhalte der Konfigurationsdatei
    private Document loadConfigDocument() 
        throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder();
    	File xmlFile = new File(FILENAME);
        Document document;
        document = (Document) builder.build(xmlFile);
        return document;
    }
    
    // liest den Pfad des REST-Servers aus der Konfigurationsdatei
    private void loadWebResourceConfiguration() throws JDOMException, IOException
    {
    	this.configuration.setPathWebResource(
    		this.getConfigParameterValue("rest.path"));
      	this.configuration.setPathDocumentation(
        	this.getConfigParameterValue("documentation.path"));
    }
 
}
       