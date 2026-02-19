package src.core;

import java.util.*;

public class BronKerbosch {

    private Graph graph;
    private int cliqueCount;
    private int startVertex;


    public BronKerbosch(Graph graph) {
        this.graph = graph;
        this.cliqueCount = 0;
    }

    public int getCliqueCount() {
        return cliqueCount;
    }

    // Run for a single starting vertex
    public void runFromVertex(int v) {

        this.startVertex = v;

        Set<Integer> A = new HashSet<>();
        A.add(v);

        Set<Integer> B = new HashSet<>(graph.getNeighbors(v));
        Set<Integer> C = new HashSet<>();

        bronKerbosch(A, B, C);
    }

    private void bronKerbosch(Set<Integer> A,
                              Set<Integer> B,
                              Set<Integer> C) {

        if (B.isEmpty() && C.isEmpty()) {

            int minVertex = Collections.min(A);

            // Avoid duplicate counting
            if (startVertex == minVertex) {
                cliqueCount++;
            }

            return;
        }

        Set<Integer> Bcopy = new HashSet<>(B);

        for (Integer v : Bcopy) {

            Set<Integer> newA = new HashSet<>(A);
            newA.add(v);

            Set<Integer> newB = new HashSet<>(B);
            newB.retainAll(graph.getNeighbors(v));

            Set<Integer> newC = new HashSet<>(C);
            newC.retainAll(graph.getNeighbors(v));

            bronKerbosch(newA, newB, newC);

            B.remove(v);
            C.add(v);
        }
    }
}
