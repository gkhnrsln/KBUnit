package main.dialogfenster;

import hilfe.guiHilfe.SpecialColor;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * InfoDialog stellt ein einfaches Benachrichtigungsfenster zur Verfuegung, das
 * der Nutzer schliessen muss, um zur ausloesenden Anwendung zurueck zu kehren.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz, Ursula Oesing
 * <br>
 */

public class InfoDialog extends TranslucentDialog//extends JDialog
{
 	private static final long serialVersionUID = 1L;

	// Symbol des Dialogs
    private Icon icon = new ImageIcon(
        Toolkit.getDefaultToolkit().createImage(
        	new File("icons\\info_big.png").getAbsolutePath()));

    // GUI-Komponenten des Dialogs
    private JPanel pnlButtons;
    private JButton btnOk;
    private JLabel lblMessage;
  
    /**   
     * Konstruktor - erstellt ein neues Benachrichtigungsfenster
     *
     * @param title Titel des Fensters
     * @param message auszugebende Nachricht
     */
    public InfoDialog(String title, String message)
    {

        super(title, 450, 170);
 
        // Hinzufuegen des Icons
        this.lblMessage = new JLabel();
        this.lblMessage.setIcon(this.icon);
        this.lblMessage.setText("<html>"+message+"</html>");
        this.lblMessage.setIconTextGap(10);
        this.lblMessage.setBounds(12,25,426,90);
        this.lblMessage.setVisible(true);
        this.add(this.lblMessage);

        // Hinzufuegen eines Button-Bereiches 
        // (graues Rechteck mit einem OK-Button)
        this.pnlButtons = new JPanel();
        this.pnlButtons.setBounds(1,125,449,50);
        this.pnlButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, 
            SpecialColor.LIGHTGRAY));
        this.pnlButtons.setBackground(SpecialColor.WHITE);
        this.pnlButtons.setLayout(null);
        this.pnlButtons.setVisible(true);
        this.add(this.pnlButtons);

        this.btnOk = new JButton("OK");
        this.btnOk.setBounds(337, 11, 100, 28);
        this.btnOk.setVisible(true);
        this.btnOk.addActionListener((aEvent) ->
        {
            // beim Klick auf den OK-Button wird das 
           	// Benachrichtigungsfenster geschlossen
            InfoDialog.this.setVisible(false);
        });
        this.pnlButtons.add(this.btnOk);

        // Darstellung des Benachrichtigungsfensters
        this.setVisible(true);

    }
}