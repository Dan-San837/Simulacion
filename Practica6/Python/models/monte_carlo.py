from __future__ import annotations

import math
from dataclasses import dataclass

from models.lcg import LinearCongruentialGenerator


@dataclass(frozen=True)
class PiEstimate:
    estimate: float
    absolute_error: float
    points_inside: int
    sample_count: int


def estimate_pi(
    generator: LinearCongruentialGenerator, sample_count: int
) -> PiEstimate:
    if sample_count < 1:
        raise ValueError("La cantidad de puntos debe ser positiva.")
    inside = 0
    for _ in range(sample_count):
        x = generator.next_float()
        y = generator.next_float()
        if x * x + y * y <= 1:
            inside += 1
    estimate = 4 * inside / sample_count
    return PiEstimate(estimate, abs(estimate - math.pi), inside, sample_count)
