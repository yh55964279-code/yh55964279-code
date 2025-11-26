import unittest

from simple_stats.descriptive import (
    mean,
    median,
    mode,
    variance,
    standard_deviation,
    summary,
)


class TestDescriptiveStats(unittest.TestCase):
    def test_mean_and_median(self):
        values = [1, 2, 3, 4]
        self.assertEqual(mean(values), 2.5)
        self.assertEqual(median(values), 2.5)

    def test_median_odd(self):
        self.assertEqual(median([5, 3, 1]), 3)

    def test_mode_handles_multiple(self):
        self.assertEqual(mode([1, 2, 2, 3, 3]), [2.0, 3.0])
        self.assertEqual(mode([4, 4, 4]), [4.0])

    def test_variance_and_standard_deviation(self):
        values = [2, 4, 4, 4, 5, 5, 7, 9]
        self.assertAlmostEqual(variance(values), 4.0)
        self.assertAlmostEqual(variance(values, sample=True), 4.571428571428571)
        self.assertAlmostEqual(standard_deviation(values), 2.0)

    def test_summary(self):
        values = [10, 20, 20, 40]
        stats = summary(values)
        self.assertEqual(stats["count"], 4)
        self.assertEqual(stats["min"], 10.0)
        self.assertEqual(stats["max"], 40.0)
        self.assertEqual(stats["mean"], 22.5)
        self.assertEqual(stats["median"], 20.0)
        self.assertEqual(stats["mode"], [20.0])

    def test_input_validation(self):
        with self.assertRaises(ValueError):
            mean([])
        with self.assertRaises(ValueError):
            variance([], sample=True)
        with self.assertRaises(ValueError):
            variance([1], sample=True)


if __name__ == "__main__":
    unittest.main()
