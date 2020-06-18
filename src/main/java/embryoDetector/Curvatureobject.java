package embryoDetector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.RealLocalizable;

public class Curvatureobject extends AbstractEuclideanSpace implements RealLocalizable, Comparable<Curvatureobject> {

	
	/*
	 * FIELDS
	 */



	
	
	public final double radiusCurvature;
	public final double distCurvature;
	public final double perimeter;
	public final int t;
	public final long[] cord;
	public final long[] centercord;
	public final int Label;
	public final double Intensity;
	public final double SecIntensity;
	
	public Curvatureobject(final double radiusCurvature, final double distCurvature, final double perimeter, final double Intensity, final double SecIntensity, final int Label, final long[] cord, final long[] centercord, final int t) {
		super(3);
		this.radiusCurvature = radiusCurvature;
		this.distCurvature = distCurvature;
		this.perimeter = perimeter;
		this.SecIntensity = SecIntensity;
		this.cord = cord;
		this.centercord = centercord;
		this.Intensity = Intensity;
		this.t = t;
		this.Label = Label;
		
	};
		
	
	


	/**
	 * Stores the specified feature value for this spot.
	 *
	 * @param feature
	 *            the name of the feature to store, as a {@link String}.
	 * @param value
	 *            the value to store, as a {@link Double}. Using
	 *            <code>null</code> will have unpredicted outcomes.
	 */
	
	

	public static final String Perimeter = "Perimeter";
	
	public static final String LABEL = "LABEL";

	public static final String Time = "Time";
	
	public static final String CenterX = "CenterX";
	
	public static final String CenterY = "CenterY";
	
	public static final String IntensityA = "IntensityA";
	
	public static final String IntensityB = "IntensityB";
	
	public static final String CurvatureCircle = "CircleCurv";
	
	public static final String Deformation = "Deform";
	
	public static final String LocationX = "LocationX";
	
	public static final String LocationY = "LocationY";
	

	
	


	
	@Override
	public int compareTo(Curvatureobject o) {
		return hashCode() - o.hashCode();
	}

	@Override
	public double getDoublePosition(int d) {
		return (float) getDoublePosition(d);
	}

	@Override
	public float getFloatPosition(int d) {
		return (float) getDoublePosition(d);
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
	
	
	
	
	
}
