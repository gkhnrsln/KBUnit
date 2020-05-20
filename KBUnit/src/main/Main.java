package main;

import hilfe.guiHilfe.SpecialColor;
import main.hauptfenster.BasicFrameControl;
import org.jdom2.JDOMException;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
  

/**
 * Die Klasse Main enthaelt die main-Methode fuer den Start der Anwendung KBUnit.
 * Es wird dort das Hauptfenster der Anwendung geoeffnet. In der Anwendung
 * werden JUnit-Tests gekapselt und ueber diese grafische Benutzeroberflaeche
 * via Reflection zur Bearbeitung zur verfuegung gestellt.
 * <br>
 * &copy; 2017 Philipp Sprengholz, Ursula Oesing  <br>
 * @author Philipp Sprengholz, Ursula Oesing
 */
 public class Main{
     
    public static void main(String... args)
	{
	    // das Look&Feel wird auf das aktuelle und systemunabhängige 
	    // Nimbus-Design gesetzt und dessen Farbschema angepasst
	    try
	    {
	        UIManager.put("control", SpecialColor.WHITE);
	        UIManager.put("background", SpecialColor.WHITE);
	        UIManager.put("nimbusLightBackground", SpecialColor.WHITE);
	        UIManager.put("textBackground", SpecialColor.WHITEGRAY);
	        UIManager.put("nimbusSelectedText", SpecialColor.BLACK);
	        UIManager.put("nimbusOrange", SpecialColor.DARKGRAY);
	        UIManager.put("nimbusBase", new Color(140,140,140));
	        UIManager.put("nimbusFocus", SpecialColor.WHITEGRAY);
	        UIManager.put("info", SpecialColor.WHITESTGRAY);
	        UIManager.put("text", SpecialColor.BLACK);
	        UIManager.put("PopupMenu[Enabled].backgroundPainter", null);
	        UIManager.put("nimbusSelectionBackground",SpecialColor.WHITEGRAY);
	        UIManager.put("JTree.lineStyle", "Angled");
	        UIManager.put("Tree.drawHorizontalLines",true);
	        UIManager.put("Tree.drawVerticalLines",true);
	        UIManager.put("Nimbus.Overrides.InheritDefaults",false);
            // Suchen nach der Look & Feel Klasse, die am besten passt
	        /*
	        for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
	        {
	            if("Nimbus".equals(info.getName())) 
	            {
	            	System.out.println(info.getClassName());
	            }
	        }
	        */
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
  
	        // die Hauptanwendung wird gestartet
	        new BasicFrameControl();
	    }
	    catch (ClassNotFoundException | InstantiationException | IllegalAccessException 
		        | UnsupportedLookAndFeelException ex)
		{
		    JOptionPane.showMessageDialog(null,"Look&Feel nicht gefunden!\n" 
		        + "KBUnit ist auf das Nimbus-Design ausgelegt und kann aktuell " 
		      	+ "nicht dargestellt werden.");
		}
	    catch (IOException ioe)
	    {
	        JOptionPane.showMessageDialog(null, 
	        	"Die Konfigurationsdatei konnte nicht gefunden werden.");
	    }
	    catch (JDOMException jdome)
	    {
	    	JOptionPane.showMessageDialog(null, 
	    		"Die Konfigurationsdatei ist fehlerhaft.");
	    }
	}
}