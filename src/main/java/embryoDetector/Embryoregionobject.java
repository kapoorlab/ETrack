package embryoDetector;

import net.imglib2.Point;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;

public class Embryoregionobject {
	
	
    public RandomAccessibleInterval<BitType> Boundaryimage;
    
    
    public Embryoregionobject( RandomAccessibleInterval<BitType> Boundaryimage) {
   	 
   	 this.Boundaryimage = Boundaryimage;
   	 
   	 
   	 
   	 
    }

}
