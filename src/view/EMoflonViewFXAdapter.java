package view;

import java.io.IOException;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import controller.VisFXController;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;
import model.ModelHandler;
import model.ModelRecognizer;
import model.VisJsScriptTemplates;
import javafx.scene.web.WebEngine;
import javafx.scene.layout.StackPane;

public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {
	private FXCanvas fxCanvas;
	private Thread visualizerThread;
	private WebView webView;
	private WebEngine engine;
	private ToggleButton attrButton;
	private ToggleButton edgeButton;
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
		if (attrButton.isSelected()) {
			model.createNetworkToggle(engine);
		} else {
			model.createNetwork(engine);
		}
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
		// set scenetype filter text
		Group group = new Group();
		Scene view_scene = new Scene(group);
		fxCanvas.setScene(view_scene);
		attrButton = new ToggleButton("hide attributes");
		attrButton.setOnAction(value -> {
			if (attrButton.isSelected()) {
				attrButton.setText("show attributes");
				engine.executeScript(VisJsScriptTemplates.destroyNetwork());
				model.createNetworkToggle(engine);
				engine.executeScript(VisJsScriptTemplates.clickOnNetworkShowAttributes());
			} else {
				attrButton.setText("hide attributes");
				engine.executeScript(VisJsScriptTemplates.destroyNetwork());
				model.createNetwork(engine);
				engine.executeScript(VisJsScriptTemplates.removeClickOnNetworkShowAttributes());
			}
		});
		edgeButton = new ToggleButton("hide edges");
		edgeButton.setOnAction(value -> {
			if (edgeButton.isSelected()) {
				edgeButton.setText("show edges");
				engine.executeScript(VisJsScriptTemplates.destroyNetwork());
				model.createNetworkToggle(engine);
			} else {
				edgeButton.setText("hide edges");
				engine.executeScript(VisJsScriptTemplates.destroyNetwork());
				model.createNetwork(engine);
			}
		});
		group.getChildren().add(webView);
		group.getChildren().add(attrButton);
		group.getChildren().add(edgeButton);
		attrButton.setLayoutX(400);
		attrButton.setLayoutY(370);
		edgeButton.setLayoutX(305);
		edgeButton.setLayoutY(370);
		fxCanvas.setScene(view_scene);
		// Add node click
//		webView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent event) {
//				if (attrButton.isSelected()) {
//					engine.executeScript(VisJsScriptTemplates.clickOnNetworkShowAttributes());
//				}
//			}
//		});

	}

}
