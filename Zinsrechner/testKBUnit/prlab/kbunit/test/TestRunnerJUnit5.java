package prlab.kbunit.test;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.reflection.TestPerformerJUnit5;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.TagFilter.includeTags;

/**
 * JUnit5TestRunner ermoeglicht das Ausfuehren von JUnit-Jupiter-Tests aus Java heraus.
 * <br>
 * &copy; 2017 Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class TestRunnerJUnit5 {

    private SummaryGeneratingListener summaryGeneratingListener;
    private ExtendedSummaryGeneratingListener extendedSummaryGeneratingListener;

    /**
     * Konstruktor, um einen JUnit5-Testrun mit dem extenden
     * SummaryGeneratinglistener durchzufuehren. In erster Linie fuer das Ausfuehren
     * von mehreren Tests gleichzeitig.
     * @param testResultInfoHashMap TestResult-Objekte, in denen die Ergebnisse
     *                              gespeichert werden sollen
     */
    private TestRunnerJUnit5(Map<String, TestResultInfo> testResultInfoHashMap
        , Map<String, Element> mapElement, Map<String, Element> elementMethodsMap
        , Selection selection, TestPerformerJUnit5 testPerformer){
        this.extendedSummaryGeneratingListener =
            new ExtendedSummaryGeneratingListener(testResultInfoHashMap
            , mapElement, elementMethodsMap, selection
            , testPerformer);
    }

    /**
     * Konstruktor, wenn zur Testausfuehrung nur ein einfacher SummaryGeneratingListener
     * verwendet werden muss
     */
    private TestRunnerJUnit5(){
        this.summaryGeneratingListener = new SummaryGeneratingListener();
    }


    public static TestRunnerJUnit5 createWithExtendedSummaryListener(Map<String, TestResultInfo> testResultInfoHashMap
        , Map<String, Element> mapElement, Map<String, Element> elementMethodsMap
        , Selection selection, TestPerformerJUnit5 testPerformer){
        return new TestRunnerJUnit5(testResultInfoHashMap, mapElement, elementMethodsMap,
            selection, testPerformer);
    }

    public static TestRunnerJUnit5 createWithStandardSummaryListener(){
        return new TestRunnerJUnit5();
    }

    /**
     * Fuehrt einen oder mehrere uebergebene Testfaelle aus.
     * Regestriert einen SummaryGeneratingListener.
     * Die Listen muessen alle jeweils die selbe Groesse haben
     *
     * @param clazzes eine oder mehrere Klassen
     * @param methodNames eine oder mehrere Methodennamen
     * @param parametersLists eine oder mehrere Parameterlisten
     * @param tags keiner, einer oder mehrere Tags zur Filterung
     *             der Testausfuehrung
     * @return TestExecutionSummary Test-Bericht
     * @throws IOException 
     * @throws JDOMException 
     */
    public TestExecutionSummary run(List<Class<?>> clazzes
        , List<String> methodNames, List<ArrayList<String>> parametersLists
        , String... tags) throws JDOMException, IOException {
        if(clazzes.size() != methodNames.size()
            || clazzes.size() != parametersLists.size()){
            throw new RuntimeException();
        }
        Map<String, String> propertiesMap = new HashMap<>();
        try{
            propertiesMap = getRunConfigurationParamsProperties();
        }
        catch(IOException e){}
        List<DiscoverySelector> discoverySelectors =
            getDiscoverySelectors(clazzes, methodNames, parametersLists);
        LauncherDiscoveryRequest request =
            buildLauncherDiscoveryRequest(discoverySelectors, tags
            , propertiesMap);
        Launcher launcher = LauncherFactory.create();
        if(this.extendedSummaryGeneratingListener == null){
            launcher.execute(request, summaryGeneratingListener);
            return summaryGeneratingListener.getSummary();
        }
        else{
            launcher.execute(request, extendedSummaryGeneratingListener);
            return extendedSummaryGeneratingListener.getSummary();
        }
    }

    private LauncherDiscoveryRequest buildLauncherDiscoveryRequest(
        List<DiscoverySelector> discoverySelectors, String[] tags
        , Map<String, String> propertiesMap) {
        LauncherDiscoveryRequestBuilder requestBuilder =
            LauncherDiscoveryRequestBuilder.request()
            .selectors(discoverySelectors);
        if(tags.length > 0 ){
            requestBuilder.filters(includeTags(tags));
        }
        return (propertiesMap != null)
            ? requestBuilder.configurationParameters(propertiesMap).build()
            : requestBuilder.build();
    }

    /**
     * Erstellt und fuellt eine Liste mit DiscoverySelectors.
     * Die Liste der Methodenparameter wird zu einem String
     * konvertiert, die Parameter werden per Komma getrennt
     *
     * @param clazzes eine oder mehrere Klassen
     * @param methodNames eine oder mehrere Methodennamen
     * @param parametersLists eine oder mehrere Parameterlisten
     * @return List<DiscoverySelector> Liste aus DiscoverySelectors
     *         die bei der programmatischen Testausfuehrung
     *         benoetigt werden
     * @throws IOException 
     * @throws JDOMException 
     */
    private List<DiscoverySelector> getDiscoverySelectors(List<Class<?>> clazzes
            , List<String> methodNames, List<ArrayList<String>> parametersLists) throws JDOMException, IOException {
        Map<String, List<String>> testfaelleNachKlasseGeordnet = filterAllOrderedTests(clazzes, methodNames);
        List<DiscoverySelector> discoverySelectors = getClassLevelDiscoverySelectors(clazzes, methodNames, parametersLists, testfaelleNachKlasseGeordnet);
        // Method-Selectors generieren:
        for(int i = 0; i < clazzes.size(); i++){
            int argIndex = 0;
            StringBuilder parametersString = new StringBuilder();
            for(String parameter : parametersLists.get(i)){
                if(argIndex < parametersLists.get(i).size() - 1)
                    parametersString.append(parameter + ",");
                else
                    parametersString.append(parameter);
                argIndex++;
            }
            discoverySelectors.add(selectMethod(clazzes.get(i), methodNames.get(i)
                    , parametersString.toString()));
        }
        return discoverySelectors;
    }

    private Map<String, List<String>> filterAllOrderedTests(List<Class<?>> clazzes, List<String> methodNames) {
        Map<String, List<String>> testfaelleNachKlasseGeordnet = new HashMap<>();
        for (int i = 0; i < clazzes.size(); i++) {
            String testClassName = clazzes.get(i).getCanonicalName();
            if(!clazzes.get(i).isAnnotationPresent(TestMethodOrder.class))
                continue;
            if(!testfaelleNachKlasseGeordnet.containsKey(testClassName)){
                List<String> testCasesPathList = new ArrayList<>(Arrays.asList(testClassName
                    + "." + methodNames.get(i)));
                testfaelleNachKlasseGeordnet.put(testClassName, testCasesPathList);
            }
            else{
                testfaelleNachKlasseGeordnet.get(testClassName).add(testClassName
                    + "." + methodNames.get(i));
            }
        }
        return testfaelleNachKlasseGeordnet;
    }

    private List<DiscoverySelector> getClassLevelDiscoverySelectors(List<Class<?>> clazzes,
        List<String> methodNames, List<ArrayList<String>> parametersLists,
        Map<String, List<String>> testfaelleNachKlasseGeordnet) throws JDOMException, IOException {
        List<DiscoverySelector> discoverySelectors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : testfaelleNachKlasseGeordnet.entrySet()) {
            CustomerTestCaseInformationLoader ctcil = CustomerTestCaseInformationLoader.getInstance();
            if(!ctcil.containsAnotherTestCaseFromSameClass(entry.getValue())) {
                Class<?> clazzOfWhichAllMethodsAreExecuted
                    = clazzes.stream().filter(clazz -> clazz.getCanonicalName()
                    .equals(entry.getKey())).findFirst().get();
                if(!discoverySelectors.contains(selectClass(clazzOfWhichAllMethodsAreExecuted)))
                    discoverySelectors.add(selectClass(clazzOfWhichAllMethodsAreExecuted));
                for (int i = 0; i < clazzes.size(); i++) {
                    if(clazzOfWhichAllMethodsAreExecuted.getCanonicalName()
                        .equals(clazzes.get(i).getCanonicalName())){
                        clazzes.remove(i);
                        methodNames.remove(i);
                        parametersLists.remove(i);
                        i--;
                    }
                }
            }
        }
        return discoverySelectors;
    }

    /**
     * Liest - falls vorhanden - die junit-platform.properties-Datei aus
     * und kopiert den Inhalt in einer HashMap. Die Datei muss in der Wurzel
     * des KLassenpfads liegen, damit sie gefunden werden kann
     *
     * @return Map<String, String> Property-Map: junit.platform-Konfigurationen
     * in Form von Key-Value-Pairs
     */
    public Map<String, String> getRunConfigurationParamsProperties()
        throws IOException {
        Map<String, String> propertiesMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(new FileInputStream("/test/junit-platform.properties"));
        properties.forEach((k, v) -> propertiesMap.put((String) k, (String) v));
        return propertiesMap;
    }
}
