import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

public class LOESCHEMICH {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String value = "WELT";
		String value2 = "**********";
		String s = "1 " + value + " 2 " + value + " 3 " + value + "3";
		StringBuilder sb = new StringBuilder(s);
		
		System.out.println(s);
		
		int index;
		int temp = 0;
		
		for (int i = 0; i< StringUtils.countMatches(s, value); i++) {
			index = ordinalIndexOf(sb.toString(), value, temp);

			String eingabe = JOptionPane.showInputDialog(null,"Gib");
			if (eingabe.equals("y")) {
				sb.replace(index, index + value.length(), value2);
				temp = 0;
			} else {
				temp++;
			}
			System.out.println(sb);
			
		}
	}
	


	public static int ordinalIndexOf(String str, String substr, int n) {
	    int pos = -1;
	    do {
	        pos = str.indexOf(substr, pos + 1);
	    } while (n-- > 0 && pos != -1);
	    return pos;
	}
	public static String replaceChar(String str, String ch, int index) {
	    return str.substring(0, index) + ch  + str.substring(index+ ch.length());
	}
}