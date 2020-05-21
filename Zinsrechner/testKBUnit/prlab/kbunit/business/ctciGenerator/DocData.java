package prlab.kbunit.business.ctciGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import prlab.kbunit.enums.Variables;

/**
 * Diese Klasse bereitet alle Informationen f&uuml;r die
 * {@code CustomerTestcaseInformation.xml} Datei vor.
 * @author G&ouml;khan Arslan
 */
public class DocData {
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
	 * Konstruktor f&uuml;r die Klasse DocData.
	 * @param file Dateipfad der Testklasse
	 * @throws IOException 
	 */
	public DocData(File file) throws IOException {
		setTestTyp(getTestType(file.toString()));
		setListeTestMethoden(getTestMethoden(file.toString()));
		setListeDescTestMethoden(getDescTestMethoden(file.toString()));
		setListeTestAttribute(getTestAttribute(file.toString()));
		setListeDescTestAttribute(getDescTestAttribute(file.toString()));
	}
	
	/**
	 * Gibt eine Liste der JavaDoc Beschreibungen zur&uuml;ck.
	 * @param file Pfad der Testklasse
	 * @return Liste der Beschreibungen zu den Testmethoden.
	 * @exception IOException
	 */
	static List<String> getDescTestMethoden (String file) {
		//temporaere Liste fuer zeileninhalte
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testmethoden
		List<String> liste = new ArrayList<String>();
		/*
		 * Lese file zeilenweise aus und fuege zeile in eine Liste
		 */
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String s = in.readLine();
			tmp.add(s);
			while(s != null){
				s = s.trim();
				tmp.add(s); 
				s = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int i = 0; //zaehler fuer Zeilen abwaerts
		
		/*
		 * gehe jede Zeile abwaerts durch
		 */
		while (i<tmp.size()) {
			//JavaDoc Beschreibung der TestMethoden
			String strDesc = "";
			//falls aktuelle Zeile eine Testmethode ist
			if(tmp.get(i).contains("void test") 
					|| tmp.get(i).contains(Variables.DYNAMIC_NODE) 
					|| tmp.get(i).contains(Variables.DYNAMIC_TEST)
					|| tmp.get(i).contains(Variables.DYNAMIC_CONTAINER)
					) {
				//zaehler fuer zeilen aufwaerts
				int j = 1;
				//pruefe bis in vorherige Zeile "/**" vorkommt
				while (!tmp.get(i-j).startsWith("/**")) {
					String prevLine = tmp.get(i-j);
					//wenn vorherige Zeile mit "}" oder ";" endet 
					//-> kein JavaDoc vorhanden
					if (prevLine.endsWith("}")|| prevLine.endsWith(";")){
						strDesc = "Beschreibung fehlt"; break;
					} 
					//Zeile ueberspringen wenn bestimmte Zeichen vorkommen 
					else if(prevLine.startsWith("@") 
							|| prevLine.contains("*/") 
							|| prevLine.contains("* @")) {
						j++;//naechste Zeile (aufwaerts)
						continue;
					}
					else {
						strDesc = prevLine + strDesc;
						j++; //naechste Zeile (aufwaerts)
					}
				}
				strDesc = strDesc.replace("*", "")
						//umlaute
						.replace("[:ss]", "ß")
						.replace("[:A]", "Ä")
						.replace("[:O]", "Ö")
						.replace("[:U]", "Ü")
						.replace("[:a]", "ä")
						.replace("[:o]", "ö")
						.replace("[:u]", "ü");
				//fuege der Liste ein neue Beschreibung hinzu
				liste.add(strDesc);
			}
			i++;//naechste Zeile (abwaerts)
		}
		return liste;
	}
	/**
	 * Gibt eine Liste der JavaDoc Beschreibungen zur&uuml;ck.
	 * Falls Deutsche Umlaute vorkommen, werden diese vorher formatiert.
	 * @param file Pfad der Testklasse
	 * @return Liste der Beschreibungen zu den Testattributen
	 * @exception IOException
	 */
	static List<String> getDescTestAttribute (String file)  {
		//temporaere Liste fuer Abgleich
		List<String> tmp = new ArrayList<String>();
		//Liste mit den Beschreibungen zu den Testattributen
		List<String> liste = new ArrayList<String>();
		
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String s = in.readLine();
			tmp.add(s);
			while(s != null){
				s = s.trim();
				tmp.add(s); 
				s = in.readLine();
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int i = 0; //zaehler fuer Zeilen (abwaerts)
		
		//gehe jede Zeile durch (abwaerts)
		while (i<tmp.size()) {
			//JavaDoc Beschreibung der Testattribute
			String strDesc = "";
			
			if(tmp.get(i).startsWith("public static")){
				int j = 1; //zaehler fuer Zeilen (aufwaerts)

				//pruefe ob Beschreibung vorhanden
				do {
					String prevLine = tmp.get(i-j); //vorherhige Zeileninhalt
					if (prevLine.startsWith("/**")) {
						strDesc = prevLine + strDesc; break;
					}
					else if (prevLine.endsWith(";")){
						strDesc = "Beschreibung fehlt"; break;
					}
					strDesc = prevLine + strDesc;
					j++; //naechste Zeile (aufwaerts)
				} while (!tmp.get(i-j).endsWith("*/"));
				//letzte Formatierungen
				strDesc = strDesc.replace("/**", "")
						.replace("*/", "")
						.replace("*", "")
						.replace("{@link ", "")
						.replace("}", "")
						//umlaute
						.replace("[:ss]", "ß")
						.replace("[:A]", "Ä")
						.replace("[:O]", "Ö")
						.replace("[:U]", "Ü")
						.replace("[:a]", "ä")
						.replace("[:o]", "ö")
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
	static int getTestType (String file) throws IOException {
		Stream<String> testTyp;
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			testTyp = stream
					.map(Objects::toString)
					//JUnit 5 Testfaelle beinhalten immer folgenden import
					.filter(type -> type.contains("import org.junit.jupiter"));
			return testTyp.count() > 0 ? 5 : 4;
		}
	}
	/**
	 * Gibt eine Liste mit jeder Testmethode in der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Methoden gelistet werden sollen.
	 * @return Liste der Testmethoden
	 * @exception IOException
	 */
	static List<String> getTestMethoden (String file) throws IOException {
		List<String> liste = null;
		List<String> methoden = new ArrayList<>();
		
		//lies die Datei zeilenweise aus
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			liste = stream
					.map(Objects::toString)
					//filtere Zeilen, welche Testmethoden sind
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
		
		String methode;
		for (int i = 0; i<liste.size(); i++) {
			String line = liste.get(i);
			
			if (line.contains(Variables.ANNOTATION_TEST4)
					|| line.contains(Variables.ANNOTATION_TEST5_PARAMETERIZED)
					|| line.contains(Variables.ANNOTATION_TEST5_REPEATED)
					|| line.contains(Variables.ANNOTATION_TEST5_FACTORY)
					|| line.contains(Variables.ANNOTATION_TEST5_TEMPLATE)
					) 
			{
				methode = liste.get(i+1).substring(liste.get(i+1).indexOf("test"));
				methode = methode.substring(0, methode.indexOf('('));
				methoden.add(methode);
			}
		}
		return methoden;
	}
	
	/**
	 * Gibt eine Liste mit jeder Testvariable in der angegebenen Datei zur&uuml;ck.
	 * @param file Datei, dessen Variablen gelistet werden sollen
	 * @return Liste der Testattribute
	 * @exception IOException
	 */
	static List<String> getTestAttribute (String file) throws IOException {
		List<String> liste = null;
		//lies die Datei zeilenweise aus
		try (Stream<String> stream = Files.lines(Paths.get(file))) {
			liste = stream
					.map(Objects::toString)
					.filter(m -> m.contains("public static") && !m.contains("{"))
					//beginne neuen String erst, wenn "test" vorkommt
					.map(m -> m.substring(m.indexOf("test")))
					//beende neuen String erst, wenn "=" vorkommt
					.map(m -> m.substring(0, m.indexOf('=')))
					.collect(Collectors.toList()); 		
		}
		return liste;
	}

	//getter and setter
	public int getTestTyp() {
		return testTyp;
	}

	public void setTestTyp(int testTyp) {
		this.testTyp = testTyp;
	}

	public List<String> getListeTestMethoden() {
		return listeTestMethoden;
	}

	public void setListeTestMethoden(List<String> listeTestMethoden) {
		this.listeTestMethoden = listeTestMethoden;
	}

	public List<String> getListeDescTestMethoden() {
		return listeDescTestMethoden;
	}

	public void setListeDescTestMethoden(List<String> listeDescTestMethoden) {
		this.listeDescTestMethoden = listeDescTestMethoden;
	}

	public List<String> getListeTestAttribute() {
		return listeTestAttribute;
	}

	public void setListeTestAttribute(List<String> listeTestAttribute) {
		this.listeTestAttribute = listeTestAttribute;
	}

	public List<String> getListeDescTestAttribute() {
		return listeDescTestAttribute;
	}

	public void setListeDescTestAttribute(List<String> listeDescTestAttribute) {
		this.listeDescTestAttribute = listeDescTestAttribute;
	}
}