package cz.jeme.programu.fuze.item;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an object that has a type, name and a unique key.
 * This will usually be objects prepared to be read from the config.
 */
public interface Keyable {
    /**
     * Returns the type of this object, this should represent some kind of group this item is in.
     * <br><p>Correct values are e.g. "gun", "ammo", "rarity"</p>
     * <br><p>Incorrect values are e.g. "item" (too generic), "smoke_grenade" (too specific, should be probably just "grenade")</p>
     *
     * @return the type
     */
    @NotNull String getType();

    /**
     * Returns the key of this object, this should be 100% unique,
     * it will be treated as a value, that can be used for registering.
     *
     * @return the key
     */
    @NotNull String getKey();

    /**
     * Returns the display name of this object.
     * This does not have to be unique.
     *
     * @return the display name
     */
    @NotNull Component getName();
}