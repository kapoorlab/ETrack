package curvatureComputer;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import pluginTools.EmbryoTrack;
import pluginTools.InteractiveEmbryo;

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
					"Curvature computed for all Cells present at  " + parent.thirdDimension);

        
	}

	
	

}