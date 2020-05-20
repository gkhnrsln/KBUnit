package main.panel.testDurchfuehren.konfigurationsgenerator;

import hilfe.guiHilfe.Formatation;
import hilfe.guiHilfe.ImageTextField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * ParamIntervalVerifier ist ein InputVerifier fuer Parametereingaben, der sowohl
 * Einzel- als auch (fuer Zahlentypen) Intervallangaben unterstuetzt; die Klasse
 * wird verwendet um die Nutzereingaben im Konfigurationsgenerator zu ueberpruefen
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class ParamIntervalVerifier extends InputVerifier
{
	// Klasse zum Parameter
    private Class<?> paramType;
    
    // Liste von Werten ausserhalb des gueltigen Bereichs
    ArrayList<Object> invalidParameterValues = new ArrayList<Object>();
    // Liste von Werten innerhalb des gueltigen Bereichs 
    ArrayList<Object> validParameterValues   = new ArrayList<Object>();
    // Liste von Werten auf der Grenze des gueltigen Bereichs
    ArrayList<Object> limitParameterValues   = new ArrayList<Object>();
    // speichert das Ergebnis der Ueberpruefung der Nutzereingabe
    private boolean verificationResult = true;


    /**
     * Konstruktor, erwartet bereits die Eingabe des Datentyps des Parameters
     *
     * @param paramType gibt den Datentyp des Parameters an, dessen Wert
     *   validiert werden soll
     */
    public ParamIntervalVerifier(Class<?> paramType)
    {
        this.paramType = paramType;
    }


    /**
     * liefert das Ergebnis der letzten Pruefung des zuletzt eingegebenen
     * Parameterwertes zurueck
     *
     * @return Ergebnis der letzten Pruefung
     */
    public boolean getLastVerificationResult()
    {
        return this.verificationResult;
    }


    /**
     * liefert die aus der Eingabe extrahierten validen Parameterwerte zurueck
     *
     * @return valide Parameterwerte aus der Nutzereingabe
     */
    public ArrayList<Object> getValidParameterValues()
    {
        return this.validParameterValues;
    }


    /**
     * liefert eingegebene Intervallgrenzen zurueck
     *
     * @return Intervallgrenzen aus der Nutzereingabe
     */
    public ArrayList<Object> getLimitParameterValues()
    {
        return this.limitParameterValues;
    }


    /**
     * liefert als ungueltig interpretierte Parameterwerte zurueck
     *
     * @return ungueltige Parameterwerte, interpretiert aus der Nutzereingabe
     */
    public ArrayList<Object> getInvalidParameterValues()
    {
        return this.invalidParameterValues;
    }


    /**
     * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
     * Zeichenkette
     *
     * @param input Eingabekomponente, deren Inhalt verifiziert werden soll
     *
     * @return Ergebnis der Verifizierung
     */
    @Override
    public boolean verify(JComponent input)
    {
        ImageTextField source = (ImageTextField)input;
        String text = source.getText();

        boolean isResultParameter = false;
        String paramName = source.getName();
        int index = paramName.indexOf("_");
        if(paramName.substring(index+1).length() >= 4
        	&& paramName.substring(index+1, index+5).equals("exp_"))
        {
            isResultParameter = true;
        }
        
        this.validParameterValues.clear();
        this.limitParameterValues.clear();
        this.invalidParameterValues.clear();

        // Pruefen von Zahleneingaben (mehrere Zahlen koennen durch Semikolon 
        // getrennt sein, ausserdem koennen Intervalle eingegeben werden)
        if (this.paramType == Integer.class || this.paramType == Byte.class  
            || this.paramType == Short.class || this.paramType == Long.class 
            || this.paramType == Float.class || this.paramType == Double.class)
        {
            this.verifyNumber(text, isResultParameter);
        }
        // BOOLEAN
        else if(this.paramType == boolean.class)
        {
            this.verifyBoolean(text);
        }    
        // CHAR
        else if(this.paramType == char.class)
        {
            this.verifyChar(text);
        }
        // STRING
        // Semikolons sind Sonderzeichen und trennen Strings (soll ein String 
        // ein Semikolon enthalten, so ist \; statt ; zu schreiben)
        // null ist Schluesselwort und stellt null-String dar (soll String die 
        // Zeichenfolge null enthalten, so ist \null statt null zu schreiben)
        else if(this.paramType == java.lang.String.class)
        {
            this.verifyString(text);
        }    
  
        if (this.verificationResult == true)
        {
            // Duplikate innerhalb der drei Listen entfernen
            HashSet<Object> h;
            h = new HashSet<Object>(this.validParameterValues);
            this.validParameterValues.clear();
            this.validParameterValues.addAll(h);
            h = new HashSet<Object>(this.invalidParameterValues);
            this.invalidParameterValues.clear();
            this.invalidParameterValues.addAll(h);
            h = new HashSet<Object>(this.limitParameterValues);
            this.limitParameterValues.clear();
            this.limitParameterValues.addAll(h);

            // Inhalte der Invalid- und Grenzwert-Liste werden entfernt, 
            // wenn sie bereits in der Liste valider Werte enthalten sind
            for (int i = 0; i < this.validParameterValues.size(); i++)
            {
                Object validEntry = this.validParameterValues.get(i);

                for (int j=0; j<this.limitParameterValues.size(); j++)
                {
                    Object limitEntry = this.limitParameterValues.get(j);
                    if (Formatation.bothNullOrEqual(validEntry,limitEntry))
                    {
                        this.limitParameterValues.remove(j);
                    }
                }

                for (int j=0; j<this.invalidParameterValues.size(); j++)
                {
                    Object invalidEntry = this.invalidParameterValues.get(j);
                    if (Formatation.bothNullOrEqual(validEntry,invalidEntry))
                    {
                        this.invalidParameterValues.remove(j);
                    }
                }
            }
        }
        source.showAlertSymbol(!this.verificationResult);
        return this.verificationResult;
    }
    
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen
    */
    private void verifyNumber(String text, boolean isResultParameter)
    {
        ArrayList<String> entries = new ArrayList<>();
        String rest = text;
        int startIndex = 0;
        int endIndex;
        while(rest.indexOf(";")>=0)
        {
            endIndex = rest.indexOf(";");
            entries.add(rest.substring(startIndex,endIndex));
            rest = rest.substring(endIndex+1);
        }
        entries.add(rest);
        // INTEGER
        if (this.paramType == Integer.class)
        {
            this.verifyInt(entries, isResultParameter);
        }
        // BYTE
        else if(this.paramType == Byte.class)
        {
            this.verifyByte(entries, isResultParameter);
        }
        // SHORT
        else if(this.paramType == Short.class)
        {
            this.verifyShort(entries, isResultParameter);  
        }
        // LONG
        else if(this.paramType == Long.class)
        {
            this.verifyLong(entries, isResultParameter);
        }    
        // FLOAT
        else if(this.paramType == Float.class)
        {
            this.verifyFloat(entries, isResultParameter);
        }
        // DOUBLE
        else if(this.paramType == Double.class)
        {
            this.verifyDouble(entries, isResultParameter);
        }    
    }
            
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell int
    */
    private void verifyInt(ArrayList<String> entries, boolean isResultParameter)
    {
        int precision         = 1;
        int negativeInfinity  = Integer.MIN_VALUE;
        int positiveInfinity  = Integer.MAX_VALUE;
        try
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
            // extrahieren und ablegen
            for (int i=0; i < entries.size(); i++)
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval    = false;
                try
                {
                    int value = Integer.parseInt(entries.get(i));
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (value > ((Integer) this.limitParameterValues
                        	.get(this.limitParameterValues.size()-1)))
                        {
                            this.validParameterValues.add(value);
                            // spaeter werden die this.limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, um Werte zwischen value und den naechsten 
                            // Intervallen zu ermitteln
                            if (!isResultParameter)
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        }
                        else
                        {
                            throw new Exception();
                        }
                    }
                    else
                    {
                        this.validParameterValues.add(value);
                        // spaeter werden die this.limitParameterValues wieder entfernt, 
                        // sie werden benoetigt, 
                        // um Werte zwischen value und den naechsten intervallen zu ermitteln
                        if (!isResultParameter)
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                }
                catch (Exception e)
                {
                    singleValue = false;
                }
                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen 
                // und entsprechend umwandeln
                if (singleValue == false)
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]")))
                    {
                        throw new Exception();
                    }
                    int commaIndex = entry.indexOf(",");
                    int lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i"))
                    {
                        lowerValue = negativeInfinity;
                    }
                    else
                    {
                        lowerValue = Integer.parseInt(lowerValueString);
                    }
                    int higherValue;
                    String higherValueString = entry.substring(commaIndex+1,entry.length()-1);
                    if (higherValueString.equals("i"))
                    {
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        higherValue = Integer.parseInt(higherValueString);
                    }
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (higherValue >= lowerValue + 2*precision 
                            && lowerValue >= ((Integer) this.limitParameterValues
                            .get(this.limitParameterValues.size()-1)))
                        {
                            interval = true;
                        }
                    }
                    else
                    {
                        if (higherValue >= lowerValue+2*precision)
                        {
                            interval = true;
                        }
                    }
                    if (!interval)
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig 
                    	// formatierter Intervall noch Einzelwert vorliegt
                        throw new Exception();
                    }
                    else
                    {
                        // Intervallgrenzen hinzufuegen
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);
                        // um Ueberlaeufe zu vermeiden, wird der lowerValue 
                        // im Bedarfsfall geaendert (
                        // nur notwendig, wenn lowerValue negativ und higherValue positiv und der 
                        // Abstand zwischen beiden den positiven Wertebereich sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0)
                        {
                            lowerValue = negativeInfinity+precision;
                        }
                        if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue) < higherValue)
                        {
                            lowerValue = higherValue-positiveInfinity;
                        }
                        int validParameterValueBetweenLimits 
                            = (int) ( (int) (Math.random()*(higherValue - lowerValue - precision)))
                            + precision + lowerValue;
                        // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                        while (validParameterValueBetweenLimits/10 > lowerValue 
                            && validParameterValueBetweenLimits/10 < higherValue 
                            && Math.abs(validParameterValueBetweenLimits) > 1000)
                        {
                            validParameterValueBetweenLimits = validParameterValueBetweenLimits/10;
                        }
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // gueltige Werte ausserhalb der Intervallgrenzen bestimmen 
            // (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0)
            {
                for (int i = 0; i<=this.limitParameterValues.size(); i+=2)
                {
                    int lowerValue;
                    int higherValue;
                    if (i == 0)
                    {
                        lowerValue  = negativeInfinity;
                        higherValue = (Integer) this.limitParameterValues.get(i);
                    }
                    else if (i == this.limitParameterValues.size())
                    {
                        lowerValue = (Integer) this.limitParameterValues.get(i-1);
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        lowerValue = (Integer) this.limitParameterValues.get(i-1);
                        higherValue = (Integer) this.limitParameterValues.get(i);
                    }
        
                    if (lowerValue == negativeInfinity && higherValue >= 0)
                    {
                        lowerValue = negativeInfinity+precision;
                    }
                    if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                    {
                        lowerValue = higherValue-positiveInfinity;
                    }
                    int invalidParameterValueOutOfLimits 
                        = (int) ( (int) (Math.random()*(higherValue-lowerValue-precision)))
                        + precision+lowerValue;
                    // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                    while (invalidParameterValueOutOfLimits/10 > lowerValue 
                        && invalidParameterValueOutOfLimits/10 < higherValue 
                        && Math.abs(invalidParameterValueOutOfLimits)>1000)
                    {
                        invalidParameterValueOutOfLimits = invalidParameterValueOutOfLimits/10;
                    }
                    if (invalidParameterValueOutOfLimits > lowerValue 
                        && invalidParameterValueOutOfLimits < higherValue)
                    {
                        this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                    }
                }
            }
            this.verificationResult = true;
        }
        catch (Exception e)
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }
  
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell byte
    */
    private void verifyByte(ArrayList<String> entries, boolean isResultParameter)
    {
        byte precision         = 1;
        byte negativeInfinity  = Byte.MIN_VALUE;
        byte positiveInfinity  = Byte.MAX_VALUE;
        try
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
        	// extrahieren und ablegen
            for (int i=0; i < entries.size(); i++)
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval    = false;
                try
                {
                    byte value = Byte.parseByte(entries.get(i));
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (value > ((Byte) this.limitParameterValues
                        	.get(this.limitParameterValues.size()-1)))
                        {
                            this.validParameterValues.add(value);
                            // spaeter werden die this.limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, um Werte zwischen value und den naechsten 
                            // Intervallen zu ermitteln
                            if (!isResultParameter)
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        }
                        else
                        {
                            throw new Exception();
                        }
                    }
                    else
                    {
                        this.validParameterValues.add(value);
                        // spaeter werden die limitParameterValues wieder entfernt, 
                        // sie werden benoetigt, 
                        // um Werte zwischen value und den naechsten intervallen zu ermitteln
                        if (!isResultParameter)
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                }
                catch (Exception e)
                {
                    singleValue = false;
                }
                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen und 
                // entsprechend umwandeln
                if (singleValue == false)
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]")))
                    {
                        throw new Exception();
                    }
                    int commaIndex = entry.indexOf(",");
                    byte lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i"))
                    {
                        lowerValue = negativeInfinity;
                    }
                    else
                    {
                        lowerValue = Byte.parseByte(lowerValueString);
                    }
                    byte higherValue;
                    String higherValueString = entry.substring(commaIndex+1,entry.length()-1);
                    if (higherValueString.equals("i"))
                    {
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        higherValue = Byte.parseByte(higherValueString);
                    }
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (higherValue >= lowerValue + 2*precision 
                            && lowerValue >= ((Byte) this.limitParameterValues
                            .get(this.limitParameterValues.size()-1)))
                        {
                            interval = true;
                        }
                    }
                    else
                    {
                        if (higherValue >= lowerValue+2*precision)
                        {
                            interval = true;
                        }
                    }
                    if (!interval)
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig formatierter Intervall 
                        // noch Einzelwert vorliegt
                        throw new Exception();
                    }
                    else
                    {
                        // Intervallgrenzen hinzufuegen
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);
                        // um Überlaeufe zu vermeiden, wird der lowerValue im Bedarfsfall geaendert 
                        // (nur notwendig, wenn lowerValue negativ und higherValue positiv und der 
                        // Abstand zwischen beiden den positiven Wertebereich sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0)
                        {
                            lowerValue = (byte) (negativeInfinity + precision);
                        }
                        if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                        {
                            lowerValue = (byte) (higherValue - positiveInfinity);
                        }
                        byte validParameterValueBetweenLimits 
                            = (byte) ( (byte) ((Math.random()*(higherValue-lowerValue-precision)))
                            + precision+lowerValue);
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // gueltige Werte ausserhalb der Intervallgrenzen bestimmen (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0)
            {
                for (int i = 0; i<=this.limitParameterValues.size(); i+=2)
                {
                    byte lowerValue;
                    byte higherValue;
                    if (i == 0)
                    {
                        lowerValue  = negativeInfinity;
                        higherValue = (Byte) this.limitParameterValues.get(i);
                    }
                    else if (i == this.limitParameterValues.size())
                    {
                        lowerValue = (Byte) this.limitParameterValues.get(i-1);
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        lowerValue = (Byte) this.limitParameterValues.get(i-1);
                        higherValue = (Byte) this.limitParameterValues.get(i);
                    }
                    // Zufallswert zwischen den Intervallgrenzen ermitteln und hinzufuegen, 
                    // wenn zwischen beiden Werten genug Platz ist
                    if (lowerValue == negativeInfinity && higherValue >= 0)
                    {
                        lowerValue = (byte) (negativeInfinity + precision);
                    }
                    if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                    {
                        lowerValue = (byte) (higherValue - positiveInfinity);
                    }
                    byte invalidParameterValueOutOfLimits 
                        = (byte) (( (byte) ((Math.random()*(higherValue-lowerValue-precision))))
                        + precision + lowerValue);
                    this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                }
            }
            this.verificationResult = true;
        }
        catch (Exception e)
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }        
    
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell short
    */
    private void verifyShort(ArrayList<String> entries, boolean isResultParameter)
    {
        this.verifyShort(entries, isResultParameter);
        short precision         = 1;
        short negativeInfinity  = Short.MIN_VALUE;
        short positiveInfinity  = Short.MAX_VALUE;
        try
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
        	// extrahieren und ablegen
            for (int i=0; i<entries.size(); i++)
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval    = false;
                try
                {
                    short value = Short.parseShort(entries.get(i));
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (value > ((Short) this.limitParameterValues
                        	.get(this.limitParameterValues.size()-1)))
                        {
                            this.validParameterValues.add(value);
                            // spaeter werden die this.limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, um Werte zwischen value und den naechsten 
                            // Intervallen zu ermitteln
                            if (!isResultParameter)
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        }
                        else
                        {
                            throw new Exception();
                        }
                    }
                    else
                    {
                        this.validParameterValues.add(value);
                        // spaeter werden die limitParameterValues wieder entfernt, 
                        // sie werden benoetigt, 
                        // um Werte zwischen value und den naechsten intervallen zu ermitteln
                        if (!isResultParameter)
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                }
                catch (Exception e)
                {
                    singleValue = false;
                }
                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen und 
                // entsprechend umwandeln
                if (singleValue == false)
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]")))
                    {
                        throw new Exception();
                    }
                    int commaIndex = entry.indexOf(",");
                    short lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i"))
                    {
                        lowerValue = negativeInfinity;
                    }
                    else
                    {
                        lowerValue = Short.parseShort(lowerValueString);
                    }
                    short higherValue;
                    String higherValueString = entry.substring(commaIndex+1,entry.length()-1);
                    if (higherValueString.equals("i"))
                    {
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        higherValue = Short.parseShort(higherValueString);
                    }
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (higherValue >= lowerValue + 2 * precision 
                            && lowerValue >= ((Short) this.limitParameterValues
                            .get(this.limitParameterValues.size()-1)))
                        {
                            interval = true;
                        }
                    }
                    else
                    {
                        if (higherValue >= lowerValue+2*precision)
                        {
                            interval = true;
                        }
                    }
                    if (!interval)
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig formatierter Intervall 
                        // noch Einzelwert vorliegt
                        throw new Exception();
                    }
                    else
                    {
                        // Intervallgrenzen hinzufuegen
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);
                        // um Ueberlaeufe zu vermeiden, wird der lowerValue im Bedarfsfall geaendert 
                        // (nur notwendig, wenn lowerValue negativ und higherValue positiv und der 
                        // Abstand zwischen beiden den positiven Wertebereich sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0)
                        {
                            lowerValue = (short) (negativeInfinity + precision);
                        }
                        if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                        {
                            lowerValue = (short) (higherValue - positiveInfinity);
                        }
                        short validParameterValueBetweenLimits 
                            = (short) ( (short) ((Math.random()*(higherValue-lowerValue-precision)))
                            + precision+lowerValue);
                        // ermittelten gueltigen Wert so stark im Betrag verringern wie nur moeglich
                        while (validParameterValueBetweenLimits/10 > lowerValue 
                            && validParameterValueBetweenLimits/10 < higherValue 
                            && Math.abs(validParameterValueBetweenLimits)>1000)
                        {
                            validParameterValueBetweenLimits 
                                = (short) (validParameterValueBetweenLimits/10);
                        }
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // gueltige Werte ausserhalb der Intervallgrenzen bestimmen 
            // (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0)
            {
                for (int i = 0; i<=this.limitParameterValues.size(); i+=2)
                {
                    short lowerValue;
                    short higherValue;
                    if (i == 0)
                    {
                        lowerValue  = negativeInfinity;
                        higherValue = (Short) this.limitParameterValues.get(i);
                    }
                    else if (i == this.limitParameterValues.size())
                    {
                        lowerValue = (Short) this.limitParameterValues.get(i-1);
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        lowerValue = (Short) this.limitParameterValues.get(i-1);
                        higherValue = (Short) this.limitParameterValues.get(i);
                    }
                    // Zufallswert zwischen den Intervallgrenzen ermitteln und hinzufuegen, 
                    // wenn zwischen beiden Werten genug Platz ist
                    if (lowerValue == negativeInfinity && higherValue >= 0)
                    {
                        lowerValue = (short) (negativeInfinity + precision);
                    }
                    if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue) < higherValue)
                    {
                        lowerValue = (short) (higherValue - positiveInfinity);
                    }
                    short invalidParameterValueOutOfLimits 
                        = (short) (( (short) ((Math.random()*(higherValue-lowerValue-precision))))
                        + precision+lowerValue);
                    // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                    while (invalidParameterValueOutOfLimits/10 > lowerValue 
                        && invalidParameterValueOutOfLimits/10 < higherValue 
                        && Math.abs(invalidParameterValueOutOfLimits)>1000)
                    {
                        invalidParameterValueOutOfLimits 
                            = (short) (invalidParameterValueOutOfLimits/10);
                    }
                    this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                }
            }
            this.verificationResult = true;
        }
        catch (Exception e)
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }   
    
    
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell long
    */
    private void verifyLong(ArrayList<String> entries, boolean isResultParameter)
    {        
        long precision         = 1;
        long negativeInfinity  = Long.MIN_VALUE;
        long positiveInfinity  = Long.MAX_VALUE;
        try
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
        	// extrahieren und ablegen
            for (int i=0; i<entries.size(); i++)
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval    = false;
                try
                {
                    long value = Long.parseLong(entries.get(i));
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (value > ((Long) this.limitParameterValues
                        	.get(this.limitParameterValues.size()-1)))
                        {
                            this.validParameterValues.add(value);
                            // spaeter werden die limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, um Werte zwischen value und den naechsten 
                            // Intervallen zu ermitteln
                            if (!isResultParameter)
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        }
                        else
                        {
                            throw new Exception();
                        }
                    }
                    else
                    {
                        this.validParameterValues.add(value);
                        // spaeter werden die limitParameterValues wieder entfernt, 
                        // sie werden benoetigt, um Werte zwischen value und den naechsten 
                        // Intervallen zu ermitteln
                        if (!isResultParameter)
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                }
                catch (Exception e)
                {
                    singleValue = false;
                }
                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen 
                // und entsprechend umwandeln
                if (singleValue == false)
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]")))
                    {
                        throw new Exception();
                    }
                    int commaIndex = entry.indexOf(",");
                    long lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i"))
                    {
                        lowerValue = negativeInfinity;
                    }
                    else
                    {
                        lowerValue = Long.parseLong(lowerValueString);
                    }
                    long higherValue;
                    String higherValueString = entry.substring(commaIndex + 1,entry.length() - 1);
                    if (higherValueString.equals("i"))
                    {
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        higherValue = Long.parseLong(higherValueString);
                    }
                    if (this.limitParameterValues.size() > 0)
                    {
                        if (higherValue >= lowerValue+2*precision 
                            && lowerValue >= ((Long) this.limitParameterValues
                            .get(this.limitParameterValues.size()-1)))
                        {
                            interval = true;
                        }
                    }
                    else
                    {
                        if (higherValue >= lowerValue+2 * precision)
                        {
                            interval = true;
                        }
                    }
                    if (!interval)
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig formatierter 
                        // Intervall noch Einzelwert vorliegt
                        throw new Exception();
                    }
                    else
                    {
                        // Intervallgrenzen hinzufuegen
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);

                        // um Ueberlaeufe zu vermeiden, wird der lowerValue im Bedarfsfall 
                        // geaendert (nur notwendig, wenn lowerValue negativ und higherValue 
                        // positiv und der Abstand zwischen beiden den positiven Wertebereich sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0)
                        {
                            lowerValue = (long) (negativeInfinity + precision);
                        }
                        if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                        {
                            lowerValue = (long) (higherValue - positiveInfinity);
                        }
                        long validParameterValueBetweenLimits 
                            = (long) ( (long) ((Math.random()*(higherValue-lowerValue-precision)))
                            + precision + lowerValue);
                        // ermittelten gueltigen Wert so stark im Betrag verringern wie nur moeglich
                        while (validParameterValueBetweenLimits/10 > lowerValue 
                            && validParameterValueBetweenLimits/10 < higherValue 
                            && Math.abs(validParameterValueBetweenLimits)>1000)
                        {
                            validParameterValueBetweenLimits = validParameterValueBetweenLimits/10;
                        }
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // gueltige Werte ausserhalb der Intervallgrenzen bestimmen 
            // (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0)
            {
                for (int i = 0; i<=this.limitParameterValues.size(); i+=2)
                {
                    long lowerValue;
                    long higherValue;
                    if (i == 0)
                    {
                        lowerValue  = negativeInfinity;
                        higherValue = (Long) this.limitParameterValues.get(i);
                    }
                    else if (i == this.limitParameterValues.size())
                    {
                        lowerValue = (Long) this.limitParameterValues.get(i-1);
                        higherValue = positiveInfinity;
                    }
                    else
                    {
                        lowerValue = (Long) this.limitParameterValues.get(i-1);
                        higherValue = (Long) this.limitParameterValues.get(i);
                    }
                    // Zufallswert zwischen den Intervallgrenzen ermitteln und hinzufuegen, 
                    // wenn zwischen beiden Werten genug Platz ist
                    if (lowerValue == negativeInfinity && higherValue >= 0)
                    {
                        lowerValue = (long) (negativeInfinity + precision);
                    }
                    if (lowerValue <= 0 && positiveInfinity-Math.abs(lowerValue)<higherValue)
                    {
                        lowerValue = (long) (higherValue - positiveInfinity);
                    }
                    long invalidParameterValueOutOfLimits 
                        = (long) (( (long) ((Math.random()*(higherValue-lowerValue-precision))))
                        + precision + lowerValue);
                    // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                    while (invalidParameterValueOutOfLimits/10 > lowerValue 
                        && invalidParameterValueOutOfLimits/10 < higherValue 
                        && Math.abs(invalidParameterValueOutOfLimits)>1000)
                    {
                        invalidParameterValueOutOfLimits = invalidParameterValueOutOfLimits/10;
                    }
                    this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                }
            }
            this.verificationResult = true;
        }
        catch (Exception e)
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }    
    
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell float
    */  
    private void verifyFloat(ArrayList<String> entries, boolean isResultParameter)
    {        
        float precision         = 0.0001f;
        float negativeInfinity  = -Float.MAX_VALUE;
        float positiveInfinity  = Float.MAX_VALUE;

        try
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
            // extrahieren und ablegen
            for (int i = 0; i < entries.size(); i++) 
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval = false;
                try 
                {
                    float value = Float.parseFloat(entries.get(i));

                    if (this.limitParameterValues.size() > 0) 
                    {
                        if (value > ((Float) this.limitParameterValues
                        	.get(this.limitParameterValues.size() - 1))) 
                        {
                            if (Float.isInfinite(value) || Float.isNaN(value)) 
                            {
                                // diese Exception wird nicht im naechsten Catch sondern erst am 
                                // Ende der Methode abgefangen
                                throw new Exception();
                            }
                            this.validParameterValues.add(value);
                            // spaeter werden die limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, 
                            // um Werte zwischen value und den naechsten intervallen zu ermitteln
                            if (!isResultParameter) 
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        } 
                        else 
                        {
                            throw new Exception();
                        }
                    } 
                    else 
                    {
                        if (Float.isInfinite(value) || Float.isNaN(value)) 
                        {
                            // diese Exception wird nicht im naechsten Catch sondern erst  
                            // am Ende der Methode abgefangen
                            throw new Exception();
                        }
                        this.validParameterValues.add(value);
                        // spaeter werden die limitParameterValues wieder entfernt, 
                        // sie werden benoetigt, 
                        // um Werte zwischen value und den naechsten intervallen zu ermitteln
                        if (!isResultParameter) 
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                } 
                catch(NumberFormatException nfe) 
                {
                    singleValue = false;
                }

                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen und 
                // entsprechend umwandeln
                if (singleValue == false) 
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]"))) 
                    {
                        throw new Exception();
                    }

                    int commaIndex = entry.indexOf(",");

                    float lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i")) 
                    {
                        lowerValue = negativeInfinity;
                    } 
                    else 
                    {
                        lowerValue = Float.parseFloat(lowerValueString);
                    }

                    float higherValue;
                    String higherValueString 
                        = entry.substring(commaIndex + 1, entry.length() - 1);
                    if (higherValueString.equals("i")) 
                    {
                        higherValue = positiveInfinity;
                    } 
                    else 
                    {
                        higherValue = Float.parseFloat(higherValueString);
                    }

                    if (this.limitParameterValues.size() > 0) 
                    {
                        if (higherValue >= lowerValue + 2 * precision
                            && lowerValue >= ((Float) this.limitParameterValues
                            .get(this.limitParameterValues.size() - 1))) 
                        {
                            interval = true;
                        }
                    } 
                    else 
                    {
                        if (higherValue >= lowerValue + 2 * precision) 
                        {
                            interval = true;
                        }
                    }
                    if (!interval) 
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig formatierter 
                        // Intervall noch Einzelwert vorliegt
                        throw new Exception();
                    } 
                    else 
                    {
                        // Intervallgrenzen hinzufuegen, aber nur, wenn diese keine 
                        // Float-Sonderwerte enthalten
                        if (Float.isInfinite(lowerValue) || Float.isNaN(lowerValue)
                                || Float.isInfinite(higherValue) || Float.isNaN(higherValue)) 
                        {
                            throw new Exception();
                        }
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);

                        // um Überlaeufe zu vermeiden, wird der lowerValue im Bedarfsfall 
                        // geaendert (nur notwendig, wenn lowerValue negativ und higherValue 
                        // positiv und der Abstand zwischen beiden den positiven Wertebereich 
                        // sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0) 
                        {
                            lowerValue = (float) (negativeInfinity + precision);
                        }
                        if (lowerValue <= 0 && positiveInfinity - Math.abs(lowerValue) < higherValue) 
                        {
                            lowerValue = (float) (higherValue - positiveInfinity);
                        }
                        float validParameterValueBetweenLimits 
                            = (float) ((float) ((Math.random() * (higherValue - lowerValue - precision)))
                            + precision + lowerValue);
                        // ermittelten gueltigen Wert so stark im Betrag verringern wie nur moeglich
                        while (validParameterValueBetweenLimits / 10 > lowerValue
                                && validParameterValueBetweenLimits / 10 < higherValue
                                && Math.abs(validParameterValueBetweenLimits) > 1000) 
                        {
                            validParameterValueBetweenLimits = validParameterValueBetweenLimits / 10;
                        }
                        // ermittelten gueltigen Wert auf eine Nachkommastelle runden, falls moeglich
                        float roundedValue 
                            = (float) (Formatation.roundToOneDecimal(validParameterValueBetweenLimits));
                        if (roundedValue > lowerValue && roundedValue < higherValue) 
                        {
                            validParameterValueBetweenLimits = roundedValue;
                        }
                        if (Float.isInfinite(validParameterValueBetweenLimits)
                                || Float.isNaN(validParameterValueBetweenLimits)) 
                        {
                            throw new Exception();
                        }
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // ungueltige Werte ausserhalb der Intervallgrenzen bestimmen 
            // (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0) 
            {
                for (int i = 0; i <= this.limitParameterValues.size(); i += 2) 
                {
                    float lowerValue;
                    float higherValue;

                    if (i == 0) 
                    {
                        lowerValue = negativeInfinity;
                        higherValue = (Float) this.limitParameterValues.get(i);
                    } 
                    else if (i == this.limitParameterValues.size()) 
                    {
                        lowerValue = (Float) this.limitParameterValues.get(i - 1);
                        higherValue = positiveInfinity;
                    } 
                    else 
                    {
                        lowerValue = (Float) this.limitParameterValues.get(i - 1);
                        higherValue = (Float) this.limitParameterValues.get(i);
                    }
                    // Zufallswert zwischen den Intervallgrenzen ermitteln und hinzufuegen, 
                    // wenn zwischen beiden Werten genug Platz ist
                    if (lowerValue == negativeInfinity && higherValue >= 0) 
                    {
                        lowerValue = (float) (negativeInfinity + precision);
                    }
                    if (lowerValue <= 0 && positiveInfinity - Math.abs(lowerValue) < higherValue) {
                        lowerValue = (float) (higherValue - positiveInfinity);
                    }
                    float invalidParameterValueOutOfLimits 
                        = (float) (((float) ((Math.random() 
                        * (higherValue - lowerValue - precision))))
                        + precision + lowerValue);
                    // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                    while (invalidParameterValueOutOfLimits / 10 > lowerValue
                            && invalidParameterValueOutOfLimits / 10 < higherValue
                            && Math.abs(invalidParameterValueOutOfLimits) > 1000) 
                    {
                        invalidParameterValueOutOfLimits = invalidParameterValueOutOfLimits / 10;
                    }
                    // ermittelten ungueltigen Wert auf eine Nachkommastelle runden, falls moeglich
                    float roundedValue 
                        = (float) (Formatation.roundToOneDecimal(invalidParameterValueOutOfLimits));
                    if (roundedValue > lowerValue && roundedValue < higherValue) 
                    {
                        invalidParameterValueOutOfLimits = roundedValue;
                    }
                    if (Float.isInfinite(invalidParameterValueOutOfLimits)
                            || Float.isNaN(invalidParameterValueOutOfLimits)) 
                    {
                        throw new Exception();
                    }
                    this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                }
            }
            this.verificationResult = true;
        } 
        catch (Exception e) 
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }
 
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Zahlen, speziell double
    */  
    private void verifyDouble(ArrayList<String> entries, boolean isResultParameter) 
    {
        double precision = 0.0000001;
        double negativeInfinity = -Double.MAX_VALUE;
        double positiveInfinity = Double.MAX_VALUE;

        try 
        {
            // alle Eintraege durchlaufen, Einzelwerte und Intervallgrenzen 
            // extrahieren und ablegen
            for (int i = 0; i < entries.size(); i++) 
            {
                // erster Versuch: gefundenen Eintrag in Zahlenwert umwandeln 
                // (funktioniert nur bei Einzeleintraegen)
                boolean singleValue = false;
                boolean interval = false;
                try 
                {
                    double value = Double.parseDouble(entries.get(i));

                    if (this.limitParameterValues.size() > 0) 
                    {
                        if (value > ((Double) this.limitParameterValues
                        	.get(this.limitParameterValues.size() - 1))) 
                        {
                            if (Double.isInfinite(value) || Double.isNaN(value)) 
                            {
                                // diese Exception wird nicht im naechsten Catch sondern erst am 
                                // Ende der Methode abgefangen
                                throw new Exception();
                            }
                            this.validParameterValues.add(value);
                            // spaeter werden die this.limitParameterValues wieder entfernt, 
                            // sie werden benoetigt, um Werte zwischen value und den naechsten  
                            // Intervallen zuermitteln
                            if (!isResultParameter) 
                            {
                                this.limitParameterValues.add(value);
                                this.limitParameterValues.add(value);
                            }
                            singleValue = true;
                        } 
                        else 
                        {
                            throw new Exception();
                        }
                    } 
                    else 
                    {
                        if (Double.isInfinite(value) || Double.isNaN(value)) 
                        {
                            // diese Exception wird nicht im naechsten Catch sondern erst am Ende der 
                            // Methode abgefangen
                            throw new Exception();
                        }
                        this.validParameterValues.add(value);
                        // spaeter werden die limitParameterValues wieder entfernt, sie werden benoetigt, 
                        // um Werte zwischen value und den naechsten intervallen zu ermitteln
                        if (!isResultParameter) 
                        {
                            this.limitParameterValues.add(value);
                            this.limitParameterValues.add(value);
                        }
                        singleValue = true;
                    }
                } 
                catch (NumberFormatException nfe) 
                {
                    singleValue = false;
                }

                // zweiter Versuch: gefundenen Eintrag als Intervall auffassen und entsprechend 
                // umwandeln
                if (singleValue == false) 
                {
                    String entry = entries.get(i);
                    if (!(entry.contains("[") && entry.contains(",") && entry.contains("]"))) 
                    {
                        throw new Exception();
                    }

                    int commaIndex = entry.indexOf(",");

                    double lowerValue;
                    String lowerValueString = entry.substring(1, commaIndex);
                    if (lowerValueString.equals("-i")) 
                    {
                        lowerValue = negativeInfinity;
                    } 
                    else 
                    {
                        lowerValue = Double.parseDouble(lowerValueString);
                    }

                    double higherValue;
                    String higherValueString = entry.substring(commaIndex + 1, entry.length() - 1);
                    if (higherValueString.equals("i")) 
                    {
                        higherValue = positiveInfinity;
                    } 
                    else 
                    {
                        higherValue = Double.parseDouble(higherValueString);
                    }

                    if (this.limitParameterValues.size() > 0) 
                    {
                        if (higherValue >= lowerValue + 2 * precision
                            && lowerValue >= ((Double) this.limitParameterValues
                            .get(this.limitParameterValues.size() - 1))) 
                        {
                            interval = true;
                        }
                    } 
                    else 
                    {
                        if (higherValue >= lowerValue + 2 * precision) 
                        {
                            interval = true;
                        }
                    }
                    if (!interval) 
                    {
                        // Verarbeitung wird abgebrochen, da weder richtig formatierter Intervall 
                        // noch Einzelwert vorliegt
                        throw new Exception();
                    } 
                    else 
                    {
                        // Intervallgrenzen hinzufuegen, aber nur, wenn diese 
                        // keine Float-Sonderwerte enthalten
                        if (Double.isInfinite(lowerValue) || Double.isNaN(lowerValue)
                                || Double.isInfinite(higherValue) || Double.isNaN(higherValue)) 
                        {
                            throw new Exception();
                        }
                        this.limitParameterValues.add(lowerValue);
                        this.limitParameterValues.add(higherValue);

                        // um Ueberlaeufe zu vermeiden, wird der lowerValue im Bedarfsfall geaendert 
                        // (nur notwendig, wenn lowerValue negativ und higherValue positiv und der 
                        // Abstand zwischen beiden den positiven Wertebereich sprengt)
                        if (lowerValue == negativeInfinity && higherValue >= 0) 
                        {
                            lowerValue = (double) (negativeInfinity + precision);
                        }
                        if (lowerValue <= 0 && positiveInfinity - Math.abs(lowerValue) < higherValue) 
                        {
                            lowerValue = (double) (higherValue - positiveInfinity);
                        }
                        double validParameterValueBetweenLimits 
                            = (double) ((double) ((Math.random() 
                            * (higherValue - lowerValue - precision)))
                            + precision + lowerValue);
                        // ermittelten gueltigen Wert so stark im Betrag verringern wie nur moeglich
                        while (validParameterValueBetweenLimits / 10 > lowerValue
                            && validParameterValueBetweenLimits / 10 < higherValue
                            && Math.abs(validParameterValueBetweenLimits) > 1000) 
                        {
                            validParameterValueBetweenLimits 
                                = validParameterValueBetweenLimits / 10;
                        }
                        // ermittelten gueltigen Wert auf eine Nachkommastelle runden, 
                        // falls moeglich
                        double roundedValue 
                            = Formatation.roundToOneDecimal(validParameterValueBetweenLimits);
                        if (roundedValue > lowerValue && roundedValue < higherValue) 
                        {
                            validParameterValueBetweenLimits = roundedValue;
                        }
                        if (Double.isInfinite(validParameterValueBetweenLimits)
                                || Double.isNaN(validParameterValueBetweenLimits)) 
                        {
                            throw new Exception();
                        }
                        this.validParameterValues.add(validParameterValueBetweenLimits);
                    }
                }
            }
            // ungueltige Werte ausserhalb der Intervallgrenzen bestimmen 
            // (falls Intervalle vorhanden)
            if (this.limitParameterValues.size() > 0) 
            {
                for (int i = 0; i <= this.limitParameterValues.size(); i += 2) 
                {
                    double lowerValue;
                    double higherValue;

                    if (i == 0) 
                    {
                        lowerValue = negativeInfinity;
                        higherValue = (Double) this.limitParameterValues.get(i);
                    } 
                    else if (i == this.limitParameterValues.size()) 
                    {
                        lowerValue = (Double) this.limitParameterValues.get(i - 1);
                        higherValue = positiveInfinity;
                    } 
                    else 
                    {
                        lowerValue = (Double) this.limitParameterValues.get(i - 1);
                        higherValue = (Double) this.limitParameterValues.get(i);
                    }
                    // Zufallswert zwischen den Intervallgrenzen ermitteln und hinzufuegen, 
                    // wenn zwischen beiden Werten genug Platz ist
                    if (lowerValue == negativeInfinity && higherValue >= 0) 
                    {
                        lowerValue = (double) (negativeInfinity + precision);
                    }
                    if (lowerValue <= 0 && positiveInfinity - Math.abs(lowerValue) < higherValue) 
                    {
                        lowerValue = (double) (higherValue - positiveInfinity);
                    }
                    double invalidParameterValueOutOfLimits 
                        = (double) (((double) ((Math.random() 
                        * (higherValue - lowerValue - precision))))
                        + precision + lowerValue);
                    // ermittelten ungueltigen Wert so stark im Betrag verringern wie nur moeglich
                    while (invalidParameterValueOutOfLimits / 10 > lowerValue
                            && invalidParameterValueOutOfLimits / 10 < higherValue
                            && Math.abs(invalidParameterValueOutOfLimits) > 1000) 
                    {
                        invalidParameterValueOutOfLimits = invalidParameterValueOutOfLimits / 10;
                    }
                    // ermittelten ungueltigen Wert auf eine Nachkommastelle runden, falls moeglich
                    double roundedValue 
                        = Formatation.roundToOneDecimal(invalidParameterValueOutOfLimits);
                    if (roundedValue > lowerValue && roundedValue < higherValue) 
                    {
                        invalidParameterValueOutOfLimits = roundedValue;
                    }
                    if (Double.isInfinite(invalidParameterValueOutOfLimits)
                            || Double.isNaN(invalidParameterValueOutOfLimits)) 
                    {
                        throw new Exception();
                    }
                    this.invalidParameterValues.add(invalidParameterValueOutOfLimits);
                }
            }
            this.verificationResult = true;
        } 
        catch (Exception e) 
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }

   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle von Boolean
    */
    private void verifyBoolean(String text)
    {
        ArrayList<String> entries = new ArrayList<>();
        String rest = text;
        int startIndex = 0;
        int endIndex;
        while (rest.indexOf(";") >= 0) 
        {
            endIndex = rest.indexOf(";");
            entries.add(rest.substring(startIndex, endIndex));
            rest = rest.substring(endIndex + 1);
        }
        entries.add(rest);

        try 
        {
            for (int i = 0; i < entries.size(); i++) 
            {
                String entry = entries.get(i);
                if (entry.equals("true")) 
                {
                    this.validParameterValues.add(true);
                } 
                else if (entry.equals("false")) 
                {
                    this.validParameterValues.add(false);
                } 
                else 
                {
                    throw new Exception();
                }
            }
            this.verificationResult = true;
            this.invalidParameterValues.add(true);
            this.invalidParameterValues.add(false);
        } 
        catch (Exception e) 
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }

  
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle Char
    */
    private void verifyChar(String text)
    {
        try 
        {
            if (text.length() % 2 == 1) 
            {
                for (int i = 0; i < text.length(); i++) 
                {
                    if (i % 2 == 1) 
                    {
                        if (text.charAt(i) != ';') 
                        {
                            throw new Exception();
                        }
                    } 
                    else 
                    {
                        this.validParameterValues.add(text.charAt(i));
                    }
                }
            } 
            else 
            {
                throw new Exception();
            }
            this.verificationResult = true;
        } 
        catch (Exception e) 
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }   
 
   /*
    * uebernimmt die eigentliche Verifizierung der vom Nutzer eingegebenen
    * Zeichenkette im Falle String
    */
    private void verifyString(String text)
    {
        try 
        {
            String semicolonReplacement;
            ArrayList<String> entries = new ArrayList<>();

            // Semicolons und Nullen filtern
            do 
            {
                semicolonReplacement = Double.valueOf(Math.random()).toString();
            } 
            while (text.contains(semicolonReplacement));

            text = text.replace("\\;", semicolonReplacement);

            String rest = text;

            int startIndex = 0;
            int endIndex;
            while (rest.indexOf(";") >= 0) 
            {
                endIndex = rest.indexOf(";");
                entries.add(rest.substring(startIndex, endIndex));
                rest = rest.substring(endIndex + 1);
            }
            entries.add(rest);

            // alle Eintraege durchlaufen, Einzelstrings extrahieren
            for (int i = 0; i < entries.size(); i++) 
            {
                String entry = entries.get(i);
                if (entry.equals("null")) 
                {
                    this.validParameterValues.add(null);
                } 
                else if (entry.equals("")) 
                {
                    this.validParameterValues.add(entry);
                } 
                else 
                {
                    // null ist Schluesselwort und muss innerhalb eines Textes immer 
                    // mit \ eingeleitet werden
                    String entryCopy = entry;
                    entryCopy = entryCopy.replace("\\null", "");
                    if (entryCopy.contains("null")) 
                    {
                        throw new Exception();
                    }
                    entry = entry.replace(semicolonReplacement, ";");
                    entry = entry.replace("\\null", "null");
                    this.validParameterValues.add(entry);
                }
            }

            this.invalidParameterValues.add(null);
            this.invalidParameterValues.add("");

            this.verificationResult = true;
        } 
        catch (Exception e) 
        {
            this.verificationResult = false;
            this.validParameterValues.clear();
            this.invalidParameterValues.clear();
            this.limitParameterValues.clear();
        }
    }   
    
}
