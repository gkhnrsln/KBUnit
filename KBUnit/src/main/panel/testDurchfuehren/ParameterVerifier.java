package main.panel.testDurchfuehren;

import hilfe.guiHilfe.ImageTextField;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * ParameterVerifier ist ein InputVerifier fuer diskrete Parametereingaben; die
 * Klasse wird verwendet, um die Nutzereingaben im beim Anlegen einer neuen
 * Einzelkonfiguration zu ueberpruefen
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */

public class ParameterVerifier extends InputVerifier
{
    private Class<?> paramType;
    // input des Wissenstraegers
    private ArrayList<Object> pureInput = new ArrayList<Object>();
    // ist Eingabe gueltig
    private boolean verificationResult = true;

    // definiert, ob mehrere Werte durch Semikolon getrennt 
    // eingegeben werden duerfen
    private boolean multiValueAllowed = true;


    /**
     * Konstruktor, erlaubt standardmaessig die Eingabe mehrerer Parameterwerte
     *
     * @param paramType Datentyp des Parameters, dessen Wert im Eingabefeld
     *   modifiziert werden soll
     */
    public ParameterVerifier(Class<?> paramType)
    {
        this.paramType = paramType;
    }


    /**
     * Konstruktor, erwartet eine konkrete Festlegung bezueglich der Eingabe
     * mehrerer Parameterwerte
     *
     * @param paramType Datentyp des Parameters, dessen Wert im Eingabefeld
     *   modifiziert werden soll
     * @param multiValueAllowed falls true ist die Eingabe mehrerer
     *   Parameterwerte zulaessig
     */
    public ParameterVerifier(Class<?> paramType, boolean multiValueAllowed)
    {
        this.paramType = paramType;
        this.multiValueAllowed = multiValueAllowed;
    }


    /**
     * liefert das Resultat der letzte Verifizierung zurueck
     *
     * @return letztes Verifizierungsergebnis
     */
    public boolean getLastVerificationResult()
    {
        return this.verificationResult;
    }


    /**
     * liefert die vom Nutzer eingegebenen Parameterwerte sortiert zurueck
     *
     * @return Liste sortierter Parameterwerte
     */
    public ArrayList<Object> getSortedInput()
    {
        return this.pureInput;
    }


    /**
     * fuehrt die Verifikation der Nutzereingabe durch
     *
     * @param input Komponente, deren Eingabe verifiziert werden soll
     *
     * @return Verifikationsergebnis
     */
    @Override 
    public boolean verify(JComponent input)
    {
        this.pureInput.clear();

        ImageTextField source = (ImageTextField)input;
        String text = source.getText();
     
        // INTEGER, BYTE, SHORT, LONG, FLOAT, DOUBLE
        if (this.paramType == Integer.class || this.paramType == Byte.class 
            || this.paramType == Short.class || this.paramType == Long.class 
            || this.paramType == Float.class || this.paramType == Double.class)
        {
            this.verifyNumbers(text);     
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
        // Semikolons sind Sonderzeichen und trennen Strings 
        // (soll ein String ein Semikolon enthalten, so ist \; 
        // statt ; zu schreiben)
        // null ist Schluesselwort und stellt null-String dar 
        // (soll String die Zeichenfolge null enthalten, so ist 
        // \null statt null zu schreiben)
        else if(this.paramType == java.lang.String.class)
        {
            this.verifyString(text);
        }

        source.showAlertSymbol(!this.verificationResult);
        if (this.verificationResult == true)
        {
            HashSet<Object> h = new HashSet<Object>(this.pureInput);
            this.pureInput.clear();
            this.pureInput.addAll(h);
        }
        return this.verificationResult;
    }
    
   /*
    * fuehrt die Verifikation der Nutzereingabe fuer Zahlen durch
    */
    private void verifyNumbers(String text)
    {
        if (!this.multiValueAllowed && text.indexOf(";") >= 1)
        {
            this.verificationResult = false;
            this.pureInput.clear();
        }
        else  
        {
            ArrayList<String> entries = new ArrayList<>();
            String rest = text;
            int startIndex = 0;
            int endIndex;
            while(rest.indexOf(";")>=0)
            {
                endIndex = rest.indexOf(";");
                entries.add(rest.substring(startIndex,endIndex));
                rest = rest.substring(endIndex +1 );
            }
            entries.add(rest);
            try
            {
                for (int i=0; i<entries.size(); i++)
                {
                    if (this.paramType == Integer.class)
                    {
                        int value = Integer.parseInt(entries.get(i));
                        this.pureInput.add(value);
                    }
                    if (this.paramType == Byte.class)
                    {
                        byte value = Byte.parseByte(entries.get(i));
                        this.pureInput.add(value);
                    }
                    if (this.paramType == Short.class)
                    {
                        short value = Short.parseShort(entries.get(i));
                        this.pureInput.add(value);
                    }
                    if (this.paramType == Long.class)
                    {
                        long value = Long.parseLong(entries.get(i));
                        this.pureInput.add(value);
                    }
                    if (this.paramType == Float.class)
                    {
                        float value = Float.parseFloat(entries.get(i));
                        this.pureInput.add(value);
                    }
                    if (this.paramType == Double.class)
                    {
                        double value = Double.parseDouble(entries.get(i));
                        this.pureInput.add(value);
                    }
                }
                this.verificationResult = true;
            }
            catch (Exception e)
            {
                this.verificationResult = false;
                this.pureInput.clear();
            }
        }
    }     
    
    
   /*
    * fuehrt die Verifikation der Nutzereingabe fuer boolean durch
    */
    private void verifyBoolean(String text)
    {       
        if (!this.multiValueAllowed && text.indexOf(";")>=1)
        {
            this.verificationResult = false;
            this.pureInput.clear();
        }
        else
        {
            ArrayList<String> entries = new ArrayList<>();
            String rest = text;
            int startIndex = 0;
            int endIndex;
            while(rest.indexOf(";")>=0)
            {
                endIndex = rest.indexOf(";");
                entries.add(rest.substring(startIndex,endIndex));
                rest = rest.substring(endIndex + 1);
            }
            entries.add(rest);
            try
            {
                for (int i=0; i < entries.size(); i++)
                {
                    String entry = entries.get(i);
                    if (entry.equals("true"))
                    {
                        this.pureInput.add(true);
                    }
                    else if (entry.equals("false"))
                    {
                        this.pureInput.add(false);
                    }
                    else
                    {
                        throw new Exception();
                    }
                }
                this.verificationResult = true;
            }
            catch (Exception e)
            {
                this.verificationResult = false;
                this.pureInput.clear();
            }
        }
    } 
    
   /*
    * fuehrt die Verifikation der Nutzereingabe fuer char durch
    */   
    private void verifyChar(String text)
    {        
        if (!this.multiValueAllowed && text.length() != 1)
        {
            this.verificationResult = false;
            this.pureInput.clear();
        }
        else
        {
            try
            {
                if (text.length() % 2 == 1)
                {
                    for (int i=0; i < text.length(); i++)
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
                            this.pureInput.add(text.charAt(i));
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
                this.pureInput.clear();
            }
        }
    } 
    
   /*
    * fuehrt die Verifikation der Nutzereingabe fuer String durch
    */
    private void verifyString(String text)
    {
        try
        {
            String semicolonReplacement;
            ArrayList<String> entries = new ArrayList<>();
            // Escaped Semicolons filtern und ersetzen
            do
            {
                semicolonReplacement = Double.toString(Math.random());
            }
            while (text.contains(semicolonReplacement));
            text = text.replace("\\;", semicolonReplacement);
            if (!this.multiValueAllowed && text.indexOf(";")>=1)
            {
                throw new Exception();
            }
            String rest = text;
            int startIndex = 0;
            int endIndex;
            while(rest.indexOf(";") >= 0)
            {
                endIndex = rest.indexOf(";");
                entries.add(rest.substring(startIndex,endIndex));
                rest = rest.substring(endIndex+1);
            }
            entries.add(rest);
            // alle Eintraege durchlaufen, Einzelstrings extrahieren
            for (int i=0; i<entries.size(); i++)
            {
                String entry = entries.get(i);
                if (entry.equals("null"))
                {
                    this.pureInput.add(null);
                }
                else if (entry.equals(""))
                {
                    this.pureInput.add(entry);
                }
                else
                {
                    // null ist Schluesselwort und muss innerhalb eines Textes 
                	// immer mit \ eingeleitet werden
                    String entryCopy = entry;
                    entryCopy = entryCopy.replace("\\null", "");
                    if (entryCopy.contains("null"))
                    {
                        throw new Exception();
                    }
                    entry = entry.replace(semicolonReplacement, ";");
                    entry = entry.replace("\\null","null");
                    this.pureInput.add(entry);
                }
            }
            this.verificationResult = true;
        }
        catch (Exception e)
        {
            this.verificationResult = false;
            this.pureInput.clear();
        }
    }
}