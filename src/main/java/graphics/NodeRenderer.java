package graphics;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import model.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * NodeRenderer.java
 *
 * Renders each graph node as:
 *   - a glowing 3D sphere (intersection marker)
 *   - a tall Box (skyscraper) sitting on that sphere for visual flair
 *
 * Different node types get different colors:
 *   HOSPITAL      -> blue + tall white building
 *   INTERSECTION  -> cyan glow
 *   PATIENT       -> red, pulsing
 *   visited (alg) -> green
 *   on path       -> bright lime
 */
public class NodeRenderer {

    public static final Color C_HOSPITAL   = Color.web("#3FA9F5");
    public static final Color C_INTERSECT  = Color.web("#1ABCFE");
    public static final Color C_PATIENT    = Color.web("#FF3B3B");
    public static final Color C_VISITED    = Color.web("#FFD93D");
    public static final Color C_PATH       = Color.web("#39FF14");
    public static final Color C_BUILDING   = Color.web("#0B2545");
    public static final Color C_BUILDING_H = Color.web("#E3F2FD");

    private final Group group = new Group();
    private final Map<Integer, Sphere> nodeShapes = new HashMap<>();
    private final Map<Integer, Box> buildingShapes = new HashMap<>();

    /** Adds a node to the scene as a sphere + small skyscraper. */
    public void addNode(Node node) {
        // Sphere marker
        Sphere sphere = new Sphere(getSphereRadius(node));
        sphere.setTranslateX(node.getX());
        sphere.setTranslateY(node.getY());
        sphere.setTranslateZ(node.getZ());
        sphere.setMaterial(materialFor(node));

        // Building (Box) sitting on the node
        double bh = buildingHeight(node);
        double bw = buildingWidth(node);
        Box building = new Box(bw, bh, bw);
        building.setTranslateX(node.getX());
        building.setTranslateY(node.getY() - bh / 2 - 8); // sits above ground
        building.setTranslateZ(node.getZ());
        building.setMaterial(buildingMaterialFor(node));

        nodeShapes.put(node.getId(), sphere);
        buildingShapes.put(node.getId(), building);
        group.getChildren().addAll(building, sphere);
    }

    /** Re-applies coloring (call after node type / state changes). */
    public void refreshNode(Node node) {
        Sphere s = nodeShapes.get(node.getId());
        if (s != null) s.setMaterial(materialFor(node));

        Box b = buildingShapes.get(node.getId());
        if (b != null) b.setMaterial(buildingMaterialFor(node));
    }

    /** Highlights a node as "currently being visited" by an algorithm. */
    public void highlightVisited(Node node) {
        Sphere s = nodeShapes.get(node.getId());
        if (s != null) s.setMaterial(glowing(C_VISITED));
    }

    /** Highlights a node as part of the final shortest path. */
    public void highlightOnPath(Node node) {
        // Don't overwrite hospital or patient colors -- keep them distinctive
        if (node.getType() == Node.NodeType.HOSPITAL
                || node.getType() == Node.NodeType.PATIENT) {
            return;
        }
        Sphere s = nodeShapes.get(node.getId());
        if (s != null) s.setMaterial(glowing(C_PATH));
    }

    /** Reset all node visuals to their base coloring. */
    public void resetAllVisuals(Iterable<Node> nodes) {
        for (Node n : nodes) refreshNode(n);
    }

    public Sphere getSphere(Node node) { return nodeShapes.get(node.getId()); }

    public Group getGroup() { return group; }

    // ----------------- helpers -----------------

    private PhongMaterial materialFor(Node node) {
        switch (node.getType()) {
            case HOSPITAL:  return glowing(C_HOSPITAL);
            case PATIENT:   return glowing(C_PATIENT);
            case EMERGENCY: return glowing(C_PATIENT);
            default:        return glowing(C_INTERSECT);
        }
    }

    private PhongMaterial buildingMaterialFor(Node node) {
        PhongMaterial m = new PhongMaterial();
        if (node.getType() == Node.NodeType.HOSPITAL) {
            m.setDiffuseColor(C_BUILDING_H);
            m.setSpecularColor(Color.WHITE);
        } else {
            m.setDiffuseColor(C_BUILDING);
            m.setSpecularColor(Color.web("#1ABCFE"));
        }
        return m;
    }

    private PhongMaterial glowing(Color c) {
        PhongMaterial m = new PhongMaterial(c);
        m.setSpecularColor(c.brighter());
        return m;
    }

    private double getSphereRadius(Node node) {
        return node.getType() == Node.NodeType.HOSPITAL ? 18 : 12;
    }

    private double buildingHeight(Node node) {
        return node.getType() == Node.NodeType.HOSPITAL ? 120 : 60 + (node.getId() % 5) * 14;
    }

    private double buildingWidth(Node node) {
        return node.getType() == Node.NodeType.HOSPITAL ? 50 : 32;
    }
}
