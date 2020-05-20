package prlab.kbunit.reflection;

import junit.framework.TestResult;
import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The {@code TestPerformer} class provides a static
 * method for performing JUnitTests using reflection on
 * {@code TestSource}-file and for creating a XML-{@link Document}
 * that contains test-result info associated with every single
 * test-case, which belongs to a given {@code TestObject}.<br>
 *
 * &copy; 2017 Alexander Georgiev, Yannis Herbig, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 * 
 *
 */
public class TestPerformerJUnit4 extends TestPerformer{

	/**
	 * creates an TestPerformer object 
	 * @param testSource, contains informations about the test class
	 * @param testObject, contains the testMethods of the testcase
	 * @param selection, contains informations about, which testclasses 
	 *                   has been selected
	 */
	public TestPerformerJUnit4(TestSource testSource, 
		TestObject testObject, Selection  selection){
		super(testSource, testObject, selection);
	}

	/**
	 * Performs and runs tests (exception-handling is considered)
	 * using reflection on {@link TestSource}-file and orders the test-results,
	 * filtered by {@link Selection}, into {@link Document} to be used by creation
	 * of a .xml-LOG-file as follows:
	 *
	 * <br><b>ROOT : </b>packageName'.'testClassName (i.e. darlehen.TilgungsdarlehenTest)
	 * <br><b>ATTRIBUTE : {@link Variables#LOG_ATTR_CREATE}
	 * <br>           TEXT :</b> date of creating this LOG-file
	 * <br><b>ELEMENT 1 : </b>testMethodName (i.e. testBerechneAnuitaetFuerPeriode)
	 * <br><b>testCase 1.1</b> (i.e. ID314__2015-05-08__12.42.07____FAILED)
	 * <br><b>parameterName 1.1.1</b>
	 * <br><b>parameterName 1.1.2</b>
	 * <br><b>parameterName 1.1.n</b>
	 * <br><b>testCase 1.2</b>
	 * <br><b>testCase 1.n</b>
	 * <br><b>ELEMENT 2 : </b>testMethodName (i.e. testBerechneGesamtschuld)
	 * <br><b>testCase 2.1
	 * <br>  testCase 2.2
	 * <br>testCase 2.n</b>
	 * <br><b>ELEMENT n : </b>testMethodName
	 * <br> <b>testCase n.1
	 * <br>  testCase n.2
	 * <br>testCase n.n</b>
	 *
	 * @return XML-Document to be used for creating LOG-file
	 */
	@Override
	public Document createDocument() {

		TestResult testResult = null;
		String     testMessage   = "";
		String     elementSuffix = "";
		ArrayList<TestResultInfo> testMethods = getTestObject().getTestMethods();

		//TEST-CLASS AS ROOT ELEMENT
		Element elementClass = new Element(getTestSource().getClazz().getName());
		elementClass.setAttribute(new Attribute(Variables.LOG_ATTR_CREATE,
			Variables.LOG_TIMESTAMP));

		Document doc = new Document(elementClass);

		Map<String,Element> mapElement = new HashMap<String, Element>();

		String[] methods = getTestSource().getTestMethodsByName();
		for(int i=0; i < methods.length; i++) {
			/*
			 *  This Map includes only Elements of the original
			 *  test-Methods (not the test-cases from the client's database!),
			 *  Their names serve as generic ones for these nodes,
			 *  which represent single test-cases. At last every test-case
			 *  belongs to its original test-Method in the tree-structure
			 *  of the .XML-LOG-file.
			 */
			mapElement.put(methods[i], new Element(methods[i]));
		}

		TestResultInfo testMethod = null;
		String goToNextMethod = null;
		String testParameterValue = null;

		ArrayList<TestParameterInfo> newparameters = new ArrayList<TestParameterInfo>();
		Field[] fields = getTestSource().getClazz().getFields();
		List<String> parameternames = new ArrayList<>();
		for (Field field : fields) {
			parameternames.add(field.getName());
		}
		//SORT PARAMETERS
		for(int i = 0; i < testMethods.size(); i++){
			testMethod = testMethods.get(i);

			System.out.println(testMethod.getDate().getTime());
			System.out.println(testMethod.getDate());
			System.out.println(testMethod.getDate().getHours());

			Element elementMethod = new Element("default");

			for(int j = 0;j < parameternames.size();j++) {
				for(int k = 0; k < testMethod.getParameters().size();k++) {
					if(parameternames.get(j).equals(testMethod.getParameters()
						.get(k).getName())) {
						newparameters.add(testMethod.getParameters().get(k));
					}
				}
			}

			//SET NEW TEST-PARAMETERS FOR EVERY TEST-CASE----------------------
			for(TestParameterInfo testParameter : newparameters) {
				//TEST-PARAMETERS AS NODES OF EVERY SINGLE TEST-CASE------------------
				try{
					testParameterValue = getTestSource().getTestParameterTypeValue(testParameter);
					elementMethod.addContent(new Element
						(testParameter.getName())
						.setText(testParameterValue));
				}
				catch(NoSuchFieldException nsfExc){
					nsfExc.printStackTrace();
				}
			}

			//RUNS SINGLE TEST-CASE-------------------------------
			testResult = ((TestSourceJUnit4)getTestSource()).runTestCaseJUnit4(testMethod);

			//EXCEPTION HANDLING PLEASE DO NOT MODIFY----------------
			String[] result = handleTestResult(testResult, testMethod);
			testMessage = result[0];
			elementSuffix = result[1];
			goToNextMethod = result[2];

			//CREATES XML-NODE AFTER EACH TEST---------------------------
			if("false".equals(goToNextMethod)){
				elementMethod.setName("ID"+ testMethod.getId() + new SimpleDateFormat
					("__yyyy-MM-dd__HH.mm.ss____").format(testMethod.getDate())
					+ elementSuffix);

				Element elementInfo = new Element("info");

				elementInfo.setAttribute(Variables.LOG_ATTR_ID,
					testMethod.getId() + "");

				elementMethod.addContent(0, elementInfo);
				elementMethod.addContent(1, new Element(Variables.LOG_ELEM_MESSAGE)
					.setText(testMessage));

				//EVERY SINGLE TEST-CASE AS NODE OF GENERIC TEST-METHOD-------------------
				mapElement.get(testMethod.getName()).addContent(elementMethod);
			}
			newparameters.clear();
		}

		for(Map.Entry<String, Element> e : mapElement.entrySet()) {

			if(e.getValue().getContentSize() == 0)
				e.getValue().addContent
					(new Comment("Selection."+ getSelection() +":  No such test cases found!"));

			//GENERIC TEST-METHODS AS NODES OF THE CLASS ROOT-ELEMENT------------------
			doc.getRootElement()
				.addContent(e.getValue());
		}
		return doc;
	}	
	
	// analysing the testResult and giving a testMessage
	private String[] handleTestResult(TestResult testResult, TestResultInfo testMethod){
		String[] result = new String[3];	
		result[2] = "false";
		/*ERROR*/ 
		if((getSelection() != Selection.FAILURE) && (testResult.errorCount() > 0)) {
			/*
			 *  Expected java.lang.Exception and same
			 *  Exception has been thrown. The current
			 *  test-case has ERROR but its result
			 *  has to be turned into SUCCESS!
			 */
			if(testMethod.isExceptionExpected()) {

				if(getSelection() == Selection.SUCCESS || getSelection() == Selection.ALL) {

					result[1] = "SUCCESSFUL";
					result[0] = "Expected exception thrown: ";
				} 
				else{
					result[2] = "true";
				}
			} 
			// Exception not expected but java.lang.Exception thrown.
			else {

				if(getSelection() == Selection.ALL   ||
					getSelection() == Selection.ERROR || 
					getSelection() == Selection.FAILURE_ERROR) {

					result[1] = "ERROR";
					result[0] = "";
				} 
				else{
					result[2] = "true";
				}	
			}

			result[0] += testResult.errors() 
				.nextElement()
				.thrownException()
				.getClass()
				.getCanonicalName() 
				+ ": " + testResult.errors()
				.nextElement()
				.exceptionMessage();
		} 
		/*FAILURE*/
		else if(((getSelection() != Selection.ERROR && getSelection() != Selection.SUCCESS) || 
				(getSelection() == Selection.ERROR && testMethod.isExceptionExpected()))  && 
				(testResult.failureCount() > 0)) {
			/*
			 * Expected java.lang.Exception but only
			 * java.lang.AssertionError has been thrown.
			 * The current test-case has FAILED, its
			 * result has to be turned into ERROR!
			 */
			if(testMethod.isExceptionExpected()) {
				if(getSelection() == Selection.ERROR      || 
					getSelection() == Selection.FAILURE_ERROR || 
					getSelection() == Selection.ALL) {
					result[1] = "ERROR";
					result[0] = "Unexpected exception, expected "
						+ "java.lang.Exception but was "
						+ testResult.failures() 
						.nextElement()
						.thrownException()
						.getClass()
						.getSuperclass()
						.getCanonicalName() +": "
						+ testResult.failures()
						.nextElement()
						.exceptionMessage();
				} 
				else{
					result[2] = "true";
				}
			}              
			// Test-case has failed due only to java.lang.AssertionFailedError:	
			else {
				result[1] = "FAILED";
				result[0] = testResult.failures()
					.nextElement()
					.exceptionMessage();
			}
		}    
		/*SUCCESS*/ 
		else if(getSelection() != Selection.ERROR && 
				testResult.wasSuccessful()) {
			/*
			 * Expected java.lang.Exception but neither java.lang.Exception 
			 * or java.lang.AssertionError has been thrown. 
			 * The current test-case is SUCCESSFUL, but its
			 * result has to be turned into FAILURE!
			 */
			if(testMethod.isExceptionExpected() && 
					getSelection() != Selection.SUCCESS) {
				result[1] = "FAILED";
				result[0] = "Expected exception not thrown: java.lang.Exception";
			} 
			// Test-case has succeeded and no Exception has been thrown:
			else if(!testMethod.isExceptionExpected()  && 
				(getSelection() == Selection.ALL ||
				getSelection() == Selection.SUCCESS)) {
				result[1] = "SUCCESSFUL";
				result[0] = "This test is successful.";
			} 
			else {
				result[2] = "true";
			}	
		}    
		/*
		 * If the developer has chosen:
		 * <1> Selection.ERROR,   but test-case has no error
		 * <2> Selection.FAILURE, but test-case hasn't failed
		 * <3> Selection.SUCCESS, but test-case isn't successful
		 * Then go to the next testMethod (TestCase):
		 */
		else {
			result[2] = "true";
		}
		return result;
	}


}
