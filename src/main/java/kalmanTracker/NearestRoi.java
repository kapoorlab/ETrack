package kalmanTracker;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import embryoDetector.Embryoobject;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.Roi;

import net.imglib2.KDTree;
import net.imglib2.Point;
import net.imglib2.RealPoint;
import pluginTools.InteractiveEmbryo;
import utility.FlagNode;
import utility.Roiobject;

public class NearestRoi {

	
	public static Roi getNearestRois(Roiobject roi, double[] Clickedpoint, final InteractiveEmbryo parent ) {
		

		
		Roi[] Allrois =roi.roilist;
		
		Roi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.length);
		final List<FlagNode<Roi>> targetNodes = new ArrayList<FlagNode<Roi>>(Allrois.length);
		for (int index = 0; index < Allrois.length; ++index) {

			Roi r = Allrois[index];
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<Roi>(Allrois[index]));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Roi>> Tree = new KDTree<FlagNode<Roi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<Roi> Search = new NNFlagsearchKDtree<Roi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				
				final FlagNode<Roi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}
		
		return KDtreeroi;
		
	}
	
	
	public static OvalRoi getNearestIntersectionRois(Roiobject roi, double[] Clickedpoint, final InteractiveEmbryo parent ) {
		

		ArrayList<OvalRoi> Allrois = roi.resultovalroi;
		
		OvalRoi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<OvalRoi>> targetNodes = new ArrayList<FlagNode<OvalRoi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			 Roi r = Allrois.get(index);
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<OvalRoi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<OvalRoi>> Tree = new KDTree<FlagNode<OvalRoi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<OvalRoi> Search = new NNFlagsearchKDtree<OvalRoi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<OvalRoi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	
	
	
public static Roi getNearestSegmentRois(Roiobject roi, double[] Clickedpoint, final InteractiveEmbryo parent ) {
		

		ArrayList<Roi> Allrois = roi.segmentrect;
		
		Roi KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<Roi>> targetNodes = new ArrayList<FlagNode<Roi>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			 Roi r = Allrois.get(index);
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<Roi>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Roi>> Tree = new KDTree<FlagNode<Roi>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<Roi> Search = new NNFlagsearchKDtree<Roi>(Tree);


				final double[] source = Clickedpoint;
				final RealPoint sourceCoords = new RealPoint(source);
				Search.search(sourceCoords);
				final FlagNode<Roi> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	
	
	
public static Line getNearestLineRois(Roiobject roi, int[] clickedpoints, final InteractiveEmbryo parent ) {
		

		ArrayList<Line> Allrois = roi.resultlineroi;
		
		Line KDtreeroi = null;

		final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
		final List<FlagNode<Line>> targetNodes = new ArrayList<FlagNode<Line>>(Allrois.size());
		for (int index = 0; index < Allrois.size(); ++index) {

			 Roi r = Allrois.get(index);
			 Rectangle rect = r.getBounds();
			 
			 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
			 

			targetNodes.add(new FlagNode<Line>(Allrois.get(index)));

		}

		if (targetNodes.size() > 0 && targetCoords.size() > 0) {

			final KDTree<FlagNode<Line>> Tree = new KDTree<FlagNode<Line>>(targetNodes, targetCoords);

			final NNFlagsearchKDtree<Line> Search = new NNFlagsearchKDtree<Line>(Tree);


				final int[] source = clickedpoints;
				final Point sourceCoords = new Point(source);
				Search.search(sourceCoords);
				final FlagNode<Line> targetNode = Search.getSampler().get();

				KDtreeroi = targetNode.getValue();

		}

		return KDtreeroi;
		
	}
	

public static Line getNearestLineRois(Roiobject roi, double[] clickedpoints, final InteractiveEmbryo parent ) {
	

	ArrayList<Line> Allrois = roi.resultlineroi;
	
	Line KDtreeroi = null;

	final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allrois.size());
	final List<FlagNode<Line>> targetNodes = new ArrayList<FlagNode<Line>>(Allrois.size());
	for (int index = 0; index < Allrois.size(); ++index) {

		 Roi r = Allrois.get(index);
		 Rectangle rect = r.getBounds();
		 
		 targetCoords.add( new RealPoint(rect.x + rect.width/2.0, rect.y + rect.height/2.0 ) );
		 

		targetNodes.add(new FlagNode<Line>(Allrois.get(index)));

	}

	if (targetNodes.size() > 0 && targetCoords.size() > 0) {

		final KDTree<FlagNode<Line>> Tree = new KDTree<FlagNode<Line>>(targetNodes, targetCoords);

		final NNFlagsearchKDtree<Line> Search = new NNFlagsearchKDtree<Line>(Tree);


			final double[] source = clickedpoints;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<Line> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

	}

	return KDtreeroi;
	
}



public static Embryoobject getNearestIntersection(ArrayList<Embryoobject> Allintersection, double[] clickedpoints, final InteractiveEmbryo parent ) {
	

	
	Embryoobject KDtreeroi = null;

	final List<RealPoint> targetCoords = new ArrayList<RealPoint>(Allintersection.size());
	final List<FlagNode<Embryoobject>> targetNodes = new ArrayList<FlagNode<Embryoobject>>(Allintersection.size());
	
	for (int index = 0; index < Allintersection.size(); ++index) {

		 Embryoobject intersect = Allintersection.get(index);
		 Point point = new Point(intersect.Location[0], intersect.Location[1]);
		 targetCoords.add( new RealPoint(point));
		 

		targetNodes.add(new FlagNode<Embryoobject>(Allintersection.get(index)));

	}

	if (targetNodes.size() > 0 && targetCoords.size() > 0) {

		final KDTree<FlagNode<Embryoobject>> Tree = new KDTree<FlagNode<Embryoobject>>(targetNodes, targetCoords);

		final NNFlagsearchKDtree<Embryoobject> Search = new NNFlagsearchKDtree<Embryoobject>(Tree);


			final double[] source = clickedpoints;
			final RealPoint sourceCoords = new RealPoint(source);
			Search.search(sourceCoords);
			final FlagNode<Embryoobject> targetNode = Search.getSampler().get();

			KDtreeroi = targetNode.getValue();

	}

	return KDtreeroi;
	
}
}
      