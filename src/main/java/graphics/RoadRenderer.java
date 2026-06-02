package graphics;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import model.Edge;
import model.Graph;
import model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Renders graph edges as 3D road boxes and displays each road weight as text. */
public class RoadRenderer {

    private static final Color ROAD_COLOR = Color.web("#444a55");
    private static final Color ROAD_GLOW  = Color.web("#1ABCFE");
    private static final Color PATH_COLOR = Color.web("#39FF14");

    private final Group group = new Group();
    private final Map<String, Box> roadShapes = new HashMap<>();
    private final Map<String, Text> weightLabels = new HashMap<>();

    public void renderAllRoads(Graph graph) {
        group.getChildren().clear();
        roadShapes.clear();
        weightLabels.clear();
        for (Edge e : graph.getAllEdges()) addRoad(e);
    }

    private void addRoad(Edge e) {
        Node a = e.getFrom();
        Node b = e.getTo();
        double dx = b.getX() - a.getX();
        double dz = b.getZ() - a.getZ();
        double length = Math.sqrt(dx * dx + dz * dz);

        Box road = new Box(length, 4, 10);
        PhongMaterial mat = new PhongMaterial(ROAD_COLOR);
        mat.setSpecularColor(ROAD_GLOW);
        road.setMaterial(mat);
        road.setTranslateX((a.getX() + b.getX()) / 2.0);
        road.setTranslateY(2);
        road.setTranslateZ((a.getZ() + b.getZ()) / 2.0);
        double angleDeg = Math.toDegrees(Math.atan2(dz, dx));
        road.getTransforms().add(new Rotate(-angleDeg, Rotate.Y_AXIS));

        Text label = new Text(String.format("%.0f", e.getWeight()));
        label.setFill(Color.web("#E6F1FF"));
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        label.setTranslateX((a.getX() + b.getX()) / 2.0);
        label.setTranslateY(-12);
        label.setTranslateZ((a.getZ() + b.getZ()) / 2.0);
        label.getTransforms().add(new Rotate(-60, Rotate.X_AXIS));

        roadShapes.put(key(a, b), road);
        weightLabels.put(key(a, b), label);
        group.getChildren().addAll(road, label);
    }

    public void highlightPath(List<Node> path) {
        if (path == null || path.size() < 2) return;
        for (int i = 0; i < path.size() - 1; i++) {
            String k = key(path.get(i), path.get(i + 1));
            Box road = roadShapes.get(k);
            if (road != null) {
                PhongMaterial mat = new PhongMaterial(PATH_COLOR);
                mat.setSpecularColor(PATH_COLOR.brighter());
                road.setMaterial(mat);
            }
            Text label = weightLabels.get(k);
            if (label != null) label.setFill(PATH_COLOR);
        }
    }

    public void resetRoadColors() {
        for (Box road : roadShapes.values()) {
            PhongMaterial mat = new PhongMaterial(ROAD_COLOR);
            mat.setSpecularColor(ROAD_GLOW);
            road.setMaterial(mat);
        }
        for (Text label : weightLabels.values()) label.setFill(Color.web("#E6F1FF"));
    }

    public void setNightMode(boolean night) {
        for (Text label : weightLabels.values()) label.setFill(night ? Color.web("#7CFFCB") : Color.web("#111827"));
    }

    public Group getGroup() { return group; }

    private String key(Node a, Node b) {
        int lo = Math.min(a.getId(), b.getId());
        int hi = Math.max(a.getId(), b.getId());
        return lo + "," + hi;
    }
}
