package prlab.kbunit.test;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.reflection.Parser;
import prlab.kbunit.reflection.TestPerformerJUnit5;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *  
 * &copy; 2017 Yannis Herbig, Ursula Oesing  <br>
 * 
 * @author Yannis Herbig
 */
public class TestSourceJUnit5 extends TestSourceJUnit{

	/**
	 * Creates a TestsourceJUnit5 Object
	 * @param source File, which contains the testclass
	 * @param prefix String, which contains the prefix of the testclass
	 */
	public TestSourceJUnit5(File source, String prefix){
		super(source, prefix);
	}

	/**
	 * Initializes an array of Method objects. In JUnit5 the testmethods
	 * are annotated with @Test, @ParameterizedTest, @RepeatedTest, @TestFacory or @TestTemplate.
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
			if(mAll[i].isAnnotationPresent(Annotation.class)){
				/* Eventuell benoetigt fuer Meta-Annotationen:
				Annotation[] annotations = mAll[i].getDeclaredAnnotations();
				Outer:
				for(Annotation methodAnnotation : annotations){
					for(Annotation annotationOfAnnotation : methodAnnotation.annotationType().getAnnotations()) {
						if (annotationOfAnnotation.annotationType() == Test.class || annotationOfAnnotation.annotationType() == ParameterizedTest.class
							|| annotationOfAnnotation.annotationType() == RepeatedTest.class || annotationOfAnnotation.annotationType() == TestFactory.class
							|| annotationOfAnnotation.annotationType() == TestTemplate.class) {
							list.add(mAll[i]);
							break Outer;
						}
					}
				}
				 */
			}
			if(mAll[i].isAnnotationPresent(Test.class)){
				list.add(mAll[i]);
			}
			else if(mAll[i].isAnnotationPresent(RepeatedTest.class)){
				list.add(mAll[i]);
			}
			else if(mAll[i].isAnnotationPresent(ParameterizedTest.class)){
				list.add(mAll[i]);
			}
			else if(mAll[i].isAnnotationPresent(TestFactory.class)){
				list.add(mAll[i]);
			}
			else if(mAll[i].isAnnotationPresent(TestTemplate.class)){
				list.add(mAll[i]);
			}

		}
		testMethods = new Method[list.size()];
		testMethodsByName = new String[list.size()];
		testMethodsByFQMN = new String[list.size()];
		String className = super.getClazz().getTypeName();
		for(int i=0; i < testMethods.length; i++) {
			testMethods[i] = list.get(i);
			testMethodsByName[i] = testMethods[i].getName();
			testMethodsByFQMN[i] = className + "." + testMethods[i].getName();
			Class<?>[] methodArgumentsTypes = testMethods[i].getParameterTypes();
			ArrayList<String> parameters = new ArrayList<>();
			for(Class<?> clazz : methodArgumentsTypes){
				parameters.add(clazz.getTypeName());
			}
			mapMethodsParameters.put(testMethods[i].getName(), parameters);
		}
	}
	
	/**
	 * implements the running of the testcase
	 * @param testMethod the testMethod, which will be tested
	 * @return TestResult, the result of the running of the testcase
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public TestExecutionSummary runTestCaseJUnit5(TestResultInfo testMethod) throws JDOMException, IOException{
		TestExecutionSummary summary = null;
		try{
	        Class<?> clazz = super.getClazz();
		    // the static attributes of the testCase must get the actual values
	        Field staticField;
	        Object obj;
	        for(int i = 0; i < testMethod.getParameters().size(); i++){
			    staticField = clazz
				    .getDeclaredField(testMethod.getParameters().get(i).getName());
			    staticField.setAccessible(true);
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
	        TestRunnerJUnit5 jUnit5TestRunner = TestRunnerJUnit5.createWithStandardSummaryListener();
			summary =
				jUnit5TestRunner.run(new ArrayList<>(Arrays.asList(clazz)),
				new ArrayList<>(Arrays.asList(testMethod.getName())),
				new ArrayList<>(Arrays.asList(mapMethodsParameters.get(testMethod.getName()))));
			summary.printTo(new PrintWriter(System.out));
        } 
		catch(NoSuchFieldException | IllegalAccessException ignored){ }
		return summary;
	}

	/**
	 * implements the running of several testcases
	 * @param testMethods the testMethods, which will be tested
	 * @return TestExecutionSummary, the result of the running of the testcase
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static TestExecutionSummary runTestCases(Map<String, TestResultInfo> testMethods
		, Map<String, Element> mapElement, Map<String, Element> elementMethodsMap
		, Selection selection, TestPerformerJUnit5 testPerformer)
		throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, JDOMException, IOException {
		List<Class<?>> testClassesList = new ArrayList<>();
		List<String> testMethodNamesList = new ArrayList<>();
		List<ArrayList<String>> parametersLists = new ArrayList<>();
		for(Map.Entry<String, TestResultInfo> testMethod : testMethods.entrySet()) {
			Class<?> clazz = Class.forName(testMethod.getValue().getPackageName()
				+ "." + testMethod.getValue().getClassName());
			testClassesList.add(clazz);
			testMethodNamesList.add(testMethod.getValue().getIdentifierName());
			parametersLists.add(testMethod.getValue().getArgumentsTypes());
			// the static attributes of the testCase must get the actual values
			Field staticField;
			Object obj;
			for (int i = 0; i < testMethod.getValue().getParameters().size(); i++) {
				staticField = clazz
					.getDeclaredField(testMethod.getValue().getParameters().get(i).getName());
				staticField.setAccessible(true);
				Object value = testMethod.getValue().getParameters().get(i).getValue();
				if (value != null) {
					obj = Parser.parseParameterValue(staticField.getType(),
						value.toString());
				} else {
					obj = null;
				}
				staticField.set(null, obj);
			}
		}
		TestRunnerJUnit5 jUnit5TestRunner =
			TestRunnerJUnit5.createWithExtendedSummaryListener(testMethods, mapElement, elementMethodsMap
				, selection, testPerformer);
		TestExecutionSummary testExecutionSummary =
			jUnit5TestRunner
				.run(testClassesList, testMethodNamesList, parametersLists);
		testExecutionSummary.printTo(new PrintWriter(System.out));
		return testExecutionSummary;
	}
}
