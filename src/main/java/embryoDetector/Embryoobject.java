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
	
	
	public final double[] Location;
	public final ArrayList<double[]> linelist;
	private String name;
	public final double perimeter;
	public final int celllabel;
	public final int t;
	public final ArrayList<Line> linerois;
	public final ArrayList<OvalRoi> curvelinerois;
	public final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanIntensity;
	private final int ID;
	private final ConcurrentHashMap< String, Double > features = new ConcurrentHashMap< String, Double >();
	public static AtomicInteger IDcounter = new AtomicInteger( -1 );
	
	public Embryoobject(final double[] Location, ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanIntensity, ArrayList<double[]> linelist, final ArrayList<Line> linerois, final ArrayList<OvalRoi> curvelinerois,
			 final double perimeter,  final int celllabel,  final int t ) {
		
		
		super(3);
		this.Location = Location;
		this.celllabel = celllabel;
		this.linelist = linelist;
		this.linerois = linerois;
		this.curvelinerois = curvelinerois;
		this.t = t;
		this.perimeter = perimeter;
		this.LineScanIntensity = LineScanIntensity;
		this.ID = IDcounter.incrementAndGet();
		this.name = "ID" + ID;
		putFeature(POSITION_T,  (double) t);
		putFeature(POSITION_X, Location[0]);
		putFeature(POSITION_Y, Location[1]);
	}


	/** The name of the spot X position feature. */
	public static final String POSITION_X = "POSITION_X";

	/** The name of the spot Y position feature. */
	public static final String POSITION_Y = "POSITION_Y";
	
	
	/** The name of the spot T position feature. */
	public static final String POSITION_T = "POSITION_T";


	/** The position features. */
	public final static String[] POSITION_FEATURES = new String[] { POSITION_X, POSITION_Y };
	static int numfeatures = 3;
	public final static Collection<String> FEATURES = new ArrayList<>(numfeatures);

	/** The 7 privileged spot feature names. */
	public final static Map<String, String> FEATURE_NAMES = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature short names. */
	public final static Map<String, String> FEATURE_SHORT_NAMES = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature dimensions. */
	public final static Map<String, EmbryoDimension> FEATURE_EmbryoDIMENSIONS = new HashMap<>(numfeatures);

	/** The 7 privileged spot feature isInt flags. */
	public final static Map<String, Boolean> IS_INT = new HashMap<>(numfeatures);

	static {
		FEATURES.add(POSITION_X);
		FEATURES.add(POSITION_Y);
		FEATURES.add(POSITION_T);

		FEATURE_NAMES.put(POSITION_X, "X");
		FEATURE_NAMES.put(POSITION_Y, "Y");
		FEATURE_NAMES.put(POSITION_T, "T");

		FEATURE_SHORT_NAMES.put(POSITION_X, "X");
		FEATURE_SHORT_NAMES.put(POSITION_Y, "Y");
		FEATURE_SHORT_NAMES.put(POSITION_T, "T");

		FEATURE_EmbryoDIMENSIONS.put(POSITION_X, EmbryoDimension.POSITION);
		FEATURE_EmbryoDIMENSIONS.put(POSITION_Y, EmbryoDimension.POSITION);
		FEATURE_EmbryoDIMENSIONS.put(POSITION_T, EmbryoDimension.TIME);

		IS_INT.put(POSITION_X, Boolean.FALSE);
		IS_INT.put(POSITION_Y, Boolean.FALSE);
		IS_INT.put(POSITION_T, Boolean.FALSE);
	}

	public void setName(final String name) {
		
		this.name = name;
	}
	
	public int ID() {
		
		return ID;
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

		final double[] sourceLocation = Location;
		final double[] targetLocation = target.Location;

		double distance = 0;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
		}

		return distance;
	}
	public double DistanceTo(Embryoobject target, final double alpha, final double beta) {
		// Returns squared distance between the source Blob and the target Blob.

		final double[] sourceLocation = Location;
		final double[] targetLocation = target.Location;

		double distance = 1.0E-5;

		for (int d = 0; d < sourceLocation.length; ++d) {

			distance += (sourceLocation[d] - targetLocation[d]) * (sourceLocation[d] - targetLocation[d]);
			
			
		}
		
		

		double cost = distance;
		
			return cost;
	}


}
