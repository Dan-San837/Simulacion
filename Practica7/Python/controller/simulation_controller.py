from time import perf_counter

from model.monte_carlo import MonteCarloPiEstimator, PiSimulationResult
from model.traffic_model import TrafficQueueModel, TrafficSummary


class SimulationController:
    def run_pi_experiment(self, sample_sizes: list[int]) -> list[PiSimulationResult]:
        results: list[PiSimulationResult] = []

        for size in sample_sizes:
            start = perf_counter()
            estimated_pi, error = MonteCarloPiEstimator.estimate_pi(size, seed=12345 + size)
            elapsed = perf_counter() - start
            results.append(
                PiSimulationResult(
                    samples=size,
                    estimated_pi=estimated_pi,
                    absolute_error=error,
                    execution_time=elapsed,
                )
            )

        return results

    def run_traffic_experiment(
        self,
        lambda_rate: float,
        mu_rate: float,
        red_duration: float,
        green_duration: float,
        dt: float,
        simulations: int,
    ) -> TrafficSummary:
        model = TrafficQueueModel(
            lambda_rate=lambda_rate,
            mu_rate=mu_rate,
            red_duration=red_duration,
            green_duration=green_duration,
            dt=dt,
            simulations=simulations,
        )
        return model.run()
