package com.example.customenchant;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightningCommand implements CommandExecutor {
    private final Enchantment enchantment;

    public LightningCommand(Enchantment enchantment) {
        this.enchantment = enchantment;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage("Hold the sword you want to enchant.");
            return true;
        }

        int maxLevel = enchantment.getMaxLevel();
        int requestedLevel = parseLevel(args, maxLevel);
        if (requestedLevel <= 0) {
            player.sendMessage("Level must be between 1 and " + maxLevel + ".");
            return true;
        }

        heldItem.addUnsafeEnchantment(enchantment, requestedLevel);
        player.sendMessage("Applied Lightning level " + requestedLevel + " to your weapon.");
        return true;
    }

    private int parseLevel(String[] args, int maxLevel) {
        if (args.length == 0) {
            return 1;
        }

        try {
            int parsed = Integer.parseInt(args[0]);
            return Math.min(Math.max(parsed, 1), maxLevel);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
