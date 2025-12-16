"""비행 슈팅 게임에서 사용하는 스프라이트 클래스."""

from dataclasses import dataclass
from typing import Tuple

import pygame

from .constants import BLUE, RED, SCREEN_HEIGHT, SCREEN_WIDTH, YELLOW


@dataclass
class Speed:
    x: int
    y: int


class Player(pygame.sprite.Sprite):
    """플레이어가 조작하는 우주선 스프라이트."""

    def __init__(self, pos: Tuple[int, int]):
        super().__init__()
        self.image = pygame.Surface((40, 48))
        self.image.fill(BLUE)
        pygame.draw.polygon(
            self.image,
            YELLOW,
            [(20, 0), (0, 48), (40, 48)],
        )
        self.rect = self.image.get_rect(center=pos)
        self.speed = Speed(6, 6)
        self.lives = 3

    def update(
        self, pressed_keys: pygame.key.ScancodeWrapper | None = None
    ) -> None:
        if pressed_keys is None:
            return

        if pressed_keys[pygame.K_LEFT]:
            self.rect.x -= self.speed.x
        if pressed_keys[pygame.K_RIGHT]:
            self.rect.x += self.speed.x
        if pressed_keys[pygame.K_UP]:
            self.rect.y -= self.speed.y
        if pressed_keys[pygame.K_DOWN]:
            self.rect.y += self.speed.y

        self.rect.clamp_ip(pygame.Rect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT))


class Bullet(pygame.sprite.Sprite):
    """플레이어가 발사하는 탄환 스프라이트."""

    def __init__(self, pos: Tuple[int, int]):
        super().__init__()
        self.image = pygame.Surface((6, 16))
        self.image.fill(YELLOW)
        self.rect = self.image.get_rect(center=pos)
        self.speed_y = -12

    def update(self, *_: object) -> None:
        self.rect.y += self.speed_y
        if self.rect.bottom < 0:
            self.kill()


class EnemyBullet(pygame.sprite.Sprite):
    """적과 보스가 발사하는 탄막 스프라이트."""

    def __init__(self, pos: Tuple[int, int], speed: Tuple[float, float]):
        super().__init__()
        self.image = pygame.Surface((8, 16))
        self.image.fill(YELLOW)
        self.rect = self.image.get_rect(center=pos)
        self.speed_x, self.speed_y = speed

    def update(self, *_: object) -> None:
        self.rect.x += self.speed_x
        self.rect.y += self.speed_y
        if self.rect.top > SCREEN_HEIGHT or self.rect.right < 0 or self.rect.left > SCREEN_WIDTH:
            self.kill()


class Enemy(pygame.sprite.Sprite):
    """화면 상단에서 내려오는 적 우주선 스프라이트."""

    def __init__(self, pos: Tuple[int, int], speed_y: int):
        super().__init__()
        self.image = pygame.Surface((34, 34))
        self.image.fill(RED)
        self.rect = self.image.get_rect(center=pos)
        self.speed_y = speed_y

    def update(self, *_: object) -> None:
        self.rect.y += self.speed_y
        if self.rect.top > SCREEN_HEIGHT:
            self.kill()
