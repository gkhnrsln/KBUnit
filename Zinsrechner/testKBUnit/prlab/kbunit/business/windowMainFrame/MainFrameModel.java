package prlab.kbunit.business.windowMainFrame;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prlab.kbunit.business.XMLAccess;
import prlab.kbunit.business.dataModel.ActiveResult;
import prlab.kbunit.business.dataModel.InactiveResult;
import prlab.kbunit.business.testClassInfo.TestClassInfo;
import prlab.kbunit.business.testClassInfo.TestClassInfoJUnit;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.scan.FolderScanner;
import prlab.kbunit.test.CustomerTestCaseInformationLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom2.JDOMException;

/**
 * MainFrameModel ist die Model-Klasse vom Hauptfenster und 
 * verwaltet Daten-Objekte.
 * <br>
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public final class MainFrameModel {
	// Testklasse mit Testfaellen 
	private TestClassInfo openedTestClass;
	// gewaehlte Java Testklasse
	private File file;
	// Daten fuer die GUI
	private ObservableList<ActiveResult> activeResults 
	    = FXCollections.observableArrayList();
	private ObservableList<InactiveResult> inactiveResults 
	    = FXCollections.observableArrayList();

	// MainFrameModel-Singleton
	private static MainFrameModel mainFrameModel;

	// Konstruktor zum einmaligen Kreiren eines BasicFrameModel - Objekt 
	// (Singleton - Pattern)
	private MainFrameModel() {
		this.openedTestClass = null;
	}

	/**
	 * Singleton vom Hauptfenster-Model
	 */
	public static MainFrameModel getInstance() {
		if(mainFrameModel == null)
		{
			mainFrameModel = new MainFrameModel();
		}    
		return mainFrameModel;
	}

	/**
	 * laedt Daten der Testklasse
	 * @param file Java Datei Testklasse
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public void loadData(File file) throws JDOMException, IOException  {
		this.file = file;
		// Clear all Tables
		clearData();
		CustomerTestCaseInformationLoader ctcil = CustomerTestCaseInformationLoader.getInstance();
		switch(ctcil.getTesttype(file.getPath())){
			case 4:
				openedTestClass = tciFabrik(TestClassInfo.TESTTYPE_JUNIT_4);
				break;
			case 5:
				openedTestClass = tciFabrik(TestClassInfo.TESTTYPE_JUNIT_5);
				break;
			case -1:
				System.out.println("Testtyp wird nicht unterstützt " +  -1);
				return;
		}
		convertArrayList();
		scanInactiveResult();
	}


	/**
	 * Loescht den Inhalt der aktiven und inaktiven Tabelle
	 */
	private void clearData() {
		activeResults.clear();
		inactiveResults.clear();
	}
	
	/**
	 * Konventiert die TestResultInfo in ActiveResult
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private void convertArrayList() throws JDOMException, IOException {
		for(int i=0;i<openedTestClass.getTriList().size();i++) {
			activeResults.add(new ActiveResult(
				openedTestClass.getTriList().get(i).getId(), 
				new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
				    .format(openedTestClass.getTriList().get(i).getDate()), 
				openedTestClass.getTriList().get(i).getPath(), 
				openedTestClass.getTriList().get(i).getSuccess(), 
				openedTestClass.getTriList().get(i).getMessage(), 
				openedTestClass.getTriList().get(i).isExceptionExpected(),
				foundTestfile(openedTestClass.getXmlFile(), 
					openedTestClass.getTriList().get(i).getId())));

			for(int j=0; j <openedTestClass.getTriList().get(i).getParameters().size(); j++) {
				if(openedTestClass.getTriList().get(i).getParameters().get(j).getValue() == null)
				{
					activeResults.get(i).addTestParameter(
						openedTestClass.getTriList().get(i).getParameters().get(j).getName(), 
						null);
				}
				else {
					activeResults.get(i).addTestParameter(
						openedTestClass.getTriList().get(i).getParameters().get(j).getName(), 
						openedTestClass.getTriList().get(i).getParameters().get(j).getValue()
						.toString());
				}	
			}
		}
	}
	
	/**
	 * Prueft, ob ID in der Testklassen.xml unter active aufgefuehrt ist
	 * @param xmlFile
	 * @param id
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public boolean foundTestfile(File xmlFile, int id) throws JDOMException, IOException {
		// Alle aktiven Testfaelle aus der XML
		ArrayList<Map.Entry<String, String>> activeIDs = XMLAccess.getActiveTestPath(xmlFile);
		for(int i=0; i<activeIDs.size();i++) {
			if(activeIDs.get(i).getKey().equals((Variables.ACTIVITY_ID_PREFIX + id)))
				return true;
		}
		return false;
	}

	/**
	 * Gibt aktive Testfaelle zurueck.
	 * @return
	 */
	public ObservableList<ActiveResult> getActiveResults() {
		return activeResults;
	}

	/**
	 * Gibt inaktive Testfaelle zurueck.
	 * @return
	 */
	public ObservableList<InactiveResult> getInactiveResults() {
		return inactiveResults;
	}

	public ObservableList<Selection> getSelection() {
		ObservableList<Selection> selList 
		    = FXCollections.observableArrayList(Selection.allSelections());
		return selList;
	}

	/**
	 * Scannt nach allen Test Java-Files im sourcefolder "test"
	 * @param dirName
	 * @return
	 * @throws IOException
	 */
	public ObservableList<File> scanSourceFolder(String dirName) throws IOException {
		ObservableList<File> filePaths 
		    = FXCollections.observableArrayList(FolderScanner
		    .scanFolder(new File(dirName), new ArrayList<File>(), 
		    Variables.EXTENSION_TEST_JAVA));
		return filePaths;
	}
	
	/**
	 * @return the openedTestClass
	 */
	public TestClassInfo getOpenedTestClass() {
		return openedTestClass;
	}

	/**
	 * @param openedTestClass the openedTestClass to set
	 */
	public void setOpenedTestClass(TestClassInfo openedTestClass) {
		this.openedTestClass = openedTestClass;
	}

	/**
	 * Fabrik-Methode zum erstellen der TestClassInfo
	 * @param type
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private TestClassInfo tciFabrik(int type) throws JDOMException, IOException {
		switch (type)
		{
			case TestClassInfo.TESTTYPE_JUNIT_4:
				return new TestClassInfoJUnit(TestClassInfo.TESTTYPE_JUNIT_4, this.file);
			case TestClassInfo.TESTTYPE_JUNIT_5:
				return new TestClassInfoJUnit(TestClassInfo.TESTTYPE_JUNIT_5, this.file);
		}
		throw new IllegalStateException("unbekannter Typ");
	}

	/**
	 * Scannt nach inaktiven Testfaellen
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private void scanInactiveResult() throws JDOMException, IOException {
		inactiveResults.clear();
		ArrayList<Map.Entry<String, String>> arr 
		    = new ArrayList<Map.Entry<String, String>>();
		// Auslesen der Inactiven Testfaelle aus der XML !
		// Alle die schon vom Runner in inaktive gesetzt wurden!
		arr = XMLAccess.getInactiveTestPath(openedTestClass.getXmlFile());
		// Inaktive Testfaellle, die in der Testklasse.xml vorhanden sind
		for(int i = 0; i<arr.size();i++) {
			inactiveResults.add(new InactiveResult(Integer.parseInt(arr.get(i)
				.getKey().substring(Variables.ACTIVITY_ID_PREFIX.length())),true));
		}

		// Testfaelle, die in der Testklasse.xml unter active fallen, 
		// jedoch fue die Version nicht mehr gueltig ist -> inactive
		ArrayList<Map.Entry<String, String>> activeIDs 
		    = XMLAccess.getActiveTestPath(openedTestClass.getXmlFile());
		List<String> listNewInactiveIDs = new ArrayList<String>();
		for(int i=0; i < activeIDs.size(); i++) {
			boolean found=false;
			for(int j=0; j < openedTestClass.getTriList().size();j++) {
				// vergleich ob active Testcase aus XML in DB vorhanden ist FILTERED LIST
				if((activeIDs.get(i).getKey().equals(Variables.ACTIVITY_ID_PREFIX
					+ openedTestClass.getTriList().get(j).getId()))) {
					found=true;
				}
			}
			if(!found)
				listNewInactiveIDs.add(activeIDs.get(i).getKey());
		}
		for(int i=0;i<listNewInactiveIDs.size();i++) {
			inactiveResults.add(new InactiveResult(
				Integer.parseInt(listNewInactiveIDs.get(i)
				.substring(Variables.ACTIVITY_ID_PREFIX.length())),false));
		}
	}
	
	/**
	 * Loescht den Testfall in der DB sowie in der GUI Resource
	 * @param tri
	 * @return
	 */
	public void deleteTestcase(int selectedId) throws Exception{
		openedTestClass.getTriDAO().deleteTestcase(selectedId);

		// Loeschen aus ActiveResult
		for(int i = 0; i < activeResults.size(); i++) {
			if(activeResults.get(i).getId()==selectedId) {
				activeResults.remove(i);
			}	
		}
		// Loeschen aus triList
		for(int i = 0; i < openedTestClass.getTriList().size(); i++) {
			if(openedTestClass.getTriList().get(i).getId()==selectedId) {
				openedTestClass.getTriList().remove(i);
			}	
		}
	}
	
}
