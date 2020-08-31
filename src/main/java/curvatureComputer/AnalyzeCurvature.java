package curvatureComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.JProgressBar;

import embryoDetector.Cellobject;
import embryoDetector.Embryoobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveEmbryo;

public class AnalyzeCurvature implements Callable< Cellobject>{
	
	
	final InteractiveEmbryo parent;
	 HashMap<Integer, ArrayList<Embryoobject>> AlldenseCurveintersection;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	final JProgressBar jpb;
	final int percent;
	final int celllabel;
	

	
	public AnalyzeCurvature(final InteractiveEmbryo parent,
			final RandomAccessibleInterval<BitType> ActualRoiimg, 
			final JProgressBar jpb, final int percent, final int celllabel) {
		
		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.jpb = jpb;
		this.percent = percent;
		this.celllabel = celllabel;
		
		
		
	}

	/**
	 * 
	 * Compute the curvature using user chosen method to get a list of Embryoobject, 
	 * Embryoobject is computing curvature/intensity/line scan intensity in a region for each cell.
	 * The method stores these results as a hashmap.
	 * 
	 * 
	 * @return
	 */
	
	private Cellobject CurvatureFinderChoice() {
		
		
				
		 
	 
		 
		 ArrayList<Embryoobject> CurvatureAndLineScan = new ArrayList<Embryoobject>();
		 
		if (parent.circlefits) {
			
     		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent,  
				 ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
		
		    curvecircle.process();
		
		   CurvatureAndLineScan = curvecircle.getResult(); 
		    
		
		}
		
		if(parent.distancemethod) {
			
		    CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, 
				ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
		
		    curvedistance.process();
		
		   CurvatureAndLineScan = curvedistance.getResult(); 
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent,  
					ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
			
			curvedistance.process();
			
			CurvatureAndLineScan = curvedistance.getResult(); 
			
		}
		
	
		
		Cellobject AlldenseCurveintersection = new Cellobject(CurvatureAndLineScan);
		
		return AlldenseCurveintersection;
	}
	@Override
	public  Cellobject call() throws Exception {

			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size() + 1), "Computing Curvature = "
					+ parent.thirdDimension + "/" + (parent.thirdDimensionSize + 1) );
		

		
			Cellobject AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}
	
	
}
