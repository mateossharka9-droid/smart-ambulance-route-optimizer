package model;

/**
 * Logical state of one ambulance during simulation.
 * Each ambulance belongs to one home hospital, but can move around the graph.
 */
public class Ambulance {

    private final int id;
    private final String name;
    private final Node homeHospital;
    private Node currentLocation;
    private double totalDistanceTraveled;
    private boolean carryingPatient;

    public Ambulance(int id, String name, Node homeHospital) {
        this.id = id;
        this.name = name;
        this.homeHospital = homeHospital;
        this.currentLocation = homeHospital;
        this.totalDistanceTraveled = 0;
        this.carryingPatient = false;
    }

    /** Backward-compatible constructor for old code. */
    public Ambulance(Node startLocation) {
        this(1, "Ambulance 1", startLocation);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Node getHomeHospital() { return homeHospital; }

    public Node getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(Node node) { this.currentLocation = node; }

    public double getTotalDistanceTraveled() { return totalDistanceTraveled; }
    public void addDistance(double d) { this.totalDistanceTraveled += d; }
    public void resetDistance() { this.totalDistanceTraveled = 0; }

    public boolean isCarryingPatient() { return carryingPatient; }
    public void setCarryingPatient(boolean carryingPatient) { this.carryingPatient = carryingPatient; }
}
