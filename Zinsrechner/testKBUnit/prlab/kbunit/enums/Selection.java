package prlab.kbunit.enums;

import java.util.ArrayList;

/**
 * The {@code Selection} class provides four constants, 
 * which are used in the static method:
 * <br>Main.loadLogger(java.io.File[], Selection)}.
 * <br>The user can choose one of the following options,
 * in order to filter <br>the test-cases to be stored in 
 * the .XML-LOG-file as required:
 * <br>
 * <br><b> FAILURE_ERROR</b> - every unsuccessful test-case will be copied
 * <br><b> SUCCESS</b> - only the successful test-cases will be copied
 * <br><b> FAILURE </b> - test-cases which failed due to {@link java.lang.AssertionError}
 * <br><b> ERROR</b> - test-cases which failed due to error or uncaught {@link java.lang.Exception}
 * <br><b> ABORTED</b> - test-cases which failed due to {@link org.opentest4j.TestAbortedException}
 * <br><b> SKIPPED</b> - only the skipped test-cases will be copied
 * <br><b> ALL  </b> - all test-cases will be copied<br>
 * 
  * &copy; 2018 Alexander Georgiev, Patrick Pete, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 *
 */
public enum Selection {
	ALL,
	SUCCESS,
	FAILURE,
	ERROR,
	FAILURE_ERROR,
	ABORTED_BY_ASSUMPTION,
	SKIPPED;

	public static ArrayList<Selection> allSelections() {
		Selection[] selections = values();
		ArrayList<Selection> names = new ArrayList<Selection>();
		for(Selection singleSelection : selections)
			names.add(singleSelection);
		return names;
	}
}
