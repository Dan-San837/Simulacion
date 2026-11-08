from __future__ import annotations

import math
from dataclasses import dataclass

_EPSILON = 1e-14
_MIN_VALUE = 1e-300
_MAX_ITERATIONS = 100_000
_LANCZOS = (
    676.5203681218851,
    -1259.1392167224028,
    771.32342877765313,
    -176.61502916214059,
    12.507343278686905,
    -0.13857109526572012,
    9.9843695780195716e-6,
    1.5056327351493116e-7,
)


@dataclass(frozen=True)
class ChiSquareResult:
    observed: tuple[int, ...]
    expected: float
    statistic: float
    degrees_of_freedom: int
    p_value: float


def test_chi_square(values: list[float], intervals: int = 10) -> ChiSquareResult:
    _validate_sample(values)
    if intervals < 2 or len(values) < intervals * 5:
        raise ValueError("Se requieren al menos 5 observaciones esperadas por intervalo.")

    frequencies = [0] * intervals
    for value in values:
        _validate_value(value)
        index = min(int(value * intervals), intervals - 1)
        frequencies[index] += 1

    expected = len(values) / intervals
    statistic = sum((frequency - expected) ** 2 / expected for frequency in frequencies)
    degrees_of_freedom = intervals - 1
    p_value = _regularized_gamma_q(degrees_of_freedom / 2, statistic / 2)
    return ChiSquareResult(
        tuple(frequencies), expected, statistic, degrees_of_freedom, p_value
    )


def _validate_sample(values: list[float]) -> None:
    if not values:
        raise ValueError("La secuencia no puede estar vacía.")


def _validate_value(value: float) -> None:
    if not math.isfinite(value) or not 0 <= value <= 1:
        raise ValueError("Los valores deben pertenecer a [0, 1].")


def _regularized_gamma_q(a: float, x: float) -> float:
    if x <= 0:
        return 1.0
    if x < a + 1:
        return 1 - _regularized_gamma_p_series(a, x)
    return _regularized_gamma_q_fraction(a, x)


def _regularized_gamma_p_series(a: float, x: float) -> float:
    term = total = 1 / a
    denominator = a
    for _ in range(_MAX_ITERATIONS):
        denominator += 1
        term *= x / denominator
        total += term
        if abs(term) <= abs(total) * _EPSILON:
            break
    return total * math.exp(-x + a * math.log(x) - math.lgamma(a))


def _regularized_gamma_q_fraction(a: float, x: float) -> float:
    b = x + 1 - a
    c = 1 / _MIN_VALUE
    d = 1 / max(abs(b), _MIN_VALUE)
    if b < 0:
        d = -d
    result = d
    for iteration in range(1, _MAX_ITERATIONS + 1):
        coefficient = -iteration * (iteration - a)
        b += 2
        d = coefficient * d + b
        if abs(d) < _MIN_VALUE:
            d = _MIN_VALUE
        c = b + coefficient / c
        if abs(c) < _MIN_VALUE:
            c = _MIN_VALUE
        d = 1 / d
        delta = d * c
        result *= delta
        if abs(delta - 1) < _EPSILON:
            break
    return math.exp(-x + a * math.log(x) - math.lgamma(a)) * result
