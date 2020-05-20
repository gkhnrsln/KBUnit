package main.panel.testDurchfuehren.testergebnisse;

import hilfe.guiHilfe.SpecialColor;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * HeadCellRenderer formatiert die Inhalte von Tabellenkoepfen, wenn ein Objekt
 * dieser Klasse als Renderer einer Tabelle zugewiesen wird
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class HeadCellRenderer extends JLabel implements TableCellRenderer
{
  	private static final long serialVersionUID = 1L;
  	// aktuell zu zeichnende Spalte
	private int col = 0;

    /**
     * zeichnet den Zellrahmen sowie die Zelle in Form eines JLabels
     * @param g Graphics - Objekt zur Zelle
     */
    @Override
    public void paint(Graphics g)
    {
        super.paint(g);
        if (col > 0)
        {
            g.setColor(SpecialColor.DARKGRAY);
            for (int i=0; i<=25; i++)
            {
                if ((i-1)%4 == 0) 
                {
                    g.drawLine(0, i, 0, i+1);
                }
            }
        }
    }

    
    /**
     * liefert den zu zeichnenden Zellinhalt in Form eines JLabels zurueck
     */
    @Override
    public JLabel getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        this.col = column;

        this.setOpaque(true);
        this.setBackground(SpecialColor.WHITE);
        this.setForeground(SpecialColor.BLACK);

        this.setHorizontalAlignment(LEFT);
        this.setVerticalAlignment(SwingConstants.BOTTOM);

        this.setIcon(null);
        this.setToolTipText(null);

        this.setText(value.toString());

        int leftPadding;
        if (column > 0)
        {
            leftPadding = 3;
        }
        else
        {
            leftPadding = 6;
        }

        EmptyBorder innerBorder = new EmptyBorder(0, leftPadding, 3, 3);
        MatteBorder outerBorder  = new MatteBorder(0, 0, 1, 0, SpecialColor.LIGHTGRAY);
        Border border = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
        this.setBorder(border);

        // Spezielle Ersetzungen
        if (value.equals(" E "))
        {
            this.setHorizontalAlignment(CENTER);
            this.setText("!");
            this.setToolTipText("Exception erwartet");
        }
        if (value.equals(" B "))
        {
            this.setHorizontalAlignment(CENTER);
            this.setText("Ergebnis");
        }

        return this;
    }
}
