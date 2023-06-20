package controller;
	
import javafx.application.Application;
import view.OtherFXMLWindow;

public class FXMLWindowLauncher {
	
	/**
	 * Main-method for launching a dynamic visualization of nodes & edges, using 
	 * Vis.js network as the main source of the visualization, JavaFX + Scene Builder 
	 * and the FX Markup Language for a better and more functional UI.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		Application.launch(OtherFXMLWindow.class);
	}
}

