package curvatureComputer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import ij.ImageStack;
import ij.gui.Line;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.EmbryoTrack;
import pluginTools.InteractiveEmbryo;
import utility.CreateTable;
import utility.Curvatureobject;
import utility.Roiobject;

public class ComputeCurvatureCurrent extends SwingWorker<Void, Void> {

	final InteractiveEmbryo parent;
	final JProgressBar jpb;

	public ComputeCurvatureCurrent(final InteractiveEmbryo parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {



		EmbryoTrack newtrack = new EmbryoTrack(parent, jpb);
		newtrack.ComputeCurvatureCurrent();
		
		return null;

	}



	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		if(parent.jpb!=null )
			utility.ProgressBar.SetProgressBar(parent.jpb, 100 ,
					"Curvature computed for all Embryos present at  " + parent.thirdDimension);

        
	}

	
	

}