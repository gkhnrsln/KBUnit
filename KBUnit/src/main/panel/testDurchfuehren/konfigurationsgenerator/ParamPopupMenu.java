package main.panel.testDurchfuehren.konfigurationsgenerator;

import hilfe.guiHilfe.CurvedTitledBorder;
import hilfe.guiHilfe.SpecialColor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ParamPopupMenu liefert fuer den Konfigurationsgenerator ein Popup-Menue
 * zur Verfuegung, mit dem einzelne Parameterkombinationen aus der Tabelle von
 * KBUnit ermittelter Kombinationen entfernt werden koennen, bevor es zur 
 * Ausfuehrung kommt
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ParamPopupMenu extends JPopupMenu
{

 	private static final long serialVersionUID = 1L;
	// das Menue besitzt nur einen Eintrag zum Loeschen einer 
 	// ausgewaehlten Parameterkombination
    JMenuItem mnPopupDelete = new JMenuItem();

    /**
     * erzeugt ein ParamPopupMenu fuer das Konfigurationsgenerator Panel
     * @param parent Aufrufer des PopupMenus
     * @param selectedRow enthaelt die selektierte Zeile
     */
    public ParamPopupMenu(final KonfigurationsgeneratorPanel parent, 
    	final int selectedRow)
    {
        super();

        this.setBackground(SpecialColor.WHITE);
        CurvedTitledBorder ctb 
            = new CurvedTitledBorder(1, "Optionen", SpecialColor.ORANGE);
        ctb.abstandUnten = 1;
        this.setBorder(ctb);
        this.setOpaque(false);

        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        this.mnPopupDelete.setText("Konfiguration löschen");
        this.mnPopupDelete.setBorder(new EmptyBorder(1, 6, 1, 6));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, -7, 0);
        this.add(this.mnPopupDelete, gbc);

        this.mnPopupDelete.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                parent.getTblCombinationsModel().removeCombination(selectedRow);
                if (parent.getTblCombinationsModel().getRowCount() == 0)
                {
                    parent.switchTab3ViewFromCombinationsToGenerator();
                }
            }
        });
    }
    
    /**
     * zeichnet das Popup-Menue
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