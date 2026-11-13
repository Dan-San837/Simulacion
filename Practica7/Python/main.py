from controller.simulation_controller import SimulationController
from view.console_view import ConsoleView
from view.svg_chart_view import SvgChartView


def main() -> None:
    controller = SimulationController()
    sample_sizes = [1_000, 10_000, 100_000, 1_000_000]

    pi_results = controller.run_pi_experiment(sample_sizes)
    ConsoleView.print_pi_table(pi_results)

    traffic_summary = controller.run_traffic_experiment(
        lambda_rate=0.8,
        mu_rate=1.2,
        red_duration=10.0,
        green_duration=20.0,
        dt=1.0,
        simulations=250,
    )
    ConsoleView.print_traffic_summary(traffic_summary)
    ConsoleView.print_comparison_message()
    chart_path = SvgChartView.write_charts(pi_results, traffic_summary)
    print(f"\nGráficas guardadas en: {chart_path.resolve()}")


if __name__ == "__main__":
    main()
