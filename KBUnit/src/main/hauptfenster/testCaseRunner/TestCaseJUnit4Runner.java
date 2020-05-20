package main.hauptfenster.testCaseRunner;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

/**
 * TestCaseJUnit4 ist ein TestCase fuer JUnit4. Im Gegensatz zu JUnit3
 * leitet ein Testfall nicht mehr von der Klasse TestCase ab. Da KBUnit  
 * aber die Methode run benoetigt, die ein TestResult zurueckgibt, muss
 * diese fuer JUnit4 Tests nachgebildet werden.
 * 
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */
   
public class TestCaseJUnit4Runner 
{
	
	// vorliegender Testfall
	private Object testCase;
	// name der Testmethode
	private String name;
			 
	/**
     * Konstruktor fuer den vorliegenden Testfall.
     * @param testCase , enthaelt den auszufuehrenden Testfall
     * @param name , enthaelt den Namen der Testmethode
     */ 
	public TestCaseJUnit4Runner(Object testCase, String name)
	{
		this.testCase = testCase;
		this.name = name;
	}
	
	/**
	 * fuehrt den JUnit4-Testfall aus
	 * @return TestResult , das Ergebnis der Testfall-Ausfuehrung
	 */ 
	public TestResult run()
	{
		Request request = Request.method(this.testCase.getClass(), this.name);
		Result result = new JUnitCore().run(request);
	    return this.resultToTestResult(result);
	}
	
	// konvertiert ein vorliegendes Result-Objekt in ein TestResult - Objekt
	private TestResult resultToTestResult(Result result)
	{
		List<Failure> failureList = result.getFailures();
		int failureCount = result.getFailureCount();
		Failure failure = null;
		TestResult testResult = new TestResult();
		for(int i = 0; i < failureCount; i++)
		{
			failure = failureList.get(i);
			if(failure.getException() instanceof AssertionError)
			{	
				testResult.addFailure(null, new AssertionFailedError(
					failure.getException().getMessage()));
			}
			else
			{   
				testResult.addError(null, failure.getException());
			}
		}
		return testResult;
	}
}
