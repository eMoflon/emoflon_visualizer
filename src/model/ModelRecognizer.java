package model;

import java.util.Collection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.moflon.core.ui.VisualiserUtilities;

public class ModelRecognizer {
	/**
	 * 
	 * @param selection
	 * @return
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

	public static ModelHandler getModel(ISelection selection) {
		return identifyModel(selection);
	}
}
