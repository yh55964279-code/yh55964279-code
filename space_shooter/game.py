"""게임 루프, 이벤트 처리, 게임 플레이 시스템 구현."""

import math
import random
import sys
from typing import Tuple

import pygame

from .constants import BLACK, FPS, SCREEN_HEIGHT, SCREEN_WIDTH, WHITE
from .boss import Boss
from .sprites import Bullet, Enemy, EnemyBullet, Player


class SpaceShooter:
    """게임 루프와 상태를 관리하는 클래스."""

    def __init__(self) -> None:
        pygame.init()
        pygame.display.set_caption("Space Shooter")
        self.screen = pygame.display.set_mode((SCREEN_WIDTH, SCREEN_HEIGHT))
        self.clock = pygame.time.Clock()
        self.font = pygame.font.SysFont("arial", 20)

        self.all_sprites = pygame.sprite.Group()
        self.bullets = pygame.sprite.Group()
        self.enemy_bullets = pygame.sprite.Group()
        self.enemies = pygame.sprite.Group()
        self.bosses = pygame.sprite.Group()

        self.player = Player((SCREEN_WIDTH // 2, SCREEN_HEIGHT - 60))
        self.all_sprites.add(self.player)

        self.score = 0
        self.enemy_spawn_delay = 900  # 밀리초 단위 스폰 지연 시간
        self.last_enemy_spawn = pygame.time.get_ticks()
        self.next_boss_score = 100
        self.enemy_shot_delay = 1100
        self.last_enemy_shot = pygame.time.get_ticks()
        self.boss_shot_delay = 950
        self.last_boss_shot = pygame.time.get_ticks()

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
        self.spawn_boss()
        self.enemy_fire()
        self.boss_fire()
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

    def spawn_boss(self) -> None:
        if self.score < self.next_boss_score or self.bosses:
            return

        boss = Boss((SCREEN_WIDTH // 2, 80))
        self.bosses.add(boss)
        self.all_sprites.add(boss)
        self.next_boss_score += 100

    def shoot(self) -> None:
        bullet = Bullet(self.player.rect.midtop)
        self.register_bullet(bullet)

    def register_bullet(self, bullet: Bullet) -> None:
        self.all_sprites.add(bullet)
        self.bullets.add(bullet)

    def register_enemy_bullet(self, bullet: EnemyBullet) -> None:
        self.all_sprites.add(bullet)
        self.enemy_bullets.add(bullet)

    def enemy_fire(self) -> None:
        """화면에 적이 있을 때 주기적으로 탄막을 발사."""

        now = pygame.time.get_ticks()
        if now - self.last_enemy_shot < self.enemy_shot_delay:
            return
        if not self.enemies:
            return

        enemy = random.choice(self.enemies.sprites())
        bullet = EnemyBullet(enemy.rect.midbottom, speed=(0, 7))
        self.register_enemy_bullet(bullet)
        self.last_enemy_shot = now

    def boss_fire(self) -> None:
        """보스가 존재할 때 점수에 비례한 탄막 개수를 발사."""

        if not self.bosses:
            return

        now = pygame.time.get_ticks()
        if now - self.last_boss_shot < self.boss_shot_delay:
            return

        boss = next(iter(self.bosses))
        barrage_count = max(1, self.score // 100)
        spread_angle = 50
        for index in range(barrage_count):
            if barrage_count == 1:
                angle_deg = 90
            else:
                angle_deg = 90 - spread_angle / 2 + (spread_angle * index) / (barrage_count - 1)
            angle_rad = math.radians(angle_deg)
            dx = math.cos(angle_rad) * 6
            dy = math.sin(angle_rad) * 6
            bullet = EnemyBullet(boss.rect.midbottom, speed=(dx, dy))
            self.register_enemy_bullet(bullet)

        self.last_boss_shot = now

    def handle_collisions(self) -> None:
        hits = pygame.sprite.groupcollide(self.enemies, self.bullets, True, True)
        if not self.bosses:
            self.score += len(hits) * 10

        boss_hits = pygame.sprite.groupcollide(self.bosses, self.bullets, False, True)
        for boss, bullets in boss_hits.items():
            defeated = boss.take_damage(len(bullets) * 5)
            if defeated:
                self.score += 50

        collisions = pygame.sprite.spritecollide(self.player, self.enemies, True)
        bullet_hits = pygame.sprite.spritecollide(self.player, self.enemy_bullets, True)
        boss_collision = pygame.sprite.spritecollide(self.player, self.bosses, True)
        if collisions or boss_collision or bullet_hits:
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

        if self.bosses:
            boss = next(iter(self.bosses))
            health_text = self.font.render(f"Boss HP: {boss.health}", True, WHITE)
            self.screen.blit(health_text, (SCREEN_WIDTH // 2 - 50, 10))
