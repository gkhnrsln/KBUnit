package prlab.kbunit.business.transfer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import prlab.kbunit.enums.Variables;

public class SANDKASTEN {
	static void saveKlasse(String path) {

		BufferedReader in;
		BufferedReader csv;
		
		BufferedWriter out;
		String zeile;
		String txtLine;
		
		try {
			in = new BufferedReader(new FileReader(Variables.TEST_PLAIN_SOURCE + path + Variables.EXTENSION_TEST_PLAIN_JAVA));
			csv = new BufferedReader(new FileReader("testKBUnit/prlab/kbunit/business/transfer/Parameter.txt"));
			//Todo: spaeter in Variables.TEST_SOURCE abspeichern
			out = new BufferedWriter(new FileWriter("transferierteKlassen/" + path.substring(path.lastIndexOf("/")) + Variables.EXTENSION_TEST_JAVA));
			//out.write("package\n");
			while (true) {
				//lies Zeile
				zeile = in.readLine();
				//Dateiende erreicht, abbrechen
				if (zeile == null) break;
				//ueberspringe package-Anweisung 
				if (zeile.startsWith("package")) continue;
				
				//falls Klassenname: zeile drunter attribute hinzufuegen
				if (zeile.contains("class")) {
					//Klassenname anpassen
					out.write(zeile.replace("Plain", "") + "\n");
					while (true) {
						//inhalt der .txt Datei lesen
						txtLine = csv.readLine();
						if (txtLine == null) {
							break;
						}
						//kopiere Inhalt von txt Datei
						out.write("\t" + txtLine + "\n");
					}
					csv.close();
					continue;
				}
				out.write(zeile + "\n");
			}
			in.close();
			out.close();


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		saveKlasse("/darlehen/Beispiel");
	}

}
