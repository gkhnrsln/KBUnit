package main.dialogfenster;

import hilfe.guiHilfe.SpecialColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;


/**
 * YesNoDialog stellt ein Auswahlfenster dar, mit dem der Nutzer eine Aktion
 * bestaetigen oder abbrechen kann; der Nutzer muss eine der angebotenen
 * Moeglichkeiten waehlen, um zur ausloesenden Anwendung zurueck zu kehren
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 * <br>
 */

public class YesNoDialog extends TranslucentDialog
{
    private static final long serialVersionUID = 1L;

	// Symbol des Dialogs
 	private Icon icon = new ImageIcon(
 	    Toolkit.getDefaultToolkit().createImage(
 	    	new File("icons\\question_big.png").getAbsolutePath()));
    
    
    // GUI-Komponenten des Dialogs
    private JPanel pnlButtons;
    private JButton btnOk;
    private JButton btnAbort;
    private JLabel lblMessage;
   
    // gibt an, ob der Dialog mittels OK/Ja geschlossen wurde
    private boolean accepted;

    /**
     * Konstruktor - erstellt ein neues Auswahlfenster
     *
     * @param title Titel des Fensters
     * @param message auszugebende Nachricht
     */
    public YesNoDialog(String title, String message)
    {
        // Vorbereiten des transluzenten Dialogs
        super(title, 450, 135);

        // Hinzufügen des Icons
        this.lblMessage = new JLabel();
        this.lblMessage.setIcon(this.icon);
        this.lblMessage.setText("<html>"+message+"</html>");
        this.lblMessage.setIconTextGap(10);
        this.lblMessage.setBounds(12, 25, 424, 50);
        this.lblMessage.setVisible(true);
        this.add(this.lblMessage);

        // Hinzufügen eines Button-Bereiches (graues Rechteck mit einem OK/Ja- und einem Abbrechen/Nein-Button)
        this.pnlButtons = new JPanel();
        this.pnlButtons.setBorder(
        	BorderFactory.createMatteBorder(1, 0, 0, 0, SpecialColor.LIGHTGRAY));
        this.pnlButtons.setBounds(1, 85, 449, 50);
        this.pnlButtons.setBackground(SpecialColor.WHITE);
        this.pnlButtons.setLayout(null);
        this.pnlButtons.setVisible(true);
        this.add(this.pnlButtons);

        this.btnOk = new JButton("Ja");
        this.btnOk.setBounds(232, 11, 100, 28);
        this.btnOk.setVisible(true);
        this.btnOk.addActionListener((aEvent) ->
        {
            // beim Klick auf den OK/Ja-Button wird das 
          	// Fenster geschlossen und true zurueck gegeben
            accepted = true;
            YesNoDialog.this.setVisible(false);
        });
        this.pnlButtons.add(this.btnOk);
        this.btnAbort = new JButton("Nein");
        this.btnAbort.setBounds(337, 11, 100, 28);
        this.btnAbort.setVisible(true);
        this.btnAbort.addActionListener((aEvent) ->
        {
            // beim Klick auf den Abbrechen/Nein-Button wird das Fenster geschlossen 
          	// und false zurueck gegeben
            accepted = false;
            YesNoDialog.this.setVisible(false);
        });
        this.pnlButtons.add(this.btnAbort);

        this.validate();
    }


    /**
     * zeigt das Fenster an und gibt beim Schliessen true (=OK/Ja) oder false
     * (= Abbrechen/Nein) zurueck, je nachdem welcher Button gedrueckt wurde
     *
     * @return true, wenn OK/Ja gedrueckt wurde
     */
    @Override
    public boolean accepted()
    {
        this.accepted = false;
        this.setVisible(true);
        return this.accepted;
    }
}