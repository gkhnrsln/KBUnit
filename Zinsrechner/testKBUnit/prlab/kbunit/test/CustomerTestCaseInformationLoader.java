package prlab.kbunit.test;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.xml.XMLFileLoader;

import java.io.IOException;
import java.util.List;

/**
 * This {@code CustomerTestCaseInformation} class provides methods
 * for loading and retrieving the test case information from the
 * {@link Variables#CUSTOMER_TEST_CASE_INFO_FILE_PATH}- file. <br>
 * 
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 *
 * @author Alexander Georgiev, Patrick Pete
 *
 */
public class CustomerTestCaseInformationLoader {

	// for loading the XML-Configuration file
	private XMLFileLoader xmlFileLoader;
	// das einzige CustomerTestCaseInformationLoader- Objekt (singleton - pattern)
	private static CustomerTestCaseInformationLoader ctcil;

	/**
	 * On creation a {@link Variables#CUSTOMER_TEST_CASE_INFO_FILE_PATH}-file
	 * will be loaded.
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	private CustomerTestCaseInformationLoader() throws JDOMException, IOException {
		xmlFileLoader = new XMLFileLoader(Variables.CUSTOMER_TEST_CASE_INFO_FILE_PATH);
	}

	public static CustomerTestCaseInformationLoader getInstance() throws JDOMException, IOException
	{
		if(ctcil == null)
		{
			ctcil = new CustomerTestCaseInformationLoader();
		}
		return ctcil;
	}

	/*
	 * Get testtype by path of file
	 */
	public int getTesttype(String path) {
		Document document = xmlFileLoader.getXmlDoc();
		List<Element> testcases = document.getRootElement().getChild("testcases").getChildren();
		for (int i = 0; i < testcases.size(); i++) {
			Element testcase = testcases.get(i);
			String pathValue = testcase.getChildTextNormalize("path");
			if(path.substring(path.indexOf("\\") + 1, path.length() - Variables.EXTENSION_JAVA.length()).replace("\\", ".")
				.equals(pathValue.substring(0, pathValue.lastIndexOf(".")))){
				String testtypeValue = testcase.getChildTextNormalize("testtype");
				return Integer.parseInt(testtypeValue);
			}
		}
		return -1;
	}

	/**
	 * liefert eine Antwort darauf, ob sich im XML-Dokument noch
	 * weitere Testmethoden aus der Testklasse befinden, aus der auch die
	 * uebergebenen Testmethoden kommen
	 *
	 * @param testCaseNames Namen des Testfälle, die ale aus einer Testklasse kommen
	 *
	 * @return Wahrheitswert, ob im XML-Dokumente noch Testfaelle aus der selben Klasse sind,
	 * wie die Testklasse, aus der die uebergebenen Testfaelle stammen
	 */
	public boolean containsAnotherTestCaseFromSameClass(List<String> testCaseNames)
	{
		assert testCaseNames.size() > 0;
		String testClassName = testCaseNames.get(0).substring(0, testCaseNames.get(0)
			.lastIndexOf("."));
		for (String testCaseName : testCaseNames) {
			if(!testClassName.equals(testCaseName.substring(0, testCaseName.lastIndexOf(".")))){
				throw new IllegalArgumentException("Die übergebenen Testfälle müssen alle aus " +
					"der selben Klasse stammen");
			}
		}
		List<?> testcases = getTestCases();
		for (int i=0; i< testcases.size(); i++)
		{
			Element node = (Element) testcases.get(i);
			String path = node.getChild("path").getText().trim();

			if (path.substring(0, path.lastIndexOf(".")).equals(testClassName)
				&& !testCaseNames.contains(path))
			{
				return true;
			}
		}
		return false;
	}

	// holt Elemente des xml-Dokuments zum Tag testcase
	private List<?> getTestCases() {
		Document document = xmlFileLoader.getXmlDoc();
		return document.getRootElement().getChild("testcases").getChildren("testcase");
	}
}
