"""Core descriptive statistics helpers.

The functions in this module deliberately avoid external dependencies and work
with any iterable of numeric values. All helpers coerce the input to a list
internally to support single-pass iterables while keeping a small footprint.
"""
from __future__ import annotations

from collections import Counter
from math import sqrt
from typing import Iterable, List


Number = float | int


def _ensure_values(values: Iterable[Number]) -> List[float]:
    """Convert *values* to a list of floats and validate non-empty input.

    Raises:
        ValueError: If *values* is empty.
    """

    data = [float(v) for v in values]
    if not data:
        raise ValueError("At least one value is required")
    return data


def mean(values: Iterable[Number]) -> float:
    """Return the arithmetic mean of *values*.

    Examples:
        >>> mean([1, 2, 3])
        2.0
    """

    data = _ensure_values(values)
    return sum(data) / len(data)


def median(values: Iterable[Number]) -> float:
    """Return the median of *values*.

    For an even number of inputs, the function returns the average of the two
    middle values.
    """

    data = sorted(_ensure_values(values))
    mid = len(data) // 2
    if len(data) % 2:
        return data[mid]
    return (data[mid - 1] + data[mid]) / 2.0


def mode(values: Iterable[Number]) -> List[float]:
    """Return a list of the most common value(s) in *values*.

    The result is sorted in ascending order and includes all tied modes.
    """

    data = _ensure_values(values)
    counts = Counter(data)
    max_count = max(counts.values())
    return sorted([value for value, count in counts.items() if count == max_count])


def variance(values: Iterable[Number], sample: bool = False) -> float:
    """Return the variance of *values*.

    Args:
        values: Iterable of numeric values.
        sample: When ``True``, compute the sample variance (dividing by ``n-1``);
            otherwise compute population variance (dividing by ``n``).
    """

    data = _ensure_values(values)
    if sample and len(data) < 2:
        raise ValueError("Sample variance requires at least two values")

    avg = mean(data)
    squared_diffs = [(x - avg) ** 2 for x in data]
    divisor = len(data) - 1 if sample else len(data)
    return sum(squared_diffs) / divisor


def standard_deviation(values: Iterable[Number], sample: bool = False) -> float:
    """Return the standard deviation of *values*.

    This is the square root of :func:`variance`.
    """

    return sqrt(variance(values, sample=sample))


def summary(values: Iterable[Number]) -> dict[str, float | List[float]]:
    """Compute a compact statistical summary of *values*.

    Returns a dictionary containing mean, median, mode, population variance,
    population standard deviation, minimum, maximum, and count.
    """

    data = _ensure_values(values)
    return {
        "count": len(data),
        "min": min(data),
        "max": max(data),
        "mean": mean(data),
        "median": median(data),
        "mode": mode(data),
        "variance": variance(data),
        "standard_deviation": standard_deviation(data),
    }
