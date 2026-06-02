package graphics;

import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * CameraController.java
 *
 * Wraps a PerspectiveCamera with three transforms (Y rotation, X rotation,
 * Z translation) and binds mouse drag + scroll wheel to orbit/zoom the city.
 *
 * Controls:
 *   - Left-drag : orbit around city
 *   - Scroll    : zoom in / out
 */
public class CameraController {

    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate rotateY = new Rotate(-30, Rotate.Y_AXIS);
    private final Rotate rotateX = new Rotate(-25, Rotate.X_AXIS);
    private final Translate translate = new Translate(0, -200, -1400);

    private double anchorX, anchorY;
    private double anchorAngleX, anchorAngleY;

    public CameraController() {
        camera.getTransforms().addAll(rotateY, rotateX, translate);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setFieldOfView(45);
    }

    /** Hooks up mouse / scroll handlers on the given SubScene. */
    public void attachTo(SubScene scene) {
        scene.setOnMousePressed(e -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.setOnMouseDragged(e -> {
            double dx = e.getSceneX() - anchorX;
            double dy = e.getSceneY() - anchorY;
            rotateY.setAngle(anchorAngleY - dx * 0.3);
            // Clamp X rotation so the camera doesn't flip upside-down
            double newX = anchorAngleX - dy * 0.3;
            newX = Math.max(-80, Math.min(10, newX));
            rotateX.setAngle(newX);
        });

        scene.setOnScroll(e -> {
            double delta = e.getDeltaY();
            double newZ = translate.getZ() + delta * 1.5;
            // Clamp zoom range
            newZ = Math.max(-3000, Math.min(-300, newZ));
            translate.setZ(newZ);
        });
    }

    public PerspectiveCamera getCamera() { return camera; }
}
