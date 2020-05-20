package main.panel.testDurchfuehren.konfigurationsgenerator;

import main.hauptfenster.TestParameterInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfo;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * ParamTableModel liefert das Model hinter der Tabelle von KBUnit ermittelter
 * Parameterkombinationen im Konfigurationsgenerator
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ParamTableModel extends AbstractTableModel
{ 
 	private static final long serialVersionUID = 1L;
 	// enthaelt die Anzahl der Spalten der Parameterkombinationen
	private int columnCount = 0;
	// enthaelt die Anzahl der Zeilen der Parameterkombinationen
    private int rowCount = 0;
    // enthaelt die Liste von Parameterkombinationen
    private ArrayList<Combination> combinations = new ArrayList<>();

    // schliesst auch das Exception-Flag mit ein
    public int numberOfEditableParameters = 1;


    /**
     * Konstruktor, erwartet Informationen ueber den zugrundeliegenden Testfall
     *
     * @param tci Testfall, anhand dessen der Aufbau der Tabelle bestimmt werden
     *   kann
     */
    public ParamTableModel(TestCaseInfo tci)
    {
        super();

        // Zahl aller Spalten bestimmen
        ArrayList<Field> parameters = tci.getParameters();
        this.columnCount = parameters.size() + 1;

        // Zahl der editierbaren Spalten bestimmen
        for (int i=0; i<parameters.size(); i++)
        {
            String paramName = parameters.get(i).getName();
            int index = paramName.indexOf("_");
            if (paramName.substring(index+1).length() >= 4
            	&& paramName.substring(index+1,index+5).equals("exp_"))
            {
                this.numberOfEditableParameters++;
            }
        }   
    }


    /**
     * fuegt der Tabelle eine neue Parameterkombination hinzu
     *
     * @param pilist Liste der Parameter, die kombiniert werden sollen
     * @param exceptionExpected Angabe, ob fuer die uebergebene
     *   Parameterkombination mit dem Wurf einer Exception gerechnet wird
     */
    public void addCombination(ArrayList<TestParameterInfo> pilist, 
    	boolean exceptionExpected)
    {
        this.combinations.add(new Combination(pilist, exceptionExpected));
        this.rowCount++;
        fireTableDataChanged();
    }


    /**
     * entfernt die in der uebergebenen Zeile hinterlegte Parameterkombination
     *
     * @param row Zeile, die aus dem Modell geloescht werden soll
     */
    public void removeCombination(int row)
    {
        this.combinations.remove(row);
        this.rowCount--;
        fireTableDataChanged();
    }


    /**
     * entfernt alle im Modell enthaltenenen Parameterkombinationen
     */
    public void removeAllCombinations()
    {
        this.combinations.clear();
        this.rowCount = 0;
        fireTableDataChanged();
    }


    /**
     * liefert eine Liste alle im Modell enthaltenen Parameterkombinationen
     * zurueck
     *
     * @return Liste der Parameterkombinationen
     */
    public ArrayList<Combination> getCombinations()
    {
        return this.combinations;
    }
 

    /**
     * liefert die Zeilenanzahl (= Zahl im Modell gespeicherter
     * Parameterkombinationen) der Tabelle zurueck
     *
     * @return Anzahl im Modell gespeicherter Parameterkombinationen
     */
    @Override
    public int getRowCount()
    {
        return this.rowCount;
    }


    /**
     * liefert die Spaltenzahl der Tabelle zurueck
     *
     * @return Spaltenzahl
     */
    @Override
    public int getColumnCount()
    {
        return this.columnCount;
    }


    /**
     * liefert den Wert einer Zelle zurueck
     *
     * @param row Zeilennummer der Zelle
     * @param col Spaltennummer der Zelle
     *
     * @return in der Zelle gespeicherter Wert
     */
    @Override
    public Object getValueAt(int row, int col)
    {
        return this.combinations.get(row);
    }


    /**
     * ueberschreibt den Wert einer Zelle
     *
     * @param aValue Wert, der in die Zelle geschrieben werden soll
     * @param rowIndex Zeilennummer der Zelle
     * @param columnIndex Spaltennummer der Zelle
     */
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        this.combinations.remove(rowIndex);
        this.combinations.add(rowIndex, (Combination) aValue);
    }


    /**
     * liefert zurueck, ob die gewaehlte Zelle editierbar ist
     *
     * @param rowIndex Zeilennummer der Zelle
     * @param columnIndex Spaltennummer der Zelle
     *
     * @return Editierbarkeit
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        // Spalten, die vom Programm berechnete Parameter enthalten koennen 
    	// nicht editiert werden
        if (columnIndex < this.columnCount-this.numberOfEditableParameters)
        {
            return false;
        }
        // die letzte Spalte (Auswahl ob Exception erwartet) kann immer editiert werden
        else if (columnIndex == this.columnCount-1)
        {
                return true;
        }
        // alle Spalten, die Ergebnisparameter enthalten, koennen editiert werden, 
        // wenn keine Exception erwartet wird
        else
        {     
            if (this.combinations.get(rowIndex).exceptionExpected)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }
}