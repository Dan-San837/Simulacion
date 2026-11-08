package models;

import java.util.Arrays;

public final class Kolmogorov {
    private Kolmogorov() {
    }

    public static Result test(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("La secuencia no puede estar vacía.");
        }
        double[] sorted = values.clone();
        for (double value : sorted) {
            if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
                throw new IllegalArgumentException("Los valores deben pertenecer a [0, 1].");
            }
        }
        Arrays.sort(sorted);
        double positive = 0.0;
        double negative = 0.0;
        for (int i = 0; i < sorted.length; i++) {
            double rank = i + 1.0;
            positive = Math.max(positive, rank / sorted.length - sorted[i]);
            negative = Math.max(negative, sorted[i] - (rank - 1.0) / sorted.length);
        }
        double statistic = Math.max(positive, negative);
        double rootN = Math.sqrt(sorted.length);
        double lambda = (rootN + 0.12 + 0.11 / rootN) * statistic;
        return new Result(statistic, positive, negative, kolmogorovSurvival(lambda));
    }

    private static double kolmogorovSurvival(double lambda) {
        if (lambda <= 0.0) {
            return 1.0;
        }
        double sum = 0.0;
        if (lambda <= 1.18) {
            for (int i = 1; i <= 100_000; i++) {
                double odd = 2.0 * i - 1.0;
                double term = Math.exp(-odd * odd * Math.PI * Math.PI
                        / (8.0 * lambda * lambda));
                sum += term;
                if (term < 1e-16) {
                    break;
                }
            }
            return Math.max(0.0, Math.min(1.0,
                    1.0 - Math.sqrt(2.0 * Math.PI) * sum / lambda));
        }
        for (int i = 1; i <= 100_000; i++) {
            double term = 2.0 * Math.exp(-2.0 * i * i * lambda * lambda);
            sum += (i % 2 == 1) ? term : -term;
            if (term < 1e-16) {
                break;
            }
        }
        return Math.max(0.0, Math.min(1.0, sum));
    }

    public static final class Result {
        public final double statistic;
        public final double positiveDifference;
        public final double negativeDifference;
        public final double pValue;

        private Result(double statistic, double positiveDifference,
                double negativeDifference, double pValue) {
            this.statistic = statistic;
            this.positiveDifference = positiveDifference;
            this.negativeDifference = negativeDifference;
            this.pValue = pValue;
        }
    }
}