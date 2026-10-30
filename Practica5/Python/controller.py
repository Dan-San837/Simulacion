"""Controlador del generador congruencial lineal."""

import csv
from pathlib import Path

from model import GeneradorCongruencialLineal
from view import Vista


class Controlador:
    CANTIDAD = 10000
    PRIMERAS = 20

    def __init__(self):
        self.directorio_base = Path(__file__).resolve().parent
        self.directorio_resultados = self.directorio_base / "resultados"
        self.modelo = GeneradorCongruencialLineal()
        self.vista = Vista(self.directorio_resultados)

    def ejecutar(self):
        self.modelo.reiniciar()
        self.vista.mostrar_configuracion(self.modelo.parametros(), self.CANTIDAD)

        estados = self.modelo.generar(self.CANTIDAD)
        normalizados = self.modelo.normalizar(estados)
        estados_iniciales = estados[: self.PRIMERAS]
        normalizados_iniciales = normalizados[: self.PRIMERAS]

        self.vista.mostrar_primeras(estados_iniciales, normalizados_iniciales)

        ruta_csv = self.vista.guardar_secuencia_csv(estados, normalizados)
        ruta_primeras = self.vista.guardar_primeras_csv(
            estados_iniciales, normalizados_iniciales
        )
        ruta_grafica = self.vista.graficar_secuencia(normalizados)

        print("\nArchivos generados:")
        print(f"  - {ruta_csv}")
        print(f"  - {ruta_primeras}")
        print(f"  - {ruta_grafica}")

        analisis = self.modelo.analizar_periodo(estados)
        self.vista.mostrar_periodo(analisis)

        self._comparar_con_java(estados_iniciales, normalizados_iniciales)

    def _comparar_con_java(self, estados, normalizados):
        ruta_java = (
            self.directorio_base.parent
            / "Java"
            / "resultados"
            / "java_primeras20.csv"
        )
        comparacion = None
        if ruta_java.exists():
            filas_java = self._leer_primeras(ruta_java)
            if len(filas_java) == len(estados):
                filas = []
                for i, (estado, valor, (x_java, u_java)) in enumerate(
                    zip(estados, normalizados, filas_java), start=1
                ):
                    igual = estado == x_java and abs(valor - u_java) <= 1e-15
                    filas.append(
                        {
                            "n": i,
                            "x_python": estado,
                            "x_java": x_java,
                            "u_python": valor,
                            "u_java": u_java,
                            "igual": igual,
                        }
                    )
                comparacion = {
                    "filas": filas,
                    "coinciden": all(fila["igual"] for fila in filas),
                }
        self.vista.mostrar_comparacion(comparacion, ruta_java)

    @staticmethod
    def _leer_primeras(ruta):
        filas = []
        with ruta.open("r", encoding="utf-8") as archivo:
            lector = csv.reader(archivo)
            next(lector, None)
            for fila in lector:
                if len(fila) >= 3:
                    filas.append((int(fila[1]), float(fila[2])))
        return filas
