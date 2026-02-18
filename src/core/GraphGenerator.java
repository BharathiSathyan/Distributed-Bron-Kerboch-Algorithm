package src.core;

import java.util.Random;

public class GraphGenerator {

    private static final long SEED = 42;  // fixed seed for reproducibility

    public static Graph generateGraph(int vertices, double density) {

        Random rand = new Random(SEED);

        Graph graph = new Graph(vertices);

        for (int i = 0; i < vertices; i++) {
            for (int j = i + 1; j < vertices; j++) {

                if (rand.nextDouble() < density) {
                    graph.addEdge(i, j);
                }
            }
        }

        return graph;
    }

    public static double generateRandomDensity(double min, double max) {
        Random rand = new Random(SEED);
        return min + (max - min) * rand.nextDouble();
    }
}

