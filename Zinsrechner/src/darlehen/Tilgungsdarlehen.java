package darlehen;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Darlehensrechner, der Methoden zur Verfuegung stellt, mit denen Annuitaeten und die
 * Gesamtschuld bei Tilgungsdarlehen (Darlehen mit konstanter Tilgung, aber
 * variablem, an der Restschuld bestimmtem Zinssatz) berechnet werden koennen
 *
 * @author Philipp Sprengholz
 */
public class Tilgungsdarlehen {

	// anfaengliches Darlehen in Cent
    private int    darlehen;   
    // vereinbarte Anzahl an Raten
    private int    laufzeit;  
    // Zinssatz (2% = 0.02) pro Periode auf die aktuelle Restschuld vor Tilgung
    private double zinssatz;
	// Inflationsrate (2% = 0.02); wird auf die Gesamtschuld gerechnet
	private double inflationsrate;
	// berechnete Gesamtschuld
	private double gesamtschuld;
	
	/*
	 Datum und Uhrzeit, wann zu dem Tilgungsdarlehen Objekt die
	 Gesamtschuld berechnet wurde
	*/
	private ZonedDateTime erstellungsZeitstempel;

    /**
     * Konstruktor - legt ein neues Objekt an
     * @param darlehen - Hoehe des Darlehens in Cent
     * @param laufzeit - Anzahl der Raten (Perioden), in denen das Darlehen
     *        zurueckgezahlt wird
     * @param zinssatz - Satz, zu dem das Darlehen verzinst wird
     */
    public Tilgungsdarlehen(int darlehen, int laufzeit, double zinssatz) {
    	this.darlehen = darlehen;
        this.laufzeit = laufzeit;
        this.zinssatz = zinssatz/100; // 1% => 0.01
    }

	/**
	 * Konstruktor - legt ein neues Objekt an
	 * @param darlehen - Hoehe des Darlehens in Cent
	 * @param laufzeit - Anzahl der Raten (Perioden), in denen das Darlehen
	 *        zurueckgezahlt wird
	 * @param zinssatz - Satz, zu dem das Darlehen verzinst wird
	 * @param inflationsrate - Satz, zu dem das Geld an Wert verliert
	 */
	public Tilgungsdarlehen(int darlehen, int laufzeit,
							double zinssatz, double inflationsrate) {
		this.darlehen = darlehen;
		this.laufzeit = laufzeit;
		this.zinssatz = zinssatz / 100; // 1% => 0.01
		this.inflationsrate = inflationsrate / 100; // 1% => 0.01
	}

	/**
	 * Konstruktor - legt ein neues Objekt an
	 * @param darlehen - Hoehe des Darlehens in Cent
	 * @param laufzeit - Anzahl der Raten (Perioden), in denen das Darlehen
	 *        zurueckgezahlt wird
	 * @param zinssatz - Satz, zu dem das Darlehen verzinst wird
	 * @param inflationsrate - Satz, zu dem das Geld an Wert verliert
	 * @param gesamtschuld - die zu dem Darlehen berechnete Gesamtschuld
	 */
	public Tilgungsdarlehen(int darlehen, int laufzeit, double zinssatz,
		double inflationsrate, double gesamtschuld) {
		this.darlehen = darlehen;
		this.laufzeit = laufzeit;
		this.zinssatz = zinssatz / 100; // 1% => 0.01
		this.inflationsrate = inflationsrate / 100; // 1% => 0.01
		this.gesamtschuld = gesamtschuld;
	}

	public Tilgungsdarlehen(){

	}

    /**
     * gibt das Darlehen aus
     * @return int, enthaelt das Darelehen
     */
    public int getDarlehen() {
		return darlehen;
	}

    /**
     * gibt die Laufzeit aus
     * @return int, enthaelt die Laufzeit
     */
	public int getLaufzeit() {
		return laufzeit;
	}

    /**
     * gibt den Zinssatz aus
     * @return double, enthaelt den Zinssatz
     */
	public double getZinssatz() {
		return zinssatz;
	}

	/**
	 * gibt die Inflationsrate aus
	 * @return double, enthaelt die Inflationsrate
	 */
	public double getInflationsrate() {
		return inflationsrate;
	}

	/**
	 * gibt die Gesamtschuld aus
	 * @return double, enthaelt die Inflationsrate
	 */
	public double getGesamtschuld() {
		return gesamtschuld;
	}

	/**
	 * gibt den Zeitstempel aus
	 * @return ZonedDateTime, enthaelt Zeitstempel der Berechnung
	 * der Gesamtschuld
	 */
	public ZonedDateTime getErstellungsZeitstempel() {
		return erstellungsZeitstempel;
	}

	/**
     * berechnet die Gesamtbelastung (Zins und Tilgung) einer bestimmten Periode
     *
     * @param periode Periode, fuer die die Gesamtbelastung ermittelt werden soll, 
     *                liegt zwischen 1 und laufzeit
     * @return Gesamtbelastung der Periode in Cent
     */
    public int berechneAnnuitaetFuerPeriode(int periode) {
        int restschuld = darlehen - (periode-1) * darlehen / laufzeit;
        int annuitaet = (int) (darlehen / laufzeit + restschuld * zinssatz);
        return annuitaet;
    }


    /**
     * berechnet die Gesamtschuld, d.h. den Betrag, der nach Aufsummierung aller
     * Annuitaeten ueber dir Laufzeit an den Kreditgeber zurueckgezahlt werden muss
     * @param user, String welcher den Namen der Person enthaelt, die anfragt
     * @throws Exception , falls die aktuellen Werte der Attribute fuer das Darlehen, die Laufzeit,
     *                     den Zinssatz oder den user nicht geeignet sind 
     * @return Gesamtbetrag in Cent
     */
    public int berechneGesamtschuld(String user)
        throws Exception {
        int gesamtschuld = 0;
        int restschuld = darlehen;
    
        if(this.darlehen <= 0){
        	throw new Exception("Das Darlehen darf nicht kleiner oder gleich 0 sein.");
        }
       
        if(this.laufzeit <= 0){
        	throw new Exception("Die Laufzeit darf nicht kleiner oder gleich 0 sein.");
        }
   
     	if("Musterperson".equals(user) && (this.zinssatz > 0)) /* && this.zinssatz < 0.2) */{	
	        for(int i=0; i<laufzeit; i++) {
	            gesamtschuld += darlehen / laufzeit + restschuld * zinssatz;
	            restschuld = restschuld - darlehen / laufzeit;
	        }
    	}
     	else {
     		if(!"Musterperson".equals(user)) {		
     		    throw new SQLException("Benutzer ist unbekannt!");
     		}
//     		if(this.zinssatz <= 0 || this.zinssatz >= 0.2) {		
//     			throw new IllegalArgumentException(
//     				"Zinssatz muss größer als 0 % und kleiner als 20 % sein!");
//     		}
     	}     
     	if(this.inflationsrate > 0){
       		double wertVerlust = gesamtschuld * this.inflationsrate;
     		gesamtschuld -= wertVerlust;
		}
     	this.gesamtschuld = gesamtschuld;
     	this.erstellungsZeitstempel = ZonedDateTime.now();
       	return gesamtschuld;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tilgungsdarlehen that = (Tilgungsdarlehen) o;
		return darlehen == that.darlehen &&
			laufzeit == that.laufzeit &&
			Double.compare(that.zinssatz, zinssatz) == 0 &&
			Double.compare(that.inflationsrate, inflationsrate) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(darlehen, laufzeit, zinssatz, inflationsrate);
	}
}
