package curvatureComputer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import embryoDetector.Cellobject;
import embryoDetector.Embryoobject;
import net.imglib2.RealLocalizable;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.EmbryoTrack;
import pluginTools.InteractiveEmbryo;
import utility.Listordereing;

public class ComputeCurvatureCurrent extends SwingWorker<Void, Void> {

	final InteractiveEmbryo parent;
	final JProgressBar jpb;

	public ComputeCurvatureCurrent(final InteractiveEmbryo parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}

	@Override
	protected Void doInBackground() throws Exception {

		String uniqueID = Integer.toString(parent.thirdDimension);

		if (parent.AllEmbryos.get(uniqueID) != null) {

			ArrayList<Cellobject> CurrentEmbryo = parent.AllEmbryos.get(uniqueID);

			for (Cellobject currentcell : CurrentEmbryo) {

				int cellid = currentcell.ID;

				ArrayList<Embryoobject> Embryocell = currentcell.cell;

				for (Embryoobject cell : Embryocell) {

					ArrayList<RealLocalizable> cellist = cell.pointlist;
					RealLocalizable minCord = Listordereing.getMinCord(cellist);
					RealLocalizable refcord = minCord;
					Pair<RealLocalizable, List<RealLocalizable>> Ordered = new ValuePair<RealLocalizable, List<RealLocalizable>>(
							refcord, cellist);
					DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

				}
			}

		}

		else {

			EmbryoTrack newtrack = new EmbryoTrack(parent, jpb);
			newtrack.ComputeCurvatureCurrent();

		}
		return null;

	}

	@Override
	protected void done() {

		parent.jpb.setIndeterminate(false);
		if (parent.jpb != null)
			utility.ProgressBar.SetProgressBar(parent.jpb, 100,
					"Curvature computed for all Cells present at  " + parent.thirdDimension);

	}

}