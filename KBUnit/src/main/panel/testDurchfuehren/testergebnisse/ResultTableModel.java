package main.panel.testDurchfuehren.testergebnisse;

import main.hauptfenster.TestResultInfo;
import main.hauptfenster.testCaseInfo.TestCaseInfo;

import javax.swing.table.AbstractTableModel;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * ResultTableModel liefert ein Modell fuer die Tabelle zur Anzeige
 * gespeicherter Testkonfigurationen
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class ResultTableModel extends AbstractTableModel
{
  	private static final long serialVersionUID = 1L;
  	// enthaelt die Anzahl von Spalten
	private int columnCount = 0;
	// enthaelt die Anzahl von Spalten
    private int rowCount = 0;
    // enthaelt die Parameter des darzustellenden Testfalls
    private ArrayList<Field> parameters;
    // enthaelt die darzustellenden Testkonfigurationen
    private ArrayList<TestResultInfo> trilist;
    // enthaelt diejenigen Testkonfigurationen, die eine 
    // andere Hintergrundfarbe erhalten
    private ArrayList<Long> highlightableTestResults = new ArrayList<>();

    /**
     * erstellt ein ResultTableModel-Objekt, welches die Inhalte der 
     * Ergebnistabelle enthaelt
     * @param tci TestCaseInfo, welche den darzustellenden Testfall enthaelt
     */
    public ResultTableModel(TestCaseInfo tci)
    {
        super();
        this.trilist = new ArrayList<>();
        this.parameters = tci.getParameters();
        this.columnCount = this.parameters.size() + 2;
    }

    /**
    * fuegt dem Modell eine neue Testkonfiguration hinzu oder ueberschreibt ein
    * bestehendes, sollte ein Testlauf mit den gleichen Parametern bereits
    * bestehen
    *
    * @param tri hinzuzufuegende Testkonfiguration
    */
    public void addTestResult(TestResultInfo tri)
    {
        boolean existingTestResultReplaced = false;

        for (int i=0; i<this.trilist.size(); i++)
        {
            TestResultInfo savedTri = this.trilist.get(i);
            if (savedTri.getId() == tri.getId())
            {
                this.trilist.set(i, tri);
                existingTestResultReplaced = true;
                break;
            }
        }

        if (!existingTestResultReplaced)
        {
            this.trilist.add(0,tri);
            this.rowCount++;
        }
    }


    /**
     * entfernt die uebergebene Testkonfiguration aus dem Modell
     *
     * @param tri zu entfernende Testkonfiguration
     */
    public void removeTestResult(TestResultInfo tri)
    {
        int index = this.trilist.indexOf(tri);
        if (index > -1)
        {
            this.trilist.remove(index);
            this.rowCount--;
        }
    }


    /**
     * liefert die Anzahl der Tabellenzeilen zurueck
     *
     * @return Zeilenzahl
     */
    @Override
    public int getRowCount()
    {
        return this.rowCount;
    }


    /**
     * liefert die Anzahl der Tabellenspalten zurueck
     *
     * @return Spaltenzahl
     */
    @Override
    public int getColumnCount()
    {
        return this.columnCount;
    }


    /**
     * leifert den in einer Zelle abgelegten Wert zurueck
     *
     * @param row Zeilennummer der Zelle
     * @param col Spaltennummer der Zelle
     *
     * @return in der Zelle abgelegter Wert
     */
    @Override
    public Object getValueAt(int row, int col)
    {
        TestResultInfo tri;
        tri = this.trilist.get(row);

        return tri;
    }


    /**
     * die Konfiguration mit der uebergebenen ID wird in der Tabelle
     * hervorgehoben dargestellt, um die zuletzt ausgefuehrten Konfigurationen
     * sofort erkenn zu koennen
     *
     * @param id ID der hervorzuhebenden Testkonfiguration
     */
    public void highlightTestResult(long id)
    {
        highlightableTestResults.add(id);
    }


    /**
     * entfernt alle Hervorhebungen, d.h. alle Testkonfigurationen werden in der
     * Tabelle normal dargestellt
     */
    public void removeAllHighlights()
    {
        highlightableTestResults.clear();
    }


    /**
     * ermittelt, ob die Testkonfiguration mit der uebergebenen ID innerhalb der
     * Tabelle hervorgehoben dargestellt wird
     *
     * @param id ID der zu untersuchenden Testkonfiguration
     *
     * @return Hervorhebung
     */
    public boolean isHighlightableTestResult(long id)
    {
        if (highlightableTestResults.contains(id))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}