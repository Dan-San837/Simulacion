import numpy as np


class ModeloDeterministico:
    """Modelo deterministico: P(t) = P0 * e^(r * t). No usa aleatoriedad."""

    def __init__(self, P0: float, r: float) -> None:
        self.P0 = float(P0)
        self.r = float(r)

    def simular(self, tiempo: float, puntos: int = 200) -> tuple[np.ndarray, np.ndarray]:
        if tiempo < 0:
            raise ValueError("El tiempo total no puede ser negativo.")
        if puntos < 2:
            raise ValueError("Se requieren al menos 2 puntos para el vector de tiempo.")

        t = np.linspace(0.0, float(tiempo), puntos)
        poblacion = self.P0 * np.exp(self.r * t)
        return t, poblacion
