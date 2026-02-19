package src.hybrid;

import mpi.*;
import src.core.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HybridRunner {

    public static void main(String[] args) throws Exception {

        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        // Thread configs per process (hybrid tuning)
        int threadsPerProcess = Runtime.getRuntime().availableProcessors() / size;
        if (threadsPerProcess < 1) threadsPerProcess = 1;

        int[] vertexSizes = {4, 15, 100, 200};

        for (int v : vertexSizes) {

            String graphPath = "data/graphs/graph_" + v + ".txt";

            Graph graph = loadGraph(graphPath);

            MPI.COMM_WORLD.Barrier(); // synchronize processes

            long startTime = System.nanoTime();

            ExecutorService executor =
                    Executors.newFixedThreadPool(threadsPerProcess);

            AtomicInteger localCliqueCount = new AtomicInteger(0);

            // Each MPI process handles subset of vertices
            for (int i = 0; i < graph.getVertices(); i++) {

                if (i % size == rank) {

                    final int vertex = i;

                    executor.submit(() -> {
                        BronKerbosch bk = new BronKerbosch(graph);
                        bk.runFromVertex(vertex);
                        localCliqueCount.addAndGet(bk.getCliqueCount());
                    });
                }
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);

            int[] globalCount = new int[1];

            // Reduce all process results to rank 0
            MPI.COMM_WORLD.Reduce(
                    new int[]{localCliqueCount.get()}, 0,
                    globalCount, 0,
                    1, MPI.INT, MPI.SUM, 0
            );

            long endTime = System.nanoTime();

            if (rank == 0) {

                double executionTime = (endTime - startTime) / 1e9;

                saveResult(
                        v,
                        size,
                        threadsPerProcess,
                        executionTime,
                        globalCount[0]
                );

                System.out.println("-----------------------------------");
                System.out.println("Graph: " + v);
                System.out.println("Processes (MPI): " + size);
                System.out.println("Threads per Process: " + threadsPerProcess);
                System.out.println("Execution Time: " + executionTime + " sec");
                System.out.println("Maximal Cliques: " + globalCount[0]);
            }
        }

        MPI.Finalize();
    }

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

    private static void saveResult(int vertices,
                                   int processes,
                                   int threadsPerProcess,
                                   double time,
                                   int cliqueCount) throws IOException {

        File dir = new File("results");
        if (!dir.exists()) dir.mkdir();

        File file = new File("results/hybrid.csv");

        boolean writeHeader = !file.exists();

        BufferedWriter writer =
                new BufferedWriter(new FileWriter(file, true));

        if (writeHeader) {
            writer.write("vertices,processes,threads_per_process,execution_time,cliques");
            writer.newLine();
        }

        writer.write(vertices + "," +
                processes + "," +
                threadsPerProcess + "," +
                time + "," +
                cliqueCount);

        writer.newLine();
        writer.close();
    }
}
