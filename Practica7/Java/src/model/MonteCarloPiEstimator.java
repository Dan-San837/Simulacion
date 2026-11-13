package model;

public class MonteCarloPiEstimator {
    public static PiSimulationResult estimatePi(int samples, long seed) {
        LCG rng = new LCG(seed);
        long inside = 0L;

        for (int i = 0; i < samples; i++) {
            double x = rng.random();
            double y = rng.random();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }

        double estimatedPi = 4.0 * inside / samples;
        return new PiSimulationResult(samples, estimatedPi, Math.abs(estimatedPi - Math.PI));
    }
}
