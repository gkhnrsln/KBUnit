package prlab.kbunit.business.windowParametrisierung;

import java.io.File;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

	private StringProperty typ;
	private StringProperty methode;
	private StringProperty parameter;
	private StringProperty wert;
	private StringProperty desc;
	
	public static MainFrameController mainFrameController;
	
	// Konstruktor
	public ParametrisierungModel(String typ, String methode, String parameter, String wert, String desc) {
		this.typ = new SimpleStringProperty(typ);
		this.methode = new SimpleStringProperty(methode);
		this.parameter = new SimpleStringProperty(parameter);
		this.wert = new SimpleStringProperty(wert);
		this.desc = new SimpleStringProperty(desc);
	}
	
	public static ObservableList<String> datenTypen() {
		return FXCollections.observableArrayList("String", "int", "double", "float", "boolean");
	}

	/* TODO: Methoden sollen aus einer Liste ubernommen werden (je nach ausgewaehlter Testklasse) */
	public static ObservableList<String> methoden() {
		
		/*
		List<String> tmp = new ArrayList<String>();
		tmp.add("testMethode1");
		tmp.add("testMethode2");
		return FXCollections.observableArrayList(tmp);
		*/
		mainFrameController = new MainFrameController();
		
		//mainFrameController.getJavaFilePlainComboBox().getSelectionModel().getSelectedItem().getClass();
		//String s = mainFrameController.getJavaFilePlainComboBox().getSelectionModel().getSelectedItem().toString();
		
		//System.out.println("TEST" + mainFrameController.getJavaFilePlainComboBox());
		
		return FXCollections.observableArrayList("testMethode1", "testMethode2");

		//MainFrameController.getJavaFilePlainComboBox().getSelectionModel().getSelectedItem().getClass();
		//String s = MainFrameController.javaFilePlainComboBox.getSelectionModel().getSelectedItem().getClass().toString();
		//return FXCollections.observableArrayList(MainFrameController.javaFilePlainComboBox);
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
