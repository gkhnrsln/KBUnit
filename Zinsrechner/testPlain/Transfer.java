import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import prlab.kbunit.enums.Variables;

/**
 * TODO:
 * Transferiere JUnit Test in KBUnit Test.
 * 
 * @author G&ouml;khan Arslan
 */
public class Transfer {
	
	/**
	 * Gibt eine Lister der Testmethoden einer Klasse zurueck.
	 * @param file
	 * @return Liste der Testmethoden
	 */
	static List<String> getTestMethode (String file) {
		List<String> liste = new ArrayList<>();

		try {
			Class<?> c = Class.forName(file);
			for (Method method : c.getDeclaredMethods()) {
				Annotation[] anon = method.getAnnotations();

				for (Annotation s : anon) {
					String strAnno = "@" + s.annotationType().getSimpleName();
					if(strAnno.equals(Variables.ANNOTATION_TEST5) 
							|| strAnno.equals(Variables.ANNOTATION_TEST5_REPEATED)
							|| strAnno.equals(Variables.ANNOTATION_TEST5_PARAMETERIZED)
							|| strAnno.equals(Variables.ANNOTATION_TEST5_FACTORY)
							|| strAnno.equals(Variables.ANNOTATION_TEST5_TEMPLATE)
							) {
							Class<?> returnType = method.getReturnType();
							liste.add(returnType + " " + method.getName());
						
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
	}
	/**
	 * Gibt den Namen der Klasse zur&uuml;ck.
	 * @param file Klasse
	 * @return Klassenname
	 * @throws ClassNotFoundException 
	 */
	static String getKlasseName (String file) throws ClassNotFoundException {
		Class<?> c = Class.forName(file);
		return c.getSimpleName();
	}
	
	static void magic (File file) throws ClassNotFoundException {
		String strKlasseName = file.getName();
		String strPackage = file.getParent();
		String strPath = strPackage.replace("testPlain\\", "") + "." + strKlasseName.replace(Variables.EXTENSION_JAVA, "");

		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String zeile;
			
			//Klassename
			do {
				zeile = in.readLine();
				
				if (zeile.contains(getKlasseName(strPath))) {
					zeile = zeile.replace("Plain", "");
					System.out.println(zeile);
					break;
				}
				System.out.println(zeile);
			} while(zeile != null);
			
			while (zeile != null){
				zeile = in.readLine();
				/* TODO: parameter
				 * - werden in der Maske definiert
				 * Format:
				 *  pubilc static "TYP" testMethodenName_ParameterName = "WERT";
				 *  pubilc static "TYP" testMethodenName_exp_ParameterName = "WERT";
				 */
				//...
				//methode, welche Werte sollen parametrisiert werden
				//...
				
				if (zeile.contains(getTestMethode(strPath).get(0))) {
					System.out.println("ZEILE GEFUNDEN");
					
					//naechste Zeile pruefen
					
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
			}

			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		for (String methode : getTestMethode("darlehen.TilgungsdarlehenTestPlain"))
			System.err.println(methode);
		
		
		//File file = new File("testPlain/darlehen/TilgungsdarlehenTestPlain.java");
		//magic(file);
		//getKlasseName("darlehen.TilgungsdarlehenTestPlain");

	}

}
