package com.example.customenchant;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class LightningStrikeListener implements Listener {
    private final Enchantment enchantment;

    public LightningStrikeListener(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || !item.hasItemMeta()) {
            return;
        }

        int level = item.getEnchantmentLevel(enchantment);
        if (level <= 0) {
            return;
        }

        player.getWorld().strikeLightning(event.getEntity().getLocation());
        event.setDamage(event.getDamage() + level);
    }
}
