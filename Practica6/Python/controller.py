from __future__ import annotations

import argparse
import math
import time
from collections.abc import Sequence

import view
from models.Kolmogorov import test_kolmogorov
from models.chi_cuadrado import test_chi_square
from models.lcg import LCGParameters, LinearCongruentialGenerator
from models.monte_carlo import estimate_pi


BENCHMARK_SIZES = (1_000, 10_000, 100_000, 1_000_000)
CONFIGURATIONS = (
    ("LCG predeterminado", LCGParameters()),
    ("Park-Miller", LCGParameters(seed=1, multiplier=16_807, increment=0, modulus=2_147_483_647)),
    ("LCG período corto", LCGParameters(seed=1, multiplier=5, increment=1, modulus=16)),
)


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(
        description="Genera números con LCG y evalúa su uniformidad estadística."
    )
    parser.add_argument("--n", type=int, default=10_000, help="Cantidad de valores (por defecto: 10000).")
    parser.add_argument("--seed", type=int, default=1, help="Semilla del LCG.")
    parser.add_argument("--a", type=int, default=1_103_515_245, help="Multiplicador del LCG.")
    parser.add_argument("--c", type=int, default=12_345, help="Incremento del LCG.")
    parser.add_argument("--m", type=int, default=2_147_483_648, help="Módulo del LCG.")
    parser.add_argument("--intervals", type=int, default=10, help="Intervalos de Chi-cuadrado.")
    parser.add_argument("--alpha", type=float, default=0.05, help="Nivel de significancia.")
    parser.add_argument("--benchmark", action="store_true", help="Ejecuta los cuatro tamaños de referencia.")
    parser.add_argument(
        "--compare-configurations",
        action="store_true",
        help="Compara el generador predeterminado, Park-Miller y un LCG de período corto.",
    )
    parser.add_argument("--show-sequence", action="store_true", help="Imprime todos los valores generados.")
    parser.add_argument("--csv", dest="csv_path", help="Guarda la secuencia como CSV.")
    parser.add_argument("--plot", dest="plot_path", help="Guarda la gráfica ECDF frente a U(0,1).")
    return parser


def main(argv: Sequence[str] | None = None) -> int:
    parser = build_parser()
    options = parser.parse_args(argv)
    try:
        if options.n < 1:
            raise ValueError("n debe ser positivo.")
        if options.intervals < 2:
            raise ValueError("El número de intervalos debe ser al menos 2.")
        if not math.isfinite(options.alpha) or not 0 < options.alpha < 1:
            raise ValueError("alpha debe pertenecer al intervalo (0, 1).")
        parameters = LCGParameters(
            seed=options.seed,
            multiplier=options.a,
            increment=options.c,
            modulus=options.m,
        )
        if options.benchmark:
            _run_benchmark()
            return 0
        if options.n < options.intervals * 5:
            raise ValueError("n debe ser al menos 5 veces el número de intervalos.")

        values = LinearCongruentialGenerator(parameters).generate(options.n)
        chi = test_chi_square(values, options.intervals)
        ks = test_kolmogorov(values)
        start = time.perf_counter()
        pi = estimate_pi(LinearCongruentialGenerator(parameters), options.n)
        elapsed = time.perf_counter() - start

        view.show_analysis(
            values,
            chi,
            ks,
            pi,
            elapsed,
            options.alpha,
            parameters,
            options.show_sequence,
            options.csv_path,
            options.plot_path,
        )
        if options.compare_configurations:
            _run_comparison(options.n, options.intervals)
    except (ValueError, RuntimeError, OSError) as error:
        parser.error(str(error))
    return 0


def _run_benchmark() -> None:
    results: list[tuple[float, float, float]] = []
    for size in BENCHMARK_SIZES:
        start = time.perf_counter()
        estimate = estimate_pi(LinearCongruentialGenerator(LCGParameters()), size)
        elapsed = time.perf_counter() - start
        results.append((estimate.estimate, estimate.absolute_error, elapsed))
    view.show_benchmark(list(BENCHMARK_SIZES), results)


def _run_comparison(sample_size: int, intervals: int) -> None:
    names: list[str] = []
    results: list[tuple[float, float, float, float]] = []
    for name, parameters in CONFIGURATIONS:
        values = LinearCongruentialGenerator(parameters).generate(sample_size)
        chi = test_chi_square(values, intervals)
        ks = test_kolmogorov(values)
        names.append(name)
        results.append((chi.statistic, chi.p_value, ks.statistic, ks.p_value))
    view.show_comparison(names, results)