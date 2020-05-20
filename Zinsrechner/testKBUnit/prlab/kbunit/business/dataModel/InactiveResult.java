package prlab.kbunit.business.dataModel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Modellklasse fuer inaktive Klassen.
 * <br>
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class InactiveResult {
	private final  IntegerProperty id;
	private final  BooleanProperty containsXML;
	
	public InactiveResult(int id, boolean containsXML) {
		this.id = new SimpleIntegerProperty(id);
		this.containsXML = new SimpleBooleanProperty(containsXML);
	}

	public IntegerProperty idProperty() {
		return this.id;
	}
	

	public int getId() {
		return this.id.get();
	}
	

	public void setId(final int id) {
		this.id.set(id);
	}
	

	public BooleanProperty containsXMLProperty() {
		return this.containsXML;
	}
	

	public boolean isContainsXML() {
		return this.containsXML.get();
	}
	

	public void setContainsXML(final boolean containsXML) {
		this.containsXML.set(containsXML);
	}

}
