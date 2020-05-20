package prlab.kbunit.business;

import org.jdom2.Attribute;
import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.file.FileCreator;
import prlab.kbunit.test.TestObject;
import prlab.kbunit.test.TestResultInfo;
import prlab.kbunit.test.TestSource;
import prlab.kbunit.xml.XMLFileLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * The {@code Activity} class provides a static method for creating an activity-
 * log-file, that structures the test-cases which belong to a certain native 
 * test-method in two {@link Element}-nodes: active - for all available test-cases 
 * and inactive - for those, that have been deleted from the client's data-base, 
 * and another one, that arranges the IDs of the currently available test-cases  
 * in {@link Map}{@code <String,List<String>}.<br>
 *  
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev, Patrick Pete
 *
 */

public class XMLAccess {

	/**
	 * Creates an activity-log-file with root-{@code Element} 
	 * {@link TestSource#getClassName()}
	 * and associated child-Elements of the given TestSource, which in turn
	 * hold(s) respectively two sub-{@code Element}s: <b>active</b>, 
	 * containing test-cases, 
	 * that are currently available in the client's data-base and have been copied via 
	 * {@link FileCreator#copyFile()}, and also <b>inactive</b>,
	 * containing those of the test-cases, that are no longer available 
	 * (have been deleted from
	 * the data-base), but still have a .java-hard-copy and has to be deleted.
	 * <br>If <b>xmlFile</b> already exists the elements, representing every 
	 * single native test-method, 
	 * will be modified as follows: the new test-cases, that are performed after 
	 * the time point to
	 * be found under {@link Variables#CNFG_NODE_UPDATE}-node 
	 * of {@link Variables#CNFG_FILE_PATH}-
	 * configuration-file will be added as child-{@code Element}s in a 
	 * {@link Variables#ACTIVITY_ELEM_ACTIVE}-node and these, no longer 
	 * matching a cross reference 
	 * list of active test-cases will be relocated in 
	 * {@link Variables#ACTIVITY_ELEM_INACTIVE}-node 
	 * and marked for deleting.   
	 * <br><b>ROOT : </b>packageName'.'testClassName (i.e. darlehen.TilgungsdarlehenTest)
	 * <br><b>ATTRIBUTE : {@link Variables#ACTIVITY_ATTR_UPDATE}
	 *            TEXT :</b> date of creating this LOG-file
	 * <br><b>ELEMENT 1 : </b>testMethodName (i.e. testBerechneAnuitaetFuerPeriode)
	 * <br><b>ELEMENT 1.1 : </b> {@link Variables#ACTIVITY_ELEM_ACTIVE}
	 * <br>(Example)
	 *<br> <b>ID_333</b>
	 * <br><b>ID_334</b>
	 * <br><b>ELEMENT 1.2 : </b> {@link Variables#ACTIVITY_ELEM_INACTIVE}
	 * <br>(Example)
	 * <br><b>ID_337</b>
	 * <br><b>ELEMENT 2 : </b>testMethodName (i.e. testBerechneGesamtschuld)
	 * <br><b>ELEMENT 2.1 : </b> {@link Variables#ACTIVITY_ELEM_ACTIVE}
	 * <br>(Example)
	 * <br><b>ID_382</b>
	 * <br><b>ID_388</b>
	 * <br><b>ELEMENT 2.2 : </b> {@link Variables#ACTIVITY_ELEM_INACTIVE}
	 * <br>(Example)
	 * <br><b>ID_383</b>
	 * <br><b>ELEMENT n : </b>testMethodName 
	 * <br><b>ELEMENT n.1 : </b> {@link Variables#ACTIVITY_ELEM_ACTIVE}
	 * <br><b>...</b>
	 * <br><b>ELEMENT n.2 : </b> {@link Variables#ACTIVITY_ELEM_INACTIVE}
	 * <br><b>...</b>
	 *  
	 * @param testSource tests, which will be logged
	 * @param testObject testobject of the tests,which will be logged
	 * @param listCompare tests, which have been logged before
	 * @param xmlFile the .xml-{@code File} to be created/modified
	 * @return map of all inactive test-cases' IDs referring to 
	 *         their native test-methods - the map-keys
	 * @throws IOException 
	 * @throws JDOMException 
	 */ 
	public static Map<String,List<String>> createLOG(TestSource testSource,
		TestObject testObject, List<String> listCompare, File xmlFile) throws JDOMException, IOException {
		Document doc;
		Element  elementClass, elementMethod, elementNewMethod;

		String newFilePath = testSource.getPathNewGeneratedTestCases();
		String className   = testSource.getClassName();

		Map<String, List<String>> mapInactive = new HashMap<String, List<String>>();

		// UPDATING THE LOG FILE~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		if(xmlFile.exists()) {

			doc = XMLFileLoader.loadXML(xmlFile);
			elementClass = doc.getRootElement();

			for(String methodName : testSource.getTestMethodsByName()) {
				List<String> listInactive = new ArrayList<String>();
				if(elementClass.getChild(methodName) == null) {
					//INSERTING NEW METHOD THAT HAS NO PREVIOUS RECORDS IN LOG-----------
					elementNewMethod = new Element(methodName);

					elementNewMethod.addContent(new Element(
						Variables.ACTIVITY_ELEM_ACTIVE));
					elementNewMethod.addContent(new Element(Variables.ACTIVITY_ELEM_INACTIVE)
						.addContent
						(new Comment("These test-cases are no longer "
						+ "available and should be deleted!")));
					for(TestResultInfo testMethod : testObject.getTestMethods()) {
						if(methodName.equals(testMethod.getName())) {
							//EVERY SINGLE TEST-CASE AS NODE OF GENERIC TEST-METHOD-------
							elementNewMethod.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
							    .addContent(
							    new Element(Variables.ACTIVITY_ID_PREFIX + testMethod.getId()) 
								.setText(newFilePath));	
						}	
					}
					elementClass.addContent(elementNewMethod);

					continue;
				}
				//INSERTING NEW TEST-CASE IN EXISTING TEST-METHOD----------------------
				for(TestResultInfo testMethod : testObject.getTestMethods()) {

					if(methodName.equals(testMethod.getName())) {
						elementClass.getChild(methodName)
						.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
						.addContent(new Element(Variables.ACTIVITY_ID_PREFIX + testMethod.getId()) 
						.setText(newFilePath));
					}

				}
		
				List<Element> listActive = elementClass.getChild(methodName)
						.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
						.getChildren();
				for(int i=0; i<listActive.size(); i++) {
					// At this point listActive includes also the new test-cases!
					if(!listCompare.contains(listActive.get(i).getName())) {
						listInactive.add(listActive.get(i)
								.getName());
						/*
						 * You need to detach() the element from its parent,
						 * before you insert it in another document or node.
						 */
						elementClass.getChild(methodName).getChild(Variables
							.ACTIVITY_ELEM_INACTIVE).addContent((Element) listActive.get(i)
							.detach());
						// Due to detach() the size of list changes dynamically
						i--;
					}	
				}
				if(!listInactive.isEmpty())
					mapInactive.put(methodName, listInactive);

			}
			// INITIAL CREATING THE LOG-FILE			
		} 
		else {

			//TEST-CLASS AS ROOT ELEMENT--------------------------------
			elementClass = new Element(className); 

			doc = new Document(elementClass);

			Map<String, Element> mapElement = new HashMap<String, Element>();

			for(String methodName : testSource.getTestMethodsByName()) {
				elementMethod = new Element(methodName);
				elementMethod.addContent(new Element(Variables.ACTIVITY_ELEM_ACTIVE));
				elementMethod.addContent(new Element(Variables.ACTIVITY_ELEM_INACTIVE)
					.addContent
					(new Comment("These test-cases are no longer "
					+ "available and should be deleted!")));
				mapElement.put(methodName, elementMethod);
			}
			for(TestResultInfo testMethod : testObject.getTestMethods()) {
				//EVERY SINGLE TEST-CASE AS NODE OF GENERIC TEST-METHOD 
				mapElement.get(testMethod.getName())
				.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
				.addContent(new Element(Variables.ACTIVITY_ID_PREFIX + testMethod.getId()) 
						.setText(newFilePath));		
			}

			for(Map.Entry<String, Element> e : mapElement.entrySet()) {
				//GENERIC TEST-METHODS AS NODES OF THE CLASS ROOT-ELEMENT
				doc.getRootElement()
				.addContent(e.getValue());	   
			}	
		}

		elementClass.setAttribute(new Attribute(Variables.ACTIVITY_ATTR_UPDATE, 
				Variables.ACTIVITY_TIMESTAMP));

		// CREATING/MODIFYING THE ACTIVITY LOG~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		XMLOutputter xmlOutputter = new XMLOutputter();

		xmlOutputter.setFormat(Format.getPrettyFormat()
				.setEncoding(Variables.LOG_ENCODING));
		try {
			xmlOutputter.output(doc, 
				new FileWriter(FileCreator.createMissingPackages(xmlFile)));
		} 
		catch (IOException ioexc) {
			ioexc.printStackTrace();
		}	
		return mapInactive;

	}

	/**
	 * Creates {@link Document} from given {@code File} in order to provide
	 * an access to the {@link Variables#ACTIVITY_ELEM_ACTIVE} {@link Element}-node
	 * and its child elements - the IDs of all active test-cases, nested directly 
	 * (one level deep), and arranges these at first in {@code List<String>} and then
	 * adds them as {@code Map}-values in a {@code Map<String,List<String>}, according
	 * to their appurtenance to the corresponding native methods - the {@code Map}-key-set.
	 * 
	 * @param xmlFile to create {@code Document} from 
	 * @return map of the active test-cases' IDs
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public static Map<String, List<String>> getActiveTestCases(File xmlFile) throws JDOMException, IOException{
		Map<String, List<String>> mapActive = new HashMap<String, List<String>>();
		if(xmlFile.exists()) {
			Document doc = XMLFileLoader.loadXML(xmlFile);
			for(Element e1 : (List<Element>) doc.getRootElement().getChildren()) {
				List<String> listActive = new ArrayList<String>();
				for(Element e2 : (List<Element>) e1.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
					.getChildren()) {
					/*  e2 represents the ID of every single test-case to be
					 *  found in Element: Variables.ACTIVITY_ELEM_ACTIVE  
					 *  i.e. Input: "ID_333" ---> Output: "333"
					 */
					listActive.add(e2.getName()
						.substring(Variables.ACTIVITY_ID_PREFIX
						.length()));
					
				}
				mapActive.put(e1.getName(), listActive);
			}     
		}
		return mapActive;
	}


	public static Map<String, List<String>> getInactiveTestCases(File xmlFile) throws JDOMException, IOException{

		Map<String, List<String>> mapInactive = new HashMap<String, List<String>>();
		if(xmlFile.exists()) {
			Document doc = XMLFileLoader.loadXML(xmlFile);
			for(Element e1 : (List<Element>) doc.getRootElement().getChildren()) {
				// e1 represents a native method of class: doc.getRootElemen()
				List<String> listInactive = new ArrayList<String>();
				for(Element e2 : (List<Element>) 
					e1.getChild(Variables.ACTIVITY_ELEM_INACTIVE)
					.getChildren()) {
					/*  e2 represents the ID of every single test-case to be
					 *  found in Element: Variables.ACTIVITY_ELEM_ACTIVE  
					 *  i.e. Input: "ID_333" ---> Output: "333"
					 */
					listInactive.add(e2.getName()
						.substring(Variables.ACTIVITY_ID_PREFIX
						.length()));
				}
				mapInactive.put(e1.getName(), listInactive);
			}     
		}
		return mapInactive;
	}

	public static boolean deleteTestCase(TestSource testSource, 
		File xmlFile, int selectedID) throws JDOMException, IOException {
		Document doc;
		Element  elementClass;

		doc = XMLFileLoader.loadXML(xmlFile);
		elementClass = doc.getRootElement();
		for(String methodName : testSource.getTestMethodsByName()) {
			List<Element> listActive = elementClass.getChild(methodName)
				.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
				.getChildren();
			for(int i=0; i<listActive.size(); i++) {
				// At this point listActive includes also the new test-cases!
				if((listActive.get(i).getName()
					.equals(Variables.ACTIVITY_ID_PREFIX + Integer.toString(selectedID)))) {
					//System.out.println("Muss gelöscht werden!");
					elementClass.getChild(methodName)
					.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
					.removeChild(Variables.ACTIVITY_ID_PREFIX + Integer.toString(selectedID));
				}	
			}
		}


		for(String methodName : testSource.getTestMethodsByName()) {
			List<Element> listInactive = elementClass.getChild(methodName)
					.getChild(Variables.ACTIVITY_ELEM_INACTIVE)
					.getChildren();
			for(int i=0; i<listInactive.size(); i++) {
				// At this point listActive includes also the new test-cases!
				if((listInactive.get(i).getName().equals(Variables.ACTIVITY_ID_PREFIX 
					+ Integer.toString(selectedID)))) {
					//System.out.println("Muss geloescht werden!");
					elementClass.getChild(methodName)
					    .getChild(Variables.ACTIVITY_ELEM_INACTIVE)
					    .removeChild(Variables.ACTIVITY_ID_PREFIX 
						+ Integer.toString(selectedID));
				}	
			}
		}

		// INITIAL CREATING THE LOG-FILE~~~~~~~~~~~~~~~~~~~~~~~~			
		elementClass.setAttribute(new Attribute(Variables.ACTIVITY_ATTR_UPDATE, 
				Variables.ACTIVITY_TIMESTAMP));
		// CREATING/MODIFYING THE ACTIVITY LOG~~~~~~~~~~~~~~~~~~~~~~~~~~
		XMLOutputter xmlOutputter = new XMLOutputter();
		xmlOutputter.setFormat(Format.getPrettyFormat()
				.setEncoding(Variables.LOG_ENCODING));
		try {
			xmlOutputter.output(doc, 
				new FileWriter(FileCreator.createMissingPackages(xmlFile)));
		} 
		catch (IOException ioexc) {
			ioexc.printStackTrace();
		}	
		return true;
	}

	public static ArrayList<Map.Entry<String, String>> getTestPath(File xmlFile) throws JDOMException, IOException{
		ArrayList<Map.Entry<String, String>> pair 
		    = new ArrayList<Map.Entry<String, String>>();
		pair.addAll(getActiveTestPath(xmlFile));
		pair.addAll(getInactiveTestPath(xmlFile));
		return pair;
	}

	public static ArrayList<Map.Entry<String, String>> 
	    getActiveTestPath(File xmlFile) throws JDOMException, IOException{
		ArrayList<Map.Entry<String, String>> pair 
		    = new ArrayList<Map.Entry<String, String>>();
		if(xmlFile.exists()) {
			Document doc = XMLFileLoader.loadXML(xmlFile);
			for(Element e1 : (List<Element>) doc.getRootElement().getChildren()) {
				for(Element e2 : (List<Element>) e1.getChild(Variables.ACTIVITY_ELEM_ACTIVE)
					.getChildren()) {
					/*  e2 represents the ID of every single test-case to be
					 *  found in Element: Variables.ACTIVITY_ELEM_ACTIVE  
					 *  i.e. Input: "ID_333" ---> Output: "333"
					 */
					pair.add(new AbstractMap
						.SimpleEntry<String, String>(e2.getName().toString(), 
						e2.getContent(0).getValue().toString()));
				}
			}     
		}
		return pair;
	}

	public static ArrayList<Map.Entry<String, String>> 
	    getInactiveTestPath(File xmlFile) throws JDOMException, IOException{
		ArrayList<Map.Entry<String, String>> pair = new ArrayList<Map.Entry<String, String>>();
		if(xmlFile.exists()) {
			Document doc = XMLFileLoader.loadXML(xmlFile);
			for(Element e1 : (List<Element>) doc.getRootElement().getChildren()) {
				// e1 represents a native method of class: doc.getRootElemen()
				for(Element e2 : (List<Element>) e1.getChild(Variables.ACTIVITY_ELEM_INACTIVE)
						.getChildren()) {
					/*  e2 represents the ID of every single test-case to be
					 *  found in Element: Variables.ACTIVITY_ELEM_ACTIVE  
					 *  i.e. Input: "ID_333" ---> Output: "333"
					 */
					pair.add(new AbstractMap.SimpleEntry<String, String>(e2.getName().toString(), 
						e2.getContent(0).getValue().toString()));
				}
			}     
		}
		return pair;
	}
}



