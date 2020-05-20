package main.hauptfenster.testCaseInfo;

/**
 * Testfall speichert alle Informationen eines Testfalls im Falle von JUnit - Tests, 
 * also Pfad, Version und Beschreibung des Testzwecks und enthaelt eine ArrayList der zugehoerigen 
 * einzelnen Testkonfiguration.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */ 
public class TestCaseInfoJUnit extends TestCaseInfo{

   /**
    * Konstruktor
    * @param testtype Testtyp (JUnit4 oder JUnit5 , ...)
    * @param path Pfad des Testfalls (Package.Klasse.Methode)
    * @param desc Beschreibung des Testfalls
    */
   public TestCaseInfoJUnit(int testtype, String path, String desc)
   {
       super(testtype, path, desc);
   }
   
   /**
    * gibt den Namen derjenigen Klasse zurueck, in welcher die Testparameter liegen
    *
    * @return Name derjenigen Klasse zurueck, in welcher die Testparameter liegen
    */
   public String getClassNameParameter()
   {
	   return this.getClassName();
   }
}
