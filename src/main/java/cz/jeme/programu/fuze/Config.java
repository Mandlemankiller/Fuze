package cz.jeme.programu.fuze;

import cz.jeme.programu.fuze.item.impl.Ammo;
import cz.jeme.programu.fuze.item.impl.Gun;
import cz.jeme.programu.fuze.item.ItemManager;
import cz.jeme.programu.fuze.item.Rarity;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the configuration of the Fuze plugin.
 */
public final class Config {
    private static @Nullable Config instance;

    /**
     * Initializes Config. If Config was already initialized, it will fail silently.
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @param plugin the {@link Fuze} plugin instance
     * @return true when Config was successfully initialized, otherwise false
     */
    public static synchronized boolean init(final @NotNull Fuze plugin) {
        if (instance == null) {
            instance = new Config(plugin);
            return true;
        }
        return false;
    }

    /**
     * Returns the instance of Config.
     * <p>Config must be initialized before calling this method.</p>
     *
     * @return Config instance
     * @throws IllegalStateException when Config was not initialized before calling this method
     */
    public static synchronized @NotNull Config instance() {
        if (instance == null)
            throw new IllegalStateException("Config was not yet initialized!");
        return instance;
    }


    private final @NotNull Fuze plugin;
    private @NotNull FileConfiguration yaml;

    private Config(final @NotNull Fuze plugin) {
        this.plugin = plugin;
    }

    /**
     * Reloads the entire plugin. Also called initially when the plugin is enabled.
     */
    public void reload() {
        plugin.reloadConfig(); // Reload config from disk
        yaml = plugin.getConfig(); // Save config contents

        // Register rarities
        Rarity.registerRarities(Objects.requireNonNull(
                getRegistry().getConfigurationSection("rarities"),
                "\"rarities\" not found in registry!"
        ));

        // Register all item types
        ItemManager.INSTANCE.reset();
        // Ammo must be registered first! Guns require the ammo to be registered, see Gun#getAmmo()
        ItemManager.INSTANCE.registerItem(Ammo.class, "ammo");
        ItemManager.INSTANCE.registerItem(Gun.class, "guns");
    }

    /**
     * Returns the registry {@link ConfigurationSection}.
     * <p>All registration data is stored in this section.</p>
     *
     * @return the registry {@link ConfigurationSection}
     */
    public @NotNull ConfigurationSection getRegistry() {
        return Objects.requireNonNull(
                yaml.getConfigurationSection("registry"),
                "\"registry\" not found in config!"
        );
    }

    /**
     * Saves the plugin configuration to disk.
     */
    public void save() {
        plugin.saveConfig();
    }

    /**
     * Returns the plugin configuration currently saved in memory.
     *
     * @return the plugin configuration
     */
    public @NotNull FileConfiguration getYaml() {
        return yaml;
    }
}
