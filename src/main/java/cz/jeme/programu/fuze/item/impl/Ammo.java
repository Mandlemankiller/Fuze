package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.registry.ItemManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ammo in the Fuze plugin.
 */
public class Ammo extends FuzeItem {
    /**
     * Returns whether an Ammo registered with the provided key exists.
     *
     * @param key the Ammo key
     * @return true when the Ammo exists otherwise false
     */
    public static boolean exists(final @NotNull String key) {
        return FuzeItem.exists(key, Ammo.class);
    }

    /**
     * Returns whether an {@link ItemStack} is an Ammo.
     *
     * @param item the ItemStack to check the key data on
     * @return true when the ItemStack is an Ammo otherwise false
     */
    public static boolean exists(final @Nullable ItemStack item) {
        return FuzeItem.exists(item, Ammo.class);
    }

    /**
     * Returns an Ammo registered with the provided Ammo key.
     *
     * @param key the ammo key
     * @return an Ammo registered with the Ammo key
     * @throws IllegalArgumentException when no Ammo with the provided key exists
     */
    public static @NotNull Ammo valueOf(final @NotNull String key) {
        return FuzeItem.valueOf(key, Ammo.class);
    }

    /**
     * Returns an Ammo parsed from an {@link ItemStack}.
     *
     * @param item the ItemStack to read the key from
     * @return an Ammo parsed from the ItemStack
     * @throws IllegalArgumentException when the provided ItemStack is not an ammo or
     *                                  when the key data stored inside the ItemStack is not valid
     */
    public static @NotNull Ammo valueOf(final @NotNull ItemStack item) {
        return FuzeItem.valueOf(item, Ammo.class);
    }


    /**
     * Initializes an Ammo.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the Ammo instance in config
     */
    protected Ammo(final @NotNull ConfigurationSection section) {
        super(section);
    }

    /**
     * Returns the Ammo material.
     *
     * @return always {@link Material#IRON_NUGGET}
     */
    @Override
    public final @NotNull Material getMaterial() {
        return Material.IRON_NUGGET;
    }

    /**
     * Returns the Ammo type.
     *
     * @return always "ammo"
     */
    @Override
    public final @NotNull String getType() {
        return "ammo";
    }
}