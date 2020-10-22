package prlab.kbunit.gui.windowMainFrame;

import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import junit.framework.TestResult;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import prlab.kbunit.Main;
import prlab.kbunit.business.ctci.CTCIFileModel;
import prlab.kbunit.business.dataModel.ActiveResult;
import prlab.kbunit.business.dataModel.ActiveResultParameter;
import prlab.kbunit.business.dataModel.InactiveResult;
import prlab.kbunit.business.run.Run;
import prlab.kbunit.business.testClassInfo.TestClassInfo;
import prlab.kbunit.business.windowMainFrame.MainFrameModel;
import prlab.kbunit.enums.Selection;
import prlab.kbunit.enums.Variables;
import prlab.kbunit.gui.util.TooltipTableRow;
import prlab.kbunit.gui.windowNewTestkonfiguration.NewTestkonfigurationController;
import prlab.kbunit.gui.windowParametrisierung.ParametrisierungController;
import prlab.kbunit.test.TestParameterInfo;
import prlab.kbunit.test.TestResultInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controller-Klasse des Hauptfensters
 * <br>
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class MainFrameController implements Initializable {
	// HostService zum oeffnen einer Datei
	private HostServices hostServices ;

	// ObservableList fuer die aktive Testfaelle
	private FilteredList<ActiveResult> activeList;

	// Tabelle Inaktive Testkonfigurationen
	@FXML
	private TableView<InactiveResult> inactiveResultTable;
	@FXML
	private TableColumn<InactiveResult, Integer> inactiveIdColumn;
	@FXML
	private Button deleteInactiveButton;

	// Tabelle Aktive Testkonfigurationen
	@FXML
	private TableView<ActiveResult> activeResultTable;
	@FXML
	private TableColumn<ActiveResult, Integer> idColumn;
	@FXML
	private TableColumn<ActiveResult, String> dateColumn;
	@FXML
	private TableColumn<ActiveResult, String> pathColumn;
	@FXML
	private TableColumn<ActiveResult, Integer> successColumn;
	@FXML
	private TableColumn<ActiveResult, String> messageColumn;
	@FXML
	private TableColumn<ActiveResult, String> exceptionExpectedColumn;
	@FXML
	private Button deleteActiveButton;

	// Combobox mit allen Testklassen
	@FXML
	private ComboBox<File> javafileComboBox;
	@FXML 
	private Button javafileButton;

	@FXML
	private ComboBox<File> javafilePlainComboBox;
	@FXML 
	private Button javafilePlainButton;
	
	// Generate CTCI
	@FXML
	private Button startGenerateCTCIButton;
	@FXML
	private Button startGenerateCTCIButtonAllTestclasses;
	
	// Logger
	@FXML
	private ComboBox<Selection> selectionComboBox; 
	@FXML
	private Button startLoggerButton;
	@FXML
	private Button startLoggerButtonAllTestclasses;

	// Runner
	@FXML
	private Button startRunnerButton;
	@FXML
	private Button startRunnerButtonAllTestclasses;

	// Bedienung - Neue Testkonfiguration
	@FXML
	private ComboBox<String> newTestcaseComboBox;
	@FXML
	private Button newTestcaseButton;
    
	private MainFrameModel mainFrameModel;
	
	private CTCIFileModel ctciFileModel;

	/**
	 * Konstruktor MainFrameControll
	 */
	public MainFrameController() {
		//this.configurationModel = ConfigurationModel.getInstance();
		this.mainFrameModel = MainFrameModel.getInstance();
	}

	//---------------------Zugriffe auf das BasicFrameModel------------------//
	public MainFrameModel getMainFrameModel() {
		return this.mainFrameModel;
	}

	/**
	 * Initialisierung der Nodes
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		idColumn.setCellValueFactory(cellData 
			-> cellData.getValue().idProperty().asObject());
		dateColumn.setCellValueFactory(cellData 
			-> cellData.getValue().dateTimeProperty());
		pathColumn.setCellValueFactory(cellData 
			-> cellData.getValue().pathProperty());
		successColumn.setCellValueFactory(cellData 
			-> cellData.getValue().succcessProperty().asObject());
		messageColumn.setCellValueFactory(cellData 
			-> cellData.getValue().messageProperty());
		exceptionExpectedColumn.setCellValueFactory(cellData 
			-> cellData.getValue().exceptionExpectedProperty().asObject().asString());

		inactiveIdColumn.setCellValueFactory(cellData 
			-> cellData.getValue().idProperty().asObject());
		// Referenz der ObservableList des MainFrameModel auf die filteredList
		activeList = new FilteredList<>(mainFrameModel.getActiveResults());

		//***************fill Selection ComboBox************************************//
		selectionComboBox.setItems(mainFrameModel.getSelection());
		selectionComboBox.getSelectionModel().select(0);

		try {
			javafileComboBox.setItems(mainFrameModel
				.scanSourceFolder(Variables.TEST_SOURCE, Variables.EXTENSION_TEST_JAVA));
			javafilePlainComboBox.setItems(mainFrameModel
				.scanSourceFolder(Variables.TEST_PLAIN_SOURCE,
						Variables.EXTENSION_TEST_PLAIN_JAVA));
			//falls Testklassen vorhanden, sind Buttons aktiv
			if (mainFrameModel.scanSourceFolder(Variables.TEST_SOURCE,
					Variables.EXTENSION_TEST_JAVA) != null) {
				startGenerateCTCIButtonAllTestclasses.setDisable(false);
				startLoggerButtonAllTestclasses.setDisable(false);
				startRunnerButtonAllTestclasses.setDisable(false);
			}
		} catch (IOException e1) {
			e1.printStackTrace();
			showMessage(AlertType.WARNING, "Fehler!", 
					"Fehler beim Laden der Testklassen", e1.getMessage());
		}

		javafileComboBox.getSelectionModel().select(-1); //obersten Eintrag ist Leer
		javafilePlainComboBox.getSelectionModel().select(-1);
		activeResultTable.setItems(activeList);
		inactiveResultTable.setItems(mainFrameModel.getInactiveResults());

		/// Markieren der Testfaelle mit JUnit Files & Erstellen eines Tooltips
		PseudoClass nojunitfiles = PseudoClass.getPseudoClass("nojunitfiles");
		activeResultTable.setRowFactory((row) -> {
			TooltipTableRow<ActiveResult> newrow 
			    = new TooltipTableRow<ActiveResult> ((ActiveResult ar) ->  {
				return getToolTip(ar);
			});

			// 
			newrow.itemProperty().addListener(new ChangeListener<ActiveResult>() {
				@Override
				public void changed(ObservableValue<? extends ActiveResult> observable, 
					ActiveResult oldValue,
					ActiveResult newValue) {
					newrow.pseudoClassStateChanged(nojunitfiles, false);
					try {
						newrow.pseudoClassStateChanged(nojunitfiles, 
							!newValue.getTestFile());
					} 
					catch (Exception e) {
					}
				}});
			return newrow;
		});

		// Markieren der inaktiven Testfaelle, die jedoch in der Testklassen-XML als 
		// aktiv gelistet sind
		PseudoClass active = PseudoClass.getPseudoClass("active");

		inactiveResultTable.setRowFactory((row) -> {
			TableRow<InactiveResult> newrow = new TableRow<InactiveResult>();
			newrow.itemProperty().addListener(new ChangeListener<InactiveResult>() {
				@Override
				public void changed(ObservableValue<? extends InactiveResult> observable, 
					InactiveResult oldValue,
					InactiveResult newValue) {
					newrow.pseudoClassStateChanged(active, false);
					try {
						newrow.pseudoClassStateChanged(active, 
							!newValue.isContainsXML());
					} 
					catch (Exception e) {
					}

				}});
			return newrow;
		});
	}

	@FXML
	private void handleNewResults() {
		boolean initialOk = true;
		try {
			initialOk = checkInitialtestkonfiguration();
		}
		catch(Exception exc) {
			initialOk = false;			
		}
		if(! initialOk) {
			showMessage(AlertType.INFORMATION, "Information", 
				"Information zum Anlegen von Testkonfigurationen", 
				"Es ist keine Initialtestkonfiguration vorhanden."
				+ "\nDaher kann keine weitere Testkonfiguration angelegt werden.");
		}
		else {
			ObservableList<ActiveResultParameter> parameters = mainFrameModel.getOpenedTestClass()
				.getParameters(newTestcaseComboBox.getSelectionModel().getSelectedItem());
			ActiveResult newResult = new ActiveResult(
				mainFrameModel.getOpenedTestClass().getPackageName()
				    + "." + mainFrameModel.getOpenedTestClass().getClassName()
				    + "." + newTestcaseComboBox.getSelectionModel().getSelectedItem(),
				parameters);
			try {
				newResult = showNewResultDialog(newResult);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
	
			if(newResult.getParameters() != null) {
				TestResultInfo tri = createTestResultInfo(newResult);
				Run runner;
				runner = new Run(javafileComboBox.getSelectionModel().getSelectedItem());
				int testSuccess;
				String testMessage;
				if(mainFrameModel.getOpenedTestClass().getTesttype() != TestClassInfo.TESTTYPE_JUNIT_5){
					TestResult testResultJunit = runner.runJUnit4Test(javafileComboBox.getSelectionModel()
						.getSelectedItem(), tri);
					if (testResultJunit.errorCount() > 0){
						testSuccess = TestResultInfo.TESTABORTED;
						testMessage = testResultJunit.errors()
							.nextElement().thrownException().getClass().getCanonicalName()
							+ ": " +  testResultJunit.errors().nextElement().exceptionMessage();
					}
					else if(testResultJunit.failureCount() > 0)	{
						testSuccess = TestResultInfo.TESTFAILED;
						testMessage = testResultJunit.failures().nextElement().exceptionMessage();
					}
					else {
						testSuccess = TestResultInfo.TESTPASSED;
						testMessage = "";
					}
				}
				else{
					TestExecutionSummary testExecutionSummary = runner.runJUnit5Test(javafileComboBox.getSelectionModel()
						.getSelectedItem(), tri);
					if(testExecutionSummary.getTestsFailedCount() > 0
						&& (testExecutionSummary.getFailures().stream().anyMatch(failure ->
						failure.getException().getClass()
							== org.opentest4j.AssertionFailedError.class))){
						List<TestExecutionSummary.Failure> filteredList = testExecutionSummary.getFailures().stream()
							.filter(failure -> failure.getException().getClass() ==
							org.opentest4j.AssertionFailedError.class)
							.collect(Collectors.toList());
						testSuccess = TestResultInfo.TESTFAILED;
						testMessage = filteredList
							.get(0).getException().getMessage();
					}
					else if(testExecutionSummary.getTestsFailedCount() > 0
							&& (testExecutionSummary.getFailures().stream().anyMatch(failure ->
							failure.getException().getClass()
									!= org.opentest4j.AssertionFailedError.class))){
						List<TestExecutionSummary.Failure> filteredList = testExecutionSummary.getFailures().stream()
							.filter(failure -> failure.getException().getClass()
								!= org.opentest4j.AssertionFailedError.class)
							.collect(Collectors.toList());
						testSuccess = TestResultInfo.TESTABORTED;
						testMessage = filteredList
								.get(0).getException().getMessage();
					}
					else if (testExecutionSummary.getTestsAbortedCount() > 0){
						testSuccess = TestResultInfo.TESTABORTED_BY_ASSUMPTION;
						testMessage = "TestAbortedException wurde geworfen";
					}
					else if(testExecutionSummary.getTestsSkippedCount() > 0
						|| testExecutionSummary.getContainersSkippedCount() > 0)
					{
						testSuccess = TestResultInfo.TESTSKIPPED;
						testMessage = "Test wurde übersprungen";
					}
					else if(testExecutionSummary.getTestsSucceededCount() > 0)
					{
						testSuccess = TestResultInfo.TESTPASSED;
						testMessage = "Test ist erfolgreich.";
					}
					else {
						testSuccess = TestResultInfo.TESTFAILED;
						testMessage = testExecutionSummary.getFailures()
							.get(0).getException().getMessage();
					}
				}
				tri.setSuccess(testSuccess);
				// Die Laenge der Message anpassen, falls sie fuer das Feld in der Tabelle zu lang ist:
				testMessage = testMessage.length() > 250 ? testMessage.substring(0, 247) + "..." : testMessage;
				tri.setMessage(testMessage);
				newResult.setDateTime(tri.getDate().toString());
				newResult.setMessage(testMessage);
				newResult.setPath(tri.getPath());
				newResult.setSuccess(testSuccess);
				newResult.setTestFile(false);
	
				try {
					int newID = mainFrameModel.getOpenedTestClass().createTestcase(tri);
					newResult.setId(newID);
					mainFrameModel.getActiveResults().add(newResult);
					// CONSOLE OUTPUT
					System.out.println();
					System.out.println("Created new testcase:");
					System.out.println(
						"**********************************************************************************");
					System.out.println("ID_"+newID);
					System.out.println();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				System.out.println("Das Anlegen einer Testkonfiguration wurde abgebrochen");
			}
		}	
	}  
	
	private boolean checkInitialtestkonfiguration() throws IOException {
	    boolean result = false;	
	    ObservableList<ActiveResult> activeResults = this.mainFrameModel.getActiveResults();
	    if(! activeResults.isEmpty()) {
	    	int i = 0;
	      	while(i < activeResults.size() && ! result) {
	       	    int laenge = javafileComboBox.getValue().toString().length();
	      		String help = javafileComboBox.getValue().toString().substring(5, laenge-5)
	      			.replace('\\', '.') + ".";
	       		if(activeResults.get(i++).getPath().equals(help + newTestcaseComboBox.getValue())){
	    			result = true;
	    		}
	      	}
	    }
		return result;
	}	

	private ActiveResult showNewResultDialog(ActiveResult activeResult) 
		throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(
			"/prlab/kbunit/resources/view/NewTestkonfigurationScene.fxml"));
		AnchorPane resultsDialog = (AnchorPane) loader.load();
		Stage dialogStage = new Stage();
		dialogStage.setResizable(true);
		dialogStage.setTitle("Neue Testkonfiguration");
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		Scene scene = new Scene(resultsDialog);
		dialogStage.setScene(scene);
		NewTestkonfigurationController newResultController = loader.getController();
		newResultController.setDialogStage(dialogStage);
		newResultController.initModel(activeResult);
		dialogStage.showAndWait();
		return newResultController.getActiveResult();
	}

	private TestResultInfo createTestResultInfo(ActiveResult result) {
		TestResultInfo tri = new TestResultInfo();
		tri.setId(0);
		tri.setPath(result.getPath());
		tri.setExceptionExpected(result.getExceptionExpected());
		tri.setMessage(result.getMessage());
		tri.setSuccess(result.getSuccess());
		Class<?> testclass = null;
		try {
			ArrayList<String> argumentTypes = new ArrayList<>();
			testclass = Class.forName(tri.getPackageName() + "." + tri.getClassName());
			Method[] methods = testclass.getDeclaredMethods();
			for(Method m : methods) {
				if (m.getName().equals(tri.getIdentifierName().substring(
					tri.getIdentifierName().lastIndexOf('.') + 1))) {
					Class<?>[] methodArgumentsTypes = m.getParameterTypes();
					for (Class<?> clazz : methodArgumentsTypes) {
						argumentTypes.add(clazz.getTypeName());
					}
				}
			}
			tri.setArgumentsTypes(argumentTypes);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		ArrayList<TestParameterInfo> params = new ArrayList<TestParameterInfo>();	
		for(ActiveResultParameter parameter : result.getParameters()) {
			params.add(new TestParameterInfo(parameter.getName(), parameter.getValue()));
		}
		tri.setParameters(params);
		return tri;
	}
	
	/**
	 * method for the "javaFilePlain" Combobox.
	 * @param event
	 */
	@FXML
	private void javafilePlainChoose(ActionEvent event) {
		if (javafilePlainComboBox.getSelectionModel().selectedItemProperty() != null) {
			javafilePlainButton.setDisable(false);
		}
	}
	
	/**
	 * method for the "javaFile" Combobox.
	 * @param event
	 */
	@FXML
	private void javafileChoose(ActionEvent event) {
		if (javafileComboBox.getSelectionModel().selectedItemProperty() != null) {
			startGenerateCTCIButton.setDisable(false);
			if (mainFrameModel.getCtciFile().exists()) javafileButton.setDisable(false);
		}
	}
	

	/**
	 * method for the "parameterize" button.
	 * @param event
	 * @throws IOException 
	 */
	@FXML
	private void parameterizeTestclass(ActionEvent event)  {
		//Maske ParametrisierungScene.fxml oeffnen
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Main.class.getResource(
			"/prlab/kbunit/resources/view/ParametrisierungScene.fxml"));
		
		AnchorPane resultsDialog;
		Stage dialogStage = new Stage();
		dialogStage.setResizable(false);
		dialogStage.setTitle("Parameter eingeben");
		dialogStage.initModality(Modality.APPLICATION_MODAL);
		try {
			resultsDialog = (AnchorPane) loader.load();
			Scene scene = new Scene(resultsDialog);
			dialogStage.setScene(scene);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ParametrisierungController parametrisierungController = loader.getController();
		parametrisierungController.setKlassePfad(javafilePlainComboBox.getSelectionModel().getSelectedItem().toString());
		parametrisierungController.initModel();
		dialogStage.showAndWait();
	}
	
	/**
	 * method for the "generate CustomerTestCaseInformation" button.
	 * @param event
	 */
	@FXML
	private void generateCTCI(ActionEvent event)  {
		Alert alert;
		//bestaetigung fuer das ersetzen der XML Datei
		Boolean isPermitted = true;
		
		if (mainFrameModel.getCtciFile().exists()) {
			alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Bestätigung");
			alert.setHeaderText("Die bestehende CustomerTestCaseInformation.xml wird überschrieben.");
			alert.setContentText("Sind Sie damit einverstanden?");
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() != ButtonType.OK){
				isPermitted = false;
			}
		}
		
		if (isPermitted) {
			try {
				this.ctciFileModel = CTCIFileModel.getInstance();
				ctciFileModel.createFile(javafileComboBox.getSelectionModel().getSelectedIndex());
				
				if (ctciFileModel.getLiMissDesc().isEmpty()) {
					showMessage(AlertType.INFORMATION, "Information",
							"Die CustomerTestCaseInformation.xml wurde erfolgreich generiert.", null);
				} else if (!ctciFileModel.getLiMissDesc().isEmpty()) {
					alert = new Alert(AlertType.WARNING);
					alert.setTitle("Problem!");
					alert.setHeaderText("Die CustomerTestCaseInformation.xml wurde generiert.");
					alert.setContentText("Es fehlt eine Beschreibung zu folgenden Methoden oder Attributen:");
					
					String temp = "";
					for (String s : ctciFileModel.getLiMissDesc()) {
						temp += s + "\n";
					}
					
					TextArea textArea = new TextArea(temp);
					textArea.setEditable(false);
					textArea.setWrapText(true);
					
					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);
					
					GridPane expContent = new GridPane();
					expContent.setMaxWidth(Double.MAX_VALUE);
					expContent.add(textArea, 0, 0);
					
					// Set expandable Exception into the dialog pane.
					alert.getDialogPane().setExpandableContent(expContent);
					alert.showAndWait();
				}
			} catch(ClassNotFoundException | IOException ex) {
				showMessage(AlertType.ERROR, "Information",
						"Die CustomerTestCaseInformation.xml konnte nicht generiert werden.", null);
			}
		}
		
	}
	
	/**
	 * method for the "generate All CustomerTestCaseInformation" button.
	 */
	@FXML
	private void generateCTCIAllTestclasses(ActionEvent event) {
		Alert alert;
		//bestaetigung fuer das ersetzen der XML Datei
		Boolean isPermitted = true;
		
		if (mainFrameModel.getCtciFile().exists()) {
			alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Bestätigung");
			alert.setHeaderText("Die bestehende CustomerTestCaseInformation.xml wird überschrieben.");
			alert.setContentText("Sind Sie damit einverstanden?");
			
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() != ButtonType.OK){
				isPermitted = false;
			}
		}
		
		if (isPermitted) {
			try {
				this.ctciFileModel = CTCIFileModel.getInstance();
				ctciFileModel.createFile();
				if (ctciFileModel.getLiMissDesc().isEmpty()) {
					showMessage(AlertType.INFORMATION, "Information",
							"Die CustomerTestCaseInformation.xml wurde erfolgreich generiert.", null);
				} else if (!ctciFileModel.getLiMissDesc().isEmpty()) {
					alert = new Alert(AlertType.WARNING);
					alert.setTitle("Problem!");
					alert.setHeaderText("Die CustomerTestCaseInformation.xml wurde generiert.");
					alert.setContentText("Es fehlt eine Beschreibung zu folgenden Methoden oder Attributen:");
					
					String temp = "";
					for (String s : ctciFileModel.getLiMissDesc()) {
						temp += s + "\n";
					}
					
					TextArea textArea = new TextArea(temp);
					textArea.setEditable(false);
					textArea.setWrapText(true);
					
					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);
					
					GridPane expContent = new GridPane();
					expContent.setMaxWidth(Double.MAX_VALUE);
					expContent.add(textArea, 0, 0);
					
					// Set expandable Exception into the dialog pane.
					alert.getDialogPane().setExpandableContent(expContent);
					alert.showAndWait();
				}
			} catch(ClassNotFoundException | IOException ex) {
				showMessage(AlertType.ERROR, "Information",
						"Die CustomerTestCaseInformation.xml konnte nicht generiert werden.", null);
			}
		}
		
	}
	
	/**
	 * method for the delete button.
	 */
	@FXML
	private void handleDeleteId(ActionEvent event) {
		if(activeResultTable.getSelectionModel().getSelectedIndex() >= 0) {
			int selectedID = activeResultTable.getSelectionModel()
				.getSelectedItem().getId();
			// Loeschen in der Datenbank sowie in der ActiveResult
			boolean ok = true;
			try {
			    mainFrameModel.deleteTestcase(selectedID);
			}
			catch (Exception exc) {
				showMessage(AlertType.WARNING, "Problem!", 
					"Fehler beim Löschen des der Testkonfiguration!", exc.getMessage());
				ok = false;
			}
			if(ok) {				
				System.out.println();
				System.out.println("Deleted test case:");
				System.out.println(
					"**********************************************************************************");
				System.out.println("ID_"+selectedID);
				System.out.println();
	
				Run runner2 = new Run(javafileComboBox.getValue());
				runner2.reRun(selectedID, 
					new File[] {javafileComboBox.getSelectionModel().getSelectedItem()});
			}
		}
		else {
			showMessage(AlertType.WARNING, "Problem!", 
				"Keine Testkonfiguration ausgewählt!", "Bitte wählen Sie eine Testkonfiguration aus!");
		}
	}

	@FXML
	private void handleDeleteInactiveId(ActionEvent event)  {
		try {
			if(inactiveResultTable.getSelectionModel().isEmpty()){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Alert!");
				alert.setHeaderText("Keine inaktive ID ausgewählt");
				alert.showAndWait();
			} 
			else {
				int selectedInactiveIndex = inactiveResultTable.getSelectionModel()
					.getSelectedIndex();
				int selectedInactiveId = inactiveResultTable.getSelectionModel()
					.getSelectedItem().getId();
				inactiveResultTable.getItems().remove(selectedInactiveIndex);
				Run runner = new Run(javafileComboBox.getValue());
				runner.reRun(selectedInactiveId, 
					new File[] {javafileComboBox.getSelectionModel().getSelectedItem()});
			}
		} catch (Exception e) {
			showMessage(AlertType.WARNING, "Warnung", 
				"Keine ID ausgewählt!", e.getMessage());
		}
	}
	
	private void showMessage(AlertType alertType, String title, 
		String header, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}

	/**
	 *  Start vom Logger fuer die geoeffnete Testklasse
	 */
	@FXML
	private void runLogger(ActionEvent event)  {
		Selection selection = selectionComboBox.getSelectionModel().getSelectedItem();
	    Run run = new Run(javafileComboBox.getValue());
	    run.startLogger(selection);
		showMessage(AlertType.INFORMATION, "Information", 
			"Das Logging ist beendet!", 
		    "Eine Log-Datei wurde erstellt. Beachten Sie die Konsolenausgabe.");
	}

	/**
	 *  Start vom Logger fuer alle Testklassen aus dem source folder test
	 */
	@FXML
	private void runLoggerAllTestclasses(ActionEvent event)  {
		Selection selection = selectionComboBox.getSelectionModel().getSelectedItem();
		ObservableList<File> files = javafileComboBox.getItems();
		Run run = new Run(files);
		run.startLogger(selection);
		showMessage(AlertType.INFORMATION, "Information",
			"Das Logging ist beendet!",
		    "Log-Dateien wurden erstellt. Beachten Sie die Konsolenausgabe.");
	}

	/**
	 * Start vom Runner fuer die geoeffnete Testklasse
	 */
	@FXML
	private void runRunner(ActionEvent event) {
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Start des Runners");
			alert.setHeaderText("Runner wirklich starten?");
			Optional<ButtonType> result = alert.showAndWait();
			if(result.get() == ButtonType.OK) {
				Run run = new Run(javafileComboBox.getValue());
				run.startRunner();
				mainFrameModel.loadData(javafileComboBox.getValue());
			} 
			showMessage(AlertType.INFORMATION, "Information", 
				"Der Runner ist beendet!", 
				"Beachten Sie die Konsolenausgabe.");
		}
		catch(Exception exc) {
			System.out.println(
				"CustomerTestcaseInformation.xml konnte nicht geöffnet werden.");
		}
	}
		
	
	/**
	 * Start vom Runner fuer alle Testklassen
	 */
	@FXML
	private void runRunnerAllTestclasses(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Start des Runners");
		alert.setHeaderText("Runner wirklich starten?");
		Optional<ButtonType> result = alert.showAndWait();
		if(result.get() == ButtonType.OK) {
			Run runner = null;
			ObservableList<File> files = javafileComboBox.getItems();
			if(files.size() != 0) {
				runner = new Run(files.get(0));
				runner.startRunner();
			}
			for(int i = 1; i < files.size(); i++) {
				runner = new Run(files.get(i), true);
			    runner.startRunner();
			}    
		} 
		showMessage(AlertType.INFORMATION, "Information", 
			"Der Runner ist beendet!", 
			"Beachten Sie die Konsolenausgabe.");
	}
	
	/**
	 * Oeffne die Testklasse
	 * @param event
	 * @throws Exception 
	 */
	@FXML
	private void scan(ActionEvent event)  {
        try {
			//Laden der Testklasse
			mainFrameModel.loadData(javafileComboBox.getValue());
	        if(mainFrameModel.getOpenedTestClass() != null) {
				//************ FILL NEW TESTCASE COMBO BOX*********//
				newTestcaseComboBox.setItems(mainFrameModel.getOpenedTestClass()
					.getIdentifierName());
				newTestcaseComboBox.getSelectionModel().select(0);
		   		startLoggerButton.setDisable(false);
				startRunnerButton.setDisable(false);
				deleteActiveButton.setDisable(false);
				deleteInactiveButton.setDisable(false);
				newTestcaseButton.setDisable(false);
		
				// Erneutes Laden der Testfaelle durch die Inaktiven Testfaelle 
			mainFrameModel.loadData(javafileComboBox.getValue());
	        }
	        else {
	    		showMessage(AlertType.INFORMATION, "KBUnit_Entwickler", 
	    			"Information zur Testklasse", 
	    			"Es wurden keine Daten zur Testklasse gefunden.");
	        }
        }
		catch(Exception exc) {
			System.out.println(
				"CustomerTestCaseInformation.xml konnte nicht geöffnet werden.");
		}
	}

	@FXML
	private void handleExit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	private void handleDocu(ActionEvent event) {
		File file=new File(getClass().getResource(
			"/prlab/kbunit/resources/doku/Anwendungsdokumentation_KBUnit.pdf")
			.getFile());
		HostServices hostServices = this.hostServices;
		hostServices.showDocument(file.getAbsolutePath());
	}

	@FXML
	private void handleAbout(){		
		String content = "\u00A9 2020 Alexander Georgiev, Patrick Pete, Ursula Oesing, Yannis Herbig \n"
	        + "This program comes with ABSOLUTELY NO WARRANTY. \n"
	        + "This is free software, and you are welcome to redistribute it under "
	        + "certain conditions; see http://www.gnu.org/licenses/ for details.";
		showMessage(AlertType.INFORMATION, "KBUnit_Entwickler", 
			"Information zur Lizenz", content);
	}

	/**
	 * Generiert einen Tooltip
	 * @param result
	 * @return
	 */
	public String getToolTip(ActiveResult result) {
		String myTooltip = "";
		for(int i=0;i<result.getParameters().size();i++) {
			myTooltip = myTooltip+(result.getParameters().get(i).getName()
				.substring(result.getParameters().get(i).getName()
				.lastIndexOf('_') + 1)+": "
				+ result.getParameters().get(i).getValue()+",");
		}
		return myTooltip;
	}

	public void setHostServices(HostServices hostServices) {
		// TODO Auto-generated method stub
		this.hostServices = hostServices ;

	}
}
