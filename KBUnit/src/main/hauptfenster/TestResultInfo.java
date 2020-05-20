package main.hauptfenster;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.sql.Timestamp;
import java.util.*;

/**
 * TestResultInfo speichert die Informationen einer Testfallkonfiguration.<br>
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestResultInfo {
	
	/** Test wurde erfolgreich durchlaufen */
	public static final int TESTPASSED = 0;
	/** Test ist fehlgeschlagen */
	public static final int TESTFAILED = 1;
	/** Test wurde abgebrochen */
	public static final int TESTABORTED = 2;
	/** Test wurde nicht gefunden */
	public static final int TESTNOTFOUND = 3;
	/** Test wurde durch Assumption abgebrochen */
	public static final int TESTABORTED_BY_ASSUMPTION = 4;
	/** Test wurde uebersprungen */
	public static final int TESTSKIPPED = 5;

	// Id der Testfallkonfiguration
	private int id;
	// Datum der Testfallkonfiguration
	private Timestamp date;
	// Pfad inklusive Methodenname der Testfallkonfiguration
	private String path;
	// Name (Methodenname) der Testfallkonfiguration
	private String name;
	// Auskunft, ob die Testfallkonfiguration erfolgreich durchgelaufen ist
	private int success;
	// Meldung zum Ausgang der Testfallkonfiguration
	private String message;
	// Auskunft, ob die Testfallkonfiguration eine Exception erwartet
	private boolean exceptionExpected;
	// Parameter der Testfallkonfiguration
	private ArrayList<TestParameterInfo> parameters 
	    = new ArrayList<TestParameterInfo>();
	// Auskunft, ob ein Ergebnis eines Testdurchgangs, eine Aktualisierung 
	// des Objekts bewirkt hat
	// Wird bei rerunAllTestsOfSeveralTestCases und 
	// rerunAllTaggedTestsOfSeveralTestCases verwendet	
	@JsonIgnore
	private boolean wasUpdatedInTestRun;
	
	/**
	 * Konstruktor, setzt das Datum und die Uhrzeit der 
	 * Durchfuehrung der Testfallkonfiguarion
	 */
	public TestResultInfo()	{
		Date date = new Date();
		this.date = new Timestamp(date.getTime());
	}
	
	/**
	 * gibt die Id der Testfallkonfiguration aus
	 * @return int, Id der Testfallkonfiguration
	 */
	public int getId()	{
		return this.id;
	}
	
	/**
	 * setzt die Id der Testfallkonfiguration
	 * @param id neuer Wert fuer die Id der Testfallkonfiguration
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * gibt das Datum und Uhrzeit der Testfallkonfiguration aus
	 * @return Date Datum und Uhrzeit der Testfallkonfiguration
	 */
	public Timestamp getDate()	{
		return this.date;
	}
	
	/**
	 * setzt das Datum und die Uhrzeit der Testfallkonfiguration
	 * @param date, neuer Wert fuer das Datum und die Uhrzeit der 
	 *              Testfallkonfiguration
	 */
	public void setDate(Timestamp date)	{
		this.date = date;
	}
	
	/**
	 * gibt den Pfad der Testfallkonfiguration aus
	 * @return String, Pfad der Testfallkonfiguration
	 */
	public String getPath()	{
		return this.path;
	}
	
	/**
	 * belegt den Pfad mit dem uebergebenen Wert und generiert 
	 * den Namen der Testfallkonfiguration
	 * @param path uebergebener Pfad
	 */ 
	public void setPath(String path) {
		this.path = path;
		String hilf = path;
		while (hilf.contains(".")){
			hilf = hilf.substring(hilf.indexOf(".")+1);
		}
		this.name = hilf;
	}
	
	/**
	 * gibt aus, ob die Testfallkonfiguration erfolgreich war
	 * @return int, Erfolg der Testfallkonfiguration
	 */
	public int getSuccess() {
		return this.success;
	}
	
	/**
	 * setzt die Info, ob die Testfallkonfiguration erfolgreich war 
	 * @param success neuer Wert fuer die Info zum Erfolg der Testfallkonfiguration
	 */
	public void setSuccess(int success) {
		this.success = success;
	}
	
	/**
	 * gibt eine Nachricht ueber den Verlauf der Testfallkonfiguration aus.
	 * @return String, Nachricht zur Testfallkonfiguration
	 */
	public String getMessage()	{
		return message;
	}
	
	/**
	 * setzt die Nachricht ueber den Verlauf der Testfallkonfiguration 
	 * @param message neuer Wert, fuer die Nachricht ueber den Verlauf der 
	 *                            Testfallkonfiguration
	 */
	public void setMessage(String message) 
	{
		this.message = message;
	}
	
	/**
	 * gibt aus, ob eine Exception erwartet wird
	 * @return boolean, ob Exception erwartet wird
	 */
	public boolean isExceptionExpected()	{
		return this.exceptionExpected;
	}
	
	/**
	 * setzt die Info, ob eine Exception erwartet wird 
	 * @param exceptionExpected, neuer Wert fuer Info, 
	 *                           ob eine Exception erwartet wird
	 */
	public void setExceptionExpected(boolean exceptionExpected)	{
		this.exceptionExpected = exceptionExpected;
	}
	
	/**
	 * gibt die Parameter der Testfallkonfiguration aus
	 * @return Parameter der Testfallkonfiguration als ArrayList
	 */
	public ArrayList<TestParameterInfo> getParameters() {
		return parameters;
	}
	
	/**
	 * setzt die Parameter der Testfallkonfiguration 
	 * @param parameters neuer Wert fuer die Parameter der Testfallkonfiguration
	 */
	public void setParameters(ArrayList<TestParameterInfo> parameters)	{
		this.parameters = parameters;
	}
	
	/**
	 * fuegt der Testfallkonfiguration einen Parameter hinzu 
	 * @param name, Name des neuen Parameter fuer die Testfallkonfiguration
	 * @param value, Wert des neuen Parameter fuer die Testfallkonfiguration
	 */
	public void addTestParameter (String name, Object value){
		this.parameters.add(new TestParameterInfo(name, value));
	}
	
	/**
	 * gibt den Namen der Testfallkonfiguration, beispielsweise die 
	 * Testmethode zum Testfall, aus
	 * @return Name der Testfallkonfiguration als String
	 */
	public String getName (){
		return this.name;
	}

	/**
	 * gibt Auskunft, ob this.TestResultInfo-Objekt waehrend
	 * Testausfuehrung aktualisiert worden ist
	 * @return Auskunft, ob Aktualisierung des Objekt vorgenommen wurde
	 */
	@JsonIgnore
	public boolean hasBeenUpdatedInTestRun() {
		return wasUpdatedInTestRun;
	}

	/**
	 * setzt den Wahrheitswert, ob das Objekt waehrend Testausfuehrung
	 * aktualisiert worden ist
	 * @param wasUpdatedInTestRun Wahrheitswert, ob this.TestResultInfo-Objekt aktualisiert wurde
	 */
	@JsonIgnore
	public void setWasUpdatedInTestRun(boolean wasUpdatedInTestRun) {
		this.wasUpdatedInTestRun = wasUpdatedInTestRun;
	}

	@Override
	/**
	 * gibt die Attributwerte des TestresultInfo-Objekts heraus
	 * @return Attributwerte des TestresultInfo-Objekts als String
	 */
	public String toString(){
		String s = "TestResultInfo [id = " + id + 
		    ", date = " + date + 
			", path = " + path + 
			", name = " + name +
			", success = " + success + 
			", message = " + message + 
			", exceptionExpected = " + exceptionExpected +
			", parameters = " + parameters + 
			"]";
		return s;
	}  
	  
	/**
	 * gibt die Namen der gespeicherten Testparameter zurueck.
	 * @return Array mit den Namen der Parameter.
	 */
	public String[] fetchTestParameterNames()	{
		List<String> testParameterNames = new ArrayList<String>();
	    for(int i = 0; i < this.parameters.size(); i++) {
	       	testParameterNames.add(this.parameters.get(i).getName());
        }
		return testParameterNames.toArray(new String[0]);
	}
	
}
