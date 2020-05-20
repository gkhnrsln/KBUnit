package prlab.kbunit.test;

import org.jdom2.Element;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.reflection.TestPerformerJUnit5;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SummaryGeneratingListenerExtended ermoeglicht es mehrere Tests gleichzeitig
 * ausfuehren und nach jeden Testrun das Ergebnis festzuhalten.
 * <br>
 * &copy; 2017 Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class ExtendedSummaryGeneratingListener extends SummaryGeneratingListener {

    /* Die in zu den Testmethoden in diesem Testrun zugehoerigen Testergebnisse
       werden hier gespeichet: */
    private Map<String, TestResultInfo> testResultInfoHashMap;
    /* Der Testplan enthaelt alle Testengines, Testklassen und Testmethoden,
    die in diesem Testrun ausgefuehrt werden sollen - in Form eines Baumes: */
    private TestPlan testPlan;
    /* Die Ergebnisse zu den Testmethoden werden in einem XML-Node gespeichert: */
    private Map<String, Element> mapElement;
    /* Die Ergebnisse zu den Testkonfigurationen und zugehoerigen Parameterwerten
    werden in einem XML-Node gespeichert: */
    private Map<String, Element> elementMethodsMap;
    /* Die Auswahl, welcher der Ergebnisse gepeichert und welche ignoriert werden sollen: */
    private Selection selection;
    /* Die es fuer einen Testdurchlauf mehrere Testruns gibt (es befindet sich immer nur jeweils
    eine Konfigurationen zu einer Testmethode in einem Testrun), muessen die Ergebnisse von
    allen Testruns aggregiert werden, was im TestPerformer mit der "multiTestRunMap" passiert : */
    private TestPerformerJUnit5 testPerformer;

    /**
     * Konstruktor, mit Argumenten, die es ermoeglichen mehrere Tests
     * ausfuehren und nach jeden Testrun das Ergebnis festhalten
     * zu koennen.
     * @param testResultInfoHashMap TestResult-Objekte, in denen die Ergebnisse
     *                              gespeichert werden sollen
     */
    public ExtendedSummaryGeneratingListener(Map<String, TestResultInfo> testResultInfoHashMap
        , Map<String, Element> mapElement, Map<String, Element> elementMethodsMap
        , Selection selection, TestPerformerJUnit5 testPerformer){
        this.testResultInfoHashMap = testResultInfoHashMap;
        this.mapElement = mapElement;
        this.elementMethodsMap = elementMethodsMap;
        this.selection = selection;
        this.testPerformer = testPerformer;
    }

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        super.testPlanExecutionStarted(testPlan);
        this.testPlan = testPlan;
    }

    /**
     * Verarbeitet uebersprungene Tests
     */
    @Override
    public synchronized void executionSkipped(TestIdentifier testIdentifier, String reason){
        super.executionSkipped(testIdentifier, reason);
        try {
            if (testIdentifier.getSource().isPresent()
                && testIdentifier.getSource().get() instanceof MethodSource) {
                String testMessage;
                String elementSuffix;
                String className = findTestMethodClassName(testPlan, testIdentifier);
                String methodName = ((MethodSource) testIdentifier.getSource().get()).getMethodName();
                String fullIdentifier = className + "." + methodName;
                String[] result = new String[]{"This test was skipped.", "SKIPPED", "false"};
                testMessage = result[0];
                elementSuffix = result[1];
                Element elementMethod = this.elementMethodsMap.get(fullIdentifier);
                TestResultInfo testMethod = this.testResultInfoHashMap.get(fullIdentifier);
                //CREATES XML-NODE AFTER EACH TEST---------------------------
                elementMethod.setName("ID" + testMethod.getId() + new SimpleDateFormat
                    ("__yyyy-MM-dd__HH.mm.ss____").format(testMethod.getDate())
                    + elementSuffix);
                Element elementInfo = new Element("info");
                elementInfo.setAttribute(Variables.LOG_ATTR_ID,
                    testMethod.getId() + "");
                updateElementAndMultiTestRunMap(testMessage, fullIdentifier, result,
                    elementMethod, testMethod, elementInfo);
            }
        }catch(Exception ignored){ }
    }

    private void updateElementAndMultiTestRunMap(String testMessage, String fullIdentifier,
        String[] result, Element elementMethod,
        TestResultInfo testMethod, Element elementInfo) {
        elementMethod.addContent(0, elementInfo);
        elementMethod.addContent(1, new Element(Variables.LOG_ELEM_MESSAGE)
            .setText(testMessage));
        if(!mapElement.get(fullIdentifier).getChildren().contains(elementMethod)) {
            //EVERY SINGLE TEST-CASE AS NODE OF GENERIC TEST-METHOD-------------------
            mapElement.get(fullIdentifier).addContent(elementMethod);
        }
        if(!this.testPerformer.getMultiTestRunMap().containsKey(testMethod.getId())){
            List<String[]> testResultsList = new ArrayList<>();
            testResultsList.add(result);
            this.testPerformer.getMultiTestRunMap().put(testMethod.getId(), testResultsList);
        }
        else{
            this.testPerformer.getMultiTestRunMap().get(testMethod.getId()).add(result);
        }
    }

    /**
     * Konstruiert zugehoerigen Klassenname aus TestPlan und TestIdentifier
     * Stammt von stackoverflow.com, Post von Vladimir Bogodukhov
     * @see <a href="https://stackoverflow.com/questions/42781020/junit5-is-there-any-reliable-way-to-get-class-of-an-executed-test">HERO-402</a>
     */
    private static String findTestMethodClassName(TestPlan testPlan, TestIdentifier identifier) {
        identifier.getSource().orElseThrow(IllegalStateException::new);
        identifier.getSource().ifPresent(source -> {
            if (!(source instanceof MethodSource)) {
                throw new IllegalStateException("identifier must contain MethodSource");
            }
        });
        TestIdentifier current = identifier;
        while (current != null) {
            if (current.getSource().isPresent() && current.getSource().get() instanceof ClassSource) {
                return ((ClassSource) current.getSource().get()).getClassName();
            }
            current = testPlan.getParent(current).orElse(null);
        }
        throw new IllegalStateException("Class name not found");
    }

    /**
     * Verarbeitet das Testergebnis: aktualisiert das zugehoerige TestResultInfo-Objekt.
     * Aktualisiert Tabelle in Datenbank
     */
    @Override
    public synchronized void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult){
        super.executionFinished(testIdentifier, testExecutionResult);
        try{
            if(testIdentifier.getType() == TestDescriptor.Type.TEST && testIdentifier.getSource().isPresent()
                && testIdentifier.getSource().get() instanceof MethodSource) {
                String testMessage;
                String elementSuffix;
                String goToNextMethod;
                String className = findTestMethodClassName(testPlan, testIdentifier);
                String methodName = ((MethodSource) testIdentifier.getSource().get()).getMethodName();
                String fullIdentifier = className + "." + methodName;
                //EXCEPTION HANDLING PLEASE DO NOT MODIFY----------------
                String[] result = handleTestResult(testExecutionResult, testResultInfoHashMap.get(fullIdentifier));
                testMessage = result[0];
                elementSuffix = result[1];
                goToNextMethod = result[2];
                Element elementMethod = this.elementMethodsMap.get(fullIdentifier);
                TestResultInfo testMethod = this.testResultInfoHashMap.get(fullIdentifier);
                //CREATES XML-NODE AFTER EACH TEST---------------------------
                if("false".equals(goToNextMethod)){
                    elementMethod.setName("ID"+ testMethod.getId() + new SimpleDateFormat
                        ("__yyyy-MM-dd__HH.mm.ss____").format(testMethod.getDate())
                        + elementSuffix);
                    Element elementInfo = new Element("info");
                    elementInfo.setAttribute(Variables.LOG_ATTR_ID,
                        testMethod.getId() + "");
                    elementInfo.setAttribute(Variables.LOG_ATTR_DISPLAYNAME,
                        testIdentifier.getDisplayName() + "");
                    updateElementAndMultiTestRunMap(testMessage, fullIdentifier, result,
                        elementMethod, testMethod, elementInfo);
                }
            }
        } catch(Exception ignored){ }
    }

    // analysing the testResult and giving a testMessage
    private String[] handleTestResult(TestExecutionResult testExecutionResult, TestResultInfo testMethod){
        String[] result = new String[3];
        result[2] = "false";
        /*ERROR*/
        if(testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED
            && (selection != Selection.FAILURE) && (selection != Selection.ABORTED_BY_ASSUMPTION) &&
            (selection != Selection.SKIPPED) && !(Objects.requireNonNull(
                testExecutionResult.getThrowable().orElse(null))
                instanceof AssertionError)) {
            /*
             *  Expected java.lang.Exception and same
             *  Exception has been thrown. The current
             *  test-case has ERROR but its result
             *  has to be turned into SUCCESS!
             */
            if(testMethod.isExceptionExpected()) {
                if(selection == Selection.SUCCESS || selection == Selection.ALL) {
                    result[1] = "SUCCESSFUL";
                    result[0] = "Expected exception thrown: ";
                }
                else{
                    result[2] = "true";
                }
            }
            // Exception not expected but java.lang.Exception thrown.
            else {
                if(selection == Selection.ALL   ||
                    selection == Selection.ERROR ||
                    selection == Selection.FAILURE_ERROR) {
                    result[1] = "ERROR";
                    result[0] = "";
                }
                else{
                    result[2] = "true";
                }
            }

            result[0] += Objects.requireNonNull(testExecutionResult.getThrowable()
                .orElse(null)).getCause()
                + ": " + Objects.requireNonNull(testExecutionResult.getThrowable()
                .orElse(null)).getMessage();
        }
        /*FAILURE*/
        else if(selection != Selection.SUCCESS && selection != Selection.ABORTED_BY_ASSUMPTION
            && selection != Selection.SKIPPED && testExecutionResult.getStatus()
            == TestExecutionResult.Status.FAILED
            && (Objects.requireNonNull(testExecutionResult.getThrowable().orElse(null))
            instanceof AssertionError)) {
            /*
             * Expected java.lang.Exception but only
             * java.lang.AssertionError has been thrown.
             * The current test-case has FAILED, its
             * result has to be turned into ERROR!
             */
            if(testMethod.isExceptionExpected()) {
                if(selection == Selection.ERROR      ||
                    selection == Selection.FAILURE_ERROR ||
                    selection == Selection.ALL) {
                    result[1] = "ERROR";
                    result[0] = "Unexpected exception, expected "
                        + "java.lang.Exception but was "
                        + Objects.requireNonNull(testExecutionResult.getThrowable()
                        .orElse(null)).getCause()
                        + ": " + Objects.requireNonNull(testExecutionResult.getThrowable()
                        .orElse(null)).getMessage();
                }
                else{
                    result[2] = "true";
                }
            }
            // Test-case has failed due only to java.lang.AssertionFailedError:
            else {
                result[1] = "FAILED";
                result[0] = Objects.requireNonNull(testExecutionResult.getThrowable()
                        .orElse(null)).getMessage();
            }
        }
        /*SUCCESS*/
        else if(selection != Selection.ERROR && selection != Selection.ABORTED_BY_ASSUMPTION
            && selection != Selection.SKIPPED &&
            testExecutionResult.getStatus()
                == TestExecutionResult.Status.SUCCESSFUL) {
            /*
             * Expected java.lang.Exception but neither java.lang.Exception
             * or java.lang.AssertionError has been thrown.
             * The current test-case is SUCCESSFUL, but its
             * result has to be turned into FAILURE!
             */
            if(testMethod.isExceptionExpected() &&
                selection != Selection.SUCCESS) {
                result[1] = "FAILED";
                result[0] = "Expected exception not thrown: java.lang.Exception";
            }
            // Test-case has succeeded and no Exception has been thrown:
            else if(!testMethod.isExceptionExpected()  &&
                (selection == Selection.ALL ||
                    selection == Selection.SUCCESS)) {
                result[1] = "SUCCESSFUL";
                result[0] = "This test is successful.";
            }
            else {
                result[2] = "true";
            }
        }
        else if((selection == Selection.ABORTED_BY_ASSUMPTION || selection == Selection.ALL) && testExecutionResult.getStatus()
            == TestExecutionResult.Status.ABORTED){
            result[1] = "ABORTED_BY_ASSUMPTION";
            result[0] = "This test is was aborted .";
        }
        /*
         * If the developer has chosen:
         * <1> Selection.ERROR,   but test-case has no error
         * <2> Selection.FAILURE, but test-case hasn't failed
         * <3> Selection.SUCCESS, but test-case isn't successful
         * <3> Selection.ABORTED_BY_ASSUMPTION, but test-case has not been aborted by assumption
         * Then go to the next testMethod (TestCase):
         */
        else {
            result[2] = "true";
        }
        return result;
    }

}
