package prlab.kbunit.gui.windowParametrisierung;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import prlab.kbunit.business.dataModel.ActiveResult;
import prlab.kbunit.business.dataModel.ActiveResultParameter;


/**
 * @author Goekhan Arslan
 */

public class ParametrisierungController implements Initializable {
	private Stage dialogStage;
	
	@FXML
	private TableView<ActiveResultParameter> parameterTableView;
	@FXML
	private TableColumn<ActiveResultParameter, String> parameterColumn;
	@FXML
	private TableColumn<ActiveResultParameter, String> valueColumn;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		//parameterTableView.setItems();
	}
	
	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}
	
	public void initModel(ActiveResult activeResult) {		
		parameterColumn.setCellValueFactory(
			cellData -> cellData.getValue().nameProperty());
		valueColumn.setCellValueFactory(
			cellData -> cellData.getValue().valueProperty());
		valueColumn.setCellFactory(
			TextFieldTableCell.forTableColumn());

		parameterTableView.setEditable(true);		
		//setTableViewEditable();
	}

}
