package prlab.kbunit.scan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code FolderScanner} class provides a static
 * method <br>for scanning of given (source-)folder 
 * and its sub-folder(s) <br>and allocating all of 
 * the files that match a specified filter.<br>
 * 
 * &copy; 2017 Alexander Georgiev, Ursula Oesing  <br>
 * 
 * @author Alexander Georgiev
 *
 */
public class FolderScanner{
	
    /**
     * scans the given file line by line
     * @param source a {@link File} where to search for .java class-files
     * @param list an empty ArrayList to write to
     * @param filter the desired file types (i.e. ".java", ".xml")
     * 
     * @return list of filtered class-files, allocated in current source-folder and
     * its sub-folders. <b>Fist element</b> of the list is at <b>position 0</b>. The list 
     * will be <b>empty</b> if the directory is empty or in case that the <b>source</b> 
     * does not denote a directory a <b>null</b> will be returned.
     * 
     */
    public static List<File> scanFolder(File source, 
        List<File> list, String filter) {
    	if(source.exists()) {
     		File file = new File(source.getPath());
        	File[] f  = file.listFiles();
        	if(f != null) {
        		for(int i=0; i<f.length; i++){
            		if(f[i].isDirectory()){
            			list.addAll(scanFolder(new File(f[i].getPath()), 
            				new ArrayList<File>(), filter));
            		}
            		else if(f[i].getName().endsWith(filter)){
              			list.add(f[i]);
            		}
            	}
        	}
        	return list;
        	
    	} 
    	else{ 
    		return list = null;
    	}
    }
  
}
