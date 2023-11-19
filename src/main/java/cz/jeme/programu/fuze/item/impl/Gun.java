package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.event.Subscribe;
import cz.jeme.programu.fuze.item.registry.ItemManager;
import cz.jeme.programu.fuze.item.storage.FuzeItemData;
import cz.jeme.programu.fuze.item.storage.ItemData;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a gun in the Fuze plugin.
 */
public class Gun extends FuzeItem {

    /**
     * Returns a gun registered with the provided key.
     *
     * @param key the gun key
     * @return a gun registered with the gun key
     * @throws IllegalArgumentException when no gun with the provided key exists
     */
    public static @NotNull Gun valueOf(final @NotNull String key) {
        return valueOf(key, Gun.class);
    }

    /**
     * Returns whether a gun registered with the provided key exists.
     *
     * @param key the gun key
     * @return true when the gun exists otherwise false
     */
    public static boolean exists(final @NotNull String key) {
        return exists(key, Gun.class);
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
     * The ammo of this gun.
     * <p>Read from the config and then parsed using {@link Ammo#valueOf(String)}.</p>
     */
    protected final @NotNull Ammo ammo;

    /**
     * The shoot cooldown of this gun in milliseconds.
     * <p>Read from the config.</p>
     */
    protected final int shootCooldown;

    /**
     * The damage of this gun.
     * <p>1 damage = half a heart</p>
     * <p>Read from the config.</p>
     */
    protected final double damage;

    /**
     * Initializes a gun.
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
        SHOOT_COOLDOWN.set(item, shootCooldown);
        DAMAGE.set(item, damage);
    }

    /**
     * Returns the gun material.
     *
     * @return always {@link Material#CROSSBOW}
     */
    @Override
    public final @NotNull Material getMaterial() {
        return Material.CROSSBOW;
    }

    /**
     * Returns the gun type.
     *
     * @return always "gun"
     */
    @Override
    public final @NotNull String getType() {
        return "gun";
    }
}
