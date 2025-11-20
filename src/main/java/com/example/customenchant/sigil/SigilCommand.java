package com.example.customenchant.sigil;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SigilCommand implements CommandExecutor, TabCompleter {
    private final SigilManager manager;

    public SigilCommand(SigilManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("list")) {
            sendList(sender);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("플레이어만 사용할 수 있는 명령어입니다.");
            return true;
        }

        String id = args[0].toLowerCase(Locale.ROOT);
        SigilType type = manager.findType(id);
        if (type == null) {
            sender.sendMessage("알 수 없는 각인입니다. /" + label + " list 로 목록을 확인하세요.");
            return true;
        }

        ItemStack held = player.getInventory().getItemInMainHand();
        if (held.getType() == Material.AIR) {
            player.sendMessage("인첸트를 적용할 아이템을 손에 들어주세요.");
            return true;
        }
        if (!type.canApply(held.getType())) {
            player.sendMessage(type.getDisplayName() + " 각인은 이 아이템에 적용할 수 없습니다.");
            return true;
        }

        int level = parseLevel(args, type.getMaxLevel());
        if (level <= 0) {
            player.sendMessage("레벨은 1 이상 " + type.getMaxLevel() + " 이하로 입력하세요.");
            return true;
        }

        Enchantment enchantment = manager.getEnchantment(type);
        held.addUnsafeEnchantment(enchantment, level);
        player.sendMessage(Component.text(type.getDisplayName() + " " + level + " 각인을 부여했습니다."));
        return true;
    }

    private int parseLevel(String[] args, int maxLevel) {
        if (args.length < 2) {
            return 1;
        }
        try {
            int parsed = Integer.parseInt(args[1]);
            return Math.max(1, Math.min(maxLevel, parsed));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void sendList(CommandSender sender) {
        sender.sendMessage("==== 각인 목록 ====");
        for (SigilType type : SigilType.values()) {
            sender.sendMessage("- " + type.getId() + " (" + type.getDisplayName() + "): " + type.getDescription());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return manager.getTypeIds().stream()
                    .filter(id -> id.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
