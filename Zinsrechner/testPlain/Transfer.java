import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import prlab.kbunit.enums.Variables;

/**
 * TODO:
 * Transferiere JUnit Test in KBUnit f&auml;higen Test.
 * 
 * @author G&ouml;khan Arslan
 */
public class Transfer {
	/**
	 * Gibt den Namen der Klasse zur&uuml;ck.
	 * @param file Klasse
	 * @return Klassenname
	 * @throws ClassNotFoundException 
	 */
	static String getTestKlasseName (String file) throws ClassNotFoundException {
		Class<?> c = Class.forName(file);
		return c.getSimpleName();
	}
	
	/**
	 * Gibt eine Liste der Testattribute einer Klasse zurueck.
	 * @param file
	 * @return Liste der Testattribute
	 */
	static List<String> getTestAttribut (String file) {
		List<String> liste = new ArrayList<>();
		
		try {
			Class<?> c = Class.forName(file);
			for (Field field : c.getDeclaredFields()) {
				if(field.getName().startsWith("test"))
					liste.add(field.getName());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
	}
	
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
					switch ("@" + s.annotationType().getSimpleName()) {
						case Variables.ANNOTATION_TEST5:
						case Variables.ANNOTATION_TEST5_REPEATED:
						case Variables.ANNOTATION_TEST5_PARAMETERIZED:
						case Variables.ANNOTATION_TEST5_FACTORY:
						case Variables.ANNOTATION_TEST5_TEMPLATE:
							Class<?> returnType = method.getReturnType();
							liste.add(returnType + " " + method.getName());
							break;
						default:
							break;
					}
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
	}

	/**
	 * TEST
	 * @param file
	 * @throws ClassNotFoundException
	 */
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
				
				if (zeile.contains(getTestKlasseName(strPath))) {
					zeile = zeile.replace("Plain", "");
					System.err.println(zeile);
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
				for (String methode : getTestMethode(strPath)) {
					if (zeile.contains(methode)) {
						System.err.println("TESTMETHODE GEFUNDEN: [" +methode  +"]");
						//naechste Zeile pruefen
						
					}
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
		//for (String methode : getTestMethode("darlehen.TilgungsdarlehenTestPlain")) System.err.println(methode);
		

		File file = new File("testPlain/darlehen/TilgungsdarlehenTestPlain.java");
		try {
			magic(file);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//getKlasseName("darlehen.TilgungsdarlehenTestPlain");

	}

}
