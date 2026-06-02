package utils;

import model.Graph;
import model.Node;
import model.Node.NodeType;

import java.util.List;
import java.util.Random;

/** Builds the demo city graph with 2 Albanian hospitals and varied road weights from 1 to 20. */
public final class GraphGenerator {

    private GraphGenerator() { }

    public static Graph generateCity() {
        Graph g = new Graph();

        Node h1 = new Node(0,  "QSUT Mother Teresa",        0,   0,    0, NodeType.HOSPITAL);
        Node h2 = new Node(14, "American Hospital Tirana", 520,   0, -260, NodeType.HOSPITAL);

        Node n1  = new Node(1,  "Main St & 1st Ave",   200,  0,  100, NodeType.INTERSECTION);
        Node n2  = new Node(2,  "Main St & 2nd Ave",   400,  0,  200, NodeType.INTERSECTION);
        Node n3  = new Node(3,  "Park Square",         150,  0, -150, NodeType.INTERSECTION);
        Node n4  = new Node(4,  "River Crossing",     -200,  0,  150, NodeType.INTERSECTION);
        Node n5  = new Node(5,  "North Plaza",          50,  0,  300, NodeType.INTERSECTION);
        Node n6  = new Node(6,  "East Market",         350,  0, -100, NodeType.INTERSECTION);
        Node n7  = new Node(7,  "West Junction",      -300,  0,  -50, NodeType.INTERSECTION);
        Node n8  = new Node(8,  "South Bridge",        100,  0, -350, NodeType.INTERSECTION);
        Node n9  = new Node(9,  "Industrial Park",     500,  0,   50, NodeType.INTERSECTION);
        Node n10 = new Node(10, "University Gate",    -150,  0,  350, NodeType.INTERSECTION);
        Node n11 = new Node(11, "Old Town",            250,  0, -250, NodeType.INTERSECTION);
        Node n12 = new Node(12, "Harbor Drive",       -400,  0,  100, NodeType.INTERSECTION);
        Node n13 = new Node(13, "Stadium Road",       -100,  0, -300, NodeType.INTERSECTION);

        Node[] all = { h1, h2, n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13 };
        for (Node n : all) g.addNode(n);

        /*
         * Road weights are intentionally varied from 1 to 20.
         * Meaning:
         *  1-6   = fast / short road
         *  7-13  = normal traffic
         *  14-20 = long road or heavy traffic
         *
         * This makes Dijkstra and A* more meaningful because the shortest
         * route is based on realistic travel cost, not only visual distance.
         */
        connect(g, h1, n1, 3);   connect(g, h1, n3, 6);   connect(g, h1, n4, 9);   connect(g, h1, n7, 14);
        connect(g, h2, n6, 4);   connect(g, h2, n9, 7);   connect(g, h2, n11, 11); connect(g, h2, n2, 16);

        connect(g, n1, n2, 5);   connect(g, n1, n5, 8);   connect(g, n2, n6, 12);  connect(g, n2, n9, 18);
        connect(g, n3, n6, 10);  connect(g, n3, n11, 2);  connect(g, n3, n8, 15);  connect(g, n4, n5, 4);
        connect(g, n4, n7, 1);   connect(g, n4, n10, 13); connect(g, n4, n12, 17); connect(g, n5, n10, 6);
        connect(g, n6, n9, 9);   connect(g, n6, n11, 3);  connect(g, n7, n12, 20); connect(g, n7, n13, 12);
        connect(g, n8, n11, 5);  connect(g, n8, n13, 19); connect(g, n9, n11, 7);  connect(g, n10, n12, 10);

        return g;
    }

    private static void connect(Graph g, Node a, Node b, double weight) {
        g.addEdge(a, b, weight);
    }

    public static Node generateRandomPatient(Graph g, Random rng) {
        List<Node> all = g.getAllNodes();
        Node chosen;
        do {
            chosen = all.get(rng.nextInt(all.size()));
        } while (chosen.getType() == NodeType.HOSPITAL);
        chosen.setType(NodeType.PATIENT);
        return chosen;
    }

    public static void clearPatients(Graph g) {
        for (Node n : g.getAllNodes()) {
            if (n.getType() == NodeType.PATIENT || n.getType() == NodeType.EMERGENCY) {
                n.setType(NodeType.INTERSECTION);
            }
        }
    }
}
