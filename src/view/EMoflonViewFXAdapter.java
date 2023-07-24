package view;

import java.io.IOException;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import api.VisJsAdapter;
import controller.VisFXController;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebView;
import model.ModelHandler;
import model.ModelRecognizer;
import model.VisJsScriptTemplates;
import javafx.scene.web.WebEngine;



public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {
    private FXCanvas fxCanvas;
	private Thread visualizerThread;
	private WebView webView;
	private WebEngine engine;
	private ModelHandler model;
	
	public EMoflonViewFXAdapter() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection) {
		System.out.println("supportsSelection");
		return ModelRecognizer.identifySelection(selection);
	}

	@Override
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection) {
		System.out.println("renderView");
		model = ModelRecognizer.identifyModel(selection);
		engine.executeScript(VisJsScriptTemplates.destroyNetwork());
		model.buildVis();
		model.createNetwork(engine);
		return true;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("createPartControl");
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		FXCanvas fxCanvasControls = new FXCanvas(parent, SWT.NONE);
		webView = new WebView();
		engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(VisJsScriptTemplates.getJSTemplate());
        // set scenetype filter text
		Group group = new Group(); 
		Group group2 = new Group();
		Parent root;
	        Scene view_scene = new Scene(group);
	        Scene scene2 = new Scene(group2);
	        Button button = new Button("JFX ButtoSn");
	        group2.getChildren().add(button);
	        group.getChildren().add(webView);
	        fxCanvas.setScene(view_scene);
//	        fxCanvasControls.setScene(scene2);

	}

}
