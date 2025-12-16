"""점수 이정표마다 등장하는 보스 스프라이트."""

from typing import Tuple

import pygame

from .constants import RED, SCREEN_WIDTH, YELLOW


class Boss(pygame.sprite.Sprite):
    """점수 이정표마다 등장하는 강한 적 스프라이트."""

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
        """피해를 적용하고 보스가 쓰러지면 True를 반환."""
        self.health -= amount
        if self.health <= 0:
            self.kill()
            return True
        return False
