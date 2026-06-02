package model;

/**
 * Patient.java
 * Represents the emergency patient location.
 * A regular intersection node is "promoted" into a Patient node
 * when an emergency is generated.
 */
public class Patient {

    private final Node node;
    private final String emergencyType;

    public Patient(Node node, String emergencyType) {
        node.setType(Node.NodeType.PATIENT);
        this.node = node;
        this.emergencyType = emergencyType;
    }

    public Node getNode() { return node; }
    public String getEmergencyType() { return emergencyType; }
}
