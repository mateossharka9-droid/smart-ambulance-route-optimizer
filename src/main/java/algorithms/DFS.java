package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;

/**
 * DFS.java
 *
 * DEPTH-FIRST SEARCH
 *
 * Explores as far as possible along each branch before backtracking.
 * Useful for:
 *   - exploring alternative routes
 *   - connectivity checks
 *   - cycle detection (not used here)
 *
 * Complexity:
 *   Time  : O(V + E)
 *   Space : O(V) for the explicit stack + visited
 *
 * Implementation note:
 *   We use an iterative DFS with an explicit stack so we can safely run on
 *   larger graphs without risking JVM stack overflow.
 */
public class DFS {

    private final Graph graph;
    private final List<Node> visitedOrder = new ArrayList<>();

    public DFS(Graph graph) {
        this.graph = graph;
    }

    /**
     * Iterative DFS from {@code start} to {@code target}.
     * Returns the discovery path from start -> target, or empty if unreachable.
     */
    public List<Node> search(Node start, Node target) {
        graph.resetAlgorithmState();
        visitedOrder.clear();

        Deque<Node> stack = new ArrayDeque<>();
        stack.push(start);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (current.isVisited()) continue;
            current.setVisited(true);
            visitedOrder.add(current);

            if (target != null && current.equals(target)) {
                return PathFinder.reconstructPath(target);
            }

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getOther(current);
                if (!neighbor.isVisited()) {
                    if (neighbor.getPrevious() == null && !neighbor.equals(start)) {
                        neighbor.setPrevious(current);
                    }
                    stack.push(neighbor);
                }
            }
        }

        if (target != null) return new ArrayList<>();
        return visitedOrder;
    }

    public List<Node> getVisitedOrder() {
        return visitedOrder;
    }
}
