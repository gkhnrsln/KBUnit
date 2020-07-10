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
	/** d */
	public static int testBerechneGesamtschuld_mill = 10000000;
	/** d */
	public static int testBerechneGesamtschuld_z = 10;
	/** d */
	public static int testBerechneGesamtschuld_d = 2;
	/** d */
	public static int testBerechneGesamtschuld_jjj = 11100000;
	/** d */
	public static String testBerechneGesamtschuld_fghfgh = "Musterperson";
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
		this.t = new Tilgungsdarlehen(testBerechneGesamtschuld_mill, testBerechneGesamtschuld_z, testBerechneGesamtschuld_d);
		int berechneteGesamtschuld = t.berechneGesamtschuld("Musterperson");
		int erwarteteGesamtschuld = testBerechneGesamtschuld_jjj;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
				"Die berechnete Gesamtschuld entspricht nicht der " 
			        + "erwarteten.");
		}
}
