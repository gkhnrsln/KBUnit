package main.panel;

import hilfe.guiHilfe.SpecialColor;

import javax.swing.*;
import java.awt.*;

/**
 * SuccesBar ist ein Fortschritssbalken, dessen Fortschritt in einen gruenen und
 * einen roten Teil aufgesplittet werden kann; somit ist eine Unterscheidung
 * bei der Ausfuehrung erfolgreicher und fehlerhafter Teilprozesse moeglich 
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class SuccessBar extends JPanel
{
   	private static final long serialVersionUID = 1L;
   	
   	// Anzahl erfolgreicher Testfaelle, nicht erfolgreicher Testfaelle, 
   	// aller Testfaelle
	int numberOfSuccesses, numberOfFailures
        , numberOfAbortionsByAssumptions, numberOfSkips, totalCapacity;


    /**
     * zeichnet den Fortschrittsbalken
     */
    @Override
    protected synchronized void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);

        // fuer den Fall, dass keine Datenbankverbindung besteht, werden die Balken 
        // nicht gezeichnet (in diesem Fall sind ja auch keine Testergebnisse sichtbar)
        if (this.totalCapacity > 0)
        {
            // Berechnung des gruenen und roten Anteils (getWidth()-2)
            int wSuccess = (getWidth())* this.numberOfSuccesses / this.totalCapacity;
            int wFailures = (getWidth())* this.numberOfFailures / this.totalCapacity;
            int wAbortionsByAssumptions 
                = (getWidth())* this.numberOfAbortionsByAssumptions / this.totalCapacity;
            int wSkips = (getWidth())* this.numberOfSkips / this.totalCapacity + 1;

            // gruener Teilbalken
            g2d.setPaint(new GradientPaint(0, 0, SpecialColor.WHITEGREEN, 0, 
                getHeight() / 2, SpecialColor.LIGHTGREEN));
            g2d.fillRect(1, 1, wSuccess,(getHeight() - 2) / 2);
            g2d.setPaint(new GradientPaint(0, getHeight() / 2 + 1, SpecialColor.GREEN, 
                0, getHeight(), SpecialColor.WHITEGREEN));
            g2d.fillRect(1, (getHeight() - 2) / 2 + 1,
                wSuccess,getHeight() - 2);

            // roter Teilbalken
            g2d.setPaint(new GradientPaint(0, 0, SpecialColor.WHITERED, 0, 
                getHeight() / 2, SpecialColor.LIGHTRED));
            g2d.fillRect(wSuccess + 1,1, wFailures,(getHeight() - 2) / 2);
            g2d.setPaint(new GradientPaint(0, getHeight() / 2 + 1, SpecialColor.RED,
                    0, getHeight(), SpecialColor.WHITERED));
            g2d.fillRect(wSuccess + 1,(getHeight()-2) / 2 + 1,
                    wFailures, getHeight() - 2);

            // gelber Teilbalken
            g2d.setPaint(new GradientPaint(0, 0, SpecialColor.WHITEYELLOW, 0,
                    getHeight() / 2, SpecialColor.LIGHTYELLOW));
            g2d.fillRect(wSuccess + wFailures + 1,1, wAbortionsByAssumptions,(getHeight() - 2) / 2);
            g2d.setPaint(new GradientPaint(0, getHeight() / 2 + 1, SpecialColor.YELLOW, 0,
                    getHeight(), SpecialColor.WHITEYELLOW));
            g2d.fillRect(wSuccess + wFailures + 1, (getHeight()-2) / 2 + 1,
                    wAbortionsByAssumptions, getHeight() - 2);

            // grauer Teilbalken
            g2d.setPaint(new GradientPaint(0, 0, SpecialColor.WHITEGRAY, 0,
                    getHeight() / 2, SpecialColor.LIGHTGRAY));
            g2d.fillRect(wSuccess + wFailures + wAbortionsByAssumptions + 1,1,
                    wSkips,(getHeight() - 2) / 2);
            g2d.setPaint(new GradientPaint(0, getHeight() / 2 + 1, SpecialColor.GRAY, 0,
                    getHeight(), SpecialColor.WHITEGRAY));
            g2d.fillRect(wSuccess + wFailures + wAbortionsByAssumptions + 1, (getHeight()-2) / 2 + 1,
                    wSkips, getHeight() - 2);

            // Rahmen
            g2d.setColor(SpecialColor.WHITE);
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g2d.drawRoundRect(0, 0, getWidth() - 1,getHeight() - 1, 2, 2);
            g2d.drawRoundRect(0, 0, getWidth() - 1,getHeight() -1, 3, 3);
            g2d.setColor(SpecialColor.GRAY);
            g2d.drawRoundRect(0, 0, getWidth() - 1,getHeight() - 1, 4, 4);
        }
        else
        {
            g2d.setColor(SpecialColor.LIGHTGRAY);
            g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
        }  
    }


    /**
     * Konstruktor, erwartet genaue Angaben zum Initialfortschritt
     *
     * @param numberOfSuccesses Anzahl erfolgreich ausgefuehrter Teilprozesse
     * @param numberOfFailures Anzahl nicht erfolgreich ausgefuehrter
     *   Teilprozesse
     * @param numberOfAbortionsByAssumptions Anzahl durch Assumptions abgebrochener
     *   Teilprozesse
     * @param numberOfSkips Gesamtzahl an Teilprozessen (ausgefuehrte und nicht
     *   ausgefuehrte)
     * @param totalCapacity Gesamtzahl an Teilprozessen (ausgefuehrte und nicht
     *   ausgefuehrte)
     */
    public SuccessBar(int numberOfSuccesses, int numberOfFailures
        , int numberOfAbortionsByAssumptions, int numberOfSkips, int totalCapacity)
    {
        this.numberOfSuccesses = numberOfSuccesses;
        this.numberOfFailures  = numberOfFailures;
        this.totalCapacity     = totalCapacity;
        this.numberOfAbortionsByAssumptions = numberOfAbortionsByAssumptions;
        this.numberOfSkips = numberOfSkips;
        this.setOpaque(false);
    }


    /**
     * setzt die Anzahl erfolgreich ausgefuehrter Teilprozesse
     *
     * @param successes Anzahl erfolgreich ausgefuehrter Teilprozesse
     */
    public void setNumberOfSuccesses(int successes)
    {
        this.numberOfSuccesses = successes;
        this.repaint();
    }

    /**
     * setzt die Anzahl durch Assumptions ausgefuehrter Teilprozesse
     *
     * @param abortionsByAssumptions Anzahl erfolgreich ausgefuehrter Teilprozesse
     */
    public void setNumberOfAbortionsByAssumptions(int abortionsByAssumptions)
    {
        this.numberOfAbortionsByAssumptions = abortionsByAssumptions;
        this.repaint();
    }

    /**
     * setzt die Anzahl durch Assumptions ausgefuehrter Teilprozesse
     *
     * @param skips Anzahl erfolgreich ausgefuehrter Teilprozesse
     */
    public void setNumberOfSkips(int skips)
    {
        this.numberOfSkips = skips;
        this.repaint();
    }

    /**
     * setzt die Anzahl nicht erfolgreich ausgefuehrter Teilprozesse
     *
     * @param failures Anzahl nicht erfolgreich ausgefuehrter Teilprozesse
     */
    public void setNumberOfFailures(int failures)
    {
        this.numberOfFailures = failures;
        this.repaint();
    }


    /**
     * setzt die Gesamtzahl von Teilprozessen
     *
     * @param capacity Gesamtzahl von Teilprozessen
     */
    public void setTotalCapacity(int capacity)
    {
        this.totalCapacity = capacity;
        this.repaint();
    }

    public int getNumberOfSuccesses() 
    {
        return numberOfSuccesses;
    }

    public int getNumberOfFailures() 
    {
        return numberOfFailures;
    }

    public int getNumberOfAbortionsByAssumptions() 
    {
        return numberOfAbortionsByAssumptions;
    }

    public int getNumberOfSkips() 
    {
        return numberOfSkips;
    }

    public int getTotalCapacity() 
    {
        return totalCapacity;
    }
}