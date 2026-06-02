# Project Notes

## What was cleaned for GitHub

- Converted the Eclipse-style structure to a standard Maven layout.
- Moved Java files into `src/main/java`.
- Moved sound assets into `src/main/resources`.
- Added `pom.xml` for Maven build/run.
- Added `.gitignore`.
- Added MIT license.
- Added GitHub Actions workflow.
- Added JUnit tests for core graph algorithms.
- Updated the README for portfolio presentation.
- Fixed the A* heuristic scaling issue.

## A* Fix Summary

The original A* heuristic used raw coordinate distance. Because coordinates are much larger than road weights, the heuristic could overestimate the real path cost. That breaks A* optimality.

The fixed implementation calculates a safe scale:

```text
safeScale = min(edgeWeight / coordinateDistance)
```

Then it uses:

```text
h(n) = euclideanDistance(n, target) * safeScale
```

This makes the heuristic safe for the current graph.
