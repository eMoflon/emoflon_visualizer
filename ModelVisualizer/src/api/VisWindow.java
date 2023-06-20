package api;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class VisWindow extends Application implements EventHandler<KeyEvent> {

	protected JSObject window;

	public VisWindow() {
	}

	/**
	 * Called at the startup -> the Java launcher loads and initializes the
	 * specified Application class (here) on the JavaFX Application Thread
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("VisWindow.fxml"));
			Scene scene = new Scene(root);
			
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("emoflon.jpg")));
			primaryStage.setTitle("Visualizer");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
			int n = 0;
		}
	}

	/**
	 * Not in use yet
	 */
	@Override
	public void handle(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

}
