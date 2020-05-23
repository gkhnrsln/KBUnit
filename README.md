# Dokumentation der Praxisphase
Beginn: 06.04.2020

Ende: 14.06.2020

Gökhan Arslan

Hochschule Bochum

<img src="https://www.hochschule-bochum.de/typo3conf/ext/hochschule_bochum/Resources/Public/Images/hs_bochum_logo.svg" alt="Kitten"
	title="A cute kitten" width="250" />

## Inhalt
Inhaltsangabe folgt

## Generieren von „CustomerTestCaseInformation.xml“ anhand von JavaDocs der JUnit Testklassen
Die *CustomerTestCaseInformation.xml* Datei wird vom Anwender noch selbst getippt. Sinnvoller wäre es diese generieren zu lassen. Die benötigten Informationen hierfür liefern die Testklassen im Source Verzeichnis „test“. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
    <date> 2020/04/14 17:41 </date>
    <testcases>
        <testcase>
            <path> darlehen.TilgungsdarlehenTest.testBerechneGesamtschuld </path>
            <desc> Dieser Test &#252;berpr&#252;ft die Berechnung der Gesamtschuld. </desc>
            <testtype> 5 </testtype>
            <parameters>
                <parameter>
                    <name> testBerechneGesamtschuld_User </name>
                    <desc> Benutzer, welcher die Berechnung durchf&#252;hren darf. </desc>
                </parameter>
                <parameter>
                    <name> testBerechneGesamtschuld_Darlehen </name>
                    <desc> Darlehen, das zur&#252;ckgezahlt werden soll </desc>
                </parameter>
                <parameter>
                    <name> testBerechneGesamtschuld_Laufzeit </name>
                    <desc> Laufzeit des Darlehens in Raten </desc>
                </parameter>
                <parameter>
                    <name> testBerechneGesamtschuld_Zinssatz </name>
                    <desc> Zinssatz, der vor Zahlung jeder Rate auf die Restschuld 
                        berechnet wird (1% = 0,01) </desc>
                </parameter>
                <parameter>
                    <name> testBerechneGesamtschuld_exp_ErwarteteGesamtschuld </name>
                    <desc> erwartete Gesamtschuld (Summe aller Tilgungen und Zinsen), 
                        die an den Kreditgeber zur&#228;ckgezahlt werden muss </desc>
                </parameter>
            </parameters>
        </testcase>
    </testcases>
</root>
```

Um die Generierung zu implementieren sind folgende Schritte notwendig:

### Schritt 1: root, date, testcases
Deklarierung der Klassenattribute von CreateCTCI
```java
private static Document doc;
private static Element elRoot;
private static Element elDate;
private static Element elTestCases;
private static Element elTestCase;
private static Element elPath;
private static Element elDescMethode;
private static Element elDescAttribut;
private static Element elTestType;
private static Element elParameters;
private static Element elParameter;
private static Element elName;
private static String strMissingDescs;
```
Instanziierung der Klassenattribute von CreateCTCI
```java
private static void load() {
    doc = new Document();
    elRoot = new Element("root");
    elDate = new Element("date");
    elTestCases = new Element(Variables.CTCI_NODE_TESTCASES);
    elTestCase = new Element(Variables.CTCI_NODE_TESTCASE);
    elPath = new Element(Variables.CTCI_NODE_PATH);
    elDescMethode = new Element(Variables.CTCI_NODE_DESC);
    elDescAttribut = new Element(Variables.CTCI_NODE_DESC);
    elTestType = new Element(Variables.CTCI_NODE_TESTTYPE);
    elParameters = new Element(Variables.CTCI_NODE_PARAMETERS);
    elParameter = new Element(Variables.CTCI_NODE_PARAMETER);
    elName = new Element(Variables.CTCI_NODE_NAME);
}
```

#### `<root>`

Das Root-Element ist das Wurzelverzeichnis des Dokuments.

#### `<date>`
Das Datum und die Uhrzeit wird mit der Funktion `LocalDateTime.now()` ermittelt. Hierbei wird die aktuelle Systemzeit verwendet. Als Rückgabewert wird dies jedoch in der ISO-8601 Norm angegeben (z. B. `2020-05-05T12:15:00.445`). Dies entspricht nicht dem gewünschten Format (`2020/05/05 12:15`). Die Funktion `DateTimeFormatter.ofPattern()` legt dieses Format fest. Mit der `LocalDateTime.now.format()` wird in das gewünschte Format formatiert und der Rückgabewert anschließend als Inhalt des Date-Elements festgelegt. 
Dem Root-Element werden als Kinder das Date-Element sowie das Testcases-Element hinzugefügt. 



## Automatisches Generieren eines normalen JUnit-Test in einen KBUnit-fähigen JUnit-Test
To be done! <code>dd</code>
