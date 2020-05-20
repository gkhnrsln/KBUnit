package prlab.kbunit.business.dataModel;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Modellklasse fuer aktive Testfaelle
 *
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class ActiveResult {

	private  final IntegerProperty id;
	private  final StringProperty date;
	private  final StringProperty path;
	private  final IntegerProperty success;
	private  final StringProperty message;
	private  final BooleanProperty exceptionExpected;
	private  final BooleanProperty testFile;
	private ObservableList<ActiveResultParameter> parameters 
	    = FXCollections.observableArrayList();

	
	// Konstruktoren
	public ActiveResult(int id, String dateTime, String path, int success, 
		String message, boolean exceptionExpected,  boolean junitfile) {
		this.id = new SimpleIntegerProperty(id);
		this.date = new SimpleStringProperty(dateTime);
		this.path = new SimpleStringProperty(path);
		this.success = new SimpleIntegerProperty(success);
		this.message = new SimpleStringProperty(message);
		this.exceptionExpected = new SimpleBooleanProperty(exceptionExpected);
		this.testFile = new SimpleBooleanProperty(junitfile);
	}
	public ActiveResult(String dateTime, String path) {
		this.id = new SimpleIntegerProperty();
		this.date = new SimpleStringProperty(dateTime);
		this.path = new SimpleStringProperty(path);
		this.success = new SimpleIntegerProperty();
		this.message = new SimpleStringProperty();
		this.exceptionExpected = new SimpleBooleanProperty();
		this.testFile = new SimpleBooleanProperty();
	}

	public ActiveResult(String path, ObservableList<ActiveResultParameter> parameters2) {
		this.id = new SimpleIntegerProperty();
		this.date = new SimpleStringProperty();
		this.path = new SimpleStringProperty(path);
		this.success = new SimpleIntegerProperty();
		this.message = new SimpleStringProperty();
		this.exceptionExpected = new SimpleBooleanProperty();
		this.testFile = new SimpleBooleanProperty();
		this.parameters = parameters2;
		
	}
	
	
	/**
	 * Setter / Getter
	 */

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public IntegerProperty idProperty() {
		return id;
	}
	public StringProperty dateTimeProperty() {
		return date;
	}
	public void setDateTime(String ldt) {
		date.set(ldt);
	}
	public String  getDateTime() {
		return date.get();
	}
	/**
	 * @return the path
	 */
	public String getPath() {
		return path.get();
	}

	public void setPath(String path) {
		this.path.set(path);
	}
	
	public StringProperty pathProperty() {
		return path;
	}

	/**
	 * @return the succcess
	 */
	public int getSuccess() {
		return success.get();
	}

	public void setSuccess(int success) {
		this.success.set(success);
	}
	public IntegerProperty succcessProperty() {
		return success;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}
	public StringProperty messageProperty() {
		return message;
	}

	/**
	 * @return the exceptionExpected
	 */
	public boolean getExceptionExpected() {
		return exceptionExpected.get();
	}

	public void setExceptionExpected(boolean exceptionExpected) {
		this.exceptionExpected.set(exceptionExpected);
	}
	public BooleanProperty exceptionExpectedProperty() {
		return exceptionExpected;
	}
	
	public ObservableList<ActiveResultParameter> getParameters() 
	{
		return parameters;
	}
	
	/**
	 * setzt die Parameter der Testfallkonfiguration 
	 * @param parameters neuer Wert fuer die Parameter der Testfallkonfiguration
	 */
	public void setParameters(ObservableList<ActiveResultParameter> parameters) 
	{
		this.parameters = parameters;
	}
	
	/**
	 * fuegt der Testfallkonfiguration einen Parameter hinzu 
	 * @param name, Name des neuen Parameter fuer die Testfallkonfiguration
	 * @param value, Wert des neuen Parameter fuer die Testfallkonfiguration
	 */
	public void addTestParameter (String name, String value)
	{
		this.parameters.add(new ActiveResultParameter(name, value));
	}
	
	
	public BooleanProperty testFileProperty() {
		return this.testFile;
	}
	

	public boolean getTestFile() {
		return this.testFile.get();
	}
	

	public void setTestFile(boolean testFile) {
		this.testFile.set(testFile);
	}
}
