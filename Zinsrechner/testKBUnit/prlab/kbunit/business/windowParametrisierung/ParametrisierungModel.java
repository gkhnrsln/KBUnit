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
		List<String> list = new ArrayList<>();

		Class<?> clazz = Class.forName(pfad);
		//nur oeffentliche Methoden
		for (Method method : clazz.getDeclaredMethods()) {
			
			for (Annotation annotation : method.getAnnotations()) {
				//nur Methoden mit Testannotation
				switch ("@" + annotation.annotationType().getSimpleName()) {
					case Variables.ANNOTATION_TEST5:
					case Variables.ANNOTATION_TEST5_REPEATED:
					case Variables.ANNOTATION_TEST5_PARAMETERIZED:
					case Variables.ANNOTATION_TEST5_FACTORY:
					case Variables.ANNOTATION_TEST5_TEMPLATE:
						list.add(method.getName());
						break;
					default:
						break;
				}
			}
		}
		return FXCollections.observableArrayList(list);
	}
	
	//getter setter
	public StringProperty getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ.set(typ);
	}

	public StringProperty getAttribut() {
		return attribut;
	}

	public void setAttribut(String attribut) {
		this.attribut.set(attribut);
	}

	public StringProperty getWert() {
		return wert;
	}

	public void setWert(String wert) {
		this.wert.set(wert);
	}
	
	public StringProperty getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc.set(desc);
	}
}
