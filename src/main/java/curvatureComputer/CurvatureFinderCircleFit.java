package curvatureComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JProgressBar;

import embryoDetector.Embryoobject;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveEmbryo;
import utility.Listordereing;

public class CurvatureFinderCircleFit<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
		implements CurvatureFinders<T> {

	public final InteractiveEmbryo parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int fourthDimension;
	public final int percent;
	public final int celllabel;
	public final ArrayList<Embryoobject> AllCurveintersection;
	public final HashMap<Integer, Embryoobject> AlldenseCurveintersection;
	ConcurrentHashMap<Integer, Embryoobject> Bestdelta = new ConcurrentHashMap<Integer, Embryoobject>();
	public final RandomAccessibleInterval<FloatType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[CircleFit-]";
	protected String errorMessage;

	public CurvatureFinderCircleFit(final InteractiveEmbryo parent,
			ArrayList<Embryoobject> AllCurveintersection,HashMap<Integer, Embryoobject> AlldenseCurveintersection,
			final RandomAccessibleInterval<FloatType> ActualRoiimg, final JProgressBar jpb, final int percent,
			final int celllabel, final int thirdDimension, final int fourthDimension) {

		this.parent = parent;
		this.AllCurveintersection = AllCurveintersection;
		this.AlldenseCurveintersection = AlldenseCurveintersection;
		this.jpb = jpb;
		this.ActualRoiimg = ActualRoiimg;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.fourthDimension = fourthDimension;
		this.percent = percent;
	}

	public class ParallelCalls implements Callable< Embryoobject>{

		
		public final InteractiveEmbryo parent;
		public final List<RealLocalizable> allorderedcandidates;
		public final RealLocalizable centerpoint;
		public final int ndims;
		public final int celllabel;
		public final int t;
		public final int index;

		
		public ParallelCalls( InteractiveEmbryo parent,
		 List<RealLocalizable> allorderedcandidates,
		 RealLocalizable centerpoint,
		 int ndims,
		 int celllabel,
		 int t,
		 int index) {
			
			
			this.parent = parent;
			this.allorderedcandidates = allorderedcandidates;
			this.centerpoint = centerpoint;
			this.ndims = ndims;
			this.celllabel = celllabel;
			this.t= t;
			this.index = index;
			
			
			
		}
		
		@Override
		public Embryoobject call() throws Exception {
			
			Embryoobject  result = getCurvature(parent, allorderedcandidates, centerpoint, ndims, celllabel, t, index);
			
			
			return result;
			
			
			
			
			
		}
	}
		
	public HashMap<Integer, Embryoobject> getMap() {

		return AlldenseCurveintersection;
	}
	
	@Override
	public ConcurrentHashMap<Integer, Embryoobject> getResult() {

		return Bestdelta;
	}

	@Override
	public boolean checkInput() {
		if (parent.CurrentView.numDimensions() > 4) {
			errorMessage = BASE_ERROR_MSG + " Can only operate on 4D, make slices of your stack . Got "
					+ parent.CurrentView.numDimensions() + "D.";
			return false;
		}
		return true;
	}

	@Override
	public boolean process() {
		
		int ndims = ActualRoiimg.numDimensions();
		String uniqueID = Integer.toString(thirdDimension) + Integer.toString(fourthDimension);

		List<RealLocalizable> candidates = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent,thirdDimension);
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(candidates);

		// Get the sparse and dense list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(candidates, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

		MarsRover(parent, Ordered.getB(), centerpoint, AllCurveintersection,
				AlldenseCurveintersection, ndims, celllabel, thirdDimension);
		
		
		
		
		
		return true;
	}

	


	
	public void MarsRover(InteractiveEmbryo parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, List<RealLocalizable> candidates,
			ArrayList<Embryoobject> AllCurveintersection, HashMap<Integer, Embryoobject> AlldenseCurveintersection,
			int ndims, int celllabel, int t) {

		// Get the sparse list of points
	     

		int count = 0;
		if (parent.minSegmentDist > candidates.size())
			parent.minSegmentDist = candidates.size();

		int i = parent.increment;
		Embryoobject resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t);
		
		Bestdelta.put(count, resultpair);
		count++;
		
		ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>> zeroline = resultpair.LineScanIntensity;
		
		
		int maxstride = parent.CellLabelsizemap.get(celllabel);
		
		
		// Get the sparse list of points, skips parent.resolution pixel points
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		
		List<Future<Embryoobject>> list = new ArrayList<Future<Embryoobject>>();
		
		for (int index = 0; index < maxstride; ++index) {
			List<RealLocalizable> allorderedcandidates = Listordereing.getList(Ordered, i + index);

		
				parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
						parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
			

			
			ParallelCalls call = new ParallelCalls(parent, allorderedcandidates, centerpoint, ndims, celllabel, t, maxstride, index);
			Future<Embryoobject> Futureresultpair = taskExecutor.submit(call);
			list.add(Futureresultpair);
		}
		
		taskExecutor.shutdown();
		for(Future<Embryoobject> fut : list){
			
			
			
			
			try {
				
				
				resultpair = fut.get();
				Embryoobject newresultpair = new Embryoobject(resultpair.functionlist, resultpair.Curvelist, zeroline);
				
				Bestdelta.put(count, newresultpair);
				count++;

				parent.localCurvature = newresultpair.Curvelist;
				parent.functions = newresultpair.functionlist;
				
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
		

		Pair<Embryoobject, Embryoobject> sparseanddensepair = GetAverage(parent, centerpoint, Bestdelta,count);
		
		AllCurveintersection.add(sparseanddensepair.getA());
		AlldenseCurveintersection.put(celllabel, sparseanddensepair.getB());
		
		
		parent.AlllocalCurvature.add(parent.localCurvature);

	}
	

	
	

	
	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public RegressionLineProfile getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new double[] { x[index], y[index] }));

		}

		// Here you choose which method is used to detect curvature

		RegressionLineProfile finalfunctionandList = NoClockRansacEllipseBlock(parent, list, centerpoint, centerpoint.numDimensions(), strideindex, false, "");

		
		return finalfunctionandList;
	}
	
	@Override
	public Pair<Embryoobject, ClockDisplayer>  getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int strideindex, String name) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<Point> pointlist = new ArrayList<Point>();
		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);
			pointlist.add(new Point(new long[] { (long) x[index], (long) y[index] }));

		}

		
		// Here you choose which method is used to detect curvature
		Pair<RegressionLineProfile, ClockDisplayer> finalfunctionandList = RansacEllipseBlock(parent, list, centerpoint, centerpoint.numDimensions(), strideindex, true, name);

       
		  
		
		return finalfunctionandList;
	}






	
	
	
	
	
}
