package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import api.VisJsAdapter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.*;

public class VisFXController implements Initializable {

	@FXML
	private WebView webView;

	private WebEngine engine;

	public VisFXController(WebView webView) {
		this.webView = webView;
		engine = webView.getEngine();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
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
//								elist.get(elist.size() - 1).setName(result.get());
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
}
