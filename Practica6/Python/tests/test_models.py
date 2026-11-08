import math
import unittest

from models.Kolmogorov import test_kolmogorov
from models.chi_cuadrado import test_chi_square
from models.lcg import LCGParameters, LinearCongruentialGenerator
from models.monte_carlo import estimate_pi


class LCGTests(unittest.TestCase):
    def test_default_sequence_matches_reference_integers(self) -> None:
        generator = LinearCongruentialGenerator(LCGParameters())
        self.assertEqual(
            [generator.next_int() for _ in range(5)],
            [1_103_527_590, 377_401_575, 662_824_084, 1_147_902_781, 2_035_015_474],
        )

    def test_values_are_in_unit_interval(self) -> None:
        values = LinearCongruentialGenerator(LCGParameters()).generate(100)
        self.assertTrue(all(0 <= value < 1 for value in values))


class UniformityTests(unittest.TestCase):
    def test_chi_square_uniform_counts_have_zero_statistic(self) -> None:
        sample = [(interval + 0.5) / 10 for interval in range(10) for _ in range(10)]
        result = test_chi_square(sample)
        self.assertEqual(result.observed, (10,) * 10)
        self.assertEqual(result.degrees_of_freedom, 9)
        self.assertAlmostEqual(result.statistic, 0)
        self.assertAlmostEqual(result.p_value, 1)

    def test_chi_square_rejects_strongly_concentrated_sample(self) -> None:
        result = test_chi_square([0.5] * 100)
        self.assertGreater(result.statistic, 100)
        self.assertLess(result.p_value, 0.05)

    def test_kolmogorov_reports_valid_statistic_and_p_value(self) -> None:
        result = test_kolmogorov([0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9])
        self.assertGreaterEqual(result.statistic, 0)
        self.assertLessEqual(result.statistic, 1)
        self.assertGreaterEqual(result.p_value, 0)
        self.assertLessEqual(result.p_value, 1)

    def test_kolmogorov_rejects_concentrated_sample(self) -> None:
        self.assertLess(test_kolmogorov([0.5] * 100).p_value, 0.05)


class MonteCarloTests(unittest.TestCase):
    def test_pi_estimate_is_reproducible(self) -> None:
        result = estimate_pi(LinearCongruentialGenerator(LCGParameters()), 1_000)
        self.assertEqual(result.estimate, 4 * result.points_inside / 1_000)
        self.assertAlmostEqual(result.absolute_error, abs(result.estimate - math.pi))
        self.assertEqual(result.sample_count, 1_000)


if __name__ == "__main__":
    unittest.main()
