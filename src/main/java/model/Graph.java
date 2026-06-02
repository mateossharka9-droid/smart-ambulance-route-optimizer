package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Weighted, undirected graph backed by an adjacency list. */
public class Graph {

    private final Map<Integer, Node> nodes = new HashMap<>();
    private final Map<Integer, List<Edge>> adjacency = new HashMap<>();

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
        adjacency.computeIfAbsent(node.getId(), k -> new ArrayList<>());
    }

    public void addEdge(Node from, Node to, double weight) {
        if (!nodes.containsKey(from.getId())) addNode(from);
        if (!nodes.containsKey(to.getId())) addNode(to);
        adjacency.get(from.getId()).add(new Edge(from, to, weight));
        adjacency.get(to.getId()).add(new Edge(to, from, weight));
    }

    public List<Edge> getNeighbors(Node node) {
        return adjacency.getOrDefault(node.getId(), new ArrayList<>());
    }

    public Node getNode(int id) { return nodes.get(id); }

    public List<Node> getAllNodes() { return new ArrayList<>(nodes.values()); }

    public List<Edge> getAllEdges() {
        List<Edge> result = new ArrayList<>();
        for (Map.Entry<Integer, List<Edge>> entry : adjacency.entrySet()) {
            for (Edge e : entry.getValue()) {
                if (e.getFrom().getId() < e.getTo().getId()) result.add(e);
            }
        }
        return result;
    }

    public int size() { return nodes.size(); }

    public void resetAlgorithmState() {
        for (Node n : nodes.values()) n.resetAlgorithmState();
    }

    public Node findNodeByType(Node.NodeType type) {
        for (Node n : nodes.values()) if (n.getType() == type) return n;
        return null;
    }

    public List<Node> findNodesByType(Node.NodeType type) {
        List<Node> result = new ArrayList<>();
        for (Node n : nodes.values()) if (n.getType() == type) result.add(n);
        return result;
    }
}
