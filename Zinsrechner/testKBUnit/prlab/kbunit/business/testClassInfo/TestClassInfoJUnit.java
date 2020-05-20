package prlab.kbunit.business.testClassInfo;

import java.io.File;
import java.io.IOException;

import org.jdom2.JDOMException;

/** 
 * Abstrakte Klasse speichert die geladene Testklasse.
 * <br>
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */ 

public class TestClassInfoJUnit extends TestClassInfo {

	public TestClassInfoJUnit(int testtype, File file) 
		throws JDOMException, IOException {
		super(testtype, file);
	}

	
	/**
	    * gibt den Namen derjenigen Klasse zurueck, in welcher die 
	    * Testparameter liegen
	    *
	    * @return Name derjenigen Klasse zurueck, in welcher die 
	    * Testparameter liegen
	    */
	   public String getClassNameParameter()
	   {
		   return this.getClassName();
	   }

}
