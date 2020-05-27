package utility;

import java.awt.Rectangle;
import java.util.ArrayList;

import ij.IJ;
import ij.gui.Roi;
import mpicbg.imglib.util.Util;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;
import pluginTools.InteractiveEmbryo;

public class Slicer {

	public static RandomAccessibleInterval<FloatType> getCurrentViewLarge(
			RandomAccessibleInterval<FloatType> originalimg, int thirdDimension) {

		final FloatType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1), originalimg.dimension(2) };
		final ImgFactory<FloatType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<FloatType> totalimg = factory.create(dim, type);

		totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		return totalimg;

	}

	public static  < T extends NumericType< T > & NativeType< T > > RandomAccessibleInterval<T> getCurrentView(RandomAccessibleInterval<T> originalimg,
			int thirdDimension, int thirdDimensionSize) {

		final T type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<T> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<T> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 1) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		

		return totalimg;

	}


	public static RandomAccessibleInterval<BitType> getCurrentViewBit(RandomAccessibleInterval<BitType> originalimg,
			int thirdDimension, int thirdDimensionSize) {

		final BitType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<BitType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<BitType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}
		if (thirdDimensionSize > 0 ) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);
		}



		return totalimg;

	}

	public static RandomAccessibleInterval<IntType> getCurrentViewInt(RandomAccessibleInterval<IntType> originalimg,
			int thirdDimension, int thirdDimensionSize) {

		final IntType type = originalimg.randomAccess().get().createVariable();
		long[] dim = { originalimg.dimension(0), originalimg.dimension(1) };
		final ImgFactory<IntType> factory = net.imglib2.util.Util.getArrayOrCellImgFactory(originalimg, type);
		RandomAccessibleInterval<IntType> totalimg = factory.create(dim, type);

		if (thirdDimensionSize == 0) {

			totalimg = originalimg;
		}

		if (thirdDimensionSize > 0 ) {

			totalimg = Views.hyperSlice(originalimg, 2, thirdDimension - 1);

		}

		

		
		
		return totalimg;

	}

	




	public static float computeValueFromScrollbarPosition(final int scrollbarPosition, final float min, final float max,
			final int scrollbarSize) {
		return min + (scrollbarPosition / (float) scrollbarSize) * (max - min);
	}

	public static int computeScrollbarPositionFromValue(final float sigma, final float min, final float max,
			final int scrollbarSize) {
		return Util.round(((sigma - min) / (max - min)) * scrollbarSize);
	}

}
