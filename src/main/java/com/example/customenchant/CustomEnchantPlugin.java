package com.example.customenchant;

import java.lang.reflect.Field;
import java.util.Objects;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.example.customenchant.sigil.SigilCommand;
import com.example.customenchant.sigil.SigilEffectListener;
import com.example.customenchant.sigil.SigilManager;

public class CustomEnchantPlugin extends JavaPlugin {
    private LightningEnchantment lightningEnchantment;
    private SigilManager sigilManager;

    @Override
    public void onEnable() {
        lightningEnchantment = new LightningEnchantment(new NamespacedKey(this, "lightning"));
        register(lightningEnchantment);
        sigilManager = new SigilManager(this, this::register);
        getServer().getPluginManager().registerEvents(new LightningStrikeListener(lightningEnchantment), this);
        getServer().getPluginManager().registerEvents(new SwordAuraListener(lightningEnchantment), this);
        getServer().getPluginManager().registerEvents(new SigilEffectListener(this, sigilManager), this);
        Objects.requireNonNull(getCommand("lightningenchant"), "lightningenchant command not registered")
                .setExecutor(new LightningCommand(lightningEnchantment));
        SigilCommand sigilCommand = new SigilCommand(sigilManager);
        var sigilPluginCommand = Objects.requireNonNull(getCommand("sigil"), "sigil command not registered");
        sigilPluginCommand.setExecutor(sigilCommand);
        sigilPluginCommand.setTabCompleter(sigilCommand);
        getLogger().info("Registered Lightning enchantment");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    private void register(Enchantment enchantment) {
        if (Enchantment.getByKey(enchantment.getKey()) != null) {
            return;
        }

        try {
            Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
        } catch (ReflectiveOperationException exception) {
            getLogger().warning("Unable to enable new enchantment registration: " + exception.getMessage());
        }

        try {
            Enchantment.registerEnchantment(enchantment);
        } catch (IllegalArgumentException exception) {
            getLogger().warning("Enchantment already registered: " + enchantment.getKey());
        }
    }
}
