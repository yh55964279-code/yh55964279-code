package com.example.customenchant.sigil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public final class SigilEnchantment extends Enchantment {
    private final SigilType type;
    private final Component displayName;
    private final Component description;
    private final String translationKey;

    public SigilEnchantment(NamespacedKey key, SigilType type) {
        super(key);
        this.type = type;
        this.translationKey = "enchantment." + key.getNamespace() + ".sigil." + key.getKey();
        this.displayName = Component.text(type.getDisplayName(), NamedTextColor.GOLD);
        this.description = Component.text(type.getDescription(), NamedTextColor.GRAY);
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
    public int getMaxLevel() {
        return type.getMaxLevel();
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
        return displayName.append(Component.text(" " + level).color(NamedTextColor.WHITE));
    }

    @Override
    public Component description() {
        return description;
    }

    @Override
    public String translationKey() {
        return translationKey;
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }
}
