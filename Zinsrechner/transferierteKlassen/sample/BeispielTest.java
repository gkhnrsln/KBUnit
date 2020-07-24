package sample;

import org.junit.jupiter.api.Test;

public class BeispielTest {
	/** wer */
	public static String testMethode1_Er = "werwe";
	/** wer */
	public static boolean testMethode1_Erf = true;
	@Test
	void testMethode1() {
		System.out.println("Musterstring");
		System.out.println(5);
		System.out.println("Musterstring");
		System.out.println("Musterstrin");
		System.out.println("Musterstring" + "Musterstring");
	}
	
	@Test
	void testMethode2() {
		int i = 5;
		System.out.println(5);
		System.out.println(i);
	}
	
	@Test
	void testMethode3() {
		System.out.println("ABC" + "ABC");
		System.out.println(5);
	}
	
	void testMethode4657() {
		
	}
}
