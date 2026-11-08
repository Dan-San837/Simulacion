import java.io.IOException;

import models.ChiCuadrado;
import models.Kolmogorov;
import models.LCG;
import models.MonteCarloPi;

public final class controller {
    private static final int DEFAULT_SAMPLE_SIZE = 10_000;
    private static final int DEFAULT_INTERVALS = 10;

    private controller() {
    }

    public static void run(String[] args) throws IOException {
        Options options = Options.parse(args);
        if (options.help) {
            view.showUsage();
            return;
        }
        if (options.benchmark) {
            runBenchmark();
            return;
        }
        if (options.sampleSize < options.intervals * 5L) {
            throw new IllegalArgumentException(
                    "n debe ser al menos 5 veces el número de intervalos.");
        }
        double[] values = new LCG(options.seed, options.multiplier, options.increment, options.modulus)
                .generate(options.sampleSize);
        ChiCuadrado.Result chi = ChiCuadrado.test(values, options.intervals);
        Kolmogorov.Result ks = Kolmogorov.test(values);
        long start = System.nanoTime();
        MonteCarloPi.Result pi = MonteCarloPi.estimate(
                new LCG(options.seed, options.multiplier, options.increment, options.modulus),
                options.sampleSize);
        double elapsed = (System.nanoTime() - start) / 1_000_000_000.0;
        view.showAnalysis(values, chi, ks, pi, elapsed, options.alpha,
                options.showSequence, options.csvPath, options.plotPath,
                options.seed, options.multiplier, options.increment, options.modulus);
        if (options.compare) {
            runComparison(options.sampleSize, options.intervals);
        }
    }

    private static void runBenchmark() {
        int[] sizes = {1_000, 10_000, 100_000, 1_000_000};
        double[][] results = new double[sizes.length][3];
        for (int i = 0; i < sizes.length; i++) {
            long start = System.nanoTime();
            MonteCarloPi.Result estimate = MonteCarloPi.estimate(
                    new LCG(LCG.DEFAULT_SEED, LCG.DEFAULT_MULTIPLIER,
                            LCG.DEFAULT_INCREMENT, LCG.DEFAULT_MODULUS), sizes[i]);
            results[i][0] = estimate.estimate;
            results[i][1] = estimate.absoluteError;
            results[i][2] = (System.nanoTime() - start) / 1_000_000_000.0;
        }
        view.showBenchmark(sizes, results);
    }

    private static void runComparison(int sampleSize, int intervals) {
        long[][] configurations = {
                {LCG.DEFAULT_SEED, LCG.DEFAULT_MULTIPLIER, LCG.DEFAULT_INCREMENT, LCG.DEFAULT_MODULUS},
                {1, 16_807, 0, 2_147_483_647},
                {1, 5, 1, 16}
        };
        String[] names = {"LCG predeterminado", "Park-Miller", "LCG período corto"};
        double[][] results = new double[configurations.length][4];
        for (int i = 0; i < configurations.length; i++) {
            double[] sample = new LCG(configurations[i][0], configurations[i][1],
                    configurations[i][2], configurations[i][3]).generate(sampleSize);
            ChiCuadrado.Result chi = ChiCuadrado.test(sample, intervals);
            Kolmogorov.Result ks = Kolmogorov.test(sample);
            results[i] = new double[] {chi.statistic, chi.pValue, ks.statistic, ks.pValue};
        }
        view.showComparison(names, results);
    }

    private static final class Options {
        private int sampleSize = DEFAULT_SAMPLE_SIZE;
        private int intervals = 10;
        private double alpha = 0.05;
        private long seed = LCG.DEFAULT_SEED;
        private long multiplier = LCG.DEFAULT_MULTIPLIER;
        private long increment = LCG.DEFAULT_INCREMENT;
        private long modulus = LCG.DEFAULT_MODULUS;
        private boolean benchmark;
        private boolean compare;
        private boolean showSequence;
        private boolean help;
        private String csvPath;
        private String plotPath;

        static Options parse(String[] args) {
            Options options = new Options();
            for (int i = 0; i < args.length; i++) {
                String argument = args[i];
                switch (argument) {
                    case "--help" -> options.help = true;
                    case "--benchmark" -> options.benchmark = true;
                    case "--compare-configurations" -> options.compare = true;
                    case "--show-sequence" -> options.showSequence = true;
                    case "--n" -> options.sampleSize = Integer.parseInt(requireValue(args, ++i, argument));
                    case "--intervals" -> options.intervals = Integer.parseInt(requireValue(args, ++i, argument));
                    case "--alpha" -> options.alpha = Double.parseDouble(requireValue(args, ++i, argument));
                    case "--seed" -> options.seed = Long.parseLong(requireValue(args, ++i, argument));
                    case "--a" -> options.multiplier = Long.parseLong(requireValue(args, ++i, argument));
                    case "--c" -> options.increment = Long.parseLong(requireValue(args, ++i, argument));
                    case "--m" -> options.modulus = Long.parseLong(requireValue(args, ++i, argument));
                    case "--csv" -> options.csvPath = requireValue(args, ++i, argument);
                    case "--plot" -> options.plotPath = requireValue(args, ++i, argument);
                    default -> throw new IllegalArgumentException("Opción desconocida: " + argument);
                }
            }
            if (options.sampleSize < 1 || options.intervals < 2
                    || !Double.isFinite(options.alpha) || options.alpha <= 0.0 || options.alpha >= 1.0) {
                throw new IllegalArgumentException("n, intervalos o alpha fuera de rango.");
            }
            return options;
        }

        private static String requireValue(String[] args, int index, String option) {
            if (index >= args.length) {
                throw new IllegalArgumentException("Falta el valor para " + option);
            }
            return args[index];
        }
    }
}