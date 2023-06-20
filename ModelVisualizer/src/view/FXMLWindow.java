package view;

import javafx.application.Application;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import netscape.javascript.JSObject;

public class FXMLWindow extends Application {

	public static FXCanvas canvas;
	
	protected JSObject window;

	public FXMLWindow() {
	}

	/**
	 * Called at the startup -> the Java launcher loads and initializes the
	 * specified Application class (here) on the JavaFX Application Thread
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 600, 400);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Example");
			primaryStage.show();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
