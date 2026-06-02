package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * BFS.java
 *
 * BREADTH-FIRST SEARCH
 *
 * Visits all nodes in layers, level by level, starting from a source node.
 * In an UNWEIGHTED graph BFS also finds the shortest path
 * (in terms of number of edges).
 *
 * Complexity:
 *   Time  : O(V + E)   (each node and each edge visited once)
 *   Space : O(V)       (visited set + queue + parent map)
 *
 * In this project BFS is used to:
 *   - demonstrate graph traversal order
 *   - visualize node-by-node exploration in 3D
 *   - find a (hop-count) shortest path from hospital -> patient
 */
public class BFS {

    private final Graph graph;
    private final List<Node> visitedOrder = new ArrayList<>();

    public BFS(Graph graph) {
        this.graph = graph;
    }

    /**
     * Runs BFS from {@code start} until {@code target} is found (or all reachable
     * nodes are explored if target is null).
     * Returns the reconstructed path from start -> target, or empty if unreachable.
     */
    public List<Node> search(Node start, Node target) {
        graph.resetAlgorithmState();
        visitedOrder.clear();

        Queue<Node> queue = new LinkedList<>();
        start.setVisited(true);
        start.setDistance(0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            visitedOrder.add(current);

            if (target != null && current.equals(target)) {
                return PathFinder.reconstructPath(target);
            }

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getOther(current);
                if (!neighbor.isVisited()) {
                    neighbor.setVisited(true);
                    neighbor.setPrevious(current);
                    neighbor.setDistance(current.getDistance() + 1); // hops
                    queue.add(neighbor);
                }
            }
        }

        // target unreachable (or full traversal completed with no target)
        if (target != null) return new ArrayList<>();
        return visitedOrder;
    }

    /** Order in which nodes were dequeued. Useful for the 3D animation. */
    public List<Node> getVisitedOrder() {
        return visitedOrder;
    }
}
