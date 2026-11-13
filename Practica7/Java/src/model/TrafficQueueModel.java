package model;

public class TrafficQueueModel {
    private final double lambdaRate;
    private final double muRate;
    private final double redDuration;
    private final double greenDuration;
    private final double dt;
    private final int simulations;

    public TrafficQueueModel(double lambdaRate, double muRate, double redDuration, double greenDuration, double dt, int simulations) {
        this.lambdaRate = lambdaRate;
        this.muRate = muRate;
        this.redDuration = redDuration;
        this.greenDuration = greenDuration;
        this.dt = dt;
        this.simulations = simulations;
    }

    public TrafficSummary run() {
        int totalSteps = (int) Math.round((redDuration + greenDuration) / dt);
        double[] queueValues = new double[simulations];
        double[] waitingTimes = new double[simulations];
        double[] queueSumsByStep = new double[totalSteps];
        double arrivalsTotal = 0.0;
        double departuresTotal = 0.0;

        for (int sim = 0; sim < simulations; sim++) {
            LCG rng = new LCG(2024L + sim);
            double queue = 0.0;
            double waitingTime = 0.0;

            for (int step = 0; step < totalSteps; step++) {
                double elapsedTime = step * dt;
                int arrivals = poisson(lambdaRate * dt, rng);
                queue += arrivals;
                arrivalsTotal += arrivals;
                waitingTime += queue * dt;

                if (elapsedTime >= redDuration) {
                    double dischargeCapacity = muRate * dt;
                    double departures = Math.min(queue, dischargeCapacity);
                    queue = Math.max(0.0, queue - departures);
                    departuresTotal += departures;
                }

                queueSumsByStep[step] += queue;
            }

            queueValues[sim] = queue;
            waitingTimes[sim] = waitingTime;
        }

        double averageQueue = 0.0;
        double averageWaitingTime = 0.0;
        for (int i = 0; i < simulations; i++) {
            averageQueue += queueValues[i];
            averageWaitingTime += waitingTimes[i];
        }
        averageQueue /= simulations;
        averageWaitingTime /= simulations;

        double finalQueue = queueValues[simulations - 1];
        double totalCycleTime = redDuration + greenDuration;
        double[] timePoints = new double[totalSteps];
        double[] averageQueueByTime = new double[totalSteps];
        for (int step = 0; step < totalSteps; step++) {
            timePoints[step] = (step + 1) * dt;
            averageQueueByTime[step] = queueSumsByStep[step] / simulations;
        }

        return new TrafficSummary(
            lambdaRate,
            muRate,
            redDuration,
            greenDuration,
            dt,
            simulations,
            averageQueue,
            averageWaitingTime,
            finalQueue,
            totalCycleTime,
            arrivalsTotal,
            departuresTotal,
            timePoints,
            averageQueueByTime
        );
    }

    private int poisson(double lambda, LCG rng) {
        if (lambda <= 0.0) {
            return 0;
        }

        double limit = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;
        while (p > limit) {
            k++;
            p *= rng.random();
        }
        return k;
    }
}
