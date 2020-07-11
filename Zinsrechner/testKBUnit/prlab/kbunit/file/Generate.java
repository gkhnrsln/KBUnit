package prlab.kbunit.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import prlab.kbunit.enums.Variables;

/**
 * Generiert eine neue KBUnit-f6auml;hige JUnit Testklasse.
 * @author G&ouml;khan Arslan
 */
public class Generate {
	static List<String> listeTestAttribute = new ArrayList<>();
	/**
	 * Gibt eine Liste mit den Testmethoden zurueck.
	 * @param file Dateipfad der Testklasse
	 * @param withReturnType falls der Rueckgabetyp mit angegebn werden soll
	 * @return Liste mit Testmethoden
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
	 * @param path
	 */
	public static void insertAttributes(String path) {
		BufferedReader quelle, txt;
		BufferedWriter ausgabe;
		String zeile, txtLine;
		List<String> temp = new ArrayList<>();
		String strPath = path
				.replace("\\","")
				.replace("/", ".")
				.replace(".java", "");
		try {
			//JUnit Testklasse
			quelle = new BufferedReader(new FileReader(Variables.TEST_PLAIN_SOURCE + path));
			//zu hinzufuegende Testattribute
			txt = new BufferedReader(new FileReader(Variables.PARAMETER_FILE_PATH));
			//Generierte KBUnit-faehige JUnit Testklasse
			File newFile = new File("transferierteKlassen/" + path.replace("Plain", "")); //TODO: ersetze mit untere Zeile
			//File newFile = new File(Variables.TEST_SOURCE + "/" + path.replace("Plain", ""));
			ausgabe = new BufferedWriter(new FileWriter(FileCreator.createMissingPackages(newFile)));
			
			while (true) {
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
						if (txtLine.contains("public static") && txtLine.contains("test") && txtLine.contains("=")) 
							listeTestAttribute.add(txtLine);
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
							String strAttrName = attr.substring(attr.indexOf("test"), attr.indexOf("_"));
							if(methodeName.equals(strAttrName)) temp.add(attr);
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
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Bestätigung");
						alert.setHeaderText(
								"Zu ersetzenden Wert [" + strAttrVal +"] in folgender Zeile gefunden:\n" + zeile.trim()
								+ "\nNachher\n" + zeile.replaceAll(strAttrVal, strAttrNameFull).trim()
								);
						alert.setContentText("Sind Sie damit einverstanden?");
						
						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK){
							//zeile = zeile.replaceAll("\\b" + strAttrVal + "\\b", strAttrNameFull);
							zeile = zeile.replaceAll(strAttrVal, strAttrNameFull);
						}
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
}
