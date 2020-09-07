package darlehen;

import org.junit.Test;

public class BeispielTest {
	/** sdf */
	public static String testBerechneGesamtschuld_name = "Sample";
	
	private Tilgungsdarlehen t;

	/**
	 * Test zur Methode berechneGesamtschuld aus der Klasse
	 * Tilgungsdarlehen
	 * @throws Exception , falls die zu testende Methode eine Exception
	 * wirft
	 */

	@Test
	void testBerechneGesamtschuld() throws Exception {
		System.out.println(testBerechneGesamtschuld_name);
		System.out.println("Sample" + testBerechneGesamtschuld_name);
		System.out.println("Sample Sample");
		System.out.println(testBerechneGesamtschuld_name + "Sample Sample" + testBerechneGesamtschuld_name);
		System.out.println("Sample" + 4 + "kk");
		}

}
