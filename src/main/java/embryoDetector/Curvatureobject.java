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



	
	
	public final ArrayList<Embryoobject> Regionobject;
	
	public final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanobject;
	
	public Curvatureobject(final ArrayList<Embryoobject> Regionobject, final ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> LineScanobject) {
		super(3);
		this.Regionobject = Regionobject;
		this.LineScanobject = LineScanobject;
	
		
	};
		
	
	


	
	


	
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
