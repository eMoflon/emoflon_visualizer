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
		engine.executeScript(VisJsScriptTemplates.destroyNetwork());
		model = ModelRecognizer.identifyModel(selection);
		model.buildVis();
		engine.executeScript(VisJsScriptTemplates.addNodes(model.returnNodes()));
		engine.executeScript(VisJsScriptTemplates.addAbstractNodes(model.returnAbstractNodes()));
		engine.executeScript(VisJsScriptTemplates.addInterfaceNodes(model.returnInterfaceNodes()));
		engine.executeScript(VisJsScriptTemplates.addEnumNodes(model.returnEnumNodes()));
		engine.executeScript(VisJsScriptTemplates.addEdges(model.returnEdges()));
//		engine.executeScript(VisJsScriptTemplates.addBidirectionalEdges(model.returnBiDirEdges()));
		engine.executeScript(VisJsScriptTemplates.addHeridityEdges(model.returnHeridityEdges()));
		engine.executeScript(VisJsScriptTemplates.addInterfaceNodes(model.returnInterfaceNodes()));
		engine.executeScript("var network = new vis.Network(container,data, options);");

		return true;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("createPartControl");
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		webView = new WebView();
		engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(VisJsScriptTemplates.getJSTemplate());
        // set scene
		Group group = new Group(); 
		Parent root;
//		try {
			FXMLLoader.setDefaultClassLoader(VisFXController.class.getClassLoader());
//			root = FXMLLoader.load(getClass().getResource("VisFXMLView.fxml"));
	        Scene view_scene = new Scene(group);
//	        Button button = new Button("JFX ButtoSn");
//	        group.getChildren().add(button);
	        group.getChildren().add(webView);
	        fxCanvas.setScene(view_scene);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
