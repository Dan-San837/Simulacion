import controller.SimulationController;
import view.ConsoleView;
import view.SvgChartView;

import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) throws IOException {
        int[] sampleSizes = {1_000, 10_000, 100_000, 1_000_000};

        SimulationController controller = new SimulationController();
        var piResults = controller.runPiExperiment(sampleSizes);
        ConsoleView.printPiTable(piResults);

        var trafficSummary = controller.runTrafficExperiment(
            0.8,
            1.2,
            10.0,
            20.0,
            1.0,
            250
        );
        ConsoleView.printTrafficSummary(trafficSummary);
        Path chartPath = SvgChartView.writeCharts(
            Path.of("resultados_monte_carlo.svg"),
            piResults,
            trafficSummary
        );
        System.out.println("\nGráficas guardadas en: " + chartPath.toAbsolutePath());
    }
}
