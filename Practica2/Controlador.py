from typing import Protocol

import matplotlib.pyplot as plt
import numpy as np
from statistics import fmean, pvariance

from Models.Continuo import ModeloContinuo
from Models.Deterministico import ModeloDeterministico
from Models.Discreto import ModeloDiscreto
from Models.Estocastico import ModeloEstocastico


class ModeloSimulable(Protocol):
    """Contrato comun de los modelos: reciben el tiempo y retornan (tiempo, poblacion)."""

    def simular(self, tiempo: float) -> tuple[np.ndarray, np.ndarray]: ...


class ControladorSimulacion:
    """Coordina la estimacion de parametros, la ejecucion de los cuatro modelos y la grafica."""

    def __init__(self, datos: list[float], dt: float = 0.1) -> None:
        if len(datos) < 2:
            raise ValueError("Se necesitan al menos 2 datos para estimar los parametros.")
        if any(dato < 0 for dato in datos):
            raise ValueError("Los datos de poblacion no pueden ser negativos.")
        if dt <= 0:
            raise ValueError("El tamano del paso (dt) debe ser mayor que 0.")

        self.datos = list(datos)
        self.dt = float(dt)
        self.tiempo = len(self.datos) - 1
        self.P0, self.r, self.sigma = self._estimar_parametros(self.datos)

    @staticmethod
    def _estimar_parametros(datos: list[float]) -> tuple[float, float, float]:
        P0 = float(datos[0])
        tasas = [
            (datos[indice + 1] - datos[indice]) / datos[indice]
            for indice in range(len(datos) - 1)
        ]
        r = fmean(tasas)
        residuos = [
            (datos[indice + 1] - datos[indice]) - r * datos[indice]
            for indice in range(len(datos) - 1)
        ]
        sigma = float(np.sqrt(pvariance(residuos)))
        return P0, r, sigma

    def _crear_modelos(self) -> dict[str, ModeloSimulable]:
        return {
            "Determinístico": ModeloDeterministico(self.P0, self.r),
            "Discreto": ModeloDiscreto(self.P0, self.r),
            "Estocástico": ModeloEstocastico(self.P0, self.r, self.sigma),
            "Continuo (Euler)": ModeloContinuo(self.P0, self.r, self.dt),
        }

    def ejecutar(self) -> dict[str, tuple[np.ndarray, np.ndarray]]:
        return {
            nombre: modelo.simular(self.tiempo)
            for nombre, modelo in self._crear_modelos().items()
        }

    def graficar(self, resultados: dict[str, tuple[np.ndarray, np.ndarray]]) -> None:
        plt.figure(figsize=(10, 6))

        for nombre, (tiempo, poblacion) in resultados.items():
            plt.plot(tiempo, poblacion, label=nombre)

        plt.scatter(
            np.arange(len(self.datos)),
            self.datos,
            color="black",
            zorder=5,
            label="Datos observados",
        )

        plt.title("Comparación de modelos de crecimiento poblacional")
        plt.xlabel("Tiempo")
        plt.ylabel("Población")
        plt.legend()
        plt.grid(True)
        plt.tight_layout()
        plt.show()
