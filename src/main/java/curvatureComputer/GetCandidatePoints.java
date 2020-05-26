package curvatureComputer;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEmbryo;

public class GetCandidatePoints {
	
	static double fcteps = 1.0E-10;
	
	public static List<RealLocalizable> ListofPoints(final InteractiveEmbryo parent, final RandomAccessibleInterval<FloatType> ActualRoiimg ,final JProgressBar jpb, int percent, int t) {
		
		parent.Listmap.clear();

			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()+ fcteps),
					"Computing Curvature T = " + t + "/" + parent.thirdDimensionSize);
		
		List<RealLocalizable> candidates = GetCoordinatesBit(ActualRoiimg);
	
		return candidates;
	}
	public static <T extends Comparable<T>> ArrayList<Pair<RealLocalizable, T>> GetCoordinates(
			RandomAccessibleInterval<T> source, final T threshold) {

		ArrayList<Pair<RealLocalizable, T>> coordinatelist = new ArrayList<Pair<RealLocalizable, T>>();

		Interval interval = Intervals.expand(source, -1);
		int ndims = source.numDimensions();
		source = Views.interval(source, interval);

		final Cursor<T> center = Views.iterable(source).localizingCursor();

		while(center.hasNext()) {
			
			final T centerValue = center.next();
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if (centerValue.compareTo(threshold) > 0) 
				coordinatelist.add(new ValuePair<RealLocalizable, T>(rpos, centerValue));
			
		}
		

		return coordinatelist;
	}

	public static ArrayList<RealLocalizable> GetCoordinatesBit(
			RandomAccessibleInterval<FloatType> actualRoiimg) {

		ArrayList<RealLocalizable> coordinatelist = new ArrayList<RealLocalizable>();
		int ndims = actualRoiimg.numDimensions();



		final Cursor<FloatType> center = Views.iterable(actualRoiimg).localizingCursor();

		

		while(center.hasNext()) {
			
			center.fwd();
			
			double[] posf = new double[ndims];
			center.localize(posf);
			final RealPoint rpos = new RealPoint(posf);
			if(center.get().get() > 0) {
				coordinatelist.add(rpos);
			}
			
		}
		
	
	
		
		return coordinatelist;
	}

}
