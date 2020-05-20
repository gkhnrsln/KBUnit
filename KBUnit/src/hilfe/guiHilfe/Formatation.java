package hilfe.guiHilfe;

import java.awt.*;

/**
 * Klasse, welche Formatierungsfunktionalitaeten zur Verfuegung stellt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */

public class Formatation {
    
      /**
     * entfernt Sonderzeichen und ersetzt englische durch deutsche Begriffe,
     * wird zur kundenfreundlichen Darstellung der Testergebnisse benoetigt
     *
     * @param message zu formatierender Text
     *
     * @return formatierter Text
     */
    public static String formatMessage (String message)
    {
        String m = message;
        if (m.contains("expected"))
        {
            m = m.replaceAll("<", " ");
            m = m.replaceAll(">", "");
            m = m.replaceAll("expected:", " (expected:");
            m = m.replaceAll("expected same:", "(expected same:");
            m = m + ")";
        }
        return m;
    }


    /**
     * ermittelt die Breite des uebergebenen Textes in Pixel
     *
     * @param text Text, dessen Breite ermittelt werden soll
     * @param font Schriftart des Textes
     *
     * @return Breite des Textes in Pixel
     */
    public static int getTextWidthInPixel(String text, Font font)
    {
        @SuppressWarnings("deprecation")
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);
        char[] cText = text.toCharArray();
        return fm.charsWidth(cText,0,cText.length);
    }


    /**
     * kuerzt einen uebergebenen Text auf die gewuenschte Laenge in Pixel
     *
     * @param text Text, welcher gekuerzt werden soll
     * @param font Schriftart des Textes
     * @param pixel gewuenschte maximale Breite des Textes in Pixel
     *
     * @return gekuerzter Text
     */
    public static String limitTextToWidthInPixel(String text, Font font, int pixel)
    {
        String target = text;
        int textPixelWidth = getTextWidthInPixel(text, font);
        int targetPixelWidth = pixel;
        // der Text muss nur gekuerzt werden, wenn seine Breite die gewuenschte 
        // Maximalbreite uebersteigt
        if(textPixelWidth > targetPixelWidth)
        {
            double factor = (double) textPixelWidth / targetPixelWidth;
            target = text.substring(0, (int) Math.round(text.length() / factor));
            while(getTextWidthInPixel(target, font) > targetPixelWidth)
            {
                target = target.substring(0, target.length()-2);
            }
            target = target.substring(0, target.length()-4);
            target = target + "...";
        }
        return target;
    }


    /**
     * ueberprueft, ob der uebergebene Parametertyp von KBUnit unterstuetzt wird
     *
     * @param type zu pruefender Parametertyp
     *
     * @return Ergebnis der Pruefung
     */
    public static boolean parameterTypeSupported(Class<?> type)
    {
        if (type == int.class || type == byte.class || type == long.class 
            || type == short.class || type == float.class || type == double.class 
            || type == boolean.class || type == char.class || type == java.lang.String.class)
        {
            return true;
        }
        else
        {
            return false;
        }

    }


    /**
     * ueberprueft ob zwei Objekte gleich oder beide null sind
     *
     * @param x Objekt 1
     * @param y Objekt 2
     * @return Ergebnis der Pruefung
     */
    public static boolean bothNullOrEqual(Object x, Object y)
    {
        return (x == y || (x != null && x.equals(y)));
    }


    public static double roundToOneDecimal(double d)
    {
        double beforeCommaPart = Math.floor(d);
        double afterCommaPart = d % beforeCommaPart;
        afterCommaPart = Math.round(afterCommaPart*10)/10.0;

        return beforeCommaPart+afterCommaPart;
    }
    
}
