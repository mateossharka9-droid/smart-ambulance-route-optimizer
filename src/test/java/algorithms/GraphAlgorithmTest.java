package algorithms;

import model.Graph;
import model.Node;
import org.junit.jupiter.api.Test;
import utils.GraphGenerator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphAlgorithmTest {

    @Test
    void generatedCityHasExpectedGraphContent() {
        Graph graph = GraphGenerator.generateCity();

        assertEquals(15, graph.size());
        assertEquals(2, graph.findNodesByType(Node.NodeType.HOSPITAL).size());
        assertFalse(graph.getAllEdges().isEmpty());
    }

    @Test
    void dijkstraFindsKnownShortestPath() {
        Graph graph = GraphGenerator.generateCity();
        Node start = graph.getNode(0);   // QSUT Mother Teresa
        Node target = graph.getNode(9);  // Industrial Park

        Dijkstra dijkstra = new Dijkstra(graph);
        List<Node> path = dijkstra.findShortestPath(start, target);
        double cost = PathFinder.totalWeight(graph, path);

        assertRoute(path, start, target);
        assertEquals(15.0, cost, 0.00001);
        assertEquals("QSUT Mother Teresa", path.get(0).getName());
        assertEquals("Park Square", path.get(1).getName());
        assertEquals("Old Town", path.get(2).getName());
        assertEquals("Industrial Park", path.get(3).getName());
    }

    @Test
    void aStarMatchesDijkstraCostForEveryNodePair() {
        Graph graph = GraphGenerator.generateCity();

        for (Node start : graph.getAllNodes()) {
            for (Node target : graph.getAllNodes()) {
                Dijkstra dijkstra = new Dijkstra(graph);
                List<Node> dijkstraPath = dijkstra.findShortestPath(start, target);
                double dijkstraCost = PathFinder.totalWeight(graph, dijkstraPath);

                AStar aStar = new AStar(graph);
                List<Node> aStarPath = aStar.findShortestPath(start, target);
                double aStarCost = PathFinder.totalWeight(graph, aStarPath);

                assertRoute(aStarPath, start, target);
                assertEquals(dijkstraCost, aStarCost, 0.00001,
                        "A* should match Dijkstra from " + start.getName() + " to " + target.getName());
            }
        }
    }

    @Test
    void bfsAndDfsReturnReachableRoutes() {
        Graph graph = GraphGenerator.generateCity();
        Node start = graph.getNode(0);
        Node target = graph.getNode(13);

        BFS bfs = new BFS(graph);
        List<Node> bfsPath = bfs.search(start, target);
        assertRoute(bfsPath, start, target);
        assertFalse(bfs.getVisitedOrder().isEmpty());

        DFS dfs = new DFS(graph);
        List<Node> dfsPath = dfs.search(start, target);
        assertRoute(dfsPath, start, target);
        assertFalse(dfs.getVisitedOrder().isEmpty());
    }

    private void assertRoute(List<Node> path, Node start, Node target) {
        assertNotNull(path);
        assertFalse(path.isEmpty());
        assertEquals(start, path.get(0));
        assertEquals(target, path.get(path.size() - 1));
    }
}
