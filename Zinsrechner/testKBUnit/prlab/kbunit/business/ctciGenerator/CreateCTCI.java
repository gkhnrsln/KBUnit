package prlab.kbunit.business.ctciGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.scan.FolderScanner;
/**
 * Diese Klasse stellt Methoden f&uuml;r die Generierung der {@code CustomerTestcaseInformation.xml}
 * Datei bereit.
 * 
 * @author G&ouml;khan Arslan
 */
public class CreateCTCI {
	/** JDOM XML Dokument */
	private static Document doc;
	/** Tag {@code <root>} f&uuml;r das XML Dokument*/
	private static Element elRoot;
	/** Tag {@code <date>} f&uuml;r das XML Dokument*/
	private static Element elDate;
    /** Tag {@code <testcases>} f&uuml;r das XML Dokument*/
	private static Element elTestCases;
    /** Tag {@code <testcase>} f&uuml;r das XML Dokument*/
	private static Element elTestCase;
    /** Tag {@code <path>} f&uuml;r das XML Dokument*/
	private static Element elPath;
    /** Tag {@code <desc>} (der Methode) f&uuml;r das XML Dokument*/
	private static Element elDescMethode;
    /** Tag {@code <desc>} (des Attributes) f&uuml;r das XML Dokument*/
	private static Element elDescAttribut;
    /** Tag {@code <testtype>} f&uuml;r das XML Dokument*/
	private static Element elTestType;
    /** Tag {@code <parameters>} f&uuml;r das XML Dokument*/
	private static Element elParameters;
    /** Tag {@code <parameter>} f&uuml;r das XML Dokument*/
	private static Element elParameter;
    /** Tag {@code <name>} f&uuml;r das XML Dokument*/
	private static Element elName;
	/**  Text zu Klassen mit fehlenden Beschreibungen */
	private static String strMissingDescs;
	
	/**
	 * init. der Elemente
	 */
	private static void load() {
		doc = new Document();
    	elRoot = new Element("root");
    	elDate = new Element("date");
        elTestCases = new Element(Variables.CTCI_NODE_TESTCASES);
        elTestCase = new Element(Variables.CTCI_NODE_TESTCASE);
        elPath = new Element(Variables.CTCI_NODE_PATH);
        elDescMethode = new Element(Variables.CTCI_NODE_DESC);
        elDescAttribut = new Element(Variables.CTCI_NODE_DESC);
        elTestType = new Element(Variables.CTCI_NODE_TESTTYPE);
        elParameters = new Element(Variables.CTCI_NODE_PARAMETERS);
        elParameter = new Element(Variables.CTCI_NODE_PARAMETER);
        elName = new Element(Variables.CTCI_NODE_NAME);
	}
	/**
	 * Erstellt eine {@code CustomerTestCaseInformation.xml} im Source Verzeichnis,
	 * welche mit {@link prlab.kbunit.enums.Variables#CUSTOMER_TEST_CASE_INFO_FILE_PATH Variables.CUSTOMER_TEST_CASE_INFO_FILE_PATH} 
	 * festgelegt wurde.
	 * @throws IOException
	 */
    private static void createXML() throws IOException {
        FileOutputStream fileStream;
        OutputStreamWriter writer;
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			fileStream = new FileOutputStream(Variables.CUSTOMER_TEST_CASE_INFO_FILE_PATH);
			writer = new OutputStreamWriter(fileStream, "UTF-8");
			outputter.output(doc, writer);
		} finally {
			doc.removeContent();
		}
    }
    
	/**
	 * Erstelle CustomerTestCaseInformation fuer eine Klasse.
	 * @param index gibt die Position des ausgew&auml;hlten Elements der 
	 * {@link prlab.kbunit.gui.windowMainFrame.MainFrameController#javafileComboBox javafileComboBox} an.
	 * @throws IOException
	 */
	public static void createFile(int index) throws IOException {
		load();
        elRoot.addContent(elDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/dd HH:mm"))));
    	//elRoot.addContent(elDate.setText(Variables.getTime().toString()));
        elRoot.addContent(elTestCases);

        //Gehe jede Testklasse durch
        ArrayList<File> listeKlassen = new ArrayList<>();
        File source = new File(Variables.TEST_SOURCE);
        FolderScanner.scanFolder(source, listeKlassen, Variables.EXTENSION_TEST_JAVA);
        
        //zaehler fuer Schleifen
        int j = 0; //testAttribute
        int k = 0; //testMethoden
        strMissingDescs = "Ausgabeformat:\nTestmethode\n\tTestattribut\n\n";
        List<Element> listeElParameters = new ArrayList<>();
        
        //////////////////////////////////////////////////
        String strKlasse = listeKlassen.get(index).toString();
        strKlasse = strKlasse
        		.replace("\\", ".")
        		.substring(5, strKlasse.indexOf(".java"));
        
        DocData testKlasse = new DocData(listeKlassen.get(index));
        int testType = testKlasse.getTestTyp();
        //Gehe jede Testmethode der Testklasse durch
        for (String strMeth : testKlasse.getListeTestMethoden()) {
            j = 0;
            String strDescMeth = testKlasse.getListeDescTestMethoden().get(k);
        	if (strDescMeth == "Beschreibung fehlt") {
        		strMissingDescs += strMeth + "\n";
        	}
            //Gehe jede Testattribute der Testklasse durch
    	    for (String strAttr : testKlasse.getListeTestAttribute()) {
    	        if (strAttr.contains(strMeth)) {
    	        	String strDescAttr = testKlasse.getListeDescTestAttribute().get(j);
    	        	if (strDescAttr == "Beschreibung fehlt") {
    	       			strMissingDescs += "\t\t" + strAttr + "\n";
    	       		}
    	       		
    	       		listeElParameters.add(elParameter.clone()
    	       			.addContent(elName.clone().setText(strAttr))
    	       			.addContent(elDescAttribut.clone().setText(strDescAttr))
    	       		);
    	       	}
            	j++; //naechstes testAttribut
   	        }
    	      
    	    elParameters.setContent(listeElParameters);
    	    //entferne alle Elemente der Liste fuer naechsten Durchlauf
    	    listeElParameters.clear();
 
           elTestCase.setContent(elPath.setText(strKlasse + "." + strMeth))
            	.addContent(elDescMethode.clone().setText(strDescMeth))
            	.addContent(elTestType.clone().setText(""+testType))
            	.addContent(elParameters.clone()
        	);
            	
    	    elTestCases.addContent(elTestCase.clone());
    	    k++; //naechste testMethode
        }
        /////////////////////////////////////////////////////////
        doc.setContent(elRoot);
        //Erstelle die XML-Datei
        createXML();
    }
	
	/**
	 * Erstelle CustomerTestCaseInformation fuer alle Klassen.
	 * @throws IOException 
	 */
    public static void createFile() throws IOException {
    	load();
        //elRoot.addContent(elDate.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY/MM/dd HH:mm"))));
    	elRoot.addContent(elDate.setText(Variables.getTime().toString()));
        elRoot.addContent(elTestCases);

        //Gehe jede Testklasse durch
        ArrayList<File> listeKlassen = new ArrayList<>();
        File source = new File(Variables.TEST_SOURCE);
        FolderScanner.scanFolder(source, listeKlassen, Variables.EXTENSION_TEST_JAVA);
        
        //zaehler fuer Schleifen
        //int i = 0; //testKlassen
        int j = 0; //testAttribute
        int k = 0; //testMethoden
        strMissingDescs = "Ausgabeformat:\npackage.Testklasse\n\tTestmethode\n\t\tTestattribut\n\n";
        List<Element> listeElParameters = new ArrayList<>();
        for (int i=0; listeKlassen.size()>i; i++) {
        	String strKlasse = listeKlassen.get(i).toString();
            strKlasse = strKlasse
            		.replace("\\", ".")
            		.substring(5, strKlasse.indexOf(".java"));
            
        	DocData testKlasse = new DocData(listeKlassen.get(i));
        	int testType = testKlasse.getTestTyp();
        	k = 0;
            boolean isNextClass = true;
            //Gehe jede Testmethode der Testklasse durch
        	for (String strMeth : testKlasse.getListeTestMethoden()) {
            	j = 0;
            	String strDescMeth = testKlasse.getListeDescTestMethoden().get(k);
        		if (strDescMeth == "Beschreibung fehlt") {
        			if(isNextClass) {
        				strMissingDescs += strKlasse + "\n";
        				isNextClass = false;
        			}
        			strMissingDescs += "\t" + strMeth + "\n";
        		}
                //Gehe jedes Testattribut der Testklasse durch
    	        for (String strAttr : testKlasse.getListeTestAttribute()) {
    	        	if (strAttr.contains(strMeth)) {
    	        		String strDescAttr = testKlasse.getListeDescTestAttribute().get(j);
    	        		if (strDescAttr == "Beschreibung fehlt") {
    	        			strMissingDescs += "\t\t" + strAttr + "\n";
    	        		}
    	        		
    	        		listeElParameters.add(elParameter.clone()
    	        			.addContent(elName.clone().setText(strAttr))
    	        			.addContent(elDescAttribut.clone().setText(strDescAttr))
    	        		);
    	        	}
    	        	j++; //naechstes testAttribut
    	        }
    	        
    	        elParameters.setContent(listeElParameters);
    	        //entferne alle Elemente der Liste fuer naechsten Durchlauf
    	        listeElParameters.clear();
 
            	elTestCase.setContent(elPath.setText(strKlasse + "." + strMeth))
            		.addContent(elDescMethode.clone().setText(strDescMeth))
            		.addContent(elTestType.clone().setText(""+ testType))
            		.addContent(elParameters.clone()
        		);
            	
    	        elTestCases.addContent(elTestCase.clone());
    	        k++; //naechste testMethode
            }
        }
        doc.setContent(elRoot);
        
        //Erstelle die XML-Datei
        createXML();
    }

	public static String getStrMissingDescs() {
		return strMissingDescs;
	}

	public static void setStrMissingDescs(String strMissingDescs) {
		CreateCTCI.strMissingDescs = strMissingDescs;
	}
}
