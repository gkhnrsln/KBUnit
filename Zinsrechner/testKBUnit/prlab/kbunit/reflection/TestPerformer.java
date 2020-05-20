package prlab.kbunit.reflection;

import org.jdom2.Document;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.*;


/**
 * The {@code TestPerformer} class provides a static
 * method for performing JUnitTests using reflection on
 * {@code TestSource}-file and for creating a XML-{@link Document}
 * that contains test-result info associated with every single
 * test-case, which belongs to a given {@code TestObject}.<br>
 *
 * &copy; 2017 Alexander Georgiev, Yannis Herbig, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev, Yannis Herbig
 * 
 *
 */
public abstract class TestPerformer {

	// containing the testMethods of the testcase
	private TestObject testObject;
	// contains informations about the test class
	private TestSource testSource;
	// contains informations about, which testclasses has been selected
	private Selection selection;
	
	/**
	 * creates an TestPerformer object 
	 * @param testSource, contains informations about the test class
	 * @param testObject, contains the testMethods of the testcase
	 * @param selection, contains informations about, which testclasses 
	 *                   has been selected
	 */
	public TestPerformer(TestSource testSource, 
		TestObject testObject, Selection  selection){
		this.testSource = testSource;
		this.testObject = testObject;
		this.selection = selection;
	}  
	
	/**
	 * creates an TestPerformer object 
	 */
	public TestPerformer(){
	}  	
	
	// Start: getters und setters
	protected TestObject getTestObject() {
		return testObject;
	}

	protected void setTestObject(TestObject testObject) {
		this.testObject = testObject;
	}

	protected TestSource getTestSource() {
		return testSource;
	}

	protected void setTestSource(TestSource testSource) {
		this.testSource = testSource;
	}

	protected Selection getSelection() {
		return selection;
	}

	protected void setSelection(Selection selection) {
		this.selection = selection;
	}	
	// Ende: getters und setters

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
	public abstract Document createDocument();
}
