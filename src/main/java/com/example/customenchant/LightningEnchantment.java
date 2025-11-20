package com.example.customenchant;

import java.util.Locale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Lightning enchantment descriptor tailored for modern Paper builds. The
 * display name and description rely on the Adventure text API because the
 * legacy getName()/getItemTarget() pair was removed in 1.20+.
 */
public final class LightningEnchantment extends Enchantment {
    private final Component displayName;
    private final Component description;
    private final String translationKey;

    public LightningEnchantment(NamespacedKey key) {
        super(key);
        this.translationKey = "enchantment." + key.getNamespace() + "." + key.getKey();
        this.displayName = Component.translatable(translationKey).color(NamedTextColor.AQUA);
        this.description = Component.text("Summons lightning and increases sweeping damage", NamedTextColor.GRAY);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean canEnchantItem(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }
        Material type = itemStack.getType();
        String name = type.name().toLowerCase(Locale.ROOT);
        return name.endsWith("_sword") || name.endsWith("_axe") || type == Material.MACE;
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false;
    }

    @Override
    public Component description() {
        return description;
    }

    @Override
    public Component displayName(int level) {
        return displayName.append(Component.text(" " + level).color(NamedTextColor.WHITE));
    }

    @Override
    public String translationKey() {
        return translationKey;
    }

    @Override
    public boolean isDiscoverable() {
        return true;
    }

    @Override
    public boolean isTradeable() {
        return true;
    }
}
