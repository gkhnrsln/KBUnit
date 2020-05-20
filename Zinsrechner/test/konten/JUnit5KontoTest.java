package konten;

import kunden.Kunde;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.condition.JRE.*;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

/**
 * Die Klasse enthaelt sowohl JUnit Tests der Version 5 zur Klasse Konto
 * als auch von der Klasse Konto unabhaengige Tests.
 * Hier werden ohne einen speziellen Anwendungsfall zahlreiche Features
 * von JUnit integriert, um diese dann in KBUnit testen zu koennen.
 *
 * @author Yannis Herbig
 */

@Tag("Konto")
@Execution(ExecutionMode.CONCURRENT)
public class JUnit5KontoTest {

    /*
     * Testvariablen, koennen vom Kunden eingestellt werden und muessen aus dem
     * Namen der verwendenden Testmethode, einem Unterstrich sowie einem fuer die
     * Testmethode einzigartigen Parameternamen zusammengesetzt sein
     */
    /** vom Wissenstraeger einstellbarer Nachname des Kunden des Kontos */
    public static String testSetKundeZumKonto_Nachname = "Musterperson";
    /** vom Wissenstraeger einstellbarer Nachname des Kunden des Kontos */
    public static String testSetKontoZumKunden_Nachname = "Musterperson";
    /** vom Wissenstraeger einstellbares Alter des Kunden des Kontos */
    public static int testRechneAlterMal2_Alter = 18;
    /** vom Wissenstraeger einstellbarer Musterstring */
    public static String testWithExternalMethodSource_Musterstring = "Musterstring";
    /** vom Wissenstraeger einstellbarer Musterstring */
    public static String testFuerDisabled_Musterstring = "Musterstring";

    // das zu testende Konto-Objekt
    private Konto k;

    private static Konto kC;

    @TempDir
    /** Test fuer die TempDir-Extension */
    Path tempDir;

    @BeforeAll
    /** Test fuer die Annotation BeforeAll */
    static void setUpBeforeClass() {
        kC = new Konto();
    }

    @BeforeEach
    /** Test fuer die Annotation BeforeEach */
    void setUp() {
        this.k = new Konto();
    }

    @AfterEach
    /** Test fuer die Annotation AfterEach */
    void tearDown() {
        this.k = null;
    }

    @AfterAll
    /** Test fuer die Annotation AfterAll */
    static void tearDownAfterClass() {
        kC = null;
    }

    /**
     * Test zum Setzen des Kunden zu dem Konto
     * @throws IOException , falls die zu testende Methode eine IOException wirft
     */
     @Test
     @DisplayName("Setze Kunde zum Konto")
     @EnabledOnOs(WINDOWS)
     void testSetKundeZumKonto() throws IOException, InterruptedException {
        System.out.println("Start von testSetKundeZumKonto");
        Thread.sleep(3000);
        Path file = Files.createFile(
            tempDir.resolve("test.txt")
        );
        this.k.setKunde(new Kunde("Elke", testSetKundeZumKonto_Nachname));
        assertEquals(testSetKundeZumKonto_Nachname, this.k.getKunde().getNachname(),
            "Der Kunde wurde zum Konto nicht gesetzt");
        assertNotNull(kC, () -> "Das Konto kC wurde nicht kreiert.");
        assertTrue(Files.isDirectory(this.tempDir), "Sollte ein Ordner sein");
        assertTrue(Files.exists(file), "Datei existiert nicht");
        System.out.println("Ende von testSetKundeZumKonto");
    }

    /**
     * Test zum Setzen des Konto zu dem Kunden
     * @throws IOException , falls die zu testende Methode eine IOException wirft
     */
    @Test
    @EnabledOnJre(JAVA_11)
    void testSetKontoZumKunden(TestInfo testInfo, TestReporter testReporter) throws Exception
    {
        System.out.println("Start von testSetKontoZumKunden");
        testReporter.publishEntry("Thread.currentThread().getName(): ", 
        	Thread.currentThread().getName());
        Thread.sleep(3000);
        this.k.setKunde(new Kunde("Elke", testSetKontoZumKunden_Nachname));
        assertEquals(testSetKundeZumKonto_Nachname, this.k.getKunde().getNachname(),
            "Der Kunde wurde zum Konto nicht gesetzt");
//        throw new Exception("Exception geworfen aus Demonstrationsgruenden");
        System.out.println("Ende von testSetKontoZumKunden");
    }

    /**
     * Test von Multiplikation.
     * Die Formatierung ist absichtlich so gewaehlt, als Test fuer den KBUnit-Runner
     */
    @Test
    @Tag("Alter")
    @Tag("Multiplikation")
    @DisplayName("Rechne Alter mal 2")
    void testRechneAlterMal2() throws InterruptedException { 
        System.out.println("Start von testRechneAlterMal2");
      	Thread.sleep(3000); 
    	assertEquals(testRechneAlterMal2_Alter + testRechneAlterMal2_Alter, 
    		2 * testRechneAlterMal2_Alter, "Fehler Multiplikation"); 
        System.out.println("Ende von testRechneAlterMal2");
    }

    /**
     * Dieser Testfall befindet sich nicht im Testfallverwaltungsdokument CustomerTestCaseInformation.xml,
     * wird aber in der zu exportierenden JAR als Teil dieser Klasse mit den anderen Testfaellen verpackt.
     */
    @Test
    void testWithArg(TestInfo testInfo) {
        System.out.println("testInfo.getClass().getTypeName(): " + testInfo.getClass().getTypeName());
        System.out.println("testInfo.getClass().getTypeName(): " + testInfo.getClass().getCanonicalName());
        System.out.println("testInfo.getClass().getTypeName(): " + testInfo.getClass().getSimpleName());
        assertEquals(2, 1 + 1);
    }
    
    @ParameterizedTest
    @MethodSource("tinyStrings")
    void testWithExternalMethodSource(String tinyString) throws InterruptedException { 
        System.out.println("Start von testWithExternalMethodSource");
        Thread.sleep(3000);
        Assertions.assertTrue(("uo".equals(tinyString) || "UO".equals(tinyString)) 
        	&& !"uO".equals(tinyString) && !"Uo".equals(tinyString));
        assertEquals("Musterstring", testWithExternalMethodSource_Musterstring);
        System.out.println("Ende von testWithExternalMethodSource");
    }
    
    static Stream<String> tinyStrings() {
    	return Stream.of("uo", "UO");
    }
  
    /**
     * Dieser Test ist erfolgreich, falls der Anwender Musterstring eingibt.
     */
    @Test
    @Disabled("Zur Demonstrierung des Ausgrauens")
    void testFuerDisabled() {
    	assertTrue(testFuerDisabled_Musterstring.equals("Musterstring"));
    }
 
}
