package prlab.kbunit.business.windowParametrisierung;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prlab.kbunit.gui.windowMainFrame.MainFrameController;
/**
 * ParametrisierungModel ist die Model-Klasse vom Parametrisierungsfenster und 
 * verwaltet Daten-Objekte.
 * @author Goekhan Arslan
 *
 */
public class ParametrisierungModel {

	private SimpleStringProperty typ;
	private SimpleStringProperty methode;
	private SimpleStringProperty parameter;
	private SimpleStringProperty wert;
	private SimpleStringProperty desc;
	
	MainFrameController mainFrameController;
	
	public ParametrisierungModel(String typ, String methode, String parameter, String wert, String desc) {
		this.typ = new SimpleStringProperty(typ);
		this.methode = new SimpleStringProperty(methode);
		this.parameter = new SimpleStringProperty(parameter);
		this.wert = new SimpleStringProperty(wert);
		this.desc = new SimpleStringProperty(desc);
	}
	
	/**
	 * 
	 * @return
	 */
	public ObservableList<String> datentypen() {
		ObservableList<String> types = FXCollections.observableArrayList("String", "int", "double", "float", "boolean");
		return types;
	}
	/**
	 * 
	 * @return
	 */
	/* TODO: Methoden sollen aus einer Liste ubernommen werden (je nach ausgewaehlter Testklasse) */
	public ObservableList<String> methoden() {
		ObservableList<String> methoden = FXCollections.observableArrayList("testMethode1", "testMethode2");
		return methoden;
	}
	
	//getter setter
	public String getTyp() {
		return typ.get();
	}

	public void setTyp(String txt) {
		typ.set(txt);
	}

	public String getMethode() {
		return methode.get();
	}

	public void setMethode(String txt) {
		methode.set(txt);
	}
	
	public String getParameter() {
		return parameter.get();
	}

	public void setParameter(String txt) {
		parameter.set(txt);
	}
	
	public String getWert() {
		return wert.get();
	}

	public void setWert(String txt) {
		wert.set(txt);
	}
	
	public String getDesc() {
		return desc.get();
	}

	public void setDesc(String txt) {
		desc.set(txt);
	}
}
