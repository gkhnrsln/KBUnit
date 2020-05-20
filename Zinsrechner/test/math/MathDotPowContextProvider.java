package math;

import org.junit.jupiter.api.extension.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Beliefert @TestTemplates
 *
 * @author Yannis Herbig
 */
public class MathDotPowContextProvider
    implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
        ExtensionContext context) {
        List<Integer> exponentsInputs = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return exponentsInputs.stream().map(this :: invocationContext);
    }

    private TestTemplateInvocationContext invocationContext(int parameter) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return "exponent: " + parameter;
            }
            @Override
            public List<Extension> getAdditionalExtensions() {
                return Collections.singletonList(new ParameterResolver() {
                    @Override
                    public boolean supportsParameter(ParameterContext parameterContext,
                        ExtensionContext extensionContext) {
                        return parameterContext.getParameter().getType().equals(int.class);
                    }
                    @Override
                    public Object resolveParameter(ParameterContext parameterContext,
                        ExtensionContext extensionContext) {
                        return parameter;
                    }
                });
            }
        };
    }
}
