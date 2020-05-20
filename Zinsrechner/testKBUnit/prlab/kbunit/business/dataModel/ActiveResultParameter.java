package prlab.kbunit.business.dataModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Modell-Klasse fuer Parameter der Testfaelle
 * <br>
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class ActiveResultParameter {
	
	private final StringProperty name;
	private final StringProperty value;
	
	public ActiveResultParameter(String name, String value) {
		this.name = new SimpleStringProperty(name);
		this.value = new SimpleStringProperty(value);
	}

	/**
	 * @return the name
	 */
	public StringProperty nameProperty() {
		return name;
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public String getName() {
		return name.get();
	}

	/**
	 * @return the value
	 */
	public StringProperty valueProperty() {
		return value;
	}
	public void setValue(String value) {
		this.value.set(value);
	}
	public String getValue() {
		return value.get();
	}

}
