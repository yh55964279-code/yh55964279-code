"""Game loop, event handling, and gameplay systems."""

import random
import sys
from typing import Tuple

import pygame

from .constants import BLACK, FPS, SCREEN_HEIGHT, SCREEN_WIDTH, WHITE
from .sprites import Bullet, Enemy, Player


class SpaceShooter:
    """Manage the gameplay loop and game state."""

    def __init__(self) -> None:
        pygame.init()
        pygame.display.set_caption("Space Shooter")
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
        self.clock = pygame.time.Clock()
        self.font = pygame.font.SysFont("arial", 20)

        self.all_sprites = pygame.sprite.Group()
        self.bullets = pygame.sprite.Group()
        self.enemies = pygame.sprite.Group()

        self.player = Player((SCREEN_WIDTH // 2, SCREEN_HEIGHT - 60))
        self.all_sprites.add(self.player)

        self.score = 0
        self.enemy_spawn_delay = 900  # milliseconds
        self.last_enemy_spawn = pygame.time.get_ticks()

    def run(self) -> None:
        while True:
            self.process_events()
            self.update_game()
            self.render()
            self.clock.tick(FPS)

    def process_events(self) -> None:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            if event.type == pygame.KEYDOWN and event.key == pygame.K_SPACE:
                self.shoot()

    def update_game(self) -> None:
        pressed_keys = pygame.key.get_pressed()
        self.all_sprites.update(pressed_keys)
        self.spawn_enemy()
        self.handle_collisions()

    def render(self) -> None:
        self.screen.fill(BLACK)
        self.all_sprites.draw(self.screen)
        self.draw_hud()
        pygame.display.flip()

    def spawn_enemy(self) -> None:
        now = pygame.time.get_ticks()
        if now - self.last_enemy_spawn < self.enemy_spawn_delay:
            return

        x = random.randint(30, SCREEN_WIDTH - 30)
        speed_y = random.randint(3, 7)
        enemy = Enemy((x, -20), speed_y)
        self.register_enemy(enemy)

        self.last_enemy_spawn = now
        self.enemy_spawn_delay = max(350, self.enemy_spawn_delay - 5)

    def register_enemy(self, enemy: Enemy) -> None:
        self.all_sprites.add(enemy)
        self.enemies.add(enemy)

    def shoot(self) -> None:
        bullet = Bullet(self.player.rect.midtop)
        self.register_bullet(bullet)

    def register_bullet(self, bullet: Bullet) -> None:
        self.all_sprites.add(bullet)
        self.bullets.add(bullet)

    def handle_collisions(self) -> None:
        hits = pygame.sprite.groupcollide(self.enemies, self.bullets, True, True)
        self.score += len(hits) * 10

        collisions = pygame.sprite.spritecollide(self.player, self.enemies, True)
        if collisions:
            self.player.lives -= 1
            if self.player.lives <= 0:
                self.game_over()

    def game_over(self) -> None:
        text = self.font.render("GAME OVER - Press R to restart", True, WHITE)
        text_rect = text.get_rect(center=(SCREEN_WIDTH // 2, SCREEN_HEIGHT // 2))
        while True:
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    pygame.quit()
                    sys.exit()
                if event.type == pygame.KEYDOWN and event.key == pygame.K_r:
                    self.__init__()
                    return

            self.screen.fill(BLACK)
            self.screen.blit(text, text_rect)
            pygame.display.flip()
            self.clock.tick(15)

    def draw_hud(self) -> None:
        score_text = self.font.render(f"Score: {self.score}", True, WHITE)
        lives_text = self.font.render(f"Lives: {self.player.lives}", True, WHITE)
        self.screen.blit(score_text, (12, 10))
        self.screen.blit(lives_text, (SCREEN_WIDTH - 120, 10))
