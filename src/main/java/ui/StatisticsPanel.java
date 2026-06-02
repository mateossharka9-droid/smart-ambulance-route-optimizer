package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Right-hand live statistics panel. */
public class StatisticsPanel extends VBox {

    private final Label algorithmLbl = new Label("Algorithm: —");
    private final Label ambulanceLbl = new Label("Selected Ambulance: —");
    private final Label hospitalLbl  = new Label("Hospital: —");
    private final Label distanceLbl  = new Label("Total Distance: —");
    private final Label visitedLbl   = new Label("Visited Nodes: —");
    private final Label timeLbl      = new Label("Execution Time: —");
    private final Label currentLbl   = new Label("Ambulance at: —");
    private final Label statusLbl    = new Label("Status: Idle");
    private final Label routeLbl     = new Label("Route: —");

    public StatisticsPanel() {
        setPadding(new Insets(18)); setSpacing(12); setPrefWidth(310);
        setStyle("-fx-background-color: linear-gradient(to bottom, #0d1b3a, #06101f);-fx-border-color: #1ABCFE;-fx-border-width: 0 0 0 2;");
        Label title = new Label("📊 STATISTICS");
        title.setStyle("-fx-text-fill: #1ABCFE; -fx-font-size: 16px; -fx-font-weight: bold;");
        for (Label l : new Label[]{algorithmLbl, ambulanceLbl, hospitalLbl, distanceLbl, visitedLbl, timeLbl, currentLbl, statusLbl, routeLbl}) {
            l.setStyle("-fx-text-fill: #e6f1ff; -fx-font-size: 13px;"); l.setWrapText(true);
        }
        routeLbl.setStyle("-fx-text-fill: #39FF14; -fx-font-size: 12px;");
        getChildren().addAll(title, algorithmLbl, ambulanceLbl, hospitalLbl, distanceLbl, visitedLbl, timeLbl, currentLbl, statusLbl, new Label(), routeLbl);
    }

    public void setAlgorithm(String s) { algorithmLbl.setText("Algorithm: " + s); }
    public void setAmbulance(String s) { ambulanceLbl.setText("Selected Ambulance: " + s); }
    public void setHospital(String s)  { hospitalLbl.setText("Hospital: " + s); }
    public void setDistance(double d)  { distanceLbl.setText(String.format("Total Distance: %.2f units", d)); }
    public void setVisited(int n)      { visitedLbl.setText("Visited Nodes: " + n); }
    public void setExecutionTime(long nanos) { timeLbl.setText(String.format("Execution Time: %.3f ms", nanos / 1_000_000.0)); }
    public void setCurrent(String s)   { currentLbl.setText("Ambulance at: " + s); }
    public void setStatus(String s)    { statusLbl.setText("Status: " + s); }
    public void setRoute(String s)     { routeLbl.setText("Route: " + s); }
}
