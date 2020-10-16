package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import curvatureComputer.ComputeCurvature;
import pluginTools.InteractiveEmbryo;

public class CurvatureListener implements ActionListener {

	final InteractiveEmbryo parent;
	

	public CurvatureListener(final InteractiveEmbryo parent) {

		this.parent = parent;
	}

	// For curvatrue
	@Override
	public void actionPerformed(ActionEvent e) {

		
		ClearStuff();
		ComputeCurvature display = new ComputeCurvature(this.parent, parent.jpb);
		display.execute();

	}
	
	public  void ClearStuff() {
		
		parent.table.removeAll();
		parent.table.repaint();
		parent.overlay.clear();
		parent.Tracklist.clear();
		if(parent.imp!=null && parent.mvl!=null)
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
		if(parent.imp!=null && parent.ml!=null)
		parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		parent.starttime = Integer.parseInt(parent.startT.getText());
		parent.endtime = Integer.parseInt(parent.endT.getText());
		parent.resolution = 1; //Integer.parseInt(parent.resolutionField.getText());
		parent.insidedistance =  Integer.parseInt(parent.interiorfield.getText());
		parent.displayIntermediate = false;
		parent.displayIntermediateBox = false;
		parent.overlay.clear();
		parent.AutostartTime = Integer.parseInt(parent.startT.getText());
		if (parent.AutostartTime <= 0)
			parent.AutostartTime = 1;
		parent.AutoendTime = Integer.parseInt(parent.endT.getText());
		
	}

}
