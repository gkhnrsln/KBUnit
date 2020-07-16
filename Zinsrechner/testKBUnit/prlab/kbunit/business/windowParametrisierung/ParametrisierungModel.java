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
 * ParametrisierungModel ist die Model-Klasse vom Parametrisierungsfenster und 
 * verwaltet Daten-Objekte.
 * 
 * @author G&ouml;khan Arslan
 */
public class ParametrisierungModel {
	
	private final StringProperty typ;
	private final StringProperty attribut;
	private final StringProperty wert;
	private final StringProperty desc;
	
	public ParametrisierungModel(String typ, String attribut, String wert, String desc) {
		this.typ = new SimpleStringProperty(typ);
		this.attribut = new SimpleStringProperty(attribut);
		this.wert = new SimpleStringProperty(wert);
		this.desc = new SimpleStringProperty(desc);
	}
	
	/**
	 * Legt die moeglichen Datentypen der ComboBox "typComboBox" fest.
	 * @return Datentypen
	 */
	public static ObservableList<String> datenTypen() {
		return FXCollections.observableArrayList("boolean", "String", "char", "byte", "short", "int", "long", "float", "double");
	}

	/**
	 * @param pfad
	 * @return
	 */
	public static ObservableList<String> methoden(String pfad) {
		return FXCollections.observableArrayList(getTestMethoden(pfad, false));
	}
	
	
	/**
	 * Gibt eine Liste mit den Testmethoden zurueck.
	 * @param file Dateipfad der Testklasse
	 * @param withReturnType falls der Rueckgabetyp mit angegeben werden soll
	 * @return Liste mit Testmethoden
	 */
	/*
	 * TODO: Gehoert das noch zum Model? 
	 */
	private static List<String> getTestMethoden (String file, boolean withReturnType) {
		List<String> liste = new ArrayList<>();

		try {
			Class<?> clazz = Class.forName(file);
			//nur oeffentliche Methoden
			for (Method method : clazz.getDeclaredMethods()) {
				for (Annotation s : method.getAnnotations()) {
					switch ("@" + s.annotationType().getSimpleName()) {
						case Variables.ANNOTATION_TEST5:
						case Variables.ANNOTATION_TEST5_REPEATED:
						case Variables.ANNOTATION_TEST5_PARAMETERIZED:
						case Variables.ANNOTATION_TEST5_FACTORY:
						case Variables.ANNOTATION_TEST5_TEMPLATE:
							if (withReturnType) {
								Class<?> returnType = method.getReturnType();
								liste.add(returnType + " " + method.getName());
							} else {
								liste.add(method.getName());
							}
							break;
						default:
							break;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
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
