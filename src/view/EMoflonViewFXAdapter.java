package view;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import controller.VisFXController;

import model.ModelHandler;
import model.ModelRecognizer;

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
