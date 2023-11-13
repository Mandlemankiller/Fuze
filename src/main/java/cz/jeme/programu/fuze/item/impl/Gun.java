package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.Message;
import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.ItemManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a gun in the Fuze plugin.
 */
public class Gun extends FuzeItem {
    /**
     * The ammo of this gun.
     * <p>Read from the {@link ConfigurationSection} and then parsed using {@link Ammo#valueOf(String)}.</p>
     */
    protected final @NotNull Ammo ammo;

    /**
     * Initializes a {@link Gun}.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the gun {@link ConfigurationSection} in config
     */
    protected Gun(final @NotNull ConfigurationSection section) {
        super(section);

        ammo = Ammo.valueOf(Objects.requireNonNull(
                section.getString("ammo"),
                Message.missing("ammo", this)
        ));
    }

    @Override
    public final @NotNull Material getMaterial() {
        return Material.CROSSBOW;
    }

    @Override
    public final @NotNull String getType() {
        return "gun";
    }

    /**
     * Returns a {@link Gun} registered with the provided gun key.
     *
     * @param key the gun key
     * @return a {@link Gun} registered with the gun key
     * @throws IllegalArgumentException when the key is not a valid gun key
     */
    public static @NotNull Gun valueOf(final @NotNull String key) {
        return valueOf(key, Gun.class);
    }
}
