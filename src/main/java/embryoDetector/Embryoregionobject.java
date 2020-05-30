package embryoDetector;

import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.real.FloatType;

public class Embryoregionobject {
	
	
    public final RandomAccessibleInterval<BitType> Boundaryimage;
    
    
    public Embryoregionobject( RandomAccessibleInterval<BitType> gradimg) {
   	 
   	 this.Boundaryimage = gradimg;
    }
    
    
}
