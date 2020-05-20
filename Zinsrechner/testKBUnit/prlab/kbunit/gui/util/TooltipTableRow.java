package prlab.kbunit.gui.util;

import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;

import java.util.function.Function;

/**
 * Hilfsklasse zum manipulieren der TableView (JavaFX)
 * <br>
 * &copy; 2018 Patrick Pete, Ursula Oesing  <br>
 *
 * @author Patrick Pete
 */

/**
 * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * @author Patrick Pete
 */
public class TooltipTableRow<T> extends TableRow<T> {
	private Function<T, String> toolTipStringFunction;

	  public TooltipTableRow(Function<T, String> toolTipStringFunction) {
	    this.toolTipStringFunction = toolTipStringFunction;
	  }

	  @Override
	  protected void updateItem(T item, boolean empty) {
	    super.updateItem(item, empty);
	    if(item == null) {
	        setTooltip(null);
	    } else {
	        Tooltip tooltip = new Tooltip(toolTipStringFunction.apply(item));
	        setTooltip(tooltip);
	    }
	  }
}
