package src.hybrid;

import src.core.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HybridRunner {

    public static void main(String[] args) throws Exception {

        int[] vertexSizes = {4, 15, 100, 200};

        // Hybrid configs (cores × threads)
        int[][] configs = {
                {1, 1},
                {2, 2},
                {2, 3},
                {3, 4}
        };

        createHeaderIfNeeded();

        for (int v : vertexSizes) {

            String graphPath = "data/graphs/graph_" + v + ".txt";

            Graph graph = loadGraph(graphPath);

            for (int[] config : configs) {

                int cores = config[0];
                int threadsPerCore = config[1];
                int totalThreads = cores * threadsPerCore;

                System.out.println("-----------------------------------");
                System.out.println("Graph: " + v +
                        " | Cores: " + cores +
                        " | Threads/Core: " + threadsPerCore);

                long startTime = System.nanoTime();

                ExecutorService executor =
                        Executors.newFixedThreadPool(totalThreads);

                AtomicInteger totalCliques =
                        new AtomicInteger(0);

                for (int i = 0; i < graph.getVertices(); i++) {

                    final int vertex = i;

                    executor.submit(() -> {

                        BronKerbosch bk =
                                new BronKerbosch(graph);

                        bk.runFromVertex(vertex);

                        totalCliques.addAndGet(
                                bk.getCliqueCount());
                    });
                }

                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.HOURS);

                long endTime = System.nanoTime();

                double executionTime =
                        (endTime - startTime) / 1e9;

                saveFinalResult(
                        v,
                        cores,
                        threadsPerCore,
                        totalThreads,
                        executionTime,
                        totalCliques.get()
                );

                System.out.println("Execution Time: "
                        + executionTime + " sec");
                System.out.println("Maximal Cliques: "
                        + totalCliques.get());
            }
        }

        System.out.println("\nHybrid execution complete.");
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
    // HEADER
    // --------------------------------------------------
    private static void createHeaderIfNeeded()
            throws IOException {

        File file = new File("results/hybrid.csv");

        if (!file.exists()) {

            File dir = new File("results");
            if (!dir.exists()) dir.mkdir();

            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file));

            writer.write("vertices,cores,threads_per_core,"
                    + "total_threads,execution_time,cliques");

            writer.newLine();
            writer.close();
        }
    }

    // --------------------------------------------------
    // SAVE RESULT
    // --------------------------------------------------
    private static void saveFinalResult(
            int vertices,
            int cores,
            int threadsPerCore,
            int totalThreads,
            double time,
            int cliqueCount) throws IOException {

        BufferedWriter writer =
                new BufferedWriter(
                        new FileWriter("results/hybrid.csv", true));

        writer.write(vertices + ","
                + cores + ","
                + threadsPerCore + ","
                + totalThreads + ","
                + time + ","
                + cliqueCount);

        writer.newLine();
        writer.close();
    }
}
 
