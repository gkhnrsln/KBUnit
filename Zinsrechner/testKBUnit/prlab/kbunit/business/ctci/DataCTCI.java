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

/*
 * TODO: Soll Entity Klasse werden.
 * 
 * Soll sich zwar selbst verwalten, aber keine Zugriffe auf weitere
 * Klassen, Schnittstellen, ... haben. Die Model-Klasse (CreateCTCI)
 * soll Objekte dieser Entity verwalten. 
 * 
 * Exceptions an den Controller (MainFrameController) geleitet werden,
 * der diese verarbeitet.
 */
/**
 * Diese Klasse bereitet alle Informationen f&uuml;r die
 * {@code CustomerTestCaseInformation.xml} Datei vor.
 * @author G&ouml;khan Arslan
 */
public class DataCTCI {
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
	public DataCTCI(int typ, List<String> listeAttr, List<String> listeMeth, List<String> listeDescAttr, List<String> listeDescMeth) 
			throws IOException, ClassNotFoundException {
		this.testTyp = typ;
		this.listeTestAttribute = listeAttr;
		this.listeTestMethoden = listeMeth;
		this.listeDescTestMethoden = listeDescMeth;
		this.listeDescTestAttribute = listeDescAttr;
	}

	//getter setter
	public int getTestTyp() {
		return testTyp;
	}
	
	public void setTestTyp(int typ) {
		this.testTyp = typ;
	}

	public List<String> getListeTestMethoden() {
		return listeTestMethoden;
	}
	
	public void setListeTestMethoden(List<String> liste) {
		this.listeTestMethoden = liste;
	}
	
	public List<String> getListeDescTestMethoden() {
		return listeDescTestMethoden;
	}
	
	public void setListeDescTestMethoden(List<String> liste) {
		this.listeDescTestMethoden = liste;
	}

	public List<String> getListeTestAttribute() {
		return listeTestAttribute;
	}
	
	public void setListeTestAttribute(List<String> liste) {
		this.listeTestAttribute = liste;
	}
	
	public List<String> getListeDescTestAttribute() {
		return listeDescTestAttribute;
	}
	
	public void setListeDescTestAttribute(List<String> liste) {
		this.listeDescTestAttribute = liste;
	}
}