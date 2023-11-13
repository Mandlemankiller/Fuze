package cz.jeme.programu.fuze.item;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents ammo in the Fuze plugin.
 */
public class Ammo extends FuzeItem {
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
     * Returns a {@link Ammo} registered with the provided key.
     *
     * @param key the key of the ammo
     * @return the {@link Ammo} registered with the key
     * @throws IllegalArgumentException when the key is not a valid ammo key
     */
    public static @NotNull Ammo valueOf(final @NotNull String key) {
        return valueOf(key, Ammo.class);
    }
}