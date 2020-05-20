package prlab.kbunit.reflection;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * The {@code TestPerformer} class provides a static
 * method for performing JUnitTests using reflection on
 * {@code TestSource}-file and for creating a XML-{@link Document}
 * that contains test-result info associated with every single
 * test-case, which belongs to a given {@code TestObject}.<br>
 *
 * &copy; 2017 Alexander Georgiev, Yannis Herbig, Ursula Oesing  <br>
 * 
 * @author Yannis Herbig
 * 
 *
 */
public class TestPerformerJUnit5 extends TestPerformer{

	// containing the testMethods of the testcase
	private List<TestObject> testObjects;
	// contains informations about the test class
	private List<TestSource> testSources;
	private Map<Integer, List<String[]>> multiTestRunMap;

	/**
	 * creates an TestPerformer object 
	 * @param testSource, contains informations about the test class
	 * @param testObject, contains the testMethods of the testcase
	 * @param selection, contains informations about, which testclasses 
	 *                   has been selected
	 */
	public TestPerformerJUnit5(TestSource testSource, 
			TestObject testObject, Selection  selection){
		super(testSource, testObject, selection);
		this.multiTestRunMap = new HashMap<>();
	}

	/**
	 * creates an TestPerformer object
	 * @param testSources, contains informations about the test classes
	 * @param testObjects, contains the testMethods of the testcases
	 * @param selection, contains informations about, which testclasses
	 *                   has been selected
	 */
	public TestPerformerJUnit5(List<TestSource> testSources,
						 List<TestObject> testObjects, Selection  selection){
		super();
		this.testSources = testSources;
		this.testObjects = testObjects;
		setSelection(selection);
		this.multiTestRunMap = new HashMap<>();
	}

	public Map<Integer, List<String[]>> getMultiTestRunMap() {
		return multiTestRunMap;
	}

	/**
	 * Performs and runs tests (exception-handling is considered)
	 * using reflection on {@link TestSource}-file and orders the test-results,
	 * filtered by {@link Selection}, into {@link Document} to be used by creation
	 * of a .xml-LOG-file as follows:
	 *
	 * <br><b>ROOT : </b>packageName'.'testClassName (i.e. darlehen.TilgungsdarlehenTest)
	 * <br><b>ATTRIBUTE : {@link Variables#LOG_ATTR_CREATE}
	 * <br>           TEXT :</b> date of creating this LOG-file
	 * <br><b>ELEMENT 1 : </b>testMethodName (i.e. testBerechneAnuitaetFuerPeriode)
	 * <br><b>testCase 1.1</b> (i.e. ID314__2015-05-08__12.42.07____FAILED)
	 * <br><b>parameterName 1.1.1</b>
	 * <br><b>parameterName 1.1.2</b>
	 * <br><b>parameterName 1.1.n</b>
	 * <br><b>testCase 1.2</b>
	 * <br><b>testCase 1.n</b>
	 * <br><b>ELEMENT 2 : </b>testMethodName (i.e. testBerechneGesamtschuld)
	 * <br><b>testCase 2.1
	 * <br>  testCase 2.2
	 * <br>testCase 2.n</b>
	 * <br><b>ELEMENT n : </b>testMethodName
	 * <br> <b>testCase n.1
	 * <br>  testCase n.2
	 * <br>testCase n.n</b>
	 *
	 * @return XML-Document to be used for creating LOG-file
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	@Override
	public Document createDocument() {

		ArrayList<TestResultInfo> testMethods = getTestObject().getTestMethods();

		//TEST-CLASS AS ROOT ELEMENT
		Element elementClass = new Element(getTestSource().getClazz().getName());
		elementClass.setAttribute(new Attribute(Variables.LOG_ATTR_CREATE,
				Variables.LOG_TIMESTAMP));

		Document doc = new Document(elementClass);

		Map<String, Integer> configsCounterMap = new HashMap<>();
		Map<String, List<TestResultInfo>> sortedConfigurations = new HashMap<>();
		for(TestResultInfo testMethod : testMethods) {
			String methodName = testMethod.getIdentifierName();
			if(!sortedConfigurations.containsKey(testMethod.getName())){
				ArrayList<TestResultInfo> allConfigsForSameMethod = new ArrayList<>();
				allConfigsForSameMethod.add(testMethod);
				sortedConfigurations.put(testMethod.getName(), allConfigsForSameMethod);
				configsCounterMap.put(methodName, 1);
			}
			else{
				sortedConfigurations.get(testMethod.getName()).add(testMethod);
				configsCounterMap.put(methodName, configsCounterMap.get(methodName) + 1);
			}
		}

		Map<String, Element> mapElement = new HashMap<String, Element>();

		String[] methods = getTestSource().getTestMethodsByName();
		String[] fqmns = getTestSource().getTestMethodsByFQMN();

		for (int i = 0; i < methods.length; i++) {
			/*
			 *  This Map includes only Elements of the original
			 *  test-Methods (not the test-cases from the client's database!),
			 *  Their names serve as generic ones for these nodes,
			 *  which represent single test-cases. At last every test-case
			 *  belongs to its original test-Method in the tree-structure
			 *  of the .XML-LOG-file.
			 */
			mapElement.put(fqmns[i], new Element(methods[i]));
		}
		String testParameterValue = null;
		ArrayList<TestParameterInfo> newparameters = new ArrayList<TestParameterInfo>();
		Field[] fields = getTestSource().getClazz().getFields();
		List<String> parameternames = new ArrayList<>();
		for (Field field : fields) {
			parameternames.add(field.getName());
		}
		int maxConfigs = Collections.max(configsCounterMap.values());
		Map<String, TestResultInfo> testConfigsBatchToRun = new HashMap<>();
		Map<String, Element> elementMethodsMap = new HashMap<>();
		for (int i = 0; i < maxConfigs; i++) {
			for(Map.Entry<String, List<TestResultInfo>> sortedConfigsEntry : sortedConfigurations.entrySet()){
				List<TestResultInfo> testConfigs = sortedConfigsEntry.getValue();
				if(i < testConfigs.size()){  // Es gibt noch weitere Testconfigs, die noch nicht durchlaufen wurden
					TestResultInfo testConfig = testConfigs.get(i);
					String fullIdentifier = testConfig.getPackageName()
						+ "." + testConfig.getClassName()
						+ "." + testConfig.getIdentifierName();
					testConfigsBatchToRun.put(fullIdentifier, testConfig);

					Element elementMethod = new Element("default");
					for (int j = 0; j < parameternames.size(); j++) {
						for (int k = 0; k < testConfig.getParameters().size(); k++) {
							if (parameternames.get(j).equals(testConfig.getParameters()
								.get(k).getName())) {
								newparameters.add(testConfig.getParameters().get(k));
							}
						}
					}
					//SET NEW TEST-PARAMETERS FOR EVERY TEST-CASE----------------------
					for (TestParameterInfo testParameter : newparameters) {
						//TEST-PARAMETERS AS NODES OF EVERY SINGLE TEST-CASE------------------
						try {
							testParameterValue = getTestSource().getTestParameterTypeValue(testParameter);
							elementMethod.addContent(new Element
								(testParameter.getName())
								.setText(testParameterValue));
						} catch (NoSuchFieldException nsfExc) {
							nsfExc.printStackTrace();
						}
					}
					elementMethodsMap.put(fullIdentifier, elementMethod);
					newparameters.clear();
				}
			}
			//RUNS SINGLE TEST-CASE-------------------------------
			try {
				TestSourceJUnit5.runTestCases(testConfigsBatchToRun
					, mapElement, elementMethodsMap, getSelection(), this);
			} catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (JDOMException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			testConfigsBatchToRun.clear();
		}
		for (Map.Entry<String, Element> e : mapElement.entrySet()) {
			lookForEventualTestContainerResultStatusUpdate(e);
			//GENERIC TEST-METHODS AS NODES OF THE CLASS ROOT-ELEMENT------------------
			doc.getRootElement()
					.addContent(e.getValue());
		}
		return doc;
	}

	private void lookForEventualTestContainerResultStatusUpdate(
		Map.Entry<String, Element> e) {
		if (e.getValue().getContentSize() == 0)
			e.getValue().addContent
				(new Comment("Selection." + getSelection()
				+ ":  No such test cases found!"));
		for(Element element : e.getValue().getChildren()){
			String tagName = element.getName();
			int id = Integer.parseInt(tagName.substring(2, tagName.indexOf("__")));
			String testExecutionResultStatus = tagName.substring(
				tagName.indexOf("____") + 4);
			if(this.multiTestRunMap.containsKey(id)
				&& !testExecutionResultStatus.equals("FAILED")){
				for(String[] result : this.multiTestRunMap.get(id)){
					if(result[1].equals("FAILED")){
						element.setName(tagName.replace(testExecutionResultStatus,
							"FAILED"));
						break;
					}
					else if(result[1].equals("ERROR")){
						element.setName(tagName.replace(testExecutionResultStatus,
							"ERROR"));
					}
					else if(result[1].equals("ABORTED_BY_ASSUMPTION")
						&& !testExecutionResultStatus.equals("ERROR")){
						element.setName(tagName.replace(testExecutionResultStatus,
							"ABORTED_BY_ASSUMPTION"));
					}
					else if(result[1].equals("SKIPPED")
						&& !testExecutionResultStatus.equals("ERROR")
						&& !testExecutionResultStatus.equals("ABORTED_BY_ASSUMPTION")){
						element.setName(tagName.replace(testExecutionResultStatus,
							"SKIPPED"));
					}
					testExecutionResultStatus = element.getName()
						.substring(tagName.indexOf("____") + 4);
				}
			}
		}
	}

	/**
	 * Performs and runs tests (exception-handling is considered)
	 * using reflection on {@link TestSource}-file and orders the test-results,
	 * filtered by {@link Selection}, into {@link Document} to be used by creation
	 * of a .xml-LOG-file as follows:
	 * @return XML-Documents to be used for creating LOG-files
	 */
	public Map<String, Document> createDocuments() {
		Map<String, Document> documentMap = new HashMap<>();
		List<TestResultInfo> testMethods = new ArrayList<>();

		for(TestObject testObject : testObjects){
			testMethods.addAll(testObject.getTestMethods());
		}
		for(TestSource testSource : testSources){
			//TEST-CLASS AS ROOT ELEMENT
			String className = testSource.getClazz().getName();
			Element elementClass = new Element(className);
			elementClass.setAttribute(new Attribute(Variables.LOG_ATTR_CREATE,
					Variables.LOG_TIMESTAMP));
			Document doc = new Document(elementClass);
			documentMap.put(className, doc);
		}

		Map<String, Integer> configsCounterMap = new HashMap<>();
		Map<String, List<TestResultInfo>> sortedConfigurations = new HashMap<>();
		for(TestResultInfo testMethod : testMethods) {
			String methodName = testMethod.getIdentifierName();
			if(!sortedConfigurations.containsKey(testMethod.getName())){
				ArrayList<TestResultInfo> allConfigsForSameMethod = new ArrayList<>();
				allConfigsForSameMethod.add(testMethod);
				sortedConfigurations.put(testMethod.getName(), allConfigsForSameMethod);
				configsCounterMap.put(methodName, 1);
			}
			else{
				sortedConfigurations.get(testMethod.getName()).add(testMethod);
				configsCounterMap.put(methodName, configsCounterMap.get(methodName) + 1);
			}
		}

		Map<String, Element> mapElement = new HashMap<>();

		for(TestSource testSource : testSources){
			String[] methods = testSource.getTestMethodsByName();
			String[] fqmns = testSource.getTestMethodsByFQMN();
			for (int i = 0; i < methods.length; i++) {
				/*
				 *  This Map includes only Elements of the original
				 *  test-Methods (not the test-cases from the client's database!),
				 *  Their names serve as generic ones for these nodes,
				 *  which represent single test-cases. At last every test-case
				 *  belongs to its original test-Method in the tree-structure
				 *  of the .XML-LOG-file.
				 */
				mapElement.put(fqmns[i], new Element(methods[i]));
			}
		}

		String testParameterValue = null;

		ArrayList<TestParameterInfo> newparameters = new ArrayList<TestParameterInfo>();
		Map<String, List<String>> parameterNamesMap = new HashMap<>();
		for(TestSource testSource : testSources) {
			Field[] fields = testSource.getClazz().getFields();
			List<String> parameternames = new ArrayList<>();
			for (Field field : fields) {
				parameternames.add(field.getName());
			}
			parameterNamesMap.put(testSource.getClazz().getName(), parameternames);
		}

		int maxConfigs = Collections.max(configsCounterMap.values());
		Map<String, TestResultInfo> testConfigsBatchToRun = new HashMap<>();
		Map<String, Element> elementMethodsMap = new HashMap<>();
		for (int i = 0; i < maxConfigs; i++) {
			for(Map.Entry<String, List<TestResultInfo>> sortedConfigsEntry : sortedConfigurations.entrySet()){
				List<TestResultInfo> testConfigs = sortedConfigsEntry.getValue();
				if(i < testConfigs.size()){  // Es gibt noch weitere Testconfigs, die noch nicht durchlaufen wurden
					TestResultInfo testConfig = testConfigs.get(i);
					String fullIdentifier = testConfig.getPackageName()
						+ "." + testConfig.getClassName()
						+ "." + testConfig.getIdentifierName();
					testConfigsBatchToRun.put(fullIdentifier, testConfig);
					String className = testConfig.getPackageName()
							+ "." + testConfig.getClassName();
					for (int j = 0; j < parameterNamesMap.get(className).size(); j++) {
						for (int k = 0; k < testConfig.getParameters().size(); k++) {
							if (parameterNamesMap.get(className).get(j).equals(testConfig.getParameters()
								.get(k).getName())) {
									newparameters.add(testConfig.getParameters().get(k));
							}
						}
					}
					Element elementMethod = new Element("default");
					//SET NEW TEST-PARAMETERS FOR EVERY TEST-CASE----------------------
					for (TestParameterInfo testParameter : newparameters) {
						//TEST-PARAMETERS AS NODES OF EVERY SINGLE TEST-CASE------------------
						try {
							TestSource currentTestSource = null;
							for(TestSource testSource : testSources){
								if(testSource.getTestMethodsByFQMN().length > 0){
									String testSourceClassName = testSource.getTestMethodsByFQMN()[0]
										.substring(0, testSource.getTestMethodsByFQMN()[0].lastIndexOf("."));
									String testConfigClassName = testConfig.getPackageName()
										+ "." + testConfig.getClassName();
									if(testSourceClassName.equals(testConfigClassName)){
										currentTestSource = testSource;
										break;
									}
								}
							}
							assert currentTestSource != null;
							testParameterValue = currentTestSource.getTestParameterTypeValue(testParameter);
							elementMethod.addContent(new Element
									(testParameter.getName())
									.setText(testParameterValue));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					elementMethodsMap.put(fullIdentifier, elementMethod);
					newparameters.clear();
				}
			}
			//RUNS ONE OR MANY TEST-CASE-------------------------------
			try {
				TestSourceJUnit5.runTestCases(testConfigsBatchToRun
					, mapElement, elementMethodsMap, getSelection(), this);
			} catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException e) {
				e.printStackTrace();
			} catch (JDOMException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			testConfigsBatchToRun.clear();
			elementMethodsMap.clear();
		}
		for (Map.Entry<String, Element> e : mapElement.entrySet()) {

			lookForEventualTestContainerResultStatusUpdate(e);
			//GENERIC TEST-METHODS AS NODES OF THE CLASS ROOT-ELEMENT------------------
			documentMap.get(e.getKey().substring(0, e.getKey().lastIndexOf("."))).getRootElement()
				.addContent(e.getValue());
		}
		return documentMap;
	}

}
