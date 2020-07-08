package prlab.kbunit.business.transfer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import prlab.kbunit.enums.Variables;

public class SANDKASTEN {
	static void saveKlasse() {

		BufferedReader in;
		BufferedReader csv;
		BufferedWriter out;
		try {
			String zeile;
			String csvLine;
			in = new BufferedReader(new FileReader(Variables.TEST_PLAIN_SOURCE + "/darlehen/Beispiel" + Variables.EXTENSION_TEST_PLAIN_JAVA));
			csv = new BufferedReader(new FileReader("testKBUnit/prlab/kbunit/business/transfer/Parameter.txt"));
			
			out = new BufferedWriter(new FileWriter("BAUKASTEN.java"));
			
			while (true) {
				zeile = in.readLine();
				//Dateiende erreicht, abbrechen
				if (zeile == null) {
					break;
				}
				//falls Klassenname, eine zeile drunter die attribute hinzufügen
				if (zeile.contains("class")) {
					out.write(zeile.replace("Plain", "") + "\n");
					while (true) {
						csvLine = csv.readLine();
						if (csvLine == null) {
							break;
						}
						//kopiere Inhalt von txt Datei
						out.write("\t" + csvLine + "\n");
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
		saveKlasse();
		/*
		File file = new File(Variables.TEST_PLAIN_SOURCE + "/darlehen/Beispiel" + Variables.EXTENSION_TEST_PLAIN_JAVA);
		String strPath = file.getParent().replace(Variables.TEST_PLAIN_SOURCE + "\\", "") + "." + file.getName().replace(Variables.EXTENSION_JAVA, "");
		
		Class<?> clazz = null;
		try {
			clazz = Class.forName(strPath);
			klasse = clazz.getSimpleName();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		System.out.println(klasse);
		*/
	}

}
