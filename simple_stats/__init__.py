"""A lightweight helper package for basic descriptive statistics.

The :mod:`simple_stats.descriptive` module exposes common summary functions such
as mean, median, mode, variance, and standard deviation. All functions accept an
iterable of numeric values and raise :class:`ValueError` when the input is
empty.
"""

from .descriptive import (
    mean,
    median,
    mode,
    variance,
    standard_deviation,
    summary,
)

__all__ = [
    "mean",
    "median",
    "mode",
    "variance",
    "standard_deviation",
    "summary",
]
