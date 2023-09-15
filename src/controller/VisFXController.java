package controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.HashBiMap;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
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
	private String highlightColor;
	private String highlightBorder;
	private Color highlight;
	private Map<ISelection, Entry<List<Integer>, List<Integer>>> savedConfigs;
	private Map<ISelection, List<FilterWordPair>> savedTableConfigs;

	private ISelection selection;
	private List<Node> controls;

	/**
	 * 
	 * @param model
	 * @param parent
	 */
	public VisFXController(Composite parent) {
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		savedConfigs = new HashMap<>();
		savedTableConfigs = new HashMap<>();
		highlight = Color.web("#ffb347");
		highlightColor = "#ffb347";
		highlightBorder = "#ae6500";
	}

	/**
	 * 
	 * @param model
	 */
	public void selectionToModel(ModelHandler model, ISelection selection) {
		this.model = model;
		this.selection = selection;
		createVis();
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

		Scene view_scene = new Scene(root, 1000, 1000);
		view_scene.getStylesheets().add(getClass().getResource("fxstyling.css").toExternalForm());
		fxCanvas.setScene(view_scene);
	}

	/** 
	 * 
	 */
	public void addControlsForView() {
		webView.setScaleX(1.2);
		webView.setScaleY(1.2);
		webView.setLayoutX(80);
		webView.setLayoutY(60);
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
		controls = new ArrayList<Node>();
		controls.add(createHideAttrToggle());
		controls.add(createChoiceBox());
		controls.add(createChoiceBoxLabel());
		controls.add(createFilterFirstTextField());
		controls.add(createFilterSecondTextField());
		controls.add(createTextFieldLabel());
		controls.add(createHideNonHighlightToggle());
		controls.add(createDeHighlightButton());
		controls.add(createColorPicker());
		controls.add(createColorPickerCheckBox());
		controls.add(createTable());
		controls.add(createAddToTableButton());
		controls.add(createClearTableButton());
		controls.add(createHideHighlightToggle());
		controls.add(createControlsLabel());
		controls.add(createClickToHighlightLabel());
		controls.add(createRemoveLastTableItemButton());
		return controls;
	}

	/**
	 *
	 */
	public void createVis() {
		engine.executeScript(VisJsScriptTemplates.destroyNetwork());
		model.buildVis();
		model.createNetwork(engine);
		addControlsForView();
		if (savedConfigs.containsKey(selection)) {
			if (savedTableConfigs.containsKey(selection)) {
				savedTableConfigs.get(selection).forEach(tableEntry -> {
					((TableView) controls.get(10)).getItems().add(tableEntry);
				});
			}
			if (!savedConfigs.get(selection).getValue().isEmpty()) {
				((ToggleButton) controls.get(5)).setSelected(true);
				((ToggleButton) controls.get(5)).setText("Show non-hightlighted nodes");
			}
			savedConfigs.get(selection).getKey().forEach(highlightId -> {
				engine.executeScript(
						VisJsScriptTemplates.hightlightColorNodes(highlightId, highlightColor, highlightBorder));
			});
			savedConfigs.get(selection).getValue().forEach(hideId -> {
				engine.executeScript(VisJsScriptTemplates.hideNode(hideId));
			});
		} else {
			savedConfigs.put(selection, new AbstractMap.SimpleEntry<List<Integer>, List<Integer>>(
					new ArrayList<Integer>(), new ArrayList<Integer>()));
		}
	}

	/**
	 * 
	 * @return
	 */
	private ToggleButton createHideAttrToggle() {
		ToggleButton attrButton = new ToggleButton("hide attributes");
		attrButton.setLayoutX(235);
		attrButton.setLayoutY(440);
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
	private Button createAddToTableButton() {
		Button addToTableButton = new Button("Add filter to table");
		addToTableButton.setLayoutX(582);
		addToTableButton.setLayoutY(470);
		addToTableButton.setOnAction(e -> {
			if (((TextField) controls.get(3)).getText().isBlank()
					|| ((TextField) controls.get(4)).getText().isBlank()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please fill in both textfields");
				alert.show();
			} else {
				if (!savedTableConfigs.containsKey(selection)) {
					savedTableConfigs.put(selection, new ArrayList<FilterWordPair>());
					savedTableConfigs.get(selection).add(new FilterWordPair(((TextField) controls.get(3)).getText(),
							((TextField) controls.get(4)).getText()));
					((TableView) controls.get(10)).getItems().add(new FilterWordPair(
							((TextField) controls.get(3)).getText(), ((TextField) controls.get(4)).getText()));
				} else {
					((TableView) controls.get(10)).getItems().add(new FilterWordPair(
							((TextField) controls.get(3)).getText(), ((TextField) controls.get(4)).getText()));
					savedTableConfigs.get(selection).add(new FilterWordPair(((TextField) controls.get(3)).getText(),
							((TextField) controls.get(4)).getText()));
				}
			}
		});
		return addToTableButton;

	}

	/**
	 * 
	 * @return
	 */
	private ChoiceBox<String> createChoiceBox() {
		ChoiceBox<String> cb = new ChoiceBox<String>();
		cb.setLayoutX(200);
		cb.setLayoutY(500);
		cb.getItems().add("None");
		model.computeitems().forEach(item -> {
			cb.getItems().add(item);
		});
		cb.setOnAction(e -> {
			if (cb.getValue().equals("None")) {
				engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
			} else {
				model.getChoiceIds(cb.getValue()).forEach(id -> {
					engine.executeScript(
							VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
					savedConfigs.get(selection).getKey().add(id);
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
		Label choiceBoxLabel = new Label("Highlight following EClass :");
		choiceBoxLabel.setLayoutX(20);
		choiceBoxLabel.setLayoutY(503);
		return choiceBoxLabel;
	}

	/**
	 * 
	 * @return
	 */
	private TextField createFilterFirstTextField() {
		TextField filterTextField = new TextField();
		filterTextField.setLayoutX(220);
		filterTextField.setLayoutY(470);
		filterTextField.setOnAction(e_ -> {
			if (((TextField) controls.get(4)).getText().isBlank()) {
				model.getTextFieldIds(filterTextField.getText()).forEach(id -> {
					engine.executeScript(
							VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
					savedConfigs.get(selection).getKey().add(id);
				});
			} else {
				model.getTextFieldAndIds(((TextField) controls.get(4)).getText(), filterTextField.getText())
						.forEach(id -> {
							engine.executeScript(
									VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
							savedConfigs.get(selection).getKey().add(id);
						});
			}
		});
		return filterTextField;
	}

	/**
	 * 
	 * @return
	 */
	private TextField createFilterSecondTextField() {
		TextField filterTextField = new TextField();
		filterTextField.setLayoutX(400);
		filterTextField.setLayoutY(470);
		filterTextField.setOnAction(e_ -> {
			if (((TextField) controls.get(3)).getText().isBlank()) {
				model.getTextFieldIds(filterTextField.getText()).forEach(id -> {
					engine.executeScript(
							VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
					savedConfigs.get(selection).getKey().add(id);
				});
			} else {
				model.getTextFieldAndIds(((TextField) controls.get(3)).getText(), filterTextField.getText())
						.forEach(id -> {
							engine.executeScript(
									VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
							savedConfigs.get(selection).getKey().add(id);
						});
			}
		});
		return filterTextField;
	}

	/**
	 * 
	 * @return
	 */
	private Label createTextFieldLabel() {
		Label choiceBoxLabel = new Label("Search for following regex :");
		choiceBoxLabel.setLayoutX(20);
		choiceBoxLabel.setLayoutY(475);
		return choiceBoxLabel;
	}

	/**
	 * 
	 * @return
	 */
	private ToggleButton createHideNonHighlightToggle() {
		ToggleButton nonHighlightButton = new ToggleButton("Hide non-hightlighted nodes");
		nonHighlightButton.setLayoutX(355);
		nonHighlightButton.setLayoutY(440);
		nonHighlightButton.setOnAction(value -> {
			if (nonHighlightButton.isSelected()) {
				if (!savedConfigs.get(selection).getKey().isEmpty()) {
					nonHighlightButton.setText("Show non-hightlighted nodes");
					model.getNonHighlightIds(savedConfigs.get(selection).getKey()).forEach(id -> {
						engine.executeScript(VisJsScriptTemplates.hideNode(id));
						savedConfigs.get(selection).getValue().add(id);
					});
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Nothing to hide!");
					alert.show();
					nonHighlightButton.setSelected(false);
				}
			} else {
				nonHighlightButton.setText("Hide non-hightlighted nodes");
				model.getNonHighlightIds(savedConfigs.get(selection).getKey()).forEach(id -> {
					engine.executeScript(VisJsScriptTemplates.showNode(id));
					savedConfigs.get(selection).getValue().remove(id);
				});
			}
		});

		return nonHighlightButton;
	}

	/**
	 * 
	 * @return
	 */
	private Button createDeHighlightButton() {
		Button deHighlightButton = new Button("De-highlight all");
		deHighlightButton.setLayoutX(745);
		deHighlightButton.setLayoutY(440);
		deHighlightButton.setOnAction(e -> {
			engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
			savedConfigs.get(selection).getKey().clear();
		});
		return deHighlightButton;
	}

	/**
	 * 
	 * @return
	 */
	private ColorPicker createColorPicker() {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setTooltip(new Tooltip("Pick preferred color for hightlighting"));
		colorPicker.setLayoutX(85);
		colorPicker.setLayoutY(440);
		colorPicker.setOnAction(e -> {
			StringBuilder sb = new StringBuilder(colorPicker.getValue().toString());
			StringBuilder sbBorder = new StringBuilder(colorPicker.getValue().darker().darker().toString());
			sb.delete(0, 2);
			sb.delete(6, 8);
			sbBorder.delete(0, 2);
			sbBorder.delete(6, 8);
			highlightColor = "#" + sb.toString();
			highlightBorder = "#" + sbBorder.toString();
			if (!((CheckBox) controls.get(8)).isSelected()) {
				engine.executeScript(
						VisJsScriptTemplates.clickOnNetworkHighlight("#" + sb.toString(), "#" + sbBorder.toString()));
			} else {
				savedConfigs.get(selection).getKey().forEach(highlightId -> {
					engine.executeScript(VisJsScriptTemplates.hightlightColorNodes(highlightId, "#" + sb.toString(),
							"#" + sbBorder.toString()));
				});
			}
		});
		return colorPicker;
	}

	/**
	 * 
	 * @return
	 */
	private CheckBox createColorPickerCheckBox() {
		CheckBox cb = new CheckBox();
		cb.setOnAction(e -> {
			if (cb.isSelected()) {
				engine.executeScript(VisJsScriptTemplates.clickOnNetworkHighlight(highlightColor, highlightBorder));
			} else {
				engine.executeScript(VisJsScriptTemplates.removeClickOnNetworkHighlight());
			}
		});
		cb.setLayoutX(615);
		cb.setLayoutY(503);
		return cb;
	}

	/**
	 * 
	 * @return
	 */
	private TableView createTable() {
		TableView<FilterWordPair> table = new TableView<FilterWordPair>();
		TableColumn<FilterWordPair, String> firstFilterWordColumn = new TableColumn<FilterWordPair, String>(
				"filter where");
		firstFilterWordColumn.setCellValueFactory(new PropertyValueFactory<FilterWordPair, String>("firstFilterWord"));
		TableColumn<FilterWordPair, String> secondFilterWordColumn = new TableColumn<FilterWordPair, String>("and");
		secondFilterWordColumn
				.setCellValueFactory(new PropertyValueFactory<FilterWordPair, String>("secondFilterWord"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(firstFilterWordColumn, secondFilterWordColumn);
		table.setLayoutX(620);
		table.setLayoutY(10);
		table.setRowFactory(tv -> {
			TableRow<FilterWordPair> row = new TableRow<FilterWordPair>();
			row.setOnMouseClicked(e -> {
				model.getTextFieldAndIds(row.getItem().getFirstFilterWord(), row.getItem().getSecondFilterWord())
						.forEach(id -> {
							engine.executeScript(
									VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
							savedConfigs.get(selection).getKey().add(id);
						});
			});
			return row;
		});
		return table;
	}

	/**
	 * 
	 * @return
	 */
	private Button createClearTableButton() {
		Button clearHighlightButton = new Button("Clear AND-table");
		clearHighlightButton.setLayoutX(725);
		clearHighlightButton.setLayoutY(470);
		clearHighlightButton.setOnAction(e -> {
			((TableView) controls.get(10)).getItems().clear();
			savedTableConfigs.get(selection).clear();
		});
		return clearHighlightButton;
	}

	/**
	 * 
	 * @return
	 */
	private ToggleButton createHideHighlightToggle() {
		ToggleButton hideHighlightButton = new ToggleButton("Hide hightlighted nodes");
		hideHighlightButton.setLayoutX(565);
		hideHighlightButton.setLayoutY(440);
		hideHighlightButton.setOnAction(value -> {
			if (hideHighlightButton.isSelected()) {
				if (!savedConfigs.get(selection).getKey().isEmpty()) {
					hideHighlightButton.setText("Show hightlighted nodes");
					savedConfigs.get(selection).getKey().forEach(highlightId -> {
						engine.executeScript(VisJsScriptTemplates.hideNode(highlightId));
					});
				} else {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setContentText("Nothing to hide!");
					alert.show();
					hideHighlightButton.setSelected(false);
				}
			} else {
				hideHighlightButton.setText("Hide hightlighted nodes");
				savedConfigs.get(selection).getKey().forEach(highlightId -> {
					engine.executeScript(VisJsScriptTemplates.showNode(highlightId));
					engine.executeScript(
							VisJsScriptTemplates.hightlightColorNodes(highlightId, highlightColor, highlightBorder));
				});
			}
		});

		return hideHighlightButton;
	}

	/**
	 * 
	 * @return
	 */
	private Label createControlsLabel() {
		Label controlsLabel = new Label("Controls :");
		controlsLabel.setLayoutX(20);
		controlsLabel.setLayoutY(443);
		return controlsLabel;
	}

	/**
	 * 
	 * @return
	 */
	private Label createClickToHighlightLabel() {
		Label clickToHighlightLabel = new Label("| Check to enable click-to-hightlight-function :");
		clickToHighlightLabel.setLayoutX(310);
		clickToHighlightLabel.setLayoutY(505);
		return clickToHighlightLabel;
	}

	/**
	 * 
	 * @return
	 */
	private Button createRemoveLastTableItemButton() {
		Button removeLastTableItemButton = new Button("remove last item from table");
		removeLastTableItemButton.setLayoutX(640);
		removeLastTableItemButton.setLayoutY(500);
		removeLastTableItemButton.setOnAction(e -> {
			if (!((TableView) controls.get(10)).getItems().isEmpty()) {
				((TableView) controls.get(10)).getItems().remove(((TableView) controls.get(10)).getItems().size() - 1);
				savedTableConfigs.get(selection).clear();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Nothing to remove!");
				alert.show();
			}
		});
		return removeLastTableItemButton;
	}

}
