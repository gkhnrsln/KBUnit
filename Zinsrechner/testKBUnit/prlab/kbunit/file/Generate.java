package prlab.kbunit.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import prlab.kbunit.enums.Variables;

public class Generate {
	public static void saveKlasse(String path) {
		BufferedReader in, txt;
		BufferedWriter out;
		String zeile, txtLine;
		
		try {
			in = new BufferedReader(new FileReader(Variables.TEST_PLAIN_SOURCE + path));
			txt = new BufferedReader(new FileReader("testKBUnit/prlab/kbunit/business/transfer/Parameter.txt"));

			File newFile = new File("transferierteKlassen/" + path.replace("Plain", ""));
			out = new BufferedWriter(new FileWriter(FileCreator.createMissingPackages(newFile)));
			
			//out.write("package\n");
			while (true) {
				//lies Zeile
				zeile = in.readLine();
				//Dateiende erreicht, abbrechen
				if (zeile == null) break;
				//falls Klassenname: zeile drunter attribute hinzufuegen
				if (zeile.contains("class")) {
					//Klassenname anpassen
					out.write(zeile.replace("Plain", "") + "\n");
					while (true) {
						//inhalt der .txt Datei lesen
						txtLine = txt.readLine();
						if (txtLine == null) {
							break;
						}
						//kopiere Inhalt von txt Datei
						out.write("\t" + txtLine + "\n");
					}
					txt.close();
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
}
