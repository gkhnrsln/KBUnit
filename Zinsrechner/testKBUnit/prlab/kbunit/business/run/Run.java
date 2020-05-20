package prlab.kbunit.business.run;

import junit.framework.TestResult;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import prlab.kbunit.business.XMLAccess;
import prlab.kbunit.business.connector.WebServiceConnector;
import prlab.kbunit.config.Configuration;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.file.FileCreator;
import prlab.kbunit.file.FileCreatorJUnit4;
import prlab.kbunit.file.FileCreatorJUnit5;
import prlab.kbunit.reflection.TestPerformer;
import prlab.kbunit.reflection.TestPerformerJUnit4;
import prlab.kbunit.reflection.TestPerformerJUnit5;
import prlab.kbunit.scan.FolderScanner;
import prlab.kbunit.test.*;
import prlab.kbunit.xml.XMLFileLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Yannis Herbig, Ursula Oesing  <br>
 * @author Alexander Georgiev, Patrick Pete, Yannis Herbig, Ursula Oesing
 */
public class Run {

	private Configuration config = new Configuration();
	private WebServiceConnector webServiceConnector;

	private ArrayList<TestResultInfo> tests
		= new ArrayList<TestResultInfo>();
	// Alle Klassen und alle Konfigs:
	private List<ArrayList<TestResultInfo>> allTests
			= new ArrayList<ArrayList<TestResultInfo>>();
	private TestSource testSource;
	private TestObject testObject = new TestObject();
	private List<TestSource> testSourcesList = new ArrayList<>();
	private List<TestObject> testObjectsList = new ArrayList<TestObject>();
	private File file;
	private boolean secondTestclass = false;
	private List<File> nonJUnit5FileList;

	public Run(File file) {
		try {
			this.webServiceConnector = new WebServiceConnector();
			loadData(file);
		}
		catch(Exception exc) {
			System.out.println("The connection to the WebService ist not possible.");
		}
	}

	public Run(List<File> files) {
		try {
		    this.webServiceConnector = new WebServiceConnector();
		    loadData(files);
		}    
		catch(Exception exc) {
			System.out.println("The connection to the WebService ist not possible.");
		}
	}

	public Run(File file, boolean moreThanOne) {
		this(file);
		this.secondTestclass = moreThanOne;
	}

	private void loadData(File file) throws JDOMException, IOException {
		this.file = file;
		String sourceClassName = ClassCreator.convertIntoClassName(file, 
			Variables.TEST_SOURCE);
		tests = webServiceConnector.readTestResultInfo(sourceClassName);
		// Handling with all testcases
		if(tests.size() > 0) {
			//---------------------TEST-SOURCE------------------------------
			testSource = TestSource.getInstance(file, Variables.TEST_SOURCE);
			//---------------------TEST-METHOD------------------------------
			testObject.setTestMethods(tests);
		}
	}

	private void loadData(List<File> files) throws JDOMException, IOException {
		this.nonJUnit5FileList = new ArrayList<>();
		for(File file : files){
			String sourceClassName = ClassCreator.convertIntoClassName(file,
				Variables.TEST_SOURCE);
			ArrayList<TestResultInfo> tests = webServiceConnector.readTestResultInfo(sourceClassName);
			if(tests.size() > 0){
				allTests.add(tests);
				TestSource newTestSource = TestSource.getInstance(file, Variables.TEST_SOURCE);
				if(!(newTestSource instanceof TestSourceJUnit5)){
					nonJUnit5FileList.add(file);
				}
				else{
					testSourcesList.add(newTestSource);
					TestObject newTestObject = new TestObject();
					newTestObject.setTestMethods(tests);
					testObjectsList.add(newTestObject);
				}
			}
		}
	}

	/**
	 * start the logger
	 * @param selection for the XML Log File
	 */
	public void startLogger(Selection selection) {

		Variables.setTime(LocalDateTime.now());
		try {
			ArrayList<String> createdLogFiles = new ArrayList<String>();
			if(selection == null){
				throw new NullPointerException("Invalid argument for selection!\n");
			}

			//--------------------CREATING XML-LOG-FILE OR FILES--------------------------

			if(allTests == null || allTests.size() == 0){
				if(testSource != null && createLogFile(testSource, testObject, selection)){
					createdLogFiles.add(testSource.getXMLFilePath());
				}
			}
			else{
				if(testSource == null && createLogFiles(testSourcesList, testObjectsList, selection)){
					for(TestSource testSource : testSourcesList){
						createdLogFiles.add(testSource.getXMLFilePath());
					}
				}
			}
			//-------------------CREATING OUTPUT IN CONSOLE-----------------------           
			if(createdLogFiles.size() > 0){
				for(int i = 0; i < createdLogFiles.size(); i++){
					System.out.println("Created: "+ createdLogFiles.get(i));	
				}
			}
			else{
				System.out.println("Not Created: " + this.file 
					+ ".xml; No database records found!");
			}
		} 
		catch(Exception exc) {
			exc.printStackTrace();         
		}
	}
	

	/**
	 * start the runner
	 */
	public void startRunner() {
		Timestamp timestamp = null;
		try {
			List<String> listCreated  = new ArrayList<String>();
			List<String> listNotFound = new ArrayList<String>();
			List<String> suiteClasses = new ArrayList<String>();
			boolean noTestcasesFound = false;
			FileCreator fileCreator = null;
			TestObject newTestObject = new TestObject();

			Map<String, Map<String,List<String>>> 
			mapAllInactive = new HashMap<String,Map<String,List<String>>>();
			timestamp = getTimestamp(config);
			Variables.setTime(LocalDateTime.now());
			if(testSource != null) {
			    testSource.setPathNewGeneratedTestCases(testSource.getClassFile().getPath()
					.replace(".java",Variables.CLASS_NAME_SUFFIX + ".java")
					.replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE));

			    testSource = TestSource.getInstance(file, Variables.TEST_SOURCE);

			    // Handling with all testcases
			    //-----------------------TEST-SOURCE(CLASS)-------------------
			    List<String> listCompare = new ArrayList<String>();
			    Map<String, List<String>> mapActive  
			        = XMLAccess.getActiveTestCases(new File(file.getPath()
					    .replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE)
					    .replace(Variables.EXTENSION_JAVA, Variables.EXTENSION_XML)));
			    for (int i = 0; i < tests.size(); i++){
				    /*
				     * List for comparing whether any of the existing test-cases,
				     * which are to find in the .xml-LOG-file, has been deleted 
				     */
				    listCompare.add(Variables.ACTIVITY_ID_PREFIX + tests.get(i).getId());
				    /*
					 * !!! Usually test-cases that are performed after timeStamp are considered
					 * to be new and should be copied by FileCrator.copyFile(...) in source-folder
					 * "testKBUnit". The KBUnit-Framework offers the opportunity to perform a 
					 * test-case repeatedly and as a result the test-case obtains a new time-stamp.
					 * Nevertheless this test-case has not be considered a new one and should not
					 * be copied. Otherwise there will be a range of copies of the same test-case, 
					 * but in different .java-files and with different time-stamps.
					 * 
					 * <1> IF the time-stamp of the current test-case is prior to the update-timeStamp
					 *     ---> The current test-case won't be copied!
					 * <2> IF the ID of the current test-case matches the one in the activity
					 *     LOG-file - that means there is already a copy of this test-case in .java-File
					 *     or .story-File
					 *     ---> The current test-case won't be copied!
					 * !!! IMPORTANT: In case a new test-method has been inserted in an old test-class, 
					 *     a NullPointerException will be thrown, if (map.get() != null) not checked,
					 *     because the name of this new method doesn't exist as Map-key yet! 
					 *     The mapActive is built based only(!) on the active test-methods respectively 
					 *     the test-cases to be found in the activity-log-file. 
					 * <3> ELSE
					 *     ---> The test-case will be copied in a new File and the activity-LOG
					 *          will be modified - this test-case is to be found under "active"-Node.
					 */  
					/*<1>*/ if(tests.get(i).getDate().before(getLastUpdateFromTest(tests.get(i))) 
						&& ! (this.secondTestclass && tests.get(i).getDate().getTime() > Variables.INITIAL_TIME)){
						continue;
					}    
					/*<2>*/else if((mapActive.get(tests.get(i).getName()) != null) && 
						(mapActive.get(tests.get(i).getName())
							.contains(Integer.toString(tests.get(i).getId())))){ 
						continue;    
					}
					else {
						for (int j = 0; j< tests.get(i).getParameters().size(); j++){
							tests.get(i).getParameters().get(j).setClassType(testSource
								.getTestParameterType(tests.get(i).getParameters().get(j).getName()));
						}
						newTestObject.addTestMethod(tests.get(i));
					}
			    }
				//---------------------CREATING/MODIFYING FILES OF TESTCASES--------------------
				fileCreator = createFileCreator(testSource, newTestObject, listNotFound, file, true);
				fileCreator.getTestSource()
				    .setPathNewGeneratedTestCases(testSource.getPathNewGeneratedTestCases());
				String help = fileCreator.copyFile(); 
				if(newTestObject.getTestMethods().size() > 0 && fileCreator.isNewTestSource()){
					suiteClasses.add(testSource.getNewClassName()); 
					listCreated.add(help);
				}  
				//--------------------CREATING/MODIFYING THE ACTIVITY-LOG-FILE-----------------
				handleActivityLogFile(fileCreator, listCompare, mapAllInactive);
				//----------   -----------CREATING/MODIFYING SUITE CLASSES--------------------------
				config.setDateAndTime(testSource.getClassName(),   
					Variables.CNFG_CURRENT_DATE, Variables.CNFG_CURRENT_TIME);
				handleSuiteClasses(fileCreator, listCreated, suiteClasses);
			}
			else {
				noTestcasesFound = true;
			}
			//--------------------------------CREATING OUTPUT IN CONSOLE---------------------------------------------
			createHeaderInConsole(timestamp);
			createContentInConsole(listCreated, suiteClasses, mapAllInactive, listNotFound, 
				noTestcasesFound);
		}
		catch(NullPointerException npExc) {
			if(npExc.getMessage() != null){
				System.out.println(Variables.ERROR + npExc.getMessage());
			}    
		} 
		catch(Exception exc){
			exc.printStackTrace();
		}
	}
	

	/**
	 * delete testcase in XML, JUnit, SuiteCase
	 * @param selectedID the testcase ID
	 * @param files Testclass Java File-Array
	 */
	public void reRun(int selectedID, File[] files) {
		try {
			List<File>   listFiles    = verifyTestFiles(files);
			List<String> listNotFound = new ArrayList<String>();
			//List<String> suiteClasses = new ArrayList<String>();
			FileCreator fileCreator = null;
			for(File file : listFiles) { 
				testSource = TestSource.getInstance(file, Variables.TEST_SOURCE);
				//------------------------TEST-SOURCE(CLASS)-----------------------------
				//TestObject   testObject  = new TestObject();
				//					List<String> listCompare = new ArrayList<String>();
				Map<String, List<String>> mapActive  
				= XMLAccess.getActiveTestCases(new File(file.getPath()
					.replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE)
					.replace(Variables.EXTENSION_JAVA, Variables.EXTENSION_XML)));

				Map<String, List<String>> mapInactive  
				= XMLAccess.getInactiveTestCases(new File(file.getPath()
					.replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE)
					.replace(Variables.EXTENSION_JAVA, Variables.EXTENSION_XML)));

				ArrayList<Map.Entry<String, String>> allTests 
				    = new ArrayList<Map.Entry<String, String>>();
				allTests = XMLAccess.getTestPath(new File(file.getPath()
					.replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE)
					.replace(Variables.EXTENSION_JAVA, Variables.EXTENSION_XML)));

				TestObject   newTestObject  = new TestObject();
				if(mapActive.entrySet().toString().contains((Integer.toString(selectedID))) 
					|| mapInactive.entrySet().toString().contains((Integer.toString(selectedID)))) {
					String myPath = "";
					List<Integer> idList = new ArrayList<>();
					int zaehler = 0; // 
				
					for(int i = 0;i < allTests.size(); i++) {
						if(allTests.get(i).getKey()
							.equals(Variables.ACTIVITY_ID_PREFIX + Integer.toString(selectedID))) {
							myPath = allTests.get(i).getValue(); // Path der ID
						}
					}

					for(int i = 0; i < allTests.size(); i++) {
						if(allTests.get(i).getValue().equals(myPath)) {
							zaehler++;
							if(Integer.parseInt(allTests.get(i).getKey()
								.toString().substring(3, allTests.get(i)
								.getKey().toString().length()))!=selectedID) {
								idList.add(Integer.parseInt(allTests.get(i).getKey()
								.toString().substring(3, allTests.get(i).getKey()
								.toString().length())));
							}
						}
					}

					for(int k = 0;k < idList.size();k++) {
						for(int j=0;j<tests.size();j++) {
							if(tests.get(j).getId()==idList.get(k)) {
								for (int h = 0; h< tests.get(j).getParameters().size(); h++){
									tests.get(j).getParameters().get(h).setClassType(testSource
										.getTestParameterType(tests.get(j)
										.getParameters().get(h).getName()));
								}
								newTestObject.addTestMethod(tests.get(j));
							}
						}
					}
					testSource.setPathNewGeneratedTestCases(myPath);
					fileCreator = createFileCreator(testSource, newTestObject, listNotFound, 
						file, true);
					XMLAccess.deleteTestCase(fileCreator.getTestSource(), 
						new File(fileCreator.getTestSource().getLOGFilePath()), selectedID);
					if(zaehler > 1) { // Pfad ist mehrmals vorhanden 
						// -> *.java loeschen & fileCreator mit ID's ausfuehren
						System.out.println(
							"Deleted JUnit test case in:");
						System.out.println(
							"**********************************************************************************");
						System.out.println(testSource.getPathNewGeneratedTestCases());
						System.out.println();
						FileCreator.deleteFile(myPath);
						fileCreator.copyFile(); 							
					}
					else { // Pfad nur einmal vorhanden -> in Testsuite und *.java loeschen
						System.out.println(
							"File with JUnit test case was deleted. File:");
						System.out.println(
							"**********************************************************************************");
						System.out.println(testSource.getPathNewGeneratedTestCases());
						System.out.println();
						deleteSuiteClasses(fileCreator, myPath);
						FileCreator.deleteFile(myPath);
					}
				}
				
			}
		}
		catch(NullPointerException npExc) {
			if(npExc.getMessage() != null){
				System.out.print(Variables.ERROR + npExc.getMessage());
			}    
		} 
		catch(Exception exc){
			exc.printStackTrace();
		}

	}
	

	/**
	 * run a JUnit Test
	 * @param file
	 * @param tri
	 * @return JUnit-TestResult Object
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public TestResult runJUnit4Test(File file, TestResultInfo tri) {
		TestResult junitTestResult = null;
		try {
			TestSource testSource = TestSource.getInstance(file, Variables.TEST_SOURCE);
			junitTestResult = ((TestSourceJUnit4)testSource).runTestCaseJUnit4(tri);
		}
		catch(Exception exc) {
			System.out.println("The testcases could not be loaded.");
		}
		return junitTestResult;
	}

	/**
	 * run a JUnit5 Test
	 * @param file
	 * @param tri
	 * @return JUnit5-TestExecutionSummary Object
	 */
	public TestExecutionSummary runJUnit5Test(File file, TestResultInfo tri) {
		TestExecutionSummary junitTestResult  = null;
		try {
		    TestSource testSource = TestSource.getInstance(file, Variables.TEST_SOURCE);
		    junitTestResult = ((TestSourceJUnit5) testSource).runTestCaseJUnit5(tri);
		}
		catch(Exception exc) {
			System.out.println("The testcases could not be loaded.");
		}
		return junitTestResult;
	}


	// controlling, if the given file ist a test file
	private static List<File> verifyTestFiles(File[] files) {

		List<File> listTestFiles = new ArrayList<File>();
		//<1>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if(files == null) {
			listTestFiles = FolderScanner.scanFolder(new File(Variables.TEST_SOURCE), 
					new ArrayList<File>(), Variables.EXTENSION_JAVA);

			if(listTestFiles == null){
				throw new NullPointerException("Missing source-folder \""
					+ Variables.TEST_SOURCE +"\"!");
			}	
			else if(listTestFiles.isEmpty()){
				throw new NullPointerException("Source-folder \""+ Variables.TEST_SOURCE 
					+ "\" is either empty or contains no " 
					+ Variables.EXTENSION_JAVA +"-files!");	
			} 
		}	
		//<2>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
		else if(files.length != 0) {
			for(File file : files){
				if(file != null) {
					if(file.isFile()) {
						if(file.getName().endsWith(".java")){
							listTestFiles.add(file);
						}    
						else{
							System.err.println(Variables.ERROR + file + " is not a .java-file!");
						}	
					} 
					else{
						System.err.println(Variables.ERROR + "File not found: " + file);
					}	
				} 
				else{
					System.err.println(Variables.ERROR + file + " is not a valid parameter!");
				}	
			}
			if(listTestFiles.isEmpty()){
				throw new NullPointerException("Array has no existing files! \n"
						+ "Unable to perform operation!");	
			}	
			//<3>~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~	
		} 
		else{ 
			throw new NullPointerException("Array is empty! \n"
					+ "Unable to perform operation!");
		}	

		return listTestFiles;	
	}

	// creating a timestamp of the moment of running or logging
	private Timestamp getTimestamp(Configuration config){
		Timestamp result = null;	
		try {
			result = new Timestamp(new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
				.parse(config.getDate() + " " + config.getTime())
				.getTime());
		} 
		catch (ParseException e) {
			result = Timestamp.valueOf("2000-01-01 00:00:00");
		}
		return result;
	}	
	
	// gets the last Update of the individual Test
	private Timestamp getLastUpdateFromTest(TestResultInfo test) {
		Timestamp result = null;
		try {
		    XMLFileLoader xmlFileLoader = new XMLFileLoader(
			    Variables.TEST_NEW_SOURCE + "\\\\" 
		        + test.getPackageName() + "\\\\" + test.getClassName() 
		        + Variables.EXTENSION_XML);
		    String[] dateTime = xmlFileLoader.getAttributValueOfRootElement(Variables.NODE_UPDATED).split(" ");
		    result = new Timestamp(new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
		    	.parse(dateTime[0] + " " + dateTime[1]).getTime());
		}
		catch(Exception exc) {
			result = Timestamp.valueOf("2000-01-01 00:00:00");
		}
		return result;
	}

	// creating a FileCreator-Object
	private FileCreator createFileCreator(TestSource testSource, 
		TestObject testObject,
		List<String> listNotFound, File file, boolean isRunner)
					throws Exception{
		FileCreator result = null;
		// creating a FileCreator-Object in dependency of the testtype
        if(testSource instanceof TestSourceJUnit4 && isRunner){ 
			result = new FileCreatorJUnit4((TestSourceJUnit4)testSource, testObject);
		}
		else if(testSource instanceof TestSourceJUnit5 && isRunner){
			result = new FileCreatorJUnit5((TestSourceJUnit5)testSource, testObject);
		}
		else if(! isRunner){
			result = null;
		}
		else{
			throw new Exception(
				"createFileCreator konnte nicht durchgefuehrt werden.");
		}
		if(testObject.getTestMethods().isEmpty()) {			
			listNotFound.add(file.getPath());
		}
		return result;
	} 

	// creating the logFile of the runner
	private void handleActivityLogFile(FileCreator fileCreator, 
		List<String> listCompare, Map<String, Map<String,List<String>>> mapAllInactive){
		try {
			Map<String,List<String>> mapInactive 
			    = XMLAccess.createLOG(fileCreator.getTestSource(), 
				fileCreator.getTestObject(), listCompare, 
				new File(fileCreator.getTestSource().getLOGFilePath()));
			if(!mapInactive.isEmpty()){
				mapAllInactive.put(fileCreator.getTestSource().getLOGFilePath(), mapInactive);
			}
		}
		catch(Exception exc) {
			System.out.println("The log files could not be loaded.");
		}
	} 

	// copying the suiteClasses 
	private void handleSuiteClasses(FileCreator fileCreator,
			List<String> listCreated, List<String> suiteClasses){
		if(!listCreated.isEmpty()) {
			fileCreator.createSuiteClasses(Variables.ALLTEST_RUNNER_FILE, 
				suiteClasses);
		}
	}   
	
	// creating the informations about the running in the console
	private void createHeaderInConsole(Timestamp timestamp) {
		if(!secondTestclass) {
			String message = "\nLast Update: "+ timestamp 
				+ "\n****************************************"
				+ "******************************************";
			System.out.println(message);
		}
	}
	
	// creating the informations about the running in the console
	private void createContentInConsole( 
		List<String> listCreated,
		List<String> suiteClasses,  Map<String, Map<String,
		List<String>>> mapAllInactive,
		List<String> listNotFound,
	    boolean noTestcasesFound){		
        String message = "";
		if(!listCreated.isEmpty()) {
			message += "\n\nCreated file(s):"
				+ "\n****************************************"
				+ "******************************************";

			for(String s : listCreated){
				message += "\n"+ s;	
			}	
		}

		if(!mapAllInactive.isEmpty()) {
			message += "\n\nThe following test cases are no longer available."
				+ "\nFor details please check the corresponding LOG_file(s):"
				+ "\n****************************************"
				+ "******************************************";

			for(Map.Entry<String, Map<String,List<String>>> 
			    e1 : mapAllInactive.entrySet()) {

				// e1.getKey() represents the file-path of every single test-class
				message += "\n"+ e1.getKey() 
				+ "\n+---------------------------------------"
				+ "-----------------------------------------+";

				for(Map.Entry<String, List<String>> e2 : e1.getValue().entrySet()) {
					// e2.getKey() represents every single native test-method
					message += "\n"+ e2.getKey() +": \n";
					int count = 1; // !!! (0%10 == 0) --> true
					for(String s : e2.getValue()) {
						message += s +", ";
						// After every 10 IDs go to next line: 
						if(count % 10 == 0){
							message += "\n";
						}	
						count++;
					}	
				}
				message += "\n+---------------------------------------"
						+"-----------------------------------------+\n";
			}
		}
		if(!listNotFound.isEmpty()) {
			message += "No new test-cases found "
				+ "for the following file: ";
			for(String s : listNotFound){
				message += "\n"+ s;
			}	
		}
		if(noTestcasesFound) {
			message += "No new test-cases found in file: \n" + this.file;
		}
		System.out.println(message);
	}

	// creating of a logFile of all testcases of the database
	private boolean createLogFile(TestSource testSource, 
		TestObject testObject, Selection selection){
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(Format.getPrettyFormat()
			.setEncoding(Variables.LOG_ENCODING));
		try {
			TestPerformer testPerformer = null;
			Document document = null;
			if(testSource instanceof TestSourceJUnit5)
				testPerformer = new TestPerformerJUnit5(testSource, testObject, selection);
	
			else
				testPerformer = new TestPerformerJUnit4(testSource, testObject, selection);
			document = testPerformer.createDocument();
		    xmlOutputter.output(document,
			    new FileWriter(FileCreator.createMissingPackages(
				new File(testSource.getXMLFilePath()))));
			return true;
		} 
		catch (IOException ioexc) {
			ioexc.printStackTrace();
			return false;
		}    
	}

	// creating of a logFile of all testcases of the database
	private boolean createLogFiles(List<TestSource> testSourcesList,
								   List<TestObject> testObjectsList, Selection selection){
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(Format.getPrettyFormat()
				.setEncoding(Variables.LOG_ENCODING));
		try {
			new Thread(() -> {
				for(int i = 0; i < nonJUnit5FileList.size(); i++){
					Run run = new Run(nonJUnit5FileList.get(i));
					run.startLogger(selection);
				}
			}).start();
			TestPerformerJUnit5 testPerformer
				= new TestPerformerJUnit5(testSourcesList, testObjectsList, selection);
			Map<String, Document> documentsMap = testPerformer.createDocuments();
			for(Map.Entry<?,?> document : documentsMap.entrySet()){
				for(TestSource testSource : testSourcesList){
					String className = testSource.getTestMethodsByFQMN()[0]
						.substring(0, testSource.getTestMethodsByFQMN()[0]
								.lastIndexOf("."));
					if(document.getKey().equals(className)){
						xmlOutputter.output((Document) document.getValue(),
							new FileWriter(FileCreator.createMissingPackages(
							new File(testSource.getXMLFilePath()))));
						break;
					}
				}
			}
			return true;
		}
		catch (IOException ioexc) {
			ioexc.printStackTrace();
			return false;
		}
	}

	/**
	 * delete the class-path in the suiteclass
	 * @param fileCreator
	 * @param path
	 */
	private void deleteSuiteClasses(
		FileCreator fileCreator, String path){
		fileCreator.deleteSuiteClasses(
			Variables.ALLTEST_RUNNER_FILE, path);
	}

}
