package methodTypes;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 
 * @author Yannis Herbig
 */
public class ParameterizingTest {
	
    /** vom Wissenstraeger einstellbare Zahl, die als Bias dient */
    public static int testWithValueSource_Bias = 2;

    @ParameterizedTest
    @ValueSource(ints = {3, 4, 5 })
    void testWithValueSource(int argument) {
        assertTrue((argument - testWithValueSource_Bias) > 0 
        	&& argument < 4 + testWithValueSource_Bias);
    }
}
