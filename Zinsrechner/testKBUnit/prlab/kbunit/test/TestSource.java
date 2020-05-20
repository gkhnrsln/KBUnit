package prlab.kbunit.test;

import prlab.kbunit.enums.Variables;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom2.JDOMException;


/**
 * The {@code TestSource} class provides through
 * reflection suitable methods for getting information
 * only about {@link Method}s and declared {@link Field}s
 * with the prefix <b>"test"</b>. <br>
 *  
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 *  
 * @author Alexander Georgiev
 *  
 */  
public abstract class TestSource extends ClassCreator{

	// contains the Method-Objects of test methods
	protected Method[]              testMethods;
	// contains the names of the test methods
	protected String[]              testMethodsByName;
	// contains the fully-qualified-method-names of the test methods
	protected String[]              testMethodsByFQMN;
	// contains the fields of the class with the test methods
	protected Map<String, Class<?>> mapFieldTypes;
	// contains the parameters of each test method in form of lists of strings
	protected Map<String, ArrayList<String>> mapMethodsParameters = new HashMap<String, ArrayList<String>>();
	// contains the path of the test classes which have to be generated
	private String                  pathNewGeneratedTestCases;

	/**
	 * creates an TestSourceJUnit object in
	 * dependency of the given file
	 * @param source, file which contains the test class
	 * @param prefix, String which contans the prefix of the test class
	 * @return an TestSourceJUnit object or an TestSourceJBehave object
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static TestSource getInstance(File source, String prefix) throws JDOMException, IOException{
		TestSource result = null;
		CustomerTestCaseInformationLoader ctcil = CustomerTestCaseInformationLoader.getInstance();
		switch(ctcil.getTesttype(source.getPath())){
			case 4: result = new TestSourceJUnit4(source, Variables.TEST_SOURCE);
					break;
			case 5: result = new TestSourceJUnit5(source, Variables.TEST_SOURCE);
					break;
		}
		return result;
	}

	/**
	 * Constructs a new class of a 
	 * given source-{@link File}.
	 * @param source {@code File} from which an 
	 *        {@code Class} object will be created
	 * @param prefix String, which contains the prefix of the testclass       
	 * @see {@link ClassCreator#ClassCreator(File)}
	 */
	protected TestSource(File source, String prefix) {
		super(source, prefix);
	}
	
	/**
	 * returns the names of all test methods of the test class.
	 * In JBehave one gets the scenarios
	 * @return String array, which contains the names of all test methods 
	 *         of the test class
	 */
	public String[] getTestMethodsByName(){
		return testMethodsByName;
	}

	/**
	 * returns the fully qualified names of all test methods of the test class.
	 * @return String array, which contains the fully qualified names of all test methods
	 *         of the test class
	 */
	public String[] getTestMethodsByFQMN(){
		return testMethodsByFQMN;
	}

	/**
	 * returns the path of the new generated TestCases
	 * @return String , which contains the path of the new generated TestCases
	 */
	public String getPathNewGeneratedTestCases(){
		return pathNewGeneratedTestCases;
	}
	
	/**
	 * sets the path of the new generated TestCases
	 * @param pathNewGeneratedTestCases String , which contains the new value 
	 *        for the path of the new generated TestCases
	 */
	public void setPathNewGeneratedTestCases(String pathNewGeneratedTestCases){
		this.pathNewGeneratedTestCases = pathNewGeneratedTestCases;
	}
	
	/**
	 * Returns (the type and) the value of the testParameter of a given class.
	 * @param testParameter, the testParameter
	 * @return String, which contains (the type and) the value of the testParameter
	 * @throws NoSuchFieldException if given Field is not known in the class
	 */
	public abstract String getTestParameterTypeValue(TestParameterInfo testParameter)
	    throws NoSuchFieldException;  
	
	/**
	 * Returns the type of a test-parameter (and only this with prefix 
	 * "<b>test</b>") of a given class.
	 * @param paramName the name of test-case parameter
	 * @return the type of the class modeled by this Class object. 
	 *         <br>For example, the type of String.class is String. 
	 */
	public Class<?> getTestParameterType(String paramName) {
		return mapFieldTypes.get(paramName);
	}
	
	/**
	 * Returns all test-parameters (and only these with prefix 
	 * "<b>test</b>") of a given class as 
	 * <br><code>Map&lt;paramName, paramType&gt;</code>
	 * @return {@link Map} of test-parameters
	 */
	public Map<String, Class<?>> getTestParameterTypes() {
		return mapFieldTypes;
	}
	
	/**
	 * Returns the paths of all test-methods of given class
	 * <br> i.e. multiplier.MultiplierTest.testGetResult
	 * @return test-methods as array of {@code String}
	 */
	public String[] getTestPaths() {
		String[] str = new String[testMethods.length];
		
		for(int i=0; i < testMethods.length; i++) {
			str[i] = testMethods[i].getDeclaringClass()
					         .getCanonicalName() 
					+ "."+ testMethods[i].getName();	
		}
		return str;
	}
	
		
	/**
	 * Provides a simple class-name for a
	 * new (test-)class to be created.
	 * <br> i.e. MultiplierTestKBUNit
	 * 
	 * @return simple name for the new (test-)class
	 *     <br>with suffix = {@link Variables#CLASS_NAME_SUFFIX} 
	 */
	public abstract String getNewSimpleName();

	 
	/**
	 * Provides path name for new file where {@link Variables#TEST_SOURCE} 
	 * <br> has been changed with {@link Variables#TEST_NEW_SOURCE}
	 * <br> and class-name gets new suffix {@link Variables#CLASS_NAME_SUFFIX}
	 * <p> <b>Example:</b>
	 * <br>test\multiplier\MultiplierTest.java
	 * <br>changed into:
	 * <br>testKBUnit\multiplier\MultiplierTestKBUnit.java
	 * @return new file-path as String
	 */
    public abstract String getNewFilePath();
    	
	/**
	 * gives information, if the name of the field starts with the name of a testmethod 
	 * @param name of the field
	 * @return true, if the name of the field starts with the name of a testmethod, 
	 *               false othewise 
	 */
	protected boolean startsWithNameOfTestmethod(String fieldName){
		boolean result = false;
		int i = 0;
		while(i < testMethodsByName.length && !result){
			if(fieldName.startsWith(testMethodsByName[i])){
				result = true;
			}
			i++;
		}
		return result;
	}

}
