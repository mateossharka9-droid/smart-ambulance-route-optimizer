package ui;

import algorithms.AStar;
import algorithms.BFS;
import algorithms.DFS;
import algorithms.Dijkstra;
import algorithms.PathFinder;
import graphics.City3D;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Ambulance;
import model.Edge;
import model.Graph;
import model.Node;
import utils.GraphGenerator;
import utils.SoundManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Main dashboard that connects UI, graph algorithms, and the 3D scene. */
public class Dashboard extends BorderPane {

    private final Graph graph;
    private final City3D city3D;
    private final ControlPanel controls = new ControlPanel();
    private final StatisticsPanel stats = new StatisticsPanel();
    private final SoundManager sounds = new SoundManager();
    private final Random rng = new Random();

    private final List<Ambulance> ambulances = new ArrayList<>();
    private Timeline patrolTimer;
    private boolean emergencyActive = false;
    private Ambulance selectedAmbulance;
    private Node currentPatient;
    private List<Node> currentPath = new ArrayList<>();
    private String selectedAlgorithm = "Dijkstra";

    public Dashboard(Graph graph, City3D city3D) {
        this.graph = graph;
        this.city3D = city3D;
        createAmbulances();
        city3D.setAmbulances(ambulances);

        setTop(buildHeader());
        setLeft(controls);
        setRight(stats);
        setCenter(buildViewport());
        wireButtons();

        selectedAmbulance = ambulances.get(0);
        city3D.getAmbulanceRenderer().selectAmbulance(selectedAmbulance);
        stats.setCurrent(selectedAmbulance.getCurrentLocation().getName());
        stats.setAmbulance(selectedAmbulance.getName());
        stats.setHospital(selectedAmbulance.getHomeHospital().getName());
        stats.setStatus("Idle patrol active — generate a patient to begin");
        startIdlePatrol();
    }

    private void createAmbulances() {
        List<Node> hospitals = graph.findNodesByType(Node.NodeType.HOSPITAL);
        if (hospitals.isEmpty()) return;
        // Keep 3 ambulances even though the city now has only 2 hospitals.
        // The third ambulance is assigned round-robin to the first hospital.
        for (int i = 0; i < 3; i++) {
            Node home = hospitals.get(i % hospitals.size());
            ambulances.add(new Ambulance(i + 1, "Ambulance " + (i + 1), home));
        }
    }

    /** Keeps ambulances moving on normal patrol when they are not handling an emergency. */
    private void startIdlePatrol() {
        if (patrolTimer != null) patrolTimer.stop();
        patrolTimer = new Timeline(new KeyFrame(Duration.seconds(2.8), e -> moveIdleAmbulancesOnce()));
        patrolTimer.setCycleCount(Timeline.INDEFINITE);
        patrolTimer.play();
    }

    private void moveIdleAmbulancesOnce() {
        for (Ambulance a : ambulances) {
            if (emergencyActive && a == selectedAmbulance) continue;
            if (a.isCarryingPatient()) continue;
            List<Edge> neighbors = graph.getNeighbors(a.getCurrentLocation());
            if (neighbors.isEmpty()) continue;
            Node next = neighbors.get(rng.nextInt(neighbors.size())).getTo();
            city3D.getAmbulanceRenderer().animateAmbulanceAlongPath(
                    a,
                    Arrays.asList(a.getCurrentLocation(), next),
                    1.8,
                    arrived -> {
                        a.setCurrentLocation(arrived);
                        if (a == selectedAmbulance) stats.setCurrent(arrived.getName());
                    },
                    null
            );
        }
    }

    private HBox buildHeader() {
        Label title = new Label("🚑  3D Smart Ambulance Route Optimizer");
        title.setStyle("-fx-text-fill: #e6f1ff; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label subtitle = new Label(" — Dijkstra, A*, BFS, DFS + Multi-Ambulance Dispatch");
        subtitle.setStyle("-fx-text-fill: #1ABCFE; -fx-font-size: 14px;");
        HBox bar = new HBox(title, subtitle);
        bar.setPadding(new Insets(14, 20, 14, 20));
        bar.setStyle("-fx-background-color: linear-gradient(to right, #06101f, #0d1b3a, #06101f);-fx-border-color: #1ABCFE;-fx-border-width: 0 0 2 0;");
        return bar;
    }

    private StackPane buildViewport() {
        StackPane pane = new StackPane(city3D.getSubScene());
        pane.setStyle("-fx-background-color: #070d1c;");
        city3D.getSubScene().widthProperty().bind(pane.widthProperty());
        city3D.getSubScene().heightProperty().bind(pane.heightProperty());
        HBox.setHgrow(pane, Priority.ALWAYS);
        VBox.setVgrow(pane, Priority.ALWAYS);
        return pane;
    }

    private void wireButtons() {
        controls.genPatientBtn.setOnAction(e -> generatePatient());
        controls.dijkstraBtn.setOnAction(e -> runDijkstra(true));
        controls.aStarBtn.setOnAction(e -> runAStar(true));
        controls.compareBtn.setOnAction(e -> compareAlgorithms());
        controls.bfsBtn.setOnAction(e -> runBFS());
        controls.dfsBtn.setOnAction(e -> runDFS());
        controls.dispatchBtn.setOnAction(e -> dispatchAmbulance());
        controls.dayNightBtn.setOnAction(e -> city3D.toggleDayNight());
        controls.muteBtn.setOnAction(e -> { sounds.toggleMute(); stats.setStatus(sounds.isMuted() ? "Sound muted" : "Sound enabled"); });
        controls.resetBtn.setOnAction(e -> resetSimulation());
    }

    private void generatePatient() {
        resetVisualsOnly();
        GraphGenerator.clearPatients(graph);
        currentPatient = GraphGenerator.generateRandomPatient(graph, rng);
        city3D.getNodeRenderer().refreshNode(currentPatient);
        sounds.dispatchAlert();

        selectedAmbulance = chooseNearestAmbulance(currentPatient);
        city3D.getAmbulanceRenderer().selectAmbulance(selectedAmbulance);

        stats.setStatus("Patient at " + currentPatient.getName() + " — nearest ambulance selected; other ambulances continue patrol");
        stats.setAlgorithm("—"); stats.setRoute("—"); stats.setDistance(0); stats.setVisited(0); stats.setExecutionTime(0);
        stats.setAmbulance(selectedAmbulance.getName());
        stats.setHospital(selectedAmbulance.getHomeHospital().getName());
        stats.setCurrent(selectedAmbulance.getCurrentLocation().getName());

        controls.dijkstraBtn.setDisable(false); controls.aStarBtn.setDisable(false); controls.compareBtn.setDisable(false);
        controls.bfsBtn.setDisable(false); controls.dfsBtn.setDisable(false); controls.dispatchBtn.setDisable(true);
    }

    private Ambulance chooseNearestAmbulance(Node patient) {
        Ambulance best = null;
        double bestCost = Double.POSITIVE_INFINITY;
        StringBuilder comparison = new StringBuilder("Nearest ambulance check: ");
        for (Ambulance a : ambulances) {
            Dijkstra d = new Dijkstra(graph);
            List<Node> path = d.findShortestPath(a.getCurrentLocation(), patient);
            double cost = PathFinder.totalWeight(graph, path);
            comparison.append(a.getName()).append("=").append(String.format("%.0f", cost)).append("  ");
            if (!path.isEmpty() && cost < bestCost) { bestCost = cost; best = a; }
        }
        stats.setRoute(comparison.toString());
        return best == null ? ambulances.get(0) : best;
    }

    private void runDijkstra(boolean enableDispatch) {
        if (currentPatient == null) return;
        selectedAlgorithm = "Dijkstra";
        resetVisualsOnly();
        long startTime = System.nanoTime();
        Dijkstra dijkstra = new Dijkstra(graph);
        List<Node> path = dijkstra.findShortestPath(selectedAmbulance.getCurrentLocation(), currentPatient);
        long elapsed = System.nanoTime() - startTime;
        stats.setAlgorithm("Dijkstra — O((V+E) log V)"); stats.setVisited(dijkstra.getVisitedOrder().size()); stats.setExecutionTime(elapsed);
        animateTraversal(dijkstra.getVisitedOrder(), () -> finishPath(path, "Shortest path computed with Dijkstra", enableDispatch));
    }

    private void runAStar(boolean enableDispatch) {
        if (currentPatient == null) return;
        selectedAlgorithm = "A*";
        resetVisualsOnly();
        long startTime = System.nanoTime();
        AStar aStar = new AStar(graph);
        List<Node> path = aStar.findShortestPath(selectedAmbulance.getCurrentLocation(), currentPatient);
        long elapsed = System.nanoTime() - startTime;
        stats.setAlgorithm("A* — O((V+E) log V)"); stats.setVisited(aStar.getVisitedOrder().size()); stats.setExecutionTime(elapsed);
        animateTraversal(aStar.getVisitedOrder(), () -> finishPath(path, "Shortest path computed with A*", enableDispatch));
    }

    private void compareAlgorithms() {
        if (currentPatient == null) return;
        Dijkstra d = new Dijkstra(graph);
        long ds = System.nanoTime();
        List<Node> dPath = d.findShortestPath(selectedAmbulance.getCurrentLocation(), currentPatient);
        long dt = System.nanoTime() - ds;

        AStar a = new AStar(graph);
        long as = System.nanoTime();
        List<Node> aPath = a.findShortestPath(selectedAmbulance.getCurrentLocation(), currentPatient);
        long at = System.nanoTime() - as;

        List<Node> showPath = aPath.isEmpty() ? dPath : aPath;
        highlightPath(showPath);
        currentPath = showPath;
        stats.setAlgorithm("Comparison: Dijkstra vs A*");
        stats.setVisited(d.getVisitedOrder().size() + a.getVisitedOrder().size());
        stats.setExecutionTime(dt + at);
        stats.setDistance(PathFinder.totalWeight(graph, showPath));
        stats.setStatus(String.format("Dijkstra: %d visited, %.3f ms | A*: %d visited, %.3f ms", d.getVisitedOrder().size(), dt/1_000_000.0, a.getVisitedOrder().size(), at/1_000_000.0));
        stats.setRoute(formatRoute(showPath));
        controls.dispatchBtn.setDisable(showPath.isEmpty());
        sounds.routeFound();
    }

    private void finishPath(List<Node> path, String status, boolean enableDispatch) {
        highlightPath(path);
        stats.setDistance(PathFinder.totalWeight(graph, path));
        stats.setRoute(formatRoute(path));
        stats.setStatus(status);
        currentPath = path;
        if (enableDispatch) controls.dispatchBtn.setDisable(path.isEmpty());
        sounds.routeFound();
    }

    private void runBFS() {
        if (currentPatient == null) return;
        resetVisualsOnly();
        BFS bfs = new BFS(graph);
        List<Node> path = bfs.search(selectedAmbulance.getCurrentLocation(), currentPatient);
        stats.setAlgorithm("BFS — O(V + E)"); stats.setVisited(bfs.getVisitedOrder().size()); stats.setExecutionTime(0);
        animateTraversal(bfs.getVisitedOrder(), () -> finishPath(path, "BFS path found, fewest hops", true));
    }

    private void runDFS() {
        if (currentPatient == null) return;
        resetVisualsOnly();
        DFS dfs = new DFS(graph);
        List<Node> path = dfs.search(selectedAmbulance.getCurrentLocation(), currentPatient);
        stats.setAlgorithm("DFS — O(V + E)"); stats.setVisited(dfs.getVisitedOrder().size()); stats.setExecutionTime(0);
        animateTraversal(dfs.getVisitedOrder(), () -> finishPath(path, "DFS path found, not always shortest", true));
    }

    private void dispatchAmbulance() {
        if (currentPatient == null || selectedAmbulance == null) return;
        disableAllButtons(true);
        emergencyActive = true;
        city3D.getAmbulanceRenderer().stopAnimation(selectedAmbulance);
        sounds.startSirenLoop();
        List<Node> outbound = computeSelectedPath(selectedAmbulance.getCurrentLocation(), currentPatient);
        resetVisualsOnly(); highlightPath(outbound);
        stats.setStatus("🚑 " + selectedAmbulance.getName() + " en route to patient...");
        city3D.getAmbulanceRenderer().selectAmbulance(selectedAmbulance);
        city3D.getAmbulanceRenderer().animateAlongPath(outbound, 0.6, arrived -> {
            selectedAmbulance.setCurrentLocation(arrived); stats.setCurrent(arrived.getName());
        }, this::onArrivedAtPatient);
    }

    private List<Node> computeSelectedPath(Node start, Node target) {
        if ("A*".equals(selectedAlgorithm)) return new AStar(graph).findShortestPath(start, target);
        return new Dijkstra(graph).findShortestPath(start, target);
    }

    private void onArrivedAtPatient() {
        sounds.stopSirenLoop();
        stats.setStatus("🩺 Patient picked up — returning to " + selectedAmbulance.getHomeHospital().getName());
        selectedAmbulance.setCarryingPatient(true);
        List<Node> returnPath = computeSelectedPath(currentPatient, selectedAmbulance.getHomeHospital());
        resetVisualsOnly(); highlightPath(returnPath); sounds.startSirenLoop();
        city3D.getAmbulanceRenderer().animateAlongPath(returnPath, 0.6, arrived -> {
            selectedAmbulance.setCurrentLocation(arrived); stats.setCurrent(arrived.getName());
        }, this::onReturnedToHospital);
    }

    private void onReturnedToHospital() {
        sounds.stopSirenLoop();
        emergencyActive = false;
        selectedAmbulance.setCarryingPatient(false);
        stats.setStatus("✅ Mission complete — ambulance back at hospital; idle patrol resumed");
        controls.genPatientBtn.setDisable(false); controls.resetBtn.setDisable(false);
    }

    private void resetSimulation() {
        sounds.stopSirenLoop();
        emergencyActive = false;
        city3D.getAmbulanceRenderer().stopAllAnimations();
        GraphGenerator.clearPatients(graph);
        currentPatient = null; currentPath = new ArrayList<>(); selectedAlgorithm = "Dijkstra";
        city3D.resetVisuals();
        for (Ambulance a : ambulances) {
            a.setCurrentLocation(a.getHomeHospital()); a.resetDistance(); a.setCarryingPatient(false);
            city3D.getAmbulanceRenderer().placeAmbulance(a, a.getHomeHospital());
        }
        selectedAmbulance = ambulances.get(0); city3D.getAmbulanceRenderer().selectAmbulance(selectedAmbulance);
        stats.setAlgorithm("—"); stats.setDistance(0); stats.setVisited(0); stats.setExecutionTime(0); stats.setRoute("—");
        stats.setCurrent(selectedAmbulance.getCurrentLocation().getName()); stats.setAmbulance(selectedAmbulance.getName()); stats.setHospital(selectedAmbulance.getHomeHospital().getName());
        stats.setStatus("Idle patrol active — generate a patient to begin");
        disableAllButtons(false);
        controls.dijkstraBtn.setDisable(true); controls.aStarBtn.setDisable(true); controls.compareBtn.setDisable(true); controls.bfsBtn.setDisable(true); controls.dfsBtn.setDisable(true); controls.dispatchBtn.setDisable(true);
    }

    private void animateTraversal(List<Node> order, Runnable whenDone) {
        double stepSec = 0.12;
        for (int i = 0; i < order.size(); i++) {
            final Node n = order.get(i);
            PauseTransition pt = new PauseTransition(Duration.seconds(stepSec * (i + 1)));
            pt.setOnFinished(ev -> city3D.getNodeRenderer().highlightVisited(n)); pt.play();
        }
        PauseTransition end = new PauseTransition(Duration.seconds(stepSec * (order.size() + 1)));
        end.setOnFinished(ev -> whenDone.run()); end.play();
    }

    private void highlightPath(List<Node> path) {
        city3D.resetVisuals(); city3D.getRoadRenderer().highlightPath(path);
        for (Node n : path) city3D.getNodeRenderer().highlightOnPath(n);
        for (Node h : graph.findNodesByType(Node.NodeType.HOSPITAL)) city3D.getNodeRenderer().refreshNode(h);
        if (currentPatient != null) city3D.getNodeRenderer().refreshNode(currentPatient);
    }

    private void resetVisualsOnly() { city3D.resetVisuals(); }

    private String formatRoute(List<Node> path) {
        if (path == null || path.isEmpty()) return "(no path)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++) {
            sb.append(path.get(i).getName()); if (i < path.size() - 1) sb.append(" → ");
        }
        return sb.toString();
    }

    private void disableAllButtons(boolean disabled) {
        controls.genPatientBtn.setDisable(disabled);
        controls.dijkstraBtn.setDisable(disabled || currentPatient == null);
        controls.aStarBtn.setDisable(disabled || currentPatient == null);
        controls.compareBtn.setDisable(disabled || currentPatient == null);
        controls.bfsBtn.setDisable(disabled || currentPatient == null);
        controls.dfsBtn.setDisable(disabled || currentPatient == null);
        controls.dispatchBtn.setDisable(disabled || currentPath == null || currentPath.isEmpty());
        controls.resetBtn.setDisable(disabled);
        controls.dayNightBtn.setDisable(false);
        controls.muteBtn.setDisable(false);
    }
}
