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

import org.apache.commons.lang3.StringUtils;

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
	
	private ParametrisierungModel parametrisierungModel;
	
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
		
		parametrisierungModel = new ParametrisierungModel(null, null, null, null);
		//***************fill Selection ComboBox************************************//
		typComboBox.setItems(parametrisierungModel.datenTypen());
		try {
			methodeComboBox.setItems(parametrisierungModel.methoden(klassePfad));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Pr&uuml;ft, ob das Formular korrekt ausgef&uuml;llt ist.
	 * @return true, wenn Eingaben korrekt 
	 */
	/*
	 * TODO: 
	 * - Minimum/Maximum Werte bei Zahlen pruefen?
	 */
	private boolean isInputCorrect() {
		//pruefe, ob Formular ausgefuellt ist
		if (! parameterTextField.getText().isBlank() 
				&& ! wertTextField.getText().isBlank() 
				&& ! descTextField.getText().isBlank()
				&& ! typComboBox.getSelectionModel().isSelected(-1)
				&& ! methodeComboBox.getSelectionModel().isSelected(-1)
				)
		{
			/*
			String datentyp = typComboBox.getSelectionModel().getSelectedItem().toString();
			String wert = wertTextField.getText();
			//wenn Datentyp boolean, pruefe, ob werte true/false
			if (datentyp.equals("boolean")) {
				if(wert.equals("true") || wert.equals("false")) return true;
				else falscheEingabe("boolean"); return false;
			} else {
				if (! datentyp.equals("String")) {
					//entferne leerzeichen bei Zahlen
					wertTextField.setText(wert.replace(" ",""));
					//nur Ziffern und bestimmte Zeichen erlaubt
					if (datentyp.equals("int") || datentyp.equals("long") || datentyp.equals("short")) {
						if (wert.matches("^[-+]?([1-9][0-9]*)")) return true;
						else falscheEingabe("int/long/short"); return false;
					} else if (datentyp.equals("char")) {
						if (wert.matches("(^[a-zA-Z]$)|^[-+]?\\b[0-9]+\\b")) return true;
						else falscheEingabe("char"); return false;
					} else if (datentyp.equals("float")) {
						if (wert.matches("^[-+]?([1-9][0-9]*)+(\\.\\d+)?[fF]$")) return true;
						else falscheEingabe("float"); return false;					
					} else if (datentyp.equals("double")) {
						if (wert.matches("^[-+]?([1-9][0-9]*)+(\\.\\d+)?$")) return true;
						else falscheEingabe("double"); return false;
					}
				}
			}*/
			return true;
		}
		showMessage(AlertType.WARNING, "Problem!",
				"Formular unvollständig!",
				"Füllen Sie das Formular aus, bevor Sie Werte hinzufügen wollen!");
		return false;
	}
	
	/**
	 * method for the add Button
	 * 
	 * F&uuml;ge der Tabelle eine neue Zeile hinzu. Es wird weiterhin darauf geachtet,
	 * dass keine Duplikate zu den Attributen existieren. Der erste Buchstabe des
	 * Parameters wird automatisch in Gro&szlig;buchstaben abgespeichert.
	 * 
	 * Konkatenation bei Parameter und Bezeichner der Testmethode, z. B.:
	 * {@code testMethode1_Parameter}
	 */
	@FXML
	private void addToParamList(ActionEvent e) {
		boolean isDuplicate = false;
		parameterTextField.setText(parameterTextField.getText().replace(" ",""));
		
		if (isInputCorrect()) {
			String typ = typComboBox.getSelectionModel().getSelectedItem();
			String attr = methodeComboBox.getSelectionModel().getSelectedItem()
					+ "_" + StringUtils.capitalize(parameterTextField.getText()); //erster Buchstabe gross
			String wert = wertTextField.getText().trim();
			String desc = descTextField.getText().trim();
			
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
				deleteButton.setDisable(false);
				saveButton.setDisable(false);
			}
		}
	}
	
	/**
	 * method for the delete Button
	 * 
	 * Entfernt ein Zeilenelement aus der Tabelle. Ist keine Zeile vorher ausgew&auml;hlt
	 * worden, folgt ein Alert.
	 * @param e
	 */
	@FXML
	private void deleteFromParamList(ActionEvent e) {
		int index = parameterTableView.getSelectionModel().getSelectedIndex();
		if (index >= 0) {
			parameterTableView.getItems().remove(index);
		} else {
			showMessage(AlertType.WARNING, "Problem!", 
					"Kein Testattribut ausgewählt!",
					"Bitte wählen Sie ein Testattribut aus!");
		}
		if (parameterTableView.getItems().isEmpty()) {
			saveButton.setDisable(true);
			deleteButton.setDisable(true);
		}
	}
	
	/**
	 * method for the transfer Button
	 * 
	 * Speichert die neuen Attribute in einer vorl&auml;ufigen Datei.
	 * 
	 * Ruft anschlie&szlig;end die Methode f&uuml;r das Generieren der neuen 
	 * KBUnit-f&auml;higen JUnit-Testklasse auf.
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
			for (ParametrisierungModel pm : parameterTableView.getItems()) {
				typ = pm.getTyp().getValue();
				attribut = pm.getAttribut().getValue();
				wert = pm.getWert().getValue();
				desc = pm.getDesc().getValue();
				
				//Zeilenumbruch, bei laengeren Kommentaren
				sb = new StringBuilder(desc);
				int i = 0;
				while ((i = sb.indexOf(" ", i + 75)) != -1) {
				    sb.replace(i, i + 1, "\n * ");
				}
				//Formatierungen bei String und char
				//TODO: Soll jetzt auf sinnvolle werte geprueft werden?
				if (typ.equals("String")) {
					wert = "\"" + wert + "\"";
				//Falls char Zeichen
				} else if (typ.equals("char") && wert.matches("^[a-zA-Z]$")) {
					wert = "'" + wert + "'";		
				}
				out.write("/** " + sb + " */\npublic static " + typ + " " + attribut + " = " + wert + ";\n");
			}
			out.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//parametrisiere und speichere Datei
		saveFile("\\" + klassePfad.replace(".", "/") + ".java");
		
		//loesche Parameter.txt
		file.delete();
		
		//Meldung, das erfolgreich erstellt wurde
		showMessage(AlertType.INFORMATION, "Information",
				"Generierte Testklasse wurde erfolgreich gespeichert!",
				"Siehe im Sourceverzeichnis \"test\"!");
	}
	
	/**
	 * generiert neuen KBUnit-f&auml;higen JUnit-Testklasse
	 * @param path 
	 */
	public void saveFile(String path) {
		List<String> listeTestAttribute = new ArrayList<>();
		BufferedReader quelle, txt;
		BufferedWriter ausgabe;
		String zeile, txtLine;
		List<String> temp = new ArrayList<>();
		String strPath = path
				.replace("\\","")
				.replace("/", ".")
				.replace(Variables.EXTENSION_JAVA, "");
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
				//falls Klassenname: Zeile drunter attribute hinzufuegen
				if (zeile.startsWith("class") || zeile.startsWith("public class")) {
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
			String strMethode = "";
			while (true) {
				zeile = quelle.readLine();
				if (zeile == null) break; //Dateiende
				try {
					for (String methodeName : parametrisierungModel.methoden(strPath)) {
						//method gefunden
						if(zeile.contains(methodeName + "(")) {
							strMethode = methodeName;
							temp.clear(); //leere Liste fuer neue Inhalte
							//pruefe, ob Attribute zur Methode passen
							for (String attr : listeTestAttribute) {
								String strAttrName = attr.substring(attr.indexOf("test"), attr.indexOf("_"));
								if(methodeName.equals(strAttrName)) temp.add(attr);
							}
							break;
						}
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (String attr : temp) {
					String strAttrNameFull = attr.substring(attr.indexOf("test"), attr.indexOf("=") - 1);
					String strAttrVal = attr.substring(attr.indexOf("=") + 2, attr.indexOf(";"));
					//wenn wert identisch mit testattributwert
					if (zeile.contains(strAttrVal)) {
						//falls in einer Zeile mehrere Faelle vorhanden
						int count = StringUtils.countMatches(zeile, strAttrVal);
						StringBuilder sb = new StringBuilder(zeile);
						int n = 0;
						int index;
						for (int i = 0; i < count; i++) {
							index = ordinalIndexOf("" + sb, strAttrVal, n);
							//Fenster
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Bestätigung für Methode [" + strMethode + "]");
							alert.setHeaderText("Zu ersetzenden Wert [" + strAttrVal +"] "
									+ "in folgender Zeile gefunden:\n" + zeile.trim()
									+ "\nNachher\n" 
									+ sb.replace(index, index + strAttrVal.length(), strAttrNameFull).toString().trim()
									);
							alert.setContentText("Sind Sie damit einverstanden?");
							
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){
								zeile = "" + sb;
								n = 0;
							} else {
								zeile = "" + sb.replace(index, index + strAttrNameFull.length(), strAttrVal);
								n++;
							}
						}	
					}
				}
				ausgabe.write(zeile + "\n");
			}
			quelle.close();
			ausgabe.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Finden das n-te Vorkommen eines substring in einer Zeichenfolge.
	 * 
	 * Aus <a href="https://programming.guide/java/nth-occurrence-in-string.html">https://programming.guide/</a>
	 * &uuml;bernommen.
	 * @param str Zeichenkette
	 * @param substr Zu suchender Wert
	 * @param n das n-te Vorkommen in der Zeichenkette
	 * @return
	 */
	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	
	/*
	private void falscheEingabe(String typ) {
		showMessage(AlertType.WARNING, "Problem!",
				"Falsche Eingabe erkannt!",
				"Geben Sie einen korrekten " + typ + " Wert ein!");
	}
	*/
	
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
