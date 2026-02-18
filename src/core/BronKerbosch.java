package src.core;

import java.util.*;

public class BronKerbosch {

    private Graph graph;
    private int cliqueCount;

    public BronKerbosch(Graph graph) {
        this.graph = graph;
        this.cliqueCount = 0;
    }

    public int getCliqueCount() {
        return cliqueCount;
    }

    // Algorithm 1 from your image
    public void run() {

        Set<Integer> A = new HashSet<>();
        Set<Integer> B = new HashSet<>();
        Set<Integer> C = new HashSet<>();

        // B initially contains all vertices
        for (int i = 0; i < graph.getVertices(); i++) {
            B.add(i);
        }

        bronKerbosch(A, B, C);
    }

    private void bronKerbosch(Set<Integer> A,
                              Set<Integer> B,
                              Set<Integer> C) {

        // If B and C are empty → maximal clique found
        if (B.isEmpty() && C.isEmpty()) {
            cliqueCount++;
            return;
        }

        // Make copy of B to avoid concurrent modification
        Set<Integer> Bcopy = new HashSet<>(B);

        for (Integer v : Bcopy) {

            // A ∪ {v}
            Set<Integer> newA = new HashSet<>(A);
            newA.add(v);

            // B ∩ N(v)
            Set<Integer> newB = new HashSet<>(B);
            newB.retainAll(graph.getNeighbors(v));

            // C ∩ N(v)
            Set<Integer> newC = new HashSet<>(C);
            newC.retainAll(graph.getNeighbors(v));

            // Recursive call
            bronKerbosch(newA, newB, newC);

            // B := B \ {v}
            B.remove(v);

            // C := C ∪ {v}
            C.add(v);
        }
    }
}

