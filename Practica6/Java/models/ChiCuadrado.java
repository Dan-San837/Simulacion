package models;

public final class ChiCuadrado {
    private ChiCuadrado() {
    }

    public static Result test(double[] values, int intervals) {
        validateSample(values);
        if (intervals < 2 || values.length < intervals * 5L) {
            throw new IllegalArgumentException(
                    "Se requieren al menos 5 observaciones esperadas por intervalo.");
        }
        int[] observed = new int[intervals];
        for (double value : values) {
            validateValue(value);
            int index = Math.min((int) (value * intervals), intervals - 1);
            observed[index]++;
        }

        double expected = (double) values.length / intervals;
        double statistic = 0.0;
        for (int frequency : observed) {
            double difference = frequency - expected;
            statistic += difference * difference / expected;
        }
        int degreesOfFreedom = intervals - 1;
        double pValue = Gamma.regularizedQ(degreesOfFreedom / 2.0, statistic / 2.0);
        return new Result(observed, expected, statistic, degreesOfFreedom, pValue);
    }

    private static void validateSample(double[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("La secuencia no puede estar vacía.");
        }
    }

    private static void validateValue(double value) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            throw new IllegalArgumentException("Los valores deben pertenecer a [0, 1].");
        }
    }

    public static final class Result {
        public final int[] observed;
        public final double expected;
        public final double statistic;
        public final int degreesOfFreedom;
        public final double pValue;

        private Result(int[] observed, double expected, double statistic,
                int degreesOfFreedom, double pValue) {
            this.observed = observed;
            this.expected = expected;
            this.statistic = statistic;
            this.degreesOfFreedom = degreesOfFreedom;
            this.pValue = pValue;
        }
    }
}

final class Gamma {
    private static final double EPSILON = 1e-14;
    private static final double MIN_VALUE = 1e-300;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double[] LANCZOS = {
            676.5203681218851, -1259.1392167224028, 771.32342877765313,
            -176.61502916214059, 12.507343278686905, -0.13857109526572012,
            9.9843695780195716e-6, 1.5056327351493116e-7
    };

    private Gamma() {
    }

    static double regularizedQ(double a, double x) {
        if (x <= 0.0) {
            return 1.0;
        }
        if (x < a + 1.0) {
            return 1.0 - regularizedPSeries(a, x);
        }
        return regularizedQFraction(a, x);
    }

    private static double regularizedPSeries(double a, double x) {
        double term = 1.0 / a;
        double sum = term;
        double denominator = a;
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            denominator++;
            term *= x / denominator;
            sum += term;
            if (Math.abs(term) <= Math.abs(sum) * EPSILON) {
                break;
            }
        }
        return sum * Math.exp(-x + a * Math.log(x) - logGamma(a));
    }

    private static double regularizedQFraction(double a, double x) {
        double b = x + 1.0 - a;
        double c = 1.0 / MIN_VALUE;
        double d = 1.0 / Math.max(Math.abs(b), MIN_VALUE);
        if (b < 0.0) {
            d = -d;
        }
        double h = d;
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            double an = -i * (i - a);
            b += 2.0;
            d = an * d + b;
            if (Math.abs(d) < MIN_VALUE) {
                d = MIN_VALUE;
            }
            c = b + an / c;
            if (Math.abs(c) < MIN_VALUE) {
                c = MIN_VALUE;
            }
            d = 1.0 / d;
            double delta = d * c;
            h *= delta;
            if (Math.abs(delta - 1.0) < EPSILON) {
                break;
            }
        }
        return Math.exp(-x + a * Math.log(x) - logGamma(a)) * h;
    }

    private static double logGamma(double value) {
        if (value < 0.5) {
            return Math.log(Math.PI) - Math.log(Math.sin(Math.PI * value))
                    - logGamma(1.0 - value);
        }
        double z = value - 1.0;
        double x = 0.99999999999980993;
        for (int i = 0; i < LANCZOS.length; i++) {
            x += LANCZOS[i] / (z + i + 1.0);
        }
        double t = z + LANCZOS.length - 0.5;
        return 0.5 * Math.log(2.0 * Math.PI) + (z + 0.5) * Math.log(t) - t + Math.log(x);
    }
}
