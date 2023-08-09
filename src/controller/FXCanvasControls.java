package controller;

import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class FXCanvasControls {

	public FXCanvasControls(ObservableList<Node> parts) {
		parts.forEach(part ->{
			if(part instanceof Button) {
				
			}
		});
	}
}
