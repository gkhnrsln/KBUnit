package main.hauptfenster;

import java.io.Serializable;

/**
 * TestParameterInfo speichert die Daten zu Parameter eines Testfalls<br>
 *  
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class TestParameterInfo implements Serializable {
	
	/** Parameter innerhalb der Grenzwerte */
	public static final int PARAMETER_IN_BOUNDS = 0;
	/** Parameter ist Grenzwert */
	public static final int PARAMETER_IS_BOUND = 1;
	/** Parameter ausserhalb der Grenzwerte */
	public static final int PARAMETER_OUT_OF_BOUNDS = 2;
	/** Parameter für das erwartete Ergebnis */
	public static final int RESULTPARAMETER = 3;
	 
	private static final long serialVersionUID = -422995892710044251L;
	// Name des Parameters der Testmethode
	private String name;
	// Wert des Parameters der Testmethode
	private Object value;
	// Datentyp des Parameters der Testmethode
	private Class<?> classType;
	// Angabe, ob Parameter innen, aussen oder auf der Grenze liegt
	private int type;
	
	/**
	* Konstruktor der die Attribute mit Werten belegt und den 
	* Typ mit dem Wert 2 vorbelegt
	* @param name, der Name des Parameters
	* @param value, der Wert des Parameters 
	*/
	public TestParameterInfo (String name, Object value){
		this.name = name;
		this.value = value;
		this.type = 2;
		this.classType = null;
	}
	
	/**
	 * Konstruktor der auch einen Wert für den Typ übernimmt
	 * @param name, der Name des Parameters
	 * @param value, der Wert des Parameters 
	 * @param type, Angabe, ob Parameter innen, aussen oder auf der Grenze liegt 
	 */
	public TestParameterInfo (String name, Object value, int type){
		this.name = name;
		this.value = value;
		this.type = type;
		this.classType = null;
	}
	
	/**
	 * default Konstruktor
	 */
	public TestParameterInfo(){		
	}
	
	/**
	 * gibt den Namen des Parameters aus
	 * @return String, Name des Parameters
	 */
	public String getName (){
		return this.name;
	}
	
	/**
	 * setzt den Namen des Parameters neu
	 * @param name neuer Name des Parameters
	 */
	public void setName (String name){
		this.name = name;
	}
	
	/**
	 * gibt den Wert des Parameters aus
	 * @return Object, Wert des Parameters
	 */
	public Object getValue(){
		return this.value;
	}
	
	/**
	 * setzt den Wert des Parameters neu
	 * @param value neuer Wert des Parameters
	 */
	public void setValue (Object value){
		this.value = value;
	}
	
	/**
	 * gibt den Grenztyp (innen, Grenze, aussen) des Parameters aus
	 * @return int, Grenztyp des Parameters
	 */
	public int getType() {
		return type;
	}

	/**
	 * setzt den den Grenztyp (innen, Grenze, aussen) des Parameters neu
	 * @param type Grenztyp des Parameters
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * gibt den Datentyp des Parameterobjekts des Parameters aus
	 * @return Class, Datentyp des Parameters
	 */
	public Class<?> getClassType(){
		return this.classType;
	}
	
	/**
	 * setzt den denDatentyp des Parameters neu
	 * @param classType Datentyp des Parameters
	 */
	public void setClassType(Class<?> classType){
		this.classType = classType;
	}

	@Override
	/**
	 * gibt die Werte des Parameterobjekts heraus
	 * @return String, Werte des Parameterobjekts
	 */
	public String toString(){
		String s = "TestParameterInfo [ name = " + name +
		    ",value = " + value + "]";
		return s;
	}

}
