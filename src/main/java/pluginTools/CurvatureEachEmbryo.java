package pluginTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

import curvatureComputer.AnalyzeCurvature;
import embryoDetector.Embryoobject;
import embryoDetector.Embryoregionobject;
import embryoDetector.LineProfileCircle;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;

public class CurvatureEachEmbryo {
	
	
	final InteractiveEmbryo parent;
	
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	
	
	final int maxlabel;
	
	final int percent;
	
	final ArrayList<Embryoobject> Embryolist;
	
	public CurvatureEachEmbryo(final InteractiveEmbryo parent, final RandomAccessibleInterval<IntType> CurrentViewInt,
			final int maxlabel, final int percent) {
		
		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.maxlabel = maxlabel;
		this.percent = percent;
        this.Embryolist = null;		
		
	}
	
	public CurvatureEachEmbryo(final InteractiveEmbryo parent, final RandomAccessibleInterval<IntType> CurrentViewInt, 
			final ArrayList<Embryoobject> Embryolist, 
			final int maxlabel, final int percent) {
		
		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.maxlabel = maxlabel;
		this.percent = percent;
        this.Embryolist = Embryolist;		
		
	}
	
	
	public ArrayList<Embryoobject> returnEmbryoList() {
		
		return Embryolist;
	}
	
	public void displayCurvature() throws Exception {
		
		Iterator<Integer> setiter = parent.pixellist.iterator();
		
		parent.overlay.clear();
		
		while (setiter.hasNext()) {

			int label = setiter.next();

			if (label > 0) {
				
				
				Embryoregionobject Embryo = EmbryoCurrentLabelBinaryImage(
						CurrentViewInt, label);
				
				AnalyzeCurvature CurvatureMe = new AnalyzeCurvature(parent,Embryo.Boundaryimage, parent.jpb, percent, label);
				HashMap<String,ArrayList<Embryoobject>> CurvatureMap  = CurvatureMe.call();
				
				
			}
			
		}
		
	}
	
	public void Common() {
		
		if(parent.Curvaturebutton.isEnabled()) {
			
			// We are still not making the object, just testing
			
		}
		
		else {
			
			// We are now taking over and making objects
			
		}
		
	}
	
	public static Embryoregionobject EmbryoCurrentLabelBinaryImage(
			RandomAccessibleInterval<IntType> Intimg, int currentLabel) {
		int n = Intimg.numDimensions();
		long[] position = new long[n];
		Cursor<IntType> intCursor = Views.iterable(Intimg).cursor();

		RandomAccessibleInterval<BitType> outimg = new ArrayImgFactory<BitType>().create(Intimg, new BitType());
		RandomAccess<BitType> imageRA = outimg.randomAccess();

		// Go through the whole image and add every pixel, that belongs to
		// the currently processed label
		long[] minVal = { Intimg.max(0), Intimg.max(1) };
		long[] maxVal = { Intimg.min(0), Intimg.min(1) };

		while (intCursor.hasNext()) {
			intCursor.fwd();
			imageRA.setPosition(intCursor);
			int i = intCursor.get().get();
			if (i == currentLabel) {

				intCursor.localize(position);
				for (int d = 0; d < n; ++d) {
					if (position[d] < minVal[d]) {
						minVal[d] = position[d];
					}
					if (position[d] > maxVal[d]) {
						maxVal[d] = position[d];
					}

				}

				imageRA.get().setOne();
			} else
				imageRA.get().setZero();

		}
		

	
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Embryoregionobject region = new Embryoregionobject(gradimg);
		return region;

	}
	
	public static RandomAccessibleInterval<BitType> GradientmagnitudeImage(RandomAccessibleInterval<BitType> inputimg) {

		RandomAccessibleInterval<BitType> gradientimg = new ArrayImgFactory<BitType>().create(inputimg, new BitType());
		Cursor<BitType> cursor = Views.iterable(gradientimg).localizingCursor();
		RandomAccessible<BitType> view = Views.extendBorder(inputimg);
		RandomAccess<BitType> randomAccess = view.randomAccess();

		// iterate over all pixels
		while (cursor.hasNext()) {
			// move the cursor to the next pixel
			cursor.fwd();

			// compute gradient and its direction in each dimension
			double gradient = 0;

			for (int d = 0; d < inputimg.numDimensions(); ++d) {
				// set the randomaccess to the location of the cursor
				randomAccess.setPosition(cursor);

				// move one pixel back in dimension d
				randomAccess.bck(d);

				// get the value
				double Back = randomAccess.get().getRealDouble();

				// move twice forward in dimension d, i.e.
				// one pixel above the location of the cursor
				randomAccess.fwd(d);
				randomAccess.fwd(d);

				// get the value
				double Fwd = randomAccess.get().getRealDouble();

				gradient += ((Fwd - Back) * (Fwd - Back)) / 4;

			}

			cursor.get().setReal(Math.sqrt(gradient));

		}

		return gradientimg;
	}
	

}
