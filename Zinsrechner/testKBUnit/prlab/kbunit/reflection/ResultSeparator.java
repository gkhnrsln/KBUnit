package prlab.kbunit.reflection;

import junit.framework.AssertionFailedError;
import junit.framework.TestResult;
import org.junit.Assert;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

/**
 * The {@code ResultSeparator} class provides method
 * <br>that transforms {@link Result} to {@link TestResult}<br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 * 
 */
public class ResultSeparator {
	
	/**
	 * Transforms given {@link Result} in {@link TestResult}. 
	 * <br>{@code Result} has the method {@link Result#getFailures()}, 
	 * which holds {@link Failure}s caused either by 
	 * <br>{@link AssertionError} or sub-classes of {@link Exception}. 
	 * {@code AssertionError} qualifies as {@code Failure} 
	 * <br>thrown by the methods of class {@link Assert} and therefore is arranged in 
	 * <br>{@link TestResult#addFailure(junit.framework.Test, AssertionFailedError)}.
	 * <br>The rest of the {@code Exception}s which cause a {@code TestCase}
	 * to fail due to error are arranged in
	 * <br>{@link TestResult#addError(junit.framework.Test, Throwable)}.
	 * @param result as a {@link Result}
	 * @return an {@code Object} of {@link TestResult}
	 * 
	 * @author Alexander Georgiev
	 */
	public static TestResult getTestResult(Result result) {

		TestResult    testResult  = new TestResult();
		
		for(Failure failure : result.getFailures()) {
		
			if(failure.getException() instanceof AssertionError) {	
				testResult.addFailure(null, new AssertionFailedError(
					failure.getException().getMessage()));
			} else {
				testResult.addError(null, failure.getException());
			}
		}
		
		return testResult;
	}

	// konvertiert ein vorliegendes TestExecutionSummary-Objekt in ein TestResult - Objekt
	public static TestResult testExecutionSummaryToTestResult(TestExecutionSummary result)
	{
		List<TestExecutionSummary.Failure> failureList = result.getFailures();
		long failureCount = result.getTestsFailedCount();
		TestExecutionSummary.Failure failure = null;
		TestResult testResult = new TestResult();
		for(int i = 0; i < failureCount; i++)
		{
			failure = failureList.get(i);
			if(failure.getException() instanceof AssertionError)
			{
				testResult.addFailure(null, new AssertionFailedError(
						failure.getException().getMessage()));
			}
			else
			{
				testResult.addError(null, failure.getException());
			}
		}
		return testResult;
	}

}
