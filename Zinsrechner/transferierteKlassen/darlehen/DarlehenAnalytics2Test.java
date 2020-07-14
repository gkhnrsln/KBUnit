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
class DarlehenAnalytics2Test {
	/** vom Wissenstr[:a]ger einstellbare minimale Gesamtschuld, die sie unter den Darlehen
	 * befinden soll */
	public static double testGetGesamtschuldenMin_MinGesatmschuld = Double.MIN_VALUE;
    // das zu testende DarlehenAnalytics-Objekt
    private final DarlehenAnalytics darlehenAnalytics = new DarlehenAnalytics();


    /**
     * Liest die Werte der Tilgungsdarlehen aus der Datei tilgungsdarlehen.csv ein,
     * die zuvor mit der Terminal-Anwendung (Tilgungsdarlehenrechner) erstelllt worden sind.
     * Danach wird anhand der vom Wissenstr[:a]ger definierten Werte kontrolliert, ob
     * die Werte vom Tilgungsdarlehen akzeptiert werden k[:o]nnen.
     */
    @DisplayName("Soll Tilgungsdarlehen akzeptiert werden")
    @ParameterizedTest(name = "Darlehen={0}, Laufzeit={1}, Zinssatz={2}, Inflationsrate={3}, Gesamtschuld={4}, Zeitstempel={5}")
    @CsvFileSource(resources = "tilgungsdarlehen.csv", numLinesToSkip = 1)
    @Order(1)
    void testSollTilgungsdarlehenAkzeptiertWerden(int darlehen, int laufzeit, int zinssatz, double inflationsrate, double gesamtschuld, @JavaTimeConversionPattern("yyyy-MM-dd HH:mm:ss") LocalDate dateArgument) {
        Tilgungsdarlehen tilgungsdarlehen = new Tilgungsdarlehen(darlehen, laufzeit, zinssatz,
            inflationsrate, gesamtschuld);
        List<Tilgungsdarlehen> tilgungsdarlehenList = darlehenAnalytics.getTilgungsdarlehenList();
        assertTrue(darlehen >= 50
            && laufzeit <= 100
            && dateArgument.getYear() >= 2018);
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
            	testGetGesamtschuldenMin_MinGesatmschuld)));
        assertEquals(darlehenAnalytics.getGesamtschuldenMin().getAsDouble(),
        	testGetGesamtschuldenMin_MinGesatmschuld);
    }

    @Test
    void testGetGesamtschuldenMax() {
    	Assumptions.assumingThat(darlehenAnalytics.getTilgungsdarlehenList().size() > 0,
            () -> darlehenAnalytics.getTilgungsdarlehenList().add(
            new Tilgungsdarlehen(0, 0, 0, 0,
            	Double.MAX_VALUE)));
        assertEquals(darlehenAnalytics.getGesamtschuldenMax().getAsDouble(),
        	Double.MAX_VALUE);
    }

    /**
     * Bestimmt das Maximum aus einer vom Wissenstr[:a]ger definierten
     * Liste aus Zahlen. Die Tilgungsdarlehen-Objekte werden gemockt:
     * Es werden jeweils nur die Gesamtschulden ben[:o]tigt.
     */
    @Test
    void testGetGesamtschuldMaxUsingMocks(){
        DarlehenAnalytics localDarlehenAnalyticsForInjectingMocks
            = new DarlehenAnalytics();
        List<Tilgungsdarlehen> tilgungsdarlehenList
            = localDarlehenAnalyticsForInjectingMocks.getTilgungsdarlehenList();
        String[] gesamtschulden = "-1,0,1"
            .split(",");
        for(String gesamtschuld : gesamtschulden){
            Tilgungsdarlehen tilgungsdarlehen = Mockito.mock(Tilgungsdarlehen.class);
            when(tilgungsdarlehen.getGesamtschuld())
                .thenReturn(Double.parseDouble(gesamtschuld));
            tilgungsdarlehenList.add(tilgungsdarlehen);
        }
        double max = localDarlehenAnalyticsForInjectingMocks
            .getGesamtschuldenMax().getAsDouble();
        assertEquals(1, max);
        // Es wird verifiziert, wie oft die Methode getGesamtschulden pro Tilgungsdarlehen-Objekt
        // aufgeruft werden musste:
        for(Tilgungsdarlehen tilgungsdarlehen : tilgungsdarlehenList){
            verify(tilgungsdarlehen,
            times(1))
            .getGesamtschuld();
        }
    }
    
    @Test
    void testFuerAssumptions1() {
    	Assumptions.assumingThat(0 == 1,
           	() -> assertTrue(0 == 0));    	
    }
    
    @Test
    void testFuerAssumptions2() {
        assumeTrue(0 == 1);
        assertTrue(0 == 0); 
    }  
  

}
