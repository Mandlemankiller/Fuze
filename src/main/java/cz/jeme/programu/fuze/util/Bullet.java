package cz.jeme.programu.fuze.util;

import cz.jeme.programu.fuze.item.impl.Gun;
import cz.jeme.programu.fuze.item.storage.FuzePersistentData;
import cz.jeme.programu.fuze.item.storage.PersistentData;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A helper class with stuff related to bullets.
 */
public final class Bullet {
    /**
     * The arrow used when filling the crossbow when initializing a {@link Gun}.
     */
    public static final @NotNull ItemStack CROSSBOW_ARROW = new ItemStack(Material.ARROW);
    /**
     * Key of the source gun data storage.
     */
    public static final @NotNull PersistentData<String, String> GUN_KEY = new FuzePersistentData<>("bullet_gun_key", PersistentData.STRING);
    /**
     * Damage of the source gun data storage.
     */
    public static final @NotNull PersistentData<Double, Double> GUN_DAMAGE = new FuzePersistentData<>("bullet_gun_damage", PersistentData.DOUBLE);

    private Bullet() {
        throw new AssertionError();
    }
}
