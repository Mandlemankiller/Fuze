package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.registry.ItemManager;
import cz.jeme.programu.fuze.item.storage.FuzeItemData;
import cz.jeme.programu.fuze.item.storage.ItemData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a gun in the Fuze plugin.
 */
public class Gun extends FuzeItem {

    /**
     * Returns a Gun registered with the provided key.
     *
     * @param key the Gun key
     * @return a Gun registered with the Gun key
     * @throws IllegalArgumentException when no Gun with the provided key exists
     */
    public static @NotNull Gun valueOf(final @NotNull String key) {
        return FuzeItem.valueOf(key, Gun.class);
    }

    /**
     * Returns a Gun parsed from an {@link ItemStack}.
     *
     * @param item the ItemStack to read the key from
     * @return a Gun parsed from the ItemStack
     * @throws IllegalArgumentException when the provided ItemStack is not a Gun or
     *                                  when the key data stored inside the ItemStack is not valid
     */
    public static @NotNull Gun valueOf(final @NotNull ItemStack item) {
        return FuzeItem.valueOf(item, Gun.class);
    }

    /**
     * Returns whether a Gun registered with the provided key exists.
     *
     * @param key the Gun key
     * @return true when the Gun exists otherwise false
     */
    public static boolean exists(final @Nullable String key) {
        return FuzeItem.exists(key, Gun.class);
    }

    /**
     * Returns whether an {@link ItemStack} is a Gun.
     *
     * @param item the ItemStack to check the key data on
     * @return true when the ItemStack is a Gun otherwise false
     */
    public static boolean exists(final @Nullable ItemStack item) {
        return FuzeItem.exists(item, Gun.class);
    }

    /**
     * Gun shoot cooldown item data storage.
     */
    public static final @NotNull ItemData<Integer, Integer> SHOOT_COOLDOWN = new FuzeItemData<>("gun_shoot_cooldown", ItemData.INTEGER);

    /**
     * Gun damage item data storage.
     */
    public static final @NotNull ItemData<Double, Double> DAMAGE = new FuzeItemData<>("gun_damage", ItemData.DOUBLE);

    /**
     * The ammo of this Gun.
     * <p>Read from the config and then parsed using {@link Ammo#valueOf(String)}.</p>
     */
    protected final @NotNull Ammo ammo;

    /**
     * The shoot cooldown of this Gun in milliseconds.
     * <p>Read from the config.</p>
     */
    protected final int shootCooldown;

    /**
     * The damage of this Gun.
     * <p>1 damage = half a heart</p>
     * <p>Read from the config.</p>
     */
    protected final double damage;

    /**
     * Initializes a Gun.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the gun instance in config
     * @throws IllegalArgumentException when the section name (gun key) doesn't match [a-z0-9_.-],
     *                                  when no rarity with the rarity key exists
     *                                  and when no ammo with the ammo key exists
     * @throws NullPointerException     when name, rarity, ammo, shoot cooldown or damage is not set in config
     */
    protected Gun(final @NotNull ConfigurationSection section) {
        super(section);

        // Load gun data from config
        ammo = Ammo.valueOf(requireConfigString("ammo"));
        shootCooldown = requireConfigInt("shoot-cooldown");
        damage = requireConfigDouble("damage");

        // Save gun data to the item
        Gun.SHOOT_COOLDOWN.write(item, shootCooldown);
        Gun.DAMAGE.write(item, damage);
    }

    /**
     * Returns the Gun material.
     *
     * @return always {@link Material#CROSSBOW}
     */
    @Override
    public final @NotNull Material getMaterial() {
        return Material.CROSSBOW;
    }

    /**
     * Returns the Gun type.
     *
     * @return always "gun"
     */
    @Override
    public @NotNull String getType() {
        return "gun";
    }
}
