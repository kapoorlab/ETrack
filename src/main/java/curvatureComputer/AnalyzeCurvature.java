package curvatureComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import javax.swing.JProgressBar;

import embryoDetector.Embryoobject;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.numeric.real.FloatType;
import pluginTools.InteractiveEmbryo;

public class AnalyzeCurvature implements Callable<HashMap<Integer, Embryoobject>>{
	
	
	final InteractiveEmbryo parent;
	final ArrayList<Embryoobject> AllCurveintersection;
	 HashMap<Integer, Embryoobject> AlldenseCurveintersection;
	final RandomAccessibleInterval<FloatType> ActualRoiimg;
	final List<RealLocalizable> candidates;
	final JProgressBar jpb;
	final int percent;
	final int celllabel;
	final int time;

	
	public AnalyzeCurvature(final InteractiveEmbryo parent, final ArrayList<RealLocalizable> candidates, 
			final RandomAccessibleInterval<FloatType> ActualRoiimg, ArrayList<Embryoobject> AllCurveintersection,
			final int time, final JProgressBar jpb, final int percent, final int celllabel) {
		
		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.AllCurveintersection = AllCurveintersection;
		this.candidates = candidates;
		this.jpb = jpb;
		this.percent = percent;
		this.celllabel = celllabel;
		this.time = time;
		
		
	}

	private HashMap<Integer,Embryoobject> CurvatureFinderChoice() {
		
		 HashMap<Integer,Embryoobject>  AlldenseCurveintersection = new HashMap<Integer,Embryoobject>();
		
	
		
		if (parent.circlefits) {
			
		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, 
				AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, time);
		
		curvecircle.process();
		
		AlldenseCurveintersection = curvecircle.getMap();
		
		}
		
		if(parent.distancemethod) {
			
		CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, AllCurveintersection,
				AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, time);
		
		curvedistance.process();
		
		AlldenseCurveintersection = curvedistance.getMap();
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, 
					AlldenseCurveintersection, ActualRoiimg, jpb, percent, celllabel, time);
			
			curvedistance.process();
			
			AlldenseCurveintersection = curvedistance.getMap();
			
			
		}
		
		return AlldenseCurveintersection;
	}
	@Override
	public HashMap<Integer, Embryoobject> call() throws Exception {
		parent.Listmap.clear();

		if (parent.thirdDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()), "Computing Curvature = "
					+ time + "/" + parent.thirdDimensionSize );
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent) / (parent.pixellist.size()),
					"Computing Curvature ");
		}

		
		HashMap<Integer,Embryoobject> AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}
	
	
}
