package cz.jeme.programu.fuze.item.loot;

import cz.jeme.programu.fuze.item.Keyable;
import cz.jeme.programu.fuze.util.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a fuze item rarity.
 */
public final class Rarity implements Keyable {
    private static final @NotNull Map<String, Rarity> RARITIES = new HashMap<>();

    /**
     * Registers all the rarities in the provided {@link ConfigurationSection}.
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @param section the {@link ConfigurationSection} to register all rarities in
     * @throws IllegalArgumentException when it encounters an invalid rarity {@link ConfigurationSection}
     */
    public static void registerRarities(final @NotNull ConfigurationSection section) {
        Rarity.RARITIES.clear();
        for (String rarityName : section.getKeys(false)) {
            ConfigurationSection raritySection = section.getConfigurationSection(rarityName);
            if (raritySection == null)
                throw new IllegalArgumentException("Invalid rarity in \"" + rarityName + "\"!");

            Rarity.RARITIES.put(rarityName, new Rarity(raritySection));
        }
    }

    /**
     * Returns a rarity registered with the provided rarity key.
     *
     * @param key the rarity key
     * @return a rarity registered with the rarity key
     * @throws IllegalArgumentException when no rarity with the provided key exists
     */
    public static @NotNull Rarity valueOf(final @NotNull String key) {
        return Optional.ofNullable(Rarity.RARITIES.get(key))
                .orElseThrow(() -> new IllegalArgumentException("Unknown rarity key: \"" + key + "\"!"));
    }

    /**
     * Returns whether a rarity registered with the provided rarity key exists.
     *
     * @param key the rarity key
     * @return true when the rarity exists otherwise false
     */
    public static boolean exists(final @NotNull String key) {
        return Rarity.RARITIES.containsKey(key);
    }

    private final @NotNull String key;
    private final @NotNull Component name;
    private final int chance;

    private Rarity(final @NotNull ConfigurationSection section) {
        key = section.getName();
        if (Rarity.RARITIES.containsKey(key))
            throw new IllegalArgumentException("\"name\" is not unique in rarity \"" + key + "\"!");

        name = Messages.deserialize(Objects.requireNonNull(
                section.getString("name"),
                Messages.missing("name", this)
        ));

        if (!section.contains("chance"))
            throw new NullPointerException(Messages.missing("chance", this));
        chance = section.getInt("chance");
        if (chance <= 0)
            throw new IllegalArgumentException("\"chance\" is not bigger than zero in rarity \"" + key + "\"!");

        Rarity.RARITIES.put(key, this);
    }

    /**
     * Returns the Rarity type.
     *
     * @return always "rarity"
     */
    @Override
    public @NotNull String getType() {
        return "rarity";
    }

    @Override
    public @NotNull String getKey() {
        return key;
    }

    @Override
    public @NotNull Component getName() {
        return name;
    }

    /**
     * Returns the chance of this rarity.
     *
     * @return the chance of this rarity
     */
    public int getChance() {
        return chance;
    }

    /**
     * Turns this rarity into a nice readable string.
     * The {@link Rarity#name} will be used for this purpose.
     *
     * @return the name of this rarity
     */
    @Override
    public String toString() {
        return Messages.strip(Messages.serialize(name));
    }
}