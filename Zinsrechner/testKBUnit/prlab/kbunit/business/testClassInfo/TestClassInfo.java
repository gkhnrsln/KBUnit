package prlab.kbunit.business.testClassInfo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prlab.kbunit.business.connector.WebServiceConnector;
import prlab.kbunit.business.dataModel.ActiveResultParameter;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.ClassCreator;
import prlab.kbunit.test.TestResultInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.jdom2.JDOMException;

/** 
 * Abstrakte Klasse speichert die geladene Testklasse.
 * <br>
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */ 

public abstract class TestClassInfo {

	/** Testtyp ist JUnit 4 */
	public static final int TESTTYPE_JUNIT_4 = 4;
	/** Testtyp ist JUnit 5 */
	public static final int TESTTYPE_JUNIT_5 = 5;

	private WebServiceConnector triDAO; 

	// Testtype (JUnit4 oder JUnit5)
	private int testtype;  
	// Testklassen Java-file
	private File file;
	// Pfad des Testfalls
	private String path;

	// die zum Testfall gehoerenden Testkonfigurationen
	private ArrayList<TestResultInfo> triList = new ArrayList<>();

	public TestClassInfo(int testtype, File file) throws JDOMException, IOException
	{
		this.triDAO = new WebServiceConnector();
		this.setTesttype(testtype);
		this.setFile(file);
		this.setPath(ClassCreator.convertIntoClassName(file, 
			Variables.TEST_SOURCE));
		// laden der Testfaelle aus der DB
		this.loadTestResultInfo();
		// TestResultInfo in eine ObservableList konvertieren
		//this.convertArrayList();
	}

	private void loadTestResultInfo() {
		triList = triDAO.readTestResultInfo(path);
	}

	/**
	 * @return the testtype
	 */
	public int getTesttype() {
		return testtype;
	}

	/**
	 * @param testtype the testtype to set
	 */
	public void setTesttype(int testtype) {
		this.testtype = testtype;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}


	/**
	 * @return the triList
	 */
	public ArrayList<TestResultInfo> getTriList() {
		return triList;
	}

	/**
	 * @param triList the triList to set
	 */
	public void setTriList(ArrayList<TestResultInfo> triList) {
		this.triList = triList;
	}

	/**
	 * gibt den Namen der Klasse zurueck, in dem der Testfall liegt
	 *
	 * @return Name des Testklasse
	 */
	public String getPackageName()
	{
		int startindex = this.path.substring(0, 
			this.path.lastIndexOf('.')).lastIndexOf('.');
		return this.path.substring(startindex + 1, 
			this.path.lastIndexOf('.'));
	}

	/**
	 * gibt den Identifier der der Testmethode zurueck
	 *
	 * @return Name der Testmethode
	 */
	public String getClassName()
	{
		return getPath().substring(path.lastIndexOf('.') + 1);
	}

	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Gibt das XML-File der Testklasse zurueck
	 * @return the xmlFile
	 */
	public File getXmlFile() {
		return new File(file.getAbsolutePath()
			.replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE)
			.replace(Variables.EXTENSION_JAVA, Variables.EXTENSION_XML));
	}

	/**
	 * gibt eine Liste aller Parameter zurueck, die dem Testfall in der
	 * Testklasse zugeordnet sind
	 *
	 * @return Liste aller Testparameter
	 */
	public ObservableList<ActiveResultParameter> getParameters(String identifier)
	{
		try
		{
			Class<?> cl1 = Class.forName(this.getPackageName() 
				+ "." + this.getClassName());
			Field[] oeffentlicheParameter = cl1.getDeclaredFields();
			ObservableList<ActiveResultParameter> sortedParameters 
			    = FXCollections.observableArrayList();
			for (int i = 0; i < oeffentlicheParameter.length; i++)
			{
				if (oeffentlicheParameter[i].getName().contains(identifier))
				{
					oeffentlicheParameter[i].setAccessible(true);
					Class<?> clazz = oeffentlicheParameter[i].getClass();
					Object value = oeffentlicheParameter[i].get(clazz);
					//parameters.add(oeffentlicheParameter[i]);
					if(value!=null) {
						sortedParameters.add(new ActiveResultParameter(oeffentlicheParameter[i]
							.getName(), value.toString()));
					} else {
						sortedParameters.add(new ActiveResultParameter(oeffentlicheParameter[i]
							.getName(), ""));
					}
				}
			}   	
			return sortedParameters;
		}
		catch (ClassNotFoundException | IllegalArgumentException | IllegalAccessException cnfe)
		{	
			cnfe.printStackTrace();
			return null;
		}
	}

	/**
	 * gibt den Identifier der der Testmethode zurueck
	 *
	 * @return Name der Testmethode
	 */
	public ObservableList<String> getIdentifierName()
	{
		ObservableList<String> nameTestcases = FXCollections.observableArrayList();

		try {
			Class<?> cl1 = Class.forName(this.getPackageName() 
				+ "." + this.getClassName());
			Field[] fields = cl1.getDeclaredFields();
			for (Field field : fields) {
				if(field.getName().contains("_")) {
					String help = field.getName().substring(0, field.getName()
						.toString().indexOf("_"));
					if(!nameTestcases.contains(help))
						nameTestcases.add(help);
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Fehler beim Klassen-Zugriff.");
			e.printStackTrace();
		}
		return nameTestcases;
	}


	/**
	 * Uebertraegt das Data-Objekt an den Web Service
	 * @param tri
	 * @return
	 */
	public int createTestcase(TestResultInfo tri) {
		try {
			int newId= triDAO.writeTestResultInfo(tri);
			
			return newId;
		} catch (Exception e) {
			System.out.println("Fehler beim Schreiben des Testfalls!");
			e.printStackTrace();
			return 0;
		}
	}
	
	/**
	 * Gibt das Data-Access-Object zurück
	 * @return the triDAO
	 */
	public WebServiceConnector getTriDAO() {
		return triDAO;
	}

}
