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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import prlab.kbunit.business.windowParametrisierung.ParametrisierungModel;
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
		
		parametrisierungModel = new ParametrisierungModel();
		//***************fill Selection ComboBox************************************//
		typComboBox.getItems().addAll(parametrisierungModel.datenTypen());
		try {
			methodeComboBox.getItems().addAll(parametrisierungModel.methoden(klassePfad));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		//addButton erst aktiv, wenn Formular ausgefuellt
		BooleanBinding booleanBind = descTextField.textProperty().isEmpty()
				.or(parameterTextField.textProperty().isEmpty())
				.or(wertTextField.textProperty().isEmpty())
				.or(typComboBox.getSelectionModel().selectedIndexProperty()
						.isEqualTo(-1))
				.or(methodeComboBox.getSelectionModel().selectedIndexProperty()
						.isEqualTo(-1));
		addButton.disableProperty().bind(booleanBind);
		//deleteButton
		deleteButton.disableProperty().bind(Bindings.isEmpty(
				parameterTableView.getItems()));
		//saveButton
		saveButton.disableProperty().bind(Bindings.isEmpty(
				parameterTableView.getItems()));
	}
	
	/**
	 * Pr&uuml;ft, ob der Wert in Zusammenhang mit dem ausgew&auml;hlten Datentypen
	 * korrekt ist. Bei Zahlen wird auf Min und Max werte geachtet. Fehlende 
	 * Formatierungen bei 'String' und ggf. 'char' werden erg&auml;nzt. L&auml;ngere
	 * Zahlendarstellungen (z. B. bei 'double') werden in die wissenschaftliche
	 * Schreibweise umgewandelt.
	 * @return true, wenn Wert korrekt
	 */
	private boolean isInputCorrect() {
		//pruefe, ob Formular ausgefuellt ist
		String typ = typComboBox.getSelectionModel().getSelectedItem();
		String wert = wertTextField.getText();
		
		if (typ.equals("String") && ! wert.matches("^\".+\"$")) {
			wertTextField.setText("\"" + wert + "\"");
		} else if (typ.equals("boolean") && ! wert.matches("(true|false)")) {
			falscheEingabe("boolean"); return false;
		} else if (typ.equals("char")) {
			//wenn (pos) Zahl
			if (wert.matches("^[0-9]+$")) {
				if (Integer.parseInt(wert) > Character.MAX_VALUE) {
					falscheEingabe("char"); return false;						
				}	
			} else if (wert.matches("^'.'$")) {
				return true;
			} else if (wert.matches("^.$")) {
				//fehlende Formatierung wird ergaenzt
				if (!wert.startsWith("'") && !wert.endsWith("'")) {
					wertTextField.setText("'" + wert + "'");
				}
			} else {
				falscheEingabe("char"); return false;
			}
		} else if (typ.equals("int")) {
			try {
				Integer.parseInt(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("int"); return false;
			}
		} else if (typ.equals("long")) {
			try {
				Long.parseLong(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("long"); return false;
			}
		} else if (typ.equals("short")) {
			try {
				Short.parseShort(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("short"); return false;
			}
		} else if (typ.equals("byte")) {
			try {
				Byte.parseByte(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("byte"); return false;
			}
		} else if (typ.equals("float")) {
			try {
				wertTextField.setText(Float.parseFloat(wert) + "f");
			} catch (NumberFormatException nfe) {
				falscheEingabe("float"); return false;
			}
		} else if (typ.equals("double")) {
			try {
				wertTextField.setText(Double.parseDouble(wert) + "");
			} catch (NumberFormatException nfe) {
				falscheEingabe("double"); return false;
			}
		}
		return true;
	}
	
	/**
	 * method for the add Button
	 * 
	 * F&uuml;ge der Tabelle eine neue Zeile hinzu. Es wird weiterhin darauf geachtet,
	 * dass keine Duplikate zu den Attributen existieren.
	 * 
	 * Konkatenation bei Parameter und Bezeichner der Testmethode, z. B.:
	 * {@code testMethode1_Parameter}
	 */
	@FXML
	private void addToParamList(ActionEvent e) {
		boolean isDuplicate = false;
		//verhindere leerzeichen beim Parameter
		parameterTextField.setText(parameterTextField.getText().replace(" ",""));

		if (isInputCorrect()) {
			String strType = typComboBox.getSelectionModel().getSelectedItem();
			String strAttr = methodeComboBox.getSelectionModel().getSelectedItem()
					+ "_" + parameterTextField.getText(); 
			String strVal = wertTextField.getText().trim();
			String strDesc = descTextField.getText().trim();
			
			//Gehe jeden Eintrag der Tabelle durch
			for (int i = 0; i < parameterTableView.getItems().size(); i++) {
				//pruefe, ob aktuelle Zeile Duplikat
				if (attributColumn.getCellData(i).equals(strAttr)) {
					isDuplicate = true;
					showMessage(AlertType.WARNING, "Problem!",
							"Duplikat erkannt!",
							"Geben Sie eine andere Bezeichnung für das Attribut ein!");
					break;
				}
			}
			//falls kein Duplikat, fuege der Tabelle hinzu
			if (!isDuplicate) {
				parameterTableView.getItems().add(
						new ParametrisierungModel(strType,strAttr,strVal,strDesc));
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
					"Kein Testattribut zum Löschen ausgewählt!",
					"Bitte wählen Sie ein Testattribut aus!");
		}
	}
	
	/**
	 * method for the transfer Button
	 * 
	 * Speichert die neuen Zeilen des Testattribute in einer vorl&auml;ufigen Datei.
	 * 
	 * Ruft anschlie&szlig;end die Methode f&uuml;r das Generieren der neuen 
	 * KBUnit-f&auml;higen JUnit-Testklasse auf.
	 * @param e
	 */
	@FXML
	private void saveParamList(ActionEvent e) {
		//Parameter.txt
		File file = new File(Variables.PARAMETER_FILE_PATH);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			for (ParametrisierungModel pm : parameterTableView.getItems()) {
				//Zeilenumbruch, bei laengeren Kommentaren
				StringBuilder sb = new StringBuilder(pm.getDesc().getValue());
				int i = 0;
				while ((i = sb.indexOf(" ", i + 75)) != -1) {
				    sb.replace(i, i + 1, "\n * ");
				}
				out.write("/** " + sb + " */\n");
				out.write("public static " + pm.getTyp().getValue());
				out.write(" " + pm.getAttribut().getValue());
				out.write(" = " + pm.getWert().getValue() + ";\n");
			}
			out.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		//parametrisiere und speichere Datei
		saveFile("\\" + klassePfad.replace(".", "/") + Variables.EXTENSION_JAVA);
		
		file.delete();
		
		showMessage(AlertType.INFORMATION, "Information",
				"Generierte Testklasse wurde erfolgreich gespeichert!",
				"Siehe im Sourceverzeichnis \"" + Variables.TEST_SOURCE + "\"!");
	}
	
	/**
	 * Generiert neuen KBUnit-f&auml;higen JUnit-Testklasse.
	 * @param path 
	 */
	private void saveFile(String path) {
		String strZeile, txtLine;
		List<String> liTestAttr = new ArrayList<>();
		
		try {
			//TestPlain-Datei
			BufferedReader quelle = new BufferedReader(new FileReader(
					Variables.TEST_PLAIN_SOURCE + path));
			//Parameter-Datei
			BufferedReader txt = new BufferedReader(new FileReader(
					Variables.PARAMETER_FILE_PATH));
			//Generierte KBUnit-faehige JUnit Testklasse
			File newFile = new File(Variables.TEST_SOURCE 
					+ "/" + path.replace(Variables.TEST_PLAIN_NAME,
							Variables.TEST_NAME));
			BufferedWriter out = new BufferedWriter(new FileWriter(
					FileCreator.createMissingPackages(newFile)));
			
			while (true) {
				strZeile = quelle.readLine();
				//falls Klassenname: Zeile drunter Attribute hinzufuegen
				if (strZeile.matches("(class|public class).+Test.+")) {
					//Klassenname anpassen
					out.write(strZeile.replace(Variables.TEST_PLAIN_NAME, 
							Variables.TEST_NAME) + "\n");
					while (true) {
						//Inhalt der .txt Datei lesen
						txtLine = txt.readLine();
						//brich ab, wenn Dateiende erreicht
						if (txtLine == null) break;
						//kopiere Inhalt von .txt Datei
						out.write("\t" + txtLine + "\n");
						//falls aktuelle Zeile eine testAttribut Deklaration ist
						if (txtLine.matches("public static.+test.+_.+=.+$")) 
							liTestAttr.add(txtLine);
					}
					txt.close();
					break;
				}
				out.write(strZeile + "\n");
			}
			//ab letzte Testattribut Zeile
			List<String> liTestAttrDekl = new ArrayList<>();
			while (true) {
				strZeile = quelle.readLine();
				//brich ab, wenn Dateiende erreicht
				if (strZeile == null) break; 
				/*
				 * Zeile aus "TestPlain" prüfen, ob es ein Methode der Combobox ist.
				 */
				for (String methodeName : methodeComboBox.getItems()) {
					//method gefunden
					if(strZeile.contains(methodeName + "(")) {
						liTestAttrDekl.clear();
						//pruefe, ob Attribute zur Methode passen
						for (String attr : liTestAttr) {
							// ... "testABC"_XYZ ...
							if(methodeName.equals(attr.substring(attr.
									indexOf("test"), attr.indexOf("_")))) 
								liTestAttrDekl.add(attr);
						}
						break;
					}
				}
				
				for (String attr : liTestAttrDekl) {
					// attr -> [public static ... testMethode_Attr1 = Wert;]
					// public static ... [testMethode_Attr1] = Wert;
					String strAttrName = attr.substring(attr.indexOf("test"),
							attr.indexOf("=") - 1);
					// public static ... testMethode_Attr1 = [Wert];
					String strAttrVal = attr.substring(attr.indexOf("=") + 2, 
							attr.indexOf(";"));
					// public static ... [testMethode]_Attr1 = Wert;
					String strMethode = attr.substring(attr.indexOf("test"), 
							attr.indexOf("_"));
					//wenn testattributwert vorkommt
					if (strZeile.contains(strAttrVal)) {
						//falls in einer Zeile mehrere Faelle vorhanden
						int count = StringUtils.countMatches(strZeile, strAttrVal);
						//fuer die Vorschau der Aenderung
						StringBuilder sb = new StringBuilder(strZeile);
						int n = 1;
						for (int i = 0; i < count; i++) {
							//finde den n-ten gesuchten substring innerhalb eines Strings
							int index = StringUtils.ordinalIndexOf(sb, strAttrVal, n);
							//Bestaetigungs-Fenster
							sb.replace(index, index + strAttrVal.length(), strAttrName);
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Bestätigung für Methode [" + strMethode + "]");
							alert.setHeaderText("Zu ersetzender Wert [" + strAttrVal +"]"
									+ " in folgender Zeile gefunden:\n" + strZeile.trim()
									+ "\nNachher\n" 
									+ sb.toString().trim()
									);
							alert.setContentText("Sind Sie damit einverstanden?");
							//Aenderung bestaetigen oder verweigern
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){
								strZeile = sb.toString();
							} else {
								//Aenderung rueckgaengig machen
								sb.replace(index, index + strAttrName.length(), strAttrVal);
								//Treffer beim naechsten Durchlauf ueberspringen
								n++; 
							}
						}	
					}
				}
				out.write(strZeile + "\n");
			}
			quelle.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void falscheEingabe(String typ) {
		showMessage(AlertType.WARNING, "Problem!",
				"Falsche Eingabe erkannt!",
				"Geben Sie einen korrekten " + typ + " Wert ein!");
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
