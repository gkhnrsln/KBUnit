package main.panel.testDurchfuehren.testergebnisse;

import hilfe.guiHilfe.Formatation;
import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.TestResultInfo;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.File;
import java.text.DateFormat;

/**
 * ResultCellRenderer formatiert die Zellen der Tabelle, in welcher die
 * Konfigurationen eines Testfalles mitsamt ihren Ausfuehrungsergebnissen
 * dargestellt werden
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class ResultCellRenderer extends JLabel implements TableCellRenderer
{
    private static final long serialVersionUID = 1L;
	// aktuell zu zeichnende Spalte
	private int col;
    // Testkonfiguration, welche den Inhalt der Zelle enthaelt	
    private TestResultInfo tri;

    // Icons fuer die Tabelle
 	private Icon okIcon = new ImageIcon(
 	    Toolkit.getDefaultToolkit().createImage(
 	    	new File("icons\\accept_b.png").getAbsolutePath()));
 	private Icon failureIcon = new ImageIcon(
 	  	Toolkit.getDefaultToolkit().createImage(
 	  		new File("icons\\exclamation_b.png").getAbsolutePath()));
 	private Icon informationIcon = new ImageIcon(
 	 	Toolkit.getDefaultToolkit().createImage(
 	 		new File("icons\\information_d.png").getAbsolutePath()));
 
    /**
     * zeichnet die Umrandung einer Zelle
     */
    @Override
    public void paint(Graphics g)
    {
        g.drawLine(0, 0, 50, 15);
        super.paint(g);

        if (this.col > 0)
        {
            g.setColor(SpecialColor.LIGHTGRAY);
            for (int i=0; i<=36; i++)
            {
                if (i%4 == 0)
                  g.drawLine(0, i, 0, i+1);
            }
        }  
    }


    /**
     * liefert die darzustellende Zelle in Form eines JLabels zurueck
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
        boolean isSelected, boolean hasFocus, int row, int column)
    {
        TestResultInfo tri = (TestResultInfo) value;

        this.tri = tri;
        this.col = column;

        this.setBorder(new MatteBorder(0, 0, 1, 0, SpecialColor.LIGHTGRAY));

        this.setIcon(null);
        this.setToolTipText(null);
        this.setHorizontalAlignment(LEFT);
        this.setVerticalAlignment(SwingConstants.CENTER);
        this.setVerticalTextPosition(SwingConstants.CENTER);
        this.setForeground(SpecialColor.BLACK);

        if (column == 0)
        {
            this.setVerticalAlignment(SwingConstants.TOP);
            this.setVerticalTextPosition(SwingConstants.TOP);

            int success = this.tri.getSuccess();

            String message = Formatation.formatMessage(this.tri.getMessage());
            if (message == null || "".equals(message))
            {
                 message = "Fehler nicht genauer spezifiziert.";
            }

            if (success == 0)
            {
                if (!this.tri.isExceptionExpected())
                {
                    this.setIcon(okIcon);
                    String text = "<html><font color=\"#000000\">Test erfolgreich.<br>"
                        + "<font color=\"#888888\">Der Test verlief erfolgreich.</html>";
                    this.setText(text);

                    String tooltipText = "<html><font color=\"#000000\">Test erfolgreich. (ID "
                        + this.tri.getId()+" | "
                        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                            .format(this.tri.getDate())+")<br><font color=\"#888888\">"
                            + "Der Test verlief erfolgreich.</html>";
                    this.setToolTipText(tooltipText);
                }
                else
                {
                    this.setIcon(failureIcon);
                    String text = "<html><font color=\"#000000\">Exception nicht aufgetreten.<br>"
                        + "<font color=\"#888888\"> Die erwartete Exception trat nicht auf. </html>";
                    this.setText(text);

                    String tooltipText 
                        = "<html><font color=\"#000000\">Exception nicht aufgetreten. (ID "
                        + this.tri.getId()+" | "
                        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(this.tri.getDate())
                        + ")<br><font color=\"#888888\"> Die erwartete Exception trat nicht auf. </html>";
                    this.setToolTipText(tooltipText);
                }
            }
            else if (success == 1)
            {
                if (!this.tri.isExceptionExpected())
                {
                    this.setIcon(failureIcon);
                    String text = "<html><font color=\"#000000\">Test fehlgeschlagen.<br>"
                        + "<font color=\"#888888\">" + message + ". </html>";
                    this.setText(text);

                    String tooltipText = "<html><font color=\"#000000\">Test fehlgeschlagen. (ID "
                        + this.tri.getId()+" | "
                        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(this.tri.getDate())+")<br><font color=\"#888888\">"
                        + message + ". </html>";
                    this.setToolTipText(tooltipText);
                }
                else
                {
                    this.setIcon(failureIcon);
                    String text = "<html><font color=\"#000000\">Exception nicht aufgetreten.<br><"
                        + "font color=\"#888888\">Die erwartete Exception trat nicht auf. </html>";
                    this.setText(text);

                    String tooltipText = "<html><font color=\"#000000\">Exception nicht aufgetreten. (ID "
                        + this.tri.getId()+" | "
                        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(this.tri.getDate())
                        + ")<br><font color=\"#888888\">Die erwartete Exception trat nicht auf. </html>";
                    this.setToolTipText(tooltipText);
                }
            }
            else if (success == 2)
            {
                if (!this.tri.isExceptionExpected())
                {
                    this.setIcon(failureIcon);
                    String text = "<html><font color=\"#000000\">Exception aufgetreten.<br><font color=\"#888888\"> "
                        + "Der Test konnte nicht ausgeführt werden. </html>";
                    this.setText(text);

                    String tooltipText = "<html><font color=\"#000000\">Exception aufgetreten. (ID "
                        + this.tri.getId()+" | "
                        + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(this.tri.getDate())
                        + ")<br><font color=\"#888888\"> Der Test konnte nicht ausgeführt werden (" 
                        + message + ")" 
                        + ". </html>";
                    this.setToolTipText(tooltipText);
                }
                else
                {
                    this.setIcon(okIcon);
                    String text = "<html><font color=\"#000000\">Test erfolgreich.<br><font color=\"#888888\"> "
                        + "Eine Exception wurde geworfen. </html>";
                    this.setText(text);
                    String tooltipText = "<html><font color=\"#000000\">Test erfolgreich. (ID " 
                        + this.tri.getId()
                        + " | " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                        .format(this.tri.getDate())
                        + ")<br><font color=\"#888888\"> Eine Exception wurde geworfen (" 
                        + message + "). </html>";
                    this.setToolTipText(tooltipText);
                }
            }
            else if (success == TestResultInfo.TESTABORTED_BY_ASSUMPTION)
            {
                this.setIcon(informationIcon); 
                String text = "<html><font color=\"#000000\">Assumption nicht erfüllt.<br><font color=\"#888888\"> "
                        + "Der Testdurchlauf wurde abgebrochen. </html>";
                this.setText(text);

                String tooltipText = "<html><font color=\"#000000\">Assumption nicht erfüllt. (ID "
                    + this.tri.getId()+" | "
                    + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(this.tri.getDate())
                    + ")<br><font color=\"#888888\"> Der Test wurde abgebrochen ("
                    + message + ")"
                    + ". </html>";
                this.setToolTipText(tooltipText);
            }
            else if (success == TestResultInfo.TESTSKIPPED)
            {
                this.setIcon(informationIcon); 
                String text = "<html><font color=\"#000000\">Test ist disabled.<br><font color=\"#888888\"> "
                    + "Der Testdurchlauf wurde übersprungen. </html>";
                this.setText(text);

                String tooltipText = "<html><font color=\"#000000\">Test ist disabled. (ID "
                    + this.tri.getId()+" | "
                    + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
                    .format(this.tri.getDate())
                    + ")<br><font color=\"#888888\"> Der Testdurchlauf wurde übersprungen ("
                    + message + ")"
                    + ". </html>";
                this.setToolTipText(tooltipText);
            }
        }

        if (column > 0 && column < this.tri.getParameters().size() + 1)
        {
            String parametername = this.tri.getParameters().get(column-1).getName();
            int index = parametername.indexOf("_");
            if (parametername.substring(index + 1).length() >= 4
            	&& parametername.substring(index + 1, index + 5).equals("exp_") 
            	&& this.tri.isExceptionExpected())
            {
                this.setForeground(SpecialColor.LIGHTGRAY);
            }
            Object hilfe = this.tri.getParameters().get(column-1).getValue();
            String hilfeText;
            String hilfeToolTipText;
            if(hilfe == null)
            {	
                hilfeText = "null";
                hilfeToolTipText = "Der Wert existiert nicht.";
            }
            else if((hilfe + "").length() == 0)
            {
            	hilfeText = "\"\"";
            	hilfeToolTipText = "Der Wert ist ein leerer String.";
            }
            else
            {
            	hilfeText = hilfe.toString();
            	hilfeToolTipText = hilfeText;
            }
            this.setText(hilfeText);
            this.setToolTipText(hilfeToolTipText);
        }

        if (column == this.tri.getParameters().size() + 1)
        {
            if (this.tri.isExceptionExpected())
            {
                this.setText("✓");
            }
            else
            {
                this.setText("");
            }
            this.setHorizontalAlignment(CENTER);
        }

        this.setOpaque(true);

        if (isSelected)
        {
            this.setBackground(SpecialColor.WHITEGRAY);
        }
        else
        {
            ResultTableModel model = (ResultTableModel) table.getModel();
            if (model.isHighlightableTestResult(this.tri.getId()))
            {
                this.setBackground(SpecialColor.HIGHLIGHT);
            }
            else
            {
                this.setBackground(Color.white);
            }
        }
        return this;
    }
}