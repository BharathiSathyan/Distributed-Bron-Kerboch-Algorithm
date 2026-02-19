package src.distributed;

import mpi.*;
import src.core.*;

import java.io.*;
import java.util.*;

public class DistributedRunner {

    public static void main(String[] args) throws Exception {

        MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int[] vertexSizes = {4, 15, 100, 200};

        for (int v : vertexSizes) {

            String graphPath = "data/graphs/graph_" + v + ".txt";

            Graph graph = loadGraph(graphPath);

            MPI.COMM_WORLD.Barrier();

            long startTime = System.nanoTime();

            BronKerbosch bk = new BronKerbosch(graph);

            // Each process handles vertices where (vertex % size == rank)
            for (int i = 0; i < graph.getVertices(); i++) {

                if (i % size == rank) {
                    bk.runFromVertex(i);
                }
            }

            int localCliqueCount = bk.getCliqueCount();

            int[] globalCount = new int[1];

            MPI.COMM_WORLD.Reduce(
                    new int[]{localCliqueCount}, 0,
                    globalCount, 0,
                    1, MPI.INT, MPI.SUM, 0
            );

            long endTime = System.nanoTime();

            if (rank == 0) {

                double executionTime = (endTime - startTime) / 1e9;

                saveResult(v, size, executionTime, globalCount[0]);

                System.out.println("-----------------------------------");
                System.out.println("Graph: " + v);
                System.out.println("Processes: " + size);
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
                                   double time,
                                   int cliqueCount) throws IOException {

        File dir = new File("results");
        if (!dir.exists()) dir.mkdir();

        BufferedWriter writer =
                new BufferedWriter(new FileWriter("results/distributed.csv", true));

        writer.write(vertices + "," + processes + "," + time + "," + cliqueCount);
        writer.newLine();

        writer.close();
    }
}
