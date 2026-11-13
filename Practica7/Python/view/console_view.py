from model.monte_carlo import PiSimulationResult
from model.traffic_model import TrafficSummary


class ConsoleView:
    @staticmethod
    def print_pi_table(results: list[PiSimulationResult]) -> None:
        print("\n=== Monte Carlo para estimación de π ===")
        print(f"{'N':>12} {'π estimado':>14} {'Error':>12} {'Tiempo (s)':>12}")
        print("-" * 60)

        for result in results:
            print(f"{result.samples:>12,} {result.estimated_pi:>14.8f} {result.absolute_error:>12.8f} {result.execution_time:>12.8f}")

    @staticmethod
    def print_traffic_summary(summary: TrafficSummary) -> None:
        print("\n=== Modelo de tráfico vehicular ===")
        print(f"Lambda: {summary.lambda_rate} veh/s")
        print(f"Mu: {summary.mu_rate} veh/s")
        print(f"R: {summary.red_duration} s")
        print(f"G: {summary.green_duration} s")
        print(f"T = R + G = {summary.total_cycle_time} s")
        print(f"Cola promedio: {summary.average_queue:.4f} vehículos")
        print(f"Tiempo medio de espera: {summary.average_waiting_time:.4f} s")
        print(f"Cola final: {summary.final_queue:.4f} vehículos")
        print(f"Llegadas totales: {summary.total_arrivals:.2f}")
        print(f"Salidas totales: {summary.total_departures:.2f}")

    @staticmethod
    def print_comparison_message() -> None:
        print("\nLa implementación en Python y Java deben producir resultados consistentes con los mismos parámetros.")
