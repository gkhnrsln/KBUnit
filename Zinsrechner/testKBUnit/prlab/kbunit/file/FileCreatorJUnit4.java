package prlab.kbunit.file;

import org.junit.runners.Suite.SuiteClasses;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.TestObject;
import prlab.kbunit.test.TestResultInfo;
import prlab.kbunit.test.TestSourceJUnit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * The {@code FileCreator} class provides methods for
 * copying files from a given location and creating new files 
 * to another location. The class provides extra method 
 * {@link FileCreatorJUnit4#copyFile()} 
 * for creating new .java files by copying ones, that
 * use JUnit 4.<br>
 *
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 */
public class FileCreatorJUnit4 extends FileCreatorJUnit{
	
	/**
     * creating a FileCreatorJUnit4 object
     * @param testSource, contains informations about the test class
     * @param testObject, contains the methods of the test class
     */
    public FileCreatorJUnit4(TestSourceJUnit testSource, TestObject testObject){
		super(testSource, testObject);
	}

	/**
	 * Copies line by line a given <b>testSource</b>-{@link File} from 
	 * a current location to a new file location. This method was 
	 * designed especially for copying .java-classes that use 
	 * <b>JUnit 4.x</b>. All test-cases (expected exceptions are considered), 
	 * which are allocated 	 * in a {@link TestObject} and belong to a 
	 * certain <b>testSource</b> will be also included in the new file. 
	 * Apart from that every newly created class will be 
	 * automatically included in a class {@link Variables#ALLTEST_RUNNER_FILE} 
	 * annotated with {@link SuiteClasses}.
	 * @return String, which contains the filePath of the new File
	 */  
     public String copyFile() {
		//File toSource = new File(super.getTestSource().getNewFilePath());	
    	 File toSource = new File(super.getTestSource().getPathNewGeneratedTestCases());
        if(super.getTestObject().getTestMethods() != null
            && super.getTestObject().getTestMethods().size() > 0){
			if(!toSource.exists()) {
				BufferedWriter bWriter;
				try {
					String fromClassName = super.getTestSource().getSimpleName();
					String toClassName = super.getTestSource().getPathNewGeneratedTestCases();
					toClassName = toClassName.substring(toClassName.indexOf(
						super.getTestSource().getSimpleName()), 
						toClassName.indexOf(Variables.EXTENSION_JAVA));
			
					bWriter = new BufferedWriter(
						new FileWriter(createMissingPackages(toSource)));
					List<String> listLines 
					    = super.readFileAsList(super.getTestSource().getClassAbsolutePath());
					for(int i = 0; i < listLines.size(); i++){
			    	    if(listLines.get(i).contains(Variables.ANNOTATION_TEST4)){
			    	    	listLines.remove(i);
			    	   	    while(! listLines.get(i).contains(
			    	   	    	Variables.METHOD_START_PUBLIC_VOID)){
			    	  	  	    listLines.remove(i);
				        	}
				     	}				   
					}
					
					int lastLine = listLines.size() - 1;
					while(!listLines.get(lastLine).contains("}") && lastLine > 0){
						lastLine--;
					}
					listLines.add(lastLine++, "");
					listLines.addAll(lastLine, getJUnitMethods());
					
				    for (String line : listLines) {
				    	/*
				    	 *  Changes the old test-class name (i.e. MultiplierTest)
				    	 *  with new one (i.e. MultiplierTestKBUnit) to avoid ambiguity
				    	 */
				     	if(line.contains(fromClassName)){
				     		line = line.replace(fromClassName, toClassName);
				     	}
				     	bWriter.write(line);
				     	bWriter.newLine();
				     }
			         bWriter.close();
			         
				} 
				catch (IOException e) {
					e.printStackTrace();
				} 
			}
        }		
	    return super.getTestSource().getNewFilePath();
	} 
		 	
	/*
	 * the content of an annotation of a test method, which shall be copied
	 * @see srlab.kbunit.plugin.file.FileCreatorJUnit#createAnnotation(
	 * prlab.kbunit.plugin.test.TestMethod)
	 */
	protected String createAnnotation(TestResultInfo testMethod){
        String result = "\t@Test";
		if(testMethod.isExceptionExpected()){
		    result += " (expected = Exception.class)";
		}
	    return result;
    }    
	
	/*
	 * Creates the call of the source method 
	 */ 
	protected String createMethodCall(TestResultInfo testMethod){
		return "\t\t"+ testMethod.getName() +"();";
	}	
}
