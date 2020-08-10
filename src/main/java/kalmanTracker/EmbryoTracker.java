package kalmanTracker;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import embryoDetector.Embryoobject;
import net.imglib2.algorithm.OutputAlgorithm;

public interface EmbryoTracker extends OutputAlgorithm< SimpleWeightedGraph< Embryoobject, DefaultWeightedEdge >> {
	
}
