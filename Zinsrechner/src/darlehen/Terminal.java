package darlehen;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;

/**
 * Terminal stellt die grafische Oberflaeche fuer Ein- und Ausgabe der
 * Darlehensberecnung zur Verfuegung und enthaelt die Main-Methode des Programms.
 *
 * @author Philipp Sprengholz
 */
public class Terminal extends JDialog {
    private static final long serialVersionUID = 1L;
    
	// Eingabefelder
    private final JLabel lblDarlehen = new JLabel("Darlehen");
    private final JTextField txtDarlehen = new JTextField();
    private final JLabel lblDarlehenHinweis = new JLabel("in EuroCent");
    private final JLabel lblLaufzeit = new JLabel("Laufzeit");
    private final JTextField txtLaufzeit = new JTextField();
    private final JLabel lblLaufzeitHinweis = new JLabel("in Raten");
    private final JLabel lblZinssatz = new JLabel("Zinssatz");
    private final JTextField txtZinssatz = new JTextField();
    private final JLabel lblZinssatzHinweis = new JLabel("in Prozent");
    // Ausgabefelder
    private final JPanel pnlAusgabe = new JPanel();
    private final JLabel lblAusgabe = new JLabel();
    private final JLabel lblGraph = new JLabel("Grafische Darstellung");
    private final JButton btnBerechnung = new JButton("Berechnung starten");

    private static final String CSV_FILE_PATH = "src/darlehen/tilgungsdarlehen.csv";
    private static CSVPrinter csvPrinter;
    private static BufferedWriter bufferedWriter;
    private String[] CSV_HEADER = { "Darlehen", "Laufzeit", "Zinssatz",
            "Inflationsrate", "Gesamtschuld", "Zeitstempel" };
    public final static String FORMAT_FOR_DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    /**
     * Konstruktor - erstellt das Fenster des Terminals
     */
    public Terminal() {
        super();
        this.initComponents();
        this.initListener();
        // Anzeige des Fensters
        this.setVisible(true);
        try {
            bufferedWriter = Files.newBufferedWriter(
        	    Paths.get(CSV_FILE_PATH),
        	    StandardOpenOption.CREATE,
        	    StandardOpenOption.APPEND);
        	    BufferedReader bufferedReader = new BufferedReader(new FileReader(CSV_FILE_PATH));
        	    csvPrinter = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT
        	        .withHeader(CSV_HEADER).withSkipHeaderRecord(
        	        bufferedReader.readLine() != null));
                csvPrinter.flush();
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * initialisiert alle grafischen Komponenten 
     */
    private void initComponents() {
        // Fenster
        this.setTitle("Kreditrechner (Tilgungsdarlehen)");
        this.setSize(420, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(null);
        this.setResizable(false);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Eingabefelder
        lblDarlehen.setBounds(13, 10, 80, 25);
        this.add(lblDarlehen);
        txtDarlehen.setBounds(100, 10, 190, 25);
        this.add(txtDarlehen);
        lblDarlehenHinweis.setBounds(300, 10, 100, 25);
        this.add(lblDarlehenHinweis);
        lblLaufzeit.setBounds(13, 40, 100, 25);
        this.add(lblLaufzeit);
        txtLaufzeit.setBounds(100, 40, 190, 25);
        this.add(txtLaufzeit);
        lblLaufzeitHinweis.setBounds(300, 40, 100, 25);
        this.add(lblLaufzeitHinweis);
        lblZinssatz.setBounds(13, 70, 100, 25);
        this.add(lblZinssatz);
        txtZinssatz.setBounds(100, 70, 190, 25);
        this.add(txtZinssatz);
        lblZinssatzHinweis.setBounds(300, 70, 100, 25);
        this.add(lblZinssatzHinweis);

        // Ausgabeelemente
        pnlAusgabe.setBounds(10, 220, 370, 240);
        this.add(pnlAusgabe);
        lblAusgabe.setBounds(10, 155, 370, 25);
        this.add(lblAusgabe);
        lblGraph.setVisible(false);
        lblGraph.setBounds(10, 190, 370, 25);
        this.add(lblGraph);

        // Button zur Ausfuehrung der Berechnung
        btnBerechnung.setBounds(10,110,370,35);
        this.add(btnBerechnung);
    }

    /*
     * initialisiert die Listener 
     */
    private void initListener() {
    	btnBerechnung.addActionListener((aEvent) ->
        {
    		try {
    		    // Erzeugung eines Darlehensrechner-Objektes
                Tilgungsdarlehen tilgungsdarlehen 
                    = new Tilgungsdarlehen(Integer.parseInt(txtDarlehen.getText()), 
                  	Integer.parseInt(txtLaufzeit.getText()), 
                   	Double.parseDouble(txtZinssatz.getText()));
                int berechneteGesamtschuld = tilgungsdarlehen.berechneGesamtschuld("Musterperson");
                    addCSVRecord(tilgungsdarlehen);
                // Ausgabe der Gesamtschuld
                lblAusgabe.setText("<html>Gesamtschuld:  " 
                    + "<span style=\"background-color: #dddddd;\"><b> &nbsp "
                    + berechneteGesamtschuld
                    + " EuroCent &nbsp </b></span></html>");
                // Ausgabe der Annuitaeten im Zeitverlauf
                    zeigeGrafikAn(tilgungsdarlehen);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "Die Angaben sind unvollständig oder fehlerhaft. " 
                    + "Bitte überprüfen Sie die eingegebenen Daten!", "Fehlermeldung", 
                    JOptionPane.ERROR_MESSAGE);
            }
         });
    	 this.addWindowListener(new WindowAdapter() {
             @Override
             public void windowClosed(WindowEvent e) {
                 try {
                     bufferedWriter.close();
                     csvPrinter.close();
                 } catch (IOException ex) {
                     ex.printStackTrace();
                 }
             }
         });
    }
    
    // zeigt eine Grafik zu den Annuitaeten an
    private void zeigeGrafikAn(Tilgungsdarlehen tilgungsdarlehen){
        Graphics2D g2d = (Graphics2D) pnlAusgabe.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(1, 1, pnlAusgabe.getWidth(), pnlAusgabe.getHeight());
        for (int i=1; i <= tilgungsdarlehen.getLaufzeit(); i++) { 
            g2d.setColor(new Color(140, 170, 255));
            g2d.fillRect(10+(pnlAusgabe.getWidth() - 20) 
            	/ tilgungsdarlehen.getLaufzeit() * (i - 1), 
            	pnlAusgabe.getHeight() - 10 
            	- tilgungsdarlehen.berechneAnnuitaetFuerPeriode(i) 
            	* (pnlAusgabe.getHeight() - 50) 
            	/ tilgungsdarlehen.berechneAnnuitaetFuerPeriode(1), 
            	(pnlAusgabe.getWidth()-20) 
            	/ tilgungsdarlehen.getLaufzeit() - 2, 
            	tilgungsdarlehen.berechneAnnuitaetFuerPeriode(i) 
            	* (pnlAusgabe.getHeight() - 50)
            	/tilgungsdarlehen.berechneAnnuitaetFuerPeriode(1));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g2d.setColor(Color.GRAY);
            g2d.drawString("" + tilgungsdarlehen
            	.berechneAnnuitaetFuerPeriode(i), 
            	11 + (pnlAusgabe.getWidth()-20) / tilgungsdarlehen.getLaufzeit() * (i-1), 
            	pnlAusgabe.getHeight() - 11 
            	- tilgungsdarlehen.berechneAnnuitaetFuerPeriode(i) 
            	* (pnlAusgabe.getHeight() - 50)
            	/ tilgungsdarlehen.berechneAnnuitaetFuerPeriode(1));
        }
        g2d.setColor(Color.GRAY);
        g2d.drawLine(5,pnlAusgabe.getHeight() - 10,pnlAusgabe.getWidth()-5,
        	pnlAusgabe.getHeight() - 10);
        g2d.drawLine(5,pnlAusgabe.getHeight() - 10, 5, 12);
        g2d.setColor(Color.GRAY);
        g2d.drawString("Annuitäten", 2, 10);
        g2d.drawString("Laufzeit", pnlAusgabe.getWidth() - 45, 
        	pnlAusgabe.getHeight());
        lblGraph.setVisible(true);
    }

    public void addCSVRecord(Tilgungsdarlehen tilgungsdarlehen) throws IOException {
        if(csvPrinter != null){
            try {
                //String zeitstempel = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                String zeitstempel = DateTimeFormatter.ofPattern(FORMAT_FOR_DATE_TIME)
                       .format(tilgungsdarlehen.getErstellungsZeitstempel());
                csvPrinter.printRecord(tilgungsdarlehen.getDarlehen(), tilgungsdarlehen.getLaufzeit(),
                       (int) tilgungsdarlehen.getZinssatz() * 100, (int) tilgungsdarlehen.getInflationsrate() * 100,
                       tilgungsdarlehen.getGesamtschuld(), zeitstempel);
                csvPrinter.flush();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    // main-Methode fuer Erzeugung eines neuen Fensters
    public static void main(String... args) {
        try {
            // Vereinbarung eines fuer die gesamte Anwendung geltenden Look&Feels (L&F)
            UIManager.put("control", Color.WHITE);
            UIManager.setLookAndFeel(
            	"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        }
        catch (Exception ex) {
            // Ausnahmebehandlung nicht notwendig, da auch mit anderem L&F 
        	// gearbeitet werden kann
        }
        // Erzeugung eines neuen Terminal-Objektes
        new Terminal(); 
    }
}
