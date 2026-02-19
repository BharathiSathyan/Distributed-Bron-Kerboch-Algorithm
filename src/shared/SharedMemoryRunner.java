package src.shared;

import src.core.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedMemoryRunner {

    public static void main(String[] args) throws Exception {

        int[] vertexSizes = {4, 15, 100, 200};

        int availableCores = Runtime.getRuntime().availableProcessors();

        createHeaderIfNeeded();

        for (int v : vertexSizes) {

            // -------------------------------
            // Dynamic Thread Configuration
            // -------------------------------
            int[] threadConfigs;

            if (v <= 15) {
                // Small graphs - full scaling analysis
                threadConfigs = new int[]{1, 2, 4, availableCores};
            } else if (v == 100) {
                // Medium graph - balanced configs
                threadConfigs = new int[]{1, 4, availableCores};
            } else {
                // Large graph (200) - time efficient + scientific baseline
                threadConfigs = new int[]{availableCores};
            }

            String graphPath = "data/graphs/graph_" + v + ".txt";

            Graph graph = loadGraph(graphPath);

            for (int threadCount : threadConfigs) {

                System.out.println("-----------------------------------");
                System.out.println("Graph: " + v +
                        " | Threads: " + threadCount +
                        " | Available Cores: " + availableCores);

                long startTime = System.nanoTime();

                ExecutorService executor =
                        Executors.newFixedThreadPool(threadCount);

                AtomicInteger totalCliques =
                        new AtomicInteger(0);

                for (int i = 0; i < graph.getVertices(); i++) {

                    final int vertex = i;

                    executor.submit(() -> {
                        BronKerbosch bk = new BronKerbosch(graph);
                        bk.runFromVertex(vertex);
                        totalCliques.addAndGet(bk.getCliqueCount());
                    });
                }

                executor.shutdown();

                // Increased timeout for dense graphs (important)
                executor.awaitTermination(2, TimeUnit.HOURS);

                long endTime = System.nanoTime();

                double executionTime =
                        (endTime - startTime) / 1e9;

                saveFinalResult(
                        v,
                        threadCount,
                        availableCores,
                        executionTime,
                        totalCliques.get()
                );

                System.out.println("Execution Time: "
                        + executionTime + " sec");

                System.out.println("Maximal Cliques: "
                        + totalCliques.get());
            }
        }

        System.out.println("\nShared memory execution complete.");
    }

    // --------------------------------------------------
    // LOAD GRAPH
    // --------------------------------------------------
    private static Graph loadGraph(String filePath)
            throws IOException {

        BufferedReader reader =
                new BufferedReader(new FileReader(filePath));

        int vertices =
                Integer.parseInt(reader.readLine());

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

    // --------------------------------------------------
    // CREATE HEADER (ONLY ONCE)
    // --------------------------------------------------
    private static void createHeaderIfNeeded()
            throws IOException {

        File file = new File("results/shared.csv");

        if (!file.exists()) {

            File dir = new File("results");
            if (!dir.exists()) dir.mkdir();

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file));

            writer.write("vertices,threads,cores,execution_time,cliques");
            writer.newLine();

            writer.close();
        }
    }

    // --------------------------------------------------
    // SAVE RESULT
    // --------------------------------------------------
    private static void saveFinalResult(
            int vertices,
            int threads,
            int cores,
            double time,
            int cliqueCount) throws IOException {

        BufferedWriter writer =
                new BufferedWriter(
                        new FileWriter("results/shared.csv", true));

        writer.write(
                vertices + "," +
                threads + "," +
                cores + "," +
                time + "," +
                cliqueCount
        );

        writer.newLine();
        writer.close();
    }
}
