package cz.jeme.programu.fuze;

import cz.jeme.programu.fuze.item.Keyable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

/**
 * A utility to help with messages, texts, strings and the {@link net.kyori.adventure} API.
 */
public final class Message {
    private Message() {
        throw new AssertionError();
    }

    /**
     * The Fuze command prefix.
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
     * Serializes a component turning it into a {@link String}.
     *
     * @param component the component to serialize
     * @return the serialized {@link String}
     */
    public static @NotNull String serialize(final @NotNull Component component) {
        return MiniMessage.miniMessage().serialize(component);
    }

    /**
     * Adds {@link Message#PREFIX} to a {@link String} and then deserializes it.
     *
     * @param string the string to prefix and deserialize
     * @return the deserialized {@link Component}
     */
    public static @NotNull Component prefix(final @NotNull String string) {
        return deserialize(PREFIX + string);
    }

    /**
     * Generates a not-found message for a configuration path with a type and name information inside.
     * <p>The message looks like this:</p>
     * {@literal  "{PATH}" not found in {TYPE} "{KEY}"!}
     * <p>For example:</p>
     * {@literal "rarity" not found in gun "ak-47"!}
     *
     * @param path the missing path
     * @return the deserialized {@link Component}
     */
    public static @NotNull String missing(final @NotNull String path, final @NotNull String type, final @NotNull String key) {
        return "\"" + path + "\" not found in " + type + " \"" + key + "\"!";
    }

    /**
     * Generates a not-found message for a configuration path
     * with the information provided by the {@link Keyable} interface ({@link Keyable#getType()}, {@link Keyable#getKey()}).
     * <p>The message looks like this:</p>
     * {@literal  "{PATH}" not found in {TYPE} "{KEY}"!}
     * <p>For example:</p>
     * {@literal "rarity" not found in gun "ak-47"!}
     *
     * @param path the missing path
     * @return the deserialized {@link Component}
     */
    public static @NotNull String missing(final @NotNull String path, final @NotNull Keyable keyable) {
        return missing(path, keyable.getType(), keyable.getKey());
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
     * Removes all tags from a component.
     * The component will be serialized and deserialized in the process.
     *
     * @param component the component to remove tags from
     * @return the stripped component
     */
    public static @NotNull Component strip(final @NotNull Component component) {
        return deserialize(strip(serialize(component)));
    }
}