package kalmanTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;

import embryoDetector.Embryoobject;


public class TimeDirectedSortedDepthFirstIterator extends SortedDepthFirstIterator<Embryoobject, DefaultWeightedEdge> {

	public TimeDirectedSortedDepthFirstIterator(final Graph<Embryoobject, DefaultWeightedEdge> g, final Embryoobject startVertex, final Comparator<Embryoobject> comparator) {
		super(g, startVertex, comparator);
	}



    @Override
	protected void addUnseenChildrenOf(final Embryoobject vertex) {

		// Retrieve target vertices, and sort them in a list
		final List< Embryoobject > sortedChildren = new ArrayList< Embryoobject >();
    	// Keep a map of matching edges so that we can retrieve them in the same order
    	final Map<Embryoobject, DefaultWeightedEdge> localEdges = new HashMap<Embryoobject, DefaultWeightedEdge>();

    	final int ts = vertex.getFeature(Embryoobject.POSITION_T).intValue();
        for (final DefaultWeightedEdge edge : specifics.edgesOf(vertex)) {

        	final Embryoobject oppositeV = Graphs.getOppositeVertex(graph, edge, vertex);
        	final int tt = oppositeV.getFeature(Embryoobject.POSITION_T).intValue();
        	if (tt <= ts) {
        		continue;
        	}

        	if (!seen.containsKey(oppositeV)) {
        		sortedChildren.add(oppositeV);
        	}
        	localEdges.put(oppositeV, edge);
        }

		Collections.sort( sortedChildren, Collections.reverseOrder( comparator ) );
		final Iterator< Embryoobject > it = sortedChildren.iterator();
        while (it.hasNext()) {
			final Embryoobject child = it.next();

            if (nListeners != 0) {
                fireEdgeTraversed(createEdgeTraversalEvent(localEdges.get(child)));
            }

            if (seen.containsKey(child)) {
                encounterVertexAgain(child, localEdges.get(child));
            } else {
                encounterVertex(child, localEdges.get(child));
            }
        }
    }



}
