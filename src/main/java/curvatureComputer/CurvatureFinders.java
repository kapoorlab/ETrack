package curvatureComputer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import embryoDetector.Curvatureobject;
import embryoDetector.Embryoobject;
import embryoDetector.LineProfileCircle;
import net.imglib2.RealLocalizable;
import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveEmbryo;

public interface CurvatureFinders<T extends RealType<T> & NativeType<T>>
		extends OutputAlgorithm< Curvatureobject > {

	
	
	public Pair<Embryoobject, ClockDisplayer>  getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int Label, int strideindex, String name);

	
	public Pair<Embryoobject, ClockDisplayer>  getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int Label, int strideindex, String name);
	/*
	 * 
	 * InteractiveEmbryo is the parent class of the function
	 * Ordered list of boundary points 
	 * Center location of the embryo
	 * List of Embryo object
	 * Hashmap linking the EMbryo to the Embryo object
	 * dimensions of the space
	 * Cell leabel
	 * Time point in pixel units
	 */
	public void MarsRover(InteractiveEmbryo parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, 
			int ndims, int celllabel, int t);

	
	
}
