package main.panel.testDurchfuehren.konfigurationsgenerator;

import hilfe.guiHilfe.ImageTextField;
import hilfe.guiHilfe.SpecialColor;
import main.dialogfenster.InfoDialog;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.TestParameterInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.panel.ParameterPanel;
import main.panel.testDurchfuehren.testergebnisse.HeadCellRenderer;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.table.TableColumn;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

/**
 * Klasse, welche das Panel fuer die Anzeige eines Testfalls  
 * und die Eingabe einer neuen Konfiguration zur Verfuegung stellt.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class KonfigurationsgeneratorPanel extends JPanel
{
    
  	private static final long serialVersionUID = 1L;
  	// GUI - Elemente
	private JPanel pnlParameterInput;
    private JPanel pnlParameterInputTitle;
    private JLabel lblParameterInputTitle;
    private JScrollPane scplParameterInput;
    private ParameterPanel pnlParameter;
    private JPanel pnlParameterInputButtons;
    private JButton btnGenerateTest;
    private JPanel pnlCombinations;
    private JPanel pnlCombinationsTitle;
    private JLabel lblCombinationsTitle;
    private JTable tblCombinations;
    public  ParamTableModel tblCombinationsModel;
    private ParamCellRenderer tblCombinationsCellRenderer;
    private HeadCellRenderer tblCombinationsHeaderRenderer;
    private JScrollPane scpCombinations;
    private JPanel pnlCombinationsButtons;
    private JButton btnBackToGenerator;
    private JButton btnRunGeneratedTests;
     
    // Fenster, welches das Panel enthaelt
    private BasicFrameView parent;
    
     
    /**
     * gibt das Model zum KonfigurationsgeneratorPanel aus
     * @return ParamTableModel , as Model zum KonfigurationsgeneratorPanel
     */
    public ParamTableModel getTblCombinationsModel()
    {
        return this.tblCombinationsModel;
    }    
    
     
    // Instanz fuer die Zufallszahlenerzeugung 
    // (wird bei zufaelliger Parameterkombination benoetigt)
    Random randomizer = new Random();
    
    /**
     * Konstruktor fuer das Panel zum Generieren von Testkonfigurationen
     * @param parent View des Hauptfensters 
     */
    public KonfigurationsgeneratorPanel(BasicFrameView parent)
    {
        this.parent = parent;
        this.setBackground(SpecialColor.WHITE);
        this.setBorder(null);
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 300));
        this.initComponents();
        this.initListener();
    }        
    
    // initialisiert die Komponenten fuer das KonfigurationsgeneratorPanel
    private void initComponents()
    {
        // Panel fuer die Eingabe der Parameter
        this.pnlParameterInput = new JPanel();
        this.pnlParameterInput.setBackground(SpecialColor.WHITE);
        this.pnlParameterInput.setBorder(null);
        this.pnlParameterInput.setLayout(new GridBagLayout());
        this.pnlParameterInput.setPreferredSize(new Dimension(400, 300));
        this.pnlParameterInput.setVisible(true);
        
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        this.add(this.pnlParameterInput,gbc);

        this.pnlParameterInputTitle = new JPanel();
        this.pnlParameterInputTitle.setBackground(SpecialColor.WHITE);
        this.pnlParameterInputTitle.setBorder(
        	new MatteBorder(0, 0, 1, 0, SpecialColor.LIGHTGRAY));
        this.pnlParameterInputTitle.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.pnlParameterInput.add(this.pnlParameterInputTitle,gbc);

        this.lblParameterInputTitle = new JLabel();
        this.lblParameterInputTitle.setText("<html><font color=\"#777777\">"
            + "Sobald für alle Eingangsparameter gültige Werte und/oder Wertintervalle "
            + "definiert wurden, ermittelt KBUnit die für eine hohe Testabdeckung erforderlichen "
            + "Parameterkombinationen und zeigt die generierten Konfigurationen zur Eingabe "
            + "von Ergebniswerten an.</font></html>");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 6, 10, 6);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.pnlParameterInputTitle.add(this.lblParameterInputTitle,gbc);

        this.pnlParameter = new ParameterPanel();
        this.pnlParameter.setBackground(SpecialColor.WHITE);
        this.pnlParameter.setLayout(new GridBagLayout());

        // das Panel wird einem ScrollPane zugeordnet, damit spaeter 
        // (falls der Platz fuer die Parametereingabe nicht ausreicht) 
        // Scrollbalken angezeigt werden
        this.scplParameterInput = new JScrollPane(this.pnlParameter);
        this.scplParameterInput.setBorder(null);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 5;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 1;
        this.pnlParameterInput.add(this.scplParameterInput,gbc);

        // es wird ein Panel fuer Buttons angelegt
        this.pnlParameterInputButtons = new JPanel();
        this.pnlParameterInputButtons.setLayout(null);
        this.pnlParameterInputButtons
            .setBorder(new MatteBorder(1, 0, 0, 0, SpecialColor.LIGHTGRAY));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 50;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        this.pnlParameterInput.add(this.pnlParameterInputButtons, gbc);

        // Button zum Starten des Tests
        this.btnGenerateTest = new JButton("Konfigurationen generieren");
        this.btnGenerateTest.setVisible(true);
        this.btnGenerateTest.setBounds(5, 11, 200, 28);
        this.pnlParameterInputButtons.add(this.btnGenerateTest);

        // Panel fuer die Anzeige der generierten Testfallkombinationen
        this.pnlCombinations = new JPanel();
        this.pnlCombinations.setBackground(SpecialColor.WHITE);
        this.pnlCombinations.setBorder(null);
        this.pnlCombinations.setLayout(new GridBagLayout());
        this.pnlCombinations.setPreferredSize(new Dimension(400, 300));
        this.pnlCombinations.setVisible(false);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.gridy = 0;
        this.add(this.pnlCombinations,gbc);

        this.pnlCombinationsTitle = new JPanel();
        this.pnlCombinationsTitle.setBackground(SpecialColor.WHITE);
        this.pnlCombinationsTitle
            .setBorder(new MatteBorder(0, 0, 1, 0, SpecialColor.LIGHTGRAY));
        this.pnlCombinationsTitle.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.pnlCombinations.add(this.pnlCombinationsTitle,gbc);

        this.lblCombinationsTitle = new JLabel();
        this.lblCombinationsTitle
            .setText("<html><font color=\"#777777\">"
            + "Die Tabelle zeigt die von KBUnit ermittelten optimalen Parameterkombinationen. "
            + "Jede Zeile repräsentiert eine neue Konfiguration, deren Ergebnisparameter vor "
            + "Durchführung des Tests geändert werden können.</font></html>");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 6, 10, 6);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.pnlCombinationsTitle.add(this.lblCombinationsTitle,gbc);

        this.tblCombinations = new JTable(){

		private static final long serialVersionUID = 1L;};
        this.tblCombinations.setRowHeight(25);
        this.tblCombinations.setRowSelectionAllowed(true);
        this.tblCombinations.setSelectionMode(
        	ListSelectionModel.SINGLE_SELECTION);
        this.tblCombinations.getTableHeader()
            .setPreferredSize(new Dimension(
            this.tblCombinations.getTableHeader().getWidth(),25));
        this.tblCombinations.getTableHeader().setReorderingAllowed(false);
        this.tblCombinationsCellRenderer = new ParamCellRenderer();
        this.tblCombinations.setDefaultRenderer(Object.class, 
        	this.tblCombinationsCellRenderer);
        this.tblCombinations.setDefaultEditor(Object.class, new ParamCellEditor());
        this.tblCombinationsHeaderRenderer = new HeadCellRenderer();
        this.tblCombinations.getTableHeader()
            .setDefaultRenderer(this.tblCombinationsHeaderRenderer);
       
        // die Tabelle wird einem ScrollPane zugeordnet
        this.scpCombinations = new JScrollPane(this.tblCombinations);
        this.scpCombinations.setBorder(null);
        this.scpCombinations.setVisible(true);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.pnlCombinations.add(this.scpCombinations, gbc);

        // es wird ein Panel fuer Buttons angelegt
        this.pnlCombinationsButtons = new JPanel();
        this.pnlCombinationsButtons.setLayout(null);
        this.pnlCombinationsButtons
            .setBorder(new MatteBorder(1, 0, 0, 0, SpecialColor.LIGHTGRAY));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 50;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        this.pnlCombinations.add(this.pnlCombinationsButtons, gbc);

        // Button um zum Generator zurueckzukehren
        this.btnBackToGenerator = new JButton("Abbrechen");
        this.btnBackToGenerator.setVisible(true);
        this.btnBackToGenerator.setBounds(5, 11, 100, 28);
        this.pnlCombinationsButtons.add(this.btnBackToGenerator);

        // Button zum Starten der generierten Konfigurationen
        this.btnRunGeneratedTests = new JButton("Konfiguration(en) testen");
        this.btnRunGeneratedTests.setVisible(true);
        this.btnRunGeneratedTests.setBounds(110, 11, 200, 28);
        this.pnlCombinationsButtons.add(this.btnRunGeneratedTests);
    }        
    
    // initialisiert die Listener fuer das KonfigurationsgeneratorPanel
    private void initListener()
    {
        this.btnGenerateTest.addActionListener((aEvent) ->
        {
            boolean abortAction = false;

            // Liste mit Parametergrenzwerten, statt eines festen Wertes wird an 
            // jeden Parameter eine Liste mit Grenzwerten angehaengt, die aus den 
            // Eingaben des Nutzers hervorgeht
            int numberOfComponents = pnlParameter.getInputComponentList().size();
            for (int i=0; i<numberOfComponents; i++)
            {
                JTextField tf = (JTextField) pnlParameter.getInputComponentList().get(i);
                ParamIntervalVerifier pcv = (ParamIntervalVerifier) tf.getInputVerifier();
                if (pcv.getLastVerificationResult() == false)
                {
                    abortAction = true;
                    break;
                }
            }
            if (abortAction)
            {
                new InfoDialog("Fehler","Eingabe nicht konform");
            }
            else
            {
                calculateAndShowCombinations();
                switchTab3ViewFromGeneratorToCombinations();
            }
        });
        
        this.tblCombinations.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (e.getButton() == 3)
                {
                    int mouseOverRow = tblCombinations.rowAtPoint(e.getPoint());
                    if (mouseOverRow > -1)
                    {
                        if (!tblCombinations.isRowSelected(mouseOverRow))
                        {
                            ListSelectionModel selectionModel 
                                = tblCombinations.getSelectionModel();
                            selectionModel.setSelectionInterval(mouseOverRow, mouseOverRow);
                        }
                        
                        // Nummer der markierten Zeile gemeinsam mit Model 
                        // an Popup uebergeben
                        int selectedRow = tblCombinations.getSelectedRow();
             
                        ParamPopupMenu ppm 
                            = new ParamPopupMenu(KonfigurationsgeneratorPanel.this, 
                            selectedRow);
                        ppm.show(tblCombinations, e.getX(), e.getY());
                    }
                }
            }
        });
        
        this.btnBackToGenerator.addActionListener((aEvent) ->
        {
            switchTab3ViewFromCombinationsToGenerator();
        });
        
        this.btnRunGeneratedTests.addActionListener((aEvent) ->
        {
            parent.runGeneratedCombinations();
        });
    }
    
    /**
     * schaltet von der Kombinations- in die Genratorsansicht um
     */
    public void switchTab3ViewFromGeneratorToCombinations()
    {
        this.pnlParameterInput.setVisible(false);
        this.pnlCombinations.setVisible(true);
    }
    
    /**
     * schaltet von der Generator- in die Kombinationsansicht um
     */
    public void switchTab3ViewFromCombinationsToGenerator()
    {
        this.pnlCombinations.setVisible(false);
        this.pnlParameterInput.setVisible(true);
    }
    
   /**
    * gibt das Panel zur Eingabe der Parameter aus.
    * @return ParameterPanel , das Model zum KonfigurationsgeneratorPanel
    */
    public ParameterPanel getPnlParameter()
    {
        return this.pnlParameter;
    }        
    
    
     /**
     * erstellt die fuer den ausgewaehlten Testfall notwendigen Eingabefelder auf
     * einem speziellen ParameterPanel, in welchem diese Eingabefelder zur
     * spaeteren Loeschung gelistet sind (fuer Konfigurationsgenerator, also OHNE
     * Ergebnisparameter / Feld fuer erwartete Exceptions)
     *
     * @param tci Testfall, fuer dessen Parameter Eingabefelder erzeugt werden
     *
     */
    public void buildParamClassesInterface(TestCaseInfo tci)
    {
        // Infobox und Buttonfeld des Konfigurationsgenerators werden sichtbar 
        // gemacht (koennten unsichtbar gemacht worden sein, falls ein Testfall 
        // keine Eingangsparameter besitzt)
        this.pnlParameterInputTitle.setVisible(true);
        this.pnlParameterInputButtons.setVisible(true);
        try
        {
            ArrayList<Field> parameters = tci.getParameters();
            GridBagConstraints gbc = new GridBagConstraints();

            // gibt die Anzahl der Testparameter abzgl. 1 an
            int paramCounter = 0;

            // Hilfsobjekt (muss im Folgenden nicht gesetzt werden, 
            // da alle Testparameter static - Klassenvariablen - sind)
            Object cl = null;

            int numberOfNonResultParameters = 0;

            // die in der parameters-Liste enthaltenen Parameter werden der 
            // Reihe nach dem entsprechenden Container hinzugefuegt
            String tooltip;
            for(int i = 0; i < parameters.size(); i++)
            {
                // speichert den kompletten Parameternamen
                String longParameterName = parameters.get(i).getName();

                // Erwartungsparameter werden nicht fuer die Intervallangabe angezeigt
                int index = longParameterName.indexOf("_");
                boolean isNoExpectedResultParameter = true;
                if(longParameterName.substring(index+1).length() >= 4
                	&& longParameterName.substring(index+1, index+5).equals("exp_"))
                {
                   isNoExpectedResultParameter = false;
                }
                else
                {
                    numberOfNonResultParameters++;
                }
                    // Parameterzaehler wird erhoeht
                    paramCounter++;

                    // aus dem kompletten Parameternamen wird ein nutzerfreundlicher 
                    // Name abgeleitet, indem der Name des Testfalls aus dem 
                    // Parameternamen entfernt wird
                    String parameterName 
                        = longParameterName.substring(longParameterName.lastIndexOf("_")+1);

                    // der gekuerzte Parametername wird mittels eines Labels angezeigt und 
                    // eine Parameterbeschreibung als Tooltip hinterlegt
                    JLabel lbl = new JLabel(parameterName);
                    lbl.setVerticalAlignment(SwingConstants.TOP);
                    if (isNoExpectedResultParameter)
                    {
                        lbl.setText("<html>" + parameterName 
                            + "<sup><font color=\"#AAAAAA\">&thinsp;E</sup></font><html>");
                    }
                    else
                    {
                        lbl.setText("<html>" + parameterName
                            + "<sup><font color=\"#AAAAAA\">&thinsp;R</sup></font><html>");
                    }
                 
                    lbl.setName(longParameterName);
                    tooltip 
                        = parent.getBasicFrameControl().getBasicFrameXmlModel()
                        .getParameterInfo(tci.getPath(), longParameterName);
                    lbl.setToolTipText(tooltip);
                    lbl.setVisible(isNoExpectedResultParameter);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.ipady = 6;
                    gbc.weighty = 0;
                    gbc.weightx = 0;
                    gbc.insets = new Insets(15, 6, 0, 11);
                    gbc.ipadx = 20;
                    gbc.gridx = 0;
                    gbc.gridwidth = 1;
                    gbc.gridheight = 1;
                    gbc.gridy = paramCounter-1;
                    this.pnlParameter.add(lbl,gbc);

                    // das Label wird in einer Liste gespeichert, sodass es spaeter 
                    // wieder entfernt werden kann
                    this.pnlParameter.getLabelList().add(lbl);

                    // die GridBagConstraints werden fuer die eigentliche Eingabekomponente 
                    // angepasst
                    gbc.weighty = 0;
                    gbc.weightx = 1.0;
                    gbc.ipadx = 200;
                    gbc.gridx = 1;
                    gbc.gridy = paramCounter-1;

                    parameters.get(i).setAccessible(true);

                    // falls der Parameter vom Typ INTEGER ist
                    if (parameters.get(i).getType() == int.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getInt(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Integer.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf, gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ BYTE ist
                    else if(parameters.get(i).getType() == byte.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getByte(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Byte.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ SHORT ist
                    else if(parameters.get(i).getType() == short.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getShort(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Short.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ LONG ist
                    else if(parameters.get(i).getType() == long.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getLong(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Long.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ FLOAT ist
                    else if(parameters.get(i).getType() == float.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getFloat(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Float.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ DOUBLE ist
                    else if(parameters.get(i).getType() == double.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getDouble(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(Double.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ BOOLEAN ist
                    else if(parameters.get(i).getType() == boolean.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getBoolean(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(boolean.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ CHAR ist
                    else if(parameters.get(i).getType() == char.class)
                    {
                        ImageTextField tf = new ImageTextField("" 
                            + parameters.get(i).getChar(cl));
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(char.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter vom Typ STRING ist
                    else if(parameters.get(i).getType() == java.lang.String.class)
                    {
                    	ImageTextField tf;
                    	if(parameters.get(i).get(cl) == null)
                    	{
                    		tf = new ImageTextField("null");
                    	}
                    	else if(parameters.get(i).get(cl).toString().length() == 0)
                    	{
                    		tf = new ImageTextField("\"\"");
                    	}
                    	else
                    	{	
                            tf = new ImageTextField("" + parameters.get(i).get(cl).toString());
                    	}
                        tf.setName(parameters.get(i).getName());
                        tf.setInputVerifier(new ParamIntervalVerifier(java.lang.String.class));
                        tf.getInputVerifier().verify(tf);
                        tf.setToolTipText(tooltip);
                        tf.setVisible(isNoExpectedResultParameter);
                        this.pnlParameter.add(tf,gbc);
                        this.pnlParameter.getInputComponentList().add(tf);
                        this.pnlParameter.getInputComponentList().get(paramCounter-1)
                            .setName(longParameterName);
                    }
                    // falls der Parameter von keinem der unterstuetzten Datentypen ist, 
                    // wird eine entsprechende Meldung ausgegeben und die Eingabe verweigert
                    else
                    {
                        JLabel lb = new JLabel("Eingabe nicht unterstützt");
                        lb.setToolTipText(tooltip);
                        lb.setVisible(true);
                        this.pnlParameter.add(lb, gbc);
                        this.pnlParameter.getLabelList().add(lb);
                    }
            }
            if (numberOfNonResultParameters == 0)
            {
                JLabel lbl = new JLabel("<html>Der Testfall besitzt keine Eingangsparameter. "
                    + "Eine automatische Generierung von Testkonfigurationen ist nicht möglich.<html>");
                this.pnlParameterInputTitle.setVisible(false);
                this.pnlParameterInputButtons.setVisible(false);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 5;
                gbc.weighty = 0;
                gbc.weightx = 1.0;
                gbc.insets = new Insets(15, 7, 0, 11);
                gbc.ipadx = 100;
                gbc.gridx = 0;
                gbc.gridwidth = 2;
                gbc.gridheight = 1;
                gbc.gridy = 0;
                this.pnlParameter.add(lbl,gbc);
                lbl.setVisible(true);
                this.pnlParameter.getLabelList().add(lbl);
                paramCounter++;
            }
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.ipadx = 0;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.gridy = paramCounter;
            JPanel placeholder = new JPanel();
            placeholder.setBackground(SpecialColor.WHITE);
            placeholder.setVisible(true);
            this.pnlParameter.add(placeholder,gbc);

            this.pnlParameter.validate();
            this.pnlParameter.repaint();

            this.pnlParameter.setPreferredSize(new Dimension(400, 40*(paramCounter+1)));
        }
        catch (IllegalAccessException iae)
        {
            new InfoDialog("Fehler beim Laden der Testparameter",
                "Die Parameter des ausgewählten Testfalles konnten nicht ordnungsgemäß "
                + "geladen werden. Unter Umständen ist das zu testende Projekt fehlerhaft.");
        }
    }

    /**
     * erstellt die Tabelle von Kombinationen von Testfaellen 
     * @param tci TestCaseInfo , zu welchem die Kombinationen erstellt werden sollen
     * @return TableColumn Tabelle fuer die Kombinationen 
     */
    public TableColumn prepareTableCombinations(TestCaseInfo tci)
    {        
        this.tblCombinationsModel = new ParamTableModel(tci);
        this.tblCombinations.setModel(this.tblCombinationsModel);

        for(int i=0; i<this.tblCombinationsModel.getColumnCount()-1; i++)
        {
           String columnHeader = tci.getParameters().get(i).getName();
           int index = columnHeader.indexOf("_");
           if(columnHeader.substring(index+1).length() >= 4
        		&& columnHeader.substring(index+1, index+5).equals("exp_"))
           {
                this.tblCombinations.getColumnModel().getColumn(i).setHeaderValue("<html><nobr>"
                    + columnHeader.substring(columnHeader.lastIndexOf("_")+1)
                    + "<sup><font color=\"#AAAAAA\">&thinsp;R</sup></nobr></html>");
           }
           else
           {
                this.tblCombinations.getColumnModel().getColumn(i).setHeaderValue("<html><nobr>"
                    + columnHeader.substring(columnHeader.lastIndexOf("_") + 1)
                    + "<sup><font color=\"#AAAAAA\">&thinsp;E</sup></nobr></html>");
           }

           View v = BasicHTML.createHTMLView(new JLabel(), "<html><nobr>" 
               + columnHeader.substring(columnHeader.lastIndexOf("_") + 1)
               + "<sup><font color=\"#AAAAAA\">&thinsp;X</sup></nobr></html>");
           this.tblCombinations.getColumnModel().getColumn(i)
               .setPreferredWidth((int) v.getPreferredSpan(View.X_AXIS));
        }
        
        TableColumn col;
        col = this.tblCombinations.getColumnModel()
            .getColumn(this.tblCombinationsModel.getColumnCount()-1);
 
        switchTab3ViewFromCombinationsToGenerator();
        return col;
    }   
    
    
    /*
     * ermittelt aus den im Generatorbereich eingegebenen Parameterwerten und
     * Intervallen optimale Parameterkombinationen und zeigt diese an
     */
    private void calculateAndShowCombinations()
    {
        this.tblCombinationsModel.removeAllCombinations();
        Boolean exceptionExpected;
        ArrayList<TestParameterInfo> tpilist;

        // jeder invalide Parameter wird mit Parametern kombiniert, die valide 
        // Werte enthalten (gibt es keine validen Werte fuer die anderen Parameter, 
        // so werden Grenzfallwerte genommen)
        exceptionExpected = true;
        for (int i = 0; i < this.pnlParameter.getInputComponentList().size(); i++)
        {
            ImageTextField tf = (ImageTextField) this.pnlParameter
            	.getInputComponentList().get(i);
            ParamIntervalVerifier piv = (ParamIntervalVerifier) tf.getInputVerifier();
            ArrayList<Object> invalidList = piv.getInvalidParameterValues();
            for (int j = 0; j < invalidList.size(); j++)
            {
                tpilist = new ArrayList<>();

                for (int k = 0; k < this.pnlParameter.getInputComponentList().size(); k++)
                {
                    if (k == i)
                    {
                        tpilist.add(new TestParameterInfo(tf.getName(), invalidList.get(j), 
                            TestParameterInfo.PARAMETER_OUT_OF_BOUNDS));
                    }
                    else //if (k != i)
                    {
                        ImageTextField tfNeu 
                            = (ImageTextField) this.pnlParameter.getInputComponentList().get(k);

                        String paramName = tfNeu.getName();
                        int paramType;
                        int index = paramName.indexOf("_");
                        if(paramName.substring(index + 1, index + 5).equals("exp_"))
                        {
                           paramType = TestParameterInfo.RESULTPARAMETER;
                        }
                        else
                        {
                           paramType = TestParameterInfo.PARAMETER_IN_BOUNDS;
                        }

                        ParamIntervalVerifier pivNeu 
                            = (ParamIntervalVerifier) tfNeu.getInputVerifier();
                        ArrayList<Object> validList = pivNeu.getValidParameterValues();
                        if (!validList.isEmpty())
                        {
                            tpilist.add(new TestParameterInfo(paramName, 
                                validList.get(randomizer.nextInt(validList.size())), paramType));
                        }
                    }
                }
                this.tblCombinationsModel.addCombination(tpilist, exceptionExpected);
            }
        }
        // jeder Grenzparameter wird mit Parametern kombiniert, die valide Werte enthalten
        exceptionExpected = false;
        for (int i = 0; i < this.pnlParameter.getInputComponentList().size(); i++)
        {
            ImageTextField tf 
                = (ImageTextField) this.pnlParameter.getInputComponentList().get(i);
            ParamIntervalVerifier piv = (ParamIntervalVerifier) tf.getInputVerifier();
            ArrayList<Object> limitList = piv.getLimitParameterValues();
            for (int j = 0; j < limitList.size(); j++)
            {
                tpilist = new ArrayList<TestParameterInfo>();

                for (int k = 0; k < this.pnlParameter.getInputComponentList().size(); k++)
                {
                    if (k == i)
                    {
                        tpilist.add(new TestParameterInfo(tf.getName(), limitList.get(j), 
                            TestParameterInfo.PARAMETER_IS_BOUND));
                    }
                    else //if(k != i)
                    {
                        ImageTextField tfNeu 
                            = (ImageTextField) this.pnlParameter.getInputComponentList().get(k);

                        String paramName = tfNeu.getName();
                        int paramType;
                        int index = paramName.indexOf("_");
                        if(paramName.substring(index+1, index+5).equals("exp_"))
                        {
                           paramType = TestParameterInfo.RESULTPARAMETER;
                        } 
                        else
                        {
                           paramType = TestParameterInfo.PARAMETER_IN_BOUNDS;
                        }

                        ParamIntervalVerifier pivNeu 
                            = (ParamIntervalVerifier) tfNeu.getInputVerifier();
                        ArrayList<Object> validList = pivNeu.getValidParameterValues();
                        if (!validList.isEmpty())
                        {
                            tpilist.add(new TestParameterInfo(paramName, 
                                validList.get(randomizer.nextInt(validList.size())), paramType));
                        }
                    }
                }
                this.tblCombinationsModel.addCombination(tpilist, exceptionExpected);
            }
        }

        // valide Parameter werden untereinander in allen moeglichen Kombinationen 
        // kombiniert (Erklaerung siehe runUserDefinedTests - funktioniert genauso)
        exceptionExpected = false;

        final ArrayList<ArrayList<TestParameterInfo>> parameters = new ArrayList<>();
        int numberOfTestsToPerform = 1;

        for (int i = 0; i < this.pnlParameter.getInputComponentList().size(); i++)
        {
            if (this.pnlParameter.getInputComponentList().get(i).getClass() 
                == ImageTextField.class)
            {
                ImageTextField tf 
                    = (ImageTextField) this.pnlParameter.getInputComponentList().get(i);
                ParamIntervalVerifier pcv = (ParamIntervalVerifier) tf.getInputVerifier();
                numberOfTestsToPerform 
                    = numberOfTestsToPerform * pcv.getValidParameterValues().size();
            }
        }

        for (int i=0; i<numberOfTestsToPerform; i++)
        {
            parameters.add(new ArrayList<TestParameterInfo>());
        }

        int reducer = 1;
        for (int i=0; i < this.pnlParameter.getInputComponentList().size(); i++)
        {
            if (this.pnlParameter.getInputComponentList().get(i).getClass() 
                == ImageTextField.class)
            {
                ImageTextField tf 
                    = (ImageTextField) this.pnlParameter.getInputComponentList().get(i);

                String paramName = tf.getName();
                int paramType;
                int index = paramName.indexOf("_");
                if(paramName.substring(index+1, index+5).equals("exp_"))
                {
                   paramType = TestParameterInfo.RESULTPARAMETER;
                }
                else
                {
                   paramType = TestParameterInfo.PARAMETER_IN_BOUNDS;
                }

                ParamIntervalVerifier pcv = (ParamIntervalVerifier) tf.getInputVerifier();
                int numberOfParameterValues = pcv.getValidParameterValues().size();

                int count = numberOfTestsToPerform/(numberOfParameterValues*reducer);
                int n = 0;
                for (int j = 0; j < numberOfTestsToPerform; j++)
                {
                    parameters.get(j)
                        .add(new TestParameterInfo(paramName, 
                        pcv.getValidParameterValues().get(n), paramType));
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
        for (int i = 0; i < numberOfTestsToPerform; i++)
        {
            this.tblCombinationsModel
                .addCombination(parameters.get(i), exceptionExpected);
        }
    }

}
