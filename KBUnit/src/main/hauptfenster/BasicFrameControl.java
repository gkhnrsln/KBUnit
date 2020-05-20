package main.hauptfenster;

import hilfe.MessageMaker;
import hilfe.MyClassLoader;
import hilfe.Variables;
import hilfe.guiHilfe.ToastMessage;
import main.dialogfenster.InfoDialog;
import main.dialogfenster.ProgressDialog;
import main.hauptfenster.extraModels.BasicFrameConfigurationModel;
import main.hauptfenster.extraModels.BasicFrameXmlModel;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.hauptfenster.testCaseRunner.TestCaseJUnit5Runner;
import main.panel.testDurchfuehren.konfigurationsgenerator.Combination;
import org.jdom2.JDOMException;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * BasicFrameControl ist das Control des Hauptfensters der KBUnit-Anwendung.
 * Es werden JUnit-Tests gekapselt und ueber diese grafische Benutzeroberflaeche
 * via Reflection zur Bearbeitung zur verfuegung gestellt.
 * <br>
 * &copy; 2020 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig, Ursula Oesing
 */   
public class BasicFrameControl {
    
	// Model - Objekt des Basisfensters
    private BasicFrameModel basicFrameModel;
	// XmlModel - Objekt des Basisfensters
    private BasicFrameXmlModel basicFrameXmlModel;
	// KonfigarionsModel - Objekt des Basisfensters
    private BasicFrameConfigurationModel basicFrameConfigurationModel;
	// View - Objekt des Basisfensters
    private BasicFrameView basicFrameView;
    // ClassLoader zum Laden des jar-Files vom Entwickler
    public static ClassLoader CLASSLOADER;
    
    // Meldungen fuer Informationsdialogfenster und Fehlermeldungsfenster
    private static final String INFO_DURCHFUEHREN_TEST = "Tests werden ausgeführt";
    private static final String FEHLER_DURCHFUEHREN_TESTFALL 
        = "Fehler bei der Durchführung des Testfalls";
    private static final String FEHLER_LESEN_TESTKONFIGURATIONEN 
        = " Fehler beim Lesen gespeicherter Testkonfigurationen";
    private static final String FEHLER_LOESCHEN_TESTKONFIGURATIONEN
        = "Fehler beim Löschen gespeicherter Testkonfigurationen";
    private static final String FEHLER_AENDERN_TESTKONFIGURATIONEN
        = "Fehler beim Ändern gespeicherter Testkonfigurationen";
    public static final String FEHLER_LESEN_TESTERGEBNISSE 
        = "Fehler beim Lesen gespeicherter Testergebnisse";
    private static final String FEHLER_LADEN_TESTFAELLE 
        = "Fehler beim Laden der Testfälle";
    private static final String FEHLER_DOKUMENTATION_NICHT_VERFUEGBAR 
        = "Dokumentation nicht verfügbar"; 
           
    /**
     * erstellt das Hauptfenster mit zugehoerigem View und Model
     * @throws JDOMException falls das Laden der Web - Resource 
     *         nicht durchgefuehrt werden konnte
     * @throws IOException falls ein Fehler beim Lesen der 
     *         Konfigurationsdatei aufgetreten ist
     */
    public BasicFrameControl() 
        throws JDOMException, IOException 
    {       
        this.basicFrameConfigurationModel 
            = BasicFrameConfigurationModel.getInstance();
        this.basicFrameModel = BasicFrameModel.getInstance(
            this.basicFrameConfigurationModel
            .getConfiguration().getPathWebResource()); 
        this.basicFrameView = new BasicFrameView(this, this.basicFrameModel);
    }  

    
    //---------------------Zugriffe auf das BasicFrameModel------------------//
    
    /**
     * gibt das Model-Objekt zu dem Hauptfenster heraus
     * @return BasicFrameModel das Model-Objekt zu dem Hauptfenster
     */
    public BasicFrameModel getBasicFrameModel() 
    {
		return basicFrameModel;
	}   
    
    /**
     * liest die Informationen zu allen Testlaeufen eines speziellen Testfalls
     * (spezifiziert durch Pfad und Version) aus
     *
     * @param tci testfall, der zu lesen ist
     * @return die Testfallergebnisse des vorgegebenen Testfalls
     */
    public ArrayList<TestResultInfo> readTestResultInfo(TestCaseInfo tci)  
    {
        ArrayList<TestResultInfo> result = null;
        try
        {    
            result = this.basicFrameModel.readTestResultInfo(tci);
        }
        catch(Exception exc)
        {
            new InfoDialog(FEHLER_LESEN_TESTERGEBNISSE, exc.getMessage());
        }
        return result;
    }  
    
 
    /**
     * liest die im Programm verwendeten Informationen zu Anzahl und
     * Erfolg gespeicherter Testkonfigurationen zum Testfall
     * @param tci TestCaseInfo, dessen Daten gelesen werden sollen 
     */
    private final void readTestConfigurationInfo(TestCaseInfo tci)
    {
        try
        {
            this.basicFrameModel.readTestConfigurationInfo(tci);
        }
        catch(Exception exc)
        {
            new InfoDialog(FEHLER_LESEN_TESTKONFIGURATIONEN, exc.getMessage());
        }
    }
  
       
    /**
     * loescht in der Datenbank die vorgegebenen Testlaeufe
     *
     * @param trilist ArraList, welche die zu loschenden TestResultInfos enthaelt 
     * @return ArrayList<Integer>, id's geloeschter TestResultInfo-Objekte
     */
    public ArrayList<Integer> deleteTestConfigurations(ArrayList<TestResultInfo> trilist) 
    {        
    	ArrayList<Integer> deleted = new ArrayList<Integer>();
    	for(int i = 0; i < trilist.size(); i++)
        {    
    		try 
    		{
                this.basicFrameModel.deleteTestResultInfo(trilist.get(i).getId());
                deleted.add(trilist.get(i).getId());
    		}
            catch(Exception exc)
            {
                new InfoDialog(FEHLER_LOESCHEN_TESTKONFIGURATIONEN, exc.getMessage());
            }
        }    
    	try
    	{
            this.basicFrameModel.readTestConfigurationInfo();
    	}
    	catch(Exception exc)
        {
            new InfoDialog(FEHLER_LESEN_TESTKONFIGURATIONEN, exc.getMessage());
        }
    	return deleted;
    }
     
    /*
     * fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (sodass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann)
     *
     * @param tci auszufuehrender Testfall
     * @param tri Testkonfiguration des Testfalls, die auszufuehren ist
     * @param parameters im Rahmen der Testausfuehrung zu nutzende Testparameter
     * @param exceptionExpected gibt an, ob eine Exception im Rahmen der
     *   Testausfuehrung erwartet wird
     *
     * @return Ergebnis der Testausfuehrung
 	 * @throws Exception, Name und Meldung der Exceptionklasse 
     */
    private TestResultInfo runTestConfiguration(TestCaseInfo tci, TestResultInfo tri,
        ArrayList<TestParameterInfo> parameters, boolean exceptionExpected) 
        throws Exception
    {
        TestResultInfo resultInfo = null;
        resultInfo = this.basicFrameModel.runTestConfiguration(tci, tri, parameters, 
             exceptionExpected);
        return resultInfo;
    }

    /*
     * fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (so dass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann). Es gibt bisher kein zugehoeriges
     * TestResultInfo-Objekt. Dieses wird neu erzeugt.
     */ 
    private TestExecutionSummary runTestConfiguration(
    	HashMap<String, TestCaseInfo> testCaseInfoHashMap,
        HashMap<String, TestResultInfo> testResultInfoHashMap,
        ProgressDialog rpd)
        throws Exception
    {
        List<Class<?>> testClassesList = new ArrayList<>();
		List<String> testMethodNamesList = new ArrayList<>();
		List<ArrayList<String>> parametersLists = new ArrayList<>();
		this.basicFrameModel.prepareAndSetTestRunParameters(
			testCaseInfoHashMap, testResultInfoHashMap,
			testClassesList, testMethodNamesList, parametersLists);
		TestExecutionSummary testExecutionSummary = 
		    TestCaseJUnit5Runner.createWithExtendedSummaryListener(testCaseInfoHashMap,
		    testResultInfoHashMap, this, rpd)
			.run(testClassesList, testMethodNamesList, parametersLists);
		testExecutionSummary.printTo(new PrintWriter(System.out));
		return testExecutionSummary;
	}
     
    /*
     * fuehrt einen uebergebenen getaggtend Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (so dass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann). Es gibt bisher kein zugehoeriges
     * TestResultInfo-Objekt. Dieses wird neu erzeugt.
     */ 
    private TestExecutionSummary runTaggedTestConfiguration(
    	HashMap<String, TestCaseInfo> testCaseInfoHashMap
        , HashMap<String, TestResultInfo> testResultInfoHashMap
        , ProgressDialog rpd, String tag)
        throws Exception
    {
 		List<Class<?>> testClassesList = new ArrayList<>();
		List<String> testMethodNamesList = new ArrayList<>();
		List<ArrayList<String>> parametersLists = new ArrayList<>();
		this.basicFrameModel.prepareAndSetTestRunParameters(testCaseInfoHashMap, 
			testResultInfoHashMap, testClassesList,
			testMethodNamesList, parametersLists);
		TestExecutionSummary testExecutionSummary = 
		    TestCaseJUnit5Runner.createWithExtendedSummaryListener(testCaseInfoHashMap, 
			testResultInfoHashMap, this, rpd)
			.run(testClassesList, testMethodNamesList, parametersLists, tag);
		testExecutionSummary.printTo(new PrintWriter(System.out));
		return testExecutionSummary;
	}
    

    /*
     * fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (sodass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann). Es gibt bisher kein zugehoeriges
     * TestResultInfo-Objekt. Dieses wird angelegt.
     */
    private TestResultInfo runTestConfiguration(TestCaseInfo tci, 
        ArrayList<TestParameterInfo> parameters, boolean exceptionExpected) 
        throws Exception 
    {
        TestResultInfo resultInfo = null;
        resultInfo = this.basicFrameModel.runTestConfiguration(tci, parameters, 
            exceptionExpected);
        return resultInfo;
    }    
       
      
    /**
     * fuehrt vom Nutzer definierte Einzeltests aus
     *
     * @param numberOfTestsToPerform, Anzahl der auszufuehrenden Tests
     *        Werte fuer die einzelnen Parameter hinterlegt hat
     * @param testCaseInfo auszufuehrender Testfall
     * @param parameters im Rahmen der Testausfuehrung zu nutzende Testparameter
     * @param exceptionExpected gibt an, ob eine Exception im Rahmen der
     *        Testausfuehrung erwartet wird
     * @return Thread , in welchem die Methode ausgefuehrt wird       
     */  
    public Thread runUserDefinedTests(final int numberOfTestsToPerform, 
    	final TestCaseInfo testCaseInfo,
        final ArrayList<ArrayList<TestParameterInfo>> parameters, 
        final boolean exceptionExpected) 
    {
      	final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);

        Thread t = new Thread(() -> {
            int totalNumberOfSuccesses = 0;
            int totalNumberOfFailures = 0;
            int totalNumberOfAbortionsByAssumptions = 0;
            int totalNumberOfSkips = 0;
            rpd.setTotalCapacity(numberOfTestsToPerform);
            String meldung = "";
            int i = 0;
            try{
                long startTimeMs = System.currentTimeMillis();
                for (i = 0; i < numberOfTestsToPerform; i++) {
                    TestResultInfo resultInfo = runTestConfiguration(testCaseInfo,
                        parameters.get(i), exceptionExpected);
                    this.repaintGuiAfterRunTestConfiguration(
                        testCaseInfo, resultInfo);
                    if(testCaseInfo.getTesttype() != TestCaseInfo.TESTTYPE_JUNIT_5){
                        if ((resultInfo.getSuccess() == 0
                            && !resultInfo.isExceptionExpected())
                            || (resultInfo.getSuccess() == 2
                            && resultInfo.isExceptionExpected()))
                        {
                            totalNumberOfSuccesses++;
                            rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                        }
                        else 
                        {
                            totalNumberOfFailures++;
                            rpd.setNumberOfFailures(totalNumberOfFailures);
                        }
                    }
                    else
                    {
                        if ((resultInfo.getSuccess() == TestResultInfo.TESTPASSED
                            && !resultInfo.isExceptionExpected())
                            || (resultInfo.getSuccess() == TestResultInfo.TESTABORTED
                            && resultInfo.isExceptionExpected()))
                        {
                            totalNumberOfSuccesses++;
                            rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                        }
                        else if(resultInfo.getSuccess() 
                        	== TestResultInfo.TESTABORTED_BY_ASSUMPTION){
                            totalNumberOfAbortionsByAssumptions++;
                            rpd.setNumberOfAbortionsByAssumptions(
                            	totalNumberOfAbortionsByAssumptions);
                        }
                        else if(resultInfo.getSuccess() == TestResultInfo.TESTSKIPPED)
                        {
                            totalNumberOfSkips++;
                            rpd.setNumberOfSkips(totalNumberOfSkips);
                        }
                        else 
                        {
                            totalNumberOfFailures++;
                            rpd.setNumberOfFailures(totalNumberOfFailures);
                        }
                    }
                }
                long endTimeMs = System.currentTimeMillis();

                ToastMessage testReportMessage 
                    = new ToastMessage(MessageMaker.makeTestRunFinishedMessage(
                    totalNumberOfSuccesses, totalNumberOfFailures
                    , totalNumberOfAbortionsByAssumptions, totalNumberOfSkips
                    , endTimeMs - startTimeMs), Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                testReportMessage.setVisible(true);
            }
            catch(Exception exc)
            {
                i = numberOfTestsToPerform;
                meldung = exc.getMessage();
            }
            rpd.setVisible(false);
            if(!"".equals(meldung)){
                new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
            }
        });
        t.start();
        return t;
    }
    
    
    /**
     * ueberschreibt das Ergbenis einer bereits frueher gespeicherten und 
     * nun erneut ausgefuehrten Testkonfiguration.
     *
     * @param resultInfo zu ueberschreibende Testkonfiguration
     */
    public void updateTestResult(TestResultInfo resultInfo) 
    {
    	try
    	{
            this.basicFrameModel.updateTestResult(resultInfo);
    	}
    	catch(Exception exc)
    	{
    	    new InfoDialog(FEHLER_AENDERN_TESTKONFIGURATIONEN, exc.getMessage());
    	}    
    }
       
    
    //-------------indirekte Zugriffe auf das BasicFrameModel------------------//

    /**
     * fuehrt alle gespeicherten Konfigurationen einer Liste uebergebener
     * Testfaelle erneut aus
     *
     * @param tcilist Liste von Testfaellen, deren saemtliche Konfigurationen
     *        ausgefuehrt werden sollen
     */
    public void rerunAllTestsOfSeveralTestCases(
        final ArrayList<TestCaseInfo> tcilist) {
        final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);
        // Tests werden in separatem Thread ausgefuehrt, um den Fortschritt
        // in der Anwendung fluessig anzeigen zu koennen
        Thread t = new Thread(() -> {
            int numberOfConfigurationsToRun = 0;
            int maxConfigs = 0; // Maximale Anzahl an Konfigurationen von einem JUnit5-Testfall
            List<TestCaseInfo> nonJupiterTests = new ArrayList<>();
            for (int j = 0; j < tcilist.size(); j++)
            {
                int numberOfAllTestConfigurations = tcilist.get(j)
                    .getNumberOfAllTestConfigurations();
                numberOfConfigurationsToRun
                    += numberOfAllTestConfigurations;
                if(tcilist.get(j).getTesttype() != TestCaseInfo.TESTTYPE_JUNIT_5){
                    nonJupiterTests.add(tcilist.get(j));
                    tcilist.remove(j);
                    j--;
                }
                else if(numberOfAllTestConfigurations > maxConfigs){
                    maxConfigs = numberOfAllTestConfigurations;
                }
            }
            rpd.setTotalCapacity(numberOfConfigurationsToRun);
            String meldung = "";
            HashMap<String, TestCaseInfo> testCaseInfoHashMap = new HashMap<>();
            HashMap<String, TestResultInfo> testResultInfoHashMap = new HashMap<>();
            AtomicInteger totalNumberOfSuccesses = new AtomicInteger();
            AtomicInteger totalNumberOfFailures = new AtomicInteger();
            AtomicInteger totalNumberOfAbortionsByAssumptions = new AtomicInteger();
            AtomicInteger totalNumberOfSkips = new AtomicInteger();
            long startTimeMs = System.currentTimeMillis();
            try {
                // Non-Junit5 Tests ausfuehren:
                Thread t2 = getNonJupiterTestRunThread(rpd, nonJupiterTests,
                        totalNumberOfSuccesses, totalNumberOfFailures);
                t2.start();
                for (int i = 0; i < maxConfigs; i++) {
                    populateTestCaseMaps(tcilist, testCaseInfoHashMap,
                        testResultInfoHashMap, i);
                    runTestConfiguration(testCaseInfoHashMap
                        , testResultInfoHashMap, rpd);
                    // TESTPASSED und TESTFAILED haengt immer davon ab, ob eine Exception 
                    // erwartet wurde,
                    // daher muessen diese Status manuell gezaehlt werden:
                    summariseTestResults(testResultInfoHashMap, totalNumberOfSuccesses,
                            totalNumberOfFailures, totalNumberOfAbortionsByAssumptions,
                            totalNumberOfSkips);
                    testCaseInfoHashMap.clear();
                    testResultInfoHashMap.clear();
                }
                t2.join();
                long endTimeMs = System.currentTimeMillis();
                ToastMessage testReportMessage = new ToastMessage(MessageMaker
                    .makeTestRunFinishedMessage(
                    totalNumberOfSuccesses.intValue(), totalNumberOfFailures
                    .intValue()
                    , totalNumberOfAbortionsByAssumptions.intValue(),
                    totalNumberOfSkips.intValue()
                    , endTimeMs - startTimeMs),
                    Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                testReportMessage.setVisible(true);
            } 
            catch(Exception exc){
                meldung = exc.getMessage();
            }
            rpd.setVisible(false);
            if(!"".equals(meldung))
            {
                new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
            }
        });
        t.start();
    }

    private void summariseTestResults(
        HashMap<String, TestResultInfo> testResultInfoHashMap,
        AtomicInteger totalNumberOfSuccesses,
        AtomicInteger totalNumberOfFailures,
        AtomicInteger totalNumberOfAbortionsByAssumptions,
        AtomicInteger totalNumberOfSkips) {
        testResultInfoHashMap.forEach((key, value) -> 
        {
            if(!value.hasBeenUpdatedInTestRun())
                return;  // continue
            switch (value.getSuccess()) {
                case TestResultInfo.TESTPASSED:
                     totalNumberOfSuccesses.incrementAndGet();
                     break;
                case TestResultInfo.TESTFAILED:
                case TestResultInfo.TESTABORTED:
                     totalNumberOfFailures.incrementAndGet();
                     break;
                case TestResultInfo.TESTABORTED_BY_ASSUMPTION:
                     totalNumberOfAbortionsByAssumptions.incrementAndGet();
                     break;
                case TestResultInfo.TESTSKIPPED:
                     totalNumberOfSkips.incrementAndGet();
                     break;
            }
        });
    }

    private void populateTestCaseMaps(ArrayList<TestCaseInfo> tcilist,
        HashMap<String, TestCaseInfo> testCaseInfoHashMap,
        HashMap<String, TestResultInfo> testResultInfoHashMap,
        int i) 
    {
        for (TestCaseInfo testCaseInfo : tcilist) 
        {
            String fullIdentifier = testCaseInfo.getPackageName()
                + "." + testCaseInfo.getClassName()
                + "." + testCaseInfo.getIdentifierName();
            testCaseInfoHashMap.put(fullIdentifier, testCaseInfo);
            if (i < readTestResultInfo(testCaseInfo).size()) 
            {  // Es gibt noch Testkonfigurationen
                TestResultInfo testResultInfo = readTestResultInfo(testCaseInfo).get(i);
                testResultInfoHashMap.put(fullIdentifier, testResultInfo);
            } 
            else 
            {
                testCaseInfoHashMap.remove(fullIdentifier);
                testResultInfoHashMap.remove(fullIdentifier);
            }
        }
    }

    private Thread getNonJupiterTestRunThread(ProgressDialog rpd,
        List<TestCaseInfo> nonJupiterTests,
        AtomicInteger totalNumberOfSuccesses,
        AtomicInteger totalNumberOfFailures) {
        return new Thread(() ->
       {
            for (TestCaseInfo tci : nonJupiterTests) 
            {
                ArrayList<TestResultInfo> savedTestConfigurations
                    = readTestResultInfo(tci);
                for (TestResultInfo savedConfig : savedTestConfigurations) 
                {
                    TestResultInfo newResult = null;
                    try 
                    {
                        newResult = runTestConfiguration(tci, savedConfig,
                            savedConfig.getParameters(),
                            savedConfig.isExceptionExpected());
                    } 
                    catch (Exception ignored) 
                    { 
                    	System.out.println(
                        	"Exception in getNonJupiterTestRunThread: "
                    	    + ignored.getMessage());
                    }
                    assert newResult != null;
                    if (newResult.getSuccess() != TestResultInfo.TESTNOTFOUND) {
                        // Oberflaeche neu darstellen
                        basicFrameView.repaintGuiAfterRunTestConfiguration(
                            tci, savedConfig);
                    }
                    if ((newResult.getSuccess() == 0
                        && !newResult.isExceptionExpected())
                        || (newResult.getSuccess() == 2
                        && newResult.isExceptionExpected())) {
                        totalNumberOfSuccesses.getAndIncrement();
                        rpd.setNumberOfSuccesses(totalNumberOfSuccesses.get());
                    }
                    else 
                    {
                        totalNumberOfFailures.getAndIncrement();
                        rpd.setNumberOfFailures(totalNumberOfFailures.get());
                    }
                }
            }
        });
    }

    /**
     * fuehrt alle gespeicherten Konfigurationen einer Liste uebergebener
     * Testfaelle erneut und parallel aus. Auch alle Konfigurationen zu einem
     * Testfall werden parallel ausgefuehrt. Kann in manchen Faellen genutzt werden,
     * wenn die Testfaelle dafuer geeignet sind. Testklassen mit statische Variablen 
     * oder Methoden sind nicht geeignet.
     *
     * @param tcilist Liste von Testfaellen, deren saemtliche Konfigurationen
     *        ausgefuehrt werden sollen
     */
    public void rerunAllTestsOfSeveralTestCasesMultithreaded(
            final ArrayList<TestCaseInfo> tcilist)
    {
        final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);
        // Tests werden in separatem Thread ausgefuehrt, um den Fortschritt
        // in der Anwendung fluessig anzeigen zu koennen
        Thread t = new Thread(() -> 
        {
            int numberOfConfigurationsToRun = 0;
            int maxConfigs = 0;
            List<TestCaseInfo> nonJupiterTests = new ArrayList<>();
            for (int j = 0; j < tcilist.size(); j++)
            {
                int numberOfAllTestConfigurations 
                    = tcilist.get(j).getNumberOfAllTestConfigurations();
                numberOfConfigurationsToRun
                    += numberOfAllTestConfigurations;
                if(tcilist.get(j).getTesttype() != TestCaseInfo.TESTTYPE_JUNIT_5)
                {
                    nonJupiterTests.add(tcilist.get(j));
                    tcilist.remove(j);
                    j--;
                }
                else if(numberOfAllTestConfigurations > maxConfigs)
                {
                    maxConfigs = numberOfAllTestConfigurations;
                }
            }
            rpd.setTotalCapacity(numberOfConfigurationsToRun);
            String meldung = "";
            AtomicInteger totalNumberOfSuccesses = new AtomicInteger();
            AtomicInteger totalNumberOfFailures = new AtomicInteger();
            AtomicInteger totalNumberOfAbortionsByAssumptions = new AtomicInteger();
            AtomicInteger totalNumberOfSkips = new AtomicInteger();
            long startTimeMs = System.currentTimeMillis();
            List<Thread> testRuns = new ArrayList<>();
            try 
            {
                // Non-Junit5 Tests ausfuehren:
                Thread t2 = getNonJupiterTestRunThread(
                	rpd, nonJupiterTests, totalNumberOfSuccesses, totalNumberOfFailures);
                testRuns.add(t2);
                t2.start();
                for (int i = 0; i < maxConfigs; i++) 
                {
                    HashMap<String, TestCaseInfo> testCaseInfoHashMap = new HashMap<>();
                    HashMap<String, TestResultInfo> testResultInfoHashMap = new HashMap<>();
                    populateTestCaseMaps(tcilist, testCaseInfoHashMap, testResultInfoHashMap, i);
                    Thread testRunBatch = new Thread(() -> 
                    {
                        TestExecutionSummary testExecutionSummary = null;
                        try {
                            testExecutionSummary 
                                = runTestConfiguration(testCaseInfoHashMap
                                , testResultInfoHashMap, rpd);
                        } 
                        catch (Exception ignored) 
                        { 
                        	System.out.println(
                                "RerunAllTestsOfSeveralTestCasesMultithreaded: "
                            	+ ignored.getMessage());
                        }
                        // TESTPASSED und TESTFAILED haengt immer davon ab, ob eine Exception erwartet wurde,
                        // daher muessen diese Status manuell gezaehlt werden:
                        assert testExecutionSummary != null;
                        summariseTestResults(testResultInfoHashMap, totalNumberOfSuccesses, 
                        	totalNumberOfFailures,
                            totalNumberOfAbortionsByAssumptions, totalNumberOfSkips);
                    });
                    testRuns.add(testRunBatch);
                    testRunBatch.start();
                }
                for(Thread testRun : testRuns){
                    testRun.join();
                }
                long endTimeMs = System.currentTimeMillis();

                ToastMessage testReportMessage 
                    = new ToastMessage(MessageMaker.makeTestRunFinishedMessage(
                    totalNumberOfSuccesses.intValue(), totalNumberOfFailures.intValue()
                    , totalNumberOfAbortionsByAssumptions.intValue(), totalNumberOfSkips.intValue()
                    , endTimeMs - startTimeMs), Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                testReportMessage.setVisible(true);
            }
            catch(Exception exc)
            {
                meldung = exc.getMessage();
            }
            rpd.setVisible(false);
            if(!"".equals(meldung)){
                new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
            }
        });
        t.start();
    }

    /**
     * fuehrt alle gespeicherten Konfigurationen einer Liste uebergebener
     * Testfaelle (welche mit dem uebergebenen Tag getaggt sind) erneut aus
     *
     * @param tcilist Liste von Testfaellen, deren saemtliche Konfigurationen
     *        ausgefuehrt werden sollen
     */
    public void rerunAllTaggedTestsOfSeveralTestCases(ArrayList<TestCaseInfo> tcilist, String tag) 
    {
        final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);
        // Tests werden in separatem Thread ausgefuehrt, um den Fortschritt
        // in der Anwendung fluessig anzeigen zu koennen
        Thread t = new Thread(() -> {
            int numberOfConfigurationsToRun = 0;
            int maxConfigs = 0;
            for (int j = 0; j < tcilist.size(); j++)
            {
                if(tcilist.get(j).getTesttype() == TestCaseInfo.TESTTYPE_JUNIT_5){
                    int numberOfAllTestConfigurations 
                        = tcilist.get(j).getNumberOfAllTestConfigurations();
                    numberOfConfigurationsToRun
                        += numberOfAllTestConfigurations;
                    if(numberOfAllTestConfigurations > maxConfigs){
                        maxConfigs = numberOfAllTestConfigurations;
                    }
                }
                else{
                    tcilist.remove(j);
                    j--;
                }
            }
            rpd.setTotalCapacity(numberOfConfigurationsToRun);
            String meldung = "";
            HashMap<String, TestCaseInfo> testCaseInfoHashMap = new HashMap<>();
            HashMap<String, TestResultInfo> testResultInfoHashMap = new HashMap<>();
            AtomicInteger totalNumberOfSuccesses = new AtomicInteger();
            AtomicInteger totalNumberOfFailures = new AtomicInteger();
            AtomicInteger totalNumberOfAbortionsByAssumptions = new AtomicInteger();
            AtomicInteger totalNumberOfSkips = new AtomicInteger();
            long executionTimeMs = 0;
            try {
                for (int i = 0; i < maxConfigs; i++) {
                    populateTestCaseMaps(tcilist, testCaseInfoHashMap, testResultInfoHashMap, i);
                    TestExecutionSummary testExecutionSummary 
                        = runTaggedTestConfiguration(testCaseInfoHashMap, testResultInfoHashMap, 
                        rpd, tag);
                    // TESTPASSED und TESTFAILED haengt immer davon ab, 
                    // ob eine Exception erwartet wurde,
                    // daher muessen diese Status manuell gezaehlt werden:
                    summariseTestResults(testResultInfoHashMap, totalNumberOfSuccesses,
                        totalNumberOfFailures, totalNumberOfAbortionsByAssumptions,
                        totalNumberOfSkips);
                    executionTimeMs += testExecutionSummary.getTimeFinished() 
                    	- testExecutionSummary.getTimeStarted();
                    testCaseInfoHashMap.clear();
                    testResultInfoHashMap.clear();
                }
                ToastMessage testReportMessage = new ToastMessage(MessageMaker
                    .makeTestRunFinishedMessage(
                    totalNumberOfSuccesses.intValue(), totalNumberOfFailures
                    .intValue()
                    , totalNumberOfAbortionsByAssumptions.intValue()
                    , totalNumberOfSkips.intValue()
                    , executionTimeMs),
                    Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                testReportMessage.setVisible(true);
            }
            catch(Exception exc)
            {
                meldung = exc.getMessage();
            }
            rpd.setVisible(false);
            if(!"".equals(meldung)){
                new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
            }
        });
        t.start();
    }

    /**
     * fuehrt eine Liste uebergebener Testkonfigurationen aus
     *
     * @param tci Testfall, zu dem die Konfigurationen gehoeren
     * @param trilist Liste der auszufuehrenden Testkonfigurationen
     */
    public void rerunTests(final TestCaseInfo tci, 
    	final ArrayList<TestResultInfo> trilist)
    {
        final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);

        Thread t = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                int numberOfConfigurationsToRun = trilist.size();
                int totalNumberOfSuccesses = 0;
                int totalNumberOfFailures = 0;
                int totalNumberOfAbortionsByAssumptions = 0;
                int totalNumberOfSkips = 0;
                rpd.setTotalCapacity(numberOfConfigurationsToRun);
                basicFrameView.getPnlTestergebnisse().getTblResultsModel()
                    .removeAllHighlights();
                int i = 0;
                String meldung = "";
                try
                {
                    long startTimeMs = System.currentTimeMillis();
	                for (i = trilist.size() - 1; i >= 0; i--) 
	                {
	                    TestCaseInfo testCaseInfo = tci;
	                    Boolean exceptionExpected 
	                        = trilist.get(i).isExceptionExpected();
	                    ArrayList<TestParameterInfo> tpilist 
	                        = trilist.get(i).getParameters();
	                    TestResultInfo resultInfo = 
	                        runTestConfiguration(testCaseInfo, trilist.get(i), 
	                        tpilist, exceptionExpected);
	                    // Oberflaeche neu darstellen
	                    basicFrameView.repaintGuiAfterRunTestConfiguration(
	                    	testCaseInfo, trilist.get(i));
                        if(testCaseInfo.getTesttype() != TestCaseInfo.TESTTYPE_JUNIT_5){
                            if ((resultInfo.getSuccess() == 0
                                && !resultInfo.isExceptionExpected())
                                || (resultInfo.getSuccess() == 2
                                && resultInfo.isExceptionExpected()))
                            {
                                totalNumberOfSuccesses++;
                                rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                            }
                            else 
                            {
                                totalNumberOfFailures++;
                                rpd.setNumberOfFailures(totalNumberOfFailures);
                            }
                        }
                        else
                        {
                            if ((resultInfo.getSuccess() == TestResultInfo.TESTPASSED
                                && !resultInfo.isExceptionExpected())
                                || (resultInfo.getSuccess() == TestResultInfo.TESTABORTED
                                && resultInfo.isExceptionExpected()))
                            {
                                totalNumberOfSuccesses++;
                                rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                            }
                            else if(resultInfo.getSuccess() 
                            	== TestResultInfo.TESTABORTED_BY_ASSUMPTION){
                                totalNumberOfAbortionsByAssumptions++;
                                rpd.setNumberOfAbortionsByAssumptions(
                                	totalNumberOfAbortionsByAssumptions);
                            }
                            else if(resultInfo.getSuccess() == TestResultInfo.TESTSKIPPED)
                            {
                                totalNumberOfSkips++;
                                rpd.setNumberOfSkips(totalNumberOfSkips);
                            }
                            else 
                            {
                                totalNumberOfFailures++;
                                rpd.setNumberOfFailures(totalNumberOfFailures);
                            }
                        }
                    }
                    long endTimeMs = System.currentTimeMillis();

                    ToastMessage testReportMessage 
                        = new ToastMessage(MessageMaker.makeTestRunFinishedMessage(
                        totalNumberOfSuccesses, totalNumberOfFailures
                        , totalNumberOfAbortionsByAssumptions, totalNumberOfSkips
                        , endTimeMs - startTimeMs), Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                    testReportMessage.setVisible(true);
                }
                catch(Exception exc)
                {
                    exc.printStackTrace();
                	i = -1;
                    meldung = exc.getMessage();
                }
                rpd.setVisible(false);
                if(!"".equals(meldung)){
                    new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
                }   
            }
        });
        t.start();
    }
    
    /**
     * fuehrt die vom Generator ermittelten optimalen Testparameterkombinationen aus.
     * @param testsToPerform anzahl von Testkombinationen
     * @param combinationList Liste von Testkombinationen
     */
    public void runGeneratedCombinations(final int testsToPerform,
        final ArrayList<Combination> combinationList) 
    {
        final ProgressDialog rpd = new ProgressDialog(INFO_DURCHFUEHREN_TEST);
        rpd.setVisible(true);
        Thread t = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                int totalNumberOfSuccesses = 0;
                int totalNumberOfFailures = 0;
                int totalNumberOfAbortionsByAssumptions = 0;
                int totalNumberOfSkips = 0;
                rpd.setTotalCapacity(testsToPerform);

                // rueckwaerts durchlaufen, damit Testkonfigurationen in beiden 
                // Tabellen gleich geordnet sind
                int i = 0;
                String meldung = "";
                try
                {
                    long startTimeMs = System.currentTimeMillis();
	                for (i = testsToPerform - 1; i >= 0; i--) 
	                {
	                    Combination c = combinationList.get(i);
	                    // ein TestResultInfo wird angelegt,
                        // welches neben dem eigentlichen TestResult 
	                    // spaeter zusaetzliche Informationen (z.B. Parameterwerte) speichert
	                    TestResultInfo resultInfo 
	                        = runTestConfiguration(basicFrameModel.getOpenedTestCase(),
	                        c.pilist, c.exceptionExpected);
	                    // Oberflaeche neu darstellen
	                    basicFrameView.repaintGuiAfterRunTestConfiguration(
	                        basicFrameModel.getOpenedTestCase(), resultInfo);

                        if(basicFrameModel.getOpenedTestCase().getTesttype() 
                            != TestCaseInfo.TESTTYPE_JUNIT_5){
                            if ((resultInfo.getSuccess() == 0
                                && !resultInfo.isExceptionExpected())
                                || (resultInfo.getSuccess() == 2
                                && resultInfo.isExceptionExpected()))
                            {
                                totalNumberOfSuccesses++;
                                rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                            }
                            else {
                                totalNumberOfFailures++;
                                rpd.setNumberOfFailures(totalNumberOfFailures);
                            }
                        }
                        else{
                            if ((resultInfo.getSuccess() == TestResultInfo.TESTPASSED
                                && !resultInfo.isExceptionExpected())
                                || (resultInfo.getSuccess() == TestResultInfo.TESTABORTED
                                && resultInfo.isExceptionExpected()))
                            {
                                totalNumberOfSuccesses++;
                                rpd.setNumberOfSuccesses(totalNumberOfSuccesses);
                            }
                            else if(resultInfo.getSuccess() 
                                == TestResultInfo.TESTABORTED_BY_ASSUMPTION)
                            {
                                totalNumberOfAbortionsByAssumptions++;
                                rpd.setNumberOfAbortionsByAssumptions(
                                	totalNumberOfAbortionsByAssumptions);
                            }
                            else if(resultInfo.getSuccess() == TestResultInfo.TESTSKIPPED)
                            {
                                totalNumberOfSkips++;
                                rpd.setNumberOfSkips(totalNumberOfSkips);
                            }
                            else 
                            {
                                totalNumberOfFailures++;
                                rpd.setNumberOfFailures(totalNumberOfFailures);
                            }
                        }
                    }
                    long endTimeMs = System.currentTimeMillis();

                    ToastMessage testReportMessage 
                        = new ToastMessage(MessageMaker.makeTestRunFinishedMessage(
                        totalNumberOfSuccesses, totalNumberOfFailures
                        , totalNumberOfAbortionsByAssumptions, totalNumberOfSkips
                        , endTimeMs - startTimeMs), Variables.TEST_RUN_TOAST_MSG_DURATION_MS);
                    testReportMessage.setVisible(true);
                }
                catch(Exception exc)
                {
                    i = -1;
                    meldung = exc.getMessage();
                }
                rpd.setVisible(false);
                if(!"".equals(meldung))
                {
                    new InfoDialog(FEHLER_DURCHFUEHREN_TESTFALL, meldung);
                }   
            }
        });
        t.start(); 
    }
    
    /**
     * erstellt updates auf die DB nach einem Signal vom ExtendedSummaryGeneratingListener
     * @param testCaseInfo aktualisiert
     * @param testResultInfo aktualisiert
     */
    public void handleExtendedSummaryGeneratingListenerDB(
    	TestCaseInfo testCaseInfo, TestResultInfo testResultInfo)
    {
        this.updateTestResult(testResultInfo);
        this.readTestConfigurationInfo(testCaseInfo);
    } 
    
    /**
     * erstellt update des GUIs nach einem Signal vom ExtendedSummaryGeneratingListener
     * @param testCaseInfo aktualisiert
     * @param testResultInfo aktualisiert
     */
    public void repaintGuiAfterRunTestConfiguration(
    	 TestCaseInfo testCaseInfo, TestResultInfo testResultInfo)
    {
      	 if (basicFrameView != null)
      	 {	
             basicFrameView.repaintGuiAfterRunTestConfiguration(
                 testCaseInfo, testResultInfo);
      	 }
    }  	
     
    //---------------------Zugriffe auf das BasicFrameXmlModel------------------//
    
    /**
     * gibt das XMLModel - Objekt zu dem Basisfenster heraus
     * @return BasicFrameXmlModel , das XMLModel - Objekt zu dem Basisfenster
     */
    public BasicFrameXmlModel getBasicFrameXmlModel()
    {
        return this.basicFrameXmlModel;
    }   
    
    //-----Zugriffe auf das BasicFrameXmlModel-------------------//
    
    /**
     * extrahiert das JarFile mit dem vorgegebenene Dateinamen, 
     * das XML-Wurzelelement aus der CustomerTestCaseinformation-Datei 
     * und speichert es im Feld rootNode
     *
     * @param filename String, welcher den Dateinamen der jar-Datei enthaelt
     * @throws JDOMException falls XML-Aufbau der zu lesenden 
     *         Testinformation nicht korrekt ist.
     * @throws IOException falls ein Fehler beim Lesen der 
     *         CustomerTestCaseInformation.xml - Datei aufgetreten ist
     */
    public void loadTestCaseInfo(String filename) 
        throws JDOMException, IOException
    {
        this.basicFrameXmlModel 
            = BasicFrameXmlModel.loadTestCaseInfo(filename);
    }   
    
  
    /**
     * liest die Daten zu gespeicherten Testfaellen aus der 
     * XML-Datei und aus der Datenbank
     */
    public void loadTestCaseList() 
    {
        ArrayList<TestCaseInfo> tcilist;
		try {
			tcilist = this.basicFrameXmlModel.getInformationOfAllTestCases();
		}    
		catch (ClassNotFoundException e) 
		{
            new InfoDialog(FEHLER_LADEN_TESTFAELLE, 
            	"Die Datei zum Laden der Testfälle konnte nicht gefunden werden.");
            return;
		}
        for (int i=0; i < tcilist.size(); i++)
	    {
	        this.readTestConfigurationInfo(tcilist.get(i));
	    }   
        java.util.Collections.sort(tcilist);
        if (tcilist != null)
        {
            final ProgressDialog rpd = new ProgressDialog(
            	"Testressourcen werden geöffnet");
            rpd.setVisible(true);
            Thread t = new Thread(new Runnable() 
            {
                @Override
                public void run() 
                {
                    // es wird ein Fortschrittsbalken angezeigt, 
                	// der den Nutzer darauf hinweist, 
                    // dass das Programm arbeitet
                    rpd.setTotalCapacity(tcilist.size());
                    String meldung = "";
                    int progress = 0;  
                    rpd.setNumberOfSuccesses(progress);
                    // das Parametereingabefeld wird geleert
                    basicFrameView.pnlNeueKonfiguration
                        .getPnlParameter().clearUserInterface();
                    // der Navigationsbaum wird geladen
                    basicFrameView.pnlNavigation
                        .loadTestCase(tcilist, rpd, progress);
                    // Das Testpanel wird geladen
                    basicFrameModel.setOpenedTestCase(tcilist.get(0));
                    basicFrameView.openTestCaseInDetail(
                    	basicFrameModel.getOpenedTestCase());
                    // Dialogfenster noch kurz anzeigen und dann schliessen
                    try 
                    {
                        Thread.sleep(500);
                    } 
                    catch (InterruptedException exc) 
                    {
                    	meldung = exc.getMessage();
                    }
                    rpd.setVisible(false);
                    if(!"".equals(meldung))
                    {
                        new InfoDialog(FEHLER_LADEN_TESTFAELLE, meldung);
                    }   
                }
            });
            t.start();
        }   
    }  
    
    //-----Zugriff auf das BasicFrameConfigurationModel------------//
    
    /**
     * oeffent die Anwenderdokumentation zu KBUnit
     */
    public void readUserDocumentationURI()
    {
        try
        {
        	String pathDocumentation 
    	        = basicFrameConfigurationModel.getConfiguration()
    	        .getPathDocumentation();
         	java.io.File file = new java.io.File(pathDocumentation);
       		Desktop.getDesktop().browse(file.toURI());
        }
        catch (IOException exc)
        {
        	new InfoDialog(FEHLER_DOKUMENTATION_NICHT_VERFUEGBAR, 
                "Die Dokumentation kann nicht angezeigt werden. "
                + "Bitte stellen Sie sicher, dass die Datei existiert.");
        }
    }
    
    //-----Zugriff auf das jarFile vom Entwickler------------//
    
   public boolean loadJARFile(File jarFile) 
   {
    	boolean jarWasAddedToClassPath = false;
	    try
	    {
	        URL jarURL = jarFile.toURI().toURL();
            MyClassLoader myClassLoader
	            = new MyClassLoader(new URL[0], this.getClass().getClassLoader());
	        CLASSLOADER = myClassLoader;
	        myClassLoader.addURL(jarURL);
        	jarWasAddedToClassPath = true;
	    }
	    catch (SecurityException |  
	        IllegalArgumentException | MalformedURLException exc)
	    {
	        jarWasAddedToClassPath = false;
	    }
	    return jarWasAddedToClassPath;
    }

}
       


      