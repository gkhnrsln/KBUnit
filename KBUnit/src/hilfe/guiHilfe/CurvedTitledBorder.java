package hilfe.guiHilfe;

import javax.swing.border.AbstractBorder;
import java.awt.*;
  
/**
 * CurvedTitledBorder stellt einen Rahmen fuer Swing-Komponenten zur Verfuegung,
 * der einen Titel tragen kann und abgerundete Ecken besitzt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class CurvedTitledBorder extends AbstractBorder
{
   	private static final long serialVersionUID = 1L;
	// Rahmenfarbe
    private Color color;
    // Abstand der zu umgebenden Komponente vom Rahmen 
    // (soweit nicht anders gesetzt 10 Pixel)
    private int sinkLevel = 10;
    // anzuzeigender Titel
    private String title;
    // fester Wert, der garantiert, dass der Rahmen an der Unterseite genau an die 
    // Komponente anschliesst
    public int abstandUnten = 7;


    /**
     * Konstruktor - erstellt einen neuen Rahmen
     *
     * @param title anzuzeigender Titel
     * @param color Rahmenfarbe
     */
    public CurvedTitledBorder(String title, Color color)
    {
        this.title = title;
        this.color = color;
    }


    /**
     * Konstruktor - erstellt einen neuen Rahmen
     *
     * @param sinkLevel Abstand der zu umgebenden Komponente vom Rahmen
     * @param title anzuzeigender Titel
     * @param color Rahmenfarbe
     */
    public CurvedTitledBorder(int sinkLevel, String title, Color color)
    {
        this.sinkLevel = sinkLevel;
        this.title = title;
        this.color = color;
    }
   

    /**
     * zeichnet den Rahmen (ueberschrieben von AbstractBorder)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        g.setColor(new Color(Math.min(255, color.getRed() + 20),
            Math.min(255, color.getGreen() + 20), Math.min(255, color.getBlue() + 20)));
        g.fillRoundRect(x, y, w - 1, 15, 5, 5);
        g.setColor(Color.GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        // Text soll antialiased sein
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawString(title, 8, 12);        
        g.setColor(color);
        g.drawRoundRect(x, y, w - 1, h - abstandUnten, 5, 5);
    }


    /**
     * liefert die Abstaende des Rahmens zu der zu umgebenden Komponente 
     * (ueberschrieben von AbstractBorder)
     */
    @Override
    public Insets getBorderInsets(Component c)
    {
        return new Insets(sinkLevel + 14, sinkLevel, sinkLevel + 7, sinkLevel);
    }


    /**
     * liefert die Abstaende des Rahmens zu der zu umgebenden Komponente 
     * (ueberschrieben von AbstractBorder)
     */
    @Override
    public Insets getBorderInsets(Component c, Insets i)
    {
        i.left = i.right = i.bottom = i.top = sinkLevel;
        return i;
    }
}
