package embryoDetector;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import ij.gui.EllipseRoi;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Embryoobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Embryoobject> {
	
	
	public final long[] Location;
	public final long[] center;
	public final ArrayList<RealLocalizable> pointlist;
	public final double perimeter;
    public final int t;
    public final int Label;
	public final ArrayList<LineProfileCircle> LineScanIntensity;
	public final double CircleCurvature;
	public final double DistCurvature;
	public final double IntensityA;
	public final double IntensityB;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	
	public Embryoobject(final long[] Location, 
			final long[] center,
			ArrayList<LineProfileCircle> LineScanIntensity, 
			ArrayList<RealLocalizable> pointlist, 
			final double CircleCurvature,
			final double DistCurvature,
			final double IntensityA,
			final double IntensityB,
			final double perimeter,
			final int Label,
			final int t) {
		
		
		super(3);
		this.Location = Location;
		this.center = center;
		this.pointlist = pointlist;
		this.CircleCurvature = CircleCurvature;
		this.DistCurvature = DistCurvature;
		this.IntensityA = IntensityA;
		this.IntensityB = IntensityB;
		this.perimeter = perimeter;
		this.LineScanIntensity = LineScanIntensity;
		this.t = t;
		this.Label = Label;

		putFeature(POSITION_X, (double)Location[0]);
		putFeature(POSITION_Y, (double)Location[1]);
		putFeature(POSITION_T, (double)t);
		putFeature(CENTER_X, (double)Location[0]);
		putFeature(CENTER_Y, (double)Location[1]);
		putFeature(CIRCLECurv, CircleCurvature);
		putFeature(DISTCurv, DistCurvature);
		putFeature(INTENSITYA, IntensityA);
		putFeature(INTENSITYB, IntensityB);
	}


	public static final String POSITION_X = "POSITION_X";

	public static final String POSITION_Y = "POSITION_Y";
	public static final String CENTER_X = "CENTER_X";

	public static final String CENTER_Y = "CENTER_Y";
	
	public static final String POSITION_T = "POSITION_T";
	
	public static final String CIRCLECurv = "CIRCLECurv";

	public static final String DISTCurv = "DISTCurv";
	
	
	public static final String INTENSITYA = "INTENSITYA";

	public static final String INTENSITYB = "INTENSITYB";
	
	
	public final static String[] POSITION_FEATURES = new String[] { POSITION_X, POSITION_Y, CENTER_X, CENTER_Y };
	
	static int numfeatures = 9;
	
	public final static Collection<String> FEATURES = new ArrayList<>(numfeatures);

	public final static Map<String, String> FEATURE_NAMES = new HashMap<>(numfeatures);

	public final static Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(numfeatures);

	public final static Map<String, EmbryoDimension> FEATURE_EmbryoDIMENSIONS = new HashMap<>(numfeatures);

	public final static Map<String, Boolean> IS_INT = new HashMap<>(numfeatures);

	static {
		FEATURES.add(POSITION_X);
		FEATURES.add(POSITION_Y);
		FEATURES.add(CENTER_X);
		FEATURES.add(CENTER_Y);
		FEATURES.add(POSITION_T);
		FEATURES.add(CIRCLECurv);
		FEATURES.add(DISTCurv);
		FEATURES.add(INTENSITYA);
		FEATURES.add(INTENSITYB);

		FEATURE_NAMES.put(POSITION_X, "X");
		FEATURE_NAMES.put(POSITION_Y, "Y");
		FEATURE_NAMES.put(CENTER_X, "X");
		FEATURE_NAMES.put(CENTER_Y, "Y");
		
		FEATURE_NAMES.put(POSITION_T, "T");
		FEATURE_NAMES.put(CIRCLECurv, "CC");
		FEATURE_NAMES.put(DISTCurv, "DC");
		FEATURE_NAMES.put(INTENSITYA, "IA");
		FEATURE_NAMES.put(INTENSITYB, "IB");
		
		FEATURE_SHORT_NAMES.put(POSITION_X, "X");
		FEATURE_SHORT_NAMES.put(POSITION_Y, "Y");
		FEATURE_SHORT_NAMES.put(CENTER_X, "X");
		FEATURE_SHORT_NAMES.put(CENTER_Y, "Y");
		
		FEATURE_SHORT_NAMES.put(POSITION_T, "T");
		FEATURE_SHORT_NAMES.put(CIRCLECurv, "CC");
		FEATURE_SHORT_NAMES.put(DISTCurv, "DC");
		FEATURE_SHORT_NAMES.put(INTENSITYA, "IA");
		FEATURE_SHORT_NAMES.put(INTENSITYB, "IB");
		
		FEATURE_EmbryoDIMENSIONS.put(POSITION_X, EmbryoDimension.POSITION);
		FEATURE_EmbryoDIMENSIONS.put(POSITION_Y, EmbryoDimension.POSITION);
		FEATURE_EmbryoDIMENSIONS.put(CENTER_X, EmbryoDimension.POSITION);
		FEATURE_EmbryoDIMENSIONS.put(CENTER_Y, EmbryoDimension.POSITION);
		
		FEATURE_EmbryoDIMENSIONS.put(POSITION_T, EmbryoDimension.TIME);
		FEATURE_EmbryoDIMENSIONS.put(CIRCLECurv, EmbryoDimension.CURVATURE);
		FEATURE_EmbryoDIMENSIONS.put(DISTCurv, EmbryoDimension.DISTCURVATURE);
		FEATURE_EmbryoDIMENSIONS.put(INTENSITYA, EmbryoDimension.INTENSITY);
		FEATURE_EmbryoDIMENSIONS.put(INTENSITYB, EmbryoDimension.INTENSITY);
		
		IS_INT.put(POSITION_X, Boolean.FALSE);
		IS_INT.put(POSITION_Y, Boolean.FALSE);
		
		IS_INT.put(CENTER_X, Boolean.FALSE);
		IS_INT.put(CENTER_Y, Boolean.FALSE);
		IS_INT.put(POSITION_T, Boolean.FALSE);
		IS_INT.put(CIRCLECurv, Boolean.FALSE);
		IS_INT.put(DISTCurv, Boolean.FALSE);
		IS_INT.put(INTENSITYA, Boolean.FALSE);
		IS_INT.put(INTENSITYB, Boolean.FALSE);
		
	}


	
	public final Double getFeature(final String feature) {
		
		return features.get(feature);
	}
	public final void putFeature( final String feature, final Double value )
	{
		features.put( feature, value );
	}
	
	
	@Override
	public int compareTo(Embryoobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public void localize(float[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public void localize(double[] position) {
		int n = position.length;
		for (int d = 0; d < n; ++d)
			position[d] = getFloatPosition(d);
		
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) Location[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return Location[d];
	}
	
	/**
	 * Returns the difference between the location of two clouds, this operation
	 * returns ( <code>A.diffTo(B) = - B.diffTo(A)</code>)
	 *
	 * @param target
	 *            the Cloud to compare to.
	 * @param int
	 *            n n = 0 for X- coordinate, n = 1 for Y- coordinate
	 * @return the difference in co-ordinate specified.
	 */
	public double diffTo(final Embryoobject s, final String feature) {
		final double f1 = features.get(feature).doubleValue();
		final double f2 = s.getFeature(feature).doubleValue();
		return f1 - f2;
	}

	/**
	 * Returns the squared distance between two clouds.
	 *
	 * @param target
	 *            the Cloud to compare to.
	 *
	 * @return the distance to the current cloud to target cloud specified.
	 */

	public double squareDistanceTo(Embryoobject target) {
		// Returns squared distance between the source Blob and the target Blob.

		final long[] sourceLocation = Location;
		final long[] targetLocation = target.Location;

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(Embryoobject target, final double alpha, final double beta) {
		// Returns squared distance between the source Blob and the target Blob.

		final long[] sourceLocation = Location;
		final long[] targetLocation = target.Location;

		double distance = 1.0E-5;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
			
			
		}
		
		

		double cost = distance;
		
			return cost;
	}


}

