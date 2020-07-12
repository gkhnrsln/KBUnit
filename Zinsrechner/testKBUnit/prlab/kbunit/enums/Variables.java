package prlab.kbunit.enums;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The {@code Variables} class provides final-variables,
 * through which the software-developer could make
 * swift and easily changes of the settings of folders, 
 * classes, the .xml-preferences file i.e. how or
 * where a LOG-file will be saved, or what name-suffix 
 * should get the files created through RUNNER and/or LOGGER
 * without any need of modifying the code in the other 
 * classes of thus plug-in.
 * <br><b>N.B.</b> These variables are not meant to be used 
 *                 by the end user!<br>
 
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 *
 */
public class Variables { 
	
	private static LocalDateTime time = LocalDateTime.now();
	
	//-------------------------FORMATS-FOR-DATE-AND-TIME----------------------------------------
	/**{@value #FORMAT_FOR_DATE_TIME}*/
	public final static String FORMAT_FOR_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	/**{@value #FORMAT_FOR_DATE}*/
	public final static String FORMAT_FOR_DATE = "yyyy-MM-dd";
	/**{@value #FORMAT_FOR_TIME}*/
	public final static String FORMAT_FOR_TIME = "HH:mm:ss";
	/**{@value #INITIAL_TIME}*/
	public final static int INITIAL_TIME = 3601000;
	
	//-------------------------FILES, FOLDERS-------------------------------------------
	/**{@value #TEST_SOURCE}*/
	public final static String TEST_SOURCE = "test";
	/**{@value #TEST_PLAIN_SOURCE}*/
	public final static String TEST_PLAIN_SOURCE = "testPlain";
	/**{@value #TEST_NEW_SOURCE}*/
	public final static String TEST_NEW_SOURCE = "testKBUnit";
	/**{@value #ALLTEST_PACKAGE}*/
	public final static String ALLTEST_PACKAGE 
	    = "allTestsKBUnit"; 
	/**{@value #CNFG_FILE_PATH}*/
	public final static String CNFG_FILE_PATH   
	    = "testKBUnit\\prlab\\kbunit\\PreferencesHome.xml";
	/**{@value #CUSTOMER_TEST_CASE_INFO_FILE_PATH}*/
	public final static String CUSTOMER_TEST_CASE_INFO_FILE_PATH
			= "test\\CustomerTestCaseInformation.xml";
	/**{@value #PARAMETER_FILE_PATH}*/
	public final static String PARAMETER_FILE_PATH
	= "testKBUnit\\Parameter.txt";

	//-------------------------FILE-SCANNER---------------------------------------------
	/**{@value #EXTENSION_JAVA}*/
	public final static String EXTENSION_JAVA = ".java";
	/**{@value #EXTENSION_TEST_JAVA}*/
	public final static String EXTENSION_TEST_JAVA = "Test.java";
	/**{@value #EXTENSION_TEST_PLAIN_JAVA}*/
	public final static String EXTENSION_TEST_PLAIN_JAVA = "TestPlain.java";
	/**{@value #EXTENSION_XML}*/  
	public final static String EXTENSION_XML = ".xml";
	
	//-------------------------CLASSES--------------------------------------------------
	/**Current Date/Time formatted [_yyMMdd_HHmmss]*/
	public static String CLASS_NAME_SUFFIX = formatDateTime("_yyMMdd_HHmmss");
	/**{@value #ALLTEST_RUNNER_CLASS}*/
	public final static String ALLTEST_RUNNER_CLASS = "RunAllTests";
    //Do not modify!
	/**{@value #TEST_NEW_SOURCE}\{@value #ALLTEST_PACKAGE}\{@value #ALLTEST_RUNNER_CLASS}.java*/
	public final static File ALLTEST_RUNNER_FILE = new File(TEST_NEW_SOURCE +"\\"
	    + ALLTEST_PACKAGE +"\\"
		+ ALLTEST_RUNNER_CLASS +".java"); 
	 
	//-------------------------TEST METHOD/PARAMETER PREFIX-----------------------------
	/**{@value #METHOD_START_PUBLIC_VOID}*/
	public final static String METHOD_START_PUBLIC_VOID = "public void";
    /**{@value #METHOD_START_VOID}*/
    public final static String METHOD_START_VOID = "void";
	/**{@value #DYNAMIC_NODE}*/
	public final static String DYNAMIC_NODE = "DynamicNode";
	/**{@value #DYNAMIC_CONTAINER}*/
	public final static String DYNAMIC_CONTAINER = "DynamicContainer";
	/**{@value #DYNAMIC_TEST}*/
	public final static String DYNAMIC_TEST = "DynamicTest";

	//-------------------------TEST ANNOTATION-----------------------------
	/**{@value #ANNOTATION_TEST4}*/
	public final static String ANNOTATION_TEST4 = "@Test";
	/**{@value #ANNOTATION_TEST5}*/
	public final static String ANNOTATION_TEST5 = "@Test";
	/**{@value #ANNOTATION_TEST5}*/
	public final static String ANNOTATION_TEST5_REPEATED = "@RepeatedTest";
	/**{@value #ANNOTATION_TEST5}*/
	public final static String ANNOTATION_TEST5_PARAMETERIZED = "@ParameterizedTest";
	/**{@value #ANNOTATION_TEST5}*/
	public final static String ANNOTATION_TEST5_FACTORY = "@TestFactory";
	/**{@value #ANNOTATION_TEST5}*/
	public final static String ANNOTATION_TEST5_TEMPLATE = "@TestTemplate";

	//-------------------------XML-LOG-FILE (CREATED BY LOGGER)-------------------------
	/**{@value #LOG_SUBPACKAGE}*/
	public final static String LOG_SUBPACKAGE   = "log";
	// How to order the test-cases in the log-file
	/**{@value #LOG_ORDER_BY}*/
	public final static String LOG_ORDER_BY     = "Id";  /*"Date"*/
	/**{@value #LOG_SORTING}*/
	public final static String LOG_SORTING      = "DESC" /*"ASC"*/;
	/*
	 * UTF-8       - Default Web-Encoding
	 * ISO 8859-15 - German, Dutch, Swedish etc.
	 */
	/**{@value #LOG_ENCODING}*/
	public final static String LOG_ENCODING     
	    = "UTF-8";
	/**{@value #LOG_ATTR_CREATE}*/
	public final static String LOG_ATTR_CREATE  
	    = "created";
	/**Current Date/Time formatted [yyyy-MM-dd]*/
	public static String LOG_TIMESTAMP    
	    = formatDateTime(FORMAT_FOR_DATE_TIME);
	/**{@value #LOG_ATTR_ID}*/
	public final static String LOG_ATTR_ID
	    = "id";
	/**{@value #LOG_ATTR_DISPLAYNAME}*/
	public final static String LOG_ATTR_DISPLAYNAME
			= "displayName";
	/**{@value #LOG_ELEM_MESSAGE}*/
	public final static String LOG_ELEM_MESSAGE 
	    = "message";
	
	//-------------------------ATTRIBUTES for Server and DB--------------------------
	/**{@value #CNFG_NODE_REST}*/
	public final static String CNFG_NODE_REST
	    = "rest";
	/**{@value #CNFG_ELEM_REST_HOST}*/
	public final static String CNFG_ELEM_REST_HOST
	    = "host";
	
	//-------------------------XML MODIFY TIMESTAMP ATTRIBUTES--------------------------
	/**{@value #CNFG_NODE_UPDATE}*/
	public final static String CNFG_NODE_UPDATE  
	    = "update";
	/**{@value #CNFG_ELEM_PATH}*/
	public final static String CNFG_NODE_PATH   
	    = "path";
	/**{@value #CNFG_ELEM_DATE}*/
	public final static String CNFG_ELEM_DATE    
	    = "date";
	/**{@value #CNFG_ELEM_TIME}*/
	public final static String CNFG_ELEM_TIME    
	    = "time";
	/**Current Date/Time formatted [yyyy-MM-dd]*/
	public static String CNFG_CURRENT_DATE = formatDateTime(FORMAT_FOR_DATE);
	/**Current Date/Time formatted [HH:mm:ss]*/
	public static String CNFG_CURRENT_TIME = formatDateTime(FORMAT_FOR_TIME);
	
	/**{@value #NODE_UPDATED}*/
	public final static String NODE_UPDATED  
	    = "updated";

	//-------------------------CustomerTestCaseInformation.xml TAGS--------------------------
	/**{@value #CTCI_NODE_TESTCASES}*/
	public final static String CTCI_NODE_TESTCASES
			= "testcases";
	/**{@value #CTCI_NODE_TESTCASE}*/
	public final static String CTCI_NODE_TESTCASE
			= "testcase";
	/**{@value #CTCI_NODE_PATH}*/
	public final static String CTCI_NODE_PATH
			= "path";
	/**{@value #CTCI_NODE_DESC}*/
	public final static String CTCI_NODE_DESC
			= "desc";
	/**{@value #CTCI_NODE_TESTTYPE}*/
	public final static String CTCI_NODE_TESTTYPE
			= "testtype";
	/**{@value #CTCI_NODE_PARAMETERS}*/
	public final static String CTCI_NODE_PARAMETERS
			= "parameters";
	/**{@value #CTCI_NODE_PARAMETER}*/
	public final static String CTCI_NODE_PARAMETER
			= "parameter";
	/**{@value #CTCI_NODE_NAME}*/
	public final static String CTCI_NODE_NAME
			= "name";

	//-------------------------ACTIVITY-LOG-FILE----------------------------------------
	/**{@value #ACTIVITY_ATTR_UPDATE}*/
	public final static String ACTIVITY_ATTR_UPDATE   
	    = "updated";
	/**Current Date/Time formatted [yyyy-MM-dd HH:mm:ss]*/
	public static String ACTIVITY_TIMESTAMP     
	    = formatDateTime(FORMAT_FOR_DATE_TIME);
	/**{@value #ACTIVITY_ELEM_ACTIVE}*/
	public final static String ACTIVITY_ELEM_ACTIVE   
	    = "active";
	/**{@value #ACTIVITY_ELEM_INACTIVE}*/
	public final static String ACTIVITY_ELEM_INACTIVE 
	    = "inactive";
	/**{@value #ACTIVITY_ID_PREFIX}*/
	public final static String ACTIVITY_ID_PREFIX     
	    = "ID_";
	
	/**Returns the current Date and/or Time according to given format */
	private static String formatDateTime(String format){
		return time
            .format(DateTimeFormatter
            .ofPattern(format));
	}

	
	/**
	 * @return the time
	 */
	public static LocalDateTime getTime() {
		return time;
	}


	/**
	 * @param time the time to set
	 */
	public static void setTime(LocalDateTime time) {
		Variables.time = time;
		Variables.CLASS_NAME_SUFFIX = formatDateTime("_yyMMdd_HHmmss");
		Variables.LOG_TIMESTAMP = formatDateTime(FORMAT_FOR_DATE_TIME);
		Variables.CNFG_CURRENT_DATE = formatDateTime(FORMAT_FOR_DATE);
		Variables.CNFG_CURRENT_TIME = formatDateTime(FORMAT_FOR_TIME);
		Variables.ACTIVITY_TIMESTAMP = formatDateTime(FORMAT_FOR_DATE_TIME);
	}


	//-------------------------MESSAGES----------------------------------------
	/**{@value #ERROR}*/
	public final static String ERROR = "ERROR! ";
	
}
