package main.hauptfenster.testCaseRunner;

import main.dialogfenster.ProgressDialog;
import main.hauptfenster.BasicFrameControl;
import main.hauptfenster.TestResultInfo;
import main.hauptfenster.extraModels.BasicFrameXmlModel;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectMethod;
import static org.junit.platform.launcher.TagFilter.includeTags;

/**
 * JUnit5TestRunner ermoeglicht das Ausfuehren von JUnit-Jupiter-Tests aus Java heraus.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class TestCaseJUnit5Runner {

    private SummaryGeneratingListener summaryGeneratingListener;
    private ExtendedSummaryGeneratingListener extendedSummaryGeneratingListener;
   
    private TestCaseJUnit5Runner(Map<String, TestCaseInfo> testCaseInfoHashMap
        , Map<String, TestResultInfo> testResultInfoHashMap
        , BasicFrameControl basicFrameControl, ProgressDialog rpd)
    {
        this.extendedSummaryGeneratingListener =
            new ExtendedSummaryGeneratingListener(testCaseInfoHashMap, testResultInfoHashMap
            , basicFrameControl, rpd);
    }

    private TestCaseJUnit5Runner()
    {
        this.summaryGeneratingListener = new SummaryGeneratingListener();
    }

    /**
     * Konstruktor, um einen JUnit5-Testrun mit dem extenden
     * SummaryGeneratinglistener durchzufuehren, in erster Linie fuer das Ausfuehren
     * von mehreren Tests in parallel. Der JUnit5TestRunner muss mit diesem
     * Konstruktor erstellt werden, wenn mehr als eine Testmethode ausgefuehrt
     * und die Ergebnisse in KBUnit standardmaessig festgehalten werden sollen
     * (d.h. Update der Datenbank, GUI usw.).
     * Wenn man nicht diesen, sondern den parameterlosen Konstruktor waehlt
     * muss bzw sollte man die Testergebnisse auf eigene Weise verarbeiten.
     * @param testCaseInfoHashMap Testfallinformationen
     * @param testResultInfoHashMap TestResult-Objekte, in denen die Ergebnisse
     *                              gespeichert werden sollen
     * @param basicFrameControl BasicFrameControl der Applikation
     * @param rpd ProgressBar des Testdurchlaufs
     */
    public static TestCaseJUnit5Runner createWithExtendedSummaryListener(
    	Map<String, TestCaseInfo> testCaseInfoHashMap
        , Map<String, TestResultInfo> testResultInfoHashMap
        , BasicFrameControl basicFrameControl, ProgressDialog rpd)
    {
        return new TestCaseJUnit5Runner(testCaseInfoHashMap, testResultInfoHashMap,
            basicFrameControl, rpd);
    }   

    /**
     * Konstruktor, wenn zur Testausfuehrung nur ein einfacher SummaryGeneratingListener
     * verwendet werden muss. Wenn man mit dem JUnit5TestRunner mehr als eine Testmethode
     * ausfuehren moechte (mehr als eine Testmethode der Methode run uebergeben moechte)
     * und diesen Konstruktor waehlt, muss bzw sollte man
     * die Testergebnisse auf eigene Weise verarbeiten.
     */
    public static TestCaseJUnit5Runner createWithStandardSummaryListener()
    {
        return new TestCaseJUnit5Runner();
    }

    /**
     * Fuehrt einen oder mehrere uebergebene Testfaelle aus.
     * Registriert einen SummaryGeneratingListener.
     * Die Listener muessen alle jeweils die selbe Groesse haben
     *
     * @param clazzes eine oder mehrere Klassen
     * @param methodNames eine oder mehrere Methodennamen
     * @param parametersLists eine oder mehrere Parameterlisten
     * @param tags keiner, einer oder mehrere Tags zur Filterung
     *             der Testausfuehrung
     * @return TestExecutionSummary Test-Bericht
     */
    public TestExecutionSummary run(List<Class<?>> clazzes
        , List<String> methodNames, List<ArrayList<String>> parametersLists
        , String... tags) throws JDOMException, IOException 
    {
        if(clazzes.size() != methodNames.size()
            || clazzes.size() != parametersLists.size())
        {
            throw new RuntimeException();
        }
        Map<String, String> propertiesMap = new HashMap<>();
        try
        {
            propertiesMap = getRunConfigurationParamsProperties();
        }
        catch(IOException e)
        {
        	System.out.println("Exception in run von testCaseJunit5Runner" 
                + e.getMessage());
        }
        List<DiscoverySelector> discoverySelectors =
            getDiscoverySelectors(clazzes, methodNames, parametersLists);
        LauncherDiscoveryRequest request =
            buildLauncherDiscoveryRequest(discoverySelectors, tags
            , propertiesMap);
        Launcher launcher = LauncherFactory.create();
        if(this.extendedSummaryGeneratingListener == null)
        {
        	try {
            launcher.execute(request, summaryGeneratingListener);
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
            return summaryGeneratingListener.getSummary();
        }
        else
        {
            launcher.execute(request, extendedSummaryGeneratingListener);
            return extendedSummaryGeneratingListener.getSummary();
        }
    }

    private LauncherDiscoveryRequest buildLauncherDiscoveryRequest(
        List<DiscoverySelector> discoverySelectors, String[] tags
        , Map<String, String> propertiesMap) 
    {
        LauncherDiscoveryRequestBuilder requestBuilder =
            LauncherDiscoveryRequestBuilder.request()
            .selectors(discoverySelectors);
        if(tags.length > 0 )
        {
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
     */
    private List<DiscoverySelector> getDiscoverySelectors(List<Class<?>> clazzes
        , List<String> methodNames, List<ArrayList<String>> parametersLists) 
    {
        Map<String, List<String>> testfaelleNachKlasseGeordnet 
            = filterAllOrderedTests(clazzes, methodNames);
        List<DiscoverySelector> discoverySelectors 
            = getClassLevelDiscoverySelectors(clazzes, methodNames, 
            parametersLists, testfaelleNachKlasseGeordnet);
        // Method-Selectors generieren:
        for(int i = 0; i < clazzes.size(); i++)
        {
            int argIndex = 0;
            StringBuilder parametersString = new StringBuilder();
            for(String parameter : parametersLists.get(i))
            {
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

    /**
     * Wenn alle Tests einer Klasse aufgewaehlt sind, ist es manchmal sinnvoll
     * bzw. notwendig, der LauncherDiscoveryRequest einen class-Level
     * Discovery-Selektor zu uebergeben. Diese Methode bildet diese Selektoren.
     * @return die clazz-level Discovery-Selektoren
     */
    private List<DiscoverySelector> getClassLevelDiscoverySelectors(List<Class<?>> clazzes,
        List<String> methodNames, List<ArrayList<String>> parametersLists,
        Map<String, List<String>> testfaelleNachKlasseGeordnet) 
    {
        List<DiscoverySelector> discoverySelectors = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : testfaelleNachKlasseGeordnet.entrySet()) 
        {
            BasicFrameXmlModel basicFrameXmlModel = null;
            try {
                basicFrameXmlModel = BasicFrameXmlModel
                    .loadTestCaseInfo(BasicFrameXmlModel.jarFileName);
            } 
            catch (JDOMException | IOException ignored) 
            {
               	System.out.println(
               		"Exception in getClassLevelDiscoverySelectors von testCaseJunit5Runner" 
                    + ignored.getMessage());            	
            }
            assert basicFrameXmlModel != null;
            if(!basicFrameXmlModel.containsAnotherTestCaseFromSameClass(entry.getValue())) 
            {
                Class<?> clazzOfWhichAllMethodsAreExecuted
                    = clazzes.stream().filter(clazz -> clazz.getCanonicalName()
                    .equals(entry.getKey())).findFirst().get();
                if(!discoverySelectors.contains(selectClass(clazzOfWhichAllMethodsAreExecuted)))
                    discoverySelectors.add(selectClass(clazzOfWhichAllMethodsAreExecuted));
                for (int i = 0; i < clazzes.size(); i++) 
                {
                    if(clazzOfWhichAllMethodsAreExecuted.getCanonicalName()
                        .equals(clazzes.get(i).getCanonicalName()))
                    {
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
     * Filterung der auszufuehrenden Klassen und Methodennamen, damit entschieden werden kann,
     * ob class-Level Discovery-Selektoren gebildet werden koennen, die fuer OrderedTests
     * gebraucht werden.
     */
    private Map<String, List<String>> filterAllOrderedTests(List<Class<?>> clazzes, 
        List<String> methodNames) 
    {
        Map<String, List<String>> testfaelleNachKlasseGeordnet = new HashMap<>();
        for (int i = 0; i < clazzes.size(); i++) 
        {
            String testClassName = clazzes.get(i).getCanonicalName();
            if(!clazzes.get(i).isAnnotationPresent(TestMethodOrder.class))
                continue;
            if(!testfaelleNachKlasseGeordnet.containsKey(testClassName))
            {
                List<String> testCasesPathList 
                    = new ArrayList<>(Arrays.asList(testClassName
                    + "." + methodNames.get(i)));
                testfaelleNachKlasseGeordnet.put(testClassName, testCasesPathList);
            }
            else
            {
                testfaelleNachKlasseGeordnet.get(testClassName).add(testClassName
                    + "." + methodNames.get(i));
            }
        }
        return testfaelleNachKlasseGeordnet;
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
        throws IOException 
    {
        JarFile jarFile = new JarFile(BasicFrameXmlModel.jarFileName);
        JarEntry jarEntry = jarFile.getJarEntry("junit-platform.properties");
        if(jarEntry == null)
        {
        	jarFile.close();
            return null;
        }    
        InputStreamReader propertiesFileInputStream =
            new InputStreamReader(jarFile.getInputStream(jarEntry));
        Map<String, String> propertiesMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(propertiesFileInputStream);
        properties.forEach((k, v) -> propertiesMap.put((String) k, (String) v));
        jarFile.close();
        return propertiesMap;
    }
}
