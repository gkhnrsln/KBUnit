package prlab.kbunit.gui.windowParametrisierung;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import prlab.kbunit.business.windowParametrisierung.*;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.file.FileCreator;

/**
 * @author G&ouml;khan Arslan
 */

public class ParametrisierungController implements Initializable {
	
	private String klassePfad;
	
	// Tabelle Testparameter
	@FXML
	private TableView<ParametrisierungModel> parameterTableView;
	@FXML
	private TableColumn<ParametrisierungModel, String> typColumn;
	@FXML
	private TableColumn<ParametrisierungModel, String> attributColumn;
	@FXML
	private TableColumn<ParametrisierungModel, String> wertColumn;
	@FXML
	private TableColumn<ParametrisierungModel, String> descColumn;
	
	// Bedienung - Neue Zeile
	@FXML
	private ComboBox<String> typComboBox;
	@FXML
	private ComboBox<String> methodeComboBox;
	@FXML
	private TextField descTextField;
	@FXML
	private TextField parameterTextField;
	@FXML
	private TextField wertTextField;
	@FXML
	private Button addButton;
	@FXML
	private Button deleteButton;
	
	@FXML
	private Button saveButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	/**
	 * init Nodes
	 */
	public void initModel() {
		typColumn.setCellValueFactory(cellData
				-> cellData.getValue().getTyp());
		attributColumn.setCellValueFactory(cellData
				-> cellData.getValue().getAttribut());
		wertColumn.setCellValueFactory(cellData
				-> cellData.getValue().getWert());
		descColumn.setCellValueFactory(cellData
				-> cellData.getValue().getDesc());
		
		klassePfad = klassePfad
				.replace(Variables.TEST_PLAIN_SOURCE + "\\", "")
				.replace("\\", ".")
				.replace(".java", "");
		
		//***************fill Selection ComboBox************************************//
		typComboBox.setItems(ParametrisierungModel.datenTypen());
		methodeComboBox.setItems(ParametrisierungModel.methoden(klassePfad));
	}
	
	/**
	 * Pr&uuml;ft, ob das Formular korrekt ausgef&uuml;llt ist.
	 * @return true, wenn Eingaben korrekt 
	 */
	private boolean isInputCorrect() {
		//pruefe, ob Formular ausgefuellt ist
		if (! parameterTextField.getText().isBlank() && ! wertTextField.getText().isBlank() 
				&& ! descTextField.getText().isBlank()) {
			String datentyp = typComboBox.getSelectionModel().getSelectedItem().toString();
			String wert = wertTextField.getText();
			parameterTextField.setText(parameterTextField.getText().replace(" ",""));
			//wenn Datentyp boolean, pruefe, ob werte true/false
			if (datentyp.equals("boolean")) {
				if(wert.equals("true") || wert.equals("false")) return true;
				else {
					showMessage(AlertType.WARNING, "Problem!",
							"Falsche Eingabe erkannt!",
							"Geben Sie entweder \"true\" oder \"false\" ein!");
					return false;
				}
			} else {
				if (! datentyp.equals("String")) {
					//entferne leerzeichen bei Zahlen
					wertTextField.setText(wert.replace(" ",""));
					//nur Ziffern und bestimmte Zeichen erlaubt
					if (datentyp.equals("int") || datentyp.equals("long")) {
						//Vorzeichen (+/-) und nur Ziffern (0-9) erlaubt
						if (wert.matches("^[-+]?([1-9][0-9]*)")) return true;
						else {
							showMessage(AlertType.WARNING, "Problem!",
									"Falsche Eingabe erkannt!",
									"Geben Sie einen korrekten Wert ein!");
							return false;
						}
					} else if (datentyp.equals("float")) {
						//Vorzeichen (+/-), Ziffern 0-9, Einmalig (.) und am Ende ein (f/F) erlaubt
						if (wert.matches("^[-+]?([1-9][0-9]*)+(\\.\\d+)?[fF]$")) return true;
						else {
							showMessage(AlertType.WARNING, "Problem!",
									"Falsche Eingabe erkannt!",
									"Geben Sie einen korrekten float Wert (z. B. 3.04F) ein!");
							return false;
						}
					} else if (datentyp.equals("double")) {
						//Vorzeichen (+/-), Ziffern (0-9) und Einmalig (.) erlaubt
						if (wert.matches("^[-+]?([1-9][0-9]*)+(\\.\\d+)?$")) return true;
						else {
							showMessage(AlertType.WARNING, "Problem!",
									"Falsche Eingabe erkannt!",
									"Geben Sie einen korrekten double Wert ein!");
							return false;
						}
					}
				}
			}
			return true;
		}
		showMessage(AlertType.WARNING, "Problem!",
				"Formular unvollständig!",
				"Füllen Sie das Formular erst aus, bevor Sie die Werte hinzufügen wollen!");
		return false;
	}
	
	/**
	 * method for the add Button
	 * 
	 * F&uuml;ge der Tabelle eine neue Zeile hinzu. Es wird weiterhin darauf geachtet,
	 * dass keine Duplikate zu den Attributen existieren.
	 */
	@FXML
	private void addToParamList(ActionEvent e) {
		String typ;
		String attr;
		String wert;
		String desc;
		boolean isDuplicate = false;

		if (isInputCorrect()) {
			typ = typComboBox.getSelectionModel().getSelectedItem().toString();
			attr = methodeComboBox.getSelectionModel().getSelectedItem().toString()
					+ "_" + parameterTextField.getText().trim();
			wert = wertTextField.getText().trim();
			desc = descTextField.getText().trim();
			//Gehe jeden Eintrag der Tabelle durch
			for (int i = 0;  i < parameterTableView.getItems().size(); i++) {
				//pruefe, ob aktuelle Zeile Duplikat
				if (attributColumn.getCellData(i).equals(attr)) {
					isDuplicate = true;
					showMessage(AlertType.WARNING, "Problem!",
							"Duplikat erkannt!",
							"Geben Sie eine andere Bezeichnung für das Attribut ein!");
					break;
				}
			}
			if (!isDuplicate) {
				parameterTableView.getItems().add(
						new ParametrisierungModel(typ,attr,wert,desc));
			}
		}
	}
	
	/**
	 * method for the delete Button
	 * @param e
	 */
	@FXML
	private void deleteFromParamList(ActionEvent e) {
		int index = parameterTableView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			parameterTableView.getItems().remove(index);
		}
	}
	
	/**
	 * 
	 * @param e
	 */
	@FXML
	private void saveParamList(ActionEvent e) {
		String typ;
		String attribut;
		String wert;
		String desc;
		StringBuilder sb;
		
		BufferedWriter out;
		//Speicherort der Parameter
		File file = new File(Variables.PARAMETER_FILE_PATH);
		try {
			out = new BufferedWriter(new FileWriter(file));
			for (ParametrisierungModel s : parameterTableView.getItems()) {
				typ = s.getTyp().getValue();
				attribut = s.getAttribut().getValue();
				wert = s.getWert().getValue();
				desc = s.getDesc().getValue();
				
				//Zeilenumbruch, bei laengeren Kommentaren
				sb = new StringBuilder(desc);
				int i = 0;
				while ((i = sb.indexOf(" ", i + 75)) != -1) {
				    sb.replace(i, i + 1, "\n * ");
				}
				//setze beim Datentyp String den Wert in hochkomma
				if (typ.equals("String")) wert = "\"" + wert + "\"";
				
				//Formatierung
				out.write("/** " + sb + " */\npublic static " + typ + " " + attribut + " = " + wert + ";\n");
			}
			out.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		//parametrisiere und speichere Datei
		insertAttributes("\\" + klassePfad.replace(".", "/") + ".java");
		
		//loesche Parameter.txt
		file.delete();
		
		//Meldung, das erfolgreich erstellt wurde
		showMessage(AlertType.INFORMATION, "Information",
				"Generierte Testklasse wurde erfolgreich gespeichert!",
				"Siehe im Sourceverzeichnis \"testPlain\"!");
		
	}
	
	/**
	 * 
	 * @param path
	 */
	public static void insertAttributes(String path) {
		List<String> listeTestAttribute = new ArrayList<>();
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
				
				for (String methodeName : ParametrisierungModel.getTestMethode(strPath, false)) {
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
	
	//Info Fenster
	private void showMessage(AlertType alertType, String title, 
			String header, String message) {
			Alert alert = new Alert(alertType);
			alert.setTitle(title);
			alert.setHeaderText(header);
			alert.setContentText(message);
			alert.showAndWait();
		}
	
	//getter setter
	public String getKlassePfad() {
		return klassePfad;
	}

	public void setKlassePfad(String klassePfad) {
		this.klassePfad = klassePfad;
	}
}
