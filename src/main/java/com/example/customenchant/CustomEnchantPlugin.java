package com.example.customenchant;

import java.util.Objects;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import com.example.customenchant.sigil.SigilCommand;
import com.example.customenchant.sigil.SigilEffectListener;
import com.example.customenchant.sigil.SigilManager;
import io.papermc.paper.registry.Registry;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;

public class CustomEnchantPlugin extends JavaPlugin {
    private LightningEnchantment lightningEnchantment;
    private SigilManager sigilManager;
    private Registry<Enchantment> enchantmentRegistry;

    @Override
    public void onLoad() {
        NamespacedKey lightningKey = new NamespacedKey(this, "lightning");
        lightningEnchantment = new LightningEnchantment(lightningKey);
        enchantmentRegistry = resolveEnchantmentRegistry();
        registerViaPaperRegistry(lightningEnchantment);
    }

    @Override
    public void onEnable() {
        if (Enchantment.getByKey(lightningEnchantment.getKey()) == null) {
            getLogger().severe("Lightning enchantment was not registered. Check server console for registry errors.");
            return;
        }
        sigilManager = new SigilManager(this, this::registerViaPaperRegistry);
        Bukkit.getPluginManager().registerEvents(new LightningStrikeListener(lightningEnchantment), this);
        Bukkit.getPluginManager().registerEvents(new SwordAuraListener(lightningEnchantment), this);
        Bukkit.getPluginManager().registerEvents(new SigilEffectListener(this, sigilManager), this);
        Objects.requireNonNull(getCommand("lightningenchant"), "lightningenchant command not registered")
                .setExecutor(new LightningCommand(lightningEnchantment));
        SigilCommand sigilCommand = new SigilCommand(sigilManager);
        var sigilPluginCommand = Objects.requireNonNull(getCommand("sigil"), "sigil command not registered");
        sigilPluginCommand.setExecutor(sigilCommand);
        sigilPluginCommand.setTabCompleter(sigilCommand);
        getLogger().info("Lightning enchantment ready for Paper 1.21.4+");
    }

    private Registry<Enchantment> resolveEnchantmentRegistry() {
        try {
            return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT);
        } catch (Throwable throwable) {
            getLogger().log(Level.SEVERE, "Unable to access Paper enchantment registry (requires Paper 1.20.5+)",
                    throwable);
            return null;
        }
    }

    private boolean registerViaPaperRegistry(Enchantment enchantment) {
        if (enchantment == null) {
            return false;
        }
        if (Enchantment.getByKey(enchantment.getKey()) != null) {
            return true;
        }
        if (enchantmentRegistry == null) {
            getLogger().severe("Enchantment registry unavailable; ensure this server runs Paper 1.20.5+.");
            return false;
        }
        try {
            enchantmentRegistry.register(enchantment.getKey(), enchantment);
            return true;
        } catch (IllegalStateException exception) {
            getLogger().log(Level.SEVERE, "Failed to register enchantment " + enchantment.getKey(), exception);
            return false;
        }
    }
}
