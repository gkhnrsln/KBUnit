/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hilfe;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Ursula Oesing
 */
public class TypeConvertation {
    
 
    /**
     * Ein Datentyp wird vorgegeben. In Abhaengigkeit dieses Typs wird der vor-
     * gegebene String konvertiert.
     * @param type vorgegebener Datentyp
     * @param valString zu konvertierender String
     * @return der Inhalt des vorgegebenen Strings, der in den vorgegebenen 
     *         Datentyp konvertiert wurde
     */
    @SuppressWarnings("deprecation")
	public static Object convertString(Type type, String valString)
    {
        Object result;
        result = null;
        if (type == byte.class)
        {
            result = new Byte(valString);
        }   
        else if(type == short.class)
        {
            result = new Short(valString);
        }    
        else if (type == int.class)
        {
            result = new Integer(valString);
        }
        else if(type == long.class)
        {
            result = new Long(valString);
        }    
        else if(type == float.class)
        {
            result = new Float(valString);
        }    
        else if(type == double.class)
        {
            result = new Double(valString);
        }    
        else if(type == boolean.class)
        {
            result = new Boolean(valString);
        }    
        else if(type == char.class)
        {
            result = valString.charAt(0);
        }    
        else if(type == String.class)
        {
            result = valString;
        }
        return result;
    }    
    
    /**
     * Die vorgegebene Testklasse eines JUnit-Tests wird fuer einen Test vorbereitet.
     * Es wird der Parameter mit dem vorgegebenen Namen und Wert gesetzt.
     * @param testclass Testklasse, die fuer einen JUnit-Test vorbereitet wird
     * @param name String, welcher den Namen des zu konvertierenden Parameters enthaelt
     * @param value Object, welches den Wert des Parameters enthaelt. Dieser ist zu konvertieren.
     * @return konvertierter Wert
     * @throws NoSuchFieldException, falls ein Attribut mit dem vorgegebenen namen 
     *                               nicht vorhanden ist
     * @throws IllegalAccessException, falls ein Attribut mit dem vorgegebenen namen 
     *                                 nicht mit dem vorgegebenen Wert belegt werden konnte
     *         
     */
    public static Object setFieldParameter(Class<?> testclass, String name, Object value) 
        throws NoSuchFieldException, IllegalAccessException
    {        
		Object result;
        result = null;
        // anhand des uebergebenen Namens wird auf den Eingabeparameter des Testfalls 
        // geschlossen und dieser referenziert
        Field fieldParameter = testclass.getDeclaredField(name);
        fieldParameter.setAccessible(true);

        // falls der Parameter vom Typ INTEGER ist
        if (fieldParameter.getType() == int.class)
        {
            fieldParameter.setInt(testclass, (Integer) value);
            result = (Integer) value;
        }
        // falls der Parameter vom Typ BYTE ist
        if (fieldParameter.getType() == byte.class)
        {
            fieldParameter.setByte(testclass, (Byte) value);
            result = (Byte) value;
        }
        // falls der Parameter vom Typ SHORT ist
        if (fieldParameter.getType() == short.class)
        {
            fieldParameter.setShort(testclass, (Short) value);
            result = (Short) value;
        }
        // falls der Parameter vom Typ LONG ist
        if (fieldParameter.getType() == long.class)
        {
            fieldParameter.setLong(testclass, (Long) value);
            result = (Long) value;
        }
        // falls der Parameter vom Typ FLOAT ist
        if (fieldParameter.getType() == float.class)
        {
            // Option A: das Textfeld enthaelt den voreingestellter Wert aus Testklasse 
            // (er wurde nicht geaendert), dieser ist daher vom Typ Float
            if (value.getClass() == Float.class)
            {
                fieldParameter.setFloat(testclass, (Float) value);
                result = (Float) value;
            }
            // Option B: das Textfeld enthaelt einen geaenderten Wert, der deshalb 
            // programmintern als Double verwaltet wird (Konvertierung notwendig)
            else if (value.getClass() == Double.class)
            {
                Double d = (Double) value;
                fieldParameter.setFloat(testclass, d.floatValue());
                result = d.floatValue();
            }
            // Option C: das Textfeld enthaelt den Wert 0, der intern als Long 
            // verwaltet wird (Konvertierung notwendig)
            else if (value.getClass() == Long.class)
            {
                Long l = (Long) value;
                fieldParameter.setFloat(testclass, l.floatValue());
                result = l.floatValue();
            }
        }
        // falls der Parameter vom Typ DOUBLE ist
        if (fieldParameter.getType() == double.class)
        {
            // Option A: Das Textfeld enthaelt einen Wert ungleich 0
            if (value.getClass() == Double.class)
            {
                fieldParameter.setDouble(testclass, (Double) value);
                result = (Double) value;
            }
            // Option B: Das Textfeld enthaelt den Wert 0, der intern als Long verwaltet wird 
            // (Konvertierung notwendig)
            else if (value.getClass() == Long.class)
            {
                Long l = (Long) value;
                fieldParameter.setDouble(testclass, l.doubleValue());
                result = l.doubleValue();
            }
        }
        // falls der Parameter vom Typ BOOLEAN ist
        if (fieldParameter.getType() == boolean.class)
        {
            fieldParameter.setBoolean(testclass, (Boolean) value);
            result = (Boolean) value;
        }
        // falls der Parameter vom Typ CHAR ist
        if (fieldParameter.getType() == char.class)
        {
            fieldParameter.setChar(testclass, value.toString().charAt(0));
            result = value.toString().charAt(0);
        }
        // falls der Parameter vom Typ STRING ist
        if(fieldParameter.getType() == java.lang.String.class)
        {
            fieldParameter.set(testclass, value);
            //staticParameter.set(testclass, value.toString());
            result = value;
        }
        return result;
    }    
  
}
