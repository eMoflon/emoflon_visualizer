package api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.emf.common.*;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EcoreFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import com.google.common.collect.HashBiMap;

import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import netscape.javascript.JSObject;

public class Controller implements Initializable {

	@FXML
	private WebView webView = new WebView();
	@FXML
	private TabPane tabPane;
	@FXML
	private DialogPane dialogPane;
	@FXML
	private Tab tabNetworkView;
	@FXML
	private Tab tabOptions;
	@FXML
	private Image image;
	@FXML
	private ToggleButton toggleButtoneclass = new ToggleButton();
	@FXML
	private ToggleButton toggleButtonabstract = new ToggleButton();
	@FXML
	private ToggleButton toggleButtoneenum = new ToggleButton();
	@FXML
	private ToggleButton toggleButtoneinterface = new ToggleButton();

	private WebEngine engine;

	protected JSObject window;

	private ChoiceBox cb = new ChoiceBox();

	Resource r;

	private int nodeIdCounter;
	private int edgeIdCounter;
	private int heridityEdgesCounter;
	private int implementEdgesCounter;

	private EList<EClassifier> elist;
	private ArrayList<String> choiceBoxList;
	private EFactory eFactory;

	protected HashBiMap<String, String> nodesWithIds = HashBiMap.create();
	protected HashBiMap<String, String> nodes = HashBiMap.create();
	protected HashBiMap<String, String> enumNodes = HashBiMap.create();
	protected HashBiMap<String, String> abstractNodes = HashBiMap.create();
	protected HashBiMap<String, String> interfaceNodes = HashBiMap.create();
	protected HashBiMap<String, ArrayList<String>> nodesAttr = HashBiMap.create();

	protected HashBiMap<String, Entry<String, String>> edges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> biDirEdges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> heridityEdges = HashBiMap.create();
	protected HashBiMap<String, Entry<String, String>> implementsEdges = HashBiMap.create();

	/**
	 * Control Class that is being used in JavaFX Scene Builder to control certain
	 * visual elements of the main visualization.
	 */

	public Controller() {
		
		ToggleGroup toggleGroup = new ToggleGroup();
		toggleButtoneclass.setToggleGroup(toggleGroup);
		toggleButtonabstract.setToggleGroup(toggleGroup);
		toggleButtoneenum.setToggleGroup(toggleGroup);
		toggleButtoneinterface.setToggleGroup(toggleGroup);
		// counters for easy debugging -- will be removed later
		nodeIdCounter = 0;
		edgeIdCounter = 0;
		heridityEdgesCounter = 0;
		implementEdgesCounter = 0;
		// get resource from ecore file
		URI fileURI = URI.createFileURI("src/api/HospitalExample.ecore");
		ResourceSet rs = new ResourceSetImpl();
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		rs.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		r = rs.getResource(fileURI, true);
		r.getContents(); // Liste
		EPackage pkg = (EPackage) r.getContents().get(0);
		eFactory = pkg.getEFactoryInstance();
		elist = pkg.getEClassifiers();

		choiceBoxList = new ArrayList(Arrays.asList("EBoolean", "EByte", "EChar", "EDouble", "EFloat", "EInt", "ELong",
				"EShort", "EString", "EJavaObject", "EJavaClass", "EBooleanObject", "EByteObject", "ECharacterObject",
				"EDoubleObject", "EFloatObject", "EIntegerObject", "ELongObject", "EShortObject", "EByteArray"));
		for (String choice : choiceBoxList) {
			cb.getItems().add(choice);
		}
		for (int i = 0; i < elist.size(); i++) {
			cb.getItems().add(elist.get(i).getName());
		}

		ArrayList<String> helpList = new ArrayList<String>();
		for (int i = 0; i < elist.size(); i++) {
			EObject current = elist.get(i);
			current.eClass().getEAllContainments().forEach(ref -> {
				if (ref.getEReferenceType().getName() == "EStructuralFeature") {
					EList<EObject> refList = (EObjectContainmentWithInverseEList) current.eGet(ref);
					for (int j = 0; j < refList.size(); j++) {
						helpList.add("\\n" + "• " + ((EStructuralFeature) refList.get(j)).getEType().getName() + " "
								+ ((EStructuralFeature) refList.get(j)).getName());
					}
					;
				}
			});
			String attrStr = "";
			for (String str : helpList)
				attrStr = attrStr + str;
			if (elist.get(i).eClass().getName() == "EClass") {
				if (((EClass) elist.get(i)).isAbstract() && !((EClass) elist.get(i)).isInterface()) {
					abstractNodes.put(nodeIdCounter + "",
							"<i>Abstract</i>" + "\\n<code>" + elist.get(i).getName() + "</code>\\n" + "--" + attrStr);
					nodesWithIds.put(nodeIdCounter + "", elist.get(i).getName());
					nodeIdCounter++;
				}
				if (((EClass) elist.get(i)).isInterface()) {
					interfaceNodes.put(nodeIdCounter + "",
							"<i>Interface</i>" + "\\n<code>" + elist.get(i).getName() + "</code>\\n" + attrStr);
					nodesWithIds.put(nodeIdCounter + "", elist.get(i).getName());
					nodeIdCounter++;
				}
				if (!((EClass) elist.get(i)).isInterface() && !((EClass) elist.get(i)).isAbstract()) {
					nodes.put(nodeIdCounter + "", "<code>" + elist.get(i).getName() + "</code>\\n" + "--" + attrStr);
					nodesWithIds.put(nodeIdCounter + "", elist.get(i).getName());
					nodeIdCounter++;
				}
			}
			if (elist.get(i).eClass().getName() == "EEnum") {
				String literalStr = "";
				EList<EEnumLiteral> literalList = ((EEnum) elist.get(i)).getELiterals();
				for (EEnumLiteral item : literalList)
					literalStr += "\\n" + item.getName();
				enumNodes.put(elist.get(i).getName() + "", "<i>" + elist.get(i).eClass().getName() + "\\n<code>"
						+ elist.get(i).getName() + "</code>" + "\\n" + literalStr);
				nodesWithIds.put(nodeIdCounter + "", elist.get(i).getName());
				nodeIdCounter++;
			}

			attrStr = "";
			helpList.clear();
		}
		
		ArrayList<String> biDirMergeNames = new ArrayList<>();

		for (int i = 0; i < elist.size(); i++) {
			for (int k = 0; k < elist.size(); k++) {
				if (elist.get(i).eClass().getName() == "EClass" && elist.get(k).eClass().getName() == "EClass"
						&& i != k)
					if (((EClass) elist.get(i)).isSuperTypeOf((EClass) elist.get(k))) {
						if (!((EClass) elist.get(i)).isInterface()) {
							heridityEdges.put(heridityEdgesCounter + "",
									new AbstractMap.SimpleEntry<String, String>(
											nodesWithIds.inverse().get(elist.get(i).getName()),
											nodesWithIds.inverse().get(elist.get(k).getName())));
							heridityEdgesCounter++;
						} else {
							implementsEdges.put(implementEdgesCounter + "",
									new AbstractMap.SimpleEntry<String, String>(
											nodesWithIds.inverse().get(elist.get(i).getName()),
											nodesWithIds.inverse().get(elist.get(k).getName())));
							implementEdgesCounter++;
						}
					}
			}

			EObject current = elist.get(i);
			current.eClass().getEAllContainments().forEach(ref -> {
				if (ref.getEReferenceType().getName() == "EStructuralFeature") {
					EList<EObject> refList = (EObjectContainmentWithInverseEList) current.eGet(ref);
					for (int j = 0; j < refList.size(); j++) {
						helpList.add(((EStructuralFeature) refList.get(j)).getName());
						if (refList.get(j).eClass().getName() == "EReference") {
							EReference er = (EReference) refList.get(j);
							if (checkForDupe(nodesWithIds.inverse().get(er.getEContainingClass().getName()),
									nodesWithIds.inverse().get(er.getEType().getName()))) {
								if (er.getEOpposite() != null) {
									biDirEdges.put(
											er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound(),
											new AbstractMap.SimpleEntry<String, String>(
													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
													nodesWithIds.inverse().get(er.getEType().getName())));
									continue;
								}

								if (!edges.containsKey(
										er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound())) {
									edges.put(er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound(),
											new AbstractMap.SimpleEntry<String, String>(
													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
													nodesWithIds.inverse().get(er.getEType().getName())));
								} else {
									edges.put(
											er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound()
													+ " ",
											new AbstractMap.SimpleEntry<String, String>(
													nodesWithIds.inverse().get(er.getEContainingClass().getName()),
													nodesWithIds.inverse().get(er.getEType().getName())));

								}
							} else {
								biDirMergeNames
										.add(er.getName() + "   " + er.getLowerBound() + "..." + er.getUpperBound());
							}
						}
					}
				}
				;
			}

			);
		}
		biDirNamesMerge(biDirMergeNames);
	}
	
	private void testingNewUniversalEdges() {
		elist.forEach(current ->{
			
		});
	}
	
	private String changeNodes() {
		ArrayList<String> list = new ArrayList<>();
		nodes.values().forEach(current -> {list.add(current);});
		String str = "[";
		for(int i = 0; i < list.size();i++) {
			str += "{ id: \""+ i + "\",font: { multi: true }, label: \"" + list.get(i) + "\", shape: \"box\", color: { background: \"#f9de8b\",border: \"black\"}},";
		}
		return str +"]";
	}


	/**
	 * Called to initialize a controller after its root element has been completely
	 * processed.
	 * 
	 * @param arg0 The location used to resolve relative paths for the root object,
	 *             or null if the location is not known.
	 * @param arg1 The resources used to localize the root object, or null if the
	 *             root object was not localized. try { setEdges(setNodes());
	 * 
	 *             } catch (Exception e) { // TODO Auto-generated catch block
	 *             e.printStackTrace(); }
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		String n = "{}";
		engine = webView.getEngine();
		engine.setJavaScriptEnabled(true);
		engine.loadContent(VisJsAdapter.getJSTemplate());
		webView.setVisible(true);
		engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == Worker.State.SUCCEEDED) {
				window = (JSObject) engine.executeScript("window");
				window.setMember("jfx", this);
				engine.executeScript(VisJsAdapter.addNodes(nodes));
				engine.executeScript(VisJsAdapter.addAbstractNodes(abstractNodes));
				engine.executeScript(VisJsAdapter.addEnumNodes(enumNodes));
				engine.executeScript(VisJsAdapter.addInterfaceNodes(interfaceNodes));
				engine.executeScript(VisJsAdapter.addEdges(edges));
				engine.executeScript(VisJsAdapter.addBidirectionalEdges(biDirEdges));
				engine.executeScript(VisJsAdapter.addHeridityEdges(heridityEdges));
				engine.executeScript(VisJsAdapter.addImplementsEdges(implementsEdges));
				engine.executeScript("var network = new vis.Network(container,data, options);");
			}
		});

		// Add node click
		webView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				ToggleGroup toggleGroup = new ToggleGroup();
				toggleButtoneclass.setToggleGroup(toggleGroup);
				toggleButtonabstract.setToggleGroup(toggleGroup);
				toggleButtoneenum.setToggleGroup(toggleGroup);
				toggleButtoneinterface.setToggleGroup(toggleGroup);
				ToggleButton selectedToggleButton = (ToggleButton) toggleGroup.getSelectedToggle();
				ArrayList<String> attrList = new ArrayList<String>();
				String resultForJS = "";
				TextInputDialog d = new TextInputDialog();
				d.setGraphic(new ImageView(this.getClass().getResource("emoflon.jpg").toString()));
				switch (selectedToggleButton.getText()) {
				case "Add EClass node": {
					d.setTitle("Add EClass");
					d.setHeaderText("Add following EClass-Node to the network");
					d.setContentText("Label:");
					EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
					EClass eclass = ecoreFactory.createEClass();
					Optional<String> result = d.showAndWait();
					cb.getItems().add(result.get());
					eclass.setName(result.get());
					if (result.isPresent()) {
						TextInputDialog d2 = new TextInputDialog();
						d2.setTitle("Add Attributes of EClass Object");
						d2.setHeaderText("How many Attributes would you like to add ?");
						d2.setContentText("amount:");
						d2.setGraphic(new ImageView(this.getClass().getResource("emoflon.jpg").toString()));
						int amountOfAttributes = Integer.parseInt(d2.showAndWait().get());
						TextInputDialog d3 = new TextInputDialog();
						Dialog d4 = new Dialog();
						String choiceBoxResult = "";
						for (int i = amountOfAttributes; i > 0; i--) {
							d4.getDialogPane().setContent(createChoiceBox());
							d4.getDialogPane().getButtonTypes().add(ButtonType.OK);
							d4.showAndWait();
							choiceBoxResult = (String) cb.getValue();
							d3.setTitle("Add Attributes of EClass Object");
							d3.setHeaderText("Add following Attributes to the EClass Object");
							d3.setContentText("Attribute-name:");
							Optional<String> result2 = d3.showAndWait();

							attrList.add("\\n" + "• " + choiceBoxResult + " " + result2.get() + " ");
							d3.getEditor().clear();
							EStructuralFeature feature = choiceToDatatype(choiceBoxResult);
							feature.setName(result2.get());
							eclass.getEStructuralFeatures().add(feature);
						}
						String lstToStr = "";
						for (String string : attrList) {
							lstToStr += string;
						}
						engine.executeScript(VisJsAdapter.addNode(nodeIdCounter + "",
								"<code>" + result.get() + "</code>\\n" + "\\n---" + lstToStr));
						engine.executeScript("var network = new vis.Network(container, data, options);");
						elist.add(eclass);
						nodeIdCounter++;
					} else {

					}

					break;
				}
				case "Add abstract EClass node": {
					d.setTitle("Add abstract EClass");
					d.setHeaderText("Add following abstract EClass-Node to the network");
					d.setContentText("Label:");
					EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
					EClass eclass = ecoreFactory.createEClass();
					eclass.setAbstract(true);
					Optional<String> result = d.showAndWait();
					cb.getItems().add(result.get());
					eclass.setName(result.get());
					if (result.isPresent()) {
						TextInputDialog d2 = new TextInputDialog();
						d2.setTitle("Add Attributes of EClass Object");
						d2.setHeaderText("How many Attributes would you like to add ?");
						d2.setContentText("amount:");
						d2.setGraphic(new ImageView(this.getClass().getResource("emoflon.jpg").toString()));
						int amountOfAttributes = Integer.parseInt(d2.showAndWait().get());
						TextInputDialog d3 = new TextInputDialog();
						Dialog d4 = new Dialog();
						for (int i = amountOfAttributes; i > 0; i--) {
							d4.getDialogPane().setContent(createChoiceBox());
							d4.getDialogPane().getButtonTypes().add(ButtonType.OK);
							d4.showAndWait();
							String choiceBoxResult = (String) cb.getValue();
							d3.setTitle("Add Attributes of abstract EClass Object");
							d3.setHeaderText("Add following Attributes to the abstract EClass Object");
							d3.setContentText("Attribute-name:");
							Optional<String> result2 = d3.showAndWait();

							attrList.add("\\n" + "• " + choiceBoxResult + " " + result2.get() + " ");
							d3.getEditor().clear();
							EStructuralFeature feature = choiceToDatatype(choiceBoxResult);
							feature.setName(result2.get());
							eclass.getEStructuralFeatures().add(feature);
						}
						String lstToStr = "";
						for (String string : attrList) {
							lstToStr += string;
						}
//						elist.get(elist.size() - 1).setName(result.get());
						engine.executeScript(VisJsAdapter.addAbstractNode(nodeIdCounter + "",
								"<i>Abstract</i>" + "\\n<code>" + result.get() + "</code>\\n" + "\\n---" + lstToStr));
						engine.executeScript("var network = new vis.Network(container, data, options);");
						elist.add(eclass);
						nodeIdCounter++;
					} else {

					}

					break;
				}
				case "Add EInterface node": {
					d.setTitle("Add EClass");
					d.setHeaderText("Add following EInterface-Node to the network");
					d.setContentText("Label:");
					EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
					EClass eclass = ecoreFactory.createEClass();
					eclass.setInterface(true);
					Optional<String> result = d.showAndWait();
					cb.getItems().add(result.get());
					eclass.setName(result.get());
					if (result.isPresent()) {
						TextInputDialog d2 = new TextInputDialog();
						d2.setTitle("Add Attributes of EInterface Object");
						d2.setHeaderText("How many Attributes would you like to add ?");
						d2.setContentText("amount:");
						d2.setGraphic(new ImageView(this.getClass().getResource("emoflon.jpg").toString()));
						int amountOfAttributes = Integer.parseInt(d2.showAndWait().get());
						TextInputDialog d3 = new TextInputDialog();
						Dialog d4 = new Dialog();
						for (int i = amountOfAttributes; i > 0; i--) {
							d4.getDialogPane().setContent(createChoiceBox());
							d4.getDialogPane().getButtonTypes().add(ButtonType.OK);
							d4.showAndWait();
							String choiceBoxResult = (String) cb.getValue();
							d3.setTitle("Add Attributes of EInterface Object");
							d3.setHeaderText("Add following Attributes to the EInterace Object");
							d3.setContentText("Attribute-name:");
							Optional<String> result2 = d3.showAndWait();

							attrList.add("\\n" + "• " + choiceBoxResult + " " + result2.get() + " ");
							d3.getEditor().clear();
							EStructuralFeature feature = choiceToDatatype(choiceBoxResult);
							feature.setName(result2.get());
							eclass.getEStructuralFeatures().add(feature);
						}
						String lstToStr = "";
						for (String string : attrList) {
							lstToStr += string;
						}
						engine.executeScript(VisJsAdapter.addInterfaceNode(nodeIdCounter + "",
								"<i>Interface</i>" + "\\n<code>" + result.get() + "</code>\\n" + "\\n---" + lstToStr));
						engine.executeScript("var network = new vis.Network(container, data, options);");
						elist.add(eclass);
						nodeIdCounter++;
					} else {

					}

					break;
				}
				case "Add EEnum node": {
					d.setTitle("Add EEnum");
					d.setHeaderText("Add following EEnumNode to the network");
					d.setContentText("Label:");
					EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
					EEnum eenum = ecoreFactory.createEEnum();
					Optional<String> result = d.showAndWait();
					cb.getItems().add(result.get());
					eenum.setName(result.get());
					if (result.isPresent()) {
						TextInputDialog d2 = new TextInputDialog();
						d2.setTitle("Add Attributes of EEnum Object");
						d2.setHeaderText("How many ELiterals would you like to add ?");
						d2.setContentText("amount:");
						d2.setGraphic(new ImageView(this.getClass().getResource("emoflon.jpg").toString()));
						int amountOfAttributes = Integer.parseInt(d2.showAndWait().get());
						TextInputDialog d3 = new TextInputDialog();
						for (int i = amountOfAttributes; i > 0; i--) {
							d3.setTitle("Add ELiterals to the EEnum");
							d3.setHeaderText("Add following ELiterals to the EEnum Object");
							d3.setContentText("ELiteral-name:");
							Optional<String> result2 = d3.showAndWait();
							attrList.add(result2.get().toUpperCase());
							d3.getEditor().clear();
						}
						String lstToStr = "";
						int j = 0;
						for (String string : attrList) {
							lstToStr += "\\n" + string;
							EEnumLiteral eliteral = ecoreFactory.createEEnumLiteral();
							eliteral.setName(string);
							eliteral.setValue(j);
							eenum.getELiterals().add(eliteral);
							j++;
						}
						engine.executeScript(VisJsAdapter.addEnumNode(nodeIdCounter + "",
								"<i>EEnum</i>" + "\\n<code>" + result.get() + "</code>\\n" + "\\n---" + lstToStr));
						engine.executeScript("var network = new vis.Network(container, data, options);");
						elist.add(eenum);
						nodeIdCounter++;
					} else {

					}

					break;
				}
				default:
					break;
				}
				try {
					r.save(null);
				} catch (IOException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("done");
			}
		});
	}

	private EStructuralFeature choiceToDatatype(String choice) {
		EcoreFactory ecoreFactory = EcoreFactory.eINSTANCE;
		EStructuralFeature feature = ecoreFactory.createEAttribute();
		switch (choice) {
		case "EBoolean": {
			feature.setEType(EcorePackage.Literals.EBOOLEAN);
			break;
		}
		case "EByte": {
			feature.setEType(EcorePackage.Literals.EBYTE);
			break;
		}
		case "EChar": {
			feature.setEType(EcorePackage.Literals.ECHAR);
			break;
		}
		case "EDouble": {
			feature.setEType(EcorePackage.Literals.EDOUBLE);
			break;
		}
		case "EFloat": {
			feature.setEType(EcorePackage.Literals.EFLOAT);
			break;
		}
		case "EInt": {
			feature.setEType(EcorePackage.Literals.EINT);
			break;
		}
		case "ELong": {
			feature.setEType(EcorePackage.Literals.ELONG);
			break;
		}
		case "EShort": {
			feature.setEType(EcorePackage.Literals.ESHORT);
			break;
		}
		case "EString": {
			feature.setEType(EcorePackage.Literals.ESTRING);
			break;
		}
		case "EJavaObject": {
			feature.setEType(EcorePackage.Literals.EJAVA_OBJECT);
			break;
		}
		case "EJavaClass": {
			feature.setEType(EcorePackage.Literals.EJAVA_CLASS);
			break;
		}
		case "EBooleanObject": {
			feature.setEType(EcorePackage.Literals.EBOOLEAN_OBJECT);
			break;
		}
		case "EByteObject": {
			feature.setEType(EcorePackage.Literals.EBYTE_OBJECT);
			break;
		}
		case "ECharacterObject": {
			feature.setEType(EcorePackage.Literals.ECHARACTER_OBJECT);
			break;
		}
		case "EDoubleObject": {
			feature.setEType(EcorePackage.Literals.EDOUBLE_OBJECT);
			break;
		}
		case "EFLoatObject": {
			feature.setEType(EcorePackage.Literals.EFLOAT_OBJECT);
			break;
		}
		case "EIntegerObject": {
			feature.setEType(EcorePackage.Literals.EINTEGER_OBJECT);
			break;
		}
		case "ELongObject": {
			feature.setEType(EcorePackage.Literals.ELONG_OBJECT);
			break;
		}
		case "EShortObject": {
			feature.setEType(EcorePackage.Literals.ESHORT_OBJECT);
			break;
		}
		case "EByteArray": {
			feature.setEType(EcorePackage.Literals.EBYTE_ARRAY);
			break;
		}
		default: {
			feature = ecoreFactory.createEReference();
			for (EClassifier eitem : elist) {
				if (choice == eitem.getName())
					feature.setEType(eitem);
				break;
			}
		}
		}
		return feature;
	}

	private boolean checkForDupe(String str1, String str2) {
		if (biDirEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(str1, str2))
				|| biDirEdges.containsValue(new AbstractMap.SimpleEntry<String, String>(str2, str1))) {
			return false;
		} else
			return true;
	}

	private void biDirNamesMerge(ArrayList<String> mergeList) {
		int i = 0;
		for (Entry<Entry<String, String>, String> entry : biDirEdges.inverse().entrySet()) {
			String value = entry.getValue();
			entry.setValue(value + "\\n" + mergeList.get(i));
			i++;
		}
	}

	private Node createChoiceBox() {
		GridPane gridPane = new GridPane();
		gridPane.add(cb, 0, 0);
		return gridPane;
	}

}