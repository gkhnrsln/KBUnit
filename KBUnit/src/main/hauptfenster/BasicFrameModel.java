package main.hauptfenster;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.*;
import com.sun.jersey.api.client.WebResource.Builder;
  
import hilfe.TypeConvertation;

import junit.framework.TestResult;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.hauptfenster.testCaseRunner.*;

import org.jdom2.JDOMException;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * BasicFrameModel ist das Model des Hauptfensters der KBUnit-Anwendung. 
 * Es realisiert den Anschluss an die Datenbank und damit die Speicherung und
 * das Auslesen von Testlaeufen.
 * In dem Hauptfenster werden JUnit-Tests gekapselt und ueber diese grafische
 * Benutzeroberflaeche via Reflection zur Bearbeitung zur verfuegung gestellt.
 * <br>
 * &copy; 2020 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig, Ursula Oesing
 */
public final class BasicFrameModel 
{
 
    // aktuell in der Anwendung geoeffneter Testfall 
    // (wird auf der rechten Seite mit Konfigurationen angezeigt)
    private TestCaseInfo openedTestCase;

    // Pfad des Web-Services
    private String pathWebResource;
    
    // Client - Objekt fuer den Web Service
    private Client client;    

    /**
     * gibt den aktuell in der Anwendung geoeeffenten Testfall aus
     * @return TestCaseInfo , den aktuell in der Anwendung geoeeffenten Testfall
     */
    public TestCaseInfo getOpenedTestCase() 
    {
        return openedTestCase;
    }
    
    /**
     * setzt den aktuell in der Anwendung geoeeffenten Testfall neu
     * @param testcase TestCaseInfo , den neu in der Anwendung zu oeffnenden Testfall
     */
    public void setOpenedTestCase(TestCaseInfo testcase)
    {
        this.openedTestCase = testcase;
    }   
    
    // das einzige BasicFrameModel - Objekt (singleton - Pattern)
    private static BasicFrameModel basicFrameModel;
    
    // Konstruktor zum einmaligen Kreiren eines BasicFrameModel 
    // - Objekt (singleton - Pattern)
    private BasicFrameModel (String pathWebResource)
    {
    	this.pathWebResource = pathWebResource;
    	this.client = Client.create();
    }
    
    /**
     * gibt das einzige BasicFrameModel - Objekt heraus (singleton - Pattern).
     * Falls es noch nicht existiert, wird es erst kreiert.
     * @param pathWebResource String, welcher den Ort des Web Services enthaelt
     * @return das einzige BasicFrameModel - Objekt
     */
    public static BasicFrameModel getInstance(String pathWebResource)
    {
        if(basicFrameModel == null)
        {
            basicFrameModel = new BasicFrameModel(pathWebResource);
        }    
        return basicFrameModel;
    } 
     
     
    /**
     * liest die Informationen zu allen Testlaeufen eines speziellen Testfalls
     * (spezifiziert durch Pfad und Version) aus
     * @param tci TestCaseInfo des zu lesenden TestResults
     * @return ArrayList , enthaelt die gelesenen Informationen zu allen
     *         Testlaeufen des vorgegebenen Testfalls in Form von TestResultInfo-Objekten
	 * @throws Exception , Name und Meldung der Exceptionklasse 
     */
    public ArrayList<TestResultInfo> readTestResultInfo(TestCaseInfo tci) 
        throws Exception
    {
     	ArrayList<TestResultInfo> tests = new ArrayList<TestResultInfo>();
     	TestResultInfo[] testsGelesen = new TestResultInfo[0];
       	WebResource resource = client.resource(
            this.pathWebResource
            + "/readTestcases/" + tci.getPath());
       	Builder builder = resource.accept(MediaType.APPLICATION_JSON);
    	String s = builder.get(String.class);
    	ObjectMapper mapper = new ObjectMapper();
    	try
    	{
    		if(!"".equals(s))
    		{
   	            testsGelesen = mapper.readValue(s, TestResultInfo[].class);
    		}    
    	}
    	catch(IOException exc)
    	{
    		throw new Exception(s);
    	}
    	try
    	{
      	    Class<?> classWithParameter = Class.forName(tci.getPackageName() + "." 
      	       + tci.getClassNameParameter(), true, BasicFrameControl.CLASSLOADER);
      	    for (int i = 0; i < testsGelesen.length; i++){
       		    tests.add(testsGelesen[i]);
       		    for (int j = 0; j < tests.get(i).getParameters().size(); j++){
      			    Class<?> classType = classWithParameter
      				    .getDeclaredField(tests.get(i)
      				    .getParameters().get(j).getName()).getType();
       			    tests.get(i).getParameters().get(j).setClassType(classType);
       			    tests.get(i).getParameters().get(j)
       			        .setValue(convertValue(tests.get(i).getParameters().get(j)));
       		    }
       		    ArrayList<TestParameterInfo> help 
       		        = tci.sortTestParamInfo(tests.get(i).getParameters());
       		    tests.get(i).setParameters(help);
       	    }
    	}
       	catch(ClassNotFoundException | NoSuchFieldException exc)
    	{
    		throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
    	}
 
        // Fuellen des TestCaseInfo-Objekts mit den zugehoerigen TestResultInfo-Objekten
        tci.setTestResultInfoList(tests);
        // Liste aller Testlaeufe zurueckgeben
        return tests;
    }

    // konvertiert den Wert des vorgegebenen TestParameters
    private Object convertValue(TestParameterInfo tpi) 
        throws NoSuchFieldException, ClassNotFoundException
    {
       	Object result = null;
       	if(tpi.getValue() != null)
       	{
            result = TypeConvertation.convertString(tpi.getClassType(), 
            	tpi.getValue().toString());
       	}
       	else
       	{
       		result = null;
       	}
        return result;
    }  
    
    /**
     * liest die im Programm verwendeten Informationen zu Anzahl und
     * Erfolg gespeicherter Testkonfigurationen zum Testfall
     * @param tci TestCaseInfo, dessen Daten gelesen werden sollen 
  	 * @throws Exception , Name und Meldung der Exceptionklasse 
     */
    public final void readTestConfigurationInfo(TestCaseInfo tci) 
        throws Exception
    {
        tci.setNumberOfAllTestConfigurations( 
            this.readNumberOfTestcases(tci.getPath()));
        tci.setNumberOfAllSuccessfulTestConfigurations(
            this.readNumberOfSuccessfulTestcases(tci.getPath()));
		tci.setNumberOfAllByAssumptionsAbortedTestConfigurations(
		    this.readNumberOfByAssumptionsAbortedTestcases(tci.getPath()));
		tci.setNumberOfAllSkippedTestConfigurations(
			this.readNumberOfSkippedTestcases(tci.getPath()));
    }

    /**
     * liest die im Programm verwendeten Informationen zu Anzahl und
     * Erfolg gespeicherter Testkonfigurationen zum geoeffneten Testfall
  	 * @throws Exception , Name und Meldung der Exceptionklasse 
     */
    public final void readTestConfigurationInfo() 
        throws Exception
    {
        this.openedTestCase.setNumberOfAllTestConfigurations( 
            this.readNumberOfTestcases(this.openedTestCase.getPath()));
        this.openedTestCase.setNumberOfAllSuccessfulTestConfigurations(
            this.readNumberOfSuccessfulTestcases(this.openedTestCase.getPath()));
		this.openedTestCase.setNumberOfAllByAssumptionsAbortedTestConfigurations(
			this.readNumberOfByAssumptionsAbortedTestcases(this.openedTestCase.getPath()));
		this.openedTestCase.setNumberOfAllSkippedTestConfigurations(
			this.readNumberOfSkippedTestcases(this.openedTestCase.getPath()));
    }
    
    
    /**
     * loescht einen in der Datenbank gespeichertern Testlauf inklusive der 
     * zugehoerigen Parameter
     *
     * @param id eindeutige Identifikationsnummer des zu loeschenden Testlaufs
   	 * @throws Exception, Name und Meldung der Exceptionklasse 
     */
    public void deleteTestResultInfo(int id) 
        throws Exception
    {
        WebResource resource = client.resource(
        	this.pathWebResource
            + "/deleteTestcase/" + id);
      	Builder builder = resource.accept(MediaType.TEXT_PLAIN);
      	String result = builder.get(String.class);
	    TestResultInfo tri;
	    tri = new TestResultInfo();
	    tri.setId(id);
	    for(int i = 0; i < this.openedTestCase.getTestResultInfoList().size(); i++)
        {
            if(this.openedTestCase.getTestResultInfoList().get(i).getId() == tri.getId())
            {
                this.openedTestCase.getTestResultInfoList().remove(i);
            }
        }    
  		if(!"".equalsIgnoreCase(result))
  		{	
		     throw new Exception(result);
		}
	}        
        
    /**
     * fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (sodass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann)
     *
     * @param tci auszufuehrender Testfall
     * @param tri Testkonfiguration des Testfalls, die auszufuehren 
     * @param parameters im Rahmen der Testausfuehrung zu nutzende Testparameter
     * @param exceptionExpected gibt an, ob eine Exception im Rahmen der
     *   Testausfuehrung erwartet wird
     *
     * @return Ergebnis der Testausfuehrung
   	 * @throws Exception , Name und Meldung der Exceptionklasse 
     */
    public TestResultInfo runTestConfiguration(TestCaseInfo tci, TestResultInfo tri,
        ArrayList<TestParameterInfo> parameters, boolean exceptionExpected) 
        throws Exception
    {
        TestResultInfo resultInfo = tri;
        try 
        {
			this.loadAndRunTestCase(tci, resultInfo, parameters, exceptionExpected, false);
		} 
        catch (NoSuchFieldException | IllegalArgumentException 
        	| IllegalAccessException | InstantiationException
			| InvocationTargetException | NoSuchMethodException 
			| SecurityException | ClassNotFoundException exc)
        {
		    throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
		}
        // Testergebnis in Tabelle abspeichern
        this.updateTestResult(resultInfo);
        // tci informationen updaten 
        this.readTestConfigurationInfo(tci);
        return resultInfo;
    }

	/**
     * fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern 
     * aus, legt das Ergebnis (Konfiguration) in der Datenbank ab und liefert es 
     * gleichzeitig an den Aufrufer zurueck (sodass dieses weiterverarbeitet, 
     * also z.B. in Tabelle angezeigt werden kann). Es gibt bisher kein zugehoeriges
     * TestResultInfo-Objekt. Dieses wird neu erzeugt.
     *
     * @param tci auszufuehrender Testfall
     * @param parameters im Rahmen der Testausfuehrung zu nutzende Testparameter
     * @param exceptionExpected gibt an, ob eine Exception im Rahmen der
     *   Testausfuehrung erwartet wird
     *
     * @return Ergebnis der Testausfuehrung
     * @throws Exception , Name und Meldung der Exceptionklasse 
     */ 
    public TestResultInfo runTestConfiguration(TestCaseInfo tci, 
        ArrayList<TestParameterInfo> parameters, boolean exceptionExpected) 
        throws Exception
    {
        TestResultInfo resultInfo = new TestResultInfo();
        try 
        {
			this.loadAndRunTestCase(tci, resultInfo, parameters, exceptionExpected, true);
		} 
        catch (NoSuchFieldException | IllegalArgumentException 
        	| IllegalAccessException | InstantiationException
			| InvocationTargetException | NoSuchMethodException 
			| SecurityException | ClassNotFoundException exc) 
        {
            throw new Exception(exc.getClass().getSimpleName() + ": " 
                + exc.getMessage());
		}
        // Testergebnis in Tabelle abspeichern
        int id = (int) this.writeTestResultInfo(resultInfo);
        resultInfo.setId(id);
        tci.getTestResultInfoList().add(resultInfo);
        // tci informationen updaten 
        this.readTestConfigurationInfo(tci);
        return resultInfo;
    } 
       
    // fuehrt einen uebergebenen Testfall mit den uebergebenen Parametern aus
    private void loadAndRunTestCase(TestCaseInfo tci, TestResultInfo resultInfo,
        ArrayList<TestParameterInfo> parameters, boolean exceptionExpected, boolean isNew)
		throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException,
		InstantiationException, InvocationTargetException, NoSuchMethodException,
		SecurityException, ClassNotFoundException, JDOMException, IOException 
    {
    	// der Testfall wird geladen
    	Class<?> testclass = Class.forName(tci.getPackageName() + "." 
    	    + tci.getClassName(), true, BasicFrameControl.CLASSLOADER);

         Class<?> classWithParameter = createClassWithParameter(testclass, tci);
        // es wird ausgelesen, ob Nutzer Exception erwartet, der Erwartungswert 
        // wird in TestResultInfo abgelegt
        resultInfo.setExceptionExpected(exceptionExpected);
        // Die Attribute der Testklasse, die zu den Testmethoden gehoeren, 
        // werden mit Werten belegt, und die ParameterInfo wird hinzugefuegt.

    	Object convFieldParameter;
       	for (int i = 0; i < parameters.size(); i++)
       	{
       		TestParameterInfo param = parameters.get(i);
       		// das Feld der Testklasse wird nun mit dem Inhalt der Eingabekomponente  
       		// gefuellt und die Parameterinformationen bereits in resultInfo abgelegt
       		convFieldParameter 
               	= TypeConvertation.setFieldParameter(classWithParameter, param.getName(), 
               	param.getValue());
            if(isNew)
        	{    
        		resultInfo.addTestParameter(param.getName(), convFieldParameter);
        	}       
        }
        // Die Testmethode wird ueber das jeweilige Testframework ausgefuehrt und das 
        // Ergebnis in result gespeichert.
    	// Zuerst wird ein TestResult (nicht zu verwechseln mit TestResultInfo) 
       	// wird angelegt
		// der Erfolg des Testlaufs wird aus result ermittelt und in resultInfo
		// festgehalten
		int testSuccess;
		String testMessage;
		if(tci.getTesttype() != TestCaseInfo.TESTTYPE_JUNIT_5)
		{
			TestCaseJUnit4Runner testCase 
			    = new TestCaseJUnit4Runner(testclass.getDeclaredConstructor()
		    	.newInstance(), tci.getIdentifierName());
			TestResult result = testCase.run();
			if (result.errorCount() > 0)
			{
				testSuccess = TestResultInfo.TESTABORTED;
				testMessage
					= result.errors().nextElement().thrownException()
					.getClass().getCanonicalName()
					+ ": " +  result.errors().nextElement().exceptionMessage();
			}
			else if(result.failureCount() > 0)
			{
				testSuccess = TestResultInfo.TESTFAILED;
				testMessage = result.failures().nextElement().exceptionMessage();
			}
			else
			{
				testSuccess = TestResultInfo.TESTPASSED;
				testMessage = "";
			}
			resultInfo.setSuccess(testSuccess);
			resultInfo.setMessage(testMessage);
			// die Informationen zum Testfall (Pfad und Version) werden ebenfalls
			// in resultInfo gespeichert
			resultInfo.setPath(tci.getPath());
		}
        else
        {
			TestExecutionSummary testExecutionSummary =
				TestCaseJUnit5Runner.createWithStandardSummaryListener()
				.run(new ArrayList<>(Arrays.asList(testclass))
				, new ArrayList<>(Arrays.asList(tci.getIdentifierName()))
				, new ArrayList<>(Collections.singletonList(tci.getArgumentsTypes())));
			testExecutionSummary.printTo(new PrintWriter(System.out));
			List<TestExecutionSummary.Failure> failures = testExecutionSummary.getFailures();
			if(testExecutionSummary.getTestsFailedCount() > 0
				&& (failures.stream().anyMatch(failure ->
				failure.getException().getClass()
					== org.opentest4j.AssertionFailedError.class))){
				List<TestExecutionSummary.Failure> filteredList
					= failures
					.stream()
					.filter(failure -> failure.getException().getClass() ==
					org.opentest4j.AssertionFailedError.class)
					.collect(Collectors.toList());
				testSuccess = TestResultInfo.TESTFAILED;
				testMessage = filteredList
					.get(0).getException().getMessage();
			}
			else if(testExecutionSummary.getTestsFailedCount() > 0
				&& (failures.stream().anyMatch(failure ->
				failure.getException().getClass()
					!= org.opentest4j.AssertionFailedError.class))){
				List<TestExecutionSummary.Failure> filteredList =
					failures.stream()
					.filter(failure -> failure.getException().getClass()
					!= org.opentest4j.AssertionFailedError.class)
					.collect(Collectors.toList());
				testSuccess = TestResultInfo.TESTABORTED;
				testMessage = filteredList
					.get(0).getException().getMessage();
			}
			else if (testExecutionSummary.getTestsAbortedCount() > 0){
				testSuccess = TestResultInfo.TESTABORTED_BY_ASSUMPTION;
				testMessage = "TestAbortedException wurde geworfen";
			}
			else if(testExecutionSummary.getTestsSkippedCount() > 0
				|| testExecutionSummary.getContainersSkippedCount() > 0)
			{
				testSuccess = TestResultInfo.TESTSKIPPED;
				testMessage = "Test wurde übersprungen";
			}
			else if(testExecutionSummary.getTestsSucceededCount() > 0)
			{
				testSuccess = TestResultInfo.TESTPASSED;
				testMessage = "Test ist erfolgreich.";
			}
			else 
			{
				testSuccess = TestResultInfo.TESTFAILED;
				if(failures.size() > 0)
					testMessage = failures
						.get(0).getException().getMessage();
				else
					testMessage = "Test ist fehlgeschlagen.";
			}
		}
        testMessage = testMessage.length() > 250 
        	? testMessage.substring(0, 247) + "..." : testMessage;
        resultInfo.setSuccess(testSuccess);
        resultInfo.setMessage(testMessage);
        // die Informationen zum Testfall (Pfad und Version) werden ebenfalls
        // in resultInfo gespeichert
        resultInfo.setPath(tci.getPath());
    }

	void prepareAndSetTestRunParameters(
		HashMap<String, TestCaseInfo> testCaseInfoHashMap, 
		HashMap<String, TestResultInfo> testResultInfoHashMap, 
		List<Class<?>> testClassesList,
		List<String> testMethodNamesList, List<ArrayList<String>> parametersLists)
		throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException 
	{
		for (Map.Entry<String, TestCaseInfo> testMethod
			: testCaseInfoHashMap.entrySet()) 
		{
			TestCaseInfo testCaseInfo = testMethod.getValue();
			// der Testfall wird geladen
			Class<?> testclass = Class.forName(testCaseInfo.getPackageName() + "."
				+ testCaseInfo.getClassName(), true,
				BasicFrameControl.CLASSLOADER);
			testClassesList.add(testclass);
			Class<?> classWithParameter = createClassWithParameter(
				testclass, testCaseInfo);
			// es wird ausgelesen, ob Nutzer Exception erwartet, der Erwartungswert
			// wird in TestResultInfo abgelegt
			String fullIdentifier = testCaseInfo.getPackageName()
				+ "." + testCaseInfo.getClassName()
				+ "." + testCaseInfo.getIdentifierName();
			testMethodNamesList.add(testCaseInfo.getIdentifierName());
			parametersLists.add(testCaseInfo.getArgumentsTypes());
			// Die Attribute der Testklasse, die zu den Testmethoden gehoeren,
			// werden mit Werten belegt, und die ParameterInfo wird hinzugefuegt.
			ArrayList<TestParameterInfo> parameterInfoArrayList =
					testResultInfoHashMap.get(fullIdentifier).getParameters();
			for (int i = 0; i < parameterInfoArrayList.size(); i++) {
				TestParameterInfo param = parameterInfoArrayList.get(i);
				// das Feld der Testklasse wird nun mit dem Inhalt der Eingabekomponente
				// gefuellt und die Parameterinformationen bereits in resultInfo abgelegt
				TypeConvertation.setFieldParameter(classWithParameter, param.getName(),
						param.getValue());
			}
		}
	}

	// erstellt ein Objekt derjenigen Klasse, welche die Parameter des Tests enthaelt.
    // Ist es die Testklasse selbst, so wird diese zurueckgegebben.
    private Class<?> createClassWithParameter(Class<?> testclass, TestCaseInfo tci) 
    	throws ClassNotFoundException
    {
    	Class<?> classWithParameter = testclass;
     	return classWithParameter;
    }
    
     /**
     * schreibt alle Informationen eines Testlaufs in die Datenbank
     *
     * @param resultInfo kapselt alle Informationen eines Testlaufs
	 * @throws Exception, enthaelt als Meldung den Typ der Exception
     */
    private int writeTestResultInfo(TestResultInfo resultInfo) 
    	throws Exception
    {
  		int newId = 0;
  		String result = "";
		try
		{
			ObjectMapper mapper = new ObjectMapper();
			String s = mapper.writeValueAsString(resultInfo);
			WebResource resource = client.resource(
				this.pathWebResource + "/insertTestcase");
			ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
				.post(ClientResponse.class, s);
			result = response.getEntity(String.class);
 	        newId = Integer.parseInt(result);
	    } 
	    catch(Exception exc)
	    {
			if("".equalsIgnoreCase(result))
			{	
			    throw new Exception(exc.getClass().getSimpleName() + ": " 
			        + exc.getMessage());
			}
			else
			{
	  		    throw new Exception(result);
			}
		}
		return newId;
    } 
    
    /**
     * ueberschreibt das Ergbenis einer bereits frueher gespeicherten und 
     * nun erneut ausgefuehrten Testkonfiguration.
     *
     * @param resultInfo zu ueberschreibende Testkonfiguration
     * @throws Exception, enthaelt als Meldung den Typ der Exception
     */
    public void updateTestResult(TestResultInfo resultInfo) 
    	throws Exception
    {
    	String s = "";
    	String result = "";
    	try
    	{
    		ObjectMapper mapper = new ObjectMapper();
    		s = mapper.writeValueAsString(resultInfo);
    	}    
    	catch(Exception exc)
    	{
    		throw new Exception(exc.getClass().getSimpleName() + ": " 
    	        + exc.getMessage());
    	}
    	WebResource resource = client.resource(
    		this.pathWebResource + "/updateTestcase");
		ClientResponse response = resource.type(MediaType.APPLICATION_JSON)
		    .post(ClientResponse.class, s);
		result = response.getEntity(String.class);
 	    if(!"".equalsIgnoreCase(result))
		{	
	        throw new Exception(result);
		}
    }
    
 
    /**
     * liest die Anzahl gespeicherter Konfigurationen zu einem bestimmten Testfall aus
     * @param path Pfad des Testfalls
     * @return Anzahl gespeicherter Konfigurationen
     * @throws Exception, the message ist the type of the Exception
     */
    private int readNumberOfTestcases(String path) 
        throws Exception    
    {
    	int count = 0;
    	String result = "";
    	try
    	{
  	        WebResource resource = client.resource(
	        	this.pathWebResource
	            + "/countTestcases/" + path);
			Builder builder = resource.accept(MediaType.APPLICATION_JSON);
	    	result = builder.get(String.class);
    	    count = Integer.parseInt(result);
    	} 
    	catch(Exception exc)
    	{
    		if("".equalsIgnoreCase(result))
    		{	
    			throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
    		}
    		else
    		{
      		    throw new Exception(result);
    		}
    	}
    	return count;
    }

    /**
     * liest die Anzahl gespeicherter, erfolgreich ausgefuehrter Konfigurationen 
     * zu einem bestimmten Testfall aus
     *
     * @param path Pfad des Testfalls
     *
     * @return Anzahl gespeicherter, erfolgreich ausgefuehrter Konfigurationen
     * @throws Exception, the message ist the type of the Exception
     */
    private int readNumberOfSuccessfulTestcases(String path) 
        throws Exception
    { 
       	int count = 0;
    	String result = "";
       	try
       	{
	        WebResource resource = client.resource(
	        	this.pathWebResource
	            + "/countSuccessfullTestcases/" + path);
			Builder builder = resource.accept(MediaType.APPLICATION_JSON);
		    result = builder.get(String.class);
	    	count = Integer.parseInt(result);
       	}
       	catch(Exception exc)
       	{
    		if("".equalsIgnoreCase(result))
    		{	
    			throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
    		}
    		else
    		{
      		    throw new Exception(result);
    		}
    	}
    	return count;
    }

	/**
	 * liest die Anzahl gespeicherter, durch Assumption abgebrochener Konfigurationen
	 * zu einem bestimmten Testfall aus
	 *
	 * @param path Pfad des Testfalls
	 *
	 * @return Anzahl gespeicherter, durch Assumption abgebrochener Konfigurationen
	 * @throws Exception, the message ist the type of the Exception
	 */
	private int readNumberOfByAssumptionsAbortedTestcases(String path)
		throws Exception
	{
		int count = 0;
		String result = "";
		try
		{
			WebResource resource = client.resource(
				this.pathWebResource
				+ "/countByAssumptionsAbortedTestcases/" + path);
			Builder builder = resource.accept(MediaType.APPLICATION_JSON);
			result = builder.get(String.class);
			count = Integer.parseInt(result);
		}
		catch(Exception exc)
		{
			if("".equalsIgnoreCase(result))
			{
				throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			}
			else
			{
				throw new Exception(result);
			}
		}
		return count;
	}


	/**
	 * liest die Anzahl gespeicherter, uebersprungener Konfigurationen
	 * zu einem bestimmten Testfall aus
	 *
	 * @param path Pfad des Testfalls
	 *
	 * @return Anzahl gespeicherter, uebersprungener Konfigurationen
	 * @throws Exception, the message ist the type of the Exception
	 */
	private int readNumberOfSkippedTestcases(String path)
			throws Exception
	{
		int count = 0;
		String result = "";
		try
		{
			WebResource resource = client.resource(
				this.pathWebResource
				+ "/countSkippedTestcases/" + path);
			Builder builder = resource.accept(MediaType.APPLICATION_JSON);
			result = builder.get(String.class);
			count = Integer.parseInt(result);
		}
		catch(Exception exc)
		{
			if("".equalsIgnoreCase(result))
			{
				throw new Exception(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			}
			else
			{
				throw new Exception(result);
			}
		}
		return count;
	}
}
