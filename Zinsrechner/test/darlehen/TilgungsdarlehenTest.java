package darlehen;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Die Klasse enthaelt JUnit Tests der Version 5 zur Klasse Tilgungsdarlehen.
 *
 * @author Philipp Sprengholz
 */
class TilgungsdarlehenTest {

	/*
	 * Testvariablen, koennen vom Wissenstraeger eingestellt werden und muessen aus dem
	 * Namen der verwendenden Testmethode, einem Unterstrich sowie einem fuer die
	 * Testmethode einzigartigen Parameternamen zusammengesetzt sein
	 */
	/** vom Wissenstraeger einstellbare Hoehe des Darlehens */
	public static int testBerechneAnnuitaetFuerPeriode_Darlehen = 250000;
	/** vom Wissenstraeger einstellbare Laufzeit des Darlehens */
	public static int testBerechneAnnuitaetFuerPeriode_Laufzeit = 10;
	/** vom Wissenstraeger einstellbare Zinssatz des Darlehens */
	public static double testBerechneAnnuitaetFuerPeriode_Zinssatz = 2;
	/** vom Wissenstraeger einstellbare Periode des Darlehens */
	public static int testBerechneAnnuitaetFuerPeriode_Periode = 2;
	/** vom Wissenstraeger einstellbare erwartete Annuitaets */
	public static int testBerechneAnnuitaetFuerPeriode_exp_ErwarteteAnnuitaet = 29500;

	/** vom Wissenstraeger einstellbarer Name des Benutzers des Darlehens */
	public static String testBerechneGesamtschuld_User = "Musterperson";
	/** vom Wissenstraeger einstellbare Hoehe des Darlehens */
	public static int testBerechneGesamtschuld_Darlehen = 10000000;
	/** vom Wissenstraeger einstellbare Laufzeit des Darlehens */
	public static int testBerechneGesamtschuld_Laufzeit = 10;
	/** vom Wissenstraeger einstellbarer Zinssatz des Darlehens */
	public static double testBerechneGesamtschuld_Zinssatz = 2;
	/** vom Wissenstraeger einstellbare erwartete Gesamtschuld des Darlehens */
	public static int testBerechneGesamtschuld_exp_ErwarteteGesamtschuld = 11100000;

	/** vom Wissenstraeger einstellbarer Name des Benutzers des Darlehens */
	public static String testBerechnungGesamtschuldMitInflation_User = "Musterperson";
	/** vom Wissenstraeger einstellbare Hoehe des Darlehens */
	public static int testBerechnungGesamtschuldMitInflation_Darlehen = 10000000;
	/** vom Wissenstraeger einstellbare Laufzeit des Darlehens */
	public static int testBerechnungGesamtschuldMitInflation_Laufzeit = 10;
	/** vom Wissenstraeger einstellbarer Zinssatz des Darlehens */
	public static double testBerechnungGesamtschuldMitInflation_Zinssatz = 2;
	/** vom Wissenstraeger einstellbare erwartete Gesamtschuld des Darlehens */
	public static int testBerechnungGesamtschuldMitInflation_exp_ErwarteteGesamtschuld = 11100000;
	/** vom Wissenstraeger tolerierte Abweichung zwischen erwarteter und berechneter Gesamtschuld*/
	public static int testBerechnungGesamtschuldMitInflation_Bias = 222000;

	// das zu testende Tilgungsdarlehen-Objekt
	private Tilgungsdarlehen t;

	/**
	 * Test zur Methode berechneGesamtschuld aus der Klasse Tilgungsdarlehen
	 * @throws Exception , falls die zu testende Methode eine Exception wirft
	 */
	@Test
	void testBerechneGesamtschuld()
		throws Exception {   
		this.t = new Tilgungsdarlehen(testBerechneGesamtschuld_Darlehen, 
		    testBerechneGesamtschuld_Laufzeit, 
			testBerechneGesamtschuld_Zinssatz);
		int berechneteGesamtschuld = t.berechneGesamtschuld(testBerechneGesamtschuld_User);
		int erwarteteGesamtschuld = testBerechneGesamtschuld_exp_ErwarteteGesamtschuld;
		assertEquals(erwarteteGesamtschuld, berechneteGesamtschuld,
			"Die berechnete Gesamtschuld entspricht nicht der erwarteten.");
	}

	/**
	 * Test zur Methode berechneAnnuitaetFuerPeriode aus der Klasse
	 * Tilgungsdarlehen
	 */
	@Test
	void testBerechneAnnuitaetFuerPeriode() {
		this.t = new Tilgungsdarlehen(testBerechneAnnuitaetFuerPeriode_Darlehen, 
			testBerechneAnnuitaetFuerPeriode_Laufzeit, 
			testBerechneAnnuitaetFuerPeriode_Zinssatz);
		int berechneteAnnuitaet 
		    = t.berechneAnnuitaetFuerPeriode(testBerechneAnnuitaetFuerPeriode_Periode);
		int erwarteteAnnuitaet = testBerechneAnnuitaetFuerPeriode_exp_ErwarteteAnnuitaet;
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
		this.t = new Tilgungsdarlehen(testBerechnungGesamtschuldMitInflation_Darlehen,
			testBerechnungGesamtschuldMitInflation_Laufzeit,
			testBerechnungGesamtschuldMitInflation_Zinssatz, inflationsRate);
		int berechneteGesamtschuld = t.berechneGesamtschuld(testBerechnungGesamtschuldMitInflation_User);
		double erwarteteGesamtschuld = testBerechnungGesamtschuldMitInflation_exp_ErwarteteGesamtschuld;
		assertTrue((berechneteGesamtschuld <=
			(erwarteteGesamtschuld + testBerechnungGesamtschuldMitInflation_Bias))
			&& (berechneteGesamtschuld >= 
			(erwarteteGesamtschuld - testBerechnungGesamtschuldMitInflation_Bias)),
			"Die berechnete Gesamtschuld entspricht nicht der erwarteten.");
	}
}
