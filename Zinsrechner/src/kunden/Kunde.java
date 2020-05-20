package kunden;

/**
 * Die Klasse enthaelt die Attribute eines Kunden.
 *
 * @author Philipp Sprengholz
 */
public class Kunde {

	// Vorname des Kunden
	private String vorname;
	// Nachname des Kunden
	private String nachname;
	// Alter des Kunden
	private int alter;
	// Geschlecht des Kunden
	private char geschlecht;

	/**
	 *  default-Konstruktor zur Erstellung eines Kunde-Objekts
	 */
	public Kunde(){		
	}  
	   
	/**
	 * Konstruktor zur Erstellung eines Kunde-Objekts
	 * @param vorname, String, welcher den Vornamen des Kunden enthaelt
	 * @param nachname, String, welcher den Nachnamen des Kunden enthaelt
	 */
	public Kunde(String vorname, String nachname){
	    if(vorname == null || nachname == null ||
	        "".equals(vorname) || "".equals(nachname)){
			throw new IllegalArgumentException(
				"Der Vorname und Nachname dürfen nicht leer sein.");
		}
		this.vorname = vorname;
		this.nachname = nachname;
	}

	/**
     * gibt den Vornamen des Kunden aus
     * @return String, enthaelt den Vornamen des Kunden
     */
	public String getVorname() {
		return vorname;
	}

	/**
     * setzt den Vornamen des Kunden neu
     * @param vorname, String, welcher Vornamen des Kunden enthaelt
     */
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	/**
     * gibt den NAchnamen des Kunden aus
     * @return String, enthaelt den Nachnamen des Kunden
     */
	public String getNachname() {
		return nachname;
	}

	/**
     * setzt den Nachnamen des Kunden neu
     * @param nachname, String, welcher Nachnamen des Kunden enthaelt
     */
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	/**
	 * gibt das Alter des Kunden aus
	 * @return String, enthaelt das Alter des Kunden
	 */
	public int getAlter() {
		return alter;
	}

	/**
	 * setzt den Nachnamen des Kunden neu
	 * @param alter, int, entaehlt Alter des Kunden
	 */
	public void setAlter(int alter) {
		this.alter = alter;
	}

	/**
	 * gibt das Geschlecht des Kunden aus
	 * @return char, enthaelt das Geschlecht des Kunden
	 */
	public char getGeschlecht() {
		return geschlecht;
	}

	/**
	 * setzt das Geschlecht des Kunden neu
	 * @param geschlecht, char, welcher das Geschlecht des Kunden enthaelt
	 */
	public void setGeschlecht(char geschlecht) {
		this.geschlecht = geschlecht;
	}
}
