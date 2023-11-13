package cz.jeme.programu.fuze.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an ammo in the Fuze plugin.
 */
public class Ammo extends FuzeItem {
    /**
     * Initializes an {@link Ammo}.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the ammo {@link ConfigurationSection} in config
     */
    protected Ammo(final @NotNull ConfigurationSection section) {
        super(section);
    }

    @Override
    public final @NotNull Material getMaterial() {
        return Material.IRON_NUGGET;
    }

    @Override
    public final @NotNull String getType() {
        return "ammo";
    }

    /**
     * Returns an {@link Ammo} registered with the provided ammo key.
     *
     * @param key the ammo key
     * @return an {@link Ammo} registered with the ammo key
     * @throws IllegalArgumentException when the key is not a valid ammo key
     */
    public static @NotNull Ammo valueOf(final @NotNull String key) {
        return valueOf(key, Ammo.class);
    }
}