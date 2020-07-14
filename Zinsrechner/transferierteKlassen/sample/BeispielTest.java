package sample;

import org.junit.jupiter.api.Test;

public class BeispielTest {
	/** sad */
	public static int testMethode2_Zahl = 5;
	/** sad */
	public static String testMethode1_Name = "Musterstring";
	/** sad */
	public static String testMethode3_Nameasd = "ABC";
	@Test
	void testMethode1() {
		System.out.println(testMethode1_Name);
		System.out.println(5);
		String s = testMethode1_Name;
		String Musterstring = "kk";
		System.out.println(testMethode1_Name);
		System.out.println("Musterstrin");
		System.out.println(testMethode1_Name + testMethode1_Name);
	}
	
	@Test
	void testMethode2() {
		int i = testMethode2_Zahl;
		System.out.println(testMethode2_Zahl);
		System.out.println(i);
	}
	
	@Test
	void testMethode3() {
		System.out.println(testMethode3_Nameasd + testMethode3_Nameasd);
		System.out.println(5);
	}
	
	void testMethode4657() {
		
	}
}
