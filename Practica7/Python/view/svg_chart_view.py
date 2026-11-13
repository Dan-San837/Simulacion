from html import escape
import math
from pathlib import Path

from model.monte_carlo import PiSimulationResult
from model.traffic_model import TrafficSummary


class SvgChartView:
    WIDTH = 1100
    COLORS = ("#2563eb", "#dc2626")

    @classmethod
    def write_charts(
        cls,
        pi_results: list[PiSimulationResult],
        traffic_summary: TrafficSummary,
        output_path: str = "resultados_monte_carlo.svg",
    ) -> Path:
        target = Path(output_path)
        document = [
            f'<svg xmlns="http://www.w3.org/2000/svg" width="{cls.WIDTH}" height="1480" viewBox="0 0 {cls.WIDTH} 1480">',
            "<style>text{font-family:Arial,sans-serif;fill:#1f2937}.grid{stroke:#d1d5db;stroke-width:1}.axis{stroke:#374151;stroke-width:1.5}.series{fill:none;stroke-width:3}.point{stroke:#fff;stroke-width:2}</style>",
            '<rect width="100%" height="100%" fill="#fff"/>',
            '<text x="550" y="38" text-anchor="middle" font-size="24" font-weight="bold">Resultados de las simulaciones Monte Carlo</text>',
        ]

        sample_labels = [f"{result.samples:,}" for result in pi_results]
        category_positions = list(range(len(pi_results)))
        estimates = [result.estimated_pi for result in pi_results]
        errors = [result.absolute_error for result in pi_results]
        execution_times = [result.execution_time for result in pi_results]
        cls._draw_panel(
            document,
            top=60,
            title="Estimación de π por tamaño de muestra",
            x_values=category_positions,
            x_labels=sample_labels,
            series=[("π estimado", cls.COLORS[0], estimates), ("π real", cls.COLORS[1], [3.141592653589793] * len(pi_results))],
            x_axis_title="Número de puntos (N)",
            y_axis_title="Valor de π",
        )
        cls._draw_panel(
            document,
            top=420,
            title="Error absoluto por tamaño de muestra",
            x_values=category_positions,
            x_labels=sample_labels,
            series=[("Error absoluto", cls.COLORS[0], errors)],
            x_axis_title="Número de puntos (N)",
            y_axis_title="Error",
            include_zero=True,
        )
        cls._draw_panel(
            document,
            top=780,
            title="Tiempo de ejecución por tamaño de muestra",
            x_values=category_positions,
            x_labels=sample_labels,
            series=[("Tiempo de ejecución", cls.COLORS[0], execution_times)],
            x_axis_title="Número de puntos (N)",
            y_axis_title="Tiempo (s)",
            include_zero=True,
        )
        cls._draw_panel(
            document,
            top=1140,
            title="Evolución promedio de vehículos en cola",
            x_values=traffic_summary.time_points,
            x_labels=None,
            series=[("Cola promedio", cls.COLORS[0], traffic_summary.average_queue_by_time)],
            x_axis_title="Tiempo (s)",
            y_axis_title="Vehículos",
            include_zero=True,
        )
        document.append("</svg>")
        target.write_text("\n".join(document), encoding="utf-8")
        return target

    @classmethod
    def _draw_panel(
        cls,
        document: list[str],
        top: int,
        title: str,
        x_values: list[float],
        x_labels: list[str] | None,
        series: list[tuple[str, str, list[float]]],
        x_axis_title: str,
        y_axis_title: str,
        include_zero: bool = False,
    ) -> None:
        left, right = 105, 1040
        plot_top, plot_bottom = top + 55, top + 285
        plot_height = plot_bottom - plot_top
        all_values = [value for _, _, values in series for value in values]
        if not all_values:
            return

        low = min(all_values)
        high = max(all_values)
        if include_zero:
            low = min(0.0, low)
        if high == low:
            high = low + 1.0
        padding = (high - low) * 0.1
        low = min(0.0, low - padding) if include_zero else low - padding
        high += padding

        document.append(f'<text x="{left}" y="{top + 25}" font-size="19" font-weight="bold">{escape(title)}</text>')
        for tick in range(6):
            ratio = tick / 5
            y = plot_bottom - ratio * plot_height
            value = low + ratio * (high - low)
            document.append(f'<line class="grid" x1="{left}" y1="{y:.2f}" x2="{right}" y2="{y:.2f}"/>')
            document.append(f'<text x="{left - 12}" y="{y + 5:.2f}" text-anchor="end" font-size="12">{value:.4f}</text>')
        document.append(f'<line class="axis" x1="{left}" y1="{plot_top}" x2="{left}" y2="{plot_bottom}"/>')
        document.append(f'<line class="axis" x1="{left}" y1="{plot_bottom}" x2="{right}" y2="{plot_bottom}"/>')

        if len(x_values) == 1:
            x_positions = [(left + right) / 2]
        else:
            x_min, x_max = min(x_values), max(x_values)
            if x_min == x_max:
                x_positions = [(left + right) / 2] * len(x_values)
            else:
                x_positions = [left + (value - x_min) / (x_max - x_min) * (right - left) for value in x_values]

        x_tick_stride = max(1, math.ceil((len(x_positions) - 1) / 5))
        for index, x in enumerate(x_positions):
            if x_labels is None and index % x_tick_stride != 0 and index != len(x_positions) - 1:
                continue
            document.append(f'<line class="grid" x1="{x:.2f}" y1="{plot_bottom}" x2="{x:.2f}" y2="{plot_bottom + 5}"/>')
            label = x_labels[index] if x_labels is not None else f"{x_values[index]:g}"
            document.append(f'<text x="{x:.2f}" y="{plot_bottom + 21}" text-anchor="middle" font-size="12">{escape(label)}</text>')

        for series_index, (label, color, values) in enumerate(series):
            coordinates = []
            for x, value in zip(x_positions, values):
                y = plot_bottom - (value - low) / (high - low) * plot_height
                coordinates.append((x, y))
            points = " ".join(f"{x:.2f},{y:.2f}" for x, y in coordinates)
            document.append(f'<polyline class="series" stroke="{color}" points="{points}"/>')
            for x, y in coordinates:
                document.append(f'<circle class="point" cx="{x:.2f}" cy="{y:.2f}" r="5" fill="{color}"/>')
            legend_x = right - 170
            legend_y = plot_top + 18 * series_index
            document.append(f'<line x1="{legend_x}" y1="{legend_y}" x2="{legend_x + 22}" y2="{legend_y}" stroke="{color}" stroke-width="3"/>')
            document.append(f'<text x="{legend_x + 28}" y="{legend_y + 4}" font-size="12">{escape(label)}</text>')

        document.append(f'<text x="{(left + right) / 2}" y="{top + 330}" text-anchor="middle" font-size="13">{escape(x_axis_title)}</text>')
        document.append(
            f'<text x="25" y="{top + 170}" text-anchor="middle" font-size="13" transform="rotate(-90 25 {top + 170})">{escape(y_axis_title)}</text>'
        )
