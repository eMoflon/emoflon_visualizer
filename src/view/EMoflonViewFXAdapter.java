package view;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import com.google.common.collect.HashBiMap;

import controller.VisFXController;
import javafx.embed.swt.FXCanvas;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

import java.util.AbstractMap;
import java.util.ArrayList;
import javafx.scene.control.ChoiceBox;
import model.ModelHandler;
import model.ModelRecognizer;
import model.VisJsScriptTemplates;
import javafx.scene.web.WebEngine;
import javafx.scene.layout.StackPane;

public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {
	private ModelHandler model;
	private VisFXController controller;

	/**
	 * Manages the visualisation of the eMoflon-view
	 * 
	 * @author maximiliansell
	 */
	public EMoflonViewFXAdapter() {

	}

	/**
	 * Checks whether the current selection is supported by the visualisation
	 * 
	 * @param part
	 * @param selection - selection to be checked
	 * @return true if selection is supported, false if not
	 */
	@Override
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection) {
		System.out.println("supportsSelection");
		return ModelRecognizer.identifySelection(selection);
	}

	/**
	 * Renders the current supported selection into the eMoflon-view
	 * 
	 * @param emoflonView - window that houses the visualisation
	 * @param part
	 * @param selection   - selection to be visualised
	 * @return true if the visualisation was succesful, false if not
	 */
	@Override
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection) {
		System.out.println("renderView");
		model = ModelRecognizer.identifyModel(selection);
		controller.selectionToModel(model, selection);
		return ModelRecognizer.isModelViewable(selection);
	}

	/**
	 * Creates the controls of the visualisation into the emoflon-view
	 * 
	 * @param parent - parent of the embedded controls
	 */
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("createPartControl");
		controller = new VisFXController(parent);
		controller.initialize();
	}
}
