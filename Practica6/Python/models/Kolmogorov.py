from __future__ import annotations

import math
from dataclasses import dataclass


@dataclass(frozen=True)
class KolmogorovResult:
    statistic: float
    positive_difference: float
    negative_difference: float
    p_value: float


def test_kolmogorov(values: list[float]) -> KolmogorovResult:
    if not values:
        raise ValueError("La secuencia no puede estar vacía.")
    if any(not math.isfinite(value) or not 0 <= value <= 1 for value in values):
        raise ValueError("Los valores deben pertenecer a [0, 1].")

    ordered = sorted(values)
    size = len(ordered)
    positive = max((index + 1) / size - value for index, value in enumerate(ordered))
    negative = max(value - index / size for index, value in enumerate(ordered))
    statistic = max(positive, negative)
    root_size = math.sqrt(size)
    corrected_lambda = (root_size + 0.12 + 0.11 / root_size) * statistic
    p_value = _kolmogorov_survival(corrected_lambda)
    return KolmogorovResult(statistic, positive, negative, p_value)


def _kolmogorov_survival(value: float) -> float:
    if value <= 0:
        return 1.0
    total = 0.0
    if value <= 1.18:
        for index in range(1, 100_001):
            odd = 2 * index - 1
            term = math.exp(-(odd**2) * math.pi**2 / (8 * value**2))
            total += term
            if term < 1e-16:
                break
        return min(1.0, max(0.0, 1 - math.sqrt(2 * math.pi) * total / value))
    for index in range(1, 100_001):
        term = 2 * math.exp(-2 * index**2 * value**2)
        total += term if index % 2 else -term
        if term < 1e-16:
            break
    return min(1.0, max(0.0, total))