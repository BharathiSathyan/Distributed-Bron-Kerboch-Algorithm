package src.core;

import java.io.*;

public class FinalResultConsolidator {

    private static final String BASE = "results/";
    private static final String OUTPUT = "results/final_consolidated.csv";

    public static void main(String[] args) {
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT));
            writer.write("dataset,model,vertices,processes,threads,execution_time,cliques");
            writer.newLine();

            consolidateFolder("sparse_runs", writer);
            consolidateFolder("dense_runs", writer);

            writer.close();

            System.out.println("======================================");
            System.out.println("FINAL CONSOLIDATED FILE CREATED:");
            System.out.println("-> " + OUTPUT);
            System.out.println("======================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void consolidateFolder(String folder, BufferedWriter writer) throws Exception {

        String path = BASE + folder + "/";

        readSequential(path + "sequential.csv", folder, writer);
        readShared(path + "shared.csv", folder, writer);
        readDistributed(path + "distributed.csv", folder, writer);
        readHybrid(path + "hybrid.csv", folder, writer);
    }

    private static void readSequential(String file, String dataset, BufferedWriter writer) {
        File f = new File(file);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 3) continue;

                int vertices = Integer.parseInt(p[0]);
                double time = Double.parseDouble(p[1]);
                int cliques = Integer.parseInt(p[2]);

                writer.write(dataset + ",Sequential," +
                        vertices + ",1,1," + time + "," + cliques);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error reading Sequential: " + file);
        }
    }

    private static void readShared(String file, String dataset, BufferedWriter writer) {
        File f = new File(file);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header if exists

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");

                int vertices = Integer.parseInt(p[0]);
                int threads = Integer.parseInt(p[1]);
                double time;
                int cliques;

                // Handle BOTH 4-column and 5-column formats
                if (p.length == 4) {
                    time = Double.parseDouble(p[2]);
                    cliques = Integer.parseInt(p[3]);
                } else { // 5 column format (with cores)
                    time = Double.parseDouble(p[3]);
                    cliques = Integer.parseInt(p[4]);
                }

                writer.write(dataset + ",Shared," +
                        vertices + ",1," + threads + "," + time + "," + cliques);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error reading Shared: " + file);
        }
    }

    private static void readDistributed(String file, String dataset, BufferedWriter writer) {
        File f = new File(file);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 4) continue;

                int vertices = Integer.parseInt(p[0]);
                int processes = Integer.parseInt(p[1]);
                double time = Double.parseDouble(p[2]);
                int cliques = Integer.parseInt(p[3]);

                writer.write(dataset + ",Distributed," +
                        vertices + "," + processes + ",1," + time + "," + cliques);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error reading Distributed: " + file);
        }
    }

    private static void readHybrid(String file, String dataset, BufferedWriter writer) {
        File f = new File(file);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] p = line.split(",");
                if (p.length < 5) continue;

                int vertices = Integer.parseInt(p[0]);
                int processes = Integer.parseInt(p[1]);
                int threads = Integer.parseInt(p[2]);
                double time = Double.parseDouble(p[3]);
                int cliques = Integer.parseInt(p[4]);

                writer.write(dataset + ",Hybrid," +
                        vertices + "," + processes + "," + threads + "," +
                        time + "," + cliques);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error reading Hybrid: " + file);
        }
    }
}
