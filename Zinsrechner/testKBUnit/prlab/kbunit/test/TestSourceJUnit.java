package prlab.kbunit.test;

import prlab.kbunit.enums.Variables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * The {@code TestSource} class provides through
 * reflection suitable methods for getting information
 * only about {@link Method}s and declared {@link Field}s
 * with the prefix <b>"test"</b>. This class was constructed
 * especially for <b>JUnit 4.x or JUnit 5.x</b>. <br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 *  
 * @author Ursula Oesing
 * 
 */  
public abstract class TestSourceJUnit extends TestSource{
	
	/**
	 * Constructs a new class of a 
	 * given source-{@link File}.
	 * @param source {@code File} from which an 
	 *        {@code Class} object will be created
	 * @param prefix String, which contains the prefix of the testclass       
	 * @see {@link ClassCreator#ClassCreator(File)}
	 */
	protected TestSourceJUnit(File source, String prefix) {
		super(source, prefix);
		this.initMethods();
		this.initFieldTypes();
        super.setPathNewGeneratedTestCases(
        	this.getNewFilePath());
	}
	 
    /**
	 * Provides a simple class-name for a
	 * new (test-)class to be created.
	 * <br> i.e. MultiplierTestKBUNit
	 * 
	 * @return simple name for the new (test-)class
	 *     <br>with suffix = {@link Variables#CLASS_NAME_SUFFIX} 
	 */
	public String getNewSimpleName(){
	    return this.getSimpleName().concat(Variables.CLASS_NAME_SUFFIX);
	}
	
	/**
	 * Provides path name for new file where {@link Variables#TEST_SOURCE} 
	 * <br> has been changed with {@link Variables#TEST_NEW_SOURCE}
	 * <br> and class-name gets new suffix {@link Variables#CLASS_NAME_SUFFIX}
	 * <p> <b>Example:</b>
	 * <br>test\multiplier\MultiplierTest.java
	 * <br>changed into:
	 * <br>testKBUnit\multiplier\MultiplierTestKBUnit.java
	 * @return new file-path as String
	 */
    public String getNewFilePath(){
	    return super.getClassFile()
	        .getPath()
		    .replace(".java",Variables.CLASS_NAME_SUFFIX + ".java")
		    .replace(Variables.TEST_SOURCE, Variables.TEST_NEW_SOURCE);
 	}
	
 	/**
	 * Initializes an array of Method objects 
	 */
	protected abstract void initMethods(); 
	
	/**
	 * inits the types of the static fields of the testclasses. 
	 */
	private void initFieldTypes() {
		mapFieldTypes = new HashMap<String, Class<?>>();
		Field[] fields = super.getClazz().getDeclaredFields();
		for(int i = 0; i < fields.length; i++) {
			if(startsWithNameOfTestmethod(fields[i].getName())){
			    mapFieldTypes.put(fields[i].getName(), 
			    	fields[i].getType());
			}    
		}
	}
		
  	/**
	 * returns the type and the value of the testParameter of a given class.
	 * @param testParameter, the testParameter
	 * @return String, which contains the type and the value of the testParameter
	 */
	public String getTestParameterTypeValue(TestParameterInfo testParameter)
	    throws NoSuchFieldException{
		Field field = super.getClazz().getDeclaredField(testParameter.getName());
		return "("+ field.getType().getSimpleName() + ") " + testParameter.getValue();
	}
		
}

