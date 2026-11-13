import math
from dataclasses import dataclass


class LCG:
    def __init__(self, seed: int):
        self.state = seed & 0x7FFFFFFF

    def next_int(self) -> int:
        self.state = (1103515245 * self.state + 12345) & 0x7FFFFFFF
        return self.state

    def random(self) -> float:
        return self.next_int() / 2147483648.0


@dataclass
class PiSimulationResult:
    samples: int
    estimated_pi: float
    absolute_error: float
    execution_time: float


class MonteCarloPiEstimator:
    @staticmethod
    def estimate_pi(samples: int, seed: int = 1) -> tuple[float, float]:
        rng = LCG(seed)
        inside = 0

        for _ in range(samples):
            x = rng.random()
            y = rng.random()
            if x * x + y * y <= 1.0:
                inside += 1

        estimated_pi = 4.0 * inside / samples
        return estimated_pi, abs(estimated_pi - math.pi)
