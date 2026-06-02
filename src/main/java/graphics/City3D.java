package graphics;

import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import model.Ambulance;
import model.Graph;
import model.Node;

import java.util.List;

/** Assembles the entire 3D world and manages day/night lighting. */
public class City3D {

    private final Graph graph;
    private final SubScene subScene;
    private final Group root3D = new Group();
    private final Box ground;
    private final AmbientLight ambient = new AmbientLight();
    private final PointLight key = new PointLight();
    private final PointLight fill = new PointLight();

    private final NodeRenderer nodeRenderer = new NodeRenderer();
    private final RoadRenderer roadRenderer = new RoadRenderer();
    private final AmbulanceRenderer ambulanceRenderer = new AmbulanceRenderer();
    private final CameraController cameraController = new CameraController();

    private boolean nightMode = true;

    public City3D(Graph graph, double width, double height) {
        this.graph = graph;
        ground = buildGround();
        roadRenderer.renderAllRoads(graph);
        for (Node n : graph.getAllNodes()) nodeRenderer.addNode(n);

        root3D.getChildren().addAll(roadRenderer.getGroup(), nodeRenderer.getGroup(), ambulanceRenderer.getGroup());
        addLights();

        subScene = new SubScene(root3D, width, height, true, javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setCamera(cameraController.getCamera());
        cameraController.attachTo(subScene);
        setNightMode(true);
    }

    private Box buildGround() {
        Box g = new Box(3000, 4, 3000);
        g.setTranslateY(8);
        root3D.getChildren().add(g);
        return g;
    }

    private void addLights() {
        key.setTranslateX(400); key.setTranslateY(-800); key.setTranslateZ(-400);
        fill.setTranslateX(-600); fill.setTranslateY(-500); fill.setTranslateZ(600);
        root3D.getChildren().addAll(ambient, key, fill);
    }

    public void setAmbulances(List<Ambulance> ambulances) { ambulanceRenderer.setAmbulances(ambulances); }

    public void toggleDayNight() { setNightMode(!nightMode); }

    public void setNightMode(boolean night) {
        this.nightMode = night;
        PhongMaterial groundMat = new PhongMaterial(night ? Color.web("#0a1429") : Color.web("#d7e8f7"));
        groundMat.setSpecularColor(night ? Color.web("#1ABCFE") : Color.web("#8bb7d8"));
        ground.setMaterial(groundMat);
        subScene.setFill(night ? Color.web("#070d1c") : Color.web("#bfe7ff"));
        ambient.setColor(night ? Color.web("#30394f") : Color.web("#e9f7ff"));
        key.setColor(night ? Color.web("#80c1ff") : Color.web("#ffffff"));
        fill.setColor(night ? Color.web("#ff6b6b") : Color.web("#cfe8ff"));
        roadRenderer.setNightMode(night);
    }

    public boolean isNightMode() { return nightMode; }
    public SubScene getSubScene() { return subScene; }
    public NodeRenderer getNodeRenderer() { return nodeRenderer; }
    public RoadRenderer getRoadRenderer() { return roadRenderer; }
    public AmbulanceRenderer getAmbulanceRenderer() { return ambulanceRenderer; }
    public Graph getGraph() { return graph; }

    public void resetVisuals() {
        roadRenderer.resetRoadColors();
        for (Node n : graph.getAllNodes()) nodeRenderer.refreshNode(n);
    }
}
