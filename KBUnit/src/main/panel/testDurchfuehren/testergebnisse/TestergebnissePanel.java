package main.panel.testDurchfuehren.testergebnisse;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.TestResultInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.panel.SuccessBar;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.table.TableColumn;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Klasse, welche das Panel fuer die Anzeige der Testergebnisse  
 * zu den Testfaellen zur Verfuegung stellt.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class TestergebnissePanel extends JPanel
{
    
	private static final long serialVersionUID = 1L;
    // Panel oberhalb der Tabelle der Testergebnisse
	private JPanel pnlOpenedTestResultSummary;
	// Fortschrittsbalken
    private SuccessBar successBarOpenedTestResultSummary;
    // Label oberhalb der Tabelle der Testergebnisse
    private JLabel lblOpenedTestResultSummary;
    // GUI - Elemente fuer das Panel zur Anzeige der Testergebnisse
    private JScrollPane scpResults;
    // Tabelle fuer die Testergebnisse
    private JTable tblResults;
    // Model zur Tabelle fuer die testergebnisse
    private ResultTableModel tblResultsModel;
    private ResultCellRenderer tblResultsCellRenderer;
    private HeadCellRenderer tblResultsHeaderRenderer;
    // View des Hauptfensters, welches das Panel zur Anzeige 
    // der Testergebnisse enthaelt
    private BasicFrameView parent;
    
    /**
     * erzeugt ein TestergebnissePanel-Objekt zur Anzeige von testergebnissen
     * @param parent BasicFrameView , View des Hauptfensters, welches das Panel 
     *                                zur Anzeige der Testergebnisse enthaelt
     */
    public TestergebnissePanel(BasicFrameView parent)
    {
        this.parent = parent;
        this.setLayout(new GridBagLayout());
        this.setPreferredSize(new Dimension(400, 200));
        this.initComponents();
        this.initListener();
    }      
    
    // initialisiert die Komponenten des TestergebnissePanels
    private void initComponents()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.ipady = 0;
        gbc.weighty = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.pnlOpenedTestResultSummary = new JPanel();
        this.pnlOpenedTestResultSummary.setLayout(new GridBagLayout());
        this.pnlOpenedTestResultSummary.setBorder(new MatteBorder(0, 0, 1, 0, 
            SpecialColor.LIGHTGRAY));
        this.add(this.pnlOpenedTestResultSummary, gbc);
        
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.weighty = 0;
        gbc.ipady = 15;
        gbc.ipadx = 100;
        gbc.weightx = 0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.successBarOpenedTestResultSummary = new SuccessBar(0, 0
            , 0, 0, 0);
        this.pnlOpenedTestResultSummary
            .add(this.successBarOpenedTestResultSummary, gbc);
 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.weighty = 1;
        gbc.ipady = 15;
        gbc.weightx = 1.0;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.lblOpenedTestResultSummary = new JLabel(
            "<html>Insgesamt definierte Testkonfigurationen: 0"
            + "<br>davon erfolgreich ausgeführt: 0"
            + "<br>davon durch Assumptions abgebrochene Ausführungen: 0"
            + "<br>davon übersprungene Ausführungen: 0" + "</html>");
        this.pnlOpenedTestResultSummary
            .add(this.lblOpenedTestResultSummary, gbc);
  
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        // Tabelle zur Anzeige gespeicherter Testlaeufe & Liste von TableModels zur 
        // Speicherung aller Testlaeufe der geoeffneten Testfaelle
        this.tblResults = new JTable(){
			private static final long serialVersionUID = 1L;};
        this.tblResults.setRowHeight(34);
        this.tblResults.getTableHeader()
            .setPreferredSize(new Dimension(
            this.tblResults.getTableHeader().getWidth(),25));
        this.tblResults.getTableHeader().setReorderingAllowed(false);
        this.tblResultsCellRenderer = new ResultCellRenderer();
        this.tblResultsHeaderRenderer = new HeadCellRenderer();
        this.tblResults.setDefaultRenderer(Object.class, 
        	this.tblResultsCellRenderer);
        this.tblResults.getTableHeader().setDefaultRenderer(
        	this.tblResultsHeaderRenderer);
        // die Liste wird in ein ScrollPane gepackt, damit Scrollbalken angezeigt werden, 
        // wenn der Platz nicht zur Darstellung aller Eintraege ausreicht
        this.scpResults = new JScrollPane(this.tblResults);
        this.scpResults.setBorder(null);
        this.scpResults.setVisible(true);
         this.add(this.scpResults, gbc);
    }        
    
    // initialisiert die Listener des TestergebnissePanels
    private void initListener()
    {
        this.tblResults.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                if (e.getButton() == 3)
                {
                    int mouseOverRow = tblResults.rowAtPoint(e.getPoint());
                    if (mouseOverRow > -1)
                    {
                        if (!tblResults.isRowSelected(mouseOverRow))
                        {
                            ListSelectionModel selectionModel = tblResults.getSelectionModel();
                            selectionModel.setSelectionInterval(mouseOverRow, mouseOverRow);
                        }
                        // alle markierten an Popup uebergeben
                        int[] selectedRows = tblResults.getSelectedRows();
                        ArrayList<TestResultInfo> trilist = new ArrayList<>();

                        for (int i=0; i<selectedRows.length; i++)
                        {
                            trilist.add((TestResultInfo) tblResults.getValueAt(selectedRows[i], 0));
                        }
                        ResultPopupMenu rpm = new ResultPopupMenu(parent, trilist);
                        rpm.show(tblResults, e.getX(), e.getY());
                    }
                }
            }
        });
    }
 
    /**
     * oeffnet den ausgewaehlten Testfall in der Detailansicht
     *
     * @param tci Testfall, welcher in der Detailansicht angezeigt wird
     * @param trilist, ArrayList von TestResultInfo-Objekten, welche die 
     *                 Testergebnisse aus der DB zum Testfall enthaelt
     */
    public void openTestCaseInDetail(TestCaseInfo tci, ArrayList<TestResultInfo> trilist)
    {        
        this.lblOpenedTestResultSummary
            .setText("<html>Insgesamt definierte Testkonfigurationen: "
            + tci.getNumberOfAllTestConfigurations() + "<br>davon erfolgreich ausgeführt: "
            + tci.getNumberOfAllSuccessfulTestConfigurations()
            + "<br>davon fehlgeschlagene Ausführungen: "
            + (tci.getNumberOfAllTestConfigurations()
            - tci.getNumberOfAllSuccessfulTestConfigurations()
            - tci.getNumberOfAllByAssumptionsAbortedTestConfigurations()
            - tci.getNumberOfAllSkippedTestConfigurations())
            + "<br>davon durch Assumptions abgebrochene Ausführungen: "
            + tci.getNumberOfAllByAssumptionsAbortedTestConfigurations()
            + "<br>davon übersprungene Ausführungen: "
            + tci.getNumberOfAllSkippedTestConfigurations() + "</html>");
        this.successBarOpenedTestResultSummary
            .setTotalCapacity(tci.getNumberOfAllTestConfigurations());
        this.successBarOpenedTestResultSummary
            .setNumberOfSuccesses(tci.getNumberOfAllSuccessfulTestConfigurations());
        this.successBarOpenedTestResultSummary
            .setNumberOfAbortionsByAssumptions(
            tci.getNumberOfAllByAssumptionsAbortedTestConfigurations());
        this.successBarOpenedTestResultSummary
            .setNumberOfSkips(tci.getNumberOfAllSkippedTestConfigurations());
        this.successBarOpenedTestResultSummary
            .setNumberOfFailures(tci.getNumberOfAllTestConfigurations()
            - tci.getNumberOfAllSuccessfulTestConfigurations()
            - tci.getNumberOfAllByAssumptionsAbortedTestConfigurations()
            - tci.getNumberOfAllSkippedTestConfigurations());
        this.tblResultsModel = new ResultTableModel(tci);
        
        // Anzeige der gelesenen Testergebnisse 
        for (int j = 0; j < trilist.size(); j++)
        {
            this.getTblResultsModel().addTestResult(trilist.get(j));
        }
   
        this.getTblResults().setModel(this.getTblResultsModel());
        this.getTblResults().getColumnModel().getColumn(0).setHeaderValue("Ergebnis");

        TableColumn col = this.getTblResults().getColumnModel().getColumn(0);
        col.setMinWidth(34);
        col.setPreferredWidth(300);

        int columnCount = this.getTblResultsModel().getColumnCount();

        for(int i=0; i<columnCount-2; i++)
        {
           String columnHeader = tci.getParameters().get(i).getName();
           int index = columnHeader.indexOf("_");
           if(columnHeader.substring(index+1).length() >= 4
        		&& columnHeader.substring(index+1, index+5).equals("exp_"))
           {
                this.getTblResults().getColumnModel().getColumn(i+1)
                    .setHeaderValue("<html><nobr>"
                    + columnHeader.substring(columnHeader.lastIndexOf("_") + 1)
                    + "<sup><font color=\"#AAAAAA\">&thinsp;R</sup></nobr></html>");
           }
           else
           {
                this.getTblResults().getColumnModel().getColumn(i+1)
                    .setHeaderValue("<html><nobr>"
                    + columnHeader.substring(columnHeader.lastIndexOf("_") + 1)
                    + "<sup><font color=\"#AAAAAA\">&thinsp;E</sup></nobr></html>");
           }

           View v = BasicHTML.createHTMLView(new JLabel(), 
               "<html><nobr>"+columnHeader.substring(columnHeader.lastIndexOf("_") + 1)
               + "<sup><font color=\"#AAAAAA\">&thinsp;X</sup></nobr></html>");
           this.getTblResults().getColumnModel().getColumn(i+1)
               .setPreferredWidth((int) v.getPreferredSpan(View.X_AXIS));
        }
        col = this.getTblResults().getColumnModel().getColumn(columnCount-1);
        col.setMinWidth(25);
        col.setPreferredWidth(25);
        col.setMaxWidth(25);
        col.setResizable(false);
        col.setHeaderValue(" E ");
   
        // Vorbereiten der Tabelle zur Anzeige optimaler, vom Programm 
        // bestimmter Parameterkombinationen
        col = this.parent.getPnlKonfigurationsgenerator().prepareTableCombinations(tci);
        col.setMinWidth(25);
        col.setPreferredWidth(25);
        col.setMaxWidth(25);
        col.setResizable(false);
        col.setHeaderValue(" E ");
    }
    
    /**
     * aktualisiert den Fortschrittsbalken
     * @param tci TestCaseInfo , enthaelt den Testfall , zu dem die Ergebnisse angezeigt werden
     */
    public void refreshSuccessBar(TestCaseInfo tci)
    {        
        this.successBarOpenedTestResultSummary.setTotalCapacity(
            tci.getNumberOfAllTestConfigurations());
        this.successBarOpenedTestResultSummary.setNumberOfSuccesses(
            tci.getNumberOfAllSuccessfulTestConfigurations());
        this.successBarOpenedTestResultSummary.setNumberOfAbortionsByAssumptions(
            tci.getNumberOfAllByAssumptionsAbortedTestConfigurations());
        this.successBarOpenedTestResultSummary.setNumberOfSkips(
            tci.getNumberOfAllSkippedTestConfigurations());
        this.successBarOpenedTestResultSummary.setNumberOfFailures(
            tci.getNumberOfAllTestConfigurations()
            - tci.getNumberOfAllSuccessfulTestConfigurations()
            - tci.getNumberOfAllByAssumptionsAbortedTestConfigurations()
            - tci.getNumberOfAllSkippedTestConfigurations());
    }    
    
    /**
     * aktualisiert das Label oberhalb der Tabelle der Testergebnisse
     * @param tci TestCaseInfo , enthaelt den Testfall , zu dem die Ergebnisse angezeigt werden
     */
    public void refreshLblOpenedTestResultSummary(TestCaseInfo tci)
    {        
        this.lblOpenedTestResultSummary
            .setText("<html>Insgesamt definierte Testkonfigurationen: "
            + tci.getNumberOfAllTestConfigurations()
            + "<br>davon erfolgreich ausgeführt: "
            + tci.getNumberOfAllSuccessfulTestConfigurations()
            + "<br>davon fehlgeschlagene Ausführungen: "
            + tci.getNumberOfAllFailedTestConfigurations()
            + "<br>davon durch Assumptions abgebrochene Ausführungen: "
            + tci.getNumberOfAllByAssumptionsAbortedTestConfigurations()
            + "<br>davon übersprungene Ausführungen: "
            + tci.getNumberOfAllSkippedTestConfigurations() + "</html>");
    }
        
    /**
     * gibt die Tabelle fuer die Testergebnisse aus
     * @return JTable , enthaelt die Tabelle fuer die Testergebnisse
     */
    public JTable getTblResults()
    {
        return this.tblResults;
    }
    
    /**
     * gibt das Model zur Tabelle fuer die Testergebnisse aus
     * @return ResultTableModel , enthaelt das Model zur Tabelle fuer die Testergebnisse
     */
    public ResultTableModel getTblResultsModel()
    {
        return this.tblResultsModel;
    }  
  

}