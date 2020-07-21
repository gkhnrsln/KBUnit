package prlab.kbunit.business.ctci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.scan.FolderScanner;
import prlab.kbunit.test.ClassCreator;

/**
 * Model-Klasse f&uuml;r die Generierung der 
 * {@code CustomerTestcaseInformation.xml} Datei.
 * 
 * @author G&ouml;khan Arslan
 */
public class CTCIFileModel {
	// das einzige FileCreatorCTCI- Objekt (singleton - pattern)
	private static CTCIFileModel ctciFileModel;
	/** JDOM XML Dokument */
	private static Document doc;
	/** Tag {@code <root>} f&uuml;r das XML Dokument*/
	private static Element elRoot;
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
	/** Text zu Klassen mit fehlenden Beschreibungen */
	private String strMissingDescs;
	
	private CTCIFileModel () {
	}
	
	/**
	 * Singleton vom FileCreatorCTCI-Model
	 */
	public static CTCIFileModel getInstance() {
		if(ctciFileModel == null) {
			ctciFileModel = new CTCIFileModel();
		}
		return ctciFileModel;
	}
	
	/**
	 * init. der Elemente
	 */
	private static void load() {
		doc = new Document();
		elRoot = new Element("root");
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
	 * Erstellt im Source Verzeichnis {@link prlab.kbunit.enums.Variables#
	 * CUSTOMER_TEST_CASE_INFO_FILE_PATH
	 * Variables.CUSTOMER_TEST_CASE_INFO_FILE_PATH} 
	 * eine {@code CustomerTestCaseInformation.xml}-Datei.
	 * @throws IOException
	 */
	private static void createXML() throws IOException {
		FileOutputStream fileStream;
		OutputStreamWriter writer;
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		fileStream = new FileOutputStream(
				Variables.CUSTOMER_TEST_CASE_INFO_FILE_PATH);
		//charset wegen Umlaute auf UTF-8
		writer = new OutputStreamWriter(fileStream, "UTF-8");
		outputter.output(doc, writer);
		//ersetze vorhandenen Text
		doc.removeContent(); 
		
	}
	
	/**
	 * Gibt eine Liste der Javadoc-Kommentare zu den Testmethoden der
	 * angegebenen Java Datei zur&uuml;ck.
	 * @param file Dateipfad der Testklasse
	 * @return Liste der Beschreibungen zu den Testmethoden
	 * @exception IOException
	 */
	private List<String> descTestMethoden(File file) throws IOException {
		//Temporaere Liste fuer Zeileninhalte
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testmethoden
		List<String> liste = new ArrayList<String>();
		
		//Lese file zeilenweise aus und fuege Zeile in eine Liste
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s = in.readLine();
		tmp.add(s);
		while(s != null) {
			s = s.trim();
			tmp.add(s); 
			s = in.readLine();
		}
		in.close();
		
		int i = 0; //Zaehler fuer Zeilen (abwaerts)
		
		// gehe jede Zeile abwaerts durch
		while (i < tmp.size()) {
			//Javadoc-Kommentare der TestMethoden
			String strDesc = "";
			String line = tmp.get(i); //aktuelle Zeile
			//falls aktuelle Zeile eine Testmethode ist
			if(line.contains("void test") 
				|| line.contains(Variables.DYNAMIC_NODE) 
				|| line.contains(Variables.DYNAMIC_TEST)
				|| line.contains(Variables.DYNAMIC_CONTAINER)
				) {
				//Zaehler fuer Zeilen (aufwaerts)
				int j = 1;
				//pruefe bis in vorheriger Zeile "/**" vorkommt
				while (!tmp.get(i-j).startsWith("/**")) {
					String prevLine = tmp.get(i-j);
					//wenn vorherige Zeile mit "}" oder ";" endet 
					//-> kein Javadoc-Kommentar vorhanden
					if (prevLine.endsWith("}") || (prevLine.endsWith(";") 
							&& ! prevLine.startsWith("*"))) {
						strDesc = "Beschreibung fehlt"; break;
					} 
					//Zeile ueberspringen wenn Annotation/Javadoc-Ende vorhanden ist
					else if(prevLine.startsWith("@") || prevLine.contains("*/")) {
						j++; //naechste Zeile (aufwaerts)
						continue;
					}
					//wenn Javadoc-Tag vorhanden
					else if(prevLine.contains("* @")) {
						strDesc = ""; //vorherigen Inhalt entfernen
						j++; //naechste Zeile (aufwaerts)
						continue;
					}
					else {
						strDesc = prevLine + strDesc;
						j++; //naechste Zeile (aufwaerts)
					}
				}
				//letzte Formatierungen
				strDesc = strDesc.replace("*", "").replace("[:ss]", "ß")
						.replace("[:A]", "Ä").replace("[:O]", "Ö")
						.replace("[:U]", "Ü").replace("[:a]", "ä")
						.replace("[:o]", "ö").replace("[:u]", "ü");
				//fuege der Liste ein neue Beschreibung hinzu
				liste.add(strDesc);
			}
			i++;//naechste Zeile (abwaerts)
		}
		return liste;
	}
	/**
	 * Gibt eine Liste der Javadoc-Kommentare zu den Testattributen der 
	 * angegebenen Java Datei zur&uuml;ck.
	 * @param file Pfad der Testklasse
	 * @return Liste der Beschreibungen zu den Testattributen
	 * @exception IOException
	 */
	private List<String> descTestAttribute(File file) throws IOException {
		//Temporaere Liste fuer Zeileninhalte
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testattributen
		List<String> liste = new ArrayList<String>();
		
		//Lese file zeilenweise aus und fuege Zeile in eine Liste
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s = in.readLine();
		tmp.add(s);
		while(s != null) {
			s = s.trim();
			tmp.add(s); 
			s = in.readLine();
		}
		in.close();
		
		int i = 0; //Zaehler fuer Zeilen (abwaerts)
		
		//gehe jede Zeile durch (abwaerts)
		while (i < tmp.size()) {
			//Javadoc-Kommentar der Testattribute
			String strDesc = "";
			String line = tmp.get(i); //aktuelle Zeile
			if(line.startsWith("public static") && line.contains("test")) {
				int j = 1; //Zaehler fuer Zeilen (aufwaerts)

				//pruefe ob Beschreibung vorhanden
				do {
					String prevLine = tmp.get(i - j); //vorherhige Zeile
					if (prevLine.startsWith("/**")) {
						strDesc = prevLine + strDesc; break;
					}
					else if (prevLine.endsWith(";")) {
						strDesc = "Beschreibung fehlt"; break;
					}
					strDesc = prevLine + strDesc;
					j++; //naechste Zeile (aufwaerts)
				} while (!tmp.get(i - j).endsWith("*/"));
				//letzte Formatierungen
				strDesc = strDesc.replace("/**", "").replace("*/", "")
						.replace("*", "")
						//Javadoc-Tags entfernen
						//.replace("{@\\w+.$", "")
						.replace("{@link", "")
					    .replace("{@code", "")
						.replace("}", "")
						//Umlaute
						.replace("[:ss]", "ß").replace("[:A]", "Ä")
						.replace("[:O]", "Ö").replace("[:U]", "Ü")
						.replace("[:a]", "ä").replace("[:o]", "ö")
						.replace("[:u]", "ü");
				//fuege der Liste ein neue Beschreibung hinzu
				liste.add(strDesc);
			}
			i++; //naechste Zeile (abwaerts)
		}
		return liste;
	} 
	
	/**
	 * Gibt eine Liste mit jedem Testattribut in der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Attribute gelistet werden sollen
	 * @return Liste der Testattribute
	 * @exception IOException
	 */
	private List<String> testAttribute(File file) throws ClassNotFoundException {
		//Formatierungen fuer den Pfad
		String strFile = ClassCreator.convertIntoClassName(file, Variables.TEST_SOURCE);
		
		List<String> liste = new ArrayList<>();

		Class<?> clazz = Class.forName(strFile);
		for (Field field : clazz.getFields()) {
			if(field.getName().startsWith("test"))
				liste.add(field.getName());
		}
		return liste;
	}
	
	/**
	 * Gibt eine Liste mit jeder Testmethode der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Methoden gelistet werden sollen.
	 * @return Liste der Testmethoden
	 * @exception ClassNotFoundException
	 */
	private List<String> testMethoden(URI file) throws IOException {
		List<String> liste = null;
		List<String> methoden = new ArrayList<>();
		
		//lies die Datei zeilenweise aus
		Stream<String> stream = Files.lines(Paths.get(file));
		liste = stream
				.map(Objects::toString)
				.filter(m -> m.contains(Variables.METHOD_START_VOID)
					|| m.contains(Variables.DYNAMIC_NODE)
					|| m.contains(Variables.DYNAMIC_CONTAINER)
					|| m.contains(Variables.DYNAMIC_TEST)
					|| m.endsWith(Variables.ANNOTATION_TEST5) 
					|| m.contains(Variables.ANNOTATION_TEST5_REPEATED) 
					|| m.contains(Variables.ANNOTATION_TEST5_PARAMETERIZED)
					|| m.contains(Variables.ANNOTATION_TEST5_FACTORY)
					|| m.contains(Variables.ANNOTATION_TEST5_TEMPLATE)
					)
				.collect(Collectors.toList());
		stream.close();
		
		for (int i = 0; i < liste.size(); i++) {
			String line = liste.get(i);
			if (line.contains(Variables.ANNOTATION_TEST5)
				|| line.contains(Variables.ANNOTATION_TEST5_PARAMETERIZED)
				|| line.contains(Variables.ANNOTATION_TEST5_REPEATED)
				|| line.contains(Variables.ANNOTATION_TEST5_FACTORY)
				|| line.contains(Variables.ANNOTATION_TEST5_TEMPLATE)
				) {
				String methode = liste.get(i+1)
						.substring(liste.get(i+1).indexOf("test"));
				methoden.add(methode.substring(0, methode.indexOf('(')));
			}
		}
		return methoden;
	}
	
	/**
	 * Gibt JUnit Testtyp der &uuml;bergebenen Java Testklasse zur&uuml;ck.
	 * @param file Datei, dessen JUnit-Testtyp (4 oder 5) erkannt werden soll
	 * @return Entweder {@code 5} oder {@code 4}.
	 * @exception IOException
	 */
	private int testType(URI file) throws IOException {
		Stream<String> testTyp;
		Stream<String> stream = Files.lines(Paths.get(file));
		testTyp = stream
				.map(Objects::toString)
				//JUnit 5 enthaelt immer folgendes import
				.filter(type -> type.contains("import org.junit.jupiter"));
		long n = testTyp.count();
		stream.close();
		return n > 0 ? 5 : 4;
	}
	
	
	/**
	 * Erstellt die CustomerTestCaseInformation.xml f&uuml;r eine bestimmte Testklasse,
	 * die vom Anwender in KBUnit-Entwickler ausgew&auml;hlt wurde.
	 * @param index gibt die Position des ausgew&auml;hlten Elements der 
	 * {@link prlab.kbunit.gui.windowMainFrame.MainFrameController
	 * #javafileComboBox javafileComboBox} an.
	 * @throws IOException
	 * @throws ClassNotFoundException 
	 */
	public void createFile(int index) throws IOException, ClassNotFoundException {
		load();
		elRoot.addContent(elTestCases);

		//Gehe jede Testklasse durch
		ArrayList<File> listeKlassen = new ArrayList<>();
		FolderScanner.scanFolder(new File(Variables.TEST_SOURCE), listeKlassen,
				Variables.EXTENSION_TEST_JAVA);

		strMissingDescs = "";
		List<Element> listeElParameters = new ArrayList<>();
		
		String strKlasse = listeKlassen.get(index).toString();
		strKlasse = strKlasse
				.replace("\\", ".")
				.substring(5, strKlasse.indexOf(Variables.EXTENSION_JAVA));
		
		DataCTCI testKlasse = new DataCTCI(
				testType(listeKlassen.get(index).toURI()),
				testAttribute(listeKlassen.get(index)),
				testMethoden(listeKlassen.get(index).toURI()),
				descTestAttribute(listeKlassen.get(index)),
				descTestMethoden(listeKlassen.get(index))
				);
		
		int k = 0; //Zaehler fuer testMethoden
		
		//Gehe jede Testmethode der Testklasse durch
		for (String strMeth : testKlasse.getListeTestMethoden()) {
			int j = 0; //Zaehler fuer testAttribute
			String strDescMeth = testKlasse.getListeDescTestMethoden().get(k);
			if (strDescMeth.equals("Beschreibung fehlt"))
				strMissingDescs += strMeth + "\n";

			//Gehe jedes Testattribut der Testklasse durch
			for (String strAttr : testKlasse.getListeTestAttribute()) {
				if (strAttr.contains(strMeth)) {
					String strDescAttr = 
							testKlasse.getListeDescTestAttribute().get(j);
					if (strDescAttr.equals("Beschreibung fehlt"))
			   			strMissingDescs += "\t\t" + strAttr + "\n";
			   		
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
			   	.addContent(elTestType.clone().setText("" + testKlasse.getTestTyp()))
			   	.addContent(elParameters.clone()
			);
				
			elTestCases.addContent(elTestCase.clone());
			k++; //naechste testMethode
		}
		doc.setContent(elRoot);
		//Erstelle die XML-Datei
		createXML();
	}
	
	/**
	 * Erstellt die CustomerTestCaseInformation.xml f&uuml;r alle Testklassen.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public void createFile() throws IOException, ClassNotFoundException {
		load();
		elRoot.addContent(elTestCases);

		//Gehe jede Testklasse durch
		ArrayList<File> listeKlassen = new ArrayList<>();
		FolderScanner.scanFolder(new File(Variables.TEST_SOURCE), listeKlassen,
				Variables.EXTENSION_TEST_JAVA);
		
		strMissingDescs = "";
		List<Element> listeElParameters = new ArrayList<>();
		
		for (int i = 0; listeKlassen.size() > i; i++) {
			String strKlasse = listeKlassen.get(i).toString();
			strKlasse = strKlasse
					.replace("\\", ".")
					.substring(5, strKlasse.indexOf(Variables.EXTENSION_JAVA));
		
			DataCTCI testKlasse = new DataCTCI(
					testType(listeKlassen.get(i).toURI()),
					testAttribute(listeKlassen.get(i)),
					testMethoden(listeKlassen.get(i).toURI()),
					descTestAttribute(listeKlassen.get(i)),
					descTestMethoden(listeKlassen.get(i))
					);
		
			int k = 0; //Zaehler fuer testMethoden
			boolean isNextClass = true;
			
			//Gehe jede Testmethode der Testklasse durch
			for (String strMeth : testKlasse.getListeTestMethoden()) {
				int j = 0; //Zaehler fuer testAttribute
				String strDescMeth = testKlasse.getListeDescTestMethoden().get(k);
				if (strDescMeth.equals("Beschreibung fehlt")) {
					if(isNextClass) {
						strMissingDescs += strKlasse + "\n";
						isNextClass = false;
					}
					strMissingDescs += "\t" + strMeth + "\n";
				}
				
				//Gehe jedes Testattribut der Testklasse durch
				for (String strAttr : testKlasse.getListeTestAttribute()) {
					if (strAttr.contains(strMeth)) {
						String strDescAttr = 
								testKlasse.getListeDescTestAttribute().get(j);
						if (strDescAttr.equals("Beschreibung fehlt"))  
							strMissingDescs += "\t\t" + strAttr + "\n";
						
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
					.addContent(elTestType.clone().setText(""+ testKlasse.getTestTyp()))
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
	
	//getter and setter
	public String getStrMissingDescs() {
		return strMissingDescs;
	}

	public void setStrMissingDescs(String strMissingDescs) {
		this.strMissingDescs = strMissingDescs;
	}
}