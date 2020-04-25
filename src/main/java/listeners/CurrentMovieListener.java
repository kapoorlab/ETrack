package listeners;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ij.IJ;
import ij.WindowManager;
import pluginTools.EmbryoFileChooser;

public class CurrentMovieListener implements ActionListener {

	final EmbryoFileChooser parent;

	public CurrentMovieListener(EmbryoFileChooser parent) {

		this.parent = parent;

	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {

		parent.impOrig = WindowManager.getCurrentImage();
		
		if(parent.impOrig!=null)
		parent.DoneCurr(parent.Cardframe);
	}

}
