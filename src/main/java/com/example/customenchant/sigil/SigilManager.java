package com.example.customenchant.sigil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class SigilManager {
    private final Map<SigilType, SigilEnchantment> enchantments = new EnumMap<>(SigilType.class);

    public SigilManager(Plugin plugin, Consumer<Enchantment> registrar) {
        for (SigilType type : SigilType.values()) {
            SigilEnchantment enchantment = new SigilEnchantment(new NamespacedKey(plugin, type.getId()), type);
            registrar.accept(enchantment);
            enchantments.put(type, enchantment);
        }
    }

    public Collection<SigilEnchantment> getEnchantments() {
        return new ArrayList<>(enchantments.values());
    }

    public SigilEnchantment getEnchantment(SigilType type) {
        return enchantments.get(type);
    }

    public SigilType findType(String id) {
        for (SigilType type : SigilType.values()) {
            if (type.getId().equalsIgnoreCase(id)) {
                return type;
            }
        }
        return null;
    }

    public List<String> getTypeIds() {
        List<String> ids = new ArrayList<>();
        for (SigilType type : SigilType.values()) {
            ids.add(type.getId());
        }
        return ids;
    }

    public int getLevel(ItemStack item, SigilType type) {
        if (item == null || !item.hasItemMeta()) {
            return 0;
        }
        SigilEnchantment enchantment = enchantments.get(type);
        if (enchantment == null) {
            return 0;
        }
        return item.getEnchantmentLevel(enchantment);
    }

    public int getEquippedLevel(Player player, SigilType type) {
        int level = 0;
        level += getLevel(player.getInventory().getItemInMainHand(), type);
        level += getLevel(player.getInventory().getItemInOffHand(), type);
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            level += getLevel(armor, type);
        }
        return level;
    }
}
