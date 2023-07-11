package view;

import java.net.URL;
import java.util.ResourceBundle;

import api.VisJsAdapter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.web.*;

public class VisFXController implements Initializable  {

	@FXML
	private WebView webView;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		webView = new WebView();
		WebEngine engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(VisJsAdapter.getJSTemplate());
		webView.setVisible(true);
		
	}

}
