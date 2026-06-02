package model;

/**
 * Node.java
 * Represents a vertex in the city graph.
 * Each node has an id, name, 3D coordinates (x, y, z), and a type
 * (HOSPITAL, INTERSECTION, PATIENT, EMERGENCY).
 *
 * Used by the Graph (adjacency list) and by the 3D renderers
 * to place spheres/buildings in the JavaFX scene.
 */
public class Node {

    /** Type of node used for coloring and behavior in the simulation. */
    public enum NodeType {
        HOSPITAL,
        INTERSECTION,
        PATIENT,
        EMERGENCY
    }

    private final int id;
    private String name;
    private double x;
    private double y;
    private double z;
    private NodeType type;

    // Algorithm state (used during BFS/DFS/Dijkstra)
    private boolean visited;
    private double distance;     // Tentative distance used by Dijkstra
    private Node previous;       // Used for path reconstruction

    public Node(int id, String name, double x, double y, double z, NodeType type) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        resetAlgorithmState();
    }

    /** Resets per-run algorithm state (called before BFS/DFS/Dijkstra). */
    public void resetAlgorithmState() {
        this.visited = false;
        this.distance = Double.POSITIVE_INFINITY;
        this.previous = null;
    }

    // ---------- Getters / Setters ----------
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setZ(double z) { this.z = z; }

    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }

    public boolean isVisited() { return visited; }
    public void setVisited(boolean visited) { this.visited = visited; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public Node getPrevious() { return previous; }
    public void setPrevious(Node previous) { this.previous = previous; }

    @Override
    public String toString() {
        return "Node{" + id + ", " + name + ", " + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node)) return false;
        return this.id == ((Node) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
