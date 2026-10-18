import numpy as np


class ModeloEstocastico:
    """Modelo estocastico: P(t+1) = P(t) + r * P(t) + e(t), con e(t) ~ N(0, sigma^2)."""

    def __init__(self, P0: float, r: float, sigma: float) -> None:
        if sigma < 0:
            raise ValueError("La desviacion estandar (sigma) no puede ser negativa.")
        self.P0 = float(P0)
        self.r = float(r)
        self.sigma = float(sigma)

    def simular(self, tiempo: int) -> tuple[np.ndarray, np.ndarray]:
        periodos = int(tiempo)
        if periodos < 0:
            raise ValueError("El numero de periodos no puede ser negativo.")

        t = np.arange(periodos + 1)
        poblacion = np.empty(periodos + 1)
        poblacion[0] = self.P0

        for indice in range(1, periodos + 1):
            poblacion_actual = poblacion[indice - 1]
            ruido = np.random.normal(loc=0.0, scale=self.sigma)
            nueva_poblacion = poblacion_actual + self.r * poblacion_actual + ruido
            poblacion[indice] = max(nueva_poblacion, 0.0)

        return t, poblacion
