package curvatureComputer;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import bdv.util.Bdv;
import embryoDetector.Circle;
import embryoDetector.Circleobject;
import embryoDetector.Embryoobject;
import embryoDetector.LineProfileCircle;
import ij.IJ;
import ij.gui.Line;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import mpicbg.models.Point;
import net.imglib2.Cursor;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.algorithm.region.hypersphere.HyperSphere;
import net.imglib2.algorithm.region.hypersphere.HyperSphereCursor;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import net.imglib2.algorithm.region.BresenhamLine;
import pluginTools.InteractiveEmbryo;
import utility.Listordereing;
import utility.Roiobject;

public abstract class MasterCurvature<T extends RealType<T> & NativeType<T>> implements CurvatureFinders<T> {

	/**
	 * 
	 * Function to fit on a list of points which are not tree based
	 * 
	 * @param parent
	 * @param centerpoint
	 * @param sublist
	 * @param functions
	 * @param interpolatedCurvature
	 * @param smoothing
	 * @param maxError
	 * @param minNumInliers
	 * @param degree
	 * @param secdegree
	 * @return
	 */

	public class ParallelCalls implements Callable<Pair<Embryoobject, ClockDisplayer>> {

		public final InteractiveEmbryo parent;
		public final String name;
		public final int Label;
		public final RealLocalizable centerpoint;
		public final List<RealLocalizable> sublist;
		public final int strideindex;

		public ParallelCalls(InteractiveEmbryo parent, RealLocalizable centerpoint, List<RealLocalizable> sublist,
				int strideindex, String name, int Label) {

			this.parent = parent;

			this.centerpoint = centerpoint;

			this.sublist = sublist;

			this.strideindex = strideindex;

			this.name = name;
			
			this.Label = Label;

		}

		@Override
		public Pair<Embryoobject, ClockDisplayer> call() throws Exception {

			Pair<Embryoobject, ClockDisplayer> localfunction = FitCircleonList(parent, centerpoint, sublist, Label,
					strideindex, name);

			return localfunction;
		}

	}

	public Embryoobject FitonList(InteractiveEmbryo parent, int Label, RealLocalizable centerpoint, List<RealLocalizable> sublist,
			int strideindex, String name) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(0), sublist.get(i).getDoublePosition(1) });
		}

		// Interfaces have to implement the curvature getting methods

		Pair<Embryoobject, ClockDisplayer> resultcurvature = getLocalcurvature(Cordlist, centerpoint, Label, strideindex,
				name);

		return resultcurvature.getA();

	}

	public Pair<Embryoobject, ClockDisplayer> FitCircleonList(InteractiveEmbryo parent, RealLocalizable centerpoint,
			List<RealLocalizable> sublist, int Label, int strideindex, String name) {

		ArrayList<double[]> Cordlist = new ArrayList<double[]>();

		for (int i = 0; i < sublist.size(); ++i) {

			Cordlist.add(new double[] { sublist.get(i).getDoublePosition(0), sublist.get(i).getDoublePosition(1) });
		}

		Pair<Embryoobject, ClockDisplayer> resultcurvature = getCircleLocalcurvature(Cordlist, centerpoint, Label, strideindex,
				name);

		// Draw the function

		return resultcurvature;

	}

	public ArrayList<Embryoobject> CommonLoop(
			InteractiveEmbryo parent, List<RealLocalizable> Ordered, RealLocalizable centerpoint, int ndims,
			int celllabel, int t) {

		// Get the sparse list of points

		int i = parent.increment;

		// Get the sparse list of points

		List<RealLocalizable> allorderedcandidates = Listordereing.getList(Ordered, i);

		parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
				parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));

		ArrayList<Embryoobject> LabelCurvature = getCurvatureLineScan(
				parent, allorderedcandidates, centerpoint, ndims, celllabel, t);

		return LabelCurvature;

	}

	/**
	 * 
	 * Take in a list of ordered co-ordinates and compute a curvature object
	 * containing the curvature information at each co-ordinate Makes a tree
	 * structure of the list
	 * 
	 * @param orderedcandidates
	 * @param ndims
	 * @param Label
	 * @param t
	 * @param z
	 * @param strideindex
	 * @return
	 */
	public ArrayList<Embryoobject>  getCurvature(InteractiveEmbryo parent, List<RealLocalizable> candidates,
			RealLocalizable centerpoint, int ndims, int Label, int t, int strideindex) {
		
		ArrayList<Embryoobject> currentobject =  getCurvatureLineScan(parent, candidates, centerpoint,  ndims,Label, t); 

		return currentobject;

	}

	public ArrayList<Embryoobject> getCurvatureLineScan(
			InteractiveEmbryo parent, List<RealLocalizable> candidates, RealLocalizable centerpoint, int ndims,
			int Label, int t) {


		ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> Hashtotalscan = new ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>();


		int segmentlabel = 1;
		MakeSegments(parent, candidates, parent.minSegDist, Label, t);
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);

		List<Future<Pair<Embryoobject, ClockDisplayer>>> list = new ArrayList<Future<Pair<Embryoobject, ClockDisplayer>>>();
		// Now do the fitting
		for (Map.Entry<Integer, List<RealLocalizable>> entry : parent.Listmap.entrySet()) {

			List<RealLocalizable> sublist = entry.getValue();
			/***
			 * get
			 * 
			 * Main method that fits on segments a function to get the curvature
			 * 
			 */

			ParallelCalls call = new ParallelCalls(parent, centerpoint, sublist, 0, Integer.toString(entry.getKey()), Label  );
			Future<Pair<Embryoobject, ClockDisplayer>> Futureresultpair = taskExecutor.submit(call);
			list.add(Futureresultpair);
		}
		taskExecutor.shutdown();

		ArrayList<ClockDisplayer> Masterclock = new ArrayList<ClockDisplayer>();
		
		ArrayList<Embryoobject> AllCells = new ArrayList<Embryoobject>(); 
		
		for (Future<Pair<Embryoobject, ClockDisplayer>> fut : list) {

			try {

				Pair<Embryoobject, ClockDisplayer> localfunction = fut.get();

				Masterclock.add(localfunction.getB());
                AllCells.add(localfunction.getA());
                
                

					Hashtotalscan.put(segmentlabel, localfunction.getA().LineScanIntensity);
					segmentlabel++;


			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
		

		return AllCells;

	}

	public ArrayList<Embryoobject> GetAverage(InteractiveEmbryo parent, RealLocalizable centerpoint,
			ConcurrentHashMap<Integer, ArrayList<Embryoobject>> bestdelta,
			int count) {

		ArrayList<Embryoobject> localCurvature = bestdelta.get(0);
		

		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] Zdist = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] ISec = new double[localCurvature.size()];

		
		ArrayList<Embryoobject> RefinedCurvature = new ArrayList<Embryoobject>();
		
		for (int index = 0; index < localCurvature.size(); ++index) {

			ArrayList<Double> CurveXY = new ArrayList<Double>();
			ArrayList<Double> CurveXYdist = new ArrayList<Double>();
			ArrayList<Double> CurveI = new ArrayList<Double>();
			ArrayList<Double> CurveISec = new ArrayList<Double>();

			X[index] = localCurvature.get(index).Location[0];
			Y[index] = localCurvature.get(index).Location[1];
			Z[index] = localCurvature.get(index).CircleCurvature;
			Zdist[index] = localCurvature.get(index).DistCurvature;
			I[index] = localCurvature.get(index).IntensityA;
			ISec[index] = localCurvature.get(index).IntensityB;

			CurveXY.add(Z[index]);
			CurveI.add(I[index]);
			CurveISec.add(ISec[index]);
			CurveXYdist.add(Zdist[index]);
			Iterator<Double> setiterdist = CurveXYdist.iterator();
			double frequdeltadist = Zdist[index];
			double intensitydelta = I[index];
			double intensitySecdelta = ISec[index];
			while (setiterdist.hasNext()) {

				Double s = setiterdist.next();

				frequdeltadist += s;

			}

			frequdeltadist /= CurveXYdist.size();
			Iterator<Double> Iiter = CurveI.iterator();

			while (Iiter.hasNext()) {

				Double s = Iiter.next();

				intensitydelta += s;

			}

			Iterator<Double> ISeciter = CurveISec.iterator();
			while (ISeciter.hasNext()) {

				Double s = ISeciter.next();

				intensitySecdelta += s;

			}

			for (int secindex = 1; secindex < count; ++secindex) {

				ArrayList<Embryoobject> testlocalCurvature = bestdelta
						.get(secindex);


				double[] Xtest = new double[testlocalCurvature.size()];
				double[] Ytest = new double[testlocalCurvature.size()];
				double[] Ztest = new double[testlocalCurvature.size()];
				double[] Itest = new double[testlocalCurvature.size()];
				double[] ISectest = new double[testlocalCurvature.size()];

				for (int testindex = 0; testindex < testlocalCurvature.size(); ++testindex) {

					Xtest[testindex] = testlocalCurvature.get(testindex).Location[0];
					Ytest[testindex] = testlocalCurvature.get(testindex).Location[1];
					Ztest[testindex] = testlocalCurvature.get(testindex).CircleCurvature;
					Itest[testindex] = testlocalCurvature.get(testindex).IntensityA;
					ISectest[testindex] = testlocalCurvature.get(testindex).IntensityB;

					if (X[index] == Xtest[testindex] && Y[index] == Ytest[testindex]) {

						CurveXY.add(Ztest[testindex]);

					}

				}

			}

			double frequdeltaperi = 0;
					
			for( int i = 0; i < localCurvature.size(); i++)
				frequdeltaperi += localCurvature.get(i).perimeter;
				
			double frequdelta = Z[index];

			Iterator<Double> setiter = CurveXY.iterator();
			while (setiter.hasNext()) {

				Double s = setiter.next();

				frequdelta += s;

			}

			frequdelta /= CurveXY.size();

			
			Embryoobject newEmbryoobject = new Embryoobject(localCurvature.get(index).Location, localCurvature.get(index).center,
					localCurvature.get(index).LineScanIntensity, localCurvature.get(index).pointlist, (float) frequdelta, (float) frequdeltadist, intensitydelta, intensitySecdelta, frequdeltaperi, localCurvature.get(index).Label, localCurvature.get(index).t);
			
			RefinedCurvature.add(newEmbryoobject);
			
			
		}
		

		return RefinedCurvature;
	}

	public ArrayList<Embryoobject> GetSingle(InteractiveEmbryo parent, RealLocalizable centerpoint,
			ConcurrentHashMap<Integer, ArrayList<Embryoobject>> bestdelta) {

		ArrayList<Embryoobject> localCurvature = bestdelta
				.get(0);
		ArrayList<Embryoobject> RefinedCurvature = new ArrayList<Embryoobject>();

		double[] X = new double[localCurvature.size()];
		double[] Y = new double[localCurvature.size()];
		double[] Z = new double[localCurvature.size()];
		double[] Zdist = new double[localCurvature.size()];
		double[] I = new double[localCurvature.size()];
		double[] ISec = new double[localCurvature.size()];
		for (int index = 0; index < localCurvature.size(); ++index) {

			ArrayList<Double> CurveXY = new ArrayList<Double>();
			ArrayList<Double> CurveXYdist = new ArrayList<Double>();
			ArrayList<Double> CurveI = new ArrayList<Double>();
			ArrayList<Double> CurveISec = new ArrayList<Double>();

			X[index] = localCurvature.get(index).Location[0];
			Y[index] = localCurvature.get(index).Location[1];
			Z[index] = localCurvature.get(index).CircleCurvature;
			Zdist[index] = localCurvature.get(index).DistCurvature;
			I[index] = localCurvature.get(index).IntensityA;
			ISec[index] = localCurvature.get(index).IntensityB;

			CurveXY.add(Z[index]);
			CurveI.add(I[index]);
			CurveISec.add(ISec[index]);
			Iterator<Double> setiterdist = CurveXYdist.iterator();
			double frequdeltadist = Zdist[index];
			double frequdeltaperi = localCurvature.get(0).perimeter;
			double frequdelta = Z[index];
			double intensitydelta = I[index];
			double intensitySecdelta = ISec[index];
			while (setiterdist.hasNext()) {

				Double s = setiterdist.next();

				frequdeltadist += s;

			}
			
			
			frequdeltadist /= CurveXYdist.size();
			Iterator<Double> setiter = CurveXY.iterator();
			while (setiter.hasNext()) {

				Double s = setiter.next();

				frequdelta += s;

			}

			frequdelta /= CurveXY.size();

			Iterator<Double> Iiter = CurveI.iterator();
			while (Iiter.hasNext()) {

				Double s = Iiter.next();

				intensitydelta += s;

			}

			Iterator<Double> ISeciter = CurveISec.iterator();
			while (ISeciter.hasNext()) {

				Double s = ISeciter.next();

				intensitySecdelta += s;

			}
			
			
			Embryoobject newEmbryoobject = new Embryoobject(localCurvature.get(index).Location, localCurvature.get(index).center,
					localCurvature.get(index).LineScanIntensity, localCurvature.get(index).pointlist, (float) frequdelta, (float) frequdeltadist, intensitydelta, intensitySecdelta, frequdeltaperi, localCurvature.get(index).Label, localCurvature.get(index).t);
			
			RefinedCurvature.add(newEmbryoobject);

		
		}


		return RefinedCurvature;
	}

	public void MakeSegments(InteractiveEmbryo parent, final List<RealLocalizable> truths, int boxSize,
			int celllabel, int t) {

		List<RealLocalizable> copytruths = new ArrayList<RealLocalizable>(truths);
		if (truths.size() < 3)
			return;
		else {
			int size = truths.size();

			int maxpoints = (int) (boxSize/parent.calibration);
			if (maxpoints <= 2)
				maxpoints = 3;
			int segmentLabel = 1;

			int index = truths.size() - 1;
			do {

				if (index >= truths.size())
					index = 0;

				copytruths.add(truths.get(index));

				index++;

			} while (copytruths.size() % maxpoints != 0);

		  
			int newsize = copytruths.size();
			List<RealLocalizable> sublist = new ArrayList<RealLocalizable>();

			int startindex = 0;
			
			int endindex = startindex + maxpoints;

			while (true) {

				sublist = copytruths.subList(startindex, Math.min(endindex, newsize));
				parent.Listmap.put(segmentLabel, sublist);


				String ID = Integer.toString(celllabel) + Integer.toString(t);
				parent.CellLabelsizemap.put(ID, size);
				segmentLabel++;

				startindex = endindex;
				endindex = startindex + maxpoints;

				if (endindex >= newsize + 1)
					break;

			}

		}

	}

	/**
	 * Obtain intensity in the user defined
	 * 
	 * @param point
	 * @return
	 */

	public Pair<Double, Double> getIntensity(InteractiveEmbryo parent, Localizable point, Localizable centerpoint) {

		RandomAccess<FloatType> ranac = parent.CurrentView.randomAccess();

		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		ranac.setPosition(point);
		ranacsec.setPosition(ranac);
		double mindistance = getDistance(point, centerpoint);
		double[] currentPosition = new double[point.numDimensions()];

		HyperSphere<FloatType> hyperSphere = new HyperSphere<FloatType>(parent.CurrentView, ranac,
				(int) parent.regiondistance);
		HyperSphereCursor<FloatType> localcursor = hyperSphere.localizingCursor();
		int Area = 1;
		while (localcursor.hasNext()) {

			localcursor.fwd();

			ranacsec.setPosition(localcursor);

			ranacsec.localize(currentPosition);

			if (currentPosition[0] > parent.CurrentView.min(0) + parent.regiondistance
					&& currentPosition[1] > parent.CurrentView.min(1) + parent.regiondistance
					&& currentPosition[0] < parent.CurrentView.max(0) - parent.regiondistance
					&& currentPosition[1] < parent.CurrentView.max(1) - parent.regiondistance) {
				double currentdistance = getDistance(localcursor, centerpoint);
				if ((currentdistance - mindistance) <= parent.regiondistance) {
					Intensity += localcursor.get().getRealDouble();
					IntensitySec += ranacsec.get().getRealDouble();
					Area++;
				}
			}
		}

		return new ValuePair<Double, Double>(Intensity / Area, IntensitySec / Area);
	}

	public static Circle LocalCircle(final ArrayList<RealLocalizable> points, int ndims) {

		final ArrayList<RealLocalizable> remainingPoints = new ArrayList<RealLocalizable>();
		if (points != null)
			remainingPoints.addAll(points);
		int size = points.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = points.get(0).getDoublePosition(i);
			pointB[i] = points.get(splitindex).getDoublePosition(i);
			pointC[i] = points.get(size - 1).getDoublePosition(i);

		}

		final Circle circle = Circle.FitCircleMb(pointA, pointB, pointC);

		return circle;

	}

	/**
	 * 
	 * Fit a local circle to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public Pair<Embryoobject, ClockDisplayer> BlockCircle(final InteractiveEmbryo parent,
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int Label, int ndims, int strideindex,
			boolean linescan, String name) {

		final Circle localcircle = LocalCircle(pointlist, ndims);
		double Kappa = 0;
		double Kappadistance = 0;
		double perimeter = 0;

		double radii = localcircle.getRadii();
		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];

		perimeter = pointlist.size() * parent.calibration;

		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = pointlist.get(0).getDoublePosition(i);
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);
			pointC[i] = pointlist.get(size - 1).getDoublePosition(i);

		}

		long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
				(long) centerpoint.getDoublePosition(1) };
		net.imglib2.Point centpos = new net.imglib2.Point(centerloc);

		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
		ClockDisplayer Clock = new ClockDisplayer("", null);
		if (strideindex == 0) {

			Pair<ArrayList<LineProfileCircle>, ClockDisplayer> PairLineScanIntensity = getLineScanIntensity(parent,
					centerloc, localcircle, pointB, ndims, name);
			LineScanIntensity = PairLineScanIntensity.getA();
			Clock = PairLineScanIntensity.getB();

		}

		Kappa = 1.0 / (radii * parent.calibration);
		if (parent.combomethod)
			Kappadistance = getDistance(pointB, centerpoint) * parent.calibration;
		for (int d = 0; d < newpos.length; ++d)
			longnewpos[d] = (long) pointB[d];
		net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

		Pair<Double, Double> Intensity = new ValuePair<Double, Double>(0.0, 0.0);

		Intensity = getIntensity(parent, intpoint, centpos);

		Embryoobject currentprofile = new Embryoobject(new long[] { (long) pointB[0], (long) pointB[1] }, centerloc,
				LineScanIntensity, pointlist, Math.max(0, Kappa), Math.max(0, Kappadistance), Intensity.getA(),
				Intensity.getB(), perimeter, Label, parent.thirdDimension);

		return new ValuePair<Embryoobject, ClockDisplayer>(currentprofile, Clock);

	}

	/**
	 * 
	 * Fit a local circle to a bunch of points
	 * 
	 * @param pointlist
	 * @param ndims
	 * @return
	 */

	public Pair<Embryoobject, ClockDisplayer> BlockDistance(final InteractiveEmbryo parent,
			final ArrayList<RealLocalizable> pointlist, RealLocalizable centerpoint, int Label, int ndims, int strideindex,
			boolean linescan, String name) {

		double Kappadistance = 0;
		double perimeter = 0;

		double[] newpos = new double[ndims];
		long[] longnewpos = new long[ndims];

		perimeter = pointlist.size() * parent.calibration;

		int size = pointlist.size();
		final double[] pointA = new double[ndims];
		final double[] pointB = new double[ndims];
		final double[] pointC = new double[ndims];

		int splitindex;
		if (size % 2 == 0)
			splitindex = size / 2;
		else
			splitindex = (size - 1) / 2;

		for (int i = 0; i < ndims; ++i) {
			pointA[i] = pointlist.get(0).getDoublePosition(i);
			pointB[i] = pointlist.get(splitindex).getDoublePosition(i);
			pointC[i] = pointlist.get(size - 1).getDoublePosition(i);

		}

		long[] centerloc = new long[] { (long) centerpoint.getDoublePosition(0),
				(long) centerpoint.getDoublePosition(1) };
		net.imglib2.Point centpos = new net.imglib2.Point(centerloc);

		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>();
		ClockDisplayer Clock = new ClockDisplayer("", null);

		Kappadistance = getDistance(pointB, centerpoint) * parent.calibration;
		for (int d = 0; d < newpos.length; ++d)
			longnewpos[d] = (long) pointB[d];
		net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

		Pair<Double, Double> Intensity = new ValuePair<Double, Double>(0.0, 0.0);

		Intensity = getIntensity(parent, intpoint, centpos);

		Embryoobject currentprofile = new Embryoobject(new long[] { (long) pointB[0], (long) pointB[1] }, centerloc,
				LineScanIntensity, pointlist, Math.max(0, Kappadistance), Math.max(0, Kappadistance), Intensity.getA(),
				Intensity.getB(), perimeter, Label, parent.thirdDimension);

		return new ValuePair<Embryoobject, ClockDisplayer>(currentprofile, Clock);

	}

	@SuppressWarnings("deprecation")
	public Pair<ArrayList<LineProfileCircle>, ClockDisplayer> getLineScanIntensity(final InteractiveEmbryo parent,
			final long[] centerpos, Circle localcircle, final double[] pointB, final int ndims, final String name) {

		int count = 0;

		int thickness = parent.linescanradius;

		ArrayList<LineProfileCircle> LineScanIntensity = new ArrayList<LineProfileCircle>(
				(int) (2 * parent.insidedistance));
		long[] longnewpos = new long[ndims];

		for (int d = 0; d < pointB.length; ++d)
			longnewpos[d] = (long) pointB[d];
		net.imglib2.Point intpoint = new net.imglib2.Point(longnewpos);

		RandomAccess<FloatType> ranac = parent.CurrentView.randomAccess();

		LinefunctionCircle NormalLine = new LinefunctionCircle(localcircle, intpoint);

		double[] NormalSlopeIntercept = NormalLine.NormalatPoint();

		double startNormalX = intpoint.getDoublePosition(0)
				- parent.insidedistance / Math.sqrt(1 + NormalSlopeIntercept[0] * NormalSlopeIntercept[0]);
		double startNormalY = NormalSlopeIntercept[0] * startNormalX + NormalSlopeIntercept[1];

		double endNormalX = intpoint.getDoublePosition(0)
				+ parent.insidedistance / Math.sqrt(1 + NormalSlopeIntercept[0] * NormalSlopeIntercept[0]);
		double endNormalY = NormalSlopeIntercept[0] * endNormalX + NormalSlopeIntercept[1];

		double[] startNormal = { startNormalX, startNormalY };

		double[] endNormal = { endNormalX, endNormalY };

		Line line = new Line((int) startNormal[0], (int) startNormal[1], (int) endNormal[0], (int) endNormal[1],
				parent.imp);
		parent.overlay.add(line);
		TextRoi newellipse = new TextRoi(startNormal[0], startNormal[1], name + "_OZ");

		Pair<double[], double[]> linescanpair = new ValuePair<double[], double[]>(startNormal, endNormal);
		ClockDisplayer masterclock = new ClockDisplayer("", linescanpair);
		line.setStrokeWidth(1);
		line.setStrokeColor(Color.WHITE);
		parent.overlay.add(newellipse);

		parent.overlay.drawLabels(true);
		parent.overlay.drawNames(true);
		parent.imp.updateAndDraw();

		if (parent.thirdDimension == 1) {

			parent.clockoverlay.add(newellipse);
			parent.clockoverlay.add(line);

			parent.clockimp.setOverlay(parent.clockoverlay);
			parent.clockimp.updateAndDraw();

		}

		double[] outsidepoint = (Distance.DistanceSq(centerpos, startNormal) < Distance.DistanceSq(centerpos,
				endNormal)) ? endNormal : startNormal;
		double[] insidepoint = (Distance.DistanceSq(centerpos, startNormal) > Distance.DistanceSq(centerpos, endNormal))
				? endNormal
				: startNormal;

		net.imglib2.Point pointOut = new net.imglib2.Point(
				new long[] { Math.round(outsidepoint[0]), Math.round(outsidepoint[1]) });
		net.imglib2.Point pointIn = new net.imglib2.Point(
				new long[] { Math.round(insidepoint[0]), Math.round(insidepoint[1]) });

		double Intensity = 0;
		double IntensitySec = 0;
		RandomAccess<FloatType> ranacsec;
		if (parent.CurrentViewSecOrig != null)
			ranacsec = parent.CurrentViewSecOrig.randomAccess();
		else
			ranacsec = ranac;

		BresenhamLine<FloatType> newline = new BresenhamLine<FloatType>(ranac, pointOut, pointIn);

		Cursor<FloatType> linecursor = newline.copyCursor();

		while (linecursor.hasNext()) {

			linecursor.fwd();

			ranac.setPosition(linecursor);
			ranacsec.setPosition(ranac);

			HyperSphere<FloatType> hyperSphereOne = new HyperSphere<FloatType>(parent.CurrentView, ranac,
					(int) thickness);

			HyperSphereCursor<FloatType> localcursorOne = hyperSphereOne.localizingCursor();

			double[] currentPosition = new double[ndims];

			int avcount = 1;
			while (localcursorOne.hasNext()) {

				localcursorOne.fwd();

				ranacsec.setPosition(localcursorOne);

				ranacsec.localize(currentPosition);

				if (currentPosition[0] > parent.CurrentView.min(0) + thickness
						&& currentPosition[1] > parent.CurrentView.min(1) + thickness
						&& currentPosition[0] < parent.CurrentView.max(0) - thickness
						&& currentPosition[1] < parent.CurrentView.max(1) - thickness) {
					Intensity += localcursorOne.get().getRealDouble();
					IntensitySec += ranacsec.get().getRealDouble();
					avcount++;
				}
			}

			count++;
			Intensity /= avcount;
			IntensitySec /= avcount;
			LineProfileCircle linescan = new LineProfileCircle(count, Intensity, IntensitySec);
			LineScanIntensity.add(linescan);

		}

		do {

			LineProfileCircle linescan = new LineProfileCircle(count, Intensity, IntensitySec);
			LineScanIntensity.add(linescan);
			count++;

		} while (count < (int) parent.insidedistance * 2);

		return new ValuePair<ArrayList<LineProfileCircle>, ClockDisplayer>(LineScanIntensity, masterclock);

	}

	public double getDistance(Localizable point, RealLocalizable centerpoint) {

		double distance = 0;

		int ndims = point.numDimensions();

		for (int i = 0; i < ndims; ++i) {

			distance += (point.getDoublePosition(i) - centerpoint.getDoublePosition(i))
					* (point.getDoublePosition(i) - centerpoint.getDoublePosition(i));

		}

		return Math.sqrt(distance);

	}

	public double getDistance(double[] point, RealLocalizable centerpoint) {

		double distance = 0;

		int ndims = point.length;

		for (int i = 0; i < ndims; ++i) {

			distance += (point[i] - centerpoint.getDoublePosition(i)) * (point[i] - centerpoint.getDoublePosition(i));

		}

		return Math.sqrt(distance);

	}

	public double getDistance(RealLocalizable point, RealLocalizable centerpoint) {

		double distance = 0;

		int ndims = point.numDimensions();

		for (int i = 0; i < ndims; ++i) {

			distance += (point.getDoublePosition(i) - centerpoint.getDoublePosition(i))
					* (point.getDoublePosition(i) - centerpoint.getDoublePosition(i));

		}

		return Math.sqrt(distance);

	}

}
