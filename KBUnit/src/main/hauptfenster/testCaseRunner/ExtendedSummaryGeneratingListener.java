package main.hauptfenster.testCaseRunner;

import main.dialogfenster.ProgressDialog;
import main.hauptfenster.BasicFrameControl;
import main.hauptfenster.TestResultInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * SummaryGeneratingListenerExtended ermoeglicht es mehrere Tests gleichzeitig
 * ausfuehren und nach jeden Testrun das Ergebnis festzuhalten.
 * <br>
 * &copy; 2017 Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbg
 */
public class ExtendedSummaryGeneratingListener extends SummaryGeneratingListener 
{

    /* Die zu den Testmethoden zugehoerigen TestCaseInfo-Objekte werden mithilfe
    diese Map aktualisiert: */
    private Map<String, TestCaseInfo> testCaseInfoHashMap;
    /* Die zu den Testmethoden zugehoerigen TestResultInfo-Objekte werden mithilfe
    diese Map aktualisiert: */
    private Map<String, TestResultInfo> testResultInfoHashMap;
    /* Eine Referenz zum BasicFrameControl wird gehalten, um Datenbank zu aktualisieren,
       und die Gui zu aktualisieren: */
    private BasicFrameControl basicFrameControl;
    /* Der Erfolgsbalken wird mithilfe dieser Referenz aktualisiert: */
    private ProgressDialog rpd;
    /* Der Testplan enthaelt alle Testengines, Testklassen und Testmethoden,
    die in diesem Testrun ausgefuehrt werden sollen - in Form eines Baumes: */
    private TestPlan testPlan;
    /*
    Die zu den Testmethoden zugehoerigen TestCaseInfo-Objekte werden mithilfe
    diese Map aktualisiert
     */
    private Map<String, Boolean> seenMap;

    /**
     * Konstruktor, mit Argumenten, die es ermoeglichen mehrere Tests
     * ausfuehren und nach jeden Testrun das Ergebnis festhalten
     * zu können.
     * @param testCaseInfoHashMap Testfallinformationen
     * @param testResultInfoHashMap TestResult-Objekte, in denen die Ergebnisse
     *                              gespeichert werden sollen
     * @param basicFrameControl BasicFrameControl der Applikation
     * @param rpd ProgressBar des Testdurchlaufs
     */
    public ExtendedSummaryGeneratingListener(Map<String, TestCaseInfo> testCaseInfoHashMap
        , Map<String, TestResultInfo> testResultInfoHashMap
        , BasicFrameControl basicFrameControl, ProgressDialog rpd)
    {
        this.testCaseInfoHashMap = testCaseInfoHashMap;
        this.testResultInfoHashMap = testResultInfoHashMap;
        this.basicFrameControl = basicFrameControl;
        this.rpd = rpd;
        this.seenMap = new HashMap<>();
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) 
    {
        super.testPlanExecutionStarted(testPlan);
        this.testPlan = testPlan;
    }

    /**
     * Verarbeitet uebersprungene Tests
     */
    @Override
    public synchronized void executionSkipped(TestIdentifier testIdentifier, String reason)
    {
        super.executionSkipped(testIdentifier, reason);
        if(testIdentifier.getSource().isPresent()
            && testIdentifier.getSource().get() instanceof MethodSource) 
        {
            String className = findTestMethodClassName(testPlan, testIdentifier);
            String methodName = ((MethodSource) testIdentifier.getSource().get()).getMethodName();
            String fullIdentifier = className + "." + methodName;
            if(!seenMap.containsKey(fullIdentifier) || !seenMap.get(fullIdentifier)
                || (seenMap.get(fullIdentifier)
                && (this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTFAILED)
                && (this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTABORTED)
                && (this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTABORTED_BY_ASSUMPTION))) 
            {
                // der Erfolg des Testlaufs wird aus result ermittelt und in resultInfo
                // festgehalten
                int testSuccess = TestResultInfo.TESTSKIPPED;

                TestResultInfo testResultInfo = this.testResultInfoHashMap.get(fullIdentifier);
                testResultInfo.setSuccess(testSuccess);
                testResultInfo.setWasUpdatedInTestRun(true);
                testResultInfo.setMessage(reason);
                // die Informationen zum Testfall (Pfad und Version) werden ebenfalls
                // in resultInfo gespeichert
                TestCaseInfo testCaseInfo = this.testCaseInfoHashMap.get(fullIdentifier);
                testResultInfo.setPath(testCaseInfo.getPath());
                // Testergebnis in Tabelle abspeichern
                this.basicFrameControl.handleExtendedSummaryGeneratingListenerDB(
                    testCaseInfo, testResultInfo);
                // Gui aktualisieren
                this.basicFrameControl.repaintGuiAfterRunTestConfiguration(
                    testCaseInfo, testResultInfo);
                // Progressbar aktualisieren
                if(rpd != null)
                    rpd.setNumberOfSkips(rpd.getNumberOfSkips() + 1);
                seenMap.put(fullIdentifier, true);
            }
        }
    }

    /**
     * Konstruiert zugehoerigen Klassenname aus TestPlan und TestIdentifier
     * Stammt von stackoverflow.com, Post von Vladimir Bogodukhov
     * @see <a href="https://stackoverflow.com/questions/42781020/junit5-is-there-any-reliable-way-to-get-class-of-an-executed-test">Get Classname</a>
     */
    private static String findTestMethodClassName(TestPlan testPlan, TestIdentifier identifier) 
    {
        identifier.getSource().orElseThrow(IllegalStateException::new);
        identifier.getSource().ifPresent(source -> 
        {
            if (!(source instanceof MethodSource)) 
            {
                throw new IllegalStateException("identifier must contain MethodSource");
            }
        });
        TestIdentifier current = identifier;
        while (current != null) 
        {
            if (current.getSource().isPresent() && current.getSource().get() instanceof ClassSource) 
            {
                return ((ClassSource) current.getSource().get()).getClassName();
            }
            current = testPlan.getParent(current).orElse(null);
        }
        throw new IllegalStateException("Class name not found");
    }

    /**
     * Verarbeitet das Testergebnis: aktualisiert das zugehoerige TestCaseInfo
     * und TestResultInfo-Objekt. Aktualisiert Tabelle in Datenbank, aktualisiert
     * ProgressBar
     */
    @Override
    public synchronized void executionFinished(TestIdentifier testIdentifier,
        TestExecutionResult testExecutionResult)
    {
        super.executionFinished(testIdentifier, testExecutionResult);
        if(testIdentifier.getType() == TestDescriptor.Type.TEST
            && testIdentifier.getSource().isPresent()
            && testIdentifier.getSource().get() instanceof MethodSource) 
        {
            try
            {
                String className = findTestMethodClassName(testPlan, testIdentifier);
                String methodName = ((MethodSource) testIdentifier.getSource().get())
                    .getMethodName();
                String fullIdentifier = className + "." + methodName;
                if(!seenMap.containsKey(fullIdentifier) || !seenMap.get(fullIdentifier)
                    || (seenMap.get(fullIdentifier)
                    && this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                    != TestResultInfo.TESTFAILED))
                {
                    TestResultInfo testResultInfo =  this.testResultInfoHashMap
                        .get(fullIdentifier);
                    if(seenMap.containsKey(fullIdentifier) && seenMap.get(fullIdentifier))
                    {
                        updateProgressBar(testResultInfo, true);
                    }
                    // der Erfolg des Testlaufs wird aus result ermittelt und in resultInfo
                    // festgehalten
                    int testSuccess;
                    String testMessage = "";
                    testSuccess = getTestSuccess(testExecutionResult, fullIdentifier);
                    if(testExecutionResult.getStatus() != TestExecutionResult.Status.SUCCESSFUL)
                        testMessage
                            = Objects.requireNonNull(testExecutionResult.getThrowable()
                            .orElse(null)).getMessage();
                    testResultInfo.setSuccess(testSuccess);
                    testResultInfo.setWasUpdatedInTestRun(true);
                    // Die Laenge der Message anpassen, falls sie fuer das Feld in der Tabelle zu lang ist:
                    testMessage = testMessage.length() > 250 ? testMessage.substring(0, 247) + "..." : testMessage;
                    testResultInfo.setMessage(testMessage);
                    // die Informationen zum Testfall (Pfad und Version) werden ebenfalls
                    // in resultInfo gespeichert
                    TestCaseInfo testCaseInfo = this.testCaseInfoHashMap.get(fullIdentifier);
                    testResultInfo.setPath(testCaseInfo.getPath());
                    // Testergebnis in Tabelle abspeichern
                    this.basicFrameControl.handleExtendedSummaryGeneratingListenerDB(
                        testCaseInfo, testResultInfo);
                    if(testResultInfo.getSuccess() != TestResultInfo.TESTNOTFOUND)
                    {
                        basicFrameControl.repaintGuiAfterRunTestConfiguration(
                            testCaseInfo, testResultInfo);
                    }
                    updateProgressBar(testResultInfo, false);
                    seenMap.put(fullIdentifier, true);
                }
            }catch (Exception ignored){ }
        }
    }
    
    /**
     * Aktualisierung der Zaehlvariablen, die zur Aktualisierung der Progress-Bar
     * genutzt werden. Wenn das TestResultInfo-Objekt einen Testcontainer darstellt,
     * so kann es sein, dass ein vorheriges Ergebnis ueberschrieben werden muss,
     * beispielsweise wenn das Ergebnisstatus zuerst erfolgreich und dann
     * fehlgeschlagen ist: das heisst die Zaehlvariable muss manchmal dekrementiert werden.
     * @param testResultInfo TestResultInfo-Objekt von dem abhängig vom Ergebnis die
     *                       zugehoerige Zaehlvariable aktualisiert werden soll
     * @param decrement Property, die aussagt, ob die Zaehlvariable zu dem Erfolgsstatus
     *                  des Testfalls inkrementiert oder dekremetiert werden soll
     */
    private void updateProgressBar(TestResultInfo testResultInfo, boolean decrement) {
        if (((testResultInfo.getSuccess() == TestResultInfo.TESTPASSED
            && !testResultInfo.isExceptionExpected())
            || (testResultInfo.getSuccess() == TestResultInfo.TESTABORTED
            && testResultInfo.isExceptionExpected())) && rpd != null)
        {
            if(decrement){
           	    rpd.setNumberOfSuccesses(rpd.getNumberOfSuccesses() - 1);
            }
            else{
                rpd.setNumberOfSuccesses(rpd.getNumberOfSuccesses() + 1);
            }
        }
        else if (testResultInfo.getSuccess() == TestResultInfo.TESTABORTED_BY_ASSUMPTION
            && rpd != null)
        {
            if(decrement){
                rpd.setNumberOfAbortionsByAssumptions(rpd.getNumberOfAbortionsByAssumptions() - 1);
            }
            else{
                rpd.setNumberOfAbortionsByAssumptions(rpd.getNumberOfAbortionsByAssumptions() + 1);
            }
        }
        else if(rpd != null)
        {
            if(decrement){
                rpd.setNumberOfFailures(rpd.getNumberOfFailures() - 1);
            }
            else{
                rpd.setNumberOfFailures(rpd.getNumberOfFailures() + 1);
            }
        }
    }

    private int getTestSuccess(TestExecutionResult testExecutionResult,
                               String fullIdentifier) {
        int testSuccess;
        if(testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED
                && (Objects.requireNonNull(testExecutionResult.getThrowable()
                .orElse(null))
                instanceof AssertionError)){
            testSuccess = TestResultInfo.TESTFAILED;
        }
        else if(testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED
                && !(Objects.requireNonNull(testExecutionResult.getThrowable()
                .orElse(null))
                instanceof AssertionError)){
            testSuccess = TestResultInfo.TESTABORTED;
        }
        else if (testExecutionResult.getStatus()
                == TestExecutionResult.Status.ABORTED
                && (!seenMap.containsKey(fullIdentifier) || !seenMap.get(fullIdentifier)
                || this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTABORTED)) {
            testSuccess = TestResultInfo.TESTABORTED_BY_ASSUMPTION;
        }
        else if (testExecutionResult.getStatus()
                == TestExecutionResult.Status.SUCCESSFUL
                && (!seenMap.containsKey(fullIdentifier) || !seenMap.get(fullIdentifier)
                || (this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTABORTED
                && this.testResultInfoHashMap.get(fullIdentifier).getSuccess()
                != TestResultInfo.TESTABORTED_BY_ASSUMPTION))) {
            testSuccess = TestResultInfo.TESTPASSED;
        }
        else {
            testSuccess = TestResultInfo.TESTFAILED;
        }
        return testSuccess;
    }

}
