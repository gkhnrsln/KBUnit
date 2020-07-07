package prlab.kbunit.gui.windowParametrisierung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import prlab.kbunit.business.windowParametrisierung.*;
import prlab.kbunit.enums.Variables;
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
	private TableColumn<ParametrisierungModel, String> methodeColumn;
	@FXML
	private TableColumn<ParametrisierungModel, String> parameterColumn;
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
	
	public void initModel() {
		typColumn.setCellValueFactory(cellData
				-> cellData.getValue().getTyp());
		methodeColumn.setCellValueFactory(cellData
				-> cellData.getValue().getMethode());
		parameterColumn.setCellValueFactory(cellData
				-> cellData.getValue().getParameter());
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
	
	
	private boolean isInputCorrect() {
		//pruefe, ob Formular ausgefuellt ist
		if (! parameterTextField.getText().isBlank() && ! wertTextField.getText().isBlank() && ! descTextField.getText().isBlank()) {
			String datentyp = typComboBox.getSelectionModel().getSelectedItem().toString();
			String wert = wertTextField.getText();
			parameterTextField.setText(parameterTextField.getText().replace(" ",""));
			//wenn Datentyp boolean, pruefe, ob werte true/false
			if (datentyp.equals("boolean")) {
				if(wert.equals("true") || wert.equals("false")) return true;
				else return false;
			} else {
				if (! datentyp.equals("String")) {
					//entferne leerzeichen bei Zahlen
					wertTextField.setText(wert.replace(" ",""));
					//nur Ziffern und bestimmte Zeichen erlaubt
					if (datentyp.equals("int") || datentyp.equals("long")) {
						if (wert.matches("[0-9]+")) return true;
						else return false;
					} else if (datentyp.equals("float")) {
						if (wert.matches("[Ff.0-9]+")) return true;
						else return false;
					} else if (datentyp.equals("double")) {
						if (wert.matches("[.0-9]+")) return true;
						else return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Fuege der Tabelle eine neue Zeile hinzu.
	 * 
	 * TODO: Es sollen keine Duplikate (Methode + Parameter) in der Tabelle vorkommen
	 * 
	 * @param e
	 */
	@FXML
	private void addToParamList(ActionEvent e) {
		
		if (isInputCorrect()) {
			parameterTableView.getItems().add(new ParametrisierungModel(
					typComboBox.getSelectionModel().getSelectedItem().toString(),
					methodeComboBox.getSelectionModel().getSelectedItem().toString(),
					parameterTextField.getText().trim(),
					wertTextField.getText().trim(),
					descTextField.getText().trim())
			);
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
	
	/*
	 * Tabelle wird in "Parameter.csv"  gespeichert.
	 * 
	 * TODO: 
	 * es wird nach vorkommen gesucht und anwender gefragt ob ersetzt werden soll
	 * und Fenster schliesst sich
	 */
	@FXML
	private void saveParamList(ActionEvent e) {
		String typ;
		String methode;
		String parameter;
		String wert;
		String desc;
		StringBuilder sb;
		
		BufferedWriter out;
		try {
			//Speicherort der Parameter
			out = new BufferedWriter(new FileWriter(".\\testKBUnit\\prlab\\kbunit\\business\\transfer\\Parameter.csv"));
			for (ParametrisierungModel s : parameterTableView.getItems()) {
				typ = s.getTyp().getValue();
				methode = s.getMethode().getValue();
				parameter = s.getParameter().getValue();
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
				out.write("/** " + sb + " */\npublic static " + typ + " " + methode + "_" + parameter + " = " + wert + ";\n");
			}
			out.close();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		//close window

	}

	//TODO
	void schreibeParameterInDatei() {
		//ParametrisierungModel.schreibeParameterInCsvDatei();
		//meldungsfenster aus view
		
	}
	
	//getter setter
	public String getKlassePfad() {
		return klassePfad;
	}

	public void setKlassePfad(String klassePfad) {
		this.klassePfad = klassePfad;
	}
}
