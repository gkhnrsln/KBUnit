package main.dialogfenster;

import main.panel.SuccessBar;

/**
 * ProgressDialog stellt ein einfaches Fenster mit einem Fortschrittsbalken dar,
 * in dem der Fortschritt eines Prozesses visualisiert werden kann
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 * <br>
 */

public class ProgressDialog extends TranslucentDialog
{
   	private static final long serialVersionUID = 1L;
	// GUI-Komponenten des Dialogs
    private SuccessBar sb;
  

    /**
     * Konstruktor - erstellt ein neues Benachrichtigungsfenster
     *
     * @param title Titel des Fensters
     */
    public ProgressDialog(String title)
    {
        super(title, 450, 70);
        setModalityType(ModalityType.MODELESS);
       
        this.sb = new SuccessBar(0,0
                ,0, 0, 0);
        this.sb.setBounds(12, 29, 426, 25);
        this.add(this.sb);
    }


    /**
     * setzt das Maximum des Fortschrittsbalkens
     *
     * @param capacity Maximum des Fortschrittsbalkens (Gesamtzahl Teilprozesse)
     */
    public void setTotalCapacity(int capacity)
    {
        this.sb.setTotalCapacity(capacity);
    }

    /**
     * setzt den Anteil erfolgreich ausgefuehrter Teilprozesse (diese werden im
     * Fortschrittsbalken gruen angezeigt)
     *
     * @param successes Anzahl erfolgreicher Teilprozesse
     */
    public synchronized void setNumberOfSuccesses(int successes)
    {
        this.sb.setNumberOfSuccesses(successes);
    }

    /**
     * setzt den Anteil durch Assumptions abgebrochene Teilprozesse (diese werden im
     * Fortschrittsbalken gelb angezeigt)
     *
     * @param abortionsByAssumptions Anzahl erfolgreicher Teilprozesse
     */
    public synchronized void setNumberOfAbortionsByAssumptions(int abortionsByAssumptions)
    {
        this.sb.setNumberOfAbortionsByAssumptions(abortionsByAssumptions);
    }

    /**
     * setzt den Anteil dder uebersprungenen Teilprozesse (diese werden im
     * Fortschrittsbalken gelb angezeigt)
     *
     * @param skips Anzahl erfolgreicher Teilprozesse
     */
    public synchronized void setNumberOfSkips(int skips)
    {
        this.sb.setNumberOfSkips(skips);
    }

    /**
     * setzt den Anteil nicht erfolgreich ausgefuehrter Teilprozesse (diese
     * werden im Fortschrittsbalken rot angezeigt)
     *
     * @param failures Anzahl nicht erfolgreicher Teilprozesse
     */
    public synchronized void setNumberOfFailures(int failures)
    {
        this.sb.setNumberOfFailures(failures);
    }

    public int getNumberOfFailures() 
    { 
    	return this.sb.getNumberOfFailures(); 
    }
    
    public int getNumberOfSuccesses() 
    { 
    	return this.sb.getNumberOfSuccesses(); 
    }
    
    public int getNumberOfAbortionsByAssumptions() 
    { 
    	return this.sb.getNumberOfAbortionsByAssumptions(); 
    }
    
    public int getNumberOfSkips() 
    { 
    	return this.sb.getNumberOfSkips(); 
    }
}