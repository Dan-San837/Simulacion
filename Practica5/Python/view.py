"""Vista del generador congruencial lineal."""

import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


class Vista:
    def __init__(self, directorio_resultados):
        self.directorio_resultados = Path(directorio_resultados)
        self.directorio_resultados.mkdir(parents=True, exist_ok=True)

    def mostrar_configuracion(self, parametros, cantidad):
        print("=" * 68)
        print("GENERADOR CONGRUENCIAL LINEAL")
        print("=" * 68)
        print(f"a       = {parametros['a']}")
        print(f"c       = {parametros['c']}")
        print(f"m       = {parametros['m']} (2^32)")
        print(f"semilla = {parametros['semilla']}")
        print(f"valores = {cantidad}")
        print("=" * 68)

    def mostrar_primeras(self, estados, normalizados, titulo="Primeras observaciones"):
        print(f"\n{titulo}:")
        print(f"{'n':>5} {'X(n)':>14} {'U(n)':>16}")
        print("-" * 39)
        for i, (estado, valor) in enumerate(zip(estados, normalizados), start=1):
            print(f"{i:>5} {estado:>14} {valor:>16.12f}")

    def mostrar_periodo(self, analisis):
        print("\n" + "-" * 68)
        print("ANALISIS DEL PERIODO")
        print("-" * 68)
        print(f"Factores primos de m: {analisis['factores_primos_m']}")
        print("Condiciones de Hull-Dobell:")
        for nombre, cumple in analisis["condiciones_cumplidas"].items():
            print(f"  - {nombre}: {'cumple' if cumple else 'no cumple'}")
        if analisis["periodo_completo"]:
            print(f"Periodo teorico: {analisis['periodo_teorico']} (periodo completo)")
        else:
            print("No se puede garantizar el periodo completo con estos parametros.")
        if analisis["repetidos_en_muestra"]:
            print(f"Se encontraron valores repetidos en la muestra de {analisis['muestra']}.")
        else:
            print(f"No hay valores repetidos en la muestra de {analisis['muestra']};")
            print("esto es consistente con un periodo de 2^32.")

    def guardar_secuencia_csv(self, estados, normalizados, nombre="python_secuencia.csv"):
        ruta = self.directorio_resultados / nombre
        with ruta.open("w", newline="", encoding="utf-8") as archivo:
            escritor = csv.writer(archivo)
            escritor.writerow(["n", "X", "U"])
            for i, (estado, valor) in enumerate(zip(estados, normalizados), start=1):
                escritor.writerow([i, estado, f"{valor:.17g}"])
        return ruta

    def guardar_primeras_csv(self, estados, normalizados, nombre="python_primeras20.csv"):
        return self.guardar_secuencia_csv(estados, normalizados, nombre)

    def graficar_secuencia(self, normalizados, nombre="python_secuencia.png", max_puntos=1000):
        ruta = self.directorio_resultados / nombre
        cantidad = len(normalizados)
        muestra = normalizados[:max_puntos]

        figura, (eje_superior, eje_inferior) = plt.subplots(2, 1, figsize=(11, 8))
        figura.suptitle(
            "Generador Congruencial Lineal - Secuencia normalizada U(n)",
            fontsize=14,
        )

        eje_superior.plot(
            range(1, len(muestra) + 1),
            muestra,
            color="#1f77b4",
            linewidth=0.8,
        )
        eje_superior.set_title(f"Primeras {len(muestra)} observaciones")
        eje_superior.set_xlabel("n")
        eje_superior.set_ylabel("U(n)")
        eje_superior.set_ylim(0, 1)
        eje_superior.grid(True, alpha=0.3)

        eje_inferior.scatter(
            range(1, cantidad + 1),
            normalizados,
            s=2,
            color="#d62728",
            alpha=0.35,
        )
        eje_inferior.set_title(f"Secuencia completa ({cantidad} observaciones)")
        eje_inferior.set_xlabel("n")
        eje_inferior.set_ylabel("U(n)")
        eje_inferior.set_ylim(0, 1)
        eje_inferior.grid(True, alpha=0.3)

        figura.tight_layout(rect=(0, 0, 1, 0.96))
        figura.savefig(ruta, dpi=150)
        plt.close(figura)
        return ruta

    def mostrar_comparacion(self, comparacion, ruta_java):
        print("\n" + "-" * 68)
        print("COMPARACION CON JAVA (primeras 20 observaciones)")
        print("-" * 68)
        if comparacion is None:
            print(f"No se encontro el archivo: {ruta_java}")
            print("Ejecute la implementacion Java y vuelva a correr Python para comparar.")
            return

        print(
            f"{'n':>5} {'X Python':>14} {'X Java':>14} "
            f"{'U Python':>16} {'U Java':>16} {'Igual':>7}"
        )
        print("-" * 81)
        for fila in comparacion["filas"]:
            print(
                f"{fila['n']:>5} {fila['x_python']:>14} {fila['x_java']:>14} "
                f"{fila['u_python']:>16.12f} {fila['u_java']:>16.12f} "
                f"{'si' if fila['igual'] else 'no':>7}"
            )
        if comparacion["coinciden"]:
            print(
                f"\nResultado: las {len(comparacion['filas'])} observaciones coinciden."
            )
        else:
            print("\nResultado: se encontraron diferencias entre las implementaciones.")
