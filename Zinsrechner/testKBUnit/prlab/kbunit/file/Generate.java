package prlab.kbunit.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.gui.windowParametrisierung.ParametrisierungController;
/**
 * Generiert eine neue KBUnit-f6auml;hige JUnit Testklasse.
 * @author G&ouml;khan Arslan
 */
public class Generate {
	static List<String> listeTestAttribute = new ArrayList<>();
	
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
	@Deprecated
	static List<String> getTestAttribut (String file) {
		List<String> liste = new ArrayList<>();
		
		try {
			Class<?> clazz = Class.forName(file);
			for (Field field : clazz.getDeclaredFields()) {
				if(field.getName().startsWith("test")) {
					// get value of the fields 
					/*
					try {
						System.err.println("\tATTR: " + field.getName() + " \tWERT: " + field.get(clazz));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					*/
					liste.add(field.getName());
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return liste;
	}
	
	/**
	 * @param path
	 */
	public static void insertAttributes(String path) {
		BufferedReader quelle, txt;
		BufferedWriter ausgabe;
		String zeile, txtLine;
		List<String> temp = new ArrayList<>();
		String strPath = path.replace("\\","").replace("/", ".").replace(".java", "");
		try {
			//JUnit Testklasse
			quelle = new BufferedReader(new FileReader(Variables.TEST_PLAIN_SOURCE + path));
			//zu hinzufuegende Testattribute
			txt = new BufferedReader(new FileReader("testKBUnit/prlab/kbunit/business/transfer/Parameter.txt"));
			//Generierte KBUnit-faehige JUnit Testklasse
			File newFile = new File("transferierteKlassen/" + path.replace("Plain", ""));
			ausgabe = new BufferedWriter(new FileWriter(FileCreator.createMissingPackages(newFile)));
			
			while (true) {
				//lies Zeile
				zeile = quelle.readLine();
				//falls Klassenname: zeile drunter attribute hinzufuegen
				if (zeile.contains("class")) {
					//Klassenname anpassen
					ausgabe.write(zeile.replace("Plain", "") + "\n");
					while (true) {
						//inhalt der .txt Datei lesen
						txtLine = txt.readLine();
						//brich ab, wenn Dateiende
						if (txtLine == null) break;
						//kopiere Inhalt von txt Datei
						ausgabe.write("\t" + txtLine + "\n");
						//falls aktuelle Zeile eine testAttribut Deklaration ist
						if (txtLine.contains("public static") && txtLine.contains("test") && txtLine.contains("=") ) {
							listeTestAttribute.add(txtLine);
						}
					}
					txt.close();
					break;
				}
				ausgabe.write(zeile + "\n");
			}
			
			//ab letzte Testattribut Zeile
			while (true) {
				zeile = quelle.readLine();
				if (zeile == null) break; //Dateiende
				
				for (String methodeName : getTestMethode(strPath, false)) {
					//method gefunden
					if(zeile.contains(methodeName + "(")) {
						temp.clear(); //leere Liste fuer neue Inhalte
						//pruefe, ob attribute zur methode passt
						for (String attr : listeTestAttribute) {
							//String strAttrNameFull = attr.substring(attr.indexOf("test"), attr.indexOf("=") - 1);
							String strAttrName = attr.substring(attr.indexOf("test"), attr.indexOf("_"));
							if(methodeName.equals(strAttrName)) {
								temp.add(attr);
							}
						}
						break;
					}
				}
				for (String attr : temp) {
					String strAttrNameFull = attr.substring(attr.indexOf("test"), attr.indexOf("=") - 1);
					String strAttrVal = attr.substring(attr.indexOf("=")+2, attr.indexOf(";"));
					//wenn wert identisch mit testattributwert
					/*
					 * TODO: Problem
					 * 
					 * Wenn Wert des Testattributes (als Zeichenkette) in einer
					 * Testmethode in einem anderen Wert eines Attributes vorkommt,
					 * wird diese faelschlicherweise ersetzt.
					 * 
					 * Beispiel:
					 * 
					 * public static int testMethode1_1 = 10;
					 * public static int testMethode1_2 = 11100000;
					 * 
					 *  > TestPlain
					 * ---------------------------------------------
					 * @Test
					 * void testMethode1() {
					 * 		System.out.print(11100000);
					 * }
					 *  > Test
					 * ---------------------------------------------
					 * @Test
					 * void testMethode1() {
					 * 		System.out.print(11testMethode1_10000);
					 * 						   ^^^^^^^^^^^^^^
					 * }
					 * 
					 * contains ist nicht optimal, andere Loesung?
					 */
					if (zeile.contains(strAttrVal)) {
						//showMessage(AlertType.CONFIRMATION, "Bestätigung","Zu ersetzenden Wert gefunden", "Ersetzen?");
						zeile = zeile.replace(strAttrVal, strAttrNameFull);
					}
				}
				ausgabe.write(zeile + "\n");
			}
			quelle.close();
			ausgabe.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Info Fenster
	private static void showMessage(AlertType alertType, String title, 
			String header, String message) {
			Alert alert = new Alert(alertType);
			alert.setTitle(title);
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.showAndWait();
		}
}
