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

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.web.*;
import javafx.scene.Scene;
import model.ModelHandler;
import model.VisJsScriptTemplates;

/**
 * Controller for the JavaFX-controls embedded into the eMoflon-view -> used to
 * control the currents contents of the webview
 * 
 * @author maximiliansell
 */
public class VisFXController {

	private FXCanvas fxCanvas;
	private ModelHandler model;
	private WebView webView;
	private WebEngine engine;
	private Group root;
	private String highlightColor;
	private String highlightBorder;
	// highlightToggle.getValue() = 0 : default
	// highlightToggle.getValue() = 1 : hideNonHighlightNodes selected
	// highloghtToggle.getValue() = 2 : hideHighlightNodes selected
	private Map<ISelection, Integer> savedHighlightToggle;
	private Map<ISelection, List<Entry<Integer, Entry<String, String>>>> savedHighlightConfigs;
	private Map<ISelection, List<Integer>> savedHideConfigs;
	private Map<ISelection, List<FilterWordPair>> savedTableConfigs;

	private ISelection selection;
	private List<Node> controls;

	/**
	 * Creates a fxCanvas and adding it to the eclipse-SWT/UI/emoflon-view of the
	 * runtime. Initializes the maps to save changes done to the visualisation of a
	 * certain selection. Initializes the default colors for highlighting nodes
	 * 
	 * @param parent - parent of the fxCanvas provided by the eclipse-SWT
	 */
	public VisFXController(Composite parent) {
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		savedTableConfigs = new HashMap<>();
		savedHighlightToggle = new HashMap<>();
		savedHighlightConfigs = new HashMap<>();
		savedHideConfigs = new HashMap<>();
		highlightColor = "#ffb347";
		highlightBorder = "#ae6500";
	}

	/**
	 * Sets the model and selection to make adjustments to the visualisation. Calls
	 * createVis() to start the creation of the visualisation
	 * 
	 * @param model     - model of the current selection
	 * @param selection - current selection to be visualised
	 */
	public void selectionToModel(ModelHandler model, ISelection selection) {
		this.model = model;
		this.selection = selection;
		createVis();
	}

	/**
	 * Initializes the webview including the needed JavaFX-scene parts
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
	 * Adds the JavaFX-controls for the emoflon-view to the controls-list. Calls
	 * createControlsForView() to create the JavaFX-controls for the list.
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
	 * Creates the controls for the emoflon-view
	 * 
	 * @return list of controls for the emoflon-view
	 */
	private List<Node> createControlsForView() {
		controls = new ArrayList<Node>();
		controls.add(createHideAttrToggle());
		controls.add(createChoiceBox());
		controls.add(createChoiceBoxLabel());
		controls.add(createFirstTextField());
		controls.add(createSecondTextField());
		controls.add(createTextFieldLabel());
		controls.add(createHideNonHighlightToggle());
		controls.add(createDeHighlightButton());
		controls.add(createColorPicker());
		controls.add(createTableLabel());
		controls.add(createTable());
		controls.add(createAddToTableButton());
		controls.add(createClearTableButton());
		controls.add(createHideHighlightToggle());
		controls.add(createControlsLabel());
		controls.add(createClickToHighlightLabel());
		controls.add(createRemoveLastTableItemButton());
		controls.add(createAndLabel());
		controls.add(createHelpButton());
		return controls;
	}

	/**
	 * Creates the visualisation using the buildVis()-method of the current model
	 * into the webview of the emoflon-view. If the current selection has been saved
	 * to any HashMap including any values -> these values like highlighted or
	 * hidden nodes will be restored
	 */
	public void createVis() {
		engine.executeScript(VisJsScriptTemplates.destroyNetwork());
		model.buildVis();
		model.createNetwork(engine);
		if (!savedHighlightConfigs.containsKey(selection)) {
			savedHighlightConfigs.put(selection, new ArrayList<Map.Entry<Integer, Map.Entry<String, String>>>());
		}
		if (!savedHideConfigs.containsKey(selection)) {
			savedHideConfigs.put(selection, new ArrayList<Integer>());
		}
		if (!savedHighlightToggle.containsKey(selection)) {
			savedHighlightToggle.put(selection, 0);
		}
		addControlsForView();
		if (savedHighlightConfigs.containsKey(selection) && savedHideConfigs.containsKey(selection)) {
			if (savedTableConfigs.containsKey(selection)) {
				savedTableConfigs.get(selection).forEach(tableEntry -> {
					((TableView) controls.get(10)).getItems().add(tableEntry);
				});
			}
			if (!savedHideConfigs.get(selection).isEmpty()) {
				if (savedHighlightToggle.get(selection) == 1) {
					((ToggleButton) controls.get(6)).setSelected(true);
					((ToggleButton) controls.get(6)).setText("Show non-hightlighted nodes");
				}
				if (savedHighlightToggle.get(selection) == 2) {
					((ToggleButton) controls.get(13)).setSelected(true);
					((ToggleButton) controls.get(13)).setText("Show hightlighted nodes");
				}
			}

			savedHighlightConfigs.get(selection).forEach(entry -> {
				engine.executeScript(VisJsScriptTemplates.hightlightColorNodes(entry.getKey(),
						entry.getValue().getKey(), entry.getValue().getValue()));
			});
			savedHideConfigs.get(selection).forEach(hideId -> {
				engine.executeScript(VisJsScriptTemplates.hideNode(hideId));
			});
		}
	}

	/**
	 * Creates a toggle-button which hides/shows the attributes of all nodes present
	 * in the webview. Furthermore adds/removes a listener to the javascript that
	 * enables to view the attributes on a clicked-node if hidden
	 * 
	 * @return toggle-button
	 */
	private ToggleButton createHideAttrToggle() {
		ToggleButton attrToggle = new ToggleButton("hide attributes");
		attrToggle.setTooltip(new Tooltip("Hide/show attributes of the nodes on click." + "\n"
				+ "If attributes are hidden -> click on node to temporarily show its attributes"));
		attrToggle.setLayoutX(240);
		attrToggle.setLayoutY(440);

		attrToggle.setOnAction(value -> {
			if (attrToggle.isSelected()) {
				engine.executeScript(VisJsScriptTemplates.hideAllAttributes(model.getNodeId()));
				attrToggle.setText("show attributes");
				engine.executeScript(VisJsScriptTemplates.clickOnNetworkShowAttributes());
			} else {
				engine.executeScript(VisJsScriptTemplates.showAllAttributes(model.getNodeId()));
				attrToggle.setText("hide attributes");
				engine.executeScript(VisJsScriptTemplates.removeClickOnNetworkShowAttributes());
			}
		});

		return attrToggle;
	}

	/**
	 * Creates a choice-box which contains the name of all EClasses currently
	 * present in the given model. Choicebox highlights any nodes which match with
	 * the selected item of the choicebox
	 * 
	 * @return choice-box
	 */
	private ChoiceBox<String> createChoiceBox() {
		ChoiceBox<String> choiceBox = new ChoiceBox<String>();
		choiceBox.setTooltip(new Tooltip("Highlights each node with the typ of the selected EClass"));
		choiceBox.setLayoutX(445);
		choiceBox.setLayoutY(530);
		model.computeitems().forEach(item -> {
			choiceBox.getItems().add(item);
		});
		choiceBox.setOnAction(e -> {
			model.getChoiceIds(choiceBox.getValue()).forEach(id -> {
				engine.executeScript(VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
				savedHighlightConfigs.get(selection)
						.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
								new AbstractMap.SimpleEntry<String, String>(highlightColor, highlightBorder)));
			});
		});
		return choiceBox;
	}

	/**
	 * Creates a label with the following label : "Highlight following EClass :"
	 * 
	 * @return label
	 */
	private Label createChoiceBoxLabel() {
		Label choiceBoxLabel = new Label("Highlight nodes with EClasses matching the following selection :");
		choiceBoxLabel.setLayoutX(20);
		choiceBoxLabel.setLayoutY(533);
		return choiceBoxLabel;
	}

	/**
	 * Creates the "first" textfield which input will be used to filter through all
	 * of the model-nodes (see : model-method "getTextFieldIds") -> if the "second"
	 * textfield is not empty both of the strings will be used as the filter (see :
	 * model-method "getTextFieldAndIds")
	 * 
	 * @return "first" textfield
	 */
	private TextField createFirstTextField() {
		TextField firstTextField = new TextField();
		firstTextField.setTooltip(new Tooltip("Highlight nodes which contain the filled in regex"));
		firstTextField.setLayoutX(380);
		firstTextField.setLayoutY(470);
		firstTextField.setOnAction(e_ -> {
			if (!firstTextField.getText().isBlank()) {
				if (((TextField) controls.get(4)).getText().isBlank()) {
					model.getTextFieldIds(firstTextField.getText()).forEach(id -> {
						engine.executeScript(
								VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
						savedHighlightConfigs.get(selection)
								.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
										new AbstractMap.SimpleEntry<String, String>(highlightColor, highlightBorder)));
					});
				} else {
					model.getTextFieldAndIds(((TextField) controls.get(4)).getText(), firstTextField.getText())
							.forEach(id -> {
								engine.executeScript(
										VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
								savedHighlightConfigs.get(selection)
										.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
												new AbstractMap.SimpleEntry<String, String>(highlightColor,
														highlightBorder)));
							});
				}
			}
		});
		return firstTextField;
	}

	/**
	 * Creates the "second" textfield which input will be used to filter through all
	 * of the model-nodes (see : model-method "getTextFieldIds") -> if the "first"
	 * textfield is not empty both of the strings will be used as the filter (see :
	 * model-method "getTextFieldAndIds")
	 * 
	 * @return "second" textfield
	 */
	private TextField createSecondTextField() {
		TextField secondTextField = new TextField();
		secondTextField.setTooltip(new Tooltip("Highlight nodes which contain the filled in regex"));
		secondTextField.setLayoutX(570);
		secondTextField.setLayoutY(470);
		secondTextField.setOnAction(e_ -> {
			if (!secondTextField.getText().isBlank()) {
				if (((TextField) controls.get(3)).getText().isBlank()) {
					model.getTextFieldIds(secondTextField.getText()).forEach(id -> {
						engine.executeScript(
								VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
						savedHighlightConfigs.get(selection)
								.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
										new AbstractMap.SimpleEntry<String, String>(highlightColor, highlightBorder)));
					});
				} else {
					model.getTextFieldAndIds(((TextField) controls.get(3)).getText(), secondTextField.getText())
							.forEach(id -> {
								engine.executeScript(
										VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
								savedHighlightConfigs.get(selection)
										.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
												new AbstractMap.SimpleEntry<String, String>(highlightColor,
														highlightBorder)));
							});
				}
			}
		});
		return secondTextField;
	}

	/**
	 * Creates a label with the following label : "Search for following regex/-es
	 * and highlight matches:"
	 * 
	 * @return label
	 */
	private Label createTextFieldLabel() {
		Label textFieldLabel = new Label("Search for following regex/-es and highlight matches:");
		textFieldLabel.setLayoutX(20);
		textFieldLabel.setLayoutY(475);
		return textFieldLabel;
	}

	/**
	 * Creates a toggle-button which hides/shows nodes that are not highlighted in
	 * any way present in the webview. If there are no nodes to be hidden and/or the
	 * toggle-button from the createHideHighlightToggle()-method is selected ->
	 * alerts the user with a message
	 * 
	 * @return toggle-button
	 */
	private ToggleButton createHideNonHighlightToggle() {
		ToggleButton hideNonHighlightToggle = new ToggleButton("Hide non-hightlighted nodes");
		hideNonHighlightToggle.setTooltip(new Tooltip("Hide all nodes which haven't been highlighted"));
		hideNonHighlightToggle.setLayoutX(360);
		hideNonHighlightToggle.setLayoutY(440);
		hideNonHighlightToggle.setOnAction(value -> {
			if (savedHighlightToggle.get(selection) == 0 || savedHighlightToggle.get(selection) == 1) {
				if (hideNonHighlightToggle.isSelected()) {
					if (!savedHighlightConfigs.get(selection).isEmpty()) {
						hideNonHighlightToggle.setText("Show non-hightlighted nodes");
						model.getNonHighlightIds(extractIdList()).forEach(id -> {
							engine.executeScript(VisJsScriptTemplates.hideNode(id));
							savedHideConfigs.get(selection).add(id);
							savedHighlightToggle.replace(selection, 1);
						});
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setHeaderText("Information");
						alert.setContentText("Nothing to hide!");
						alert.show();
						hideNonHighlightToggle.setSelected(false);
					}
				} else {
					hideNonHighlightToggle.setText("Hide non-hightlighted nodes");
					model.getNonHighlightIds(extractIdList()).forEach(id -> {
						engine.executeScript(VisJsScriptTemplates.showNode(id));
						for (int i = 0; i < savedHideConfigs.get(selection).size(); i++) {
							if (savedHideConfigs.get(selection).get(i).equals(id)) {
								savedHideConfigs.get(selection).remove(i);
							}
						}
						savedHighlightToggle.replace(selection, 0);
					});
				}
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Everthing will be hidden away, better only use one toggle!");
				alert.setHeaderText("Information");
				alert.show();
				hideNonHighlightToggle.setSelected(false);
			}
		});
		return hideNonHighlightToggle;
	}

	/**
	 * Creates a button which de-highlights all highlighted nodes present in the
	 * webview
	 * 
	 * @return button
	 */
	private Button createDeHighlightButton() {
		Button deHighlightButton = new Button("De-highlight all");
		deHighlightButton.setTooltip(new Tooltip("De-highlights all nodes"));
		deHighlightButton.setLayoutX(750);
		deHighlightButton.setLayoutY(440);
		deHighlightButton.setOnAction(e -> {
			((ChoiceBox) controls.get(1)).valueProperty().set(null);
			if (((ToggleButton) controls.get(13)).isSelected()) {
				savedHighlightConfigs.get(selection).forEach(entry -> {
					engine.executeScript(VisJsScriptTemplates.showNode(entry.getKey()));
					engine.executeScript(VisJsScriptTemplates.hightlightColorNodes(entry.getKey(),
							entry.getValue().getKey(), entry.getValue().getValue()));
					for (int i = 0; i < savedHideConfigs.get(selection).size(); i++) {
						if (savedHideConfigs.get(selection).get(i).equals(entry.getKey())) {
							savedHideConfigs.get(selection).remove(i);
						}
					}
					savedHighlightToggle.replace(selection, 0);
				});
				((ToggleButton) controls.get(13)).setSelected(false);
			}
			engine.executeScript(VisJsScriptTemplates.deHightlightChoiceNodes(model.getNodeId()));
			savedHighlightConfigs.get(selection).clear();
		});
		return deHighlightButton;
	}

	/**
	 * Creates a color-picker that sets the instance variables highlightColor and
	 * highlightBorder -> Sets the color for highlighting nodes
	 * 
	 * @return color-picker
	 */
	private ColorPicker createColorPicker() {
		ColorPicker colorPicker = new ColorPicker();
		colorPicker.setTooltip(new Tooltip("color-picker \n Pick preferred color for hightlighting"));
		colorPicker.setLayoutX(90);
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
		});
		return colorPicker;
	}

	/**
	 * Creates a table for saving different filters -> a filters that can be saved
	 * consists of the two strings/regex provided by both textfields.
	 * Filters/Highlights all nodes containing both textfield strings/regex
	 * 
	 * @return table
	 */
	private TableView createTable() {
		TableView<FilterWordPair> table = new TableView<FilterWordPair>();
		table.setTooltip(new Tooltip("Saves and-expressions from both textfields for later use." + "\n"
				+ "Highlights and-expression on-click"));
		TableColumn<FilterWordPair, String> firstFilterWordColumn = new TableColumn<FilterWordPair, String>(
				"filter where :");
		firstFilterWordColumn.setCellValueFactory(new PropertyValueFactory<FilterWordPair, String>("firstFilterWord"));
		TableColumn<FilterWordPair, String> secondFilterWordColumn = new TableColumn<FilterWordPair, String>("and :");
		secondFilterWordColumn
				.setCellValueFactory(new PropertyValueFactory<FilterWordPair, String>("secondFilterWord"));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		table.getColumns().addAll(firstFilterWordColumn, secondFilterWordColumn);
		table.setLayoutX(620);
		table.setLayoutY(32);
		table.setRowFactory(tv -> {
			TableRow<FilterWordPair> row = new TableRow<FilterWordPair>();
			row.setOnMouseClicked(e -> {
				model.getTextFieldAndIds(row.getItem().getFirstFilterWord(), row.getItem().getSecondFilterWord())
						.forEach(id -> {
							engine.executeScript(
									VisJsScriptTemplates.hightlightColorNodes(id, highlightColor, highlightBorder));
							savedHighlightConfigs.get(selection)
									.add(new AbstractMap.SimpleEntry<Integer, Map.Entry<String, String>>(id,
											new AbstractMap.SimpleEntry<String, String>(highlightColor,
													highlightBorder)));
						});
			});
			return row;
		});
		return table;
	}

	/**
	 * Creates a button that adds a filter consisting of both textfield
	 * inputs/strings/regex to the table -> Shows an Alert if one of the textfields
	 * is empty
	 * 
	 * @return button
	 */
	private Button createAddToTableButton() {
		Button addToTableButton = new Button("Add filter to table");
		addToTableButton.setTooltip(new Tooltip("Adds the and-expression from both textfields to the table"));
		addToTableButton.setLayoutX(280);
		addToTableButton.setLayoutY(500);
		addToTableButton.setOnAction(e -> {
			if (((TextField) controls.get(3)).getText().isBlank()
					|| ((TextField) controls.get(4)).getText().isBlank()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Please fill in both textfields!");
				alert.setHeaderText("Warning");
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
				((TextField) controls.get(3)).clear();
				((TextField) controls.get(4)).clear();
			}
		});
		return addToTableButton;

	}

	/**
	 * Creates a button that clears the contents of the table
	 * 
	 * @return button
	 */
	private Button createClearTableButton() {
		Button clearTableButton = new Button("Clear AND-table");
		clearTableButton.setTooltip(new Tooltip("Clears all and-expressions from the table"));
		clearTableButton.setLayoutX(420);
		clearTableButton.setLayoutY(500);
		clearTableButton.setOnAction(e -> {
			if (((TableView) controls.get(10)).getItems().isEmpty()) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("There is nothing to clear!");
				alert.setHeaderText("Warning");
				alert.show();
			} else {
				((TableView) controls.get(10)).getItems().clear();
				savedTableConfigs.get(selection).clear();
			}
		});
		return clearTableButton;
	}

	/**
	 * Creates a toggle-button which hides/shows nodes that are currently
	 * highlighted in any way present in the webview. If there are no nodes to be
	 * hidden and/or the toggle-button from the
	 * createHideNonHighlightToggle()-method is selected it -> alerts the user with
	 * a message
	 * 
	 * @return toggle-button
	 */
	private ToggleButton createHideHighlightToggle() {
		ToggleButton hideHighlightToggle = new ToggleButton("Hide hightlighted nodes");
		hideHighlightToggle.setTooltip(new Tooltip("Hide highlighted nodes"));
		hideHighlightToggle.setLayoutX(570);
		hideHighlightToggle.setLayoutY(440);
		hideHighlightToggle.setOnAction(value -> {
			if (savedHighlightToggle.get(selection) == 0 || savedHighlightToggle.get(selection) == 2) {
				if (hideHighlightToggle.isSelected()) {
					if (!savedHighlightConfigs.get(selection).isEmpty()) {
						hideHighlightToggle.setText("Show hightlighted nodes");
						savedHighlightConfigs.get(selection).forEach(entry -> {
							engine.executeScript(VisJsScriptTemplates.hideNode(entry.getKey()));
							savedHideConfigs.get(selection).add(entry.getKey());
							savedHighlightToggle.replace(selection, 2);
						});
					} else {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setContentText("Nothing to hide!");
						alert.setHeaderText("Information");
						alert.show();
						hideHighlightToggle.setSelected(false);
					}
				} else {
					hideHighlightToggle.setText("Hide hightlighted nodes");
					savedHighlightConfigs.get(selection).forEach(entry -> {
						engine.executeScript(VisJsScriptTemplates.showNode(entry.getKey()));
						engine.executeScript(VisJsScriptTemplates.hightlightColorNodes(entry.getKey(),
								entry.getValue().getKey(), entry.getValue().getValue()));
						for (int i = 0; i < savedHideConfigs.get(selection).size(); i++) {
							if (savedHideConfigs.get(selection).get(i).equals(entry.getKey())) {
								savedHideConfigs.get(selection).remove(i);
							}
						}
						savedHighlightToggle.replace(selection, 0);
					});
				}
			} else {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setContentText("Everthing will be hidden away, better only use one toggle!");
				alert.setHeaderText("Information");
				alert.show();
				hideHighlightToggle.setSelected(false);
			}
		});

		return hideHighlightToggle;
	}

	/**
	 * Creates a label with the following label : "Controls :"
	 * 
	 * @return label
	 */
	private Label createControlsLabel() {
		Label controlsLabel = new Label("Controls :");
		controlsLabel.setLayoutX(20);
		controlsLabel.setLayoutY(445);
		return controlsLabel;
	}

	/**
	 * Creates a label with the following label : "Controls for and-expression table
	 * :"
	 * 
	 * @return label
	 */
	private Label createClickToHighlightLabel() {
		Label clickToHighlightLabel = new Label("Controls for the and-expression table :");
		clickToHighlightLabel.setLayoutX(20);
		clickToHighlightLabel.setLayoutY(505);
		return clickToHighlightLabel;
	}

	/**
	 * Creates a button that removes the last item/filter from the table -> alerts
	 * the user if there are no items/filters to remove
	 * 
	 * @return
	 */
	private Button createRemoveLastTableItemButton() {
		Button removeLastTableItemButton = new Button("remove last item from table");
		removeLastTableItemButton.setTooltip(new Tooltip("Removes the last and-expression from the table"));
		removeLastTableItemButton.setLayoutX(550);
		removeLastTableItemButton.setLayoutY(500);
		removeLastTableItemButton.setOnAction(e -> {
			if (!((TableView) controls.get(10)).getItems().isEmpty()) {
				((TableView) controls.get(10)).getItems().remove(((TableView) controls.get(10)).getItems().size() - 1);
				savedTableConfigs.get(selection).clear();
			} else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Nothing to remove!");
				alert.setHeaderText("Warning");
				alert.show();
			}
		});
		return removeLastTableItemButton;
	}

	/**
	 * Creates a label with the following label : "&"
	 * 
	 * @return
	 */
	private Label createAndLabel() {
		Label controlsLabel = new Label("&");
		controlsLabel.setTooltip(new Tooltip(
				"Fill in both textfields and click the 'Add filter to table'-button to add the emerging and-expression to the table"));
		controlsLabel.setLayoutX(555);
		controlsLabel.setLayoutY(478);
		return controlsLabel;
	}

	/**
	 * Creates a list containing all of the node ids from the savedHighlightConfig
	 * 
	 * @return list of ids
	 */
	private List<Integer> extractIdList() {
		List<Integer> idList = new ArrayList<Integer>();
		savedHighlightConfigs.get(selection).forEach(entry -> {
			idList.add(entry.getKey());
		});
		return idList;
	}

	/**
	 * Creates a label with the following label : "and-expression table :"
	 * 
	 * @return label
	 */
	private Label createTableLabel() {
		Label tableLabel = new Label("And-expression table :");
		tableLabel.setLayoutX(660);
		tableLabel.setLayoutY(10);
		return tableLabel;
	}

	/**
	 * Creates a button which creates a message window with a short explanation/help
	 * about the controls
	 * 
	 * @return button
	 */
	private Button createHelpButton() {
		Button helpButton = new Button("Help");
		helpButton.setStyle("-fx-background-color: #A7C7E7");
		helpButton.setLayoutX(820);
		helpButton.setLayoutY(530);
		helpButton.setOnAction(e -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setContentText(
					"You can choose different colors for highlighting from the color-picker. Default is yellow." + "\n"
							+ "Hovering over the controls will give you a short explanation/tooltip.");
			alert.setHeaderText("Need Help ?");
			alert.show();
		});
		return helpButton;
	}

}
