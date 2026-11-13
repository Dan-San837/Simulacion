package view;

import model.PiSimulationResult;
import model.TrafficSummary;

import java.util.List;

public class ConsoleView {
    public static void printPiTable(List<PiSimulationResult> results) {
        System.out.println("\n=== Monte Carlo para estimación de π ===");
        System.out.printf("%-12s %-14s %-12s %-12s%n", "N", "π estimado", "Error", "Tiempo (s)");
        System.out.println("------------------------------------------------------------");

        for (PiSimulationResult result : results) {
            System.out.printf("%-12d %-14.8f %-12.8f %-12.8f%n",
                result.getSamples(),
                result.getEstimatedPi(),
                result.getAbsoluteError(),
                result.getExecutionTime()
            );
        }
    }

    public static void printTrafficSummary(TrafficSummary summary) {
        System.out.println("\n=== Modelo de tráfico vehicular ===");
        System.out.printf("Lambda: %.4f veh/s%n", summary.getLambdaRate());
        System.out.printf("Mu: %.4f veh/s%n", summary.getMuRate());
        System.out.printf("R: %.2f s%n", summary.getRedDuration());
        System.out.printf("G: %.2f s%n", summary.getGreenDuration());
        System.out.printf("T = R + G = %.2f s%n", summary.getTotalCycleTime());
        System.out.printf("Cola promedio: %.4f vehículos%n", summary.getAverageQueue());
        System.out.printf("Tiempo medio de espera: %.4f s%n", summary.getAverageWaitingTime());
        System.out.printf("Cola final: %.4f vehículos%n", summary.getFinalQueue());
        System.out.printf("Llegadas totales: %.2f%n", summary.getTotalArrivals());
        System.out.printf("Salidas totales: %.2f%n", summary.getTotalDepartures());
    }
}
