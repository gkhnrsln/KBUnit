package main.panel.testDurchfuehren.konfigurationsgenerator;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.TestParameterInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * ParamCellRenderer formatiert die Inhalte der Tabelle, in welcher generierte
 * Parameterkombinationen angezeigt werden; anhand einer speziellen Farbgebung
 * erkennt der Nutzer beispielsweise, welche Parameterwerte Grenzwerte 
 * darstellen, gueltig oder ungueltig sind
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class ParamCellRenderer implements TableCellRenderer
{ 
    /**
     * liefert die zu zeichnende Zelle in Form eines JLabels oder einer
     * JCheckBox zurueck
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        Combination c = (Combination) value;

        if (column < table.getColumnCount()-1)
        {
            final int col = column;
            JLabel lbl = new JLabel()
            {
     			private static final long serialVersionUID = 1L;

				@Override
                public void paint(Graphics g) 
                {
                    super.paint(g);
                    if (col > 0)
                    {
                        g.setColor(SpecialColor.WHITEGRAY);
                        for (int i=0; i<=36; i++)
                        {
                            if (i%4 == 0) 
                            {
                                g.drawLine(0, i, 0, i+1);
                            }
                        }
                    }
                }
            };

            int leftPadding;
            if (column == 0)
            {
                leftPadding = 7;
            }
            else
            {
                leftPadding = 3;
            }

            TestParameterInfo tpi = c.pilist.get(column);
            String hilfeText;
            String hilfeToolTipText;
            if(tpi.getValue() == null)
            {
            	hilfeText = "null";
            	hilfeToolTipText = "Der Wert existiert nicht.";
            }
            else if((tpi.getValue() + "").length() == 0)
            {
            	hilfeText = "\"\"";
            	hilfeToolTipText = "Der Wert ist ein leerer String.";
            }
            else
            {
            	hilfeText = tpi.getValue() + "";
            	hilfeToolTipText = hilfeText;
            }
            lbl.setText(hilfeText);
            lbl.setToolTipText(hilfeToolTipText);
            lbl.setBorder(new EmptyBorder(0, leftPadding, 0, 0));

            lbl.setOpaque(true);
            if (isSelected)
            {
                lbl.setBackground(SpecialColor.WHITEGRAY);
            }
            else
            {
                lbl.setBackground(SpecialColor.WHITE);
            }
            
            if (tpi.getType() == TestParameterInfo.PARAMETER_IS_BOUND)
            {
                lbl.setForeground(SpecialColor.ORANGE);
            }
            else if(tpi.getType() == TestParameterInfo.PARAMETER_OUT_OF_BOUNDS)
            {
                lbl.setForeground(SpecialColor.RED);
            }
            else
            {
                if (c.exceptionExpected && tpi.getType() 
                	== TestParameterInfo.RESULTPARAMETER)
                {
                    lbl.setForeground(SpecialColor.LIGHTGRAY);
                }
                else
                {
                    lbl.setForeground(SpecialColor.BLACK);
                }
            }
            return lbl;
        }
        else
        {
            final int col = column;
            JCheckBox chb = new JCheckBox()
            {
       			private static final long serialVersionUID = 1L;

				@Override
                public void paint(Graphics g)
                {
                    super.paint(g);
                    if (col > 0)
                    {
                        g.setColor(SpecialColor.WHITEGRAY);
                        for (int i=0; i <= 36; i++)
                        {
                            if (i%4 == 0) 
                            {
                                g.drawLine(0, i, 0, i+1);
                            }
                        }
                    }
                }
            };
            chb.setSelected(c.exceptionExpected);
            chb.setHorizontalAlignment(SwingConstants.CENTER);
            chb.setOpaque(true);
            if (isSelected)
            {
                chb.setBackground(SpecialColor.WHITEGRAY);
            }
            else
            {
                chb.setBackground(SpecialColor.WHITE);
            }
            return chb;
        } 
    }
}