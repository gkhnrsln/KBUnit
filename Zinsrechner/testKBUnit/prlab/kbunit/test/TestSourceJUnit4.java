package prlab.kbunit.test;

import junit.framework.TestResult;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import prlab.kbunit.reflection.Parser;
import prlab.kbunit.reflection.ResultSeparator;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 *  
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Ursula Oesing
 */
public class TestSourceJUnit4 extends TestSourceJUnit{
 
	/** 
	 * kreates a TestsourceJUnit4 Object
	 * @param source File, which contains the testclass
	 * @param prefix String, which contains the prefix of the testclass
	 */
	public TestSourceJUnit4(File source, String prefix){
		super(source, prefix);
	}
	
	/**
	 * Initializes an array of Method objects. In JUnit4 all the testmethods 
	 * have an annotation @Test.
	 */
    @Override
    protected void initMethods(){		
		ArrayList<Method> list = new ArrayList<Method>();
		/*
		 * getDeclaredMethods() returns only these methods,
		 * which are declared in the given class and not
		 * the methods declared in the superclass.
		 */
		Method[] mAll  = super.getClazz().getDeclaredMethods();
		for(int i=0; i < mAll.length; i++){
			if(mAll[i].isAnnotationPresent(Test.class)){
				list.add(mAll[i]);
			}
		}
		testMethods = new Method[list.size()];
		testMethodsByName = new String[list.size()];
		for(int i=0; i < testMethods.length; i++) {
			testMethods[i] = list.get(i);
			testMethodsByName[i] = testMethods[i].getName();
		}
	}   
	
	/**
	 * implements the running of the testcase
	 * @param testMethod the testMethod, which will be tested
	 * @return TestResult, the result of the running of the testcase
	 */
	public TestResult runTestCaseJUnit4(TestResultInfo testMethod){
		TestResult testResult = new TestResult();
		try{
	        Class<?> clazz = super.getClazz();
		    // the static attributes of the testCase must get the actual values
	        Field staticField;
	        Object obj;
	        for(int i = 0; i < testMethod.getParameters().size(); i++){
			    staticField = clazz
				    .getDeclaredField(testMethod.getParameters().get(i).getName());
			    Object value = testMethod.getParameters().get(i).getValue();
			    if(value != null){
	    	        obj = Parser.parseParameterValue(staticField.getType(),
		    	        value.toString());
			    }    
			    else{
				    obj = null;
			    }
		        staticField.set(null, obj);
		    } 
	        testResult = ResultSeparator.getTestResult(
			    new JUnitCore().run(Request.method(clazz, testMethod.getName())));
        } 
		catch(NoSuchFieldException | IllegalAccessException exc){
			testResult.addError(null, exc);
		}
		return testResult;
	}	
}
