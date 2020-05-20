package hilfe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Stack;

/**
 * konstruiert eine Test-Report-Message
 * &copy; 2017 Philipp Sprengholz, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class MyClassLoader extends URLClassLoader 
{

    public MyClassLoader(URL[] urls, ClassLoader parent) 
    {
        super(urls, parent);
    }

    public void addURL(URL url) 
    {
        super.addURL(url);
    }

    /**
     * Stellt fest, ob es sich um eine Innere Klasse handelt
     * @param clazz die Klasse, für die ueberprueft werden soll,
     *              ob es sich um eine innere Klasse (nicht-statisch) handelt
     * @return Wahrheitswert, ob uebergebene Klasse eine innere Klasse ist
     */
    public static boolean isInnerClass(Class<?> clazz) 
    {
        return clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers());
    }

    /**
     * kreiert eine Testinstanz der uebergebenen Klasse.
     * bei verschachtelten Klassen, muss immer jeweils die Instanz
     * der umgebenen Klasse uebergeben werden
     * @param testclass die Klasse, von der eine Test-Instanz erstellt
     *                  werden soll
     * @return Testinstanz-Objekt
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    public static Object getTestClassInstance(Class<?> testclass)
        throws NoSuchMethodException, IllegalAccessException
        , InvocationTargetException, InstantiationException 
    {
        Stack<Class<?>> outerClassesStack = new Stack<>();
        Class<?> currentClass = testclass;
        while(MyClassLoader.isInnerClass(currentClass))
        {
            outerClassesStack.push(currentClass.getEnclosingClass());
            currentClass = currentClass.getEnclosingClass();
        }
        Object currentInstance = null, outerInstance = null;
        Constructor<?> currentConstructor;
        Class<?> outerClass = null;

        while(!outerClassesStack.empty())
        {
            currentClass = outerClassesStack.pop();
            if(currentInstance == null)
            {  // Beim ersten Durchlauf wahr
                currentConstructor = currentClass.getDeclaredConstructor();
                currentConstructor.setAccessible(true);
                currentInstance = currentConstructor.newInstance();
            }
            else
            {
                currentConstructor = currentClass.getDeclaredConstructor(outerClass);
                currentConstructor.setAccessible(true);
                currentInstance = currentConstructor.newInstance(outerInstance);
            }
            outerClass = currentClass;
            outerInstance = currentInstance;
        }
        Constructor<?> testClassConstructor = null;
        Object newTestInstance = null;
        if(outerClass != null && outerInstance != null)
        {
            testClassConstructor = testclass.getDeclaredConstructor(outerClass);
            testClassConstructor.setAccessible(true);
            newTestInstance = testClassConstructor.newInstance(outerInstance);
        }
        else
        {
            testClassConstructor = testclass.getDeclaredConstructor();
            testClassConstructor.setAccessible(true);
            newTestInstance = testClassConstructor.newInstance();
        }
        return newTestInstance;
    }
}
