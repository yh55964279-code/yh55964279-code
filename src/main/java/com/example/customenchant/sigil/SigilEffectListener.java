package com.example.customenchant.sigil;

import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;

public class SigilEffectListener implements Listener {
    private static final UUID GROWTH_HEALTH = UUID.fromString("1f5c85cf-1b52-4c47-9d3b-d2b8b45b7959");
    private static final UUID ENDURANCE_HEALTH = UUID.fromString("f0e30a04-c0d3-4c35-b79e-0dd1c5302db7");
    private static final UUID IMPULSE_SPEED = UUID.fromString("15a4d2de-45b9-4eaa-91f0-68232cebb90c");
    private static final UUID IMPULSE_ATTACK_SPEED = UUID.fromString("7876b0dd-57a2-45c1-9f4e-89cf89a9af2d");
    private static final UUID IMPULSE_HEALTH = UUID.fromString("e4960db1-6b58-4d05-9349-73fc6ab955ba");
    private static final UUID FORTIFY_SPEED = UUID.fromString("0e4279a9-6b7f-4d0a-85a5-0bfec11c9fef");

    private final Plugin plugin;
    private final SigilManager manager;

    public SigilEffectListener(Plugin plugin, SigilManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        requestAttributeUpdate(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        clearAttributes(event.getPlayer());
    }

    @EventHandler
    public void onItemSwitch(PlayerItemHeldEvent event) {
        requestAttributeUpdate(event.getPlayer());
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        requestAttributeUpdate(event.getPlayer());
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            requestAttributeUpdate(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        applyEnduranceReduction(player, event);
        applyShieldMitigation(player, event);
        triggerReuseHeal(player, event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = getPlayer(event.getDamager());
        if (attacker != null) {
            applyStrifeBonus(attacker, event);
            applyMadness(attacker, event);
            applyEndurancePenalty(attacker, event);
        }
    }

    private Player getPlayer(Entity source) {
        if (source instanceof Player player) {
            return player;
        }
        if (source.getType() == EntityType.TRIDENT || source.getType() == EntityType.ARROW
                || source.getType() == EntityType.SPECTRAL_ARROW) {
            ProjectileSource shooter = ((org.bukkit.entity.Projectile) source).getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }
        return null;
    }

    private void updateAttributes(Player player) {
        int growth = manager.getEquippedLevel(player, SigilType.GROWTH);
        applyNumberModifier(player, Attribute.GENERIC_MAX_HEALTH, GROWTH_HEALTH, "sigil-growth", growth);

        int endurance = manager.getEquippedLevel(player, SigilType.ENDURANCE);
        double enduranceBonus = Math.max(0, endurance - 1);
        applyNumberModifier(player, Attribute.GENERIC_MAX_HEALTH, ENDURANCE_HEALTH, "sigil-endurance", enduranceBonus);

        int impulse = manager.getEquippedLevel(player, SigilType.IMPULSE);
        double speedBonus = impulse * 0.004; // +0.4% per level
        double attackSpeedBonus = impulse * 0.006; // +0.6% per level
        double impulsePenalty = impulse == 0 ? 0 : -impulse * 0.004;
        applyNumberModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, IMPULSE_SPEED, "sigil-impulse-speed", speedBonus);
        applyNumberModifier(player, Attribute.GENERIC_ATTACK_SPEED, IMPULSE_ATTACK_SPEED, "sigil-impulse-attack", attackSpeedBonus);
        applyScalarModifier(player, Attribute.GENERIC_MAX_HEALTH, IMPULSE_HEALTH, "sigil-impulse-health", impulsePenalty);

        boolean fortifyActive = getShieldLevel(player, SigilType.FORTIFICATION) > 0;
        applyScalarModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, FORTIFY_SPEED, "sigil-fortify-speed",
                fortifyActive ? -0.5 : 0);
    }

    private void requestAttributeUpdate(Player player) {
        plugin.getServer().getScheduler().runTask(plugin, () -> updateAttributes(player));
    }

    private void clearAttributes(Player player) {
        removeModifier(player, Attribute.GENERIC_MAX_HEALTH, GROWTH_HEALTH);
        removeModifier(player, Attribute.GENERIC_MAX_HEALTH, ENDURANCE_HEALTH);
        removeModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, IMPULSE_SPEED);
        removeModifier(player, Attribute.GENERIC_ATTACK_SPEED, IMPULSE_ATTACK_SPEED);
        removeModifier(player, Attribute.GENERIC_MAX_HEALTH, IMPULSE_HEALTH);
        removeModifier(player, Attribute.GENERIC_MOVEMENT_SPEED, FORTIFY_SPEED);
    }

    private void applyNumberModifier(Player player, Attribute attribute, UUID id, String name, double amount) {
        applyModifier(player, attribute, id, name, amount, Operation.ADD_NUMBER);
    }

    private void applyScalarModifier(Player player, Attribute attribute, UUID id, String name, double amount) {
        applyModifier(player, attribute, id, name, amount, Operation.MULTIPLY_SCALAR_1);
    }

    private void applyModifier(Player player, Attribute attribute, UUID id, String name, double amount,
            Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        removeModifier(player, attribute, id);
        if (amount == 0) {
            return;
        }
        AttributeModifier modifier = new AttributeModifier(id, name, amount, operation);
        instance.addTransientModifier(modifier);
        if (attribute == Attribute.GENERIC_MAX_HEALTH && player.getHealth() > instance.getValue()) {
            player.setHealth(instance.getValue());
        }
    }

    private void removeModifier(Player player, Attribute attribute, UUID id) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) {
            return;
        }
        AttributeModifier modifier = instance.getModifier(id);
        if (modifier != null) {
            instance.removeModifier(modifier);
        }
    }

    private void applyEnduranceReduction(Player player, EntityDamageEvent event) {
        int endurance = manager.getEquippedLevel(player, SigilType.ENDURANCE);
        if (endurance <= 0) {
            return;
        }
        double reduction = Math.min(0.7, endurance * 0.014);
        event.setDamage(event.getDamage() * (1 - reduction));
    }

    private void applyShieldMitigation(Player player, EntityDamageEvent event) {
        int ironWall = getShieldLevel(player, SigilType.IRON_WALL);
        if (ironWall > 0) {
            double reduction = Math.min(0.8, ironWall * 0.04);
            event.setDamage(event.getDamage() * (1 - reduction));
        }
        if (getShieldLevel(player, SigilType.FORTIFICATION) > 0) {
            event.setDamage(event.getDamage() * 0.75);
        }
    }

    private void triggerReuseHeal(Player player, EntityDamageEvent event) {
        int reuse = manager.getEquippedLevel(player, SigilType.REUSE);
        if (reuse <= 0) {
            return;
        }
        double damage = event.getDamage();
        double heal = Math.floor(damage * 0.03 * reuse);
        if (heal <= 0) {
            return;
        }
        event.setDamage(Math.max(0.0, damage - heal));
    }

    private void applyStrifeBonus(Player attacker, EntityDamageByEntityEvent event) {
        int level = getWeaponLevel(attacker, SigilType.STRIFE);
        if (level <= 0) {
            return;
        }
        event.setDamage(event.getDamage() * (1 + level * 0.03));
    }

    private void applyMadness(Player attacker, EntityDamageByEntityEvent event) {
        int level = getWeaponLevel(attacker, SigilType.MADNESS);
        if (level <= 0) {
            return;
        }
        AttributeInstance maxHealth = attacker.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double max = maxHealth == null ? 20.0 : maxHealth.getValue();
        double ratio = attacker.getHealth() / Math.max(1.0, max);
        double baseDamage = event.getDamage();
        if (ratio >= 0.7) {
            event.setDamage(baseDamage * 0.5);
            return;
        }
        double missingRatio = 1.0 - ratio;
        double pressure = baseDamage / Math.max(1.0, attacker.getHealth());
        double combined = missingRatio * level + pressure;
        double cappedBonus = Math.min(2.5, combined); // hard cap to prevent runaway burst
        event.setDamage(baseDamage * (1 + cappedBonus));
    }

    private void applyEndurancePenalty(Player attacker, EntityDamageByEntityEvent event) {
        int level = manager.getEquippedLevel(attacker, SigilType.ENDURANCE);
        if (level <= 0) {
            return;
        }
        double reduction = Math.min(0.9, level * 0.10);
        event.setDamage(event.getDamage() * (1 - reduction));
    }

    private int getShieldLevel(Player player, SigilType type) {
        int level = 0;
        ItemStack main = player.getInventory().getItemInMainHand();
        if (isShield(main)) {
            level = Math.max(level, manager.getLevel(main, type));
        }
        ItemStack off = player.getInventory().getItemInOffHand();
        if (isShield(off)) {
            level = Math.max(level, manager.getLevel(off, type));
        }
        return level;
    }

    private int getWeaponLevel(Player player, SigilType type) {
        ItemStack main = player.getInventory().getItemInMainHand();
        return manager.getLevel(main, type);
    }

    private boolean isShield(ItemStack item) {
        return item != null && item.getType() == Material.SHIELD;
    }
}
