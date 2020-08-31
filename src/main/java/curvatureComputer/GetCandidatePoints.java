package curvatureComputer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.logic.BitType;
import net.imglib2.view.Views;
import pluginTools.InteractiveEmbryo;
import net.imagej.ops.OpService;

public class GetCandidatePoints {
	
	static double fcteps = 1.0E-10;
	
	public static List<RealLocalizable> ListofPoints(final InteractiveEmbryo parent, final RandomAccessibleInterval<BitType> ActualRoiimg ,final JProgressBar jpb, int percent, int t) {
		
		parent.Listmap.clear();

			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()+ fcteps),
					"Computing Curvature T = " + t + "/" + parent.thirdDimensionSize);
		
		List<RealLocalizable> candidates = GetCoordinatesBit(parent,ActualRoiimg);
	
		return candidates;
	}


	public static ArrayList<RealLocalizable> GetCoordinatesBit(InteractiveEmbryo parent,
			RandomAccessibleInterval<BitType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();



		

		// Thin Embryo borders
				OpService ops = parent.ij.op();

				actualRoiimg = ops.morphology().thinZhangSuen(actualRoiimg);
				
				final Cursor<BitType> center = Views.iterable(actualRoiimg).localizingCursor();
		

		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if(center.get().getInteger() > 0) {
				coordinatelist.add(rpos);
			}
			
		}
		
	
	
		
		return coordinatelist;
	}

}
