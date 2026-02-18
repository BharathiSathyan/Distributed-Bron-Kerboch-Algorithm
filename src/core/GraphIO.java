package src.core;

import java.io.*;

public class GraphIO {

    public static void saveGraph(Graph graph, String filePath) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

        writer.write(graph.getVertices() + "\n");

        for (int i = 0; i < graph.getVertices(); i++) {
            for (int neighbor : graph.getNeighbors(i)) {
                if (i < neighbor) {
                    writer.write(i + " " + neighbor + "\n");
                }
            }
        }

        writer.close();
    }
}
