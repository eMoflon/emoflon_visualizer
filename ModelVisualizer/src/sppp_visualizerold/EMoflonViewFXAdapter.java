package sppp_visualizerold;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;


public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {
    private FXCanvas fxCanvas;
	private Thread visualizerThread;
	
	public EMoflonViewFXAdapter() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection) {
		System.out.println("supportsSelection");
		return true;
	}

	@Override
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection) {
		System.out.println("renderView");
		return true;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		System.out.println("createPartControl");

		fxCanvas = new FXCanvas(parent, SWT.NONE);

        // set scene
		Group group = new Group();
        Scene view_scene = new Scene(group);
        Button button = new Button("JFX ButtoSn");
        group.getChildren().add(button);
        fxCanvas.setScene(view_scene);
	}

}
