package pluginTools;

import java.util.ArrayList;
import java.util.Iterator;

import budDetector.Budregionobject;
import budDetector.Distance;
import embryoDetector.Curvatureobject;
import embryoDetector.Embryoregionobject;
import net.imglib2.Cursor;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.view.Views;

public class CurvatureEachEmbryo {
	
	
	final InteractiveEmbryo parent;
	
	final RandomAccessibleInterval<IntType> CurrentViewInt;
	
	final int t;
	
	final int maxlabel;
	
	final int percent;
	
	final ArrayList<Curvatureobject> Embryolist;
	
	public CurvatureEachEmbryo(final InteractiveEmbryo parent, final RandomAccessibleInterval<IntType> CurrentViewInt, final int t,
			final int maxlabel, final int percent) {
		
		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
        this.Embryolist = null;		
		
	}
	
	public CurvatureEachEmbryo(final InteractiveEmbryo parent, final RandomAccessibleInterval<IntType> CurrentViewInt, 
			final ArrayList<Curvatureobject> Embryolist, final int t,
			final int maxlabel, final int percent) {
		
		this.parent = parent;
		this.CurrentViewInt = CurrentViewInt;
		this.t = t;
		this.maxlabel = maxlabel;
		this.percent = percent;
        this.Embryolist = Embryolist;		
		
	}
	
	
	public ArrayList<Curvatureobject> returnEmbryoList() {
		
		return Embryolist;
	}
	
	public void displayCurvature() {
		
		Iterator<Integer> setiter = parent.pixellist.iterator();
		parent.overlay.clear();
		
		while (setiter.hasNext()) {

			int label = setiter.next();

			if (label > 0) {
				
				Embryoregionobject PairCurrentViewBit = EmbryoCurrentLabelBinaryImage(
						CurrentViewInt, label);
				
				
			}
			
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
		
		double size = Math.sqrt(Distance.DistanceSq(minVal, maxVal));

	
		Point min = new Point(minVal.length);
		// Gradient image gives us the bondary points
		RandomAccessibleInterval<BitType> gradimg = GradientmagnitudeImage(outimg);
		
		Budregionobject region = new Budregionobject(gradimg, outimg, min,  size);
		return region;

	}
	

}
