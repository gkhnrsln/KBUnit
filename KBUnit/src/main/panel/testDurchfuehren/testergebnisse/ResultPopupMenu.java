package main.panel.testDurchfuehren.testergebnisse;

import hilfe.guiHilfe.CurvedTitledBorder;
import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.TestResultInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * ResultPopupMenu stellt ein Popup-Menue zur Verfuegung, 
 * mit welchem gespeicherte
 * Konfigurationen erneut ausgefuehrt oder geloescht werden koennen.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ResultPopupMenu extends JPopupMenu
{
   	private static final long serialVersionUID = 1L;
	// die beiden Eintraege des Menues
    private JMenuItem mnPopupRerun = new JMenuItem();
    private JMenuItem mnPopupDelete = new JMenuItem();
    // Testkonfiguration, auf welche sich das PopupMenu bezieht
    private final ArrayList<TestResultInfo> trilist;

     /**
     * Konstruktor, erwartet das Hauptanwendungsfenster sowie eine Liste von
     * Testkonfigurationen, auf deren Basis die einzelnen, mit den Buttons des
     * Menues verknuepften Aktionen ausgefuehrt werden sollen
     *
     * @param parent Hauptanwendungsfenster, dass die Tabelle enthaelt, zu der das
     *   Popup-Menue gehoert
     * @param tl Liste von Testkonfigurationen, auf die sich die Aktionen des
     *   Popup-Menues beziehen sollen
     */
    public ResultPopupMenu(final BasicFrameView parent, ArrayList<TestResultInfo> tl)
    {
        super();

        this.trilist = tl;
  
        this.setBackground(SpecialColor.WHITE);
        CurvedTitledBorder ctb = new CurvedTitledBorder(1, "Optionen", 
        	SpecialColor.ORANGE);
        ctb.abstandUnten = 1;
        this.setBorder(ctb);
        this.setOpaque(false);

        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        this.mnPopupRerun.setText("Test erneut ausführen");
        this.mnPopupRerun.setBorder(new EmptyBorder(1, 6, 1, 6));
        gbc.gridy = 0;
        this.add(this.mnPopupRerun, gbc);
        this.mnPopupDelete.setText("Test löschen");
        this.mnPopupDelete.setBorder(new EmptyBorder(1, 6, 1, 6));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, -7, 0);
        this.add(this.mnPopupDelete, gbc);
 
        this.mnPopupRerun.addActionListener((aEvent) ->
        {
            parent.getBasicFrameControl()
                .rerunTests(parent.getBasicFrameControl()
                .getBasicFrameModel()
                .getOpenedTestCase(), trilist);
        });

        this.mnPopupDelete.addActionListener((aEvent) ->
        {
            parent.deleteTestConfigurations(trilist);
        });
    }
    
    /**
     * zeichnet das Popup-Meue
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
        	RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(SpecialColor.WHITE);
        g2.fillRoundRect(1, 1, getWidth()-1, getHeight()-1,5,5);

        paintBorder(g);
    }

    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        paintBorder(g);
    }

   
}