package kalmanTracker;

import costMatrix.CostFunction;
import embryoDetector.Embryoobject;

public class ETrackCostFunction  implements CostFunction< Embryoobject, Embryoobject >
{

	
// Alpha is the weightage given to distance and Beta is the weightage given to the ratio of pixels
	public final double beta;
	public final double alpha;
	
	

	
	public double getAlpha(){
		
		return alpha;
	}
	
  
	public double getBeta(){
		
		return beta;
	}

	public ETrackCostFunction (double alpha, double beta){
		
		this.alpha = alpha;
		this.beta = beta;
		
	}
	
	
@Override
public double linkingCost( final Embryoobject source, final Embryoobject target )
{
	return source.DistanceTo(target, alpha, beta);
}
	



}
