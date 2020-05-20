package prlab.kbunit.config;

import java.io.IOException;

import org.jdom2.JDOMException;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.xml.XMLFileLoader;

/**
 * This {@code Configuration} class provides methods
 * for loading and retrieving a DatabaseConfiguration
 * and for updating the date and time elements for the Runner <br>
 * 
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 *
 * @author Alexander Georgiev, Patrick Pete
 *
 */
public class Configuration {
	
	// Pfad der Web Resource
	private String pathWebResource;
	// contains the information of KBUnit Server
	private static  WebResource resource;
	// File der Resource-Configuration 
	private XMLFileLoader xmlFileLoader;	
	// the date of loading the Runner file
	private String date;
	// the time of loading the Runner file
	private String time;
	
	
	/**
	 * On creation a {@link Variables#CNFG_FILE_PATH}-file 
	 * will be loaded <br>and an instance of 
	 * a DatabaseConfiguration will be built.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public Configuration() {
		try {
		    xmlFileLoader = new XMLFileLoader(Variables.CNFG_FILE_PATH);
			pathWebResource = generateFromXml(Variables.CNFG_NODE_REST + "." 
			    + Variables.CNFG_ELEM_REST_HOST);
			this.loadWebResource();
			this.loadUpdateConfiguration();
		}
		catch(Exception exc) {
			System.out.println("The configuration file could not be loaded.");
		}
	}

	/**
	 * gives the resource-Object
	 * @return WebResource , contains the resource-Object
	 */
	public WebResource getWebResource (){
		return resource;
	}
	
	/**
	 * @return the pathWebResource
	 */
	public String getPathWebResource() {
		return pathWebResource;
	}

	public String generateFromXml(String node) {
		return this.xmlFileLoader.getConfigParameterValue(node);
	}
	
	/**
	 * fills the resource-Attribut
	 */
	public void loadWebResource(){
		Client client = new Client();
		WebResource resource 
		    = client.resource(xmlFileLoader.getConfigParameterValue(
			Variables.CNFG_NODE_REST + "." + Variables.CNFG_ELEM_REST_HOST));
		Configuration.resource =  resource;
	}
	
	
	/** 
	 * @return <b>date</b> given in 
	 * <code>{@linkplain Variables#CNFG_FILE_PATH}</code>
	 * <br>as value of attribute <code>{@linkplain Variables#CNFG_ELEM_DATE}</code>.
	 */
	public String getDate(){
		return date;
	}
	
	/** 
	 * @return <b>time</b> given in 
	 * <code>{@linkplain Variables#CNFG_FILE_PATH}</code>
	 * <br>as value of attribute <code>{@linkplain Variables#CNFG_ELEM_TIME}</code>.
	 */
	public String getTime(){
		return time;
	}
	
	/**
	 * Modifies <b>date</b> and <b>time</b> attributes of a given testclass
	 * Element <code>{@linkplain Variables#CNFG_NODE_UPDATE}</code> <br>
	 * @param date in format: Year-Month-Day
	 * @param time in format: Hours:Minutes:Seconds
	 * 
	 * @see <code>{@linkplain XMLFileLoader#setDateAndTime(String, String)}</code>
	 */
	public void setDateAndTime(String path, String date, String time){
		this.xmlFileLoader.setDateAndTime(date, time);
		this.date = date;
		this.time = time;
	} 
	
	/*
	 * Loads UpdateConfiguration, that is used to determine 
	 * the date and time after which the Test-Cases will be
	 * eventually copied from the client's database.
	 * No test cases, which have been performed before
	 * this point in time, will be copied.
	 */
	private void loadUpdateConfiguration(){
		date = xmlFileLoader.getConfigParameterValue
		    (Variables.CNFG_NODE_UPDATE +"."+ Variables.CNFG_ELEM_DATE);
		time = xmlFileLoader.getConfigParameterValue
			(Variables.CNFG_NODE_UPDATE +"."+ Variables.CNFG_ELEM_TIME);
	}


}
