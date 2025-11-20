package com.example.customenchant;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SwordAuraListener implements Listener {
    private static final long COOLDOWN_TICKS = 40L; // 2 seconds
    private static final double STEP = 1.0;
    private static final int STEPS = 10;
    private static final double HIT_RADIUS = 0.75;
    private static final double BASE_DAMAGE = 2.0;

    private final Enchantment enchantment;
    private final Map<UUID, Long> lastUse = new HashMap<>();

    public SwordAuraListener(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        int level = item.getEnchantmentLevel(enchantment);
        if (level <= 0) {
            return;
        }

        long now = player.getWorld().getFullTime();
        long last = lastUse.getOrDefault(player.getUniqueId(), 0L);
        if (now - last < COOLDOWN_TICKS) {
            return;
        }
        lastUse.put(player.getUniqueId(), now);

        spawnBladeWave(player, level);
    }

    private void spawnBladeWave(Player player, int level) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection().normalize();
        World world = player.getWorld();
        Set<UUID> damaged = new HashSet<>();

        for (int i = 1; i <= STEPS; i++) {
            Location point = start.clone().add(direction.clone().multiply(i * STEP));
            world.spawnParticle(Particle.SWEEP_ATTACK, point, 1, 0.15, 0.15, 0.15, 0.0);
            world.spawnParticle(Particle.CRIT, point, 4, 0.1, 0.1, 0.1, 0.01);

            damageNearby(player, level, damaged, point);
        }
    }

    private void damageNearby(Player player, int level, Set<UUID> damaged, Location point) {
        World world = point.getWorld();
        double damage = BASE_DAMAGE + level;

        for (Entity entity : world.getNearbyEntities(point, HIT_RADIUS, HIT_RADIUS, HIT_RADIUS)) {
            if (entity.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (!damaged.add(entity.getUniqueId())) {
                continue;
            }
            living.damage(damage, player);
        }
    }
}
