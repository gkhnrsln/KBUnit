package prlab.kbunit.business.ctci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.ClassCreator;

/**
 * Diese Klasse bereitet alle Informationen f&uuml;r die
 * {@code CustomerTestCaseInformation.xml} Datei vor.
 * @author G&ouml;khan Arslan
 */
public class DataCTCI {
	private URI file;
	/** JUnit Testtyp der Testklasse */
	private int testTyp;
	/** Liste der TestMethoden der Testklasse */
	private List<String> listeTestMethoden;
	/** Liste der TestAttribute der Testklasse */
	private List<String> listeTestAttribute;
	/** Liste der Beschreibungen der Test Methoden der Testklasse */
	private List<String> listeDescTestMethoden;
	/** Liste der Beschreibungen der Test Attribute der Testklasse */
	private List<String> listeDescTestAttribute;
	
	/**
	 * Konstruktor f&uuml;r die Klasse.
	 * @param file Dateipfad der Testklasse
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public DataCTCI(URI file) throws IOException, ClassNotFoundException {
		this.file = file;
		this.testTyp = testType();
		this.listeTestMethoden = testMethoden();
		this.listeTestAttribute = testAttribute();
		this.listeDescTestMethoden = descTestMethoden();
		this.listeDescTestAttribute = descTestAttribute();
	}
	
	/**
	 * Gibt eine Liste der Javadoc-Kommentare zu den Testmethoden der
	 * angegebenen Java Datei zur&uuml;ck.
	 * @param file Dateipfad der Testklasse
	 * @return Liste der Beschreibungen zu den Testmethoden
	 * @exception IOException
	 */
	private List<String> descTestMethoden() {
		//Temporaere Liste fuer Zeileninhalte
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testmethoden
		List<String> liste = new ArrayList<String>();
		
		//Lese file zeilenweise aus und fuege Zeile in eine Liste
		BufferedReader in;
		try {
			File f = new File(file);
			in = new BufferedReader(new FileReader(f));
			String s = in.readLine();
			tmp.add(s);
			while(s != null) {
				s = s.trim();
				tmp.add(s); 
				s = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
					//Zeile ueberspringen wenn Annotation / Javadoc Ende vorhanden ist
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
	private List<String> descTestAttribute() {
		//Temporaere Liste fuer Zeileninhalte
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testattributen
		List<String> liste = new ArrayList<String>();
		
		//Lese file zeilenweise aus und fuege Zeile in eine Liste
		BufferedReader in;
		try {
			File f = new File(file);
			in = new BufferedReader(new FileReader(f));
			String s = in.readLine();
			tmp.add(s);
			while(s != null) {
				s = s.trim();
				tmp.add(s); 
				s = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
					String prevLine = tmp.get(i-j); //vorherhiger Zeileninhalt
					if (prevLine.startsWith("/**")) {
						strDesc = prevLine + strDesc; break;
					}
					else if (prevLine.endsWith(";")) {
						strDesc = "Beschreibung fehlt"; break;
					}
					strDesc = prevLine + strDesc;
					j++; //naechste Zeile (aufwaerts)
				} while (!tmp.get(i-j).endsWith("*/"));
				//letzte Formatierungen
				strDesc = strDesc.replace("/**", "").replace("*/", "")
						.replace("*", "")
						//Javadoc-Tags entfernen
						.replace("{@link ", "").replace("{@code", "")
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
	 * Gibt JUnit Testtyp der &uuml;bergebenen Java Testklasse zur&uuml;ck.
	 * @param file Datei, dessen JUnit-Testtyp (4 oder 5) erkannt werden soll
	 * @return Entweder {@code 5} oder {@code 4}.
	 * @exception IOException
	 */
	private int testType() throws IOException {
		Stream<String> testTyp;
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			testTyp = stream
					.map(Objects::toString)
					//JUnit 5 enthaelt immer folgendes import
					.filter(type -> type.contains("import org.junit.jupiter"));
			return testTyp.count() > 0 ? 5 : 4;
		}
	}
	
	/**
	 * Gibt eine Liste mit jeder Testmethode der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Methoden gelistet werden sollen.
	 * @return Liste der Testmethoden
	 * @exception ClassNotFoundException
	 */
	private List<String> testMethoden() throws IOException {
		List<String> liste = null;
		List<String> methoden = new ArrayList<>();
		
		//lies die Datei zeilenweise aus
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
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
		}
		
		for (int i = 0; i<liste.size(); i++) {
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
	 * Gibt eine Liste mit jedem Testattribut in der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Attribute gelistet werden sollen
	 * @return Liste der Testattribute
	 * @exception IOException
	 */
	private List<String> testAttribute() throws ClassNotFoundException {
		//Formatierungen fuer den Pfad
		File file = new File(this.file);
		String strFile = ClassCreator.convertIntoClassName(file, Variables.TEST_SOURCE);
		
		List<String> liste = new ArrayList<>();

		Class<?> clazz = Class.forName(strFile);
		for (Field field : clazz.getFields()) {
			if(field.getName().startsWith("test"))
				liste.add(field.getName());
		}
		return liste;
	}

	//getter
	public int getTestTyp() {
		return testTyp;
	}

	public List<String> getListeTestMethoden() {
		return listeTestMethoden;
	}

	public List<String> getListeDescTestMethoden() {
		return listeDescTestMethoden;
	}

	public List<String> getListeTestAttribute() {
		return listeTestAttribute;
	}

	public List<String> getListeDescTestAttribute() {
		return listeDescTestAttribute;
	}
}