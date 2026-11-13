package controller;

import model.MonteCarloPiEstimator;
import model.PiSimulationResult;
import model.TrafficQueueModel;
import model.TrafficSummary;

import java.util.ArrayList;
import java.util.List;

public class SimulationController {
    public List<PiSimulationResult> runPiExperiment(int[] sampleSizes) {
        List<PiSimulationResult> results = new ArrayList<>();

        for (int size : sampleSizes) {
            long start = System.nanoTime();
            PiSimulationResult result = MonteCarloPiEstimator.estimatePi(size, 12345L + size);
            double elapsedSeconds = (System.nanoTime() - start) / 1_000_000_000.0;
            results.add(new PiSimulationResult(size, result.getEstimatedPi(), result.getAbsoluteError(), elapsedSeconds));
        }

        return results;
    }

    public TrafficSummary runTrafficExperiment(double lambdaRate, double muRate, double redDuration, double greenDuration, double dt, int simulations) {
        TrafficQueueModel model = new TrafficQueueModel(lambdaRate, muRate, redDuration, greenDuration, dt, simulations);
        return model.run();
    }
}
