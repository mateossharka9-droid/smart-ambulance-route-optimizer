package model;

/**
 * Edge.java
 * Represents a weighted, undirected road between two nodes.
 * Weight = distance (or travel time) used by Dijkstra.
 */
public class Edge {

    private final Node from;
    private final Node to;
    private final double weight;

    public Edge(Node from, Node to, double weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Node getFrom() { return from; }
    public Node getTo() { return to; }
    public double getWeight() { return weight; }

    /** Returns the node at the other end of this edge given one endpoint. */
    public Node getOther(Node n) {
        if (n.equals(from)) return to;
        if (n.equals(to))   return from;
        throw new IllegalArgumentException("Node " + n + " is not an endpoint of this edge.");
    }

    @Override
    public String toString() {
        return from.getName() + " <-> " + to.getName() + " (" + weight + ")";
    }
}
