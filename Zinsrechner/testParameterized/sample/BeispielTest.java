package sample;

import org.junit.jupiter.api.Test;

public class BeispielTest {
	/** c */
	public static String testMethode1_C = "c";
	/** c */
	public static String testMethode1_Cd = "Musterstring";
	@Test
	void testMethode1() {
		System.out.println(testMethode1_Cd);
		System.out.println(5);
		System.out.println(testMethode1_Cd);
		System.out.println("Musterstrin");
		System.out.println(testMethode1_Cd + testMethode1_Cd);
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
