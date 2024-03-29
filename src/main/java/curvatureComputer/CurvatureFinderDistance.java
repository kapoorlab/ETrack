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

import bdv.util.Bdv;
import curvatureComputer.CurvatureFinderCircleFit.ParallelCalls;
import embryoDetector.Embryoobject;
import embryoDetector.LineProfileCircle;
import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import pluginTools.InteractiveEmbryo;
import utility.Listordereing;

public class CurvatureFinderDistance<T extends RealType<T> & NativeType<T>> extends MasterCurvature<T>
		implements CurvatureFinders<T> {

	public final InteractiveEmbryo parent;
	public final JProgressBar jpb;
	public final int thirdDimension;
	public final int percent;
	public final int celllabel;
	ArrayList<Embryoobject> CurvatureAndLineScan;
	ConcurrentHashMap<Integer, ArrayList<Embryoobject>> Bestdelta = new ConcurrentHashMap<Integer, ArrayList<Embryoobject>>();
	public final RandomAccessibleInterval<BitType> ActualRoiimg;
	private final String BASE_ERROR_MSG = "[DistanceMethod-]";
	protected String errorMessage;


	public CurvatureFinderDistance(final InteractiveEmbryo parent,
			final RandomAccessibleInterval<BitType> ActualRoiimg, final JProgressBar jpb, final int percent,
			final int celllabel, final int thirdDimension)  {

		this.parent = parent;
		this.jpb = jpb;
		this.ActualRoiimg = ActualRoiimg;
		this.celllabel = celllabel;
		this.thirdDimension = thirdDimension;
		this.percent = percent;
	}


	public class ParallelCalls implements Callable<ArrayList<Embryoobject>> {

		
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
		public ArrayList<Embryoobject> call() throws Exception {
			
			ArrayList<Embryoobject>  result = getCurvature(parent, allorderedcandidates, centerpoint, ndims, celllabel, t, index);
			
			
			return result;
			
		}
	}

	@Override
	public ArrayList<Embryoobject> getResult() {

		return CurvatureAndLineScan;
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
		String uniqueID = Integer.toString(thirdDimension);

		List<RealLocalizable> candidates = GetCandidatePoints.ListofPoints(parent, ActualRoiimg, jpb, percent,thirdDimension);
		
		// Get mean co-ordinate from the candidate points
		RealLocalizable centerpoint = Listordereing.getMeanCord(candidates);

		// Get the sparse and dense list of points
		Pair<RealLocalizable, List<RealLocalizable>> Ordered = Listordereing.getOrderedList(candidates, parent.resolution);

		DisplayListOverlay.ArrowDisplay(parent, Ordered, uniqueID);

		MarsRover(parent, Ordered.getB(), centerpoint, ndims, celllabel, thirdDimension);
		
		return true;
	}

	@Override
	public String getErrorMessage() {

		return errorMessage;
	}

	@Override
	public Pair<Embryoobject, ClockDisplayer> getLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int Label, int strideindex, String name) {
		double[] x = new double[Cordlist.size()];
		double[] y = new double[Cordlist.size()];

		ArrayList<RealLocalizable> list = new ArrayList<RealLocalizable>();
		for (int index = 0; index < Cordlist.size() - 1; ++index) {
			x[index] = Cordlist.get(index)[0];
			y[index] = Cordlist.get(index)[1];

			RealPoint point = new RealPoint(new double[] { x[index], y[index] });
			list.add(point);

		}

		// Here you choose which method is used to detect curvature
		
		Pair<Embryoobject, ClockDisplayer> finalfunctionandList = BlockDistance(parent, list, centerpoint, Label, centerpoint.numDimensions(), strideindex, false, name);

		
		
		
		return finalfunctionandList;
	}
	@Override
	public Pair<Embryoobject, ClockDisplayer>  getCircleLocalcurvature(ArrayList<double[]> Cordlist,
			RealLocalizable centerpoint, int Label, int strideindex, String name) {
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

		Pair<Embryoobject, ClockDisplayer> finalfunctionandList = BlockCircle(parent, list, centerpoint, Label, centerpoint.numDimensions(), strideindex, false, name);

		
		return finalfunctionandList;
	}

	
	

	
	
	@Override
	public void MarsRover(InteractiveEmbryo parent, List<RealLocalizable> Ordered,
			RealLocalizable centerpoint, 
			int ndims, int celllabel, int t) {

		// Get the sparse list of points
	     

		int count = 0;


		int i = parent.increment;
		ArrayList<Embryoobject> resultpair = CommonLoop(parent, Ordered, centerpoint, ndims, celllabel, t);
		
		Bestdelta.put(count, resultpair);
		count++;
		
		
		String ID = Integer.toString(celllabel) + Integer.toString(t);
		int maxstride = parent.CellLabelsizemap.get(ID);
		
		
		// Get the sparse list of points, skips parent.resolution pixel points
		// set up executor service
		int nThreads = Runtime.getRuntime().availableProcessors();
		final ExecutorService taskExecutor = Executors.newFixedThreadPool(nThreads);
		
		List<Future<ArrayList<Embryoobject>>> list = new ArrayList<Future<ArrayList<Embryoobject>>>();
		
		for (int index = 0; index < maxstride; ++index) {
			List<RealLocalizable> allorderedcandidates = Listordereing.getList(Ordered, i + index);

		
				parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension,
						parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
			

			
			ParallelCalls call = new ParallelCalls(parent, allorderedcandidates, centerpoint, ndims, celllabel, t, index);
			Future<ArrayList<Embryoobject>> Futureresultpair = taskExecutor.submit(call);
			list.add(Futureresultpair);
		}
		
		taskExecutor.shutdown();
		for(Future<ArrayList<Embryoobject>> fut : list){
			
			
			
			
			try {
				
				ArrayList<Embryoobject> newresultpair = fut.get();
				
				Bestdelta.put(count, newresultpair);
				count++;

				
				
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
		

		CurvatureAndLineScan = GetAverage(parent, centerpoint, Bestdelta,count);
		


		


	}
	

}
