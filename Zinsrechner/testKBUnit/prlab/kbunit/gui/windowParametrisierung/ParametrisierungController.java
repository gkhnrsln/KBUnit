package prlab.kbunit.gui.windowParametrisierung;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import prlab.kbunit.business.windowParametrisierung.*;

/**
 * @author G&ouml;khan Arslan
 */

public class ParametrisierungController implements Initializable {
	// Tabelle Testparameter
	@FXML
	private TableView<ParametrisierungModel> parameterTableView;
	@FXML
	private TableColumn<ParametrisierungModel, SimpleStringProperty> typColumn;
	@FXML
	private TableColumn<ParametrisierungModel, SimpleStringProperty> methodeColumn;
	@FXML
	private TableColumn<ParametrisierungModel, SimpleStringProperty> parameterColumn;
	@FXML
	private TableColumn<ParametrisierungModel, SimpleStringProperty> wertColumn;
	@FXML
	private TableColumn<ParametrisierungModel, SimpleStringProperty> descColumn;
	
	// Bedienung - Neue Zeile
	@FXML
	private ComboBox<String> typComboBox;
	@FXML
	private ComboBox<String> methodeComboBox;
	@FXML
	private TextArea tfDesc;
	@FXML
	private TextField txtMethode;
	@FXML
	private TextField txtWert;
	@FXML
	private Button saveButton;
	
	@FXML
	private Button addButton;
	
	private ParametrisierungModel parametrisierungModel;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Tabelle sollte eigentlich beim starten leer sein
		//dient nur zum testen
		//...
		typColumn.setCellValueFactory(new PropertyValueFactory<ParametrisierungModel, SimpleStringProperty>("typ")); //soll cmbbx
		methodeColumn.setCellValueFactory(new PropertyValueFactory<ParametrisierungModel, SimpleStringProperty>("methode"));
		parameterColumn.setCellValueFactory(new PropertyValueFactory<ParametrisierungModel, SimpleStringProperty>("parameter"));
		wertColumn.setCellValueFactory(new PropertyValueFactory<ParametrisierungModel, SimpleStringProperty>("wert"));
		descColumn.setCellValueFactory(new PropertyValueFactory<ParametrisierungModel, SimpleStringProperty>("desc"));
		//...
		
		parameterTableView.setItems(getParametrisierungModel());
		
		//***************fill Selection ComboBox************************************//
		typComboBox.setItems(parametrisierungModel.datentypen());
		methodeComboBox.setItems(parametrisierungModel.methoden());
		
		//button erst aktiv, wenn "Formular" ausgefuellt ist
		addButton.setDisable(true);
	}
	
	@FXML
	public ObservableList<ParametrisierungModel> getParametrisierungModel() {
		//die Parameter sollen von dem "Formular" uebernommen werden
		ParametrisierungModel zeile1 =
				new ParametrisierungModel("String","testBerechneGesamtschuld","User","Musterperson","vom Wissenstr[:a]ger einstellbarer Name des Benutzers des Darlehens");
		ParametrisierungModel zeile2 = 
				new ParametrisierungModel("String","testBerechneGesamtschuld","Darlehen","100000000","vom Wissenstr[:a]ger einstellbarer H[:o]he des Darlehens");
		
		ObservableList<ParametrisierungModel> liste = FXCollections.observableArrayList(zeile1, zeile2);
		
		return liste;
	}
	
	
	
}
