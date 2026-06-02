package algorithms;

import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PathFinder.java
 *
 * Shared utility used by BFS, DFS and Dijkstra.
 *
 *  - reconstructPath(target):  walks the `previous` chain backwards from
 *                              target to start and reverses the result.
 *
 *  - totalWeight(graph, path): computes the total edge weight of a given path
 *                              (useful for the statistics panel).
 *
 * Path reconstruction runs in O(V) in the worst case.
 */
public final class PathFinder {

    private PathFinder() { /* utility class */ }

    /**
     * Builds the path start -> ... -> target by following Node.previous pointers.
     * If target was never reached, an empty list is returned.
     */
    public static List<Node> reconstructPath(Node target) {
        List<Node> path = new ArrayList<>();
        if (target == null) return path;

        // If target has no predecessor and was not the start of any search,
        // it is unreachable.
        if (target.getPrevious() == null && Double.isInfinite(target.getDistance())
                && !target.isVisited()) {
            return path;
        }

        Node current = target;
        while (current != null) {
            path.add(current);
            current = current.getPrevious();
        }
        Collections.reverse(path);
        return path;
    }

    /** Sum of edge weights along a path. Returns 0 for an empty or single-node path. */
    public static double totalWeight(Graph graph, List<Node> path) {
        if (path == null || path.size() < 2) return 0;
        double total = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Node a = path.get(i);
            Node b = path.get(i + 1);
            for (model.Edge edge : graph.getNeighbors(a)) {
                if (edge.getOther(a).equals(b)) {
                    total += edge.getWeight();
                    break;
                }
            }
        }
        return total;
    }
}
