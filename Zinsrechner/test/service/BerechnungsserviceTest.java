package service;

import daos.Datenzugriffsobjekt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Die Zahlen fuer die Addition und Multiplikation, die aus
 * dem sogenannten Datenzugriffsobjekt (Data Access Object - DAO)
 * stammen, werden gemockt. Das heißt sie werden durch vom
 * Wissenstraeger definierte Zahlen ersetzt.
 * Beispiel stammt aus YouTube-Video:
 * @see <a href="https://www.youtube.com/watch?v=7ndQXjP9yMw">JUnit5-Mockito Tutorial | 15 mins Video | Fully Explained</a>
 * 
 * @author Yannis Herbig
 */

@ExtendWith(MockitoExtension.class)
class BerechnungsserviceTest {

    // Der erste Parameter für die Addition
    public static int testAddiereAlleZahlen_EingabeZahl1 = 1;
    // Der zweite Parameter für die Addition
    public static int testAddiereAlleZahlen_EingabeZahl2 = 2;
    // Der dritte Parameter für die Addition
    public static int testAddiereAlleZahlen_EingabeZahl3 = 3;
    // Ergebnis-Summe, die zur Ueberpruefung dient
    public static int testAddiereAlleZahlen_exp_ErgebnisSumme = 6;

    @InjectMocks
    Berechnungsservice berechnungsService;
    @Mock
    Datenzugriffsobjekt datenzugriffsobjekt;

    @Test
    void testAddiereAlleZahlen() {
        when(datenzugriffsobjekt.holeAlleZahlen()).thenReturn(new int[] {testAddiereAlleZahlen_EingabeZahl1,
        		testAddiereAlleZahlen_EingabeZahl2, testAddiereAlleZahlen_EingabeZahl3});
        assertEquals(testAddiereAlleZahlen_exp_ErgebnisSumme, berechnungsService.addiereAlleZahlen());
    }

}
