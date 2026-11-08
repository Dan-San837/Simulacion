package models;

public final class MonteCarloPi {
    private MonteCarloPi() {
    }

    public static Result estimate(LCG generator, int sampleCount) {
        if (sampleCount < 1) {
            throw new IllegalArgumentException("La cantidad de puntos debe ser positiva.");
        }
        int inside = 0;
        for (int i = 0; i < sampleCount; i++) {
            double x = generator.nextDouble();
            double y = generator.nextDouble();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }
        double estimate = 4.0 * inside / sampleCount;
        return new Result(estimate, Math.abs(estimate - Math.PI), inside, sampleCount);
    }

    public static final class Result {
        public final double estimate;
        public final double absoluteError;
        public final int pointsInside;
        public final int sampleCount;

        private Result(double estimate, double absoluteError, int pointsInside, int sampleCount) {
            this.estimate = estimate;
            this.absoluteError = absoluteError;
            this.pointsInside = pointsInside;
            this.sampleCount = sampleCount;
        }
    }
}
