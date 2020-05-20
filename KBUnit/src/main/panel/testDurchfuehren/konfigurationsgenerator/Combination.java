package main.panel.testDurchfuehren.konfigurationsgenerator;

import main.hauptfenster.TestParameterInfo;

import java.util.ArrayList;

/**
 * Combination stellt eine bestimmte Kombination von Parameterwerten dar und
 * speichert, ob fuer diese Kombination das Auftreten einer Exception bei
 * Testausfuehrung erwartet wird.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class Combination
{
    /** Liste von Testparametern */	
    public ArrayList<TestParameterInfo> pilist = new ArrayList<>();
    /** 
     * speichert, ob eine Exception erwartet wird
     */
    public boolean exceptionExpected = false;


    /**
     * Konstruktor, legt auf Basis einer Liste von Testparametern sowie der
     * Information, ob eine Exception erwartet wird, eine neue Kombination an
     *
     * @param pilist Liste von Testparametern
     * @param exceptionExpected Angabe, ob Exception erwartet wird
     */
    public Combination(ArrayList<TestParameterInfo> pilist, boolean exceptionExpected)
    {
        this.pilist = pilist;
        this.exceptionExpected = exceptionExpected;
    }
}