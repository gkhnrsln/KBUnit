package main.hauptfenster;

import hilfe.guiHilfe.ImageTextField;
import hilfe.guiHilfe.SpecialColor;
import hilfe.guiHilfe.TabbedPaneUI;
import main.dialogfenster.InfoDialog;
import main.dialogfenster.OpenProjectDialog;
import main.dialogfenster.YesNoDialog;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.panel.ParameterPanel;
import main.panel.navigation.NavigationPanel;
import main.panel.testDurchfuehren.ParameterVerifier;
import main.panel.testDurchfuehren.konfigurationsgenerator.Combination;
import main.panel.testDurchfuehren.konfigurationsgenerator.KonfigurationsgeneratorPanel;
import main.panel.testDurchfuehren.neueKonfiguration.NeueKonfigurationPanel;
import main.panel.testDurchfuehren.testergebnisse.TestergebnissePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * BasicFrameView ist das View des Hauptfensters der KBUnit-Anwendung.
 * Es werden JUnit-Tests gekapselt und ueber diese grafische Benutzeroberflaeche
 * via Reflection zur Bearbeitung zur verfuegung gestellt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class BasicFrameView extends JFrame
{
   	private static final long serialVersionUID = 1L;
	// Menue
    private JMenuBar menubar;
    private JMenu mnFile;
    private JMenuItem mnFileExit;
    private JMenu mnInformation;
    private JMenuItem mnInformationLicence;
    private JMenuItem mnInformationDoc;

    // Alle Komponenten werden in der Ansicht gruppiert, sie gehoeren entweder 
    // zur Baumansicht links oder zum ausgewaehlten Testfall rechts; 
    // dazwischen befindet sich ein Splitpane
    NavigationPanel pnlNavigation;
    private JPanel pnlTest;
    private JSplitPane sp;

    // Anzeigebereich auf der linken Seite oben (immer sichtbar; 
    // zeigt allgemeine Testinformationen)
    private JLabel lblOpenedTest;

    // darunter kommed ein TabbedPane, welches verschiedene Anzeigebereiche 
    // (pnlTestresult, pnlNewConfiguration, pnlConfigurationGenerator) enthaelt
    JTabbedPane tpTest;

    // Anzeigebereich auf der linken Seite unten - sichtbar im Testergebnisse-Modus
    private TestergebnissePanel pnlTestergebnisse;
    
    public TestergebnissePanel getPnlTestergebnisse()
    {
        return this.pnlTestergebnisse;    
    }   
    
    // Anzeigebereich auf der linken Seite unten - sichtbar im Konfigurationsmodus 
    NeueKonfigurationPanel pnlNeueKonfiguration;
    // Anzeigebereich auf der linken Seite unten - sichtbar im Modus zur 
    // Erstellung optimaler Testonfigurationen
    private KonfigurationsgeneratorPanel pnlKonfigurationsgenerator;

    public KonfigurationsgeneratorPanel getPnlKonfigurationsgenerator() 
    {
        return pnlKonfigurationsgenerator;
    }
    
    // model, das zum Frame gehoert
    private BasicFrameModel basicFrameModel; 
    // control, das zum Frame gehoert
    private BasicFrameControl basicFrameControl; 
    
    public BasicFrameControl getBasicFrameControl() 
    {
		return basicFrameControl;
	}

	/**
     * Konstruktor - erstellt das Hauptfenster der Anwendung
     * @param basicFrameControl , das Control-Objekt zu dem Hauptfenster
     * @param basicFrameModel , das Model-Objekt zu dem Hauptfenster
     */
    public BasicFrameView(BasicFrameControl basicFrameControl, 
    	BasicFrameModel basicFrameModel) 
    {
        super();
        this.basicFrameControl = basicFrameControl;
        this.basicFrameModel = basicFrameModel;
        // initialisiert und zeichnet die GUI-Komponenten
        this.initComponents();
        // initialisiert die Listener
        this.initListener(); 
        // zeigt den Oeffnen-Dialog (die Anwendung wird erst sichtbar, wenn der 
        // Nutzer ein Projekt zum Testen ausgewaehlt hat)
        this.loadTestCaseInfo();
    }


    /**
     * legt die GUI-Komponenten an 
     * @throws IOException 
     */
    private void initComponents() 
    {
        // Einstellungen des Fensters
        this.setTitle(" KBUnit");
    	Image img = Toolkit.getDefaultToolkit().createImage(new File("icons\\kbunit.png")
    		.getAbsolutePath());
        this.setIconImage(img);
        this.setMinimumSize(new Dimension(800, 500));
        this.setSize(900, 500);
        this.setLocationRelativeTo(null);
        this.setVisible(false);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new GridBagLayout());

        // Constraints fuer das GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();

        // Menue anlegen
        this.menubar = new JMenuBar();
        this.mnFile = new JMenu("Programm");
        this.mnFileExit = new JMenuItem("Beenden");
        this.mnInformation = new JMenu("Information");
        this.mnInformationLicence = new JMenuItem("Information zur Lizenz öffnen");
        this.mnInformationDoc = new JMenuItem("Dokumentation öffnen");
        this.menubar.setPreferredSize(new Dimension(400, 25));
        this.menubar.setMinimumSize(new Dimension(400, 25));
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        this.getContentPane().add(this.menubar, gbc);

        // Menueeintraege zuordnen
        this.menubar.add(this.mnFile);
        this.mnFile.add(this.mnFileExit);
        this.menubar.add(this.mnInformation);
        this.mnInformation.add(this.mnInformationLicence);
        this.mnInformation.addSeparator();
        this.mnInformation.add(this.mnInformationDoc);
 
        // Hinzufuegen eines Panels fuer die Uebersicht
        this.pnlNavigation = new NavigationPanel(this);
        this.pnlNavigation.setLayout(new GridBagLayout());
        this.pnlNavigation.setBackground(SpecialColor.WHITE);
        this.pnlNavigation.setVisible(true);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 10;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.gridy = 1;
        this.getContentPane().add(this.pnlNavigation, gbc);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 40;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        this.pnlNavigation.setScollPaneNavigation(gbc);
              
        // Hinzufuegen eines Panels fuer Informationen 
        // zu einem ausgewaehlten konkreten Test
        this.pnlTest = new JPanel();
        this.pnlTest.setPreferredSize(new Dimension(600,300));
        this.pnlTest.setLayout(new GridBagLayout());
        this.pnlTest.setBackground(SpecialColor.WHITESTGRAY);
        this.pnlTest.setVisible(true);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 10;
        gbc.weightx = 1.0;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.gridy = 1;
        this.getContentPane().add(this.pnlTest,gbc);

        // Hinzufuegen des SplitPanes, welche eine Platzaufteilung 
        // durch den Nutzer erlaubt 
        // (zwischen Uebersicht und einzelnem Testfall)
        sp = new JSplitPane();
        this.sp.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        this.sp.setLeftComponent(this.pnlNavigation);
        this.sp.setRightComponent(this.pnlTest);
        this.sp.setResizeWeight(0);
        this.sp.setDividerSize(3);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 4;
        gbc.gridy = 1;
        this.getContentPane().add(sp,gbc);

        // Hinzufuegen eines Labels zur Anzeige des aktuell 
        // ausgewaehlten Testfalles
        this.lblOpenedTest = new JLabel();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 6, 15, 6);
        gbc.weighty = 0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        this.pnlTest.add(this.lblOpenedTest,gbc);

        // es wird ein Panel angelegt, welches Informationen 
        // zu gespeicherten Testlaeufen enthaelt
        this.pnlTestergebnisse = new TestergebnissePanel(this);
        // es wird ein Panel angelegt, welches Informationen zum  
        // aktuellen Testlauf (Parameter, Testlaufbutton, Testergebnis) enthaelt
        this.pnlNeueKonfiguration = new NeueKonfigurationPanel(this);
        // es wird ein Panel angelegt, welches Informationen zur 
        // Testfallgenerierung enthaelt
        this.pnlKonfigurationsgenerator = new KonfigurationsgeneratorPanel(this);
     
        // Fuellen des Tabbedpanes mit den drei Modipanel pnlTestergebnisse, 
        // pnlNeueKonfiguration und pnlKonfigurationsgenerator
        this.tpTest = new JTabbedPane();
        this.tpTest.setUI(new TabbedPaneUI());
        this.tpTest.add("Testergebnisse", this.pnlTestergebnisse);
        this.tpTest.add("Neue Konfiguration", this.pnlNeueKonfiguration);
        this.tpTest.add("Konfigurationsgenerator", this.pnlKonfigurationsgenerator);
        this.tpTest.setOpaque(true);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 2;
        this.pnlTest.add(this.tpTest,gbc);
        pack();
        this.setVisible(true);
    }
     
    /**
     * legt die Listener der GUI-Komponenten an 
     */
    private void initListener()
    {
        this.mnFileExit.addActionListener((aEvent) ->
        {
            // Beenden des Programms
            System.exit(0);
        });
        this.mnInformationLicence.addActionListener((aEvent) ->
        {
           	new InfoDialog("Information",
                "&copy; 2020 Philipp Sprengholz, Ursula Oesing, Yannis Herbig <br>"
                 + "This program comes with ABSOLUTELY NO WARRANTY. <br>"
                 + "This is free software, and you are welcome to redistribute it under "
                 + "certain conditions; see http://www.gnu.org/licenses/ for details.");
        });
        this.mnInformationDoc.addActionListener((aEvent) ->
        {
            basicFrameControl.readUserDocumentationURI();
        });
   }

   /**
    * laedt ein neues Projekt (JAR-File) bzw. Testinformationen aus diesem
    */
    private void loadTestCaseInfo()
    {
        // der OpenProjectDialog wird aufgerufen, dieser liefert null zurueck, 
        // wenn keine neuen Testfaelle geoeffnet wurden (bzw. der Nutzer im Dialog 
        // auf Abbrechen geklickt hat)
        OpenProjectDialog openProjectDialog = new OpenProjectDialog(this);
        boolean erg = openProjectDialog.open();
        if(erg)
        {
            this.basicFrameControl.loadTestCaseList();
        }
    }
     
    /**
     * oeffnet den ausgewaehlten Testfall in der Detailansicht
     *
     * @param tci Testfall, welcher in der Detailansicht angezeigt wird
     */
    public void openTestCaseInDetail(TestCaseInfo tci)
    {     
        this.basicFrameModel.setOpenedTestCase(tci);
        // Anzeige des geoeffneten Testfalls und zusammengefasster Testergebnisse
        if(tci != null && tci.getTesttype() == TestCaseInfo.TESTTYPE_JUNIT_5)
        {
            String text = "";
            if(tci.getDisplayName() != null && !tci.getDisplayName().equals(""))
            {
                text = tci.getDisplayName();
            }
            else
            {
                text = tci.getIdentifierName();
            }
            if(tci.isDisabled())
            {
                if(tci.getDisabledMessage() != null && !tci.getDisabledMessage().equals(""))
                {
                    this.lblOpenedTest.setText("<html><font size=+1>"
                        + text + " </font>"
                        // + "<font color=\"#808080\" size=+1>" 
                        // + "(" + tci.getIdentifierName() + ")" + "</font>"
                        + "<font color=\"#808080\" size=+1>" 
                        + " (" + tci.getDisabledMessage() + ") " + "</font>"
                        + " </font><p/><font color=\"#808080\">" + tci.getDesc()+"</font><html>");
                }
                else{
                    this.lblOpenedTest.setText("<html><font size=+1>"
                        + text + " </font>"
                        //+ "<font color=\"#808080\" size=+1>" 
                        // + "(" + tci.getIdentifierName() + ")" + "</font>"
                        + "<font color=\"#808080\" size=+1>" + " (" + "Disabled" + ") " + "</font>"
                        + " </font><p/><font color=\"#808080\">" + tci.getDesc()+"</font><html>");
                }
            }
            else
            {
                this.lblOpenedTest.setText("<html><font size=+1>"
                    + text
                    + " </font><p/><font color=\"#808080\">" + tci.getDesc()+"</font><html>");
            }
        }
        else if(tci != null)
        {
            this.lblOpenedTest.setText("<html><font size=+1>"
                + tci.getIdentifierName()
                + " </font><p/><font color=\"#808080\">" + tci.getDesc()+"</font><html>");
        }
        this.pnlNeueKonfiguration.getPnlParameter().clearUserInterface();
        this.pnlNeueKonfiguration.buildUserInterface(tci);
        this.pnlKonfigurationsgenerator.getPnlParameter().clearUserInterface();
        this.pnlKonfigurationsgenerator.buildParamClassesInterface(tci);
        // Lesen der Liste der Testergebnisse aus der DB
        ArrayList<TestResultInfo> trilist 
            = this.basicFrameControl.readTestResultInfo(tci);
        // Anzeige des Testfalls und seiner Ergebnisse
        if(trilist != null)
        {	
        	this.pnlTestergebnisse.openTestCaseInDetail(tci, trilist);
        	if(trilist.size() == 0)
        	{
        		Thread t = this.runUserDefinedTests(this.pnlNeueKonfiguration.getPnlParameter());
                while(t.isAlive())
                {
                	try 
                	{
						TimeUnit.MILLISECONDS.sleep(1000);
					} 
                	catch (InterruptedException exc) 
                	{
						new InfoDialog("Interner Fehler (Threads).", exc.getMessage());
					}
                }
         	    trilist = this.basicFrameControl.readTestResultInfo(tci);
        		TestResultInfo testResultInfo = trilist.get(0);
          		testResultInfo.setDate(new Timestamp(1000));
 	            this.basicFrameControl.updateTestResult(testResultInfo);
           	}
        }    
    }
  
    /**
     * loescht eine Liste uebergebener Testkonfigurationen aus der Ansicht und der
     * Datenbank
     *
     * @param trilist Liste zu loeschender Testkonfigurationen
     */
    public void deleteTestConfigurations(ArrayList<TestResultInfo> trilist)
    {
        YesNoDialog ynd = new YesNoDialog("Testlauf löschen", 
            "Wenn Sie den Testlauf löschen, ist dieser in Zukunft nicht mehr verfügbar. "
            + "Möchten Sie den Datensatz wirklich entfernen?");
        if (ynd.accepted()) 
        {
        	ArrayList<Integer> deleted = this.basicFrameControl.deleteTestConfigurations(trilist);
            int size = trilist.size();
            for (int i=0; i < size; i++)
            {
            	if(deleted.contains(trilist.get(i).getId()))
            	{	
                    this.pnlTestergebnisse.getTblResultsModel().removeTestResult(trilist.get(i));
            	}    
            }    
            this.pnlTestergebnisse.getTblResultsModel().fireTableDataChanged();
            // Anzeige aktualisieren
            this.pnlNavigation.getTrNavigation().repaint();
            this.pnlTestergebnisse.refreshLblOpenedTestResultSummary(
                this.basicFrameModel.getOpenedTestCase());
            this.pnlTestergebnisse.refreshSuccessBar(
                this.basicFrameModel.getOpenedTestCase());
        }
    }
      
    /**
     * aktualisiert die Oberflaeche nach Ausfuehrung des Testfalls 
     * mit zugehoerigem TestResult.
     *
     * @param tci ausgefuehrter Testfall
     * @param resultInfo TestResultInfo zu dem Testfall
     */
    public void repaintGuiAfterRunTestConfiguration(TestCaseInfo tci, 
    	TestResultInfo resultInfo)
    {
        // Oberflaeche neu darstellen
        this.pnlNavigation.getTrNavigation().repaint();
 
        if (this.basicFrameModel.getOpenedTestCase().equals(tci))
        {
            // Textanzeige
            this.pnlTestergebnisse.refreshLblOpenedTestResultSummary(tci);
            // Erfolgsbalken
            this.pnlTestergebnisse.refreshSuccessBar(tci);
            // Tabelle
            this.pnlTestergebnisse.getTblResultsModel().addTestResult(resultInfo);
            this.pnlTestergebnisse.getTblResultsModel().fireTableDataChanged();
            this.pnlTestergebnisse.getTblResultsModel()
                .highlightTestResult(resultInfo.getId());
        }
    }        


    /**
     * fuehrt alle gespeicherten Konfigurationen einer Liste uebergebener
     * Testfaelle erneut aus
     *
     * @param tcilist Liste von Testfaellen, deren saemtliche Konfigurationen
     *   ausgefuehrt werden sollen
     */
    public void rerunAllTestsOfSeveralTestCases(
    	final ArrayList<TestCaseInfo> tcilist)
    {
        this.basicFrameControl.rerunAllTestsOfSeveralTestCases(tcilist);
    }

    /**
     * fuehrt alle gespeicherten Konfigurationen einer Liste uebergebener
     * Testfaelle, die uebereinstimmend getaggt sind, erneut aus
     *
     * @param tcilist Liste von getaggten Testfaellen, deren saemtliche Konfigurationen
     *   ausgefuehrt werden sollen
     */
    public void rerunAllTaggedTestsOfSeveralTestCases(final ArrayList<TestCaseInfo> tcilist, String tag) {
        this.basicFrameControl.rerunAllTaggedTestsOfSeveralTestCases(tcilist, tag);
    }

    /**
     * fuehrt die vom Generator ermittelten optimalen Testparameterkombinationen
     * aus
     */
    public void runGeneratedCombinations()
    { 
        pnlTestergebnisse.getTblResultsModel().removeAllHighlights();
        // alle im dritten Tab generierten Parameterkombinationen werden in Tests 
        // umgewandelt und zwecks Fortschrittsanzeige in separatem Thread ausgefuehrt
        final int testsToPerform = this.pnlKonfigurationsgenerator
            .getTblCombinationsModel().getRowCount();
        final ArrayList<Combination> combinationList;
        combinationList 
            = pnlKonfigurationsgenerator.getTblCombinationsModel().getCombinations();
        this.basicFrameControl.runGeneratedCombinations(testsToPerform, combinationList);  
        this.tpTest.setSelectedIndex(0);
    }


    /**
     * fuehrt vom Nutzer definierte Einzeltests aus
     *
     * @param p Panel, auf dem die Eingabekomponenten liegen, in denen der Nutzer
     *   Werte fuer die einzelnen Parameter hinterlegt hat
     * @return Thread , in welchem die Methode ausgefuehrt wird    
     */   
    public Thread runUserDefinedTests(ParameterPanel p)
    {  
        this.pnlTestergebnisse.getTblResultsModel().removeAllHighlights();
        final TestCaseInfo testCaseInfo 
            = this.basicFrameModel.getOpenedTestCase();

        final Boolean exceptionExpected;
        JCheckBox chb = (JCheckBox) p.getInputComponentList().get(0);
        exceptionExpected = chb.isSelected();

        final ArrayList<ArrayList<TestParameterInfo>> parameters = new ArrayList<>();
        int numberOfTestsToPerform = 1;

        // Anzahl durchzufuehrender Tests bestimmen
        for (int i = 1; i < p.getInputComponentList().size(); i++)
        {
            if (p.getInputComponentList().get(i).getClass() 
            	== ImageTextField.class)
            {
                ImageTextField tf 
                    = (ImageTextField) p.getInputComponentList().get(i);
                ParameterVerifier pcv 
                    = (ParameterVerifier) tf.getInputVerifier();
                numberOfTestsToPerform 
                    = numberOfTestsToPerform * pcv.getSortedInput().size();
            }
        }

        // Parameterlisten fuer die Anzahl identifizierter Tests anlegen
        for (int i=0; i < numberOfTestsToPerform; i++)
        {
            parameters.add(new ArrayList<TestParameterInfo>());
        }

        int reducer = 1;
        for (int i=1; i < p.getInputComponentList().size(); i++)
        {
            if (p.getInputComponentList().get(i).getClass() == ImageTextField.class)
            {
                ImageTextField tf = (ImageTextField) p.getInputComponentList().get(i);
                ParameterVerifier pcv = (ParameterVerifier) tf.getInputVerifier();
                int numberOfParameterValues = pcv.getSortedInput().size();

                int count = numberOfTestsToPerform/(numberOfParameterValues*reducer);
                int n=0;
                for (int j=0; j < numberOfTestsToPerform; j++)
                {
                    parameters.get(j).add(new TestParameterInfo(tf.getName(), 
                        pcv.getSortedInput().get(n)));
                    if((j+1) % count == 0)
                    {
                        n++;
                    }
                    if(n == numberOfParameterValues)
                    {
                        n = 0;
                    }
                }
                reducer = reducer * numberOfParameterValues;
            }
        }
        // alle Tests werden in neuem Thread ausgefuehrt und dabei ein 
        // Fortschrittsdialog angezeigt
        Thread t = this.basicFrameControl.runUserDefinedTests(numberOfTestsToPerform, 
        	testCaseInfo, parameters, exceptionExpected);
        this.tpTest.setSelectedIndex(0);
        return t;
    }

}