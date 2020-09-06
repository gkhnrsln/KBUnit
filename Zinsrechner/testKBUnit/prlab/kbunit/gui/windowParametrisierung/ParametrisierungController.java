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
		typComboBox.setItems(parametrisierungModel.datenTypen());
		try {
			methodeComboBox.setItems(parametrisierungModel.methoden(klassePfad));
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
		String datentyp = typComboBox.getSelectionModel().getSelectedItem();
		String wert = wertTextField.getText();
		if (datentyp.equals("String") && !wert.startsWith("\"") && !wert.endsWith("\"")) {
			wertTextField.setText("\"" + wert + "\"");
		} else if (datentyp.equals("boolean")) {
			if(! wert.equals("true") && ! wert.equals("false")) {
				falscheEingabe("boolean"); return false;
			}
		} else if (datentyp.equals("char")) {
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
		} else if (datentyp.equals("int")) {
			try {
				Integer.parseInt(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("int"); return false;
			}
		} else if (datentyp.equals("long")) {
			try {
				Long.parseLong(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("long"); return false;
			}
		} else if (datentyp.equals("short")) {
			try {
				Short.parseShort(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("short"); return false;
			}
		} else if (datentyp.equals("byte")) {
			try {
				Byte.parseByte(wert);
			} catch (NumberFormatException nfe) {
				falscheEingabe("byte"); return false;
			}
		} else if (datentyp.equals("float")) {
			try {
				wertTextField.setText(Float.parseFloat(wert) + "f");
			} catch (NumberFormatException nfe) {
				falscheEingabe("float"); return false;
			}
		} else if (datentyp.equals("double")) {
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
					"Kein Testattribut ausgewählt!",
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
		StringBuilder sb;
		BufferedWriter out;
		//Speicherort der Parameterzeilen
		File file = new File(Variables.PARAMETER_FILE_PATH);
		try {
			out = new BufferedWriter(new FileWriter(file));
			for (ParametrisierungModel pm : parameterTableView.getItems()) {
				//Zeilenumbruch, bei laengeren Kommentaren
				sb = new StringBuilder(pm.getDesc().getValue());
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
		saveFile("\\" + klassePfad.replace(".", "/") + ".java");
		
		//loesche Parameter.txt
		file.delete();
		
		//Meldung, das erfolgreich erstellt wurde
		showMessage(AlertType.INFORMATION, "Information",
				"Generierte Testklasse wurde erfolgreich gespeichert!",
				"Siehe im Sourceverzeichnis \"" + Variables.TEST_SOURCE + "\"!");
	}
	
	/**
	 * Generiert neuen KBUnit-f&auml;higen JUnit-Testklasse.
	 * @param path 
	 */
	public void saveFile(String path) {
		String strZeile, txtLine;
		List<String> temp = new ArrayList<>();
		List<String> listeTestAttribute = new ArrayList<>();
		
		try {
			//TestPlain-Datei
			BufferedReader quelle = new BufferedReader(new FileReader(
					Variables.TEST_PLAIN_SOURCE + path));
			//Parameter-Datei
			BufferedReader txt = new BufferedReader(new FileReader(
					Variables.PARAMETER_FILE_PATH));
			//Generierte KBUnit-faehige JUnit Testklasse
			File newFile = new File(Variables.TEST_SOURCE 
					+ "/" + path.replace(Variables.TEST_PLAIN_NAME, Variables.TEST_NAME));
			BufferedWriter out = new BufferedWriter(new FileWriter(
					FileCreator.createMissingPackages(newFile)));
			
			while (true) {
				strZeile = quelle.readLine();
				//falls Klassenname: Zeile drunter Attribute hinzufuegen
				if (strZeile.startsWith("class") || strZeile.startsWith("public class")) {
					//Klassenname anpassen
					out.write(strZeile.replace(Variables.TEST_PLAIN_NAME, Variables.TEST_NAME) + "\n");
					while (true) {
						//Inhalt der .txt Datei lesen
						txtLine = txt.readLine();
						//brich ab, wenn Dateiende erreicht
						if (txtLine == null) break;
						//kopiere Inhalt von .txt Datei
						out.write("\t" + txtLine + "\n");
						//falls aktuelle Zeile eine testAttribut Deklaration ist
						if (txtLine.contains("public static") && txtLine.contains("test") 
								&& txtLine.contains("=")) {
							listeTestAttribute.add(txtLine);
						}
					}
					txt.close();
					break;
				}
				out.write(strZeile + "\n");
			}
			//ab letzte Testattribut Zeile
			String strMethode = "";
			while (true) {
				strZeile = quelle.readLine();
				//brich ab, wenn Dateiende erreicht
				if (strZeile == null) break; 
				
				for (String methodeName : methodeComboBox.getItems()) {
					//method gefunden
					if(strZeile.contains(methodeName + "(")) {
						strMethode = methodeName;
						temp.clear(); //leere Liste fuer neue Inhalte
						//pruefe, ob Attribute zur Methode passen
						for (String attr : listeTestAttribute) {
							String strAttrName = attr.substring(attr.indexOf("test"),
									attr.indexOf("_"));
							if(methodeName.equals(strAttrName)) temp.add(attr);
						}
						break;
					}
				}
				
				for (String attr : temp) {
					String strAttrNameFull = attr.substring(attr.indexOf("test"),
							attr.indexOf("=") - 1);
					String strAttrVal = attr.substring(attr.indexOf("=") + 2, attr.indexOf(";"));
					//wenn wert identisch mit testattributwert
					if (strZeile.contains(strAttrVal)) {
						//falls in einer Zeile mehrere Faelle vorhanden
						int count = StringUtils.countMatches(strZeile, strAttrVal);
						StringBuilder sb = new StringBuilder(strZeile);
						int n = 0;
						int index;
						for (int i = 0; i < count; i++) {
							index = ordinalIndexOf("" + sb, strAttrVal, n);
							//Fenster
							Alert alert = new Alert(AlertType.CONFIRMATION);
							alert.setTitle("Bestätigung für Methode [" + strMethode + "]");
							alert.setHeaderText("Zu ersetzenden Wert [" + strAttrVal +"] "
									+ "in folgender Zeile gefunden:\n" + strZeile.trim()
									+ "\nNachher\n" 
									+ sb.replace(index, index + strAttrVal.length(), strAttrNameFull).toString().trim()
									);
							alert.setContentText("Sind Sie damit einverstanden?");
							
							Optional<ButtonType> result = alert.showAndWait();
							if (result.get() == ButtonType.OK){
								strZeile = "" + sb;
								n = 0;
							} else {
								strZeile = "" + sb.replace(index, index + strAttrNameFull.length(), strAttrVal);
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
