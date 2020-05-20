package methodTypes;

import org.junit.jupiter.api.extension.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Beispiel stammt aus dem JUnit5-User-Guide:
 * @see <a href="https://junit.org/junit5/docs/current/user-guide/#extensions-test-templates">Test-Template-Context-Provider Beispiel</a>
 * 
 */
public class TemplateInvocationContextProvider
        implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(
        ExtensionContext context) {
        return Stream.of(invocationContext("apple"), invocationContext("banana"));
    }

    private TestTemplateInvocationContext invocationContext(String parameter) {
        return new TestTemplateInvocationContext() {
            @Override
            public String getDisplayName(int invocationIndex) {
                return parameter;
            }

            @Override
            public List<Extension> getAdditionalExtensions() {
                return Collections.singletonList(new ParameterResolver() {
                    @Override
                    public boolean supportsParameter(ParameterContext parameterContext,
                        ExtensionContext extensionContext) {
                        return parameterContext.getParameter().getType().equals(String.class);
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