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
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import pluginTools.InteractiveEmbryo;

public class AnalyzeCurvature implements Callable< HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>> >{
	
	
	final InteractiveEmbryo parent;
	 HashMap<Integer, Curvatureobject> AlldenseCurveintersection;
	final RandomAccessibleInterval<BitType> ActualRoiimg;
	final JProgressBar jpb;
	final int percent;
	final int celllabel;
	final int time;

	
	public AnalyzeCurvature(final InteractiveEmbryo parent,
			final RandomAccessibleInterval<BitType> ActualRoiimg, 
			final int time, final JProgressBar jpb, final int percent, final int celllabel) {
		
		this.parent = parent;
		this.ActualRoiimg = ActualRoiimg;
		this.jpb = jpb;
		this.percent = percent;
		this.celllabel = celllabel;
		this.time = time;
		
		
	}

	private HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>> CurvatureFinderChoice() {
		
		 HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  AlldenseCurveintersection = 
				 new HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>();
		 
	 
		 String ID = Integer.toString(celllabel) + Integer.toString(time);
		
		if (parent.circlefits) {
			
		CurvatureFinderCircleFit<FloatType> curvecircle = new CurvatureFinderCircleFit<FloatType>(parent,  
				 ActualRoiimg, jpb, percent, celllabel, time);
		
		curvecircle.process();
		
		
		Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvecircle.getResult(); 
		AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
		
		}
		
		if(parent.distancemethod) {
			
		CurvatureFinderDistance<FloatType> curvedistance = new CurvatureFinderDistance<FloatType>(parent, 
				ActualRoiimg, jpb, percent, celllabel, time);
		
		curvedistance.process();
		
		Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvedistance.getResult(); 
		AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
		
	     }
		
		if(parent.combomethod) {
			
			CurvatureFinderCircleFit<FloatType> curvedistance = new CurvatureFinderCircleFit<FloatType>(parent,  
					ActualRoiimg, jpb, percent, celllabel, time);
			
			curvedistance.process();
			
			Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>> CurvatureAndLineScan = curvedistance.getResult(); 
			AlldenseCurveintersection.put(ID, CurvatureAndLineScan);
			
		}
		
		return AlldenseCurveintersection;
	}
	@Override
	public  HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  call() throws Exception {

			utility.ProgressBar.SetProgressBar(jpb, 100 * percent / (parent.pixellist.size() + 1), "Computing Curvature = "
					+ time + "/" + (parent.thirdDimensionSize + 1) );
		

		
		 HashMap<String,Pair<ArrayList<Curvatureobject>,ConcurrentHashMap<Integer, ArrayList<LineProfileCircle>>>>  AlldenseCurveintersection = 	CurvatureFinderChoice();
		
		
		
		return  AlldenseCurveintersection;
	}
	
	
}
