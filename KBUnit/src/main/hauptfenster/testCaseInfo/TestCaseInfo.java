package main.hauptfenster.testCaseInfo;

import hilfe.guiHilfe.Formatation;
import main.dialogfenster.InfoDialog;
import main.hauptfenster.BasicFrameControl;
import main.hauptfenster.TestParameterInfo;
import main.hauptfenster.TestResultInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

/** 
 * TestCaseInfo speichert alle Informationen eines Testfalls, also Pfad und 
 * Beschreibung des Testzwecks und enthaelt eine ArrayList der zugehoerigen 
 * einzelnen Testkonfiguration.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */ 
public abstract class TestCaseInfo implements Comparable<TestCaseInfo>
{
	/** Testtyp ist JUnit 4 */
	public static final int TESTTYPE_JUNIT_4 = 4;
    /** Testtyp ist JUnit 5 */
    public static final int TESTTYPE_JUNIT_5 = 5;
 	// Testtype (JUnit4 oder JUnit5)
	private int testtype;  
	// Pfad des Testfalls
    private String path;
    // Beschreibung des Testfalls
    private String desc;
    // Display-Name des Testfalls, insbesondere für Junit5
    private String displayName;
    // Tags des Testfalls, insbesondere für Junit5
    private HashSet<String> tags = new HashSet<>();
    // Sagt an, ob der Test via die @Disabled-Annotation deaktiviert wurde
    private boolean disabled;
    // Falls vorhanden, die Erklaerung warum die @Disabled-Annotation gesetzt wurde
    private String disabledMessage;
    // die zum Testfall gehoerenden Testkonfigurationen
    private ArrayList<TestResultInfo> triList = new ArrayList<>();
    // Falls vorhanden, die Datentypen der Testmethode
    private ArrayList<String> argumentsTypes;
    // Angaben zu Anzahl und Erfolg gespeicherter Testkonfigurationen
    private int numberOfAllTestConfigurations;
    private int numberOfAllSuccessfulTestConfigurations;
    private int numberOfAllByAssumptionsAbortedTestConfigurations;
    private int numberOfAllSkippedTestConfigurations;
    private int numberOfAllFailedTestConfigurations;

    /**
     * Zeigt an, ob der Testfall bei einer der vorhandenen Testkonfigurationen
     * durch eine Assumption abgebrochen wurde
     * @return Wahrheitswert, ob eine Assumption zum Abbruch gesorgt hat
     */
    public boolean hasTestsAbortedByAssumption(){
        return numberOfAllByAssumptionsAbortedTestConfigurations > 0;
    }

    /**
     * Konstruktor
     * @param testtype Testtyp (JUnit3, JUnit4 oder JUnit5 , ...)
     * @param path Pfad des Testfalls (Package.Klasse.Methode)
     * @param desc Beschreibung des Testfalls
     */
    public TestCaseInfo(int testtype, String path, String desc)
    {
        this.testtype = testtype;
        this.path = path;
        this.desc = desc;
    }

    /**
     * ermittelt die Anzahl aller Testkonfigurationen zum Testfall
     * @return Anzahl gespeicherter Testkonfigurationen
     */
    public int getNumberOfAllTestConfigurations()
    {
        return numberOfAllTestConfigurations;
    }

    /**
     * setzt die Anzahl aller Testkonfigurationen zum Testfall
     * @param numberOfAllTestConfigurations int , neue Anzahl 
     *        von Testkonfigurationen
     */
    public void setNumberOfAllTestConfigurations(
    	int numberOfAllTestConfigurations) 
    {
        this.numberOfAllTestConfigurations 
            = numberOfAllTestConfigurations;
    }
      
    /**
     * ermittelt die Anzahl aller erfolgreich ausgefuehrten 
     * Testkonfigurationen zum Testfallobjekt
     * @return Anzahl gespeicherter, erfolgreicher Testkonfigurationen
     */
    public int getNumberOfAllSuccessfulTestConfigurations()
    {
        return numberOfAllSuccessfulTestConfigurations;
    }
    
    /**
     * setzt die Anzahl aller erfolgreichen Testkonfigurationen 
     * zum Testfall
     * @param numberOfAllSuccessfulTestConfigurations int , 
     *        neue Anzahl erfolgreicher Testkonfigurationen
     */
    public void setNumberOfAllSuccessfulTestConfigurations(
    	int numberOfAllSuccessfulTestConfigurations) 
    {
        this.numberOfAllSuccessfulTestConfigurations 
            = numberOfAllSuccessfulTestConfigurations;
    }

    /**
     * setzt die Anzahl aller abgebrochenen Testkonfigurationen
     * zum Testfall
     * @param numberOfAllByAssumptionsAbortedTestConfigurations int ,
     *        neue Anzahl erfolgreicher Testkonfigurationen
     */
    public void setNumberOfAllByAssumptionsAbortedTestConfigurations(
            int numberOfAllByAssumptionsAbortedTestConfigurations)
    {
        this.numberOfAllByAssumptionsAbortedTestConfigurations
            = numberOfAllByAssumptionsAbortedTestConfigurations;
    }

    /**
     * ermittelt die Anzahl aller übersprungener
     * Testkonfigurationen zum Testfallobjekt
     * @return Anzahl gespeicherter, übersprungener Testkonfigurationen
     */
    public int getNumberOfAllSkippedTestConfigurations()
    {
        return numberOfAllSkippedTestConfigurations;
    }

    /**
     * setzt die Anzahl aller übersprungenen Testkonfigurationen
     * zum Testfall
     * @param numberOfAllSkippedTestConfigurations int ,
     *        neue Anzahl übersprungener Testkonfigurationen
     */
    public void setNumberOfAllSkippedTestConfigurations(
        int numberOfAllSkippedTestConfigurations)
    {
        this.numberOfAllSkippedTestConfigurations
            = numberOfAllSkippedTestConfigurations;
    }

    /**
     * ermittelt die Anzahl aller durch Assumption abgebrochener
     * Testkonfigurationen zum Testfallobjekt
     * @return Anzahl gespeicherter, erfolgreicher Testkonfigurationen
     */
    public int getNumberOfAllByAssumptionsAbortedTestConfigurations()
    {
        return numberOfAllByAssumptionsAbortedTestConfigurations;
    }

    /**
     * setzt die Anzahl aller fehlgeschlagener Testkonfigurationen
     * zum Testfall
     * @param numberOfAllFailedTestConfigurations int ,
     *        neue Anzahl fehlgeschlagener Testkonfigurationen
     */
    public void setNumberOfAllFailedTestConfigurations(
        int numberOfAllFailedTestConfigurations)
    {
        this.numberOfAllFailedTestConfigurations
            = numberOfAllFailedTestConfigurations;
    }

    /**
     * ermittelt die Anzahl aller fehlgeschlagener
     * Testkonfigurationen zum Testfallobjekt
     * @return Anzahl gespeicherter, fehlgeschlagenerTestkonfigurationen
     */
    public int getNumberOfAllFailedTestConfigurations()
    {
        return numberOfAllFailedTestConfigurations;
    }

    /**
     * gibt den Testtype des Testfalls innerhalb der Testressource 
     * (JAR) zurueck
     *
     * @return Testtype des Testfalls
     */
    public int getTesttype()
    {
    	return this.testtype;
    }

    /**
     * gibt den Pfad des Testfalls innerhalb der Testressource 
     * (JAR) zurueck
     *
     * @return Pfad des Testfalls
     */
    public String getPath()
    {
        return this.path;
    }
    
    /**
     * gibt die Beschreibung des Testfalls zurueck
     *
     * @return Beschreibung des Testfalls
     */
    public String getDesc()
    {
    	return this.desc;
    }

    /**
     * setzt die Testfallergebnisse neu
     *
     * @param triList ArrayList von Testfallergebnissen
     */
    public void setTestResultInfoList(ArrayList<TestResultInfo> triList) 
    {
        this.triList = triList;
    }

    /**
     * gibt die Ergebnisse des Testfalls zurueck
     *
     * @return ArrayList Ergebnisse des Testfalls
     */
    public ArrayList<TestResultInfo> getTestResultInfoList() 
    {
        return triList;
    }


    /**
     * gibt den vollen Namen des Packages (kann mehrere Subpackages enthalten)
     * zurueck, in dem der Testfall liegt,
     *
     * @return Name des Testpackages
     */
    public String getPackageName()
    {
        // es wird der gesamte Pfad zurueckgegeben
        // (entweder ein Package oder eine Folge von Packages)
  		String subString = this.path.substring(0, this.path.lastIndexOf('.'));
    	return subString.substring(0, subString.lastIndexOf('.'));
    }

    /**
     * gibt den Namen der Klasse zurueck, in dem der Testfall liegt
     *
     * @return Name des Testklasse
     */
    public String getClassName()
    {
        int startindex = this.path.substring(0, 
        	this.path.lastIndexOf('.')).lastIndexOf('.');
        return this.path.substring(startindex + 1, 
        	this.path.lastIndexOf('.'));
    }
    
    /**
     * gibt den Namen derjenigen Klasse zurueck, in welcher die Testparameter liegen
     *
     * @return Name derjenigen Klasse zurueck, in welcher die Testparameter liegen
     */
    public abstract String getClassNameParameter();
      
    /**
     * gibt den Identifier der Testmethode zurueck
     *
     * @return Name der Testmethode
     */
    public String getIdentifierName()
    {
    	return getPath().substring(path.lastIndexOf('.') + 1);
    }

    /**
     * gibt eine Liste der Argumente (ihre Datentypen) der Testmethode zurück
     *
     * @return Datentypen der Parameter der Testmethode
     */
    public ArrayList<String> getArgumentsTypes() {
        return argumentsTypes;
    }

    /**
     * setzt die Datentypen der Parameter der Testmethode.
     */
    public void setArgumentsTypes(ArrayList<String> argumentsTypes) {
        this.argumentsTypes = argumentsTypes;
    }

    /**
     * gibt den Display-Namen der Testmethode zurueck.
     * Bei Junit-5 kann dieser mit der 'DisplayName'-Annotation definiert werden
     *
     * @return Display-Name der Testmethode
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * setzt den Display-Namen der Testmethode.
     * Bei Junit-5 kann dieser mit der 'DisplayName'-Annotation definiert werden
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * gibt den Boolean-Wert fuer die Disabled-Annotation zurueck,
     * ob der Test diese besitzt, oder nicht
     * @return ob Test disabled ist oder nicht
     */
    public boolean isDisabled() 
    { 
    	return disabled; 
    }

    /**
     * setzt den Boolean-Wert, ob Methode disabled wurde, oder nicht.
     * Bei Junit-5 kann dies ueber doe 'Disabled'-Annotation definiert werden
     */
    public void setDisabled(boolean disabled) 
    { 
    	this.disabled = disabled; 
    }

    /**
     * gibt die Disabled-Message der Testmethode zurueck.
     * Bei Junit-5 kann diese als Wert der Disabled-Annotation definiert werden
     *
     * @return Disabled-Message der Testmethode
     */
    public String getDisabledMessage() 
    { 
    	return disabledMessage; 
    }

    /**
     * setzt die Disabled-Message der Testmethode.
     * Bei Junit-5 kann diese als Wert der Disabled-Annotation definiert werden
     */
    public void setDisabledMessage(String disabledMessage) 
    { 
    	this.disabledMessage = disabledMessage; 
    }

    /**
     * gibt die Tags der Testmethode zurueck.
     * Bei Junit-5 können diese mit der 'Tag'-Annotation definiert werden
     *
     * @return Display-Name der Testmethode
     */
    public HashSet<String> getTags() 
    {
        return tags;
    }

    /**
     * setzt die Tags der Testmethode. Tags kann es bei JUnit 5 geben.
     */
    public void setTags(HashSet<String> tags) 
    { 
    	this.tags = tags; 
    }

    /**
     * gibt eine Liste aller Parameter zurueck, die dem Testfall in der
     * Testklasse zugeordnet sind
     *
     * @return Liste aller Testparameter
     */
    public ArrayList<Field> getParameters()
    {
        try
        {
           	String name = this.getPackageName() + "." 
                 + this.getClassNameParameter();
           	Class<?> cl1 = Class.forName(name, true, BasicFrameControl.CLASSLOADER);
            Field[] oeffentlicheParameter = cl1.getDeclaredFields();
            ArrayList<Field> parameters = new ArrayList<>();
            for (int i = 0; i < oeffentlicheParameter.length; i++)
            {
                if (oeffentlicheParameter[i].getName()
                	.contains(this.getIdentifierName()) 
                    && Formatation.parameterTypeSupported(oeffentlicheParameter[i]
                    .getType()))
                {
                    parameters.add(oeffentlicheParameter[i]);
                }
            }
          
            // Sortieren nach Eingangs- und Ausgangsparametern
            ArrayList<Field> sortedParameters = new ArrayList<>();
            int j = 0;  	
            for (int i = 0; i < parameters.size(); i++)
            {
                int index = getIdentifierName().length();
                if(parameters.get(i).getName().substring(index+1).length() >= 4   
                	&& parameters.get(i).getName()
                	.substring(index+1, index+5).equals("exp_"))
                {  
                    sortedParameters.add(j, parameters.get(i));
                }
                else
                {
                    j++;
                    sortedParameters.add(j-1, parameters.get(i));
                }
            }     	
            return sortedParameters;
        }
        catch (ClassNotFoundException cnfe)
        {
            new InfoDialog("Fehler beim Laden der Testparameter", 
                "Die Parameter des ausgewählten Testfalles konnten nicht ordnungsgemäß "
                + "geladen werden. Unter Umständen ist das zu testende Projekt fehlerhaft.");
            return null;
        }
    }
    
    /** 
     * sortiert die vorgegebene Liste von TestParamInfo-Objekten nach der Reihenfolge 
     * der Field-Parameter
     * @param pi, ArrayList mit TestParamInfo-Objekten, welche sortiert wird
     * @return geordnete ArrayList von TestParamInfo-Objekten
     */
    public  ArrayList<TestParameterInfo> sortTestParamInfo(ArrayList<TestParameterInfo> pi)
    {
    	ArrayList<TestParameterInfo> erg = new ArrayList<TestParameterInfo>();
    	ArrayList<Field> fieldParameter = this.getParameters();
    	String name = null;
    	int pos = 0;
    	for(int i = 0; i < fieldParameter.size(); i++)
    	{
    		name = fieldParameter.get(i).getName();
            while(!name.equalsIgnoreCase(pi.get(pos).getName()))
            {
            	pos++;
            }
            erg.add(pi.get(pos));
            pos = 0;;
    	}
    	return erg;
    }
    
    /**
     * implementiert das Interface Comparable, um Testfaelle untereinander
     * vergleichen zu koennen
     *
     * @param argument Testfallobjekt, welches mit dem vorliegenden
     *   Testfallobjekt verglichen werden soll
     * @return Ergebnis des Vergleichs
     */
    @Override
    public int compareTo(TestCaseInfo argument)
    {
        return this.path.compareTo(argument.path);
    }

    @Override
    public String toString() {
        return "TestCaseInfo{" +
            "testtype=" + testtype +
            ", path='" + path + '\'' +
            ", desc='" + desc + '\'' +
            ", displayName='" + displayName + '\'' +
            ", tags=" + tags +
            ", disabled=" + disabled +
            ", disabledMessage='" + disabledMessage + '\'' +
            ", triList=" + triList +
            ", argumentsTypes=" + argumentsTypes +
            ", numberOfAllTestConfigurations=" 
            + numberOfAllTestConfigurations +
            ", numberOfAllSuccessfulTestConfigurations=" 
            + numberOfAllSuccessfulTestConfigurations +
            ", numberOfAllByAssumptionsAbortedTestConfigurations=" 
            + numberOfAllByAssumptionsAbortedTestConfigurations +
            ", numberOfAllSkippedTestConfigurations=" 
            + numberOfAllSkippedTestConfigurations +
            '}';
    }
    

}
