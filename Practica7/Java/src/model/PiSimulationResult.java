package model;

public class PiSimulationResult {
    private final int samples;
    private final double estimatedPi;
    private final double absoluteError;
    private final double executionTime;

    public PiSimulationResult(int samples, double estimatedPi, double absoluteError) {
        this(samples, estimatedPi, absoluteError, 0.0);
    }

    public PiSimulationResult(int samples, double estimatedPi, double absoluteError, double executionTime) {
        this.samples = samples;
        this.estimatedPi = estimatedPi;
        this.absoluteError = absoluteError;
        this.executionTime = executionTime;
    }

    public int getSamples() {
        return samples;
    }

    public double getEstimatedPi() {
        return estimatedPi;
    }

    public double getAbsoluteError() {
        return absoluteError;
    }

    public double getExecutionTime() {
        return executionTime;
    }
}
