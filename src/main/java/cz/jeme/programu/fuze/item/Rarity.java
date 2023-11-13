package cz.jeme.programu.fuze.item;

import cz.jeme.programu.fuze.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a {@link FuzeItem}'s rarity.
 */
public final class Rarity implements Keyable {
    private static final @NotNull Map<String, Rarity> RARITIES = new HashMap<>();
    private final @NotNull String key;
    private final @NotNull Component name;
    private final int chance;

    private Rarity(final @NotNull ConfigurationSection section) {
        key = section.getName();
        if (RARITIES.containsKey(key))
            throw new IllegalArgumentException("\"name\" is not unique in rarity \"" + key + "\"!");

        name = Message.deserialize(Objects.requireNonNull(
                section.getString("name"),
                Message.missing("name", this)
        ));

        if (!section.contains("chance"))
            throw new NullPointerException(Message.missing("chance", this));
        chance = section.getInt("chance");
        if (chance <= 0)
            throw new IllegalArgumentException("\"chance\" is not bigger than zero in rarity \"" + key + "\"!");

        RARITIES.put(key, this);
    }

    /**
     * Registers all the rarities in the provided {@link ConfigurationSection}.
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @param section the {@link ConfigurationSection} to register all rarities in
     * @throws IllegalArgumentException when it encounters an invalid rarity {@link ConfigurationSection}
     */
    public static void registerRarities(final @NotNull ConfigurationSection section) {
        RARITIES.clear();
        for (String rarityName : section.getKeys(false)) {
            ConfigurationSection raritySection = section.getConfigurationSection(rarityName);
            if (raritySection == null)
                throw new IllegalArgumentException("Invalid rarity in \"" + rarityName + "\"!");

            RARITIES.put(rarityName, new Rarity(raritySection));
        }
    }


    /**
     * Returns a {@link Rarity} registered with the provided rarity key.
     *
     * @param key the rarity key
     * @return a {@link Rarity} registered with the rarity key
     * @throws IllegalArgumentException when the key is not a valid rarity key
     */
    public static @NotNull Rarity valueOf(final @NotNull String key) {
        return Optional.ofNullable(RARITIES.get(key)).orElseThrow(
                () -> new IllegalArgumentException("Unknown rarity key: \"" + key + "\"!")
        );
    }

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
     * TODO
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
        return Message.strip(Message.serialize(name));
    }
}