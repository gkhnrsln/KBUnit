package prlab.kbunit.business.ctci;

import java.io.IOException;
import java.util.List;

/**
 * Diese Klasse bereitet alle Informationen f&uuml;r die Generierung der
 * {@code CustomerTestCaseInformation.xml}-Datei vor.
 * @author G&ouml;khan Arslan
 */
public class DataCTCI {
	/** JUnit Testtyp der Testklasse */
	private int testTyp;
	/** Liste der Testmethoden der Testklasse */
	private List<String> listeTestMethoden;
	/** Liste der Testattribute der Testklasse */
	private List<String> listeTestAttribute;
	/** Liste der Beschreibungen der Testmethoden der Testklasse */
	private List<String> listeDescTestMethoden;
	/** Liste der Beschreibungen der Testattribute der Testklasse */
	private List<String> listeDescTestAttribute;
	
	/**
	 * Konstruktor
	 * @param typ JUnit Testtyp der Testklasse
	 * @param listeAttr Liste der TestAttribute der Testklasse
	 * @param listeMeth Liste der TestMethoden der Testklasse
	 * @param listeDescAttr Liste der Beschreibungen der Testattribute der Testklasse
	 * @param listeDescMeth Liste der Beschreibungen der Testmethoden der Testklasse
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