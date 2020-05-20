package main.hauptfenster.testCaseRunner;

import org.jdom2.JDOMException;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * TestCaseJUnit5 ist ein TestCase fuer JUnit5. Im Gegensatz zu JUnit3
 * leitet ein Testfall nicht mehr von der Klasse TestCase ab. Da JUnit5
 * kein TestResult-Typ besitzt, implementiert TestCaseJUnit5
 * nicht das TestCaseInterface
 * 
 * <br>
 * &copy; 2017 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
   
public class TestCaseJUnit5Runner_SimpleDemo 
{

	// vorliegender Testfall
	private Object testCase;
	// name der Testmethode
	private String name;
	// Falls vorhanden, die Datentypen der Testmethode
	public ArrayList<String> argumentsTypes;

	/**
     * Konstruktor fuer den vorliegenden Testfall.
     * @param testCase , enthaelt den auszufuehrenden Testfall
     * @param name , enthaelt den Namen der Testmethode
     */
	public TestCaseJUnit5Runner_SimpleDemo(Object testCase, String name, 
		ArrayList<String> argumentsTypes)
	{
		this.testCase = testCase;
		this.name = name;
		this.argumentsTypes = argumentsTypes;
	}
	
	/**
	 * fuehrt den JUnit5-Testfall aus
	 * @return TestExecutionSummary , das Ergebnis der Testfall-Ausfuehrung
	 */ 
	public TestExecutionSummary run() throws JDOMException, IOException {
		TestExecutionSummary summary 
		    = TestCaseJUnit5Runner.createWithStandardSummaryListener()
			.run(Arrays.asList(this.testCase.getClass())
            , Arrays.asList(this.name), Collections.singletonList(this.argumentsTypes));
		summary.printTo(new PrintWriter(System.out));
	    return summary;
	}

}
