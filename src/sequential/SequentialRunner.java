package src.sequential;

import src.core.*;

import java.io.*;
// import java.util.*;

public class SequentialRunner {

    public static void main(String[] args) throws Exception {

        int[] vertexSizes = {4, 15, 100, 200};

        for (int v : vertexSizes) {

            String graphPath = "data/graphs/graph_" + v + ".txt";
            String checkpointPath = "results/sequential_checkpoint_" + v + ".txt";

            Graph graph = loadGraph(graphPath);

            int startVertex = readCheckpoint(checkpointPath);

            System.out.println("Starting Graph " + v + " from vertex: " + startVertex);

            long startTime = System.nanoTime();

            BronKerbosch bk = new BronKerbosch(graph);

            for (int i = startVertex; i < graph.getVertices(); i++) {

                bk.runFromVertex(i);

                // Checkpoint every 20 vertices
                if (i % 20 == 0) {
                    System.out.println("Graph " + v + " processed vertex: " + i);
                    writeCheckpoint(checkpointPath, i + 1);
                }
            }

            long endTime = System.nanoTime();

            double executionTime = (endTime - startTime) / 1e9;

            int cliqueCount = bk.getCliqueCount();

            saveFinalResult(v, executionTime, cliqueCount);

            // Delete checkpoint if completed
            new File(checkpointPath).delete();

            System.out.println("-----------------------------------");
            System.out.println("Graph: " + v);
            System.out.println("Execution Time: " + executionTime + " sec");
            System.out.println("Maximal Cliques: " + cliqueCount);
        }

        System.out.println("Sequential execution complete.");
    }

    // Load graph
    private static Graph loadGraph(String filePath) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        int vertices = Integer.parseInt(reader.readLine());
        Graph graph = new Graph(vertices);

        String line;

        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(" ");
            int u = Integer.parseInt(parts[0]);
            int v = Integer.parseInt(parts[1]);

            graph.addEdge(u, v);
        }

        reader.close();
        return graph;
    }

    // Read checkpoint
    private static int readCheckpoint(String path) {

        try {
            File file = new File(path);
            if (!file.exists()) return 0;

            BufferedReader reader = new BufferedReader(new FileReader(file));
            int value = Integer.parseInt(reader.readLine());
            reader.close();
            return value;
        } catch (Exception e) {
            return 0;
        }
    }

    // Write checkpoint
    private static void writeCheckpoint(String path, int vertex) throws IOException {

        File dir = new File("results");
        if (!dir.exists()) dir.mkdir();

        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(String.valueOf(vertex));
        writer.close();
    }

    // Save final result
    private static void saveFinalResult(int vertices,
                                        double time,
                                        int cliqueCount) throws IOException {

        File dir = new File("results");
        if (!dir.exists()) dir.mkdir();

        BufferedWriter writer =
                new BufferedWriter(new FileWriter("results/sequential.csv", true));

        writer.write(vertices + "," + time + "," + cliqueCount);
        writer.newLine();

        writer.close();
    }
}
