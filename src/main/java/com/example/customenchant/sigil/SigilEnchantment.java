package com.example.customenchant.sigil;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class SigilEnchantment extends Enchantment {
    private final SigilType type;

    public SigilEnchantment(NamespacedKey key, SigilType type) {
        super(key);
        this.type = type;
    }

    public SigilType getType() {
        return type;
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item != null && type.canApply(item.getType());
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return type.getTarget();
    }

    @Override
    public int getMaxLevel() {
        return type.getMaxLevel();
    }

    @Override
    public String getName() {
        return type.getDisplayName();
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public Component displayName(int level) {
        return Component.text(type.getDisplayName() + " " + level);
    }
}
