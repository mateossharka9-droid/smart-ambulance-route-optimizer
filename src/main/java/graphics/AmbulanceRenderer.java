package graphics;

import javafx.animation.Interpolator;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.util.Duration;
import model.Ambulance;
import model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/** Renders ambulances, emergency movement, and non-emergency patrol movement. */
public class AmbulanceRenderer {

    private final Group group = new Group();
    private final Map<Integer, Group> ambulanceGroups = new HashMap<>();
    private final Map<Integer, SequentialTransition> runningAnimations = new HashMap<>();
    private Group activeGroup;
    private final double bodyHeight = 24;

    public AmbulanceRenderer() { }

    public void setAmbulances(List<Ambulance> ambulances) {
        group.getChildren().clear();
        ambulanceGroups.clear();
        runningAnimations.clear();
        int index = 0;
        for (Ambulance a : ambulances) {
            Group model = build(index++);
            placeModelAt(model, a.getCurrentLocation());
            ambulanceGroups.put(a.getId(), model);
            group.getChildren().add(model);
            if (activeGroup == null) activeGroup = model;
        }
    }

    private Group build(int index) {
        Group g = new Group();
        Box body = new Box(60, bodyHeight, 28);
        PhongMaterial red = new PhongMaterial(index == 0 ? Color.web("#E53935") : index == 1 ? Color.web("#FF7043") : Color.web("#D81B60"));
        red.setSpecularColor(Color.web("#FFCDD2"));
        body.setMaterial(red);

        Box cabin = new Box(22, bodyHeight - 4, 26);
        PhongMaterial white = new PhongMaterial(Color.WHITE);
        cabin.setMaterial(white);
        cabin.setTranslateX(-18);

        Sphere light = new Sphere(5);
        PhongMaterial blue = new PhongMaterial(Color.web("#3FA9F5"));
        blue.setSpecularColor(Color.WHITE);
        light.setMaterial(blue);
        light.setTranslateY(-bodyHeight / 2 - 4);

        g.getChildren().addAll(body, cabin, light);
        return g;
    }

    public void selectAmbulance(Ambulance ambulance) {
        activeGroup = ambulanceGroups.get(ambulance.getId());
        for (Map.Entry<Integer, Group> entry : ambulanceGroups.entrySet()) {
            entry.getValue().setScaleX(entry.getKey() == ambulance.getId() ? 1.25 : 1.0);
            entry.getValue().setScaleY(entry.getKey() == ambulance.getId() ? 1.25 : 1.0);
            entry.getValue().setScaleZ(entry.getKey() == ambulance.getId() ? 1.25 : 1.0);
        }
    }

    public void placeAt(Node node) {
        if (activeGroup != null) placeModelAt(activeGroup, node);
    }

    public void placeAmbulance(Ambulance ambulance, Node node) {
        stopAnimation(ambulance);
        Group g = ambulanceGroups.get(ambulance.getId());
        if (g != null) placeModelAt(g, node);
    }

    private void placeModelAt(Group model, Node node) {
        model.setTranslateX(node.getX());
        model.setTranslateY(node.getY() - bodyHeight / 2 - 4);
        model.setTranslateZ(node.getZ());
    }

    public void animateAlongPath(List<Node> path, double secPerSegment, Consumer<Node> onSegmentEnd, Runnable onFinished) {
        if (activeGroup == null || path == null || path.size() < 2) {
            if (onFinished != null) onFinished.run();
            return;
        }
        SequentialTransition seq = buildTransition(activeGroup, path, secPerSegment, onSegmentEnd, onFinished);
        seq.play();
    }

    /** Animates one specific ambulance, used for idle patrol movement too. */
    public void animateAmbulanceAlongPath(Ambulance ambulance, List<Node> path, double secPerSegment, Consumer<Node> onSegmentEnd, Runnable onFinished) {
        Group model = ambulanceGroups.get(ambulance.getId());
        if (model == null || path == null || path.size() < 2) {
            if (onFinished != null) onFinished.run();
            return;
        }
        stopAnimation(ambulance);
        SequentialTransition seq = buildTransition(model, path, secPerSegment, onSegmentEnd, onFinished);
        runningAnimations.put(ambulance.getId(), seq);
        seq.setOnFinished(ev -> {
            runningAnimations.remove(ambulance.getId());
            if (onFinished != null) onFinished.run();
        });
        seq.play();
    }

    public void stopAnimation(Ambulance ambulance) {
        SequentialTransition seq = runningAnimations.remove(ambulance.getId());
        if (seq != null) seq.stop();
    }

    public void stopAllAnimations() {
        for (SequentialTransition seq : runningAnimations.values()) seq.stop();
        runningAnimations.clear();
    }

    private SequentialTransition buildTransition(Group model, List<Node> path, double secPerSegment, Consumer<Node> onSegmentEnd, Runnable onFinished) {
        SequentialTransition seq = new SequentialTransition();
        for (int i = 1; i < path.size(); i++) {
            Node target = path.get(i);
            TranslateTransition tt = new TranslateTransition(Duration.seconds(secPerSegment), model);
            tt.setToX(target.getX());
            tt.setToY(target.getY() - bodyHeight / 2 - 4);
            tt.setToZ(target.getZ());
            tt.setInterpolator(Interpolator.EASE_BOTH);
            final Node arrived = target;
            tt.setOnFinished(ev -> { if (onSegmentEnd != null) onSegmentEnd.accept(arrived); });
            seq.getChildren().add(tt);
        }
        seq.setOnFinished(ev -> { if (onFinished != null) onFinished.run(); });
        return seq;
    }

    public Group getGroup() { return group; }
}
