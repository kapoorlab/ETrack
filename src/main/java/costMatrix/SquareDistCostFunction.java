package costMatrix;

import embryoDetector.Embryoobject;

/**
 * Implementation of various cost functions
 * 
 * 
 */

// Cost function base don minimizing the squared distances

public class SquareDistCostFunction implements CostFunction< Embryoobject, Embryoobject >
{

	@Override
	public double linkingCost( final Embryoobject source, final Embryoobject target )
	{
		return source.squareDistanceTo(target );
	}
	
	
	
	

}
