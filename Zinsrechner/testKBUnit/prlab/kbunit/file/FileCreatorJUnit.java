package prlab.kbunit.file;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.TestObject;
import prlab.kbunit.test.TestParameterInfo;
import prlab.kbunit.test.TestResultInfo;
import prlab.kbunit.test.TestSourceJUnit;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
* The {@code FileCreatorJUnit} class provides methods for
* copying files for testing with JUnit.<br> 
*
* &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
* 
* @author Ursula Oesing
* 
*/
public abstract class FileCreatorJUnit extends FileCreator{
	
	// contains informations about the test class
	private TestSourceJUnit testSource;
	
	/**
	 * creating a FileCreatorJUnit object. Because this class is abstract,
	 * it is only used by deriving classes.
	 * @param testSource, contains informations about the test class 
	 * @param testObject, contains the testMethods of the test case
	 */	
	public FileCreatorJUnit(TestSourceJUnit testSource, TestObject testObject){
		super(testObject);
		this.testSource = testSource;
    }
	
	/**
	 * returning the testSource attribut
	 * @return TestSourceJUnit, which contains informations about the test class
	 */
	public TestSourceJUnit getTestSource(){
		return testSource;
	}
	
	/**
	 * the deriving classes have to implement, 
	 * whether the TestSource is copied yet or not.
	 * JUnit tests are always new.
	 */ 
	public boolean isNewTestSource(){
	    return true;	
	}
	
	/*
	 * Returns list of all new test methods prepared for coping a given
	 * test source file. Exception handling is also included.
	 */
    protected List<String> getJUnitMethods() {
		
		List<String> listAll    = new ArrayList<>();
		List<String> listMethod = new ArrayList<>();
		
		//Lists all test methods of given JUnit test-class
		for(TestResultInfo testMethod : super.getTestObject().getTestMethods()) {
			
			//The list has to be initially reset in case of more than one source-test-methods
			listMethod.clear();

			//-------------------ADDING COMMENTARY BEFORE @Test-ANNOTATION-----------------------
			listMethod.add("\t// "+ new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
			    .format(testMethod.getDate()));
			
			//------------------ADDING @Test AND EVENTUALLY EXPECTED EXCEPTION-------------------
			listMethod.add(createAnnotation(testMethod));
			
			//----------------------DECLARING FIRST LINE OF TEST METHOD---------------------------
			listMethod.add("\tpublic void "
			    + testMethod.getName() +"_"
			    + testMethod.getId() 
			    + "() throws Exception {");
			
			//---------------------ADDING TEST PARAMETER TO CURRENT TEST METHOD-------------------
			List<TestParameterInfo> listParameters = testMethod.getParameters();
			Object value;
			for(TestParameterInfo param : listParameters) {
				value = param.getValue();
				if(value != null){
				    listMethod.add("\t\t"+ param.getName()
			            + " = "
			            + super.adjustValue(param.getClassType(), 
			            value.toString()) + ";");
				}
				else{
					listMethod.add("\t\t"+ param.getName()
			            + " = "
			            + super.adjustValue(param.getClassType(), 
			            null) + ";");
				}
			}
		
			//--------------------------ADDING SOURCE-TEST-METHOD---------------------------------
			listMethod.add(createMethodCall(testMethod));
			
			//------------------------------END OF TEST METHOD------------------------------------
			listMethod.add("\t}\n");
			
			//--------------ADDING COLLECTION TO LINE-LIST OF CURRENT TEST-SOURCE-----------------
			listAll.addAll(listMethod);
		}
		
		return listAll;
	}

    /*
	 * derived classes have to implement a method, which contains the
	 * annotation of a test method, which shall be copied
	 */
    protected abstract String createAnnotation(TestResultInfo testMethod);
    
    /*
   	 * derived classes have to implement the call of the original test method
   	 */
    protected abstract String createMethodCall(TestResultInfo testMethod);
	
}
