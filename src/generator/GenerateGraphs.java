package src.generator;

import src.core.*;

public class GenerateGraphs {

    public static void main(String[] args) throws Exception {

        int[] vertexSizes = {4, 15, 100, 200};

        double minDensity = 0.5;
        double maxDensity = 1.0;

        for (int vertices : vertexSizes) {

            double density = GraphGenerator.generateRandomDensity(minDensity, maxDensity);

            Graph graph = GraphGenerator.generateGraph(vertices, density);

            String fileName = "data/graphs/graph_" + vertices + ".txt";

            GraphIO.saveGraph(graph, fileName);

            int maxEdges = vertices * (vertices - 1) / 2;
            double actualDensity = (double) graph.getEdgeCount() / maxEdges;

            System.out.println("=================================");
            System.out.println("Graph Generated");
            System.out.println("Vertices: " + vertices);
            System.out.println("Target Density Range: [0.5 - 1.0]");
            System.out.println("Actual Density: " + actualDensity);
            System.out.println("Edges: " + graph.getEdgeCount());
            System.out.println("Saved at: " + fileName);
        }

        System.out.println("All graphs generated successfully.");
    }
}

