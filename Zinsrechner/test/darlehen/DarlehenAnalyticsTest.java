package darlehen;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.JavaTimeConversionPattern;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assume.assumeTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Die Klasse enthaelt JUnit Tests der Version 5 zur Klasse DarlehenAnalytics.
 *
 * @author Yannis Herbig
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class DarlehenAnalyticsTest {

    /** vom Wissenstr[:a]ger einstellbare Mindestdarlehen */
    public static int testSollTilgungsdarlehenAkzeptiertWerden_MinDarlehen = 50;
    /** vom Wissenstr[:a]ger einstellbare maximale Laufzeit */
    public static int testSollTilgungsdarlehenAkzeptiertWerden_MaxLaufzeit = 100;
    /** vom Wissenstr[:a]ger einstellbare minimale Jahreszahl */
    public static int testSollTilgungsdarlehenAkzeptiertWerden_MinJahreszahl = 2018;

    /** vom Wissenstr[:a]ger einstellbare minimale Gesamtschuld,
     * die sie unter den Darlehen befinden soll */
    public static double testGetGesamtschuldenMin_MinGesamtschuld = Double.MIN_VALUE;

    /** vom Wissenstr[:a]ger einstellbare maximale Gesamtschuld,
     * die sie unter den Darlehen befinden soll */
    public static double testGetGesamtschuldenMax_MaxGesamtschuld = Double.MAX_VALUE;

    /** vom Wissenstr[:a]ger einstellbare Gesamtschulden fuer Tilgungsdarlehen Stub .
     * Angabe der Wert erfolgt durch das Trennen mit Komma */
    public static String testGetGesamtschuldMaxUsingMocks_Gesamtschulden = "-1,0,1";
    /** vom Wissenstr[:a]ger einstellbare Anzahl der Methodenaufrufe
     * von {@link darlehen.Tilgungsdarlehen#getGesamtschuld}, die fuer jedes Tilgungsdarlehen
     * durchgefuehrt werden, um das Maximum zu bestimmen */
    public static int testGetGesamtschuldMaxUsingMocks_NumOfGetGesamtschuldenCallsForEach = 1;
    /** vom Wissenstr[:a]ger einstellbare erwartete maximale Gesamtschuld die aus den
     * beiden vom Wissenstr[:a]ger einstellbaren Werten bestimmt werden soll */
    public static double testGetGesamtschuldMaxUsingMocks_exp_MaxGesamtschuld = 1;
    
    /** vom Wissenstr[:a]ger einstellbare Zahl. Test wird ausgefuehrt, falls Zahl gleich 1 ist */
    public static int testFuerAssumptions1_Zahl = 0; 
    
    /** vom Wissenstr[:a]ger einstellbare Zahl. Test wird ausgefuehrt, falls Zahl gleich 1 ist */
    public static int testFuerAssumptions2_Zahl = 0; 

    // das zu testende DarlehenAnalytics-Objekt
    private final DarlehenAnalytics darlehenAnalytics = new DarlehenAnalytics();


    /**
     * Liest die Werte der Tilgungsdarlehen aus der Datei tilgungsdarlehen.csv ein,
     * die zuvor mit der Terminal-Anwendung (Tilgungsdarlehenrechner) erstellelt worden sind.
     * Danach wird anhand der vom Wissenstr[:a]ger definierten Werte kontrolliert, ob
     * die Werte vom Tilgungsdarlehen akzeptiert werden koennen.
     */
    @DisplayName("Soll Tilgungsdarlehen akzeptiert werden")
    @ParameterizedTest(name = "Darlehen={0}, Laufzeit={1}, Zinssatz={2}, Inflationsrate={3}, Gesamtschuld={4}, Zeitstempel={5}")
    @CsvFileSource(resources = "tilgungsdarlehen.csv", numLinesToSkip = 1)
    @Order(1)
    void testSollTilgungsdarlehenAkzeptiertWerden(int darlehen, int laufzeit, int zinssatz, double inflationsrate, double gesamtschuld, @JavaTimeConversionPattern("yyyy-MM-dd HH:mm:ss") LocalDate dateArgument) {
        Tilgungsdarlehen tilgungsdarlehen = new Tilgungsdarlehen(darlehen, laufzeit, zinssatz,
            inflationsrate, gesamtschuld);
        List<Tilgungsdarlehen> tilgungsdarlehenList = darlehenAnalytics.getTilgungsdarlehenList();
        assertTrue(darlehen >= testSollTilgungsdarlehenAkzeptiertWerden_MinDarlehen
            && laufzeit <= testSollTilgungsdarlehenAkzeptiertWerden_MaxLaufzeit
            && dateArgument.getYear() >= testSollTilgungsdarlehenAkzeptiertWerden_MinJahreszahl);
        tilgungsdarlehenList.add(tilgungsdarlehen);
    }

    @Test
    void testSortiereTilgungsdarlehenNachGesamtschuld() {
    	Assumptions.assumingThat(darlehenAnalytics.getTilgungsdarlehenList().size() > 1,
            () -> darlehenAnalytics.sortiereTilgungsdarlehenNachGesamtschuld());
        assertThat(darlehenAnalytics.getTilgungsdarlehenList())
            .isSortedAccordingTo(Comparator.comparingDouble(Tilgungsdarlehen::getGesamtschuld));
    }
    
    @Test
    void testGetGesamtschuldenMin() {
    	Assumptions.assumingThat(darlehenAnalytics.getTilgungsdarlehenList().size() > 0,
       	    () -> darlehenAnalytics.getTilgungsdarlehenList().add(
            new Tilgungsdarlehen(0, 0, 0, 0,
            	testGetGesamtschuldenMin_MinGesamtschuld)));
        assertEquals(darlehenAnalytics.getGesamtschuldenMin().getAsDouble(),
        	testGetGesamtschuldenMin_MinGesamtschuld);
    }

    @Test
    void testGetGesamtschuldenMax() {
    	Assumptions.assumingThat(darlehenAnalytics.getTilgungsdarlehenList().size() > 0,
            () -> darlehenAnalytics.getTilgungsdarlehenList().add(
            new Tilgungsdarlehen(0, 0, 0, 0,
            	testGetGesamtschuldenMax_MaxGesamtschuld)));
        assertEquals(darlehenAnalytics.getGesamtschuldenMax().getAsDouble(),
        	testGetGesamtschuldenMax_MaxGesamtschuld);
    }

    /**
     * Bestimmt das Maximum aus einer vom Wissenstr[:a]ger definierten
     * Liste aus Zahlen. Die Tilgungsdarlehen-Objekte werden gemockt:
     * Es werden jeweils nur die Gesamtschulden benoetigt.
     */
    @Test
    void testGetGesamtschuldMaxUsingMocks(){
        DarlehenAnalytics localDarlehenAnalyticsForInjectingMocks
            = new DarlehenAnalytics();
        List<Tilgungsdarlehen> tilgungsdarlehenList
            = localDarlehenAnalyticsForInjectingMocks.getTilgungsdarlehenList();
        String[] gesamtschulden = testGetGesamtschuldMaxUsingMocks_Gesamtschulden
            .split(",");
        for(String gesamtschuld : gesamtschulden){
            Tilgungsdarlehen tilgungsdarlehen = Mockito.mock(Tilgungsdarlehen.class);
            when(tilgungsdarlehen.getGesamtschuld())
                .thenReturn(Double.parseDouble(gesamtschuld));
            tilgungsdarlehenList.add(tilgungsdarlehen);
        }
        double max = localDarlehenAnalyticsForInjectingMocks
            .getGesamtschuldenMax().getAsDouble();
        assertEquals(testGetGesamtschuldMaxUsingMocks_exp_MaxGesamtschuld, max);
        // Es wird verifiziert, wie oft die Methode getGesamtschulden pro Tilgungsdarlehen-Objekt
        // aufgeruft werden musste:
        for(Tilgungsdarlehen tilgungsdarlehen : tilgungsdarlehenList){
            verify(tilgungsdarlehen,
            times(testGetGesamtschuldMaxUsingMocks_NumOfGetGesamtschuldenCallsForEach))
            .getGesamtschuld();
        }
    }
    
    @Test
    void testFuerAssumptions1() {
    	Assumptions.assumingThat(testFuerAssumptions1_Zahl == 1,
           	() -> assertTrue(testFuerAssumptions1_Zahl == 0));    	
    }
    
    @Test
    void testFuerAssumptions2() {
        assumeTrue(testFuerAssumptions2_Zahl == 1);
        assertTrue(testFuerAssumptions2_Zahl == 0); 
    }  
  

}
