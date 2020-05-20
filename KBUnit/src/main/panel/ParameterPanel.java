package main.panel;

import javax.swing.*;
import java.util.ArrayList;

/**
 * ParameterPanel ist ein Panel, welches die Eingabe der Parameterwerte
 * ermoeglicht und zusaetzlich zwei Listen (fuer Labels und Eingabekomponenten) 
 * besitzt, in denen die auf dem Panel platzierten Komponenten registriert werden 
 * koennen, um sie spaeter einfacher wiederfinden, auslesen und loeschen zu koennen
 *
* <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ParameterPanel extends JPanel
{
   	private static final long serialVersionUID = 1L;

   	// Liste aller Label
	private ArrayList<JLabel> labelList = new ArrayList<>();

	// Liste aller Eingabefelder
    private ArrayList<JComponent> inputComponentList = new ArrayList<>();

    /**
     * gibt eine Liste der Label des Eingabepanels heraus
     * @return Liste der Label des Eingabepanels
     */   
    public ArrayList<JLabel> getLabelList() 
    {
        return labelList;
    }
      
    /**
     * gibt eine Liste der Eingabefelder des Eingabepanels heraus
     * @return Liste der Eingabefelder des Eingabepanels
     */   
    public ArrayList<JComponent> getInputComponentList() 
    {
        return inputComponentList;
    }

      
    /**
     * loescht die Eingabefelder 
     */
    public void clearUserInterface()
    {
        this.labelList.clear();
        this.inputComponentList.clear();
        this.removeAll();
    }

}