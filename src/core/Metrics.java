package src.core;

public class Metrics {

    public static double computeSpeedup(double sequentialTime,
                                        double parallelTime) {
        return sequentialTime / parallelTime;
    }

    public static double computeEfficiency(double speedup,
                                           int processors) {
        return speedup / processors;
    }
}

