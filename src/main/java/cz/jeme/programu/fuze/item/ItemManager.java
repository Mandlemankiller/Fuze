package cz.jeme.programu.fuze.item;

import cz.jeme.programu.fuze.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Manages all item registration in the Fuze plugin.
 */
public enum ItemManager {
    /**
     * The one and only {@link ItemManager}.
     */
    INSTANCE;

    private final @NotNull Map<String, Set<FuzeItem>> typedItems = new HashMap<>();
    private final @NotNull Map<String, FuzeItem> keyedItems = new HashMap<>();

    /**
     * Clears all item registrations.
     * <p>After calling this method, you should always register all items again.</p>
     */
    public void reset() {
        typedItems.clear();
    }

    /**
     * Registers an item.
     *
     * @param itemClass   the item class
     * @param sectionName the name of the {@link ConfigurationSection}, that contains the configuration for all instances of this item
     * @param <T>         the item
     */
    public <T extends FuzeItem> void registerItem(final @NotNull Class<T> itemClass, final @NotNull String sectionName) {
        ConfigurationSection section = Objects.requireNonNull(
                Config.instance().getRegistry().getConfigurationSection(sectionName),
                "\"" + sectionName + "\" not found in registry!"
        );
        final Constructor<T> constructor;
        try {
            constructor = itemClass.getDeclaredConstructor(ConfigurationSection.class);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(
                    "No constructor matching %s was found in item: %s"
                            .formatted(ConfigurationSection.class.getName(), itemClass.getName()),
                    e
            );
        }
        constructor.setAccessible(true);
        for (String itemKey : section.getKeys(false)) {
            final T item;
            try {
                item = constructor.newInstance(section.getConfigurationSection(itemKey));
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                throw new IllegalStateException(
                        "Unable to register (initialize) item: " + itemClass.getName(),
                        e
                );
            }
            typedItems.computeIfAbsent(item.getType(), type -> new HashSet<>());
            typedItems.get(item.getType()).add(item);
            keyedItems.put(item.getKey(), item);
        }
    }

    /**
     * Returns an item registered with the provided key.
     * <p><b>You should probably use {@link FuzeItem#valueOf(String)} or the item's own valueOf method instead!</b></p>
     *
     * @param key the item key
     * @return an optional item registered with the key
     */
    public @NotNull Optional<FuzeItem> getItemByKey(final @NotNull String key) {
        return Optional.ofNullable(keyedItems.get(key));
    }

    /**
     * Returns whether an item registered with the provided key exists.
     * <p><b>You should probably use {@link FuzeItem#exists(String)} or the item's own exists method instead!</b></p>
     *
     * @param key the item key
     * @return true when the item exists otherwise false
     */
    public boolean existsItemByKey(final @NotNull String key) {
        return keyedItems.containsKey(key);
    }

    /**
     * Returns all items registered with the provided item type.
     * When no items are found, an empty immutable set is returned.
     *
     * @param type the item type
     * @return a set of items registered with the item type
     */
    public @NotNull Set<FuzeItem> getItemsByType(final @NotNull String type) {
        return typedItems.containsKey(type) ? new HashSet<>(typedItems.get(type)) : Set.of();
    }


    /**
     * Returns all item types registered.
     *
     * @return a set of all item types registered
     */
    public @NotNull Set<String> getItemTypes() {
        return new HashSet<>(typedItems.keySet());
    }

    /**
     * Returns all items registered.
     *
     * @return a set of all items registered
     */
    public @NotNull Set<FuzeItem> getItems() {
        return new HashSet<>(keyedItems.values());
    }

    /**
     * Returns all registered item keys.
     *
     * @return a set of all item keys registered
     */
    public @NotNull Set<String> getItemKeys() {
        return new HashSet<>(keyedItems.keySet());
    }
}
