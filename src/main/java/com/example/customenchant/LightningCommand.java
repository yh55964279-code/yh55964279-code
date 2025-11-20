package com.example.customenchant;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightningCommand implements CommandExecutor {
    private final LightningEnchantment enchantment;

    public LightningCommand(LightningEnchantment enchantment) {
        this.enchantment = enchantment;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("플레이어만 사용할 수 있는 명령어입니다.", NamedTextColor.RED));
            return true;
        }

        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem == null || heldItem.getType() == Material.AIR) {
            player.sendMessage(Component.text("검이나 도끼를 손에 들어 주세요.", NamedTextColor.RED));
            return true;
        }

        int maxLevel = enchantment.getMaxLevel();
        int requestedLevel = parseLevel(args, maxLevel);
        if (requestedLevel <= 0) {
            player.sendMessage(Component.text("레벨은 1 이상 " + maxLevel + " 이하만 가능합니다.", NamedTextColor.RED));
            return true;
        }

        heldItem.addUnsafeEnchantment(enchantment, requestedLevel);
        player.sendMessage(Component.text("라이트닝 각인을 적용했습니다 (", NamedTextColor.AQUA)
                .append(Component.text(requestedLevel, NamedTextColor.YELLOW))
                .append(Component.text(" 레벨)", NamedTextColor.AQUA)));
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
