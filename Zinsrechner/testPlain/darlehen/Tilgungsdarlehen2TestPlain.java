package darlehen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.RepeatedTest;


/**
 * Die Klasse enthaelt JUnit Tests der Version 5 zur Klasse
 * Tilgungsdarlehen.
 *
 * @author Philipp Sprengholz
*/

class Tilgungsdarlehen2TestPlain {
	// das zu testende Tilgungsdarlehen-Objekt
	private Tilgungsdarlehen t;

	/**
	 * Test zur Methode berechneGesamtschuld aus der Klasse
	 * Tilgungsdarlehen
	 * @throws Exception , falls die zu testende Methode eine Exception
	 * wirft
	 */

	@Test
	void testBerechneGesamtschuld() throws Exception {
		this.t = new Tilgungsdarlehen(10000000, 10, 2);
		int berechneteGesamtschuld = t.berechneGesamtschuld("Musterperson");
		int erwarteteGesamtschuld = 11100000;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
				"Die berechnete Gesamtschuld entspricht nicht der " 
			        + "erwarteten.");
		}
	
	/**
	 * Test zur Methode berechneAnnuitaetFuerPeriode aus der Klasse
	 * Tilgungsdarlehen
	 */
	@Test
	void testBerechneAnnuitaetFuerPeriode() {
		this.t = new Tilgungsdarlehen(250000, 10, 2);
		int berechneteAnnuitaet 
		    = t.berechneAnnuitaetFuerPeriode(2);
		int erwarteteAnnuitaet = 29500;
		assertEquals(erwarteteAnnuitaet, berechneteAnnuitaet,
			"Die berechnete Annuitaet entspricht nicht der erwarteten.");
	}
	
	/**
	 * Test zur Methode berechneGesamtschuld aus der Klasse Tilgungsdarlehen
	 * mit dem Setzen der Inflationsrate. Durch die randomisierte Ermittlung der Inflationsrate
	 * ist die Testausfuehrung zufallsbehaftet, was ein Grund fuer den Einsatz
	 * von RepeatedTests sein kann.
	 * @throws Exception , falls die zu testende Methode eine Exception wirft
	 */
	@RepeatedTest(3)
	void testBerechnungGesamtschuldMitInflation()
		throws Exception {
		double inflationsRate = ThreadLocalRandom.current().nextDouble(1, 2);
		this.t = new Tilgungsdarlehen(10000000,	10,	2, inflationsRate);
		int berechneteGesamtschuld = t.berechneGesamtschuld("Musterperson");
		double erwarteteGesamtschuld = 11100000;
		assertTrue((berechneteGesamtschuld <=
			(erwarteteGesamtschuld + 222000))
			&& (berechneteGesamtschuld >= 
			(erwarteteGesamtschuld - 222000)),
			"Die berechnete Gesamtschuld entspricht nicht der erwarteten.");
	}
}