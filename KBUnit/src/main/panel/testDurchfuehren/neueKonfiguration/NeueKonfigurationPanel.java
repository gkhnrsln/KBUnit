package main.panel.testDurchfuehren.neueKonfiguration;

import hilfe.guiHilfe.ImageTextField;
import hilfe.guiHilfe.SpecialColor;
import main.dialogfenster.InfoDialog;
import main.hauptfenster.BasicFrameView;
import main.hauptfenster.testCaseInfo.TestCaseInfo;
import main.panel.ParameterPanel;
import main.panel.testDurchfuehren.ParameterVerifier;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

/** 
 * Klasse, welche das Panel fuer die Anzeige eines Testfalls und die Eingabe 
 * einer neuen Konfiguration zur Verfuegung stellt.
 *
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz
 */
public class NeueKonfigurationPanel extends JPanel
{
    
  	private static final long serialVersionUID = 1L;
  	// GUI-Elemente fuer das Panel
	private JPanel pnlTitle;
    private JLabel lblTitle;
    private JScrollPane scpParameter;
    private ParameterPanel pnlParameter;
    private JPanel pnlButtons;
    private JButton btnRunTest;
    
    // enthaelt das View zum Hauptfenster, welches das Panel enthaelt
    private BasicFrameView parent;
    
    /**
     * erzeugt ein NeueKonfigurationPanel - Objekt fuer das View zum Hauptfenster
     * @param parent BasicFrameView des Hauptfensters
     */
    public NeueKonfigurationPanel(BasicFrameView parent)
    {
        this.parent = parent;
        this.setBackground(SpecialColor.WHITE);
        this.setBorder(null);
        this.setLayout(new GridBagLayout());
        this.initComponents();
        this.initListener();
    }   
    
    // erstellt die Komponenten des Konfigurationspanels
    private void initComponents()
    {
        GridBagConstraints gbc = new GridBagConstraints();

        this.pnlTitle = new JPanel();
        this.pnlTitle.setBackground(SpecialColor.WHITE);
        this.pnlTitle.setBorder(new MatteBorder(0, 0, 1, 0, SpecialColor.LIGHTGRAY));
        this.pnlTitle.setLayout(new GridBagLayout());
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.add(this.pnlTitle,gbc);
   
        this.lblTitle = new JLabel();
        this.lblTitle.setText(
            "<html><font color=\"#777777\">Sobald die einzelnen Testparameter mit Werten belegt sind, "
            + "kann eine neue Konfiguration angelegt und getestet werden. "
            + "Für Eingangsparameter können mehrere durch Semikolon getrennte Werte angegeben werden, "
            + "wodurch automatisch mehrere Konfigurationen generiert werden.</font></html>");
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 6, 10, 6);
        gbc.weighty = 1.0;
        gbc.ipady = 0;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 0;
        this.pnlTitle.add(this.lblTitle,gbc);

        // es wird ein Panel angelegt, welches Platz fuer die Testparameter bietet
        this.pnlParameter = new ParameterPanel();
        this.pnlParameter.setBackground(SpecialColor.WHITE);
        this.pnlParameter.setLayout(new GridBagLayout());

        // das Panel wird einem ScrollPane zugeordnet, damit spaeter 
        // (falls der Platz fuer die Parametereingabe nicht ausreicht) 
        // Scrollbalken angezeigt werden
        this.scpParameter = new JScrollPane(this.pnlParameter);
        this.scpParameter.setBorder(null);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 1.0;
        gbc.ipady = 5;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        this.add(this.scpParameter,gbc);

        // es wird ein Panel angelegt, welches allgemeine Informationen zur Parametervergabe
        this.pnlButtons = new JPanel();
        this.pnlButtons.setLayout(null);
        this.pnlButtons.setBorder(new MatteBorder(1, 0, 0, 0, SpecialColor.LIGHTGRAY));
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0;
        gbc.ipady = 50;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        this.add(this.pnlButtons, gbc);

        // Button zum Starten des Tests
        this.btnRunTest = new JButton("Konfiguration(en) testen");
        this.btnRunTest.setBounds(5,11,200,28);
        this.btnRunTest.setVisible(true);
      
        this.pnlButtons.add(this.btnRunTest);
    }   
    
    // erstellt die Listener des Konfigurationspanels
    private void initListener()
    {
        this.btnRunTest.addActionListener((aEvent) ->
        {
            boolean abortAction = false;

            // es wird ueberprueft, ob all Parametereingaben korrekt sind, 
            // erst dann wird der Test ausgefuehrt  
            int numberOfComponents = pnlParameter.getInputComponentList().size();
            for (int i=0; i<numberOfComponents; i++)
            {
                if (pnlParameter.getInputComponentList().get(i).getClass() 
                 	== ImageTextField.class)
                {
                    ImageTextField tf 
                        = (ImageTextField) pnlParameter.getInputComponentList().get(i);
                    ParameterVerifier pcv = (ParameterVerifier) tf.getInputVerifier();
                    if (!pcv.getLastVerificationResult())
                    {
                        abortAction = true;
                        break;
                    }
                }
            }
            if (abortAction)
            {
                new InfoDialog("Achtung",
                    "Die Parametereingaben sind nicht korrekt. "
                    + "Bitte überprüfen Sie Ihre Angaben in den rot markierten Feldern.");
            }
            else
            {
                parent.runUserDefinedTests(pnlParameter);
            }
        });
    }   
    
    /**
     * gibt das Panel zu einer neuen Testkonfiguration aus.
     * @return ParameterPanel , das Panel fuer eine neue Testkonfiguration
     */
    public ParameterPanel getPnlParameter()
    {
        return this.pnlParameter;
    }    
    
    /**
     * erstellt die fuer den vorgegebenen Testfall notwendigen Eingabefelder auf
     * einem speziellen ParameterPanel, in welchem diese Eingabefelder zur
     * spaeteren Loeschung gelistet sind (fuer Konfigurationsgenerator, also MIT
     * Ergebnisparameter / Feld fuer erwartete Exceptions)
     *
     * @param tci Testfall, fuer dessen Parameter Eingabefelder erzeugt werden
     */
    public void buildUserInterface(TestCaseInfo tci)
    {
        try
        {  
            ArrayList<Field> parameters = tci.getParameters();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.ipady = 5;
            gbc.weighty = 0;
            gbc.weightx = 0;
            gbc.insets = new Insets(10, 5, 0, 0);
            gbc.ipadx = 10;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.gridy = 0;
            JCheckBox chb = new JCheckBox(
            	"<html><font color=\"#000000\"> Exception erwartet</html>");
            chb.setSelected(false);
            chb.addActionListener((aEvent) ->
            {
                for (int i=0; i< pnlParameter.getInputComponentList().size(); i++)
                {
                    String componentName = pnlParameter.getInputComponentList().get(i).getName();
                    int index = componentName.indexOf("_");
                    if(componentName.substring(index+1, index+5).equals("exp_"))
                    {
                        boolean enabled;
                        if (pnlParameter.getInputComponentList().get(i).isEnabled())
                        {
                            enabled = false;
                        }
                        else
                        {
                            enabled = true;
                        }
                        pnlParameter.getInputComponentList().get(i).setEnabled(enabled);
                        for (int j=0; j<pnlParameter.getLabelList().size(); j++)
                        {
                            if(pnlParameter.getLabelList().get(j).getName().equals(componentName))
                            {
                                pnlParameter.getLabelList().get(j).setEnabled(enabled);
                            }
                        }
                    }
                }
            });

            chb.setBorderPainted(true);
            chb.setVisible(true);
            this.pnlParameter.add(chb, gbc);
            this.pnlParameter.getInputComponentList().add(chb);
            this.pnlParameter.getInputComponentList().get(0).setName("exceptionExpected");
            // gibt die Anzahl der Testparameter abzgl. 1 an
            int paramCounter = 0;
            // Hilfsobjekt (muss im Folgenden nicht gesetzt werden, da alle 
            // Testparameter static - Klassenvariablen - sind)
            Object cl = null;

            // die in der parameters-Liste enthaltenen Parameter werden der Reihe 
            // nach dem entsprechenden Container hinzugefuegt
            for(int i = 0; i < parameters.size(); i++)
            {
                // speichert den kompletten Parameternamen
                String longParameterName = parameters.get(i).getName();
                // feststellung, ob es sich um keinen Erwartungsparameter handelt 
                // (bei diesem sollen spaeter nicht mehrere Werte durch Semikolon getrennt 
                // eingegeben werden koennen)
                boolean isNoExpectedResultParameter = true;
                int index = longParameterName.indexOf("_");
                if(longParameterName.substring(index+1).length() >= 4
                	&& longParameterName.substring(index+1, index+5).equals("exp_"))
                {
                    isNoExpectedResultParameter = false;
                }
                // Parameterzaehler wird erhoeht
                paramCounter++;
                // aus dem kompletten Parameternamen wird ein nutzerfreundlicher 
                // Name abgeleitet, 
                // indem der Name des Testfalls aus dem Parameternamen entfernt wird
                String parameterName = longParameterName
                	.substring(longParameterName.lastIndexOf("_")+1);

                // der gekuerzte Parametername wird mittels eines Labels angezeigt und eine 
                // Parameterbeschreibung als Tooltip hinterlegt
                JLabel lbl = new JLabel();
                lbl.setVerticalAlignment(SwingConstants.TOP);
                if (isNoExpectedResultParameter)
                {
                    lbl.setText("<html>" + parameterName 
                        + "<sup><font color=\"#AAAAAA\">&thinsp;E</sup></font><html>");
                }
                else
                {
                    lbl.setText("<html>" + parameterName
                        + "<sup><font color=\"#AAAAAA\">&thinsp;R</sup></font><html>");
                }
                String tooltip;
                tooltip = parent.getBasicFrameControl().getBasicFrameXmlModel()
                    .getParameterInfo(tci.getPath(), longParameterName);
                lbl.setName(longParameterName);
                lbl.setToolTipText(tooltip);
                lbl.setVisible(true);
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 6;
                gbc.weighty = 0;
                gbc.weightx = 0;
                gbc.insets = new Insets(15, 6, 0, 11);
                gbc.ipadx = 20;
                gbc.gridx = 0;
                gbc.gridwidth = 1;
                gbc.gridheight = 1;
                gbc.gridy = paramCounter;
                pnlParameter.add(lbl,gbc);
                // das Label wird in einer Liste gespeichert, sodass es spaeter 
                // wieder entfernt werden kann
                pnlParameter.getLabelList().add(lbl);
                // die GridBagConstraints werden fuer die eigentliche 
                // Eingabekomponente angepasst
                gbc.weighty = 0;
                gbc.weightx = 1.0;
                gbc.ipadx = 200;
                gbc.gridx = 1;
                gbc.gridy = paramCounter;

                parameters.get(i).setAccessible(true);

                // falls der Parameter vom Typ INTEGER ist
                if (parameters.get(i).getType() == int.class)
                {
                    ImageTextField tf = new ImageTextField(
                    	"" + parameters.get(i).getInt(cl));
                    tf.setInputVerifier(new ParameterVerifier(Integer.class, 
                        isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf, gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ BYTE ist
                else if(parameters.get(i).getType() == byte.class)
                {
                    ImageTextField tf = new ImageTextField("" + parameters.get(i).getByte(cl));
                    tf.setInputVerifier(new ParameterVerifier(Byte.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ SHORT ist
                else if(parameters.get(i).getType() == short.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getShort(cl));
                    tf.setInputVerifier(new ParameterVerifier(Short.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ LONG ist
                else if(parameters.get(i).getType() == long.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getLong(cl));
                    tf.setInputVerifier(new ParameterVerifier(Long.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ FLOAT ist
                else if(parameters.get(i).getType() == float.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getFloat(cl));
                    tf.setInputVerifier(new ParameterVerifier(Float.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ DOUBLE ist
                else if(parameters.get(i).getType() == double.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getDouble(cl));
                    tf.setInputVerifier(new ParameterVerifier(Double.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ BOOLEAN ist
                else if(parameters.get(i).getType() == boolean.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getBoolean(cl));
                    tf.setInputVerifier(new ParameterVerifier(boolean.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter vom Typ CHAR ist
                else if(parameters.get(i).getType() == char.class)
                {
                    ImageTextField tf = new ImageTextField("" 
                        + parameters.get(i).getChar(cl));
                    tf.setInputVerifier(new ParameterVerifier(char.class, 
                    	isNoExpectedResultParameter));
                    tf.getInputVerifier().verify(tf);
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf,gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                    .setName(longParameterName);
                }
                // falls der Parameter vom Typ STRING ist
                else if(parameters.get(i).getType() == java.lang.String.class)
                {
                	ImageTextField tf;
                	if(parameters.get(i).get(cl) == null)
                	{
                		tf = new ImageTextField("null");
                	}
                	else if(parameters.get(i).get(cl).toString().length() == 0)
                	{	
                        tf = new ImageTextField("\"\"" + parameters.get(i)
                            .get(cl).toString());
                	}
                	else
                	{	
                        tf = new ImageTextField("" 
                	        + parameters.get(i).get(cl).toString());
                	}
             	
                	tf.setInputVerifier(new ParameterVerifier(java.lang.String.class, 
                        isNoExpectedResultParameter));
 	                tf.getInputVerifier().verify(tf); 
                    tf.setToolTipText(tooltip);
                    tf.setVisible(true);
                    this.pnlParameter.add(tf, gbc);
                    this.pnlParameter.getInputComponentList().add(tf);
                    this.pnlParameter.getInputComponentList().get(paramCounter)
                        .setName(longParameterName);
                }
                // falls der Parameter von keinem der unterstuetzten Datentypen ist, wird eine 
                // entsprechende Meldung ausgegeben und die Eingabe verweigert
                else
                {
                    JLabel lb = new JLabel("Eingabe nicht unterstuetzt");
                    lb.setToolTipText(tooltip);
                    lb.setVisible(true);
                    this.pnlParameter.add(lb,gbc);
                    this.pnlParameter.getLabelList().add(lb);
                }
            }
            if (paramCounter == 0)
            {
                JLabel lbl = new JLabel(
                    "<html>Der Testfall kann nicht durch Parameter beeinflusst werden. "
                    + "Sie können den Test nur starten.<html>");
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.ipady = 5;
                gbc.weighty = 0;
                gbc.weightx = 1.0;
                gbc.insets = new Insets(15, 7, 0, 11);
                gbc.ipadx = 100;
                gbc.gridx = 0;
                gbc.gridwidth = 2;
                gbc.gridheight = 1;
                gbc.gridy = 1;
                this.pnlParameter.add(lbl,gbc);
                lbl.setVisible(true);
                this.pnlParameter.getLabelList().add(lbl);
                paramCounter++;
            }
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weighty = 1;
            gbc.weightx = 1;
            gbc.insets = new Insets(0, 0, 0, 0);
            gbc.ipadx = 0;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.gridheight = 1;
            gbc.gridy = paramCounter + 1;
            JPanel placeholder = new JPanel();
            placeholder.setBackground(SpecialColor.WHITE);
            placeholder.setVisible(true);
            this.pnlParameter.add(placeholder,gbc);
            this.pnlParameter.validate();
            this.pnlParameter.repaint();
            this.pnlParameter.setPreferredSize(new Dimension(400, 40 * (paramCounter+1)));
        }
        catch (IllegalAccessException iae)
        {
            new InfoDialog("Fehler beim Laden der Testparameter",
                "Die Parameter des ausgewählten Testfalles konnten nicht "
                + "ordnungsgemäß geladen werden. Unter Umständen ist das " 
                + "zu testende Projekt fehlerhaft.");
        }
    }
   
}
