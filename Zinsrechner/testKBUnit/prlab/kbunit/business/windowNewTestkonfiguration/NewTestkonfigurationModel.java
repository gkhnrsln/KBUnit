package prlab.kbunit.business.windowNewTestkonfiguration;

import javafx.collections.ObservableList;
import prlab.kbunit.business.dataModel.ActiveResult;
import prlab.kbunit.business.dataModel.ActiveResultParameter;

/**
 * 
 * @author Patrick Pete
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 */
public class NewTestkonfigurationModel {
	
	private ActiveResult activeResult ;
	
	public NewTestkonfigurationModel(ActiveResult activeResult) {
		this.activeResult = activeResult;
	}
	
	public ObservableList<ActiveResultParameter> getParameters() {
		return activeResult.getParameters();
	}
	
	public void setParameters(ObservableList<ActiveResultParameter> para) {
		this.activeResult.setParameters(para);
	}
	
	/**
	 * @return the active
	 */
	public ActiveResult getActiveResult() {
		return activeResult;
	}
	/**
	 * @param active the active to set
	 */
	public void setActiveResult(ActiveResult active) {
		this.activeResult = active;
	}
	
}
