package sppp_visualizerold;

import java.io.IOException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import api.Main;
import api.VisWindow;
import javafx.application.Application;
import javafx.embed.swt.FXCanvas;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {

	private final Stage stage = new Stage();
	
	public EMoflonViewFXAdapter() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection) {
		System.out.println("success");
		return true;
	}

	@Override
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection) {
		System.out.println("success");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 600, 400);
		stage.setScene(scene);
		stage.show();
		Application.launch(FXMLWindow.class);
		return true;
	}

}
