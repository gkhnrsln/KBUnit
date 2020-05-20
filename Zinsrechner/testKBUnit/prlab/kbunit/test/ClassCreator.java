package prlab.kbunit.test;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.file.FileCreator;

import java.io.File;

/**
 * The {@code ClassCreator} class provides method for 
 * creating of {@code Class} object from given source
 * {@code File} and also methods for allocating new 
 * class-names such as {@link ClassCreator#getNewClassName()},
 * and new file-paths 
 * and {@link ClassCreator#getXMLFilePath()}.<br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 *
 */
public class ClassCreator{
	
	// Testclass
	private Class<?> clazz;
	// Name of the testclass
	private File     classFile;
	
	/**
	 * This constructor converts the name of
	 * a given source-{@link File} into class-name
	 * via the method: {@link ClassCreator#convertIntoClassName(File)}.
	 * After that the so converted name <br>is used for creating
	 * an {@code Class} object associated with the class or
	 * interface with this class-name.
	 * @param source {@code File} from which an 
	 *        {@code Class} object will be created
	 * @param prefix String, which contains the prefix of the testclass       
	 * @see {@link Class#forName(String)}
	 */
	protected ClassCreator(File source, String prefix) {
		
		try {
			clazz = Class.forName(convertIntoClassName(source, prefix));
			
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
		classFile = source;
	}
	
	/**
	 * @return {@link Class} created from source-file
	 */
	public Class<?> getClazz(){
		return this.clazz;
	} 
	
	/**
	 * i.e. multiplier.MultiplierTest
	 * @return name of this class
	 */
	public String getClassName(){
		return this.clazz.getName();
	}
	  
	public File getClassFile() {
		return classFile;
	}
	
	/**
	 * Provides new canonical name with
	 * {@link Variables#CLASS_NAME_SUFFIX}
	 * <br>for class created automatically in 
	 * <br> the ClassCreator().
	 * <p>i.e. multiplier.MutiplierTestKBUnit
	 * @return new class-name 
	 */
	public String getNewClassName(){
		return this.clazz.getCanonicalName()
			.concat(Variables.CLASS_NAME_SUFFIX);
	}
	
	/**
	 * i.e. MultiplierTest
	 * @return the simple name of given class {@link Class#getSimpleName()}
	 */
	public String getSimpleName(){
		return this.clazz.getSimpleName();
	}
	

	/**
	 * i.e. test\multiplier\MultiplierTest.java
	 * @return the file-path of given {@link File#getPath()}
	 */
	public String getFilePath(){
		return this.classFile.getPath();
	}
	
   
    /**
	 * Provides path name for new LOG-XML-file where {@link Variables#TEST_SOURCE} 
	 * <br> has been changed with {@link Variables#TEST_NEW_SOURCE},
	 * <br>.java-class-name gets new suffix {@link Variables#CLASS_NAME_SUFFIX}
	 * <br>and finally will be transformed in .XML-file 
	 * <p> <b>Example:</b>
	 * <br>test\multiplier\MultiplierTest.java
	 * <br>changed into:
	 * <br>testKBUnit\multiplier\log\MultiplierTestKBUnit.xml
	 * @return new file-path as String
	 */
    public String getXMLFilePath(){
    	return Variables.TEST_NEW_SOURCE +"\\"
    	    + clazz.getPackage().getName().replace('.', '\\') 
    	    + "\\" + Variables.LOG_SUBPACKAGE +"\\"
    	    + clazz.getSimpleName() + Variables.CLASS_NAME_SUFFIX + ".xml";
    }
    
    /**
     * Provides name for the activity-log-file,
     * so that it will be saved in the package
     * used <br>for containing the test-cases copied
     * by {@link FileCreator#copyFile()}.
     * @return new file-path as String
     *  <br> i.e. testKBUnit\multiplier\MultiplierTest.xml
     */
    public String getLOGFilePath(){
    	return this.classFile
			.getPath()
			.replace(".java", ".xml")
			.replace(Variables.TEST_SOURCE, 
			Variables.TEST_NEW_SOURCE);
    }
	
	/**
	 * i.e. C:\Users\NORD\Eclipse_Workspace\TestKBUnit\testKBUnit\...\Probe.java
	 * @return the absolute-path of given class
	 */
	public String getClassAbsolutePath(){
		return this.classFile.getAbsolutePath();
	}
	
	/**
	 * Converts the path of given .java-{@link File} into class-name.
	 * However <b>source</b> has to point to .java-Files,<br>that are 
	 * located in test-source-folder: {@link Variables#TEST_SOURCE}.
	 * <b>Example:</b>
	 * <b>Source-folder: </b>"test"
	 * <b>File: </b>./test/multiplier/MultiplierTest.java
	 * <b>String: </b> multiplier.MultiplierTest
	 * @param source {@link File} which path to be converted
	 * @param prefix for finding the begin Index
	 * @return the <b>source</b>-pathname converted in
	 *         appropriate class-name
	 */
	public static String convertIntoClassName(File source, String prefix){
	
		String str     = source.getPath();
		int beginIndex = str.indexOf(prefix) 
			+ prefix.length() 
			+ 1; // represent '\';
		// !!! '.java' --> (str.length() - 5)
		return str.substring(beginIndex, str.length() - 5).replace("\\", ".");
	
	}
	
	/**
	 * Provides the Superclass-name of given <b>subclass</b>
	 * <br> i.e. junit.framework.TestCase
	 * @return the name of <b>this</b> Superclass
	 */
	public String getSuperclass(){
		return this.clazz.getSuperclass().getName();
	}

}
