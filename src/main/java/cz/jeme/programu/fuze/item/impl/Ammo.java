package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.registry.ItemManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an ammo in the Fuze plugin.
 */
public class Ammo extends FuzeItem {
    /**
     * Returns an ammo registered with the provided ammo key.
     *
     * @param key the ammo key
     * @return an ammo registered with the ammo key
     * @throws IllegalArgumentException when no ammo with the provided key exists
     */
    public static @NotNull Ammo valueOf(final @NotNull String key) {
        return valueOf(key, Ammo.class);
    }

    /**
     * Returns whether an ammo registered with the provided key exists.
     *
     * @param key the ammo key
     * @return true when the ammo exists otherwise false
     */
    public static boolean exists(final @NotNull String key) {
        return exists(key, Ammo.class);
    }


    /**
     * Initializes an ammo.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the ammo instance in config
     */
    protected Ammo(final @NotNull ConfigurationSection section) {
        super(section);
    }

    /**
     * Returns the ammo material.
     *
     * @return always {@link Material#IRON_NUGGET}
     */
    @Override
    public final @NotNull Material getMaterial() {
        return Material.IRON_NUGGET;
    }

    /**
     * Returns the ammo type.
     *
     * @return always "ammo"
     */
    @Override
    public final @NotNull String getType() {
        return "ammo";
    }
}