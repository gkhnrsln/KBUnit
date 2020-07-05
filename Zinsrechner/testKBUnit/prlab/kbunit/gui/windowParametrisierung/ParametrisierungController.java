package prlab.kbunit.gui.windowParametrisierung;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private TextArea descTextArea;
	@FXML
	private TextField methodeTextField;
	@FXML
	private TextField wertTextField;
	
	@FXML
	private Button saveButton;
	
	@FXML
	private Button addButton;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//button erst aktiv, wenn "Formular" ausgefuellt ist
		addButton.setDisable(true);
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
		
		klassePfad = klassePfad.replace(Variables.TEST_PLAIN_SOURCE + "\\", "").replace("\\", ".").replace(".java", "");
		//***************fill Selection ComboBox************************************//
		typComboBox.setItems(ParametrisierungModel.datenTypen());
		//methodeComboBox.setItems(ParametrisierungModel.methoden());
		methodeComboBox.setItems(ParametrisierungModel.methoden(klassePfad));
		
		parameterTableView.setItems(getParametrisierungModel());
		parameterTableView.setEditable(true);
	}
	
	//TODO: muss eher in Modell-Klasse
	@FXML
	public ObservableList<ParametrisierungModel> getParametrisierungModel() {
		//die Parameter sollen von dem "Formular" uebernommen werden
		ParametrisierungModel zeile1 =
				new ParametrisierungModel("String","testBerechneGesamtschuld","User","Musterperson","vom Wissenstr[:a]ger einstellbarer Name des Benutzers des Darlehens");
		ParametrisierungModel zeile2 = 
				new ParametrisierungModel("String","testBerechneGesamtschuld","Darlehen","100000000","vom Wissenstr[:a]ger einstellbarer H[:o]he des Darlehens");
		
		return FXCollections.observableArrayList(zeile1, zeile2);
	}
	
	
	@FXML
	private void addToParamList(ActionEvent e) {
		//Formulareintrag soll der Liste hinzugefuegt werden
		ParametrisierungModel zeile3 = 
				new ParametrisierungModel("String","testBerechneGesamtschuld","Laufzeit","10","vom Wissenstr[:a]ger einstellbare Laufzeit des Darlehens");
	}
	
	@FXML
	private void saveParamList(ActionEvent e) {
		//Tabelle soll gespeichert werden 
		//es wird nach vorkommen gesucht und anwender gefragt ob ersetzt werden soll
		//und Fenster schlieﬂt sich
	}

	public String getKlassePfad() {
		return klassePfad;
	}

	public void setKlassePfad(String klassePfad) {
		this.klassePfad = klassePfad;
	}
}
