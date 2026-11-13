package view;

import model.PiSimulationResult;
import model.TrafficSummary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SvgChartView {
    private static final int WIDTH = 1100;
    private static final String BLUE = "#2563eb";
    private static final String RED = "#dc2626";

    public static Path writeCharts(Path outputPath, List<PiSimulationResult> piResults,
                                   TrafficSummary trafficSummary) throws IOException {
        StringBuilder svg = new StringBuilder();
        svg.append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"1100\" height=\"1480\" viewBox=\"0 0 1100 1480\">\n")
            .append("<style>text{font-family:Arial,sans-serif;fill:#1f2937}.grid{stroke:#d1d5db;stroke-width:1}.axis{stroke:#374151;stroke-width:1.5}.series{fill:none;stroke-width:3}.point{stroke:#fff;stroke-width:2}</style>\n")
            .append("<rect width=\"100%\" height=\"100%\" fill=\"#fff\"/>\n")
            .append("<text x=\"550\" y=\"38\" text-anchor=\"middle\" font-size=\"24\" font-weight=\"bold\">Resultados de las simulaciones Monte Carlo</text>\n");

        double[] categoryPositions = new double[piResults.size()];
        double[] estimates = new double[piResults.size()];
        double[] errors = new double[piResults.size()];
        double[] executionTimes = new double[piResults.size()];
        List<String> sampleLabels = new ArrayList<>();
        for (int i = 0; i < piResults.size(); i++) {
            PiSimulationResult result = piResults.get(i);
            categoryPositions[i] = i;
            estimates[i] = result.getEstimatedPi();
            errors[i] = result.getAbsoluteError();
            executionTimes[i] = result.getExecutionTime();
            sampleLabels.add(String.format(Locale.US, "%,d", result.getSamples()));
        }

        drawPanel(svg, 60, "Estimación de π por tamaño de muestra",
            categoryPositions, sampleLabels,
            List.of(new Series("π estimado", BLUE, estimates),
                new Series("π real", RED, constantValues(piResults.size(), Math.PI))),
            "Número de puntos (N)", "Valor de π", false);
        drawPanel(svg, 420, "Error absoluto por tamaño de muestra",
            categoryPositions, sampleLabels,
            List.of(new Series("Error absoluto", BLUE, errors)),
            "Número de puntos (N)", "Error", true);
        drawPanel(svg, 780, "Tiempo de ejecución por tamaño de muestra",
            categoryPositions, sampleLabels,
            List.of(new Series("Tiempo de ejecución", BLUE, executionTimes)),
            "Número de puntos (N)", "Tiempo (s)", true);
        drawPanel(svg, 1140, "Evolución promedio de vehículos en cola",
            trafficSummary.getTimePoints(), null,
            List.of(new Series("Cola promedio", BLUE, trafficSummary.getAverageQueueByTime())),
            "Tiempo (s)", "Vehículos", true);

        svg.append("</svg>\n");
        Files.writeString(outputPath, svg.toString(), StandardCharsets.UTF_8);
        return outputPath;
    }

    private static double[] constantValues(int size, double value) {
        double[] values = new double[size];
        for (int i = 0; i < size; i++) {
            values[i] = value;
        }
        return values;
    }

    private static void drawPanel(StringBuilder svg, int top, String title, double[] xValues,
                                  List<String> xLabels, List<Series> series, String xAxisTitle,
                                  String yAxisTitle, boolean includeZero) {
        int left = 105;
        int right = 1040;
        double plotTop = top + 55;
        double plotBottom = top + 285;
        double plotHeight = plotBottom - plotTop;
        double low = Double.POSITIVE_INFINITY;
        double high = Double.NEGATIVE_INFINITY;
        int valueCount = 0;
        for (Series current : series) {
            for (double value : current.values()) {
                low = Math.min(low, value);
                high = Math.max(high, value);
                valueCount++;
            }
        }
        if (valueCount == 0) {
            return;
        }
        if (includeZero) {
            low = Math.min(0.0, low);
        }
        if (high == low) {
            high = low + 1.0;
        }
        double padding = (high - low) * 0.1;
        low = includeZero ? Math.min(0.0, low - padding) : low - padding;
        high += padding;

        svg.append("<text x=\"").append(left).append("\" y=\"").append(top + 25)
            .append("\" font-size=\"19\" font-weight=\"bold\">").append(escape(title)).append("</text>\n");
        for (int tick = 0; tick <= 5; tick++) {
            double ratio = tick / 5.0;
            double y = plotBottom - ratio * plotHeight;
            double value = low + ratio * (high - low);
            svg.append("<line class=\"grid\" x1=\"").append(left).append("\" y1=\"").append(number(y))
                .append("\" x2=\"").append(right).append("\" y2=\"").append(number(y)).append("\"/>\n")
                .append("<text x=\"").append(left - 12).append("\" y=\"").append(number(y + 5))
                .append("\" text-anchor=\"end\" font-size=\"12\">").append(number(value, 4)).append("</text>\n");
        }
        svg.append("<line class=\"axis\" x1=\"").append(left).append("\" y1=\"").append(number(plotTop))
            .append("\" x2=\"").append(left).append("\" y2=\"").append(number(plotBottom)).append("\"/>\n")
            .append("<line class=\"axis\" x1=\"").append(left).append("\" y1=\"").append(number(plotBottom))
            .append("\" x2=\"").append(right).append("\" y2=\"").append(number(plotBottom)).append("\"/>\n");

        double[] xPositions = new double[xValues.length];
        if (xValues.length == 1) {
            xPositions[0] = (left + right) / 2.0;
        } else if (xValues.length > 1) {
            double xMin = xValues[0];
            double xMax = xValues[0];
            for (double xValue : xValues) {
                xMin = Math.min(xMin, xValue);
                xMax = Math.max(xMax, xValue);
            }
            for (int i = 0; i < xValues.length; i++) {
                xPositions[i] = xMin == xMax ? (left + right) / 2.0
                    : left + (xValues[i] - xMin) / (xMax - xMin) * (right - left);
            }
        }
        int xTickStride = Math.max(1, (int) Math.ceil((xPositions.length - 1) / 5.0));
        for (int i = 0; i < xPositions.length; i++) {
            if (xLabels == null && i % xTickStride != 0 && i != xPositions.length - 1) {
                continue;
            }
            double x = xPositions[i];
            String label = xLabels != null ? xLabels.get(i) : number(xValues[i], 0);
            svg.append("<line class=\"grid\" x1=\"").append(number(x)).append("\" y1=\"").append(number(plotBottom))
                .append("\" x2=\"").append(number(x)).append("\" y2=\"").append(number(plotBottom + 5)).append("\"/>\n")
                .append("<text x=\"").append(number(x)).append("\" y=\"").append(number(plotBottom + 21))
                .append("\" text-anchor=\"middle\" font-size=\"12\">").append(escape(label)).append("</text>\n");
        }

        for (int seriesIndex = 0; seriesIndex < series.size(); seriesIndex++) {
            Series current = series.get(seriesIndex);
            StringBuilder points = new StringBuilder();
            int pointCount = Math.min(xPositions.length, current.values().length);
            for (int i = 0; i < pointCount; i++) {
                double y = plotBottom - (current.values()[i] - low) / (high - low) * plotHeight;
                points.append(number(xPositions[i])).append(',').append(number(y)).append(' ');
            }
            svg.append("<polyline class=\"series\" stroke=\"").append(current.color())
                .append("\" points=\"").append(points.toString().trim()).append("\"/>\n");
            for (int i = 0; i < pointCount; i++) {
                double y = plotBottom - (current.values()[i] - low) / (high - low) * plotHeight;
                svg.append("<circle class=\"point\" cx=\"").append(number(xPositions[i])).append("\" cy=\"")
                    .append(number(y)).append("\" r=\"5\" fill=\"").append(current.color()).append("\"/>\n");
            }
            int legendX = right - 170;
            double legendY = plotTop + 18 * seriesIndex;
            svg.append("<line x1=\"").append(legendX).append("\" y1=\"").append(number(legendY))
                .append("\" x2=\"").append(legendX + 22).append("\" y2=\"").append(number(legendY))
                .append("\" stroke=\"").append(current.color()).append("\" stroke-width=\"3\"/>\n")
                .append("<text x=\"").append(legendX + 28).append("\" y=\"").append(number(legendY + 4))
                .append("\" font-size=\"12\">").append(escape(current.label())).append("</text>\n");
        }
        svg.append("<text x=\"").append((left + right) / 2).append("\" y=\"").append(top + 330)
            .append("\" text-anchor=\"middle\" font-size=\"13\">").append(escape(xAxisTitle)).append("</text>\n")
            .append("<text x=\"25\" y=\"").append(number(top + 170))
            .append("\" text-anchor=\"middle\" font-size=\"13\" transform=\"rotate(-90 25 ")
            .append(number(top + 170)).append(")\">").append(escape(yAxisTitle)).append("</text>\n");
    }

    private static String number(double value) {
        return number(value, 2);
    }

    private static String number(double value, int decimals) {
        return String.format(Locale.US, "% ." + decimals + "f", value).trim();
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
            .replace("\"", "&quot;").replace("'", "&apos;");
    }

    private record Series(String label, String color, double[] values) { }
}
