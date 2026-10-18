import numpy as np


class ModeloDiscreto:
    """Modelo discreto: P(t+1) = P(t) + r * P(t)."""

    def __init__(self, P0: float, r: float) -> None:
        self.P0 = float(P0)
        self.r = float(r)

    def simular(self, tiempo: int) -> tuple[np.ndarray, np.ndarray]:
        periodos = int(tiempo)
        if periodos < 0:
            raise ValueError("El numero de periodos no puede ser negativo.")

        t = np.arange(periodos + 1)
        poblacion = np.empty(periodos + 1)
        poblacion[0] = self.P0

        for indice in range(1, periodos + 1):
            poblacion_actual = poblacion[indice - 1]
            incremento = self.r * poblacion_actual
            poblacion[indice] = poblacion_actual + incremento

        return t, poblacion
