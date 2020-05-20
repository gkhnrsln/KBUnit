package main.hauptfenster.extraModels;

/**
 * Daten fuer die Konfiguration der WebResource und der Dokumentation
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */ 
public class Configuration 
{

	// Pfad der Web Resource
	private String pathWebResource;
	// Pfad der Anwenderdokumentation
	private String pathDocumentation;
	// Dateiname der Anwenderdokumentation
	private String nameDocumentation;
	
	/**
	 * gibt den Pfad der WebResource heraus
	 * @return String , welcher den Pfad der WebResource enthaelt
	 */
	public String getPathWebResource()
	{
		return this.pathWebResource;
	}
	
	/**
	 * setzt den Pfad der WebResource 
	 * @param pathWebResource, String , welcher den neuen Pfad der 
	 *                         WebResource enthaelt
	 */
	public void setPathWebResource(String pathWebResource) 
	{
		this.pathWebResource = pathWebResource;
	}
	
	/**
	 * gibt den Pfad der Anwenderdokumentation heraus
	 * @return String, welcher den Pfad der Anwenderdokumentation enthaelt
	 */
	public String getPathDocumentation()
	{
		return this.pathDocumentation;
	}
	
	/**
	 * setzt den Pfad der Anwenderdokumentation 
	 * @param pathDocumentation String, welcher den neuen Pfad der 
	 *                          Anwenderdokumentation enthaelt
	 */
	public void setPathDocumentation(String pathDocumentation) 
	{
		this.pathDocumentation = pathDocumentation;
	}

	/**
	 * gibt den Namen der Anwenderdokumentation heraus
	 * @return String, welcher den Namen der Anwenderdokumentation enthaelt
	 */
	public String getNameDocumentation() 
	{
		return nameDocumentation;
	}

	/**
	 * setzt den Namen der Anwenderdokumentation 
	 * @param nameDocumentation String, welcher den neuen Namen der 
	 *                          Anwenderdokumentation enthaelt
	 */
	public void setNameDocumentation(String nameDocumentation) 
	{
		this.nameDocumentation = nameDocumentation;
	}	
	
}
