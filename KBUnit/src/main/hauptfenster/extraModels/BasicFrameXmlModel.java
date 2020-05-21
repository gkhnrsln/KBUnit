package main.hauptfenster.extraModels;
   
import main.hauptfenster.BasicFrameControl;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfoJUnit;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * BasicFrameXmlModel ist ein Model des Hauptfensters der Anwendung. 
 * Es realisiert das Lesen der CustomerTestCaseInformation.xml ,
 * eine Datei mit den Informationen zu den Testfaellen, die der Entwickler erstellt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */
public abstract class BasicFrameXmlModel {
	
 	// Wurzel des XML-Dokuments
	private static Element rootNode;
	// Testtyp, legt fest, ob ein JUnit4 - Test, JUnit5 - Test, ... zugrunde liegt.
	// public static int testtype;

    // JAR-File, in dem sich das XML-Dokument befindet
    public static String jarFileName;
    
 	// das einzige BasicFrameXMLModel- Objekt (singleton - pattern)
    private static BasicFrameXmlModel basicFrameXmlModel;

    // der Konstruktor fuer das einmalige Kreieren eines BasicFrameXMLModel- Objekts 
    // (singleton - pattern)
    private BasicFrameXmlModel()
    {    	
    }
    
    /*
     * gibt das einzige BasicFrameXmlModel - Objekt heraus (singleton - Pattern).
     * Falls es noch nicht existiert, wird es erst kreiert.
     * @return das einzige BasicFrameXmlModel - Objekt
     */
    
     private static BasicFrameXmlModel getInstance()
    {
    	if(basicFrameXmlModel == null)
       	{
            basicFrameXmlModel = new BasicFrameXmlModelJUnit();
      	}
        return basicFrameXmlModel;
    }      
    
    /**
     * liefert eine Liste mit den Informationen (Pfad, Beschreibung, Version) 
     * aller im XML-Dokument aufgefuehrten Testfaelle zurueck
     *
     * @return Liste aller Testfaelle
     * @throws ClassNotFoundException 
     */
    public ArrayList<TestCaseInfo> getInformationOfAllTestCases() 
    	throws ClassNotFoundException
    {
    	List<?> testcases = getTestCases();
        ArrayList<TestCaseInfo> tcis = new ArrayList<>();
        TestCaseInfo tci = null;
        for (int i=0; i < testcases.size(); i++)
        {
            Element node = (Element) testcases.get(i);
        	tci = this.getSpecialTestCaseInformations(node);
            tcis.add(tci);
        }
        return tcis;
    }    
    
    protected abstract List<?> getTestCases();
      
    /**
     * liest das jar-File mit dem vorgegebenen Namen inklusive 
     * CustomerTestCaseInformation.xml, extrahiert das XML-Wurzelelement 
     * aus einem uebergebenen InputStreamReader und speichert es im Feld rootNode
     *
     * @param filename der Name des einzulesenden XML-Dokuments
     * @return BasicFrameXmoModel - Objekt
     * @throws JDOMException falls der Aufbau des Dokuments nicht korrekt ist
     * @throws IOException falls ein Fehler beim Lesen der Datei aufgetreten ist
     */
    public static BasicFrameXmlModel loadTestCaseInfo(String filename) 
        throws JDOMException, IOException
    {
        jarFileName = filename;
        JarFile jarFile = new JarFile(filename);
        JarEntry entry = jarFile.getJarEntry("CustomerTestCaseInformation.xml");
        InputStreamReader stream = new InputStreamReader(jarFile.getInputStream(entry), "UTF-8");
        SAXBuilder builder = new SAXBuilder();
        Document document = (Document) builder.build(stream);
        rootNode = document.getRootElement();
        jarFile.close();
        return getInstance();
    }
      
     
    /**
     * liefert die Beschreibung eines bestimmten Parameters zurueck
     *
     * @param testCaseName Name des Testfalls, zu dem der Parameter gehoert
     * @param parameterName Name des Parameters
     *
     * @return im XML-Dokument hinterlegte Beschreibung des Parameters
     */  
    public String getParameterInfo(String testCaseName, String parameterName)
    {
     	String paramDesc = "";
       	List<?> testcases = getTestCases();
        for (int i=0; i< testcases.size(); i++)
        {
            Element node = (Element) testcases.get(i);
            if (node.getChild("path").getText().trim().equals(testCaseName))
            {
                List<?> parameter = node.getChild("parameters").getChildren("parameter");
                for (int j = 0; j<parameter.size(); j++)
                {
                    Element param = (Element) parameter.get(j);
                    if (param.getChild("name").getText().trim().equals(parameterName))
                    {
                        paramDesc = param.getChild("desc").getText().trim();
                    }
                }
            }
        }
        return paramDesc;
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
                throw new IllegalArgumentException(
                	"Die übergebenen Testfälle müssen alle aus " +
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

    // holt den Pfad und die Beschreibungen zu dem Testfall des vorgegebenen Knotens
    protected abstract TestCaseInfo getSpecialTestCaseInformations(Element node)
        throws ClassNotFoundException;
          
    // liest die CustomerTestCaseInformation.xml von JUnit Tests
    private static final class BasicFrameXmlModelJUnit extends BasicFrameXmlModel
    {
    	
    	// Konstruktor zum Erzeugen eines BasicFrameXmlModelJUnit - Objekts
        private BasicFrameXmlModelJUnit()
        {
        	super();
        }
        
        // holt Elemente des xml-Dokuments zum Tag testcase
        protected List<?> getTestCases()
        {
            return rootNode.getChild("testcases").getChildren("testcase");
        }
        

        /**
         * holt den Pfad, die Beschreibungen zu dem Testfall des vorgegebenen Knotens.
         * Wenn es sich um JUnit-5-Tests handelt, werden mit Reflection weitere
         * Informationen zu den Testfall extrahiert und im TestCaseInfo-Objekt gespeichert
         *
         * @param node Knoten der CustomerTestCase.xml-Datei
         * @return TestCaseInfo - Objekt
         * @throws ClassNotFoundException 
         */
        protected TestCaseInfo getSpecialTestCaseInformations(Element node) 
        	throws ClassNotFoundException 
        {
            String path = node.getChildTextNormalize("path");
            String desc = node.getChildTextNormalize("desc");
            int testtype = Integer.parseInt(node.getChildTextNormalize("testtype"));
            TestCaseInfo newJunitTestCaseInfo =
                new TestCaseInfoJUnit(testtype, path, desc);
            if(testtype == TestCaseInfo.TESTTYPE_JUNIT_5)
            {
                String displayName = "";
                String disabledMessage = "";
                String className = path.substring(0, path.lastIndexOf('.'));
                String methodName = path.substring(path.lastIndexOf('.') + 1);
                // der Testfall wird geladen
                Class<?> testclass =
                    Class.forName(className, true
                    , BasicFrameControl.CLASSLOADER);
                HashSet<String> tags = new HashSet<String>();
                ArrayList<String> argumentTypes = new ArrayList<>();
                if(testclass.isAnnotationPresent(Tag.class)){
                    Annotation[] annotationsByType = testclass
                        .getAnnotationsByType(Tag.class);
                    for(Annotation annotation : annotationsByType)
                    {
                        tags.add(((Tag) annotation).value());
                    }
                }
                if(testclass.isAnnotationPresent(Disabled.class))
                {
                    newJunitTestCaseInfo.setDisabled(true);
                    disabledMessage = testclass.getAnnotation(Disabled.class)
                        .value();
                    newJunitTestCaseInfo.setDisabledMessage(disabledMessage);
                }
                Method[] methods = testclass.getDeclaredMethods();
                for(Method m : methods)
                {
                    if(m.getName().equals(methodName))
                    {
                        Class<?>[] methodArgumentsTypes = m.getParameterTypes();
                        for(Class<?> clazz : methodArgumentsTypes)
                        {
                            argumentTypes.add(clazz.getTypeName());
                        }
                        if(m.isAnnotationPresent(DisplayName.class))
                        {
                            displayName = m.getAnnotation(DisplayName.class).value();
                            newJunitTestCaseInfo.setDisplayName(displayName);
                        }
                        Annotation[] annotationsByType =
                            m.getAnnotationsByType(Tag.class);
                        for(Annotation annotation : annotationsByType)
                        {
                            tags.add(((Tag) annotation).value());
                        }
                        if(m.isAnnotationPresent(Disabled.class)){
                            disabledMessage = m.getAnnotation(Disabled.class)
                                .value();
                            newJunitTestCaseInfo.setDisabled(true);
                            newJunitTestCaseInfo.setDisabledMessage(disabledMessage);
                        }
                        break;
                    }
                }
                newJunitTestCaseInfo.setTags(tags);
                newJunitTestCaseInfo.setArgumentsTypes(argumentTypes);
            }
            return newJunitTestCaseInfo;
        }
    }

}
    