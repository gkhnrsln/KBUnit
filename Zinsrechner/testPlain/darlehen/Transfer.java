package darlehen;

import java.lang.reflect.Method;

import prlab.kbunit.enums.Variables;

public class Transfer {

	public static void main(String[] args) {
		String klasse = Variables.TEST_PLAIN_SOURCE;
		try {
			
			//Class c = Class.forName("darlehen.DarlehenAnalyticsTest");
			Class c = Class.forName("darlehen.TilgungsdarlehenTestPlain");
			for (Method s1 : c.getDeclaredMethods())
				System.out.println(s1);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

}
