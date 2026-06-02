package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Dijkstra.java
 *
 * DIJKSTRA'S SHORTEST PATH ALGORITHM
 *
 * Finds the shortest path (minimum total edge weight) from a source node
 * to every other node in a WEIGHTED graph with NON-NEGATIVE edge weights.
 *
 * Strategy:
 *   1. Initialize all distances to +infinity, source distance = 0.
 *   2. Use a min-priority queue keyed on tentative distance.
 *   3. Pop the closest unvisited node, "relax" each outgoing edge:
 *          if  dist[u] + w(u,v) < dist[v]
 *          then dist[v] = dist[u] + w(u,v); prev[v] = u
 *   4. Repeat until the queue is empty or target is settled.
 *
 * Complexity:
 *   Time  : O((V + E) log V)   using a binary heap (Java's PriorityQueue)
 *   Space : O(V)
 *
 * Why Dijkstra here?
 *   The ambulance must travel the FASTEST/SHORTEST route -- not the fewest hops.
 *   Edge weights model real travel cost (distance or time), so BFS would be wrong.
 *   Dijkstra is optimal for non-negative weights and is the textbook choice
 *   for emergency-response routing problems.
 */
public class Dijkstra {

    private final Graph graph;
    private final List<Node> visitedOrder = new ArrayList<>();

    public Dijkstra(Graph graph) {
        this.graph = graph;
    }

    /**
     * Runs Dijkstra from {@code start} to {@code target}.
     * Returns the reconstructed shortest path, or empty list if unreachable.
     */
    public List<Node> findShortestPath(Node start, Node target) {
        graph.resetAlgorithmState();
        visitedOrder.clear();

        // Min-priority queue ordered by tentative distance
        PriorityQueue<Node> pq = new PriorityQueue<>(
                Comparator.comparingDouble(Node::getDistance));

        start.setDistance(0);
        pq.add(start);

        while (!pq.isEmpty()) {
            Node current = pq.poll();

            // Skip stale entries (a shorter distance was already finalized)
            if (current.isVisited()) continue;
            current.setVisited(true);
            visitedOrder.add(current);

            // Early termination: target settled -> we have its shortest distance
            if (current.equals(target)) break;

            // Relaxation step
            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getOther(current);
                if (neighbor.isVisited()) continue;

                double newDist = current.getDistance() + edge.getWeight();
                if (newDist < neighbor.getDistance()) {
                    neighbor.setDistance(newDist);
                    neighbor.setPrevious(current);
                    // "Decrease-key" via re-insertion (standard Java PQ pattern)
                    pq.add(neighbor);
                }
            }
        }

        return PathFinder.reconstructPath(target);
    }

    /** Total weight of the shortest path that was just found. */
    public double getDistanceTo(Node target) {
        return target.getDistance();
    }

    public List<Node> getVisitedOrder() {
        return visitedOrder;
    }
}
