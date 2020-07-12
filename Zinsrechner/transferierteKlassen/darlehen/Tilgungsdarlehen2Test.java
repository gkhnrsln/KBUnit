package darlehen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Die Klasse enthaelt JUnit Tests der Version 5 zur Klasse
 * Tilgungsdarlehen.
 *
 * @author Philipp Sprengholz
*/

class Tilgungsdarlehen2Test {
	/** ghj */
	public static String testBerechneGesamtschuld_hjgj = "gjhj";
	/** ghj */
	public static String testBerechneGesamtschuld_jjjjjjjjjj = "Musterperson";
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
		int berechneteGesamtschuld = t.berechneGesamtschuld(testBerechneGesamtschuld_jjjjjjjjjj);
		int erwarteteGesamtschuld = 11100000;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
				"Die berechnete Gesamtschuld entspricht nicht der " 
			        + "erwarteten.");
		}
}
