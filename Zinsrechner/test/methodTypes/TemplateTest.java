package methodTypes;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 
 * @author Yannis Herbig
 */

public class TemplateTest {

    /** vom Wissenstraeger einstellbarer Wahrheitswert */
    public static boolean testTemplate_ListShouldContain = true;

    final List<String> fruits = Arrays.asList("apple", "banana", "lemon");

    @TestTemplate
    @ExtendWith(TemplateInvocationContextProvider.class)
    void testTemplate(String fruit) {
        if(testTemplate_ListShouldContain)
            assertTrue(fruits.contains(fruit));
        else
            assertFalse(fruits.contains(fruit));
    }

}
