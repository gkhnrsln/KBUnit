package methodTypes;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import static org.junit.jupiter.api.Assertions.assertTrue;
   
/** 
 * @author Yannis Herbig
 */

public class RepeatingTest {

    /** vom Wissenstraeger einstellbare maximale Wiederholungszahl,
     * fuer den Repeated Test, bei dem noch erfolgreich ausgefuehrt wird
     */
    public static int testRepeating_MaxRepetition = 10;

    @RepeatedTest(5)
    void testRepeating(RepetitionInfo repetitionInfo) { 
    	assertTrue(repetitionInfo.getCurrentRepetition() < testRepeating_MaxRepetition); 
    }

}
