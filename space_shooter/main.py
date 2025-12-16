"""게임을 실행하기 위한 진입점 헬퍼."""

from .game import SpaceShooter


def main() -> None:
    """게임 인스턴스를 생성하고 루프를 시작."""
    game = SpaceShooter()
    game.run()
