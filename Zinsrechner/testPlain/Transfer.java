import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;

import prlab.kbunit.enums.Variables;

/**
 * TODO:
 * Transferiere JUnit Test in KBUnit Test.
 * 
 * @author G&ouml;khan Arslan
 */
public class Transfer {
	
	/**
	 * Gibt Methode einer Klasse zurueck.
	 * @param file
	 */
	static String getMethode (String file) {
		String s = "";
		try {
			Class<?> c = Class.forName(file);
			for (Method method : c.getDeclaredMethods()) {
				String name = method.getName();
				Class<?> returnType = method.getReturnType();
				s = returnType + " " + name;
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	/**
	 * 
	 * @param file
	 * @return
	 */
	static String getKlasse (String file) {
		String s = "";
		try {
			Class<?> c = Class.forName(file);
			s = c.getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return s;
	
	}
	
	static void magic (File file) {
		String strKlasseName = file.getName();
		String strPackage = file.getParent();
		String strPath = strPackage.replace("testPlain\\", "") + "." + strKlasseName.replace(Variables.EXTENSION_JAVA, "");

		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String zeile;
			do {
				zeile = in.readLine();
				//Klassename
				if (zeile.contains(getKlasse(strPath))) {
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
				
				if (zeile.contains(getMethode(strPath))) {
					System.out.println("ZEILE GEFUNDEN");
				}
				/*
				while(true) {
					String val = JOptionPane.showInputDialog("Geben Sie das zu parametrisierte Wert ein");
					if (zeile.contains(val)) {
						
					}
					//System.exit(0);
					
					if(true) {
						
						break;
					}
				}*/
				
				System.out.println(zeile);
			} while(zeile != null);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		//getMethode("darlehen.TilgungsdarlehenTestPlain");
		File file = new File("testPlain/darlehen/TilgungsdarlehenTestPlain.java");
		magic(file);
		//getKlasse("darlehen.TilgungsdarlehenTestPlain");

	}

}
