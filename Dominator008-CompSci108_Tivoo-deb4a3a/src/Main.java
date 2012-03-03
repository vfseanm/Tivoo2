import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import view.SelectFile;
import controller.*;

public class Main {
    
    public static void main(String[] args) throws IOException {
	TivooController controller = new TivooController(); 

	controller.view();
	//controller.initialize();
	
	
    }
    
}