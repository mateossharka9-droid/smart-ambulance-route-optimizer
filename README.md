# 3D Smart Ambulance Route Optimizer

A Java + JavaFX 3D emergency-dispatch simulator that uses graph algorithms to route ambulances through a weighted city road network.

This project was developed for a **Data Structures & Algorithms** course and cleaned for GitHub/portfolio presentation. It demonstrates graph representation, graph traversal, shortest-path search, priority queues, path reconstruction, and real-time JavaFX visualization.

---

## Features

- 3D JavaFX city visualization with buildings, roads, hospitals, intersections, and ambulances.
- Weighted undirected graph stored as an adjacency list.
- BFS and DFS traversal visualization.
- Dijkstra shortest path using `PriorityQueue`.
- A* shortest path with a safe scaled Euclidean heuristic.
- Nearest-ambulance selection using shortest-path cost.
- Three ambulances assigned across the available hospitals.
- Patient generation at random city intersections.
- Animated ambulance dispatch from ambulance location to patient, then back to hospital.
- Dijkstra vs A* comparison mode.
- Day/night mode.
- Dispatch alert and siren sound support.
- Live statistics panel showing route, distance, visited nodes, selected algorithm, and execution time.

---

## Algorithms Used

### BFS — Breadth-First Search

- Complexity: `O(V + E)`
- Uses a queue.
- Finds the path with the fewest number of edges in an unweighted graph.
- In this weighted road network, BFS is used mainly for traversal comparison.

### DFS — Depth-First Search

- Complexity: `O(V + E)`
- Uses a stack.
- Explores as deep as possible before backtracking.
- Not guaranteed to find the shortest path.

### Dijkstra

- Complexity: `O((V + E) log V)`
- Uses a priority queue.
- Finds the shortest path in a weighted graph with non-negative edge weights.
- Used as the reliable baseline for ambulance routing.

### A*

- Complexity: `O((V + E) log V)` in this implementation.
- Uses `f(n) = g(n) + h(n)`.
- `g(n)` is the real cost from the start node.
- `h(n)` is the scaled Euclidean estimate to the target.
- Used to compare informed search against Dijkstra.

---

## Project Structure

```text
smart-ambulance-route-optimizer/
├── README.md
├── pom.xml
├── .gitignore
├── LICENSE
├── docs/
│   ├── report.pdf
│   └── screenshots/
│       └── .gitkeep
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── algorithms/
│   │   │   ├── graphics/
│   │   │   ├── main/
│   │   │   ├── model/
│   │   │   ├── ui/
│   │   │   └── utils/
│   │   └── resources/
│   │       └── sounds/
│   │           └── ambulance_siren.wav
│   └── test/
│       └── java/
│           └── algorithms/
└── .github/
    └── workflows/
        └── build.yml
```

---

## Requirements

- Java 21 or newer
- Maven 3.9 or newer

The project uses JavaFX through Maven dependencies, so you do not need to manually download the JavaFX SDK when running with Maven.

---

## How to Run

Clone the repository:

```bash
https://github.com/mateossharka9-droid/smart-ambulance-route-optimizer
cd smart-ambulance-route-optimizer
```

Run the JavaFX app:

```bash
mvn clean javafx:run
```

Run the tests:

```bash
mvn test
```

---

## How to Use the App

1. Click **Generate Patient**.
2. Choose an algorithm: **Dijkstra**, **A\***, **BFS**, or **DFS**.
3. Watch the visited nodes light up.
4. The final route is highlighted.
5. Click **Dispatch Ambulance** to animate the ambulance to the patient and back to the hospital.
6. Use **Compare Dijkstra vs A\*** to compare visited nodes and execution time.
7. Use **Day/Night Mode** to change the visualization theme.

---

## Screenshots

Add your own screenshots in:

```text
docs/screenshots/
```

Recommended screenshots:

- Dashboard startup
- Patient generated
- Dijkstra route highlighted
- A* comparison result
- Ambulance dispatch animation
- Day/night mode

Example markdown after adding screenshots:

```md
![Dashboard](docs/screenshots/dashboard.png)
![Dijkstra Route](docs/screenshots/dijkstra-route.png)
```

---

## Portfolio Description

You can use this text in your CV or GitHub profile:

> Built a JavaFX 3D ambulance routing simulator using graph data structures and pathfinding algorithms. Implemented BFS, DFS, Dijkstra, and A* to compare traversal behavior and shortest-path routing in a weighted road network. The system includes multiple ambulances, nearest-ambulance selection, animated route visualization, day/night mode, sound effects, and live algorithm statistics.

---

## Suggested GitHub Topics

```text
java javafx dsa graph-algorithms dijkstra astar bfs dfs pathfinding ambulance-dispatch data-structures algorithms
```

---

## Academic Note

This repository is based on a university DSA project. If the project was completed by a team, list the team members and your contribution clearly in your GitHub description, CV, or portfolio.

Example:

> Team university project. My contribution focused on A* explanation/comparison, graph algorithm analysis, route visualization, and GitHub-ready project cleanup.

---

## Future Improvements

- Add real city map data.
- Add traffic-based dynamic weights.
- Add hospital capacity and ambulance availability.
- Add emergency priority levels.
- Add save/load graph from JSON or CSV.
- Add more automated tests.
- Add screenshots and a short demo GIF.

---

## License

This project is licensed under the MIT License.
