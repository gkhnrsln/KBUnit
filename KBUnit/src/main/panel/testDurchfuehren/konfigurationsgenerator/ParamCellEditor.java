package main.panel.testDurchfuehren.konfigurationsgenerator;

import hilfe.guiHilfe.ImageTextField;
import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.TestParameterInfo;
import main.panel.testDurchfuehren.ParameterVerifier;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * ParamCellEditor ermoeglicht die Editierung von Tabellenzellen, die Klasse wird
 * genutzt, um die Ergebniswerte automatisch generierter
 * Testparameterkonfigurationen innerhalb einer Tabelle modifizieren zu koennen
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class ParamCellEditor extends AbstractCellEditor implements TableCellEditor
{
   	private static final long serialVersionUID = 1L;
	Combination combination;
    int index;

    int activeComponent;
    ImageTextField imageTextfield;
    JCheckBox chb;

    
    /**
     * liefert den in der Zelle eingetragenen Wert zurueck und schreibt ihn
     * in den zugeordneten Parameter der Testkonfiguration
     */
    @Override
    public Object getCellEditorValue()
    {
        try
        {
            // this.imageTextfield
            if (this.activeComponent == 1)
            {
                TestParameterInfo tpi = this.combination.pilist.get(this.index);
                ParameterVerifier pv 
                    = (ParameterVerifier) this.imageTextfield.getInputVerifier();
                if (!(pv.getLastVerificationResult() == false))
                {
                    tpi.setValue(pv.getSortedInput().get(0));
                }
            }
            // checkbox
            else
            {
                this.combination.exceptionExpected = this.chb.isSelected();
            }
        }
        catch(Exception e)
        {
            // es wird nichts gemacht, das urspruengliche, 
        	// unmodifizierte Objekt wird bei falscher Eingabe zurueckgliefert
        }
        return this.combination;
    }


    /**
     * liefert eine editierbare Komponente (JTextField) zurueck, in der der Wert
     * eines Parameters modifiziert werden kann
     *
     * @return Eingabekomponente, in der der Parameterwert editiert werden kann
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column)
    {
        this.combination = (Combination) value;
        this.index = column;

        if (column < table.getColumnCount()-1)
        {
            this.activeComponent = 1;
            TestParameterInfo tpi = this.combination.pilist.get(column);
            this.imageTextfield = new ImageTextField(tpi.getValue()+"");
            ParameterVerifier pv = new ParameterVerifier(tpi.getValue().getClass(), false);
            this.imageTextfield.setInputVerifier(pv);
            this.imageTextfield.getInputVerifier().verify(this.imageTextfield);
            return this.imageTextfield;
        }
        else
        {
            this.activeComponent = 2;
            this.chb = new JCheckBox();
            this.chb.setSelected(this.combination.exceptionExpected);
            this.chb.setHorizontalAlignment(SwingConstants.CENTER);
            this.chb.setOpaque(true);
            this.chb.setBackground(SpecialColor.WHITEGRAY);

            // beim Klick auf die Checkbox wird sofort ausgeloest, dass der neue Wert 
            // fuer exceptionExpected in der ausgewaehlten Combination hinterlegt wird 
            // (sonst wuerde der neue Wert erst nach Verlassen der Zelle geschrieben und 
            // die Ergebnisparameter wuerden erst dann ausgegraut werden)
            final JTable t = table;
            this.chb.addActionListener((aEvent) ->
            {
                fireEditingStopped();
                t.repaint();
            });
            return this.chb;
        }
    }
}