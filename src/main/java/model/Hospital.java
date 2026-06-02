package model;

/**
 * Hospital.java
 * Simple wrapper around a Node tagged as HOSPITAL.
 * The hospital is always the start and final return point of the ambulance.
 */
public class Hospital {

    private final Node node;

    public Hospital(Node node) {
        if (node.getType() != Node.NodeType.HOSPITAL) {
            node.setType(Node.NodeType.HOSPITAL);
        }
        this.node = node;
    }

    public Node getNode() { return node; }
}
