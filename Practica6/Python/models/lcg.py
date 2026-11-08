from __future__ import annotations

from dataclasses import dataclass


@dataclass(frozen=True)
class LCGParameters:
    seed: int = 1
    multiplier: int = 1_103_515_245
    increment: int = 12_345
    modulus: int = 2_147_483_648

    def __post_init__(self) -> None:
        if not 2 <= self.modulus <= 2**31:
            raise ValueError("El módulo debe estar entre 2 y 2^31.")
        if not 0 <= self.multiplier < self.modulus:
            raise ValueError("El multiplicador debe estar en [0, módulo).")
        if not 0 <= self.increment < self.modulus:
            raise ValueError("El incremento debe estar en [0, módulo).")
        if not 0 <= self.seed < self.modulus:
            raise ValueError("La semilla debe estar en [0, módulo).")


class LinearCongruentialGenerator:
    def __init__(self, parameters: LCGParameters) -> None:
        self.parameters = parameters
        self._state = parameters.seed

    def next_int(self) -> int:
        params = self.parameters
        self._state = (
            params.multiplier * self._state + params.increment
        ) % params.modulus
        return self._state

    def next_float(self) -> float:
        return self.next_int() / self.parameters.modulus

    def generate(self, count: int) -> list[float]:
        if count < 1:
            raise ValueError("La cantidad de números debe ser positiva.")
        return [self.next_float() for _ in range(count)]
