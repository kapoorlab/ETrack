package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mpicbg.imglib.image.display.Display;
import pluginTools.InteractiveEmbryo;
import utility.DisplayAuto;

public class ClearDisplayListener implements ActionListener {

	
	final InteractiveEmbryo parent;

	public ClearDisplayListener(final InteractiveEmbryo parent) {

		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String uniqueID = Integer.toString(parent.thirdDimension) ;
		
		
		// Remove the current rois
		parent.ZTRois.remove(uniqueID);
		DisplayAuto.Display(parent);
		parent.imp.updateAndDraw();
		
	}
	
	
	
	
	
}
