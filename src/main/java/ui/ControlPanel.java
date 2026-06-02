package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Left control panel. */
public class ControlPanel extends VBox {

    public final Button genPatientBtn  = makeBtn("🚨  Generate Patient",      "#FF3B3B");
    public final Button dijkstraBtn    = makeBtn("⚡  Run Dijkstra",           "#1ABCFE");
    public final Button aStarBtn       = makeBtn("⭐  Run A*",                 "#7CFFCB");
    public final Button compareBtn     = makeBtn("📊  Compare Dijkstra/A*",   "#FDE047");
    public final Button bfsBtn         = makeBtn("🔵  Run BFS",                "#3FA9F5");
    public final Button dfsBtn         = makeBtn("🟣  Run DFS",                "#9C27B0");
    public final Button dispatchBtn    = makeBtn("🚑  Dispatch Ambulance",     "#39FF14");
    public final Button dayNightBtn    = makeBtn("🌙  Day/Night Mode",         "#8B5CF6");
    public final Button muteBtn        = makeBtn("🔊  Mute/Unmute",            "#94A3B8");
    public final Button resetBtn       = makeBtn("♻️  Reset Simulation",       "#FFC107");

    public ControlPanel() {
        setPadding(new Insets(18)); setSpacing(10); setPrefWidth(250); setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b3a, #06101f);-fx-border-color: #1ABCFE;-fx-border-width: 0 2 0 0;");
        Label title = new Label("🚑 CONTROLS");
        title.setStyle("-fx-text-fill: #1ABCFE; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label algoLbl = new Label("Algorithms"); algoLbl.setStyle("-fx-text-fill: #8aa6c0; -fx-font-size: 11px; -fx-padding: 8 0 0 0;");
        Label simLbl = new Label("Simulation"); simLbl.setStyle("-fx-text-fill: #8aa6c0; -fx-font-size: 11px; -fx-padding: 8 0 0 0;");
        dijkstraBtn.setDisable(true); aStarBtn.setDisable(true); compareBtn.setDisable(true); bfsBtn.setDisable(true); dfsBtn.setDisable(true); dispatchBtn.setDisable(true);
        getChildren().addAll(title, simLbl, genPatientBtn, dispatchBtn, dayNightBtn, muteBtn, resetBtn, algoLbl, dijkstraBtn, aStarBtn, compareBtn, bfsBtn, dfsBtn);
    }

    private Button makeBtn(String text, String accent) {
        Button b = new Button(text); b.setMaxWidth(Double.MAX_VALUE);
        String normal = "-fx-background-color: #122644;-fx-text-fill: #e6f1ff;-fx-font-size: 13px;-fx-padding: 10 14 10 14;-fx-border-color: " + accent + ";-fx-border-width: 1;-fx-border-radius: 6;-fx-background-radius: 6;-fx-cursor: hand;";
        String hover = "-fx-background-color: " + accent + ";-fx-text-fill: #06101f;-fx-font-size: 13px;-fx-font-weight: bold;-fx-padding: 10 14 10 14;-fx-border-color: " + accent + ";-fx-border-width: 1;-fx-border-radius: 6;-fx-background-radius: 6;-fx-cursor: hand;";
        b.setStyle(normal); b.setOnMouseEntered(e -> b.setStyle(hover)); b.setOnMouseExited(e -> b.setStyle(normal));
        return b;
    }
}
