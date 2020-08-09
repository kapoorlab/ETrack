package pluginTools;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import ij.IJ;
import kalmanGUI.CovistoKalmanPanel;

import javax.swing.JProgressBar;

import embryoDetector.Curvatureobject;
import net.imglib2.Cursor;
import net.imglib2.KDTree;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealPoint;
import net.imglib2.RealPointSampleList;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.neighborsearch.NearestNeighborSearchOnKDTree;
import net.imglib2.type.Type;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.Util;
import net.imglib2.util.ValuePair;
import net.imglib2.view.Views;
import pluginTools.InteractiveEmbryo.ValueChange;

public class EmbryoTrack {

	final InteractiveEmbryo parent;
	final JProgressBar jpb;
	Pair<Boolean, String> isVisited;

	public EmbryoTrack(final InteractiveEmbryo parent, final JProgressBar jpb) {

		this.parent = parent;

		this.jpb = jpb;
	}






	public void ShowEmbryoCurvature() throws Exception {

		int percent = 0;
		

		percent++;

	
		RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.Segoriginalimg, parent.thirdDimension,
				parent.thirdDimensionSize);
		
		
		GetPixelList(CurrentViewInt);
	
		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(CurrentViewInt), min, max);

		
		CurvatureEachEmbryo compute = new CurvatureEachEmbryo(parent, CurrentViewInt, parent.thirdDimension, max.get(), percent);
		compute.displayCurvature();
		
		
	}
	
	


	public void ShowEmbryoCurvatureTime() throws Exception {

		int percent = 0;


				for (int t = parent.AutostartTime; t <= parent.AutoendTime; ++t) {
					parent.TID = Integer.toString(t);
					parent.Accountedframes.put(parent.TID, t);

						
						
						
						if(parent.EscapePressed) {

							if(parent.jpb!=null ) 
								utility.ProgressBar.SetProgressBar(parent.jpb, 100 ,
										"You pressed Escape to stop calculation, press restart to start again" );
							parent.EscapePressed = false;
							break;
						}
						
						
						parent.thirdDimension = t;
						parent.updatePreview(ValueChange.THIRDDIMmouse);
						
						parent.inputFieldT.setText(Integer.toString((int)parent.thirdDimension));
						
						parent.timeslider.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.thirdDimension, parent.thirdDimensionsliderInit, parent.thirdDimensionSize, parent.scrollbarSize));
						parent.timeslider.repaint();
						parent.timeslider.validate();
						parent.panelFirst.validate();
						parent.panelFirst.repaint();
						
						ArrayList<Curvatureobject> Embryolist = new ArrayList<Curvatureobject>();
						RandomAccessibleInterval<IntType> CurrentViewInt = utility.Slicer.getCurrentViewInt(parent.Segoriginalimg, t,
								parent.thirdDimensionSize);
						GetPixelList(CurrentViewInt);
					
						IntType min = new IntType();
						IntType max = new IntType();
						computeMinMax(Views.iterable(CurrentViewInt), min, max);
						CurvatureEachEmbryo compute = new CurvatureEachEmbryo(parent, CurrentViewInt,Embryolist, t, max.get(), (int) percent);
						compute.displayCurvature();
						
						
						parent.AllEmbryos.put(Integer.toString(t), Embryolist);
						
					
				}


			

		
		
	}

	
	
	public void DistanceTransformImage(RandomAccessibleInterval<BitType> inputimg,
			RandomAccessibleInterval<BitType> bitimg, RandomAccessibleInterval<BitType> outimg) {
		int n = inputimg.numDimensions();

		// make an empty list
		final RealPointSampleList<BitType> list = new RealPointSampleList<BitType>(n);

		// cursor on the binary image
		final Cursor<BitType> cursor = Views.iterable(bitimg).localizingCursor();

		// for every pixel that is 1, make a new RealPoint at that location
		while (cursor.hasNext())
			if (cursor.next().getInteger() == 1)
				list.add(new RealPoint(cursor), cursor.get());

		// build the KD-Tree from the list of points that == 1
		final KDTree<BitType> tree = new KDTree<BitType>(list);

		// Instantiate a nearest neighbor search on the tree (does not modifiy
		// the tree, just uses it)
		final NearestNeighborSearchOnKDTree<BitType> search = new NearestNeighborSearchOnKDTree<BitType>(tree);

		// randomaccess on the output
		final RandomAccess<BitType> ranac = outimg.randomAccess();

		// reset cursor for the input (or make a new one)
		cursor.reset();

		// for every pixel of the binary image
		while (cursor.hasNext()) {
			cursor.fwd();

			// set the randomaccess to the same location
			ranac.setPosition(cursor);

			// if value == 0, look for the nearest 1-valued pixel
			if (cursor.get().getInteger() == 0) {
				// search the nearest 1 to the location of the cursor (the
				// current 0)
				search.search(cursor);

				// get the distance (the previous call could return that, this
				// for generality that it is two calls)

				ranac.get().setReal(search.getDistance());

			} else {
				// if value == 1, no need to search
				ranac.get().setZero();
			}
		}

	}




	public RandomAccessibleInterval<BitType> CreateBinary(RandomAccessibleInterval<FloatType> source, double lowprob,
			double highprob) {

		RandomAccessibleInterval<BitType> copyoriginal = new ArrayImgFactory<BitType>().create(source, new BitType());

		final RandomAccess<BitType> ranac = copyoriginal.randomAccess();
		final Cursor<FloatType> cursor = Views.iterable(source).localizingCursor();

		while (cursor.hasNext()) {

			cursor.fwd();

			ranac.setPosition(cursor);
			if (cursor.get().getRealDouble() > lowprob && cursor.get().getRealDouble() < highprob) {

				ranac.get().setOne();
			} else {
				ranac.get().setZero();
			}

		}

		return copyoriginal;

	}

	
	public  void GetPixelList(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);
		Cursor<IntType> intCursor = Views.iterable(intimg).cursor();
		// Neglect the background class label
		parent.pixellist.clear();
		
		
		while (intCursor.hasNext()) {
			intCursor.fwd();
			int i = intCursor.get().get();

			if(!parent.pixellist.contains(i) && i > 0)
				parent.pixellist.add(i);



		}

	}
	
	public Boolean contains(int i) {
		
		Iterator<Integer> listiter = parent.pixellist.iterator();
		Boolean contains = false;
		while(listiter.hasNext()) {
			
			
			int entry = listiter.next();
			
			if(i == entry)
				contains = true;
			
		}
		
		return contains;
		
	}

	public int GetMaxlabelsseeded(RandomAccessibleInterval<IntType> intimg) {

		IntType min = new IntType();
		IntType max = new IntType();
		computeMinMax(Views.iterable(intimg), min, max);

		return max.get();

	}

	public <T extends Comparable<T> & Type<T>> void computeMinMax(final Iterable<T> input, final T min, final T max) {
		// create a cursor for the image (the order does not matter)
		final Iterator<T> iterator = input.iterator();

		// initialize min and max with the first image value
		T type = iterator.next();

		min.set(type);
		max.set(type);

		// loop over the rest of the data and determine min and max value
		while (iterator.hasNext()) {
			// we need this type more than once
			type = iterator.next();

			if (type.compareTo(min) < 0)
				min.set(type);

			if (type.compareTo(max) > 0)
				max.set(type);
		}
	}

	
	public void ComputeCurvatureCurrent() throws Exception {

		


		ShowEmbryoCurvature();


	}


}
