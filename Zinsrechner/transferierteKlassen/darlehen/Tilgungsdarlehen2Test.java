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
	/** d */
	public static int testBerechneGesamtschuld_a = 10000000;
	/** d */
	public static int testBerechneGesamtschuld_ab = 10;
	/** d */
	public static int testBerechneGesamtschuld_abc = 2;
	/** d */
	public static String testBerechneGesamtschuld_abcd = "Musterperson";
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
		this.t = new Tilgungsdarlehen(testBerechneGesamtschuld_a, testBerechneGesamtschuld_ab, testBerechneGesamtschuld_abc);
		int berechneteGesamtschuld = t.berechneGesamtschuld(testBerechneGesamtschuld_abcd);
		int erwarteteGesamtschuld = 11100000;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
				"Die berechnete Gesamtschuld entspricht nicht der " 
			        + "erwarteten.");
		}
}
