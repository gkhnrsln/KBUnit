package darlehen;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Die Klasse enthaelt JUnit Tests der Version 5 zur Klasse
 * Tilgungsdarlehen.
 *
 * @author Philipp Sprengholz
*/

class TilgungsdarlehenTest {
	/** ... */
	public static int testBerechneGesamtschuld_Zahl = 10000000;
	/** ... */
	public static int testBerechneGesamtschuld_Zehn = 10;
	/** ... */
	public static int testBerechneGesamtschuld_Zwei = 2;
	/** ... */
	public static String testBerechneGesamtschuld_Name = "Musterperson";
	/** ... */
	public static int testBerechneGesamtschuld_Schulden = 11100000;
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
		this.t = new Tilgungsdarlehen(testBerechneGesamtschuld_Zahl, testBerechneGesamtschuld_Zehn, testBerechneGesamtschuld_Zwei);
		int berechneteGesamtschuld = t.berechneGesamtschuld(testBerechneGesamtschuld_Name);
		int erwarteteGesamtschuld = 11testBerechneGesamtschuld_Zehn0000;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
				"Die berechnete Gesamtschuld entspricht nicht der " 
			        + "erwarteten.");
		}
}
