package main.panel.navigation;

import hilfe.guiHilfe.CurvedTitledBorder;
import hilfe.guiHilfe.FilterComboBox;
import hilfe.guiHilfe.SpecialColor;
import main.dialogfenster.InfoDialog;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.testCaseInfo.TestCaseInfo;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * TestCasePopupMenu stellt ein PopupMenue fuer die Baumdarstellung aller 
 * geoeffneten Testfaelle zur Verfuegung und ermoeglich beispielsweise die
 * Ausfuehrung aller Konfigurationen aller im Baum selektierter Testfaelle
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing, Yannis Herbig  <br>
 * @author Philipp Sprengholz
 */

public class TestCasePopupMenu extends JPopupMenu
{
	private static final long serialVersionUID = 1L;
	
	// Menuepunkt zum Oeffnen von Testfaellen
	private JMenuItem mnPopupOpen = new JMenuItem();
	// Menuepunkt zum Ausfuehren von Testfaellen
    private JMenuItem mnPopupRerun = new JMenuItem();
    // Menuepunkt zum Ausfuehren von Testfaellen mit Tags
    private JMenuItem mnPopupRerunAllWithTag = new JMenuItem();
    // Liste von Testfaellen
    private final ArrayList<TestCaseInfo> tcilist;
    boolean tcilistHasJunit5TestCase;

    /**
     * Konstruktor, erwartet das Hauptanwendungsfenster 
     * sowie die Liste der im
     * Baum markierten Testfaelle
     *
     * @param parent Hauptanwendungsfenster
     * @param tl Liste der selektierten Testfaelle
     */
    public TestCasePopupMenu(final BasicFrameView parent, 
    	ArrayList<TestCaseInfo> tl)
    {
        super();

        this.tcilist = tl;
        this.setBackground(SpecialColor.WHITE);
        CurvedTitledBorder ctb 
            = new CurvedTitledBorder(1, "Optionen", SpecialColor.ORANGE);
        ctb.abstandUnten = 1;
        this.setBorder(ctb);
        this.setOpaque(false);

        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        if (tl.size() == 1 && tl.get(0).getNumberOfAllTestConfigurations() == 0)
        {
            this.mnPopupOpen.setText("Test öffnen");
            this.mnPopupOpen.setBorder(new EmptyBorder(1, 6, 1, 6));
            gbc.insets = new Insets(0, 0, -7, 0);
            gbc.gridy = 0;
            this.add(this.mnPopupOpen, gbc);
        }
        else if(tl.size() == 1 && tl.get(0).getNumberOfAllTestConfigurations() > 0)
        {
            this.mnPopupOpen.setText("Test öffnen");
            this.mnPopupOpen.setBorder(new EmptyBorder(1, 6, 1, 6));
            gbc.gridy = 0;
            this.add(this.mnPopupOpen, gbc);

            this.mnPopupRerun.setText("alle enthaltenen Tests ausführen");
            this.mnPopupRerun.setBorder(new EmptyBorder(1, 6, 1, 6));
            gbc.insets = new Insets(0, 0, -7, 0);
            gbc.gridy = 1;
            this.add(this.mnPopupRerun, gbc);

        }
        else if (tl.size() > 1 && tl.stream().anyMatch(tci -> tci.getNumberOfAllTestConfigurations() > 0))
        {
            this.mnPopupRerun.setText("alle enthaltenen Tests ausführen");
            this.mnPopupRerun.setBorder(new EmptyBorder(1, 6, 1, 6));
            gbc.insets = new Insets(0, 0, -7, 0);
            gbc.gridy = 0;
            this.add(this.mnPopupRerun, gbc);

            // Tags existieren nur in JUnit-5: Pruefen ob Junit-5-Testfaelle in der Auswahl dabei sind
            for(TestCaseInfo testCaseInfo : tcilist)
            {
                if(testCaseInfo.getTesttype() == TestCaseInfo.TESTTYPE_JUNIT_5)
                {
                    this.mnPopupRerunAllWithTag.setText("alle enthaltenen Tests mit Tag ausführen ");
                    this.mnPopupRerunAllWithTag.setBorder(new EmptyBorder(1, 6, 1, 6));
                    gbc.insets = new Insets(7, 0, -7, 0);
                    gbc.gridy = 2;
                    this.add(this.mnPopupRerunAllWithTag, gbc);
                    tcilistHasJunit5TestCase = true;
                    break;
                }
            }
        }
        // Zu den ausgewaehlen Testfaellen gibt es noch keine TestResultInfo-Objekte, sie muessen erst 
        // einzeld ausgefuehrt werden
        else{  
            this.addPopupMenuListener(new PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                    SwingUtilities.invokeLater(() -> ((JPopupMenu) e.getSource()).setVisible(false));
                }

                @Override
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

                }

                @Override
                public void popupMenuCanceled(PopupMenuEvent e) {

                }
            });
            return;
        }

        this.mnPopupOpen.addActionListener((aEvent) -> 
        {
           	TestCaseInfo tci = tcilist.get(0);
            parent.openTestCaseInDetail(tci);                
        });

        this.mnPopupRerun.addActionListener((aEvent) -> 
        {
            parent.rerunAllTestsOfSeveralTestCases(tcilist);   
        });

        if(tcilistHasJunit5TestCase){
            addRerunAllTaggedTestsMouseListener(parent);
        }

    }

    private void addRerunAllTaggedTestsMouseListener(BasicFrameView parent) {
        mnPopupRerunAllWithTag.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {}
 
            @Override
            public void mousePressed(MouseEvent mouseEvent) 
            {}

            @Override
            public void mouseReleased(MouseEvent mouseEvent) 
            {
                handleRerunAllTaggedTestPopup(parent);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) 
            {}

            @Override
            public void mouseExited(MouseEvent mouseEvent) 
            {}
        });
    }

    private void handleRerunAllTaggedTestPopup(BasicFrameView parent) 
    {
        ArrayList<String> tagsList = new ArrayList<>();
       
        for(TestCaseInfo testCaseInfo : tcilist)
        {
            for(String tag : testCaseInfo.getTags())
            {
                if(!tagsList.contains(tag))
                    tagsList.add(tag);
            }
        }
        Collections.sort(tagsList);
        FilterComboBox fcb = new FilterComboBox(tagsList);
        String[] options = { "Start", "Abbrechen" };
        int selection = JOptionPane.showOptionDialog(null, fcb,
            "Alle enthaltenen Tests mit Tag ausführen",
            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
            options, options[0]);
        if(selection == JOptionPane.CLOSED_OPTION || options[selection].equals("Abbrechen")){
            return;
        }
        String selectedTag = (String) fcb.getSelectedItem();
        if(selectedTag == null || selectedTag.equals("")){
        	new InfoDialog("Information", "Die Eingabe darf nicht leer sein. "
                + "Bitte überprüfen Sie die eingegebenen Daten!");
            handleRerunAllTaggedTestPopup(parent);
            return;
        }
        if(selectedTag.length() > 50){
           	new InfoDialog("Information", "Die Anzahl an Zeichen ist zu groß. "
                + "Bitte überprüfen Sie die eingegebenen Daten!");
            handleRerunAllTaggedTestPopup(parent);
            return;
        }
        if(!tagsList.contains(selectedTag)){
           	new InfoDialog("Information", "Das Tag existiert nicht. "
                + "Bitte überprüfen Sie die eingegebenen Daten!");
            handleRerunAllTaggedTestPopup(parent);
            return;
        }
        parent.rerunAllTaggedTestsOfSeveralTestCases(tcilist, selectedTag);
    }

    /**
     * zeichnet das Popup-Menue
     */
    @Override
    protected void paintComponent(Graphics g)
    {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 0));
        g2.fillRect(0, 0, getWidth(), getHeight());


        g2.setColor(SpecialColor.WHITE);
        g2.fillRoundRect(1, 1, getWidth()-1, getHeight() - 1, 5, 5);

        paintBorder(g);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        paintBorder(g);
    }

}