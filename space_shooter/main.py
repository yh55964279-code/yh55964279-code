"""Entry point helpers for launching the game."""

from .game import SpaceShooter


def main() -> None:
    """Create the game instance and start the loop."""
    game = SpaceShooter()
    game.run()
