package prlab.kbunit.business.transfer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
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
	static String getTestKlasseName (String file) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(file);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return clazz.getSimpleName();
	}
	
	/**
	 * TODO: Wird nicht benoetigt, weil ja diese erst spaeter dazukommen.
	 * 
	 * Gibt eine Liste der Testattribute einer Klasse zurueck.
	 * @param file
	 * @return Liste der Testattribute
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	static List<String> getTestAttribut (String file) {
		List<String> liste = new ArrayList<>();
		
		try {
			Class<?> clazz = Class.forName(file);
			for (Field field : clazz.getDeclaredFields()) {
				if(field.getName().startsWith("test")) {
					// get value of the fields 
					try {
						System.err.println("\tATTR: " + field.getName() + " \tWERT: " + field.get(clazz));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					liste.add(field.getName());
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
	}
	
	/**
	 * Gibt eine Lister der Testmethoden einer Klasse zurueck.
	 * @param file
	 * @param withReturnType Wenn auch der Rueckgabewert mitangegeben werden soll
	 * @return Liste der Testmethoden
	 */
	public static List<String> getTestMethode (String file, boolean withReturnType) {
		List<String> liste = new ArrayList<>();

		try {
			Class<?> clazz = Class.forName(file);
			//nur oeffentliche Methoden
			for (Method method : clazz.getDeclaredMethods()) {
				for (Annotation s : method.getAnnotations()) {
					switch ("@" + s.annotationType().getSimpleName()) {
						case Variables.ANNOTATION_TEST5:
						case Variables.ANNOTATION_TEST5_REPEATED:
						case Variables.ANNOTATION_TEST5_PARAMETERIZED:
						case Variables.ANNOTATION_TEST5_FACTORY:
						case Variables.ANNOTATION_TEST5_TEMPLATE:
							if (withReturnType) {
								Class<?> returnType = method.getReturnType();
								liste.add(returnType + " " + method.getName());
							} else {
								liste.add(method.getName());
							}
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
		//Dateipfad formatieren
		String strKlasseName = file.getName();
		String strPackage = file.getParent();
		String strPath = strPackage.replace(Variables.TEST_PLAIN_SOURCE + "\\", "") + "." + strKlasseName.replace(Variables.EXTENSION_JAVA, "");
		
		//Datei lesen
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(file));
			String zeile;
			
			//Klassename aendern
			do {
				zeile = in.readLine();
				//Klassenname gefunden
				if (zeile.contains(getTestKlasseName(strPath))) {
					//entferne Plain 
					zeile = zeile.replace("Plain", "");		
					System.out.println(zeile);
					break;
				} else {
					System.out.println(zeile);
				}
			} while(true);
			
			/* 
			 * ab hier to do   
			 */
			
			while (true) {
				zeile = in.readLine();
				//Dateiende erreicht, abbrechen
				if (zeile == null) {
					break;
				}
				System.out.println(zeile);
				
				/* 
				 * TODO: parameter
				 */
				for (String methode : getTestMethode(strPath, true)) {
					if (zeile.contains(methode)) {
						System.out.println("TESTMETHODE GEFUNDEN: [" + methode +"]");
						//naechste Zeile pruefen
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
