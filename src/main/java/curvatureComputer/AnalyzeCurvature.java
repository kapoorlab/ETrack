package curvatureComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.JProgressBar;
import embryoDetector.Embryoobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveEmbryo;

public class AnalyzeCurvature implements Callable< HashMap<String,ArrayList<Embryoobject>>>{
	
	
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
	
	private HashMap<String,ArrayList<Embryoobject>> CurvatureFinderChoice() {
		
		 HashMap<String,ArrayList<Embryoobject>>  AlldenseCurveintersection = 
				 new HashMap<String,ArrayList<Embryoobject>>();
		 
	 
		 String ID = Integer.toString(celllabel) + Integer.toString(parent.thirdDimension);
		 
		if (parent.circlefits) {
			
     		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent,  
				 ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
		
		    curvecircle.process();
		
		    ArrayList<Embryoobject> CurvatureAndLineScan = curvecircle.getResult(); 
		    AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
		
		}
		
		if(parent.distancemethod) {
			
		    CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, 
				ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
		
		    curvedistance.process();
		
		    ArrayList<Embryoobject> CurvatureAndLineScan = curvedistance.getResult(); 
	     	AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent,  
					ActualRoiimg, jpb, percent, celllabel, parent.thirdDimension);
			
			curvedistance.process();
			
			ArrayList<Embryoobject> CurvatureAndLineScan = curvedistance.getResult(); 
			AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
			
		}
		
		return AlldenseCurveintersection;
	}
	@Override
	public  HashMap<String,ArrayList<Embryoobject>>  call() throws Exception {

			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size() + 1), "Computing Curvature = "
					+ parent.thirdDimension + "/" + (parent.thirdDimensionSize + 1) );
		

		
		 HashMap<String,ArrayList<Embryoobject>>  AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}
	
	
}
