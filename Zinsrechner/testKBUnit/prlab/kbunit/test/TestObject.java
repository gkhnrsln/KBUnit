package prlab.kbunit.test;
   
import java.util.ArrayList;

/**
 * {@code TestObject} is a generic class, that holds 
 * every single test-case represented by a TestMethod,
 * which in its turn contains none, one or many 
 *  TestParameters. This class provides
 * two methods for adding TestMethods 
 * in {@code ArrayList} and getting it back.  <br>                                       
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 *  
 * @author Alexander Georgiev
 *
 */
public class TestObject {
	
	// contains TestResultInfo-Objects
	private ArrayList<TestResultInfo> testMethods;
	
	/**
	 * Initializes an empty ArrayList of TestMethod-Objects
	 */
	public TestObject(){
		this.testMethods = new ArrayList<TestResultInfo>(); 
	}
	   
	/**
	 * Adds new TestMethod in the ArrayList 
	 * @param testMethod which will be added
	 */
	public void addTestMethod(TestResultInfo testMethod){
		this.testMethods.add(testMethod);
	}
	
	/**
	 * Lists all TestMethods of given JUnitTest-class
	 * @return all TestMethods as {@link ArrayList}
	 */
	public ArrayList<TestResultInfo> getTestMethods(){
		return this.testMethods;  
	}
	
	/**
	 * sets the TestMethods with new values
	 * @param tests , ArrayList of TestResultInfo objects contains the new TestMethods
	 */
	public void setTestMethods (ArrayList<TestResultInfo> tests){
		this.testMethods = tests;
	}

}
