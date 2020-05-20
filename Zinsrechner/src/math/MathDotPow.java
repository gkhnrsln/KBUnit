package math;

import java.util.Collections;

public class MathDotPow {
	
	public double raiseToAPower(double base, int power){
	    return Collections.nCopies(power, base)
	        .stream()
	        .reduce(1.0, (partialResult, nextElement) -> partialResult * nextElement);
	}

}
