from __future__ import annotations

import csv
from pathlib import Path

from models.Kolmogorov import KolmogorovResult
from models.chi_cuadrado import ChiSquareResult
from models.lcg import LCGParameters
from models.monte_carlo import PiEstimate


def _show_section(title: str) -> None:
    print(f"\n{'=' * 68}\n  {title}\n{'=' * 68}")


def show_analysis(
    values: list[float],
    chi: ChiSquareResult,
    ks: KolmogorovResult,
    pi: PiEstimate,
    pi_seconds: float,
    alpha: float,
    parameters: LCGParameters,
    show_sequence: bool = False,
    csv_path: str | None = None,
    plot_path: str | None = None,
) -> None:
    _show_section("ANÁLISIS DE SECUENCIA PSEUDOALEATORIA")
    print(f"{'Tamaño de muestra':<24}: {len(values):,}")
    print(f"{'Semilla':<24}: {parameters.seed}")
    print(f"{'Multiplicador (a)':<24}: {parameters.multiplier}")
    print(f"{'Incremento (c)':<24}: {parameters.increment}")
    print(f"{'Módulo (m)':<24}: {parameters.modulus}")

    _show_section("SECUENCIA GENERADA")
    if show_sequence:
        print(f"{'Índice':>10}  {'Valor pseudoaleatorio':>24}")
        print(f"{'-' * 10}  {'-' * 24}")
        for index, value in enumerate(values, 1):
            print(f"{index:>10,}  {value:>24.16f}")
    else:
        print(f"{'Índice':>10}  {'Valor pseudoaleatorio':>24}")
        print(f"{'-' * 10}  {'-' * 24}")
        for index, value in enumerate(values[:10], 1):
            print(f"{index:>10,}  {value:>24.16f}")
        if len(values) > 10:
            print(f"  ... {len(values) - 10:,} valores omitidos (usa --show-sequence para ver todos)")

    _show_section("PRUEBA CHI-CUADRADO")
    print(f"{'Intervalo':>18}  {'Observada':>12}  {'Esperada':>12}  {'Aporte χ²':>12}")
    print(f"{'-' * 18}  {'-' * 12}  {'-' * 12}  {'-' * 12}")
    for index, observed in enumerate(chi.observed):
        lower = index / len(chi.observed)
        upper = (index + 1) / len(chi.observed)
        contribution = (observed - chi.expected) ** 2 / chi.expected
        closing = "]" if index == len(chi.observed) - 1 else ")"
        interval = f"[{lower:.1f}, {upper:.1f}{closing}"
        print(
            f"{interval:>18}  {observed:>12,}  {chi.expected:>12,.2f}  "
            f"{contribution:>12.6f}"
        )
    print(f"{'Estadístico χ²':<24}: {chi.statistic:.6f}")
    print(f"{'Grados de libertad':<24}: {chi.degrees_of_freedom}")
    print(f"{'Valor p':<24}: {chi.p_value:.8g}")
    _show_decision("Chi-cuadrado", chi.p_value, alpha)

    _show_section("PRUEBA KOLMOGOROV-SMIRNOV")
    print(f"{'Estadístico D':<24}: {ks.statistic:.8f}")
    print(f"{'D positiva':<24}: {ks.positive_difference:.8f}")
    print(f"{'D negativa':<24}: {ks.negative_difference:.8f}")
    print(f"{'Valor p (aproximado)':<24}: {ks.p_value:.8g}")
    _show_decision("Kolmogorov-Smirnov", ks.p_value, alpha)

    _show_section("ESTIMACIÓN DE π — MONTE CARLO")
    print(f"{'Puntos simulados':<24}: {pi.sample_count:,}")
    print(f"{'Puntos dentro del cuarto de círculo':<36}: {pi.points_inside:,}")
    print(f"{'Estimación de π':<24}: {pi.estimate:.8f}")
    print(f"{'Error absoluto':<24}: {pi.absolute_error:.8f}")
    print(f"{'Tiempo de ejecución':<24}: {pi_seconds:.6f} s")

    outputs: list[str] = []
    if csv_path:
        write_sequence_csv(values, csv_path)
        outputs.append(f"Secuencia CSV: {csv_path}")
    if plot_path:
        plot_cdf(values, plot_path)
        outputs.append(f"Gráfica: {plot_path}")
    if outputs:
        _show_section("ARCHIVOS GENERADOS")
        for output in outputs:
            print(f"- {output}")


def show_benchmark(sizes: list[int], results: list[tuple[float, float, float]]) -> None:
    _show_section("BENCHMARK DE ESTIMACIÓN DE π")
    print(f"{'N':>12}  {'π estimado':>14}  {'Error absoluto':>16}  {'Tiempo (s)':>12}")
    print(f"{'-' * 12}  {'-' * 14}  {'-' * 16}  {'-' * 12}")
    for size, (estimate, error, elapsed) in zip(sizes, results):
        print(f"{size:>12,}  {estimate:>14.8f}  {error:>16.8f}  {elapsed:>12.6f}")


def show_comparison(
    names: list[str], results: list[tuple[float, float, float, float]]
) -> None:
    _show_section("COMPARACIÓN DE CONFIGURACIONES LCG")
    print(
        f"{'Configuración':<24}  {'χ²':>12}  {'p(χ²)':>12}  "
        f"{'D(KS)':>12}  {'p(KS)':>12}"
    )
    print(f"{'-' * 24}  {'-' * 12}  {'-' * 12}  {'-' * 12}  {'-' * 12}")
    for name, (chi_statistic, chi_p, ks_statistic, ks_p) in zip(names, results):
        print(
            f"{name:<24}  {chi_statistic:>12.6f}  {chi_p:>12.6g}  "
            f"{ks_statistic:>12.8f}  {ks_p:>12.6g}"
        )


def _show_decision(name: str, p_value: float, alpha: float) -> None:
    decision = (
        "no se rechaza H0 (compatible con U(0,1))"
        if p_value >= alpha
        else "se rechaza H0"
    )
    print(f"  Decisión ({name}): {decision} (α={alpha:.3f})")


def write_sequence_csv(values: list[float], file_path: str) -> None:
    with Path(file_path).open("w", newline="", encoding="utf-8") as output:
        writer = csv.writer(output, lineterminator="\n")
        writer.writerow(("indice", "valor"))
        writer.writerows(enumerate(values, 1))


def plot_cdf(values: list[float], file_path: str) -> None:
    try:
        import matplotlib.pyplot as plt
    except ImportError as error:
        raise RuntimeError(
            "La gráfica requiere Matplotlib. Instala las dependencias con "
            "`python -m pip install -r requirements.txt`."
        ) from error

    ordered = sorted(values)
    empirical = [(index + 1) / len(ordered) for index in range(len(ordered))]
    plt.figure(figsize=(8, 5))
    plt.step(ordered, empirical, where="post", label="Distribución empírica")
    plt.plot([0, 1], [0, 1], label="Uniforme teórica U(0,1)")
    plt.xlim(0, 1)
    plt.ylim(0, 1)
    plt.xlabel("x")
    plt.ylabel("F(x)")
    plt.title("Distribución empírica vs. uniforme teórica")
    plt.grid(True, alpha=0.25)
    plt.legend()
    plt.tight_layout()
    plt.savefig(file_path)
    plt.close()