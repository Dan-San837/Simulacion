from dataclasses import dataclass
import math

from model.monte_carlo import LCG


@dataclass
class TrafficSummary:
    lambda_rate: float
    mu_rate: float
    red_duration: float
    green_duration: float
    dt: float
    simulations: int
    average_queue: float
    average_waiting_time: float
    final_queue: float
    total_cycle_time: float
    total_arrivals: float
    total_departures: float
    time_points: list[float]
    average_queue_by_time: list[float]


class TrafficQueueModel:
    def __init__(self, lambda_rate: float, mu_rate: float, red_duration: float, green_duration: float, dt: float, simulations: int):
        self.lambda_rate = lambda_rate
        self.mu_rate = mu_rate
        self.red_duration = red_duration
        self.green_duration = green_duration
        self.dt = dt
        self.simulations = simulations

    @staticmethod
    def poisson(lam: float, rng: LCG) -> int:
        if lam <= 0:
            return 0
        limit = math.exp(-lam)
        k = 0
        p = 1.0
        while p > limit:
            k += 1
            p *= rng.random()
        return k

    def run(self) -> TrafficSummary:
        total_steps = int(round((self.red_duration + self.green_duration) / self.dt))
        queue_values = []
        waiting_times = []
        queue_sums_by_step = [0.0] * total_steps
        arrivals_total = 0.0
        departures_total = 0.0

        for sim in range(self.simulations):
            rng = LCG(2024 + sim)
            queue = 0.0
            waiting_time = 0.0

            for step in range(total_steps):
                elapsed_time = step * self.dt
                arrivals = self.poisson(self.lambda_rate * self.dt, rng)
                queue += arrivals
                arrivals_total += arrivals
                waiting_time += queue * self.dt

                if elapsed_time >= self.red_duration:
                    discharge_capacity = self.mu_rate * self.dt
                    departures = min(queue, discharge_capacity)
                    queue = max(0.0, queue - departures)
                    departures_total += departures

                queue_sums_by_step[step] += queue

            queue_values.append(queue)
            waiting_times.append(waiting_time)

        total_cycle_time = self.red_duration + self.green_duration
        average_queue = sum(queue_values) / len(queue_values)
        average_waiting_time = sum(waiting_times) / len(waiting_times)
        final_queue = queue_values[-1]
        average_queue_by_time = [value / self.simulations for value in queue_sums_by_step]

        return TrafficSummary(
            lambda_rate=self.lambda_rate,
            mu_rate=self.mu_rate,
            red_duration=self.red_duration,
            green_duration=self.green_duration,
            dt=self.dt,
            simulations=self.simulations,
            average_queue=average_queue,
            average_waiting_time=average_waiting_time,
            final_queue=final_queue,
            total_cycle_time=total_cycle_time,
            total_arrivals=arrivals_total,
            total_departures=departures_total,
            time_points=[(step + 1) * self.dt for step in range(total_steps)],
            average_queue_by_time=average_queue_by_time,
        )
