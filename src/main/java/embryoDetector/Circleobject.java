package embryoDetector;

import java.util.ArrayList;

import net.imglib2.RealLocalizable;

public class Circleobject {

	public final Circle function;

	public final ArrayList<RealLocalizable> candidates;

	/**
	 * 
	 * A ransac function output containing an ellipsoid function
	 * @param function
	 * @param linearfunction
	 * @param inliers
	 */
	public Circleobject(final Circle function, ArrayList<RealLocalizable> candidates ) {
		
		
		this.function = function;

		this.candidates = candidates;
		
	}
	
	
}
