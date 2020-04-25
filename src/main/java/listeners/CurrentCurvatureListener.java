package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ellipsoidDetector.Intersectionobject;
import ij.IJ;
import kalmanForSegments.Segmentobject;
import net.imglib2.img.display.imagej.ImageJFunctions;
import pluginTools.InteractiveEmbryo;

public class CurrentCurvatureListener implements ActionListener {

	final InteractiveEmbryo parent;

	public CurrentCurvatureListener(final InteractiveEmbryo parent) {

		this.parent = parent;
	}

	// For curvatrue current
	@Override
	public void actionPerformed(ActionEvent e) {

		
	
		
			parent.StartCurvatureComputingCurrent();

	}
	
	public  void ClearStuff() {
		
		parent.table.removeAll();
		parent.table.repaint();
		parent.localCurvature.clear();
		parent.AlllocalCurvature.clear();
		parent.overlay.clear();
		parent.Tracklist.clear();
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
		parent.imp.getCanvas().removeMouseMotionListener(parent.ml);
		
		
		parent.displayCircle.setState(false);
		parent.displaySegments.setState(false);
		parent.displayIntermediate = false;
		parent.displayIntermediateBox = false;
		parent.parentgraph = new SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>(
				DefaultWeightedEdge.class);
		parent.parentgraphZ = new HashMap<String, SimpleWeightedGraph<Intersectionobject, DefaultWeightedEdge>>();
		parent.empty = utility.Binarization.CreateBinaryBit(parent.originalimg, parent.lowprob, parent.highprob);
		parent.parentgraphSegZ = new HashMap<String, SimpleWeightedGraph<Segmentobject, DefaultWeightedEdge>>();
		parent.ALLSegments.clear();
		parent.SegmentFinalresult.clear();
		parent.overlay.clear();
		parent.imp.getCanvas().removeMouseListener(parent.mvl);
	}

}
