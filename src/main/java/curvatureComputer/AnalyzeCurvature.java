package curvatureComputer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JProgressBar;

import embryoDetector.Curvatureobject;
import embryoDetector.Embryoobject;
import embryoDetector.LineProfileCircle;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.RealLocalizable;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveEmbryo;

public class AnalyzeCurvature implements Callable< HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>> >{
	
	
	final InteractiveEmbryo parent;
	final ArrayList<Curvatureobject> AllCurveintersection;
	 HashMap<Integer, Curvatureobject> AlldenseCurveintersection;
	final RandomAccessibleInterval<FloatType> ActualRoiimg;
	final List<RealLocalizable> candidates;
	final JProgressBar jpb;
	final int percent;
	final int celllabel;
	final int time;

	
	public AnalyzeCurvature(final InteractiveEmbryo parent, final ArrayList<RealLocalizable> candidates, 
			final RandomAccessibleInterval<FloatType> ActualRoiimg, ArrayList<Curvatureobject> AllCurveintersection,
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

	private HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>> CurvatureFinderChoice() {
		
		 HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  AlldenseCurveintersection = 
				 new HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>();
		 
	 
		
		if (parent.circlefits) {
			
		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, 
				 ActualRoiimg, jpb, percent, celllabel, time);
		
		curvecircle.process();
		
		
		Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvecircle.getResult(); 
		AlldenseCurveintersection.put(time, CurvatureAndLineScan);
		
		}
		
		if(parent.distancemethod) {
			
		CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, AllCurveintersection,
				ActualRoiimg, jpb, percent, celllabel, time);
		
		curvedistance.process();
		
		Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvedistance.getResult(); 
		AlldenseCurveintersection.put(time, CurvatureAndLineScan);
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent, AllCurveintersection, 
					ActualRoiimg, jpb, percent, celllabel, time);
			
			curvedistance.process();
			
			Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvedistance.getResult(); 
			AlldenseCurveintersection.put(time, CurvatureAndLineScan);
			
		}
		
		return AlldenseCurveintersection;
	}
	@Override
	public  HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  call() throws Exception {
		parent.Listmap.clear();

		if (parent.thirdDimensionSize != 0 && parent.Accountedframes.size() != 0 && parent.Accountedframes != null)
			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size()), "Computing Curvature = "
					+ time + "/" + parent.thirdDimensionSize );
		else {

			utility.ProgressBar.SetProgressBar(jpb, 100 * (percent) / (parent.pixellist.size()),
					"Computing Curvature ");
		}

		
		 HashMap<Integer,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}
	
	
}
