package prlab.kbunit.business.windowParametrisierung;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import prlab.kbunit.enums.Variables;

/**
 * ParametrisierungModel ist die Model-Klasse vom 
 * Parametrisierungsfenster und verwaltet Daten-Objekte.
 * 
 * @author G&ouml;khan Arslan
 */
public class ParametrisierungModel {
	
	private StringProperty typ;
	private StringProperty attribut;
	private StringProperty wert;
	private StringProperty desc;
	
	public ParametrisierungModel() {
	}

	public ParametrisierungModel(String typ, String attribut, String wert, String desc){
		this.typ = new SimpleStringProperty(typ);
		this.attribut = new SimpleStringProperty(attribut);
		this.wert = new SimpleStringProperty(wert);
		this.desc = new SimpleStringProperty(desc);
	}
	
	/**
	 * Legt die moeglichen Datentypen der ComboBox "typComboBox" fest.
	 * 
	 * Dies sind die Java Grunddatentypen {@code boolean}, {@code char},
	 * {@code byte}, {@code short}, {@code int}, {@code long}, {@code float}
	 * und {@code double} sowie der Datentyp {@code String}.
	 * @return Datentypen
	 */
	public ObservableList<String> datenTypen() {
		return FXCollections.observableArrayList(
				"String", "boolean", "char", "byte",
				"short", "int", "long", "float", "double"
				);
	}

	/**
	 * Gibt eine Liste mit Testmethoden zu einer Testklasse zur&uuml;ck.
	 * @param pfad Pfad der Testklasse
	 * @return Liste 
	 * @throws ClassNotFoundException 
	 */
	public ObservableList<String> methoden(String pfad) 
			throws ClassNotFoundException {
		List<String> liste = new ArrayList<>();
		Class<?> clazz = Class.forName(pfad);
		//nur oeffentliche Methoden
		for (Method method : clazz.getDeclaredMethods()) {
			for (Annotation s : method.getAnnotations()) {
				switch ("@" + s.annotationType().getSimpleName()) {
					case Variables.ANNOTATION_TEST5:
					case Variables.ANNOTATION_TEST5_REPEATED:
					case Variables.ANNOTATION_TEST5_PARAMETERIZED:
					case Variables.ANNOTATION_TEST5_FACTORY:
					case Variables.ANNOTATION_TEST5_TEMPLATE:
						liste.add(method.getName());
						break;
					default:
						break;
				}
			}
		}
		return FXCollections.observableArrayList(liste);
	}
	
	//getter setter
	public StringProperty getTyp() {
		return typ;
	}

	public void setTyp(String txt) {
		typ.set(txt);
	}

	public StringProperty getAttribut() {
		return attribut;
	}

	public void setAttribut(String txt) {
		attribut.set(txt);
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
