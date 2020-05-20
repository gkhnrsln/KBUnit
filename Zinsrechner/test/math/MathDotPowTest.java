package math;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Die Klasse enthaelt Testmethoden, in denen jeweils
 * auf unterschiedliche Art und Weise die Methode {@link java.lang.Math#pow(double, double)}
 * getestet wird
 * 
 * @author Yannis Herbig
 */
public class MathDotPowTest {

    /** vom Wissenstraeger einstellbare Basen, die potenziert werden sollen */
    public static int testRaiseToPowerStandard_Base = 2;
    public static int testRaiseToPowerRepeated_Base = 2;
    public static int testRaiseToPowerParameterized_Base = 2;
    public static int testRaiseToPowerTemplate_Base = 2;
    
    public static int testRaiseToPowerPerformance_Base = 2;    
    /** vom Wissenstraeger einstellbarer Wert, nach wie vielen Millisekunden die
        Testmethode fehlschlagen soll, wenn sie dann immer noch nicht fertig ist */
    public static int testRaiseToPowerPerformance_TimeoutInNanos = 1000000;    
    /** vom Wissenstraeger einstellbarer Exponent, mit der die zugehoerige Basis 
        im Performance-Test potenziert werden soll */
    public static int testRaiseToPowerPerformance_Exponent = 2;    
    /** vom Wissenstraeger einstellbares Ergebnis, das im Performance-Test zu den 
        entsprechenden Parametern erwartet wird */
    public static long testRaiseToPowerPerformance_exp_Result = 4;
    
    /** vom Wissenstraeger einstellbare Basis fuer die TestFactory, die potenziert werden soll */
    public static int testFactory_Base = 2;
    
	// das zu testende MathDotPow-Objekt
	private MathDotPow mdp;
	
	@BeforeEach
	public void setUp() throws Exception {
        this.mdp = new MathDotPow();
	}

	@AfterEach
	public void tearDown() throws Exception {
        this.mdp = null;
	}

    @Test
    void testRaiseToPowerStandard() {
        for (int exponent = 0; exponent < 11; exponent++) {
            assertEquals(this.mdp.raiseToAPower(testRaiseToPowerStandard_Base, exponent),
                Math.pow(testRaiseToPowerStandard_Base, exponent));
        }
    }

    @RepeatedTest(11)
    void testRaiseToPowerRepeated(RepetitionInfo repetitionInfo) {
        int exponent = repetitionInfo.getCurrentRepetition() - 1; // Anfang bei 0
        assertEquals(this.mdp.raiseToAPower(testRaiseToPowerRepeated_Base, exponent),
            Math.pow(testRaiseToPowerRepeated_Base, exponent));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10})
    void testRaiseToPowerParameterized(int exponent) {
        assertEquals(this.mdp.raiseToAPower(testRaiseToPowerParameterized_Base, exponent),
            Math.pow(testRaiseToPowerParameterized_Base, exponent));
    }
 
    @TestTemplate
    @ExtendWith(MathDotPowContextProvider.class)
    void testRaiseToPowerTemplate(int exponent) {
        assertEquals(this.mdp.raiseToAPower(testRaiseToPowerTemplate_Base, exponent),
            Math.pow(testRaiseToPowerTemplate_Base, exponent));
    }

    @Test
    void testRaiseToPowerPerformance(){
        assertTimeoutPreemptively(
        	Duration.ofNanos(testRaiseToPowerPerformance_TimeoutInNanos), () -> {
        		double actualResult = Math.pow(testRaiseToPowerPerformance_Base, 
        			testRaiseToPowerPerformance_Exponent);
                assertEquals(testRaiseToPowerPerformance_exp_Result, actualResult);
            });
    }
    
    @TestFactory
    Stream<DynamicTest> testFactory()
    {
        AtomicReference<Double> result = new AtomicReference<>((double) 1);
        return IntStream.range(0, 11)
            .mapToObj(exponent -> dynamicTest("exponent: " + exponent, () -> {
                if (exponent != 0) {
                    result.updateAndGet(v -> v * testFactory_Base);
                }
                assertEquals(result.get(), Math.pow(testFactory_Base, exponent));
            }));
    }
  
}

