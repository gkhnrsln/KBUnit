package prlab.kbunit.business.windowParametrisierung;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prlab.kbunit.business.transfer.Transfer;

/**
 * ParametrisierungModel ist die Model-Klasse vom Parametrisierungsfenster und 
 * verwaltet Daten-Objekte.
 * @author G&ouml;khan Arslan
 *
 */
public class ParametrisierungModel {
	
	private final StringProperty typ;
	private final StringProperty methode;
	private final StringProperty parameter;
	private final StringProperty wert;
	private final StringProperty desc;
	
	public ParametrisierungModel(String typ, String methode, String parameter, String wert, String desc) {
		this.typ = new SimpleStringProperty(typ);
		this.methode = new SimpleStringProperty(methode);
		this.parameter = new SimpleStringProperty(parameter);
		this.wert = new SimpleStringProperty(wert);
		this.desc = new SimpleStringProperty(desc);
	}
	
	/**
	 * Legt die moeglichen Datentypen der ComboBox "typComboBox" fest.
	 * @return Datentypen
	 */
	public static ObservableList<String> datenTypen() {
		return FXCollections.observableArrayList("String", "int", "long", "float", "double", "boolean");
	}

	/**
	 * 
	 * @param pfad
	 * @return
	 */
	public static ObservableList<String> methoden(String pfad) {
		return FXCollections.observableArrayList(Transfer.getTestMethode(pfad));
	}
	
	//getter setter
	public StringProperty getTyp() {
		return typ;
	}

	public void setTyp(String txt) {
		typ.set(txt);
	}

	public StringProperty getMethode() {
		return methode;
	}

	public void setMethode(String txt) {
		methode.set(txt);
	}
	
	public StringProperty getParameter() {
		return parameter;
	}

	public void setParameter(String txt) {
		parameter.set(txt);
	}
	
	public StringProperty getWert() {
		return wert;
	}

	public void setWert(String txt) {
		wert.set(txt);
	}
	
	public StringProperty getDesc() {
		return desc;
	}

	public void setDesc(String txt) {
		desc.set(txt);
	}
}
