package cz.jeme.programu.fuze.util;

import cz.jeme.programu.fuze.item.Keyable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * A utility to help with messages, texts, strings and the {@link net.kyori.adventure} API.
 */
public final class Messages {
    private Messages() {
        throw new AssertionError(); // Utility
    }

    /**
     * The fuze command prefix.
     */
    public static final @NotNull String PREFIX = "<dark_gray>[<gradient:#0066B3:#004778>ꜰᴜᴢᴇ</gradient>]: </dark_gray>";

    /**
     * Deserializes a string turning it into a {@link Component}.
     *
     * @param string the string to deserialize
     * @return the deserialized {@link Component}
     */
    public static @NotNull Component deserialize(final @NotNull String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

    /**
     * Serializes a {@link Component} turning it into a string.
     *
     * @param component the {@link Component} to serialize
     * @return the serialized string
     */
    public static @NotNull String serialize(final @NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Adds {@link Messages#PREFIX} to a string and then deserializes it.
     *
     * @param string the string to prefix and deserialize
     * @return the prefixed and deserialized {@link Component}
     */
    public static @NotNull Component prefix(final @NotNull String string) {
        return Messages.deserialize(Messages.PREFIX + string);
    }

    /**
     * Generates a not-found message for a configuration path with a type and name information inside.
     * <br><p>The message looks like this:</p>
     * {@literal "{PATH}" not defined in {TYPE} configuration: {KEY}}
     * <br><p>For example:</p>
     * {@literal "rarity" not defined in gun configuration: ak-47}
     *
     * @param path the missing path
     * @param type the keyable type
     * @param key  the keyable key
     * @return the not-found message for the missing path
     */
    public static @NotNull String missing(final @NotNull String path, final @NotNull String type, final @NotNull String key) {
        return "\"%s\" not defined in %s configuration: %s"
                .formatted(path, type, key);
    }

    /**
     * Generates a not-found message for a configuration path
     * with the information provided by the {@link Keyable} interface ({@link Keyable#getType()}, {@link Keyable#getKey()}).
     * <br><p>The message looks like this:</p>
     * {@literal "{PATH}" not defined in {TYPE} configuration: {KEY}}
     * <br><p>For example:</p>
     * {@literal "rarity" not defined in gun configuration: ak-47}
     *
     * @param path    the missing path
     * @param keyable the keyable to read type and key information from
     * @return the not-found message for the missing path
     */
    public static @NotNull String missing(final @NotNull String path, final @NotNull Keyable keyable) {
        return Messages.missing(path, keyable.getType(), keyable.getKey());
    }

    /**
     * Removes all tags from a string.
     *
     * @param string the string to remove tags from
     * @return the stripped string
     */
    public static @NotNull String strip(final @NotNull String string) {
        return MiniMessage.miniMessage().stripTags(string);
    }

    /**
     * Serializes a component and removes all tags.
     *
     * @param component the component to remove tags from
     * @return the serialized and stripped component
     */
    public static @NotNull String strip(final @NotNull Component component) {
        return Messages.strip(Messages.serialize(component));
    }
}
