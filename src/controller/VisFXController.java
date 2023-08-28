package controller;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.web.*;
import javafx.scene.Scene;
import model.ModelHandler;
import model.VisJsScriptTemplates;

/**
 * 
 * @author ms21xino
 *
 */
public class VisFXController {

	private FXCanvas fxCanvas;
	private ModelHandler model;
	private WebView webView;
	private WebEngine engine;
	private Group root;

	/**
	 * 
	 * @param model
	 * @param parent
	 */
	public VisFXController(Composite parent) {
		fxCanvas = new FXCanvas(parent, SWT.NONE);
	}

	public void handOverModel(ModelHandler model) {
		this.model = model;
	}

	/**
	 * 
	 */
	public void initialize() {
		webView = new WebView();
		root = new Group();

		engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(VisJsScriptTemplates.getJSTemplate());

		Scene view_scene = new Scene(root);
		fxCanvas.setScene(view_scene);
	}

	/** 
	 * 
	 */
	public void addControlsforView() {
		root.getChildren().clear();
		root.getChildren().add(webView);
		createControlsForView().forEach(control -> {
			root.getChildren().add(control);
		});
	}

	/**
	 * 
	 * @return
	 */
	private List<Node> createControlsForView() {
		List<Node> controls = new ArrayList<Node>();
		controls.add(createHideAttrToggle());
		controls.add(createChoiceBox());
		controls.add(createChoiceBoxLabel());
		controls.add(createFilterTextField());
		controls.add(createTextFieldLabel());
		return controls;
	}

	/**
	 * 
	 */
	public void buildVisWithControls() {
		engine.executeScript(VisJsScriptTemplates.destroyNetwork());
		model.buildVis();
		model.createNetwork(engine);
	}

	/**
	 * 
	 * @return
	 */
	private ToggleButton createHideAttrToggle() {
		ToggleButton attrButton = new ToggleButton("hide attributes");
		attrButton.setLayoutX(400);
		attrButton.setLayoutY(370);
		attrButton.setOnAction(value -> {
			if (attrButton.isSelected()) {
				engine.executeScript(VisJsScriptTemplates.hideAllAttributes(model.getNodeId()));
				attrButton.setText("show attributes");
				engine.executeScript(VisJsScriptTemplates.clickOnNetworkShowAttributes());
			} else {
				engine.executeScript(VisJsScriptTemplates.showAllAttributes(model.getNodeId()));
				attrButton.setText("hide attributes");
				engine.executeScript(VisJsScriptTemplates.removeClickOnNetworkShowAttributes());
			}
		});

		return attrButton;
	}

	/**
	 * 
	 * @return
	 */
	private ChoiceBox<String> createChoiceBox() {
		ChoiceBox<String> cb = new ChoiceBox<String>();
		cb.setLayoutX(220);
		cb.setLayoutY(370);
		cb.getItems().add("None");
		model.computeitems().forEach(item -> {
			cb.getItems().add(item);
		});
		cb.setOnAction(e -> {
			if (cb.getValue().equals("None")) {
				engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
			} else {
				engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
				model.getChoiceIds(cb.getValue()).forEach(id -> {
					engine.executeScript(VisJsScriptTemplates.hightlightChoiceNodes(id));
				});
			}
		});
		return cb;
	}

	/**
	 * 
	 * @return
	 */
	private Label createChoiceBoxLabel() {
		Label choiceBoxLabel = new Label("Highlight following EClasses :");
		choiceBoxLabel.setLayoutX(20);
		choiceBoxLabel.setLayoutY(373);
		return choiceBoxLabel;
	}

	/**
	 * 
	 * @return
	 */
	private TextField createFilterTextField() {
		TextField filterTextField = new TextField();
		filterTextField.setLayoutX(220);
		filterTextField.setLayoutY(400);
		filterTextField.setOnAction(e_ -> {
			engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
			model.getTextFieldIds(filterTextField.getText()).forEach(id -> {
				engine.executeScript(VisJsScriptTemplates.hightlightChoiceNodes(id));
			});
		});
		return filterTextField;
	}

	/**
	 * 
	 * @return
	 */
	private Label createTextFieldLabel() {
		Label choiceBoxLabel = new Label("Search for following String :");
		choiceBoxLabel.setLayoutX(20);
		choiceBoxLabel.setLayoutY(400);
		return choiceBoxLabel;
	}

}
