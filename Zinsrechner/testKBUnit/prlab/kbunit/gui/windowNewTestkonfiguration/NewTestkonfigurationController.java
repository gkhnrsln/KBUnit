package prlab.kbunit.gui.windowNewTestkonfiguration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import prlab.kbunit.business.dataModel.ActiveResult;
import prlab.kbunit.business.dataModel.ActiveResultParameter;
import prlab.kbunit.business.windowNewTestkonfiguration.NewTestkonfigurationModel;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class NewTestkonfigurationController implements Initializable {

	private Stage dialogStage;
	private NewTestkonfigurationModel nrdm;
	
	@FXML
	private TableView<ActiveResultParameter> parameterTableView;
	@FXML
	private TableColumn<ActiveResultParameter, String> parameterColumn;
	@FXML
	private TableColumn<ActiveResultParameter, String> valueColumn;
	@FXML
	private CheckBox expectedCheckbox;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
	}

	public void setDialogStage(Stage dialogStage){
		this.dialogStage = dialogStage;
	}


	public void initModel(ActiveResult activeResult) {
		if (this.nrdm != null) {
			throw new IllegalStateException(
				"Model can only be initialized once");
		}
		
		this.nrdm = new NewTestkonfigurationModel(activeResult);		
		parameterColumn.setCellValueFactory(
			cellData -> cellData.getValue().nameProperty());
		valueColumn.setCellValueFactory(
			cellData -> cellData.getValue().valueProperty());
		valueColumn.setCellFactory(
			TextFieldTableCell.forTableColumn());
		valueColumn.setOnEditCommit(
			event -> {
			    nrdm.getParameters().get(
				parameterTableView.getSelectionModel().getSelectedIndex())
			    .setValue(event.getNewValue());
		});
		dialogStage.setOnCloseRequest((WindowEvent event1) -> {
			nrdm.setParameters(null);
		});
		parameterTableView.setItems(nrdm.getParameters());
		parameterTableView.setEditable(true);		
		setTableViewEditable();
	}

	@FXML
	private void handleCreate(ActionEvent e) {
	    nrdm.getActiveResult().setExceptionExpected(expectedCheckbox.isSelected());
		dialogStage.close();
	}
	
	public ActiveResult getActiveResult(){
		return nrdm.getActiveResult();
	}
	
	private void setTableViewEditable() {
		valueColumn.setEditable(true);
		parameterTableView.getSelectionModel().cellSelectionEnabledProperty().set(true);
		parameterTableView.setOnKeyPressed(event -> {
			if(event.getCode().isLetterKey() || event.getCode().isDigitKey()) {
				editFocusedCell();
			} 
			else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB) {
				parameterTableView.getSelectionModel().selectNext();
				event.consume();
			} else if (event.getCode() == KeyCode.LEFT) {
				parameterTableView.getSelectionModel().selectPrevious();
				event.consume();
			}
		});
	}
	
	private void editFocusedCell() {
		@SuppressWarnings("unchecked")
		final TablePosition<ActiveResultParameter, ?> 
		    focusedCell = parameterTableView.focusModelProperty().get()
		        .focusedCellProperty().get();
		parameterTableView.edit(focusedCell.getRow(), focusedCell.getTableColumn());
	}
}
