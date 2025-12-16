"""Boss sprite for milestone score events."""

from typing import Tuple

import pygame

from .constants import RED, SCREEN_WIDTH, YELLOW


class Boss(pygame.sprite.Sprite):
    """A tougher enemy that appears at score milestones."""

    def __init__(self, pos: Tuple[int, int], health: int = 60):
        super().__init__()
        self.image = pygame.Surface((120, 60))
        self.image.fill(RED)
        pygame.draw.rect(self.image, YELLOW, self.image.get_rect(), width=4)
        self.rect = self.image.get_rect(center=pos)
        self.health = health
        self.speed_x = 4
        self.direction = 1

    def update(self, *_: object) -> None:
        self.rect.x += self.speed_x * self.direction
        if self.rect.left <= 10 or self.rect.right >= SCREEN_WIDTH - 10:
            self.direction *= -1

    def take_damage(self, amount: int) -> bool:
        """Apply damage and return True if the boss is defeated."""
        self.health -= amount
        if self.health <= 0:
            self.kill()
            return True
        return False
