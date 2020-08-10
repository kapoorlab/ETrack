package kalmanTracker;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import embryoDetector.Embryoobject;



public class TimeDirectedDepthFirstIterator extends SortedDepthFirstIterator<Embryoobject, DefaultWeightedEdge> {

	public TimeDirectedDepthFirstIterator(Graph<Embryoobject, DefaultWeightedEdge> g, Embryoobject startVertex) {
		super(g, startVertex, null);
	}
	
	
	
    protected void addUnseenChildrenOf(Embryoobject vertex) {
    	
    	int ts = vertex.getFeature(Embryoobject.POSITION_T).intValue();
        for (DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {
            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(edge));
            }

            Embryoobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
            int tt = oppositeV.getFeature(Embryoobject.POSITION_T).intValue();
            if (tt <= ts) {
            	continue;
            }

            if ( seen.containsKey(oppositeV)) {
                encounterVertexAgain(oppositeV, edge);
            } else {
                encounterVertex(oppositeV, edge);
            }
        }
    }

	
	
}
