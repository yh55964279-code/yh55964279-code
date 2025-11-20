package com.example.customenchant.sigil;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.bukkit.Material;

public enum SigilType {
    GROWTH(
            "growth",
            "생장",
            "갑옷 장착 시 레벨당 최대 체력이 늘어납니다.",
            5,
            armorMaterials()),
    IMPULSE(
            "impulse",
            "충동",
            "이속과 공속을 높이는 대신 최대 체력이 소폭 감소합니다.",
            5,
            armorMaterials()),
    ENDURANCE(
            "endurance",
            "인내",
            "받는 피해를 줄이는 대신 공격력이 감소합니다.",
            5,
            armorMaterials()),
    REUSE(
            "reuse",
            "재사용",
            "피해를 입은 직후 일정 비율만큼 체력을 즉시 회복합니다.",
            5,
            armorMaterials()),
    IRON_WALL(
            "iron_wall",
            "철벽",
            "방패를 쥐고 있을 때 레벨당 4% 피해를 더 막아냅니다.",
            5,
            shields()),
    FORTIFICATION(
            "fortification",
            "요새화",
            "방패를 드는 동안 이동 속도가 절반이 되지만 25% 추가 피해 감소를 얻습니다.",
            1,
            shields()),
    STRIFE(
            "strife",
            "투쟁심",
            "무기를 든 상태에서 가하는 피해가 레벨당 3% 증가합니다.",
            5,
            weaponMaterials()),
    MADNESS(
            "madness",
            "광기",
            "체력이 낮을수록 공격력이 치솟지만, 체력이 높으면 오히려 약해집니다.",
            1,
            weaponMaterials());

    private final String id;
    private final String displayName;
    private final String description;
    private final int maxLevel;
    private final Set<Material> materials;

    SigilType(String id, String displayName, String description, int maxLevel, Set<Material> materials) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.maxLevel = maxLevel;
        this.materials = materials;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public boolean canApply(Material material) {
        return materials == null || materials.contains(material);
    }

    private static Set<Material> weaponMaterials() {
        EnumSet<Material> set = EnumSet.of(
                Material.WOODEN_SWORD,
                Material.STONE_SWORD,
                Material.IRON_SWORD,
                Material.GOLDEN_SWORD,
                Material.DIAMOND_SWORD,
                Material.NETHERITE_SWORD,
                Material.WOODEN_AXE,
                Material.STONE_AXE,
                Material.IRON_AXE,
                Material.GOLDEN_AXE,
                Material.DIAMOND_AXE,
                Material.NETHERITE_AXE,
                Material.TRIDENT,
                Material.MACE);
        return Collections.unmodifiableSet(set);
    }

    private static Set<Material> armorMaterials() {
        EnumSet<Material> set = EnumSet.noneOf(Material.class);
        List<Material> helmets = Arrays.asList(
                Material.LEATHER_HELMET,
                Material.CHAINMAIL_HELMET,
                Material.IRON_HELMET,
                Material.GOLDEN_HELMET,
                Material.DIAMOND_HELMET,
                Material.NETHERITE_HELMET,
                Material.TURTLE_HELMET);
        List<Material> chestplates = Arrays.asList(
                Material.LEATHER_CHESTPLATE,
                Material.CHAINMAIL_CHESTPLATE,
                Material.IRON_CHESTPLATE,
                Material.GOLDEN_CHESTPLATE,
                Material.DIAMOND_CHESTPLATE,
                Material.NETHERITE_CHESTPLATE,
                Material.ELYTRA);
        List<Material> leggings = Arrays.asList(
                Material.LEATHER_LEGGINGS,
                Material.CHAINMAIL_LEGGINGS,
                Material.IRON_LEGGINGS,
                Material.GOLDEN_LEGGINGS,
                Material.DIAMOND_LEGGINGS,
                Material.NETHERITE_LEGGINGS);
        List<Material> boots = Arrays.asList(
                Material.LEATHER_BOOTS,
                Material.CHAINMAIL_BOOTS,
                Material.IRON_BOOTS,
                Material.GOLDEN_BOOTS,
                Material.DIAMOND_BOOTS,
                Material.NETHERITE_BOOTS);
        set.addAll(helmets);
        set.addAll(chestplates);
        set.addAll(leggings);
        set.addAll(boots);
        return Collections.unmodifiableSet(set);
    }

    private static Set<Material> shields() {
        return Collections.unmodifiableSet(EnumSet.of(Material.SHIELD));
    }
}
