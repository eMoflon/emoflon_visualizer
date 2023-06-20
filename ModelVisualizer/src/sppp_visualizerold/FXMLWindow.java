package sppp_visualizerold;


import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.application.Application;
import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class FXMLWindow extends Application {

	private static FXMLWindow singleton;
	private static Composite composite;
	
	public FXMLWindow() {
//		singleton = this;
//		System.out.println("Creating FXMLWindow");
	}

	@Override
    public void start(Stage primaryStage) {
		if(composite == null)
			throw new RuntimeException("FXMLWindow needs a preregistered composite");
		
		System.out.println("Starting FXMLWindow");
		var canvas = new FXCanvas(composite, SWT.NONE);

        canvas.setScene(createScene());
    }
	
	public static void registerParentComposite(Composite composite) {
		System.out.println("Register Composite");
		FXMLWindow.composite = composite;
//		
//		var canvas = new FXCanvas(composite, SWT.NONE);
//
//        canvas.setScene(createScene());
	}
	
	private static Scene createScene() {
		System.out.println("Creating Scene");
		Group group = new Group();
        Scene scene = new Scene(group);
        Button button = new Button("JFX Button");
        group.getChildren().add(button);
        return scene;
	}

	public static void updateSelection(ISelection selection) {
		
	}
	
	private void updateSelectionOnInstance(ISelection selection) {
		
	}
}
