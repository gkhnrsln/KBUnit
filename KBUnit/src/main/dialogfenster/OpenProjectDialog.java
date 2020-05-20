package main.dialogfenster;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.BasicFrameView;
import org.jdom2.JDOMException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * OpenProjectDialog stellt das View eines Dialog zum Oeffnen eines zu testenden Projektes
 * bereit
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz, Ursula Oesing
 * <br>
 */

public class OpenProjectDialog extends TranslucentDialog
{
   	private static final long serialVersionUID = 1L;

	// gibt an, ob neue Testfaelle geladen werden
    private boolean newTestcasesLoaded = false;
  
    // GUI-Komponenten
    private JLabel lblFile;
    private JTextField txtFile;
    private JButton btnFile;
    private JFileChooser fcFile;
    private JLabel lblInfo;
    private JPanel pnlButtons;
    private JButton btnOk;
    private JButton btnAbort;

    // Icons
	private Icon okIcon = new ImageIcon(
	    Toolkit.getDefaultToolkit()
	        .createImage(new File("icons\\accept.png").getAbsolutePath()));
	private Icon failureIcon = new ImageIcon(
	 	Toolkit.getDefaultToolkit()
	 	    .createImage(new File("icons\\exclamation.png").getAbsolutePath()));
	private Icon searchIcon = new ImageIcon(
		Toolkit.getDefaultToolkit()
		 	.createImage(new File("icons\\explore.png").getAbsolutePath()));
	private Icon openIcon = new ImageIcon(
		Toolkit.getDefaultToolkit()
		    .createImage(new File("icons\\open_big.png").getAbsolutePath()));
 
    // die folgenden Variablen speichern die Inhalte der GUI-Komponenten, um diese 
    // bei Abbruch des Dialogs wieder vollstaendig herstellen zu koennen
    // Name des zu oeffnenden JARs
    private String rFilename;        
    // Information ueber die Verfuegbarkeit von Testinformationen
    private String rInfo;              
    // Icon zur Visualisierung der Verfuegbarkeit von Testinformationen
    private ImageIcon rInfoIcon;   
  
    // das Fenster, welches den Dialog aufgerufen hat
    private BasicFrameView parent;
 
  
    /**
     * Konstruktor - erstellt ein neues Fenster
     * @param parent Aufrufer des Fensters
     */
    public OpenProjectDialog(BasicFrameView parent)
    {
        super("Testressourcen öffnen", 450, 200);
        this.parent = parent;
        initComponents();
        initListener();
    }


    /**
     * legt die GUI-Komponenten an 
     */
    private void initComponents()
    {
        // Hinzufuegen der GUI-Komponenten
        this.lblFile = new JLabel();
        this.lblFile.setIcon(this.openIcon);
        this.lblFile.setText(
            "<html> Durch das Laden neuer Testressourcen werden bisher geöffnete "
            + "Testfälle geschlossen.</html>");
        this.lblFile.setIconTextGap(10);
        this.lblFile.setBounds(12,25,426,50);
        this.lblFile.setVisible(true);
        this.add(this.lblFile);

        this.txtFile  = new JTextField("");
        this.txtFile.setEditable(false);
        this.txtFile.setBounds(12,85,395,24);
        this.txtFile.setBorder(BorderFactory
        	.createMatteBorder(1, 1, 1, 1, SpecialColor.LIGHTGRAY));
        this.txtFile.setVisible(true);
        this.add(this.txtFile);

        this.btnFile = new JButton("");
        this.btnFile.setBounds(410,83,28,28);
        this.btnFile.setIcon(this.searchIcon);
        this.btnFile.setVisible(true);
        this.add(this.btnFile);

        this.fcFile = new JFileChooser();
        this.fcFile.removeChoosableFileFilter(
        	this.fcFile.getChoosableFileFilters()[0]);
        this.fcFile.setFileFilter(
        	new FileNameExtensionFilter("Java-Projekt (*.jar)", "JAR"));

        this.lblInfo = new JLabel();
        this.lblInfo.setBounds(12,115,424,30);
        this.lblInfo.setVisible(true);
        this.add(this.lblInfo);

        this.lblInfo.setIcon(null);
        this.lblInfo.setText("");
       
        this.pnlButtons = new JPanel();
        this.pnlButtons.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, SpecialColor.LIGHTGRAY));
        this.pnlButtons.setBounds(1,150,449,50);
        this.pnlButtons.setBackground(SpecialColor.WHITE);
        this.pnlButtons.setLayout(null);

        this.pnlButtons.setVisible(true);
        this.add(this.pnlButtons);

        this.btnOk = new JButton("OK");
        this.btnOk.setBounds(232, 11, 100, 28);
        this.btnOk.setEnabled(false);
        this.btnOk.setVisible(true);
        this.pnlButtons.add(this.btnOk);

        this.btnAbort = new JButton("Abbrechen");
        this.btnAbort.setBounds(337, 11, 100, 28);
        this.btnAbort.setVisible(true);
        this.pnlButtons.add(this.btnAbort);
        this.validate();
    }
    
    /**
     * bestimmt die Aktionen einzelner Listener
     */
    private void initListener()
    {
        this.btnFile.addActionListener(aEvent ->
        {
            if(fcFile.showOpenDialog(OpenProjectDialog.this) 
            	== JFileChooser.APPROVE_OPTION)
            {
                // wird auf den Search-Button gedrueckt, so kann ein Projekt (JAR-File) 
                // gesucht und geoeffnet werden, die ausgewaehlte Datei wird auf 
                // Testinformationen untersucht
                String filename = fcFile.getSelectedFile().getAbsolutePath();
                txtFile.setText(filename);
                getXMLInformation(filename);
            }
        });
        
        this.btnOk.addActionListener(aEvent ->
        {
	        // wird auf OK geklickt, dann wird das JAR-File geladen und der Dialog geschlossen
	        if (loadJARFile())
	        {
	            newTestcasesLoaded = true;
	            OpenProjectDialog.this.setVisible(false);
	        }
	        else
	        {
	            new InfoDialog("Öffnen fehlgeschlagen", 
	                "Das Projekt kann aufgrund eines Fehlers nicht geöffnet werden. "
	                + "Bitte wählen Sie eine andere Datei.");
	        }
        });
       
        this.btnAbort.addActionListener(aEvent ->
        {
            // wird auf Abbrechen geklickt, so werden die Eintraege in den 
            // GUI-Komponenten zurueckgesetzt und das Fenster geschlossen
            newTestcasesLoaded = false;
            rollbackSettings();
            OpenProjectDialog.this.setVisible(false);
        });      
    }


    /**
     * zeigt den Dialog an und liefert Informationen zum Laden von Testfaellen zurueck
     *
     * @return true, falls Testfaelle geladen werden sollen, false sonst
     */
    public boolean open()
    {
        boolean erg = false;
        // die Eintraege aller GUI-Komponenten werden gesichert
        this.backupSettings();
        // der Dialog wird angezeigt
        this.setVisible(true);
        // wurden neue Testfaelle geoeffnet, so werden diese geliefert
        if (this.newTestcasesLoaded)
        {   
            erg = true;
        }
        return erg;
    }


    /**
     * sichert die Eintraege aller GUI-Komponenten und ermoeglicht so ein Rollback
     * der Eintraege im Falle eines Abbruchs
     */
    private void backupSettings()
    {
        this.rFilename = this.txtFile.getText();
        this.rInfo = this.lblInfo.getText();
        this.rInfoIcon = (ImageIcon) this.lblInfo.getIcon();
    }


    /**
     * stellt die Eintraege aller GUI-Komponenten (im Falle eines Abbruchs) wieder her
     */
    private void rollbackSettings()
    {
        this.txtFile.setText(this.rFilename);
        this.fcFile.setCurrentDirectory(new File(this.rFilename));
        this.lblInfo.setText(this.rInfo);
        this.lblInfo.setIcon(this.rInfoIcon);
        if (!this.rFilename.equals(""))
        {
            this.getXMLInformation(this.rFilename);
        }
        this.btnOk.setEnabled(true);
    }


    /**
     * fuegt die im Dateiauswahldialog geoeffnete JAR-Datei dem Classpath hinzu
     *
     * @return gibt an, ob die Inhalte dem Classpath hinzugefuegt werden konnten
     */
    private boolean loadJARFile()
    {
        boolean jarWasAddedToClassPath = false;
        File jarFile = this.fcFile.getSelectedFile();
        jarWasAddedToClassPath 
            = this.parent.getBasicFrameControl().loadJARFile(jarFile); 
        return jarWasAddedToClassPath;
    }
    
     /**
     * laedt saemtliche Testinformationen aus der im JAR-File enthaltenen
     * XML-Datei in das xml-Objekt und ueberprueft, ob Testinformationen
     * ueberhaupt verfuegbar sind
     *
     * @param dateiname Pfad des zu oeffnenden Projekts (JAR-Files)
     */
    private void getXMLInformation(String dateiname)
    {
        try
        {    
            // Die GUI-Komponenten werden aktiviert.
            this.parent.getBasicFrameControl().loadTestCaseInfo(dateiname); 
            // im Dialog wird angezeigt, ob XML-Informationen gefunden wurden, 
            this.lblInfo.setText("<html>Es wurden Testinformationen gefunden.</html>");
            this.lblInfo.setIcon(okIcon);
            this.btnOk.setEnabled(true);
        }
        catch(JDOMException | IOException exc)
        {
            this.lblInfo.setText("<html>Das ausgewählte Projekt " 
                + "enthält keine Testinformationen. "
                + "Es kann nicht geöffnet werden.</html>");
            this.lblInfo.setIcon(failureIcon);
            this.btnOk.setEnabled(false);
        }    
    }
}
