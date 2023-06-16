package controller;

import java.net.URL;
import java.util.ResourceBundle;

import api.VisJsAdapter;
import javafx.fxml.Initializable;
import javafx.scene.web.*;

public class ControllerPlaceHolder implements Initializable  {

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		WebView webView = new WebView();
		WebEngine engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
//		engine.loadContent(VisJsAdapter.getJSTemplate());
		webView.setVisible(true);
		
	}

}
