import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;

import models.ChiCuadrado;
import models.Kolmogorov;
import models.MonteCarloPi;

public final class view {
    private view() {
    }

    private static void showSection(String title) {
        System.out.println();
        System.out.println("====================================================================");
        System.out.println("  " + title);
        System.out.println("====================================================================");
    }

    public static void showAnalysis(double[] values, ChiCuadrado.Result chi,
            Kolmogorov.Result ks, MonteCarloPi.Result pi, double piSeconds,
            double alpha, boolean showSequence, String csvPath, String plotPath,
            long seed, long multiplier, long increment, long modulus) throws IOException {
        showSection("ANÁLISIS DE SECUENCIA PSEUDOALEATORIA");
        System.out.printf(Locale.ROOT, "%-24s: %,d%n", "Tamaño de muestra", values.length);
        System.out.printf(Locale.ROOT, "%-24s: %d%n", "Semilla", seed);
        System.out.printf(Locale.ROOT, "%-24s: %d%n", "Multiplicador (a)", multiplier);
        System.out.printf(Locale.ROOT, "%-24s: %d%n", "Incremento (c)", increment);
        System.out.printf(Locale.ROOT, "%-24s: %d%n", "Módulo (m)", modulus);

        showSection("SECUENCIA GENERADA");
        System.out.printf("%10s  %24s%n", "Índice", "Valor pseudoaleatorio");
        System.out.printf("%10s  %24s%n", "----------", "------------------------");
        if (showSequence) {
            for (int i = 0; i < values.length; i++) {
                System.out.printf(Locale.ROOT, "%10s  %24.16f%n",
                        String.format(Locale.ROOT, "%,d", i + 1), values[i]);
            }
        } else {
            int previewSize = Math.min(10, values.length);
            for (int i = 0; i < previewSize; i++) {
                System.out.printf(Locale.ROOT, "%10d  %24.16f%n", i + 1, values[i]);
            }
            if (values.length > previewSize) {
                System.out.printf(Locale.ROOT, "  ... %,d valores omitidos (usa --show-sequence para ver todos)%n",
                        values.length - previewSize);
            }
        }

        showSection("PRUEBA CHI-CUADRADO");
        System.out.printf(Locale.ROOT, "%18s  %12s  %12s  %12s%n",
                "Intervalo", "Observada", "Esperada", "Aporte χ²");
        System.out.printf("%18s  %12s  %12s  %12s%n",
                "------------------", "------------", "------------", "------------");
        for (int i = 0; i < chi.observed.length; i++) {
            double lower = (double) i / chi.observed.length;
            double upper = (double) (i + 1) / chi.observed.length;
            double contribution = Math.pow(chi.observed[i] - chi.expected, 2) / chi.expected;
            String closing = i == chi.observed.length - 1 ? "]" : ")";
            String interval = String.format(Locale.ROOT, "[%.1f, %.1f%s", lower, upper, closing);
            System.out.printf(Locale.ROOT, "%18s  %12s  %12.2f  %12.6f%n",
                    interval, String.format(Locale.ROOT, "%,d", chi.observed[i]),
                    chi.expected, contribution);
        }
        System.out.printf(Locale.ROOT, "%-24s: %.6f%n", "Estadístico χ²", chi.statistic);
        System.out.printf(Locale.ROOT, "%-24s: %d%n", "Grados de libertad", chi.degreesOfFreedom);
        System.out.printf(Locale.ROOT, "%-24s: %.8g%n", "Valor p", chi.pValue);
        showDecision("Chi-cuadrado", chi.pValue, alpha);

        showSection("PRUEBA KOLMOGOROV-SMIRNOV");
        System.out.printf(Locale.ROOT, "%-24s: %.8f%n", "Estadístico D", ks.statistic);
        System.out.printf(Locale.ROOT, "%-24s: %.8f%n", "D positiva", ks.positiveDifference);
        System.out.printf(Locale.ROOT, "%-24s: %.8f%n", "D negativa", ks.negativeDifference);
        System.out.printf(Locale.ROOT, "%-24s: %.8g%n", "Valor p (aproximado)", ks.pValue);
        showDecision("Kolmogorov-Smirnov", ks.pValue, alpha);

        showSection("ESTIMACIÓN DE π — MONTE CARLO");
        System.out.printf(Locale.ROOT, "%-36s: %,d%n", "Puntos simulados", pi.sampleCount);
        System.out.printf(Locale.ROOT, "%-36s: %,d%n",
                "Puntos dentro del cuarto de círculo", pi.pointsInside);
        System.out.printf(Locale.ROOT, "%-36s: %.8f%n", "Estimación de π", pi.estimate);
        System.out.printf(Locale.ROOT, "%-36s: %.8f%n", "Error absoluto", pi.absoluteError);
        System.out.printf(Locale.ROOT, "%-36s: %.6f s%n", "Tiempo de ejecución", piSeconds);

        boolean hasOutputs = csvPath != null || plotPath != null;
        if (hasOutputs) {
            showSection("ARCHIVOS GENERADOS");
        }
        if (csvPath != null) {
            writeCsv(values, Path.of(csvPath));
            System.out.println("- Secuencia CSV: " + csvPath);
        }
        if (plotPath != null) {
            writeSvg(values, Path.of(plotPath));
            System.out.println("- Gráfica: " + plotPath);
        }
    }

    public static void showBenchmark(int[] sizes, double[][] results) {
        showSection("BENCHMARK DE ESTIMACIÓN DE π");
        System.out.printf(Locale.ROOT, "%12s  %14s  %16s  %12s%n",
                "N", "π estimado", "Error absoluto", "Tiempo (s)");
        System.out.printf("%12s  %14s  %16s  %12s%n",
                "------------", "--------------", "----------------", "------------");
        for (int i = 0; i < sizes.length; i++) {
            System.out.printf(Locale.ROOT, "%12s  %14.8f  %16.8f  %12.6f%n",
                    String.format(Locale.ROOT, "%,d", sizes[i]),
                    results[i][0], results[i][1], results[i][2]);
        }
    }

    public static void showComparison(String[] names, double[][] values) {
        showSection("COMPARACIÓN DE CONFIGURACIONES LCG");
        System.out.printf(Locale.ROOT, "%-24s  %12s  %12s  %12s  %12s%n",
                "Configuración", "χ²", "p(χ²)", "D(KS)", "p(KS)");
        System.out.printf("%24s  %12s  %12s  %12s  %12s%n",
                "------------------------", "------------", "------------", "------------", "------------");
        for (int i = 0; i < names.length; i++) {
            System.out.printf(Locale.ROOT, "%-24s  %12.6f  %12.6g  %12.8f  %12.6g%n",
                    names[i], values[i][0], values[i][1], values[i][2], values[i][3]);
        }
    }

    public static void showUsage() {
        System.out.println("Uso: java -cp out main [opciones]");
        System.out.println("  --n N --seed N --a N --c N --m N --intervals N --alpha X");
        System.out.println("  --benchmark | --compare-configurations | --show-sequence");
        System.out.println("  --csv ARCHIVO --plot ARCHIVO.svg --help");
    }

    private static void showDecision(String name, double pValue, double alpha) {
        String decision = pValue >= alpha
                ? "no se rechaza H0 (compatible con U(0,1))"
                : "se rechaza H0";
        System.out.printf(Locale.ROOT, "  Decisión (%s): %s (α=%.3f)%n", name, decision, alpha);
    }

    private static void writeCsv(double[] values, Path path) throws IOException {
        try (var writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write("indice,valor\n");
            for (int i = 0; i < values.length; i++) {
                writer.write((i + 1) + "," + Double.toString(values[i]) + "\n");
            }
        }
    }

    private static void writeSvg(double[] values, Path path) throws IOException {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        StringBuilder svg = new StringBuilder("""
                <svg xmlns="http://www.w3.org/2000/svg" width="800" height="500" viewBox="0 0 800 500">
                <rect width="100%" height="100%" fill="white"/>
                <text x="400" y="28" text-anchor="middle" font-family="sans-serif" font-size="18">ECDF vs U(0,1)</text>
                <path d="M 70 430 H 750 M 70 430 V 55" stroke="black" fill="none"/>
                """);
        svg.append("<path d=\"M 70 430 L 750 55\" stroke=\"#d62728\" fill=\"none\" stroke-width=\"2\"/>\n");
        svg.append("<path d=\"M 70 430 ");
        double previousY = 430.0;
        for (int i = 0; i < sorted.length; i++) {
            double x = 70.0 + sorted[i] * 680.0;
            double y = 430.0 - (i + 1.0) / sorted.length * 375.0;
            svg.append(String.format(Locale.ROOT, "L %.3f %.3f L %.3f %.3f ",
                    x, previousY, x, y));
            previousY = y;
        }
        svg.append("\" stroke=\"#1f77b4\" fill=\"none\" stroke-width=\"1.5\"/>\n")
                .append("<text x=\"400\" y=\"475\" text-anchor=\"middle\" font-family=\"sans-serif\">x</text>\n")
                .append("<text x=\"20\" y=\"245\" text-anchor=\"middle\" font-family=\"sans-serif\" "
                        + "transform=\"rotate(-90 20 245)\">F(x)</text>\n")
                .append("<text x=\"575\" y=\"75\" fill=\"#d62728\" font-family=\"sans-serif\">U(0,1)</text>\n")
                .append("<text x=\"575\" y=\"98\" fill=\"#1f77b4\" font-family=\"sans-serif\">ECDF</text>\n")
                .append("</svg>\n");
        Files.writeString(path, svg, StandardCharsets.UTF_8);
    }
}