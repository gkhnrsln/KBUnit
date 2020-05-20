package prlab.kbunit.file;

import org.junit.runners.Suite.SuiteClasses;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.test.TestObject;
import prlab.kbunit.test.TestParameterInfo;
import prlab.kbunit.test.TestResultInfo;
import prlab.kbunit.test.TestSourceJUnit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * The {@code FileCreator} class provides methods for
 * copying files from a given location and creating new files 
 * to another location. The class provides extra method 
 * {@link FileCreatorJUnit5#copyFile()}
 * for creating new .java files by copying ones, that
 * use JUnit 5.<br>
 *
 * &copy; 2017 Alexander Georgiev, Yannis Herbig, Ursula Oesing  <br>
 * @author Yannis Herbig
 */
public class FileCreatorJUnit5 extends FileCreatorJUnit{

    // Der neue Klassenname der zu generierenden Testklasse:
    private String toClassName;
    // Die Testkonfigurationen werden nach Methodenname in dieser Map geordnet:
    private Map<String, ArrayList<TestResultInfo>> sortedConfigurations = new HashMap<>();
    /* Testmethoden, die nicht mit @Test annotiert sind, werden in getJUnitMethods generiert.
       Zuvor werden dazu die dazu benoetigten Bestandteile in diesen Maps gespeichert: */
    private Map<String, ArrayList<String>> nonStandardTestMethodsAnnotations = new HashMap<>();
    private Map<String, String> nonStandardTestMethodHeaders = new HashMap<>();
    private Map<String, String> nonStandardTestMethodOrigParamsNames = new HashMap<>();
    private Map<String, String> nonStandardTestMethodOrigParams = new HashMap<>();

    /**
     * creating a FileCreatorJUnit5 object
     * @param testSource, contains informations about the test class
     * @param testObject, contains the methods of the test class
     */
    public FileCreatorJUnit5(TestSourceJUnit testSource, TestObject testObject){
        super(testSource, testObject);
    }

    /**
     * Sortiert die Testkonfigurationen und ordnet sie ihren Methoden zu
     */
    private void organiseTestConfigs(){
        for(TestResultInfo testMethod : super.getTestObject().getTestMethods()) {
            if(!sortedConfigurations.containsKey(testMethod.getName())){
                ArrayList<TestResultInfo> allConfigsForSameMethod = new ArrayList<>();
                allConfigsForSameMethod.add(testMethod);
                sortedConfigurations.put(testMethod.getName(), allConfigsForSameMethod);
            }
            else{
                sortedConfigurations.get(testMethod.getName()).add(testMethod);
            }
        }
    }

    /**
     * Lists all test methods of given JUnit test-class.
     * Copies line by line a given <b>testSource</b>-{@link File} from
     * a current location to a new file location. This method was
     * designed especially for copying .java-classes that use
     * <b>JUnit 5.x</b>. All test-cases (expected exceptions are considered),
     * which are allocated 	 * in a {@link TestObject} and belong to a
     * certain <b>testSource</b> will be also included in the new file.
     * Apart from that every newly created class will be
     * automatically included in a class {@link Variables#ALLTEST_RUNNER_FILE}
     * annotated with {@link SuiteClasses}.
     * @return String, which contains the filePath of the new File
     */
    public String copyFile() {
        organiseTestConfigs();
        boolean anyNewStandardTestConfigsExist = false;
        File toSource = new File(super.getTestSource().getPathNewGeneratedTestCases());
        if(super.getTestObject().getTestMethods() != null
                && super.getTestObject().getTestMethods().size() > 0){
            if(!toSource.exists()) {
                BufferedWriter bWriter;
                try {
                    String fromClassName = super.getTestSource().getSimpleName();
                    toClassName = super.getTestSource().getPathNewGeneratedTestCases();
                    toClassName = toClassName.substring(toClassName.indexOf(
                        super.getTestSource().getSimpleName()),
                        toClassName.indexOf(Variables.EXTENSION_JAVA));
                    bWriter = new BufferedWriter(
                            new FileWriter(createMissingPackages(toSource)));
                    List<String> listLines
                            = readFileAsList(super.getTestSource().getClassAbsolutePath());
                    for(int i = 0; i < listLines.size(); i++){
                        if(listLines.get(i).contains(Variables.ANNOTATION_TEST5)
                            && !listLines.get(i).contains(Variables.ANNOTATION_TEST5_FACTORY)
                            && !listLines.get(i).contains(Variables.ANNOTATION_TEST5_TEMPLATE)
                            && !listLines.get(i).contains("@TestMethodOrder")
                            && !listLines.get(i).contains("@TestInstance")){
                            int testAnnotationIndex = i;
                            while(!listLines.get(i).contains(" " + Variables.METHOD_START_VOID + " ")
                                && !listLines.get(i).contains("\t" + Variables.METHOD_START_VOID + " ")){
                                i++;  // Index von Methodenkopf erreichen
                            }
                            String methodHeaderLine = listLines.get(i);
                            ArrayList<TestResultInfo> testMethods = super.getTestObject()
                                    .getTestMethods();
                            boolean methodHasNewConfigs = false;
                            String methodName, bodyMethodParameterDeclarations,
                                    bodyMethodHeaderLine = "";
                            StringBuilder methodParameterNames = new StringBuilder();
                            String bodyMethodName = "";
                            // Ueberpruefen, ob es zu dir Methode neue Testkonfigurationen gibt
                            for(TestResultInfo method : testMethods) {
                                if (methodHeaderLine.contains(method.getName())) {
                                    methodHasNewConfigs = true;
                                    anyNewStandardTestConfigsExist = true;
                                    methodName = method.getName();
                                    StringBuilder originalParams = new StringBuilder(),
                                        originalParamsNames = new StringBuilder();
                                    extractOrigArgsFromMethodHeader(methodHeaderLine, originalParams);
                                    extractOrigArgsNames(originalParams, originalParamsNames);
                                    listLines.remove(i);	// Methodenkopf entfernen (wird durch neuen ersetzt)
                                    listLines.add(i, createAnnotations(method));
                                    StringBuilder methodParameterDeclarations = new StringBuilder();
                                    bodyMethodParameterDeclarations = constructNewArgsForMethodHeaders(
                                        methodParameterNames, method, originalParams,
                                        originalParamsNames, methodParameterDeclarations);
                                    bodyMethodName = methodName + "_Body";
                                    bodyMethodHeaderLine = constructBodyMethodHeaderLine(
                                        methodHeaderLine, bodyMethodParameterDeclarations,
                                        methodName, bodyMethodName);
                                    methodHeaderLine = methodHeaderLine.substring(0,
                                        methodHeaderLine.indexOf("(") + 1)
                                        + methodParameterDeclarations + methodHeaderLine.substring(
                                        methodHeaderLine.indexOf(")"));
                                    // Andere Testfallkonfigurationen zur selben Methode, muessen nicht mehr beachtet werden
                                    break;	
                                }
                            }
                            if(!methodHasNewConfigs){
                                i++;
                                // Methode bleibt unveraendert, da es keine neuen Testfallkonfigs gibt
                                continue;	
                            }
                            if(listLines.get(testAnnotationIndex).contains(Variables.ANNOTATION_TEST5))
                                listLines.remove(testAnnotationIndex);
                            // Format der Methodensignatur ueberpruefen, um Anpassungen vornehmen zu koennen:
                            if(!methodHeaderLine.contains("{") && !methodHeaderLine.contains(" throws ")
                                && (!listLines.get(i).contains("{") || listLines.get(i).contains("for") 
                                || listLines.get(i).contains("if") || listLines.get(i).contains("do") 
                                || listLines.get(i).contains("while") || listLines.get(i).contains("switch")
                                || listLines.get(i).contains("[]"))
                                && !listLines.get(i).contains(" throws ")
                                && !listLines.get(i).contains("\tthrows ")){
                                methodHeaderLine += " throws Exception {";
                            }
                            else if(methodHeaderLine.contains("{") && !methodHeaderLine.contains(" throws ")
                                && (!listLines.get(i).contains("{") || listLines.get(i).contains("for") 
                                || listLines.get(i).contains("if") || listLines.get(i).contains("do") 
                                || listLines.get(i).contains("while") || listLines.get(i).contains("switch")
                                || listLines.get(i).contains("[]"))  && !listLines.get(i).contains(" throws ")
                                && !listLines.get(i).contains("\tthrows ")){
                                methodHeaderLine = methodHeaderLine.substring(0, methodHeaderLine.indexOf("{")) 
                                	+ " throws Exception {"
                                    + methodHeaderLine.substring(methodHeaderLine.indexOf("{") + 1);
                            }
                            else if(!methodHeaderLine.contains("{") && methodHeaderLine.contains(" throws ")
                                && !listLines.get(i).contains("{") && !listLines.get(i).contains(" throws ")
                                && !listLines.get(i).contains("\tthrows ")){
                                methodHeaderLine += " { ";
                            }
                            else if(!methodHeaderLine.contains("{") && !methodHeaderLine.contains(" throws ")
                                && listLines.get(i).contains("{") && !listLines.get(i).contains(" throws ")
                                && !listLines.get(i).contains("\tthrows ")){
                                methodHeaderLine += " throws Exception ";
                            }
                            listLines.add(i, methodHeaderLine);

                            /*
                                Es wird die Zeile benoetigt, in der sich die '}' befindet, die das Ende der Methode makiert.
                                Dazu wird zu Beginn ueberprueft, wie die Methode/der Methodenkopf formatiert ist
                             */
                            ArrayList<Character> headerCharsList = new ArrayList<>();
                            int bracketCount = getBracketCount(methodHeaderLine, headerCharsList);
                            ArrayList<String> copyOfMethodBodyIndented = new ArrayList<>();
                            ArrayList<Character> copyOfMethodBodyInChars = new ArrayList<>();
                            boolean methodBodyStartReached = false;
                            if(bracketCount == 0){ // void func(){return;} oder: void func()\n{\nreturn;}
                                if(methodHeaderLine.contains("{")
                                    && methodHeaderLine.contains("}")){	// void func(){return;}
                                    i = handleMethodFormatType1(listLines, i, bodyMethodHeaderLine,
                                        methodParameterNames, bodyMethodName,
                                        headerCharsList, copyOfMethodBodyInChars,
                                        methodBodyStartReached);
                                }
                                else {	// void func()\n{\nreturn;}
                                    i = handleMethodFormatType2(listLines, i, bodyMethodHeaderLine,
                                        methodParameterNames, bodyMethodName,
                                        copyOfMethodBodyIndented);
                                }
                            }
                            else if(bracketCount == 1){ 	// Beispiel: void func {\nreturn;}
                                i = handleMethodFormatType3(listLines, i, bodyMethodHeaderLine,
                                    methodParameterNames, bodyMethodName, bracketCount,
                                    copyOfMethodBodyIndented);
                            }
                        }
                        else if(listLines.get(i).contains(Variables.ANNOTATION_TEST5_PARAMETERIZED)
                            || listLines.get(i).contains(Variables.ANNOTATION_TEST5_REPEATED)
                            || listLines.get(i).contains(Variables.ANNOTATION_TEST5_FACTORY)
                            || listLines.get(i).contains(Variables.ANNOTATION_TEST5_TEMPLATE)){
                            ArrayList<String> methodAnnotations = new ArrayList<>();
                            List<Integer> annotationIndices = new ArrayList<>();
                            while(!listLines.get(i).contains(" " + Variables.METHOD_START_VOID + " ")
                                && !listLines.get(i).contains("\t" + Variables.METHOD_START_VOID + " ")
                                && !listLines.get(i).contains(Variables.DYNAMIC_NODE)
                                && !listLines.get(i).contains(Variables.DYNAMIC_TEST)
                                && !listLines.get(i).contains(Variables.DYNAMIC_CONTAINER)){
                                methodAnnotations.add(listLines.get(i));
                                annotationIndices.add(i);
                                i++;
                            }
                            String methodHeaderLine = listLines.get(i);

                            boolean methodHasNewConfigs = false;
                            ArrayList<TestResultInfo> testMethods = super.getTestObject().getTestMethods();
                            String methodName, bodyMethodParameterDeclarations, bodyMethodHeaderLine = "";
                            StringBuilder methodParameterNames = new StringBuilder();

                            // Ueberpruefen, ob es zu der Methode neue Testkonfigurationen gibt
                            for(TestResultInfo method : testMethods) {
                                methodName = method.getName();
                                if (methodHeaderLine.contains(methodName)) {
                                    methodHasNewConfigs = true;
                                    /* Die sich oberhalb der Test-Annotation befindlichen und zur Testmethode 
                                       zugehoerigen Annotations ebenfalls einsammeln: */
                                    for(int j = annotationIndices.stream().min(Integer::compare).get() - 1; j >= 0 
                                    	&& listLines.get(j).contains("@")
                                        && !listLines.get(j).contains("*/") && !listLines.get(j).contains("//"); j--){
                                        annotationIndices.add(j);
                                        methodAnnotations.add(listLines.get(j));
                                    }
                                    nonStandardTestMethodsAnnotations.put(methodName, methodAnnotations);
                                    if(!methodHeaderLine.contains("{"))
                                        nonStandardTestMethodHeaders.put(methodName, methodHeaderLine + " {");
                                    else
                                        nonStandardTestMethodHeaders.put(methodName, methodHeaderLine);
                                    StringBuilder originalParams = new StringBuilder(),
                                        originalParamsNames = new StringBuilder();
                                    extractOrigArgsFromMethodHeader(methodHeaderLine, originalParams);
                                    extractOrigArgsNames(originalParams, originalParamsNames);
                                    nonStandardTestMethodOrigParams.put(methodName, originalParams.toString());
                                    nonStandardTestMethodOrigParamsNames.put(methodName, originalParamsNames.toString());
                                    StringBuilder methodParameterDeclarations = new StringBuilder();
                                    bodyMethodParameterDeclarations = constructNewArgsForMethodHeaders(methodParameterNames,
                                        method, originalParams, originalParamsNames, methodParameterDeclarations);
                                    bodyMethodHeaderLine = constructBodyMethodHeaderLine(methodHeaderLine, bodyMethodParameterDeclarations,
                                        methodName, methodName);
                                    // Andere Testfallkonfigurationen zur selben Methode, muessen nicht mehr beachtet werden.
                                    break;	
                                }
                            }
                            if(!methodHasNewConfigs){
                                i++;
                                continue;	// Methode bleibt unveraendert, da es keine neuen Testfallkonfigs gibt
                            }
                            annotationIndices.sort(Comparator.naturalOrder());
                            int methodHeaderIndex = i - annotationIndices.size();
                            int helperIndex = 0;
                            while(!annotationIndices.isEmpty()) {
                                Integer index = annotationIndices.remove(0);
                                listLines.remove(index - helperIndex);
                                helperIndex++;
                            }
                            if(listLines.get(methodHeaderIndex).contains(" " + Variables.METHOD_START_VOID + " ")
                                || listLines.get(methodHeaderIndex).contains("\t" + Variables.METHOD_START_VOID + " ")
                                || listLines.get(methodHeaderIndex).contains(Variables.DYNAMIC_NODE)
                                || listLines.get(methodHeaderIndex).contains(Variables.DYNAMIC_TEST)
                                || listLines.get(methodHeaderIndex).contains(Variables.DYNAMIC_CONTAINER)){
                                listLines.set(methodHeaderIndex, bodyMethodHeaderLine);
                            }
                        }
                    }
                    int lastLine = listLines.size() - 1;
                    while(!listLines.get(lastLine).contains("}") && lastLine > 0){
                        lastLine--;
                    }
                    listLines.add(lastLine++, "");
                    listLines.addAll(lastLine, getJUnitMethods());
                    if(anyNewStandardTestConfigsExist){
                        listLines.addAll(createProviderClass(sortedConfigurations));
                        listLines.add(1,"import static org.junit.jupiter.params.provider.Arguments.arguments;"
                            + "\nimport java.util.stream.Stream;"
                            + "\nimport org.junit.jupiter.params.provider.MethodSource;"
                            + "\nimport org.junit.jupiter.params.ParameterizedTest;"
                            + "\nimport org.junit.jupiter.params.provider.Arguments;");
                    }
                    listLines.add(2, "import static org.junit.jupiter.api.Assertions.assertThrows;");
                    for (String line : listLines) {
                        /*
                         *  Changes the old test-class name (i.e. MultiplierTest)
                         *  with new one (i.e. MultiplierTestKBUnit) to avoid ambiguity
                         */
                        if(line.contains(fromClassName) && !line.contains(toClassName)){
                            line = line.replace(fromClassName, toClassName);
                            if(!line.contains("public") && !line.contains("@MethodSource(")){
                                line = "public " + line;
                            }
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

    /**
     * Handling the method format, when curly bracket
     * is part of method header line.
     * method format looks like this: void func(){\nreturn;}
     * @return line index
     */
    private int handleMethodFormatType3(List<String> listLines, int i, String bodyMethodHeaderLine, 
    	StringBuilder methodParameterNames,
        String bodyMethodName, int bracketCount, ArrayList<String> copyOfMethodBodyIndented) {
        int lineIndex = i + 1, bodyLinesCount = 0;
        // Ende des Methoden-Koerper finden:
        while(bracketCount != 0 && lineIndex < listLines.size()){
            for(char c : listLines.get(lineIndex).toCharArray()){
                if(c == '}'){
                    bracketCount--;
                }
                else if(c == '{'){
                    bracketCount++;
                }
            }
            if(bracketCount != 0){
                copyOfMethodBodyIndented.add(listLines.get(lineIndex));
            }
            listLines.remove(lineIndex);
            bodyLinesCount++;
        }
        if(bracketCount == 0){
            listLines.add(i + 1, "\t\t" + "if(exceptionExpected) {");
            listLines.add(i + 2, "\t\t\t" + "assertThrows(Exception.class, () -> {");
            listLines.add(i + 3, "\t\t\t\t" + bodyMethodName + "(" + methodParameterNames + ");");
            listLines.add(i + 4, "\t\t\t});\n\t\t}\n\t\telse {\t");
            listLines.add(i + 5, "\t\t\t" + bodyMethodName + "(" + methodParameterNames + ");");
            listLines.add(i + 6, "\t\t}\n\t}\n");
            if(bodyMethodHeaderLine.contains("{") && !bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine = bodyMethodHeaderLine.substring(0, bodyMethodHeaderLine.indexOf("{")) 
                	+ " throws Exception {";
            }
            else if(!bodyMethodHeaderLine.contains("{") && !bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine += " throws Exception {";
            }
            else if(!bodyMethodHeaderLine.contains("{") && bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine += " { ";
            }
            listLines.add(i + 7, bodyMethodHeaderLine);
            listLines.addAll(i + 8, copyOfMethodBodyIndented);
            listLines.add(bodyLinesCount + i + 7, "\t}\n");
            i += bodyLinesCount + 7;
        }
        return i;
    }

    /**
     * Handling the method format, when the curly bracket
     * has its own line
     * method format looks like this: void func()\n{\nreturn;}
     * @return line index
     */
    private int handleMethodFormatType2(List<String> listLines, int i, String bodyMethodHeaderLine, 
    	StringBuilder methodParameterNames,
        String bodyMethodName, ArrayList<String> copyOfMethodBodyIndented) {
        int bracketCount;
        bracketCount = 1;
        int lineIndex = i + 2, bodyLinesCount = 0;
        System.out.println("listLines.get(lineIndex) = " + listLines.get(lineIndex));
        // Ende des Methoden-Koerper finden:
        while(bracketCount != 0 && lineIndex < listLines.size()){
            for(char c : listLines.get(lineIndex).toCharArray()){
                if(c == '}'){
                    bracketCount--;
                }
                else if(c == '{'){
                    bracketCount++;
                }
            }
            if(bracketCount != 0){
                copyOfMethodBodyIndented.add(listLines.get(lineIndex));
            }
            listLines.remove(lineIndex);
            bodyLinesCount++;
        }
        if(bracketCount == 0){
            listLines.add(i + 2, "\t\t" + "if(exceptionExpected) {");
            listLines.add(i + 3, "\t\t\t" + "assertThrows(Exception.class, () -> {");
            listLines.add(i + 4, "\t\t\t\t" + bodyMethodName + "(" + methodParameterNames + ");");
            listLines.add(i + 5, "\t\t\t});\n\t\t}\n\t\telse {\t");
            listLines.add(i + 6, "\t\t\t" + bodyMethodName + "(" + methodParameterNames + ");");
            listLines.add(i + 7, "\t\t}\n\t}\n");
            if(!bodyMethodHeaderLine.contains("{") && !bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine += " throws Exception {";
            }
            else if(bodyMethodHeaderLine.contains("{") && !bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine = bodyMethodHeaderLine.substring(0, bodyMethodHeaderLine.indexOf("{")) 
                	+ " throws Exception {";
            }
            else if(!bodyMethodHeaderLine.contains("{") && bodyMethodHeaderLine.contains(" throws ")){
                bodyMethodHeaderLine += " { ";
            }
            listLines.add(i + 8, bodyMethodHeaderLine);
            listLines.addAll(i + 9, copyOfMethodBodyIndented);
            listLines.add(bodyLinesCount + i + 8, "\t}\n");
            i += bodyLinesCount + 8;
        }
        return i;
    }

    /**
     * Handling the one-liner method format.
     * Method format looks like this: void func(){return;}
     * @return line index
     */
    private int handleMethodFormatType1(List<String> listLines, int i,
        String bodyMethodHeaderLine,
        StringBuilder methodParameterNames,
        String bodyMethodName,
        ArrayList<Character> headerCharsList,
        ArrayList<Character> copyOfMethodBodyInChars,
        boolean methodBodyStartReached) {
        int bracketCount;
        ArrayList<Character> newMethodHeader = new ArrayList<>();
        bracketCount = 1;
        for (char current_char : headerCharsList) {
            if (methodBodyStartReached) {
                copyOfMethodBodyInChars.add(current_char);
                if (current_char == '}') {
                    bracketCount--;
                } else if (current_char == '{') {
                    bracketCount++;
                }
                if (bracketCount == 0) {
                    copyOfMethodBodyInChars.remove(copyOfMethodBodyInChars.size() - 1);
                }
            } else {
                newMethodHeader.add(current_char);
            }
            if (current_char == '{') {
                methodBodyStartReached = true;
            }
        }
        List<Character> bodyMethodHeaderChars = bodyMethodHeaderLine.chars()
                .mapToObj(e->(char)e).collect(Collectors.toList());
        int charsIndex = 0;
        while(charsIndex < bodyMethodHeaderChars.size() && bodyMethodHeaderChars.get(charsIndex) != '{'){
            charsIndex++;
        }
        if(bodyMethodHeaderChars.get(charsIndex) == '{'){
            charsIndex++;
            while(charsIndex < bodyMethodHeaderChars.size()){
                bodyMethodHeaderChars.remove(charsIndex);
            }
        }
        String newMethodHeaderString = newMethodHeader.stream()
            .map(Object::toString)
            .collect(Collectors.joining());
        String methodBody = copyOfMethodBodyInChars.stream()
            .map(Object::toString)
            .collect(Collectors.joining());
        String newBodyMethodHeaderString = bodyMethodHeaderChars.stream()
            .map(Object::toString)
            .collect(Collectors.joining());
        listLines.set(i, newMethodHeaderString);
        listLines.add(i + 1, "\t\t" + "if(exceptionExpected) {");
        listLines.add(i + 2, "\t\t\t" + "assertThrows(Exception.class, () -> {");
        listLines.add(i + 3, "\t\t\t\t" + bodyMethodName + "("
            + methodParameterNames + ");");
        listLines.add(i + 4, "\t\t\t});\n\t\t}\n\t\telse {\t");
        listLines.add( i + 5, "\t\t\t" + bodyMethodName + "("
            + methodParameterNames + ");");
        listLines.add(i + 6, "\t\t}\n\t}\n");
        listLines.add(i + 7, newBodyMethodHeaderString);
        listLines.add(i + 8, "\t\t" + methodBody);
        listLines.add(i + 9, "\t}");
        i += 9;
        return i;
    }

    /**
     * Zaehlt die Differenz zwischen oeffnenden-
     * und schliessenden geschweiften Klammern
     * @return Integer, der fuer die Differenz zwischen oeffnenden-
     * und schliessenden geschweiften Klammern steht
     */
    private int getBracketCount(String methodHeaderLine,
        ArrayList<Character> headerCharsList) {
        int bracketCount = 0;
        for(char c : methodHeaderLine.toCharArray()){
            headerCharsList.add(c);
            if(c == '{'){
                bracketCount++;
            }
            else if(c == '}'){
                bracketCount--;
            }
        }
        return bracketCount;
    }

    private String constructBodyMethodHeaderLine(String methodHeaderLine,
        String bodyMethodParameterDeclarations,
        String oldMethodName,
        String newMethodName) {
        String bodyMethodHeaderLine;
        if(methodHeaderLine.contains("{") && methodHeaderLine.contains("}")){  // One-Liner Methode
            String bodyMethodHeaderLineNoParams = (methodHeaderLine.substring(0,
                methodHeaderLine.indexOf("(") + 1)
                + methodHeaderLine.substring(methodHeaderLine.indexOf(")")))
                .replace(oldMethodName, newMethodName);
            bodyMethodHeaderLine = bodyMethodHeaderLineNoParams.substring(0,
                bodyMethodHeaderLineNoParams.indexOf("(") + 1)
                + bodyMethodParameterDeclarations + bodyMethodHeaderLineNoParams
                .substring(bodyMethodHeaderLineNoParams.indexOf(")"));
        }
        else{
            String bodyMethodHeaderLineNoParams = (methodHeaderLine.substring(0,
                methodHeaderLine.indexOf("(") + 1)
                + methodHeaderLine.substring(methodHeaderLine.lastIndexOf(")")))
                .replace(oldMethodName, newMethodName);
            bodyMethodHeaderLine = bodyMethodHeaderLineNoParams.substring(0,
                bodyMethodHeaderLineNoParams.indexOf("(") + 1)
                + bodyMethodParameterDeclarations + bodyMethodHeaderLineNoParams
                .substring(bodyMethodHeaderLineNoParams.lastIndexOf(")"));
        }
        if(!bodyMethodHeaderLine.contains("private")){
            bodyMethodHeaderLine = "\tprivate " + bodyMethodHeaderLine.trim();
        }
        return bodyMethodHeaderLine;
    }
  
    private String constructNewArgsForMethodHeaders(StringBuilder methodParameterNames,
        TestResultInfo method, StringBuilder originalParams,
        StringBuilder originalParamsNames, StringBuilder methodParameterDeclarations) {
        String bodyMethodParameterDeclarations = "";
        ArrayList<TestParameterInfo> listParameters = method.getParameters();
        int argIndex = 0;
        for (TestParameterInfo param : listParameters) {
            methodParameterDeclarations.append(param.getClassType().getTypeName())
                .append(" ").append(param.getName()).append(", \n\t\t");
            if(argIndex < listParameters.size() - 1)
                methodParameterNames.append(param.getName()).append(", ");
            else
                methodParameterNames.append(param.getName());
            argIndex++;
        }  
        if(methodParameterDeclarations.length() >= 5) {
            bodyMethodParameterDeclarations = methodParameterDeclarations.substring(0,
                methodParameterDeclarations.length() - 5);
        }
        if(!originalParams.toString().equals("")){
            methodParameterDeclarations.append("boolean exceptionExpected, ")
                .append(originalParams);
            bodyMethodParameterDeclarations += ", \n\t\t" + originalParams;
            methodParameterNames.append(", ").append(originalParamsNames);
        }
        else{
            methodParameterDeclarations.append("boolean exceptionExpected");
        }
        return bodyMethodParameterDeclarations;
    }

    private void extractOrigArgsNames(StringBuilder originalParams,
                                      StringBuilder originalParamsNames) {
        StringBuilder originalParamsMinusAnnotations = removeAnnotationsFromMethodParams(originalParams);
        if(originalParamsMinusAnnotations.toString().trim().length() > 1){
            String[] splitArray = originalParamsMinusAnnotations.toString().split(",");
            int paramsCounter = 0;
            for(String param : splitArray){
                param = param.trim();
                if(paramsCounter < splitArray.length - 1)
                    originalParamsNames.append(param.split(" ")[1]).append(", ");
                else
                    originalParamsNames.append(param.split(" ")[1]);
                paramsCounter++;
            }
        }
    }

    private StringBuilder removeAnnotationsFromMethodParams(StringBuilder originalParams) {
        originalParams.insert(0, "(").append(")");
        StringBuilder originalParamsMinusAnnotations = new StringBuilder();
        boolean openingBracketReached = false;
        int bracketStack = 0;
        boolean annotationDetected = false;
        for (char c : originalParams.toString().toCharArray()) {
            if (c == ')') {
                bracketStack--;
                if(bracketStack == 0)
                    break;
            }
            if (openingBracketReached) {
                if(c == '@'){
                    annotationDetected = true;
                }
                if(annotationDetected && c == ' ' && bracketStack <= 1){
                    annotationDetected = false;
                }
                if(!annotationDetected && bracketStack <= 1){
                    originalParamsMinusAnnotations.append(c);
                }
            }
            if (c == '(') {
                openingBracketReached = true;
                bracketStack++;
            }
        }
        originalParams.deleteCharAt(0).deleteCharAt(originalParams.length() - 1);
        return originalParamsMinusAnnotations;
    }

    private void extractOrigArgsFromMethodHeader(String methodHeaderLine,
        StringBuilder originalParams) {
        boolean openingBracketReached = false;
        int bracketStack = 0;
        for (char c : methodHeaderLine.toCharArray()) {
            if (c == ')') {
                bracketStack--;
                if(bracketStack == 0)
                    break;
            }
            if (openingBracketReached) {
                originalParams.append(c);
            }
            if (c == '(') {
                openingBracketReached = true;
                bracketStack++;
            }
        }
    }

    /*
     * the content of an annotation of a test method, which shall be copied
     * @see srlab.kbunit.plugin.file.FileCreatorJUnit#createAnnotation(
     * prlab.kbunit.plugin.test.TestMethod)
     */
    protected String createAnnotations(TestResultInfo testMethod){
        return "\t@ParameterizedTest\n" + "\t@MethodSource(\""
            + testMethod.getPath().substring(0, testMethod.getPath().indexOf("."))
            + "." + toClassName + "_Provider#" + testMethod.getName()
            + "_Provider\")";
    }

    /*
     * the content of an annotation of a test method, which shall be copied
     * @see srlab.kbunit.plugin.file.FileCreatorJUnit#createAnnotation(
     * prlab.kbunit.plugin.test.TestMethod)
     */
    protected String createAnnotations(ArrayList<String> methodAnnotations){
        StringBuilder result = new StringBuilder();
        int argCounter = 0;
        for(String annotation : methodAnnotations){
            if(argCounter < methodAnnotations.size() - 1)
                result.append(annotation).append("\n");
            else
                result.append(annotation);
            argCounter++;
        }
        return result.toString();
    }

    /*
     * Returns list of all new test methods (except for the ones annotated with the standard @Test)
     * prepared for copying a given test source file. Exception handling is also included.
     */
    @Override
    protected List<String> getJUnitMethods() {
        ArrayList<String> listAll    = new ArrayList<>();
        ArrayList<String> listMethod = new ArrayList<>();
        for (Map.Entry<String, ArrayList<TestResultInfo>> methodEntry : sortedConfigurations.entrySet()) {
            if(nonStandardTestMethodsAnnotations.containsKey(methodEntry.getKey())) {
                ArrayList<TestResultInfo> methods = methodEntry.getValue(); // all have same method name
                for(TestResultInfo testMethod : methods) {
                    //-------------------ADDING COMMENTARY BEFORE Test-ANNOTATION-----------------------
                    listMethod.add("\t// "+ new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
                         .format(testMethod.getDate()));
                    //------------------ADDING Annotation/s and method header-------------------
                    listMethod.add(createAnnotations(nonStandardTestMethodsAnnotations
                    	.get(methodEntry.getKey())));

                    String methodName = testMethod.getName();
                    String methodHeader = nonStandardTestMethodHeaders.get(methodName);
                    if(methodHeader.contains("{") && methodHeader.contains("}")){  // Format: void func{return;}
                        methodHeader = methodHeader.substring(0, methodHeader.indexOf("{") + 1);
                    }
                    if(!methodHeader.contains(" throws "))
                        methodHeader = methodHeader.substring(0, methodHeader.indexOf("{")) + " throws Exception {";
                    //---------------------ADDING TEST PARAMETER TO CURRENT TEST METHOD-------------------
                    List<TestParameterInfo> listParameters = testMethod.getParameters();
                    StringBuilder parametersForMethodCall = new StringBuilder();
                    Object value;
                    int argIndex = 0;
                    for (TestParameterInfo param : listParameters) {
                        argIndex++;
                        value = param.getValue();
                        if (argIndex < listParameters.size()) {
                            if (value != null) {
                                parametersForMethodCall.append(super.adjustValue(param.getClassType(),
                                        value.toString())).append(", ");
                            }
                            else {
                                parametersForMethodCall.append(super.adjustValue(param.getClassType(),
                                        null)).append(", ");
                            }
                        }
                        else {
                            if (value != null) {
                                parametersForMethodCall.append(super.adjustValue(param.getClassType(),
                                        value.toString()));
                            }
                            else {
                                parametersForMethodCall.append(super.adjustValue(param.getClassType(),
                                        null));
                            }
                        }
                    }
                    String newConfigMethodName = methodName + "_" + testMethod.getId();
                    String methodHeaderLine;
                    if(methodHeader.contains("{") && methodHeader.contains("}")){
                        String bodyMethodHeaderLineNoParams = (methodHeader.substring(0, methodHeader.indexOf("(") + 1)
                            + methodHeader.substring(methodHeader.indexOf(")"))).replace(methodName, newConfigMethodName);
                        methodHeaderLine = bodyMethodHeaderLineNoParams.substring(0, bodyMethodHeaderLineNoParams.indexOf("(") + 1)
                            + nonStandardTestMethodOrigParams.get(methodName) 
                            + bodyMethodHeaderLineNoParams.substring(bodyMethodHeaderLineNoParams.indexOf(")"));
                    }
                    else{
                        String bodyMethodHeaderLineNoParams = (methodHeader.substring(0, methodHeader.indexOf("(") + 1)
                            + methodHeader.substring(methodHeader.lastIndexOf(")"))).replace(methodName, newConfigMethodName);
                        methodHeaderLine = bodyMethodHeaderLineNoParams.substring(0, bodyMethodHeaderLineNoParams.indexOf("(") + 1)
                            + nonStandardTestMethodOrigParams.get(methodName) 
                            + bodyMethodHeaderLineNoParams.substring(bodyMethodHeaderLineNoParams.lastIndexOf(")"));
                    }
                    listMethod.add(methodHeaderLine);
                    if (nonStandardTestMethodOrigParamsNames.containsKey(methodName)
                        && nonStandardTestMethodOrigParamsNames.get(methodName) != null
                        && !nonStandardTestMethodOrigParamsNames.get(methodName).equals("")) {
                        parametersForMethodCall.append(", ").append(nonStandardTestMethodOrigParamsNames.get(methodName));
                    }
                    //  ADDING EVENTUALLY EXPECTED EXCEPTION
                    var isDynamicTest = methodHeaderLine.contains(Variables.DYNAMIC_NODE)
                        || methodHeaderLine.contains(Variables.DYNAMIC_TEST)
                        || methodHeaderLine.contains(Variables.DYNAMIC_CONTAINER);
                    if (testMethod.isExceptionExpected()) {
                        listMethod.add("\t\t" + "assertThrows(Exception.class, () -> "
                            + methodName + "(" + parametersForMethodCall + ")"
                            + ");");
                        if(isDynamicTest){
                            listMethod.add("\t\treturn null;");
                        }
                    } else {
                        if(isDynamicTest){
                            listMethod.add("\t\treturn "+ methodName +"(" + parametersForMethodCall + ");");
                        }
                        else{
                            //--------------------------ADDING SOURCE-TEST-METHOD---------------------------------
                            listMethod.add(createMethodCall(testMethod, parametersForMethodCall.toString()));
                        }
                    }

                    //------------------------------END OF TEST METHOD------------------------------------
                    listMethod.add("\t}\n");

                    //--------------ADDING COLLECTION TO LINE-LIST OF CURRENT TEST-SOURCE-----------------
                    listAll.addAll(listMethod);

                    listMethod.clear();
                }
            }
        }
        return listAll;
    }

    @Override
    protected String createAnnotation(TestResultInfo testMethod) {
        return null;
    }

    @Override
    protected String createMethodCall(TestResultInfo testMethod) {
        return "\t\t"+ testMethod.getName() +"();";
    }

    /*
     * Creates the call of the source method
     */
    protected String createMethodCall(TestResultInfo testMethod, String parametersForMethodCall){
        return "\t\t"+ testMethod.getName() +"(" + parametersForMethodCall + ");";
    }

    /**
     * Returns the class that contains the methods that provide the JUnit5 test methods
     * with arguments.
     */
    protected List<String> createProviderClass(Map<String,
            ArrayList<TestResultInfo>> sortedConfigurations){
        ArrayList<String> providerClass_Lines = new ArrayList<>();
        String providersClassName = toClassName + "_Provider";
        String classLine = "\nclass " + providersClassName + " {\n\t";
        providerClass_Lines.add(classLine);
        for (Map.Entry<String, ArrayList<TestResultInfo>> methodEntry :
            sortedConfigurations.entrySet()) {
            ArrayList<TestResultInfo> methods = methodEntry.getValue();
            if(!nonStandardTestMethodsAnnotations.containsKey(methods.get(0).getName()))
                providerClass_Lines.addAll(createProviderMethod(methods));
        }
        providerClass_Lines.add("}");
        return providerClass_Lines;
    }

    /**
     * Returns the method that provides the respective JUnit5 test method with arguments
     * with arguments.
     */
    protected ArrayList<String> createProviderMethod(ArrayList<TestResultInfo> methods){
        ArrayList<String> providerClassMethodsLines = new ArrayList<>();
        providerClassMethodsLines.add("\tstatic Stream<Arguments> "
            + methods.get(0).getName() + "_Provider() {");
        providerClassMethodsLines.add("\t\treturn Stream.of(");
        int methodIndex = 0;
        for(TestResultInfo testMethod : methods) {
            ArrayList<TestParameterInfo> testMethodParameters =
                    testMethod.getParameters();
            StringBuilder argumentBuilder = new StringBuilder("\t\t\targuments(");
            for(TestParameterInfo testParameterInfo : testMethodParameters){
                if (testParameterInfo.getClassType() == char.class) {
                    argumentBuilder.append("'").append(testParameterInfo.getValue()).append("', ");
                } else if (testParameterInfo.getClassType() == String.class) {
                    argumentBuilder.append("\"").append(testParameterInfo
                            .getValue()).append("\", ");
                } else {
                    argumentBuilder.append(testParameterInfo.getValue()).append(", ");
                }
            }
            if(testMethod.isExceptionExpected()){
                argumentBuilder.append("true");
            }
            else{
                argumentBuilder.append("false");
            }
            if(methodIndex < methods.size() - 1)
                argumentBuilder.append("), ");
            else
                argumentBuilder.append(") ");
            // Ein Kommentar mit ID und Erstellungsdatum der Testkonfiguration erstellen:
            argumentBuilder.append("// ID_").append(testMethod.getId()).append(" ")
                .append(new SimpleDateFormat(Variables.FORMAT_FOR_DATE_TIME)
                .format(testMethod.getDate()));
            providerClassMethodsLines.add(argumentBuilder.toString());
            methodIndex++;
        }
        providerClassMethodsLines.add("\t\t);\n\t}\n");
        return providerClassMethodsLines;
    }

}
