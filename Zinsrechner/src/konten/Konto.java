
package konten;

import kunden.Kunde;

/**
 * Die Klasse enthaelt die Attribute eines Kontos.
 *
 * @author Philipp Sprengholz
 */
public class Konto {
	
	// Kunde des Kontos
	private Kunde kunde;
	// iban des Kontos
	private String iban;
	// Geld auf dem Konto
	private int kontostand;

	/**
     * gibt den Kunden des Kontos aus
     * @return Konto, enthaelt den Kunden des Kontos
     */
	public Kunde getKunde() {
		return kunde;
	}

	/**
     * setzt den Kunden des Kontos neu
     * @param kunde, Kunde, welcher den Kunden des Kontos enthaelt
     */
	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}
	
	/**
     * gibt die Iban des Kontos aus
     * @return String, enthaelt die iban des Kontos
     */
	public String getIban() {
		return iban;
	}

	/**
     * setzt die Iban des Kontos neu
     * @param iban, String, welcher die neue Iban des Kontos enthaelt
     */
	public void setIban(String iban) {
		this.iban = iban;
	}

	/**
	 * gibt den Stand des Kontos aus
	 * @return String, enthaelt den Geldbetrag des Kontos
	 */
	public int getKontostand() {
		return kontostand;
	}

	/**
	 * setzt den Kontostand des Kontos neu
	 * @param kontostand, int, welcher den neuen Kontostand des Kontos enthaelt
	 */
	public void setKontostand(int kontostand) {
		this.kontostand = kontostand;
	}
}
