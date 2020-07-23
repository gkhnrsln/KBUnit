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
 * stammen, werden gemockt. Das heisst sie werden durch vom
 * Wissenstraeger definierte Zahlen ersetzt.
 * Beispiel stammt aus YouTube-Video:
 * @see <a href="https://www.youtube.com/watch?v=7ndQXjP9yMw">JUnit5-Mockito Tutorial | 15 mins Video | Fully Explained</a>
 * 
 * @author Yannis Herbig
 */

@ExtendWith(MockitoExtension.class)
class Berechnungsservice2TestPlain {
    @InjectMocks
    Berechnungsservice berechnungsService;
    @Mock
    Datenzugriffsobjekt datenzugriffsobjekt;

    @Test
    void testAddiereAlleZahlen() {
        when(datenzugriffsobjekt.holeAlleZahlen()).thenReturn(new int[] {1,
        		2, 3});
        assertEquals(6, berechnungsService.addiereAlleZahlen());
    }

}
