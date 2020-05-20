package konten;

import kunden.Kunde;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Die Klasse enthaelt JUnit Tests der Version 4 zur Klasse Konto.
 *
 * @author Ursula Oesing
 */
public class KontoTest {
	
	/*
     * Testvariablen, koennen vom Kunden eingestellt werden und muessen aus dem
     * Namen der verwendenden Testmethode, einem Unterstrich sowie einem fuer die
     * Testmethode einzigartigen Parameternamen zusammengesetzt sein
     */
	/** vom Wissenstraeger einstellbarer Nachname des Kunden des Kontos */
    public static String testSetKundeZumKonto_Nachname = "Musterperson";
    
    // das zu testende Konto-Objekt
	private Konto k;
 
    private static Konto kC;
    
    @Rule
    /** Test fuer die Annotation Rule */
    public TemporaryFolder folder = new TemporaryFolder();
    
    @BeforeClass
    /** Test fuer die Annotation BeforeClass */
	public static void setUpBeforeClass() throws Exception {
        kC = new Konto();
	}
    
 	@Before
 	/** Test fuer die Annotation Before */
	public void setUp() throws Exception {
        this.k = new Konto();
	}

	@After
	/** Test fuer die Annotation After */
	public void tearDown() throws Exception {
        this.k = null;
	}
	
	@AfterClass
	/** Test fuer die Annotation AfterClass */
	public static void tearDownAfterClass() throws Exception {
	    kC = null;
	}

	/**
     * Test zum Setzen des Kunden zu dem Konto
	 * @throws IOException , falls die zu testende Methode eine IOException wirft
     */
	@Test
    public void testSetKundeZumKonto() throws IOException {
     	File file = folder.newFile("test.txt");
        this.k.setKunde(new Kunde("Elke", testSetKundeZumKonto_Nachname));
        assertTrue("Der Kunde wurde zum Konto nicht gesetzt",
			testSetKundeZumKonto_Nachname.equals(this.k.getKunde().getNachname()));
        assertTrue("Das Konto kC wurde nicht kreiert.", kC != null);
    	assertTrue(file.exists());
    }
}
