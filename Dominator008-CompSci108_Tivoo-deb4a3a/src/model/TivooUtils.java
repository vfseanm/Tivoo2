package model;

import java.io.File;

public class TivooUtils {
    
    public static void deleteFile(String path){ 
	File file = new File(path);
	if (file.exists()) { 
	    if(file.isFile()) { 
		file.delete(); 
	    } 
	    else if(file.isDirectory()){ 
		File files[] = file.listFiles(); 
		for (int i = 0; i < files.length; i++)
		    deleteFile(files[i].getPath()); 
	    } 
	    file.delete(); 
       	} else{ 
       	    throw new TivooException("File not found!");
       	} 
    } 
    
    public static void clearDirectory(String path){ 
	File file = new File(path);
	if (file.exists()) { 
	    if(file.isDirectory()){ 
		File files[] = file.listFiles(); 
		for (int i = 0; i < files.length; i++)
		    deleteFile(files[i].getPath()); 
	    }
       	} else{ 
       	    throw new TivooException("Directory not found!");
       	} 
    } 
    
}