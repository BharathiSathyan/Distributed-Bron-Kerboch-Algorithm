package src.core;

import java.util.*;

public class Graph {

    private int vertices;
    private List<Set<Integer>> adjList;

    public Graph(int vertices) {
        this.vertices = vertices;
        adjList = new ArrayList<>();
        for (int i = 0; i < vertices; i++) {
            adjList.add(new HashSet<>());
        }
    }

    public int getVertices() {
        return vertices;
    }

    public void addEdge(int u, int v) {
        if (u == v) return; // no self loop

        adjList.get(u).add(v);
        adjList.get(v).add(u);
    }

    public Set<Integer> getNeighbors(int v) {
        return adjList.get(v);
    }

    public int getEdgeCount() {
        int total = 0;
        for (Set<Integer> neighbors : adjList) {
            total += neighbors.size();
        }
        return total / 2; // because undirected
    }

    public List<Set<Integer>> getAdjList() {
        return adjList;
    }
}
