package prlab.kbunit.file;

import org.junit.runners.Suite.SuiteClasses;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.TestObject;
import prlab.kbunit.test.TestSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code FileCreator} class provides methods for
 * copying files from a given location and creating new files 
 * to another location. <br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 * 
 */
public abstract class FileCreator {

	// containing the testMethods of the testcase
	private TestObject testObject;

	/**
	 * creating a FileCreator-Object. Because this class is abstract,
	 * it is only used by deriving classes.
	 * @param testObject, contains the testMethods of the test case
	 */
	public FileCreator(TestObject testObject){
		this.testObject = testObject;
	}

	/**
	 * returning the testObject attribut
	 * @return testObject, which contains the testMethods of the testcase
	 */
	public TestObject getTestObject(){
		return testObject;
	}

	/**
	 * the deriving classes, which are not abstract, have to implement 
	 * a method for copying the test class
	 * @return String, which contains the FilePath of the new File
	 */
	public abstract String copyFile();

	/**
	 * method for deleting the given file
	 * @param file String, which contains the FilePath of the file
	 */
	public static void deleteFile(String file){
		File source = new File(file);
		if(source.exists()){
			source.delete();
		}
	}

	/**
	 * the deriving classes, which are not abstract, have to implement 
	 * a method for returning the TestSource attribut
	 * @return a kind of TestSource
	 */
	public abstract TestSource getTestSource();

	/**
	 * the deriving classes have to implement, 
	 * whether the TestSource is copied yet or not.
	 * @return true, if it is not copied yet
	 */ 
	public abstract boolean isNewTestSource();

	/**
	 * Creates the package(s) that have not been created 
	 * of given {@code File}-path.
	 * @param out the {@code File} to be created
	 * @return new {@code File} with correct File-path
	 */
	public static File createMissingPackages(File out){
		File pack = new File(out.getParent());
		if(!pack.exists()){
			pack.mkdirs();	
		}
		return out;
	}


	/**
	 * Reads all lines of given {@link File}. 
	 * <br>See {@link Files#readAllLines(Path path, Charset cs)} 
	 *
	 * @param fullPath the absolute path of given {@code File}
	 *        <br>See {@link File#getAbsolutePath()}
	 *        <br> i.e. C:\Users\NORD\workspace\TestKBUnitNEW\testKBUnit\runDriver\Probe.java
	 * @return all lines of a certain file as {@code List}
	 */
	public static List<String> readFileAsList(String fullPath) {

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(fullPath),
					Charset.defaultCharset());	  
		} 
		catch (IOException e) { 
			e.printStackTrace();
		}

		return lines;
	}


	/*
	 * By copying the given test-object and its 
	 * test-methods and parameters, that belong
	 * to each test-method it is important to know
	 * the type of the value to be copied. 
	 * <1>Example: 
	 *    value is from type String and 
	 *    has to be copied in quotes.
	 *    value: Name --> copied: "Name"
	 * <2>Example:
	 *    value is from type boolean and
	 *    has to be copied without quotes.
	 *    value: true --> copied: true; 
	 */
	protected String adjustValue(Class<?> type, String value) {
		if(value == null){
			return value;
		}
		switch(type.getSimpleName()) {
		case("byte")   :
		case("short")  :
		case("int")    :
		case("long")   :
		case("double") : return value;
		case("boolean"): if(value.equals("1")){
							return "true";
						 }
						 else if(value.equals("0")){
							 return "false";
						 }
						 else{
							 return value;
						 }
		case("char")   : return "\'"+ value +"\'";
		default        : if(value.equals("\"\"")) {
			                return "\"\\\"\\\"\"";
		 				 }
		 				 else {
			                 return "\"" + value + "\""; 
		                 }
		}  
	}

	/* gives the position of the testparameter of the line */
	protected int getPositionTestParameter(String line, ArrayList<String> testParameter){
		int result = -1;
		int i = 0;
		while(result == -1 && i < testParameter.size()){
			if(line.contains(testParameter.get(i))){
				result = i;
			}
			i++;
		}
		return result;
	}

	
	/**
	 * Creates class {@link Variables#ALLTEST_RUNNER_CLASS} in 
	 * package {@link Variables#ALLTEST_PACKAGE}
	 * <br>suitable for running bundle of JUnit-test-cases 
	 * with {@link SuiteClasses}.
	 * 
	 * @param source {@link File} with certain path {@link Variables#ALLTEST_RUNNER_FILE} 
	 * @param suiteClasses {@link ArrayList} of class-names in the form:
	 * <br>i.e. multiplier.MultiplierTest 
	 */
	public void createSuiteClasses(File source, List<String> suiteClasses) {
		try {
			BufferedWriter bWriter;
			if(suiteClasses != null && suiteClasses.size() > 0){
				if(!source.exists()){
					bWriter = new BufferedWriter(
							new FileWriter(createMissingPackages(source)));
					bWriter.write("package "+ Variables.ALLTEST_PACKAGE +"; \n\n"
							+ "import org.junit.runner.RunWith; \n"
							+ "import org.junit.platform.runner.JUnitPlatform; \n"
							+ "import org.junit.platform.suite.api.IncludeClassNamePatterns; \n"
							+ "import org.junit.platform.suite.api.SelectClasses; \n\n"
							+ "@RunWith(JUnitPlatform.class) \n"
							+ "@IncludeClassNamePatterns(\".*\") \n"
							+ "@SelectClasses({ \n");
					for(int i=0; i < suiteClasses.size(); i++){
						bWriter.write("\t\t"+ suiteClasses.get(i) +".class");
						if( i != suiteClasses.size()-1){
							bWriter.append(',');
						}
						bWriter.newLine();
					}

					bWriter.write("}) \n"
							+ "public class "+ Variables.ALLTEST_RUNNER_CLASS +" {}");
					bWriter.close();

				} 
				else {
					List<String> lines = readFileAsList(source.getAbsolutePath());
					bWriter = new BufferedWriter(new FileWriter(source));
					for(String line : lines){
						if(line.replaceAll("\\s", "").contains("@SelectClasses({")){
							for(int i=0; i < suiteClasses.size(); i++){
								line += "\n\t"+ suiteClasses.get(i) +".class,";
							}
						}
						bWriter.write(line);
						bWriter.newLine();
					}
					bWriter.close();
				}
			}
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public void deleteSuiteClasses(File source, String path) {
		try {
			BufferedWriter bWriter;
			List<String> lines = readFileAsList(source.getAbsolutePath());
			bWriter = new BufferedWriter(new FileWriter(source));
			int beginIndex = path.indexOf(Variables.TEST_NEW_SOURCE) 
				+ Variables.TEST_NEW_SOURCE.length() + 1; // represent '\';
				// !!! '.java' --> (str.length() - 5)
			String classPath =  path.substring(beginIndex, path.length() - 5)
				.replace("\\", ".")+".class";
			for(String line : lines){
				if(line.replaceAll("\\s", "").contains(classPath)){
					line = null;
				}
				if(line!=null) {
					bWriter.write(line);
					bWriter.newLine();
				}
			}
			bWriter.close();
		} 
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

}
