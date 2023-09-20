package model;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.moflon.core.ui.VisualiserUtilities;

/**
 * Helper-class for the EMoflonViewFXAdapter that handles the selection and its
 * "model-type"
 *
 * @author maximiliansell
 *
 */
public class ModelRecognizer {
	/**
	 * Checks with the help of the VisualiserUtilities-class whether the given
	 * selection is supported by the visualisation
	 * 
	 * @param selection - selection to be checked
	 * @return true if selection is supported, false if not
	 */
	public static boolean identifySelection(ISelection selection) {
		// check whether the selection contains an ecore selection

		if (VisualiserUtilities.isEcoreSelection(selection)) {
			Collection<EObject> elist = VisualiserUtilities.extractEcoreSelection(selection);
			// check whether the extracted collection has metamodel elements
			if (VisualiserUtilities.hasMetamodelElements(elist)) {
				return true;
			} else {
				// check whether the extracted collection has model elements
				if (VisualiserUtilities.hasModelElements(elist)) {
					return true;
				} else {
					System.out.println("Error : Selection cannot be visualized yet");
					return false;
				}
			}
		} else {
			System.out.println("Error : Selection doesn't contain an Ecore selection");
			return false;
		}
	}

	/**
	 * Identifies with the help of the VisualiserUtilities-class the type of
	 * emf-model that the selection contains
	 * 
	 * @param selection - selection to be identified
	 * @return type of model of the selection
	 */
	public static ModelHandler identifyModel(ISelection selection) {
		Collection<EObject> elist = VisualiserUtilities.extractEcoreSelection(selection);
		ModelHandler model = null;
		if (VisualiserUtilities.hasMetamodelElements(elist)) {
			model = new MetaModelHandler(elist);
		}
		if (VisualiserUtilities.hasModelElements(elist)) {
			model = new XMIModelHandler(elist);
		}
		return model;
	}

	/**
	 * Identifies with the help of the VisualiserUtilities-class if the model can be
	 * visualised by the current implementation
	 * 
	 * @param selection - selection to be identified
	 * @return whether the selection is viewable
	 */
	public static boolean isModelViewable(ISelection selection) {
		Collection<EObject> elist = VisualiserUtilities.extractEcoreSelection(selection);
		if (VisualiserUtilities.hasMetamodelElements(elist)) {
			return true;
		}
		if (VisualiserUtilities.hasModelElements(elist)) {
			return true;
		}
		return false;
	}

}
