import numpy as np


class ModeloContinuo:
    """Modelo continuo con Euler: dP/dt = r * P  =>  P(t+dt) = P(t) + dt * (r * P(t))."""

    def __init__(self, P0: float, r: float, dt: float) -> None:
        if dt <= 0:
            raise ValueError("El tamano del paso (dt) debe ser mayor que 0.")
        self.P0 = float(P0)
        self.r = float(r)
        self.dt = float(dt)

    def simular(self, tiempo: float) -> tuple[np.ndarray, np.ndarray]:
        if tiempo < 0:
            raise ValueError("El tiempo total no puede ser negativo.")

        pasos = int(round(float(tiempo) / self.dt))
        t = np.arange(pasos + 1, dtype=float) * self.dt
        poblacion = np.empty(pasos + 1)
        poblacion[0] = self.P0

        for indice in range(1, pasos + 1):
            poblacion_actual = poblacion[indice - 1]
            cambio = self.r * poblacion_actual
            poblacion[indice] = poblacion_actual + self.dt * cambio

        return t, poblacion
