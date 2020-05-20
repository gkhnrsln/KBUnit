package math;

import org.junit.jupiter.api.*;
import java.util.stream.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/** 
 * @author Yannis Herbig
 */
public class TestFactoryTest {

    /** vom Wissenstraeger einstellbare Zahl, welche ersten n geraden natuerlichen Zahlen ausgegeben werden sollen */
    public static int testDynamicTestFromIntStream_Limit = 10;

    @TestFactory
    Stream<DynamicTest> testDynamicTestFromIntStream() {
        return IntStream.iterate(0, n -> n + 2).limit(testDynamicTestFromIntStream_Limit)
             .mapToObj(n -> dynamicTest("test_" + n, () -> {
                 assertTrue(n % 2 == 0);
             }));
    }
}
