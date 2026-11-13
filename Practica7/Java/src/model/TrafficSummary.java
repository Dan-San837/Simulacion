package model;

public class TrafficSummary {
    private final double lambdaRate;
    private final double muRate;
    private final double redDuration;
    private final double greenDuration;
    private final double dt;
    private final int simulations;
    private final double averageQueue;
    private final double averageWaitingTime;
    private final double finalQueue;
    private final double totalCycleTime;
    private final double totalArrivals;
    private final double totalDepartures;
    private final double[] timePoints;
    private final double[] averageQueueByTime;

    public TrafficSummary(double lambdaRate, double muRate, double redDuration, double greenDuration,
                          double dt, int simulations, double averageQueue, double averageWaitingTime,
                          double finalQueue, double totalCycleTime, double totalArrivals, double totalDepartures,
                          double[] timePoints, double[] averageQueueByTime) {
        this.lambdaRate = lambdaRate;
        this.muRate = muRate;
        this.redDuration = redDuration;
        this.greenDuration = greenDuration;
        this.dt = dt;
        this.simulations = simulations;
        this.averageQueue = averageQueue;
        this.averageWaitingTime = averageWaitingTime;
        this.finalQueue = finalQueue;
        this.totalCycleTime = totalCycleTime;
        this.totalArrivals = totalArrivals;
        this.totalDepartures = totalDepartures;
        this.timePoints = timePoints.clone();
        this.averageQueueByTime = averageQueueByTime.clone();
    }

    public double getLambdaRate() { return lambdaRate; }
    public double getMuRate() { return muRate; }
    public double getRedDuration() { return redDuration; }
    public double getGreenDuration() { return greenDuration; }
    public double getDt() { return dt; }
    public int getSimulations() { return simulations; }
    public double getAverageQueue() { return averageQueue; }
    public double getAverageWaitingTime() { return averageWaitingTime; }
    public double getFinalQueue() { return finalQueue; }
    public double getTotalCycleTime() { return totalCycleTime; }
    public double getTotalArrivals() { return totalArrivals; }
    public double getTotalDepartures() { return totalDepartures; }
    public double[] getTimePoints() { return timePoints.clone(); }
    public double[] getAverageQueueByTime() { return averageQueueByTime.clone(); }
}
