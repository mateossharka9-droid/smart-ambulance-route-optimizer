package algorithms;

import model.Edge;
import model.Graph;
import model.Node;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * AStar.java
 *
 * A* shortest path algorithm.
 *
 * f(n) = g(n) + h(n)
 * g(n) = real cost from start to the current node
 * h(n) = estimated cost from the current node to the target
 *
 * Important portfolio fix:
 * The 3D coordinates are large visual coordinates, while road weights are small
 * travel-cost values. Using raw Euclidean distance as h(n) can overestimate and
 * make A* return a non-optimal path. This implementation automatically scales
 * the Euclidean heuristic using the smallest weight-per-coordinate ratio found
 * in the graph. That keeps the heuristic admissible for this weighted graph.
 *
 * Complexity: O((V + E) log V) with Java PriorityQueue.
 */
public class AStar {

    private final Graph graph;
    private final List<Node> visitedOrder = new ArrayList<>();
    private final double heuristicScale;

    public AStar(Graph graph) {
        this.graph = graph;
        this.heuristicScale = calculateSafeHeuristicScale(graph);
    }

    public List<Node> findShortestPath(Node start, Node target) {
        graph.resetAlgorithmState();
        visitedOrder.clear();

        Map<Node, Double> fScore = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueue<>(
                Comparator.comparingDouble(n -> fScore.getOrDefault(n, Double.POSITIVE_INFINITY))
        );

        start.setDistance(0);
        fScore.put(start, heuristic(start, target));
        open.add(start);

        while (!open.isEmpty()) {
            Node current = open.poll();
            if (current.isVisited()) continue;

            current.setVisited(true);
            visitedOrder.add(current);

            if (current.equals(target)) break;

            for (Edge edge : graph.getNeighbors(current)) {
                Node neighbor = edge.getOther(current);
                if (neighbor.isVisited()) continue;

                double tentativeG = current.getDistance() + edge.getWeight();
                if (tentativeG < neighbor.getDistance()) {
                    neighbor.setDistance(tentativeG);
                    neighbor.setPrevious(current);
                    fScore.put(neighbor, tentativeG + heuristic(neighbor, target));
                    open.add(neighbor);
                }
            }
        }

        return PathFinder.reconstructPath(target);
    }

    /**
     * Safe Euclidean heuristic for a graph whose visual coordinates and edge
     * weights are on different scales.
     */
    private double heuristic(Node a, Node b) {
        return euclideanDistance(a, b) * heuristicScale;
    }

    private double calculateSafeHeuristicScale(Graph graph) {
        double minWeightPerCoordinateUnit = Double.POSITIVE_INFINITY;

        for (Edge edge : graph.getAllEdges()) {
            double coordinateDistance = euclideanDistance(edge.getFrom(), edge.getTo());
            if (coordinateDistance <= 0) continue;

            double ratio = edge.getWeight() / coordinateDistance;
            if (ratio < minWeightPerCoordinateUnit) {
                minWeightPerCoordinateUnit = ratio;
            }
        }

        if (Double.isInfinite(minWeightPerCoordinateUnit)) {
            return 0.0;
        }
        return minWeightPerCoordinateUnit;
    }

    private double euclideanDistance(Node a, Node b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    public double getHeuristicScale() {
        return heuristicScale;
    }

    public List<Node> getVisitedOrder() {
        return visitedOrder;
    }
}
