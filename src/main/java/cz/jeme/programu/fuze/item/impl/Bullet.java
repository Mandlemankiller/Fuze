package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.item.storage.FuzeItemData;
import cz.jeme.programu.fuze.item.storage.ItemData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class Bullet {
    public static final @NotNull ItemStack BULLET = new ItemStack(Material.ARROW);
    public static final @NotNull ItemData<String, String> GUN_KEY = new FuzeItemData<>("bullet_gun_key", ItemData.STRING);
    public static final @NotNull ItemData<Double, Double> GUN_DAMAGE = new FuzeItemData<>("bullet_gun_damage", ItemData.DOUBLE);

    private Bullet() {
        throw new AssertionError();
    }
}
