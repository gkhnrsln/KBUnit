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
	private List<String> liTestMethoden;
	/** Liste der Testattribute der Testklasse */
	private List<String> liTestAttribute;
	/** Liste der Beschreibungen der Testmethoden der Testklasse */
	private List<String> liDescTestMethoden;
	/** Liste der Beschreibungen der Testattribute der Testklasse */
	private List<String> liDescTestAttribute;
	
	/**
	 * Konstruktor
	 * @param typ JUnit Testtyp der Testklasse
	 * @param liAttr Liste der TestAttribute der Testklasse
	 * @param liMeth Liste der TestMethoden der Testklasse
	 * @param liDescAttr Liste der Beschreibungen der Testattribute der Testklasse
	 * @param liDescMeth Liste der Beschreibungen der Testmethoden der Testklasse
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public DataCTCI(int typ, List<String> liAttr, List<String> liMeth, List<String> liDescAttr, List<String> liDescMeth) 
			throws IOException, ClassNotFoundException {
		this.testTyp = typ;
		this.liTestAttribute = liAttr;
		this.liTestMethoden = liMeth;
		this.liDescTestMethoden = liDescMeth;
		this.liDescTestAttribute = liDescAttr;
	}
	
	//getter setter
	public int getTestTyp() {
		return testTyp;
	}
	
	public void setTestTyp(int typ) {
		this.testTyp = typ;
	}

	public List<String> getLiTestMethoden() {
		return liTestMethoden;
	}
	
	public void setLiTestMethoden(List<String> li) {
		this.liTestMethoden = li;
	}
	
	public List<String> getLiDescTestMethoden() {
		return liDescTestMethoden;
	}
	
	public void setLiDescTestMethoden(List<String> li) {
		this.liDescTestMethoden = li;
	}

	public List<String> getLiTestAttribute() {
		return liTestAttribute;
	}
	
	public void setLiTestAttribute(List<String> li) {
		this.liTestAttribute = li;
	}
	
	public List<String> getLiDescTestAttribute() {
		return liDescTestAttribute;
	}
	
	public void setLiDescTestAttribute(List<String> li) {
		this.liDescTestAttribute = li;
	}
}