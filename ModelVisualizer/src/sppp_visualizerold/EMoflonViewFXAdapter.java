package sppp_visualizerold;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.IWorkbenchPart;
import org.moflon.core.ui.EMoflonView;
import org.moflon.core.ui.visualisation.common.EMoflonViewVisualizer;

import javafx.application.Application;
import javafx.embed.swt.FXCanvas;


public class EMoflonViewFXAdapter implements EMoflonViewVisualizer {

	private Thread visualizerThread;
	
	public EMoflonViewFXAdapter() {
		// TODO Auto-generated constructor stub
	}
	

	@Override
	public boolean supportsSelection(IWorkbenchPart part, ISelection selection) {
		System.out.println("success");
		return true;
	}

	@Override
	public boolean renderView(EMoflonView emoflonView, IWorkbenchPart part, ISelection selection) {
		System.out.println("success");
		
		if(visualizerThread == null) {
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					Application.launch(FXMLWindow.class);
				}
			};
			visualizerThread = new Thread(runnable);
			visualizerThread.start();
			FXMLWindow.registerParentComposite(emoflonView.getParentComposite());
		}
		return true;
	}

}
