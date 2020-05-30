

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import prlab.kbunit.enums.Variables;
import prlab.kbunit.scan.FolderScanner;
/**
 * TODO:
 * Transferiere JUnit Test in KBUnit Test.
 * 
 * @author G&ouml;khan Arslan
 */
public class Transfer {
	
	/**
	 * Gibt Methode einer Klasse zurueck.
	 * @param klasse
	 */
	static void getMethode (String klasse) {
		try {
			Class<?> c = Class.forName(klasse);
			for (Method method : c.getDeclaredMethods()) {
				String name = method.getName();
				Class<?> returnType = method.getReturnType();
				System.out.println(returnType + " " + name);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param klasse
	 * @return
	 */
	static String getKlasse (String klasse) {
		String s = "";
		try {
			Class<?> c = Class.forName(klasse);
			//System.out.println(c.getCanonicalName());
			//System.out.println(c.getName());
			s = c.getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	
	}
	
	static void magic (String file) {
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String zeile;
			do {
				zeile = in.readLine();
				//Klassename
				if (zeile.contains(getKlasse("darlehen.TilgungsdarlehenTestPlain"))) {
					zeile = zeile.replace("Plain", "");
				}
				/* TODO: parameter
				 * - werden in der Maske definiert
				 * Format:
				 *  pubilc static "TYP" testMethodenName_ParameterName = "WERT";
				 *  pubilc static "TYP" testMethodenName_exp_ParameterName = "WERT";
				 */
				//...
				//methode, welche Werte sollen parametrisiert werden
				//...
				while(true) {
					String val = JOptionPane.showInputDialog("Geben Sie das zu parametrisierte Wert ein");
					if (zeile.contains(val)) {
						
					}
					System.exit(0);
					
					if(true) {
						
						break;
					}
				}
				
				System.out.println(zeile);
			} while(zeile != null);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		//getMethode("darlehen.TilgungsdarlehenTestPlain");
		File file = new File("C:/Users/Herfel/eclipse-workspace/Zinsrechner/testPlain/darlehen/TilgungsdarlehenTestPlain.java");
		magic(file.toString());
		//getKlasse("darlehen.TilgungsdarlehenTestPlain");
	}

}
