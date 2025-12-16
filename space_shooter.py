"""Arcade-style vertical scrolling shooter built with pygame.

Run the game with:
    python space_shooter.py

If you cannot open a window (e.g., on a headless server), set SDL_VIDEODRIVER=dummy
before running to disable the display requirement. The game uses simple colored shapes
instead of external assets for portability.
"""

from space_shooter.main import main


if __name__ == "__main__":
    main()
