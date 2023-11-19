package cz.jeme.programu.fuze.item.registry;

import cz.jeme.programu.fuze.Config;
import cz.jeme.programu.fuze.item.FuzeItem;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Manages all item registration in the Fuze plugin.
 */
public enum ItemManager {
    /**
     * The one and only {@link ItemManager}.
     */
    INSTANCE;

    private final @NotNull Map<Class<? extends FuzeItem>, ItemRegistry<? extends FuzeItem>> registries = new HashMap<>();
    private final @NotNull Map<String, Class<? extends FuzeItem>> itemTypes = new HashMap<>();

    /**
     * Clears all item registrations.
     * <p>After calling this method, you should always register all items again.</p>
     */
    public void reset() {
        registries.clear();
        itemTypes.clear();
    }

    /**
     * Registers an item.
     *
     * @param itemClass   the item class
     * @param sectionName the name of the {@link ConfigurationSection}, that contains the configuration for all instances of this item
     * @param <T>         the item
     */
    public <T extends FuzeItem> void registerItem(final @NotNull Class<T> itemClass, final @NotNull String sectionName) {
        ItemRegistry<T> registry = new ItemRegistry<>(itemClass, sectionName);
        ConfigurationSection section = Objects.requireNonNull(
                Config.instance().getRegistry().getConfigurationSection(sectionName),
                "\"" + sectionName + "\" not found in registry!"
        );
        registry.registerItem(section);
        String type = registry.getItemType()
                .orElseThrow(() -> new IllegalStateException("Item type is undefined after successfull registration of \"" + itemClass.getName() + "\"!"));
        if (itemTypes.containsKey(type))
            throw new IllegalArgumentException("Item type must be unique (\"" + type + "\")!");
        itemTypes.put(type, itemClass);
        registries.put(itemClass, registry);
    }

    /**
     * Returns an item registered with the provided key.
     * <p><b>You should probably use {@link FuzeItem#valueOf(String, Class)} or the item's own valueOf method instead!</b></p>
     *
     * @param key       the item key
     * @param itemClass the item class
     * @param <T>       the item
     * @return an optional item registered with the key
     */
    public <T extends FuzeItem> @NotNull Optional<T> getItemByKey(final @NotNull String key, final @NotNull Class<T> itemClass) {
        Optional<ItemRegistry<T>> optionalRegistry = getRegistryByClass(itemClass);
        return optionalRegistry.flatMap(registry -> registry.getItemByKey(key));
    }

    /**
     * Returns whether an item registered with the provided key exists.
     * <p><b>You should probably use {@link FuzeItem#exists(String, Class)} or the item's own exists method instead!</b></p>
     *
     * @param key       the item key
     * @param itemClass the item class
     * @param <T>       the item
     * @return true when the item exists otherwise false
     */
    public <T extends FuzeItem> boolean existsItemByKey(final @NotNull String key, final @NotNull Class<T> itemClass) {
        Optional<ItemRegistry<T>> optionalRegistry = getRegistryByClass(itemClass);
        return optionalRegistry.map(registry -> registry.getKeys().contains(key)).orElse(false);
    }

    /**
     * Translates an item type to an item class.
     *
     * @param type the item type
     * @return the item class
     */
    public @NotNull Optional<Class<? extends FuzeItem>> typeToItemClass(final @NotNull String type) {
        return Optional.ofNullable(itemTypes.get(type));
    }

    /**
     * Returns an {@link ItemRegistry} registered with the provided item class.
     *
     * @param itemClass the item class
     * @param <T>       the item
     * @return an optional {@link ItemRegistry} registered with the item class
     */
    public <T extends FuzeItem> @NotNull Optional<ItemRegistry<T>> getRegistryByClass(final @NotNull Class<T> itemClass) {
        ItemRegistry<? extends FuzeItem> registry = registries.get(itemClass);
        if (registry == null) return Optional.empty();
        @SuppressWarnings("unchecked") ItemRegistry<T> iRegistry = (ItemRegistry<T>) registry;
        return Optional.of(iRegistry);
    }

    /**
     * Returns an {@link ItemRegistry} registered with the provided item type.
     *
     * @param type the item type
     * @return an optional {@link ItemRegistry} registered with the item type
     */
    public @NotNull Optional<ItemRegistry<? extends FuzeItem>> getRegistryByType(final @NotNull String type) {
        Optional<Class<? extends FuzeItem>> optionalItemClass = typeToItemClass(type);
        if (optionalItemClass.isEmpty()) return Optional.empty();
        Optional<? extends ItemRegistry<? extends FuzeItem>> optionalRegistry = getRegistryByClass(optionalItemClass.get());
        @SuppressWarnings("unchecked") Optional<ItemRegistry<? extends FuzeItem>> typedOptionalRegistry = (Optional<ItemRegistry<? extends FuzeItem>>) optionalRegistry;
        return typedOptionalRegistry;
    }

    /**
     * Returns all items registered with the provided item class.
     *
     * @param itemClass the item class
     * @param <T>       the item
     * @return a list of items registered with the item class
     */
    public <T extends FuzeItem> @NotNull List<T> getItemsByClass(final @NotNull Class<T> itemClass) {
        Optional<ItemRegistry<T>> optionalRegistry = getRegistryByClass(itemClass);
        return optionalRegistry.map(ItemRegistry::getItems).orElseGet(List::of);
    }

    /**
     * Returns all items registered with the provided item type.
     *
     * @param type the item type
     * @return a list of items registered with the item type
     */
    public @NotNull List<? extends FuzeItem> getItemsByType(final @NotNull String type) {
        Optional<ItemRegistry<? extends FuzeItem>> optionalRegistry = getRegistryByType(type);
        return optionalRegistry.map(ItemRegistry::getItems).orElseGet(List::of);
    }

    /**
     * Returns all item keys registered with the provided item class.
     *
     * @param itemClass the item class
     * @return a set of item keys registered with the item class
     */
    public @NotNull Set<String> getKeysByClass(final @NotNull Class<? extends FuzeItem> itemClass) {
        Optional<? extends ItemRegistry<? extends FuzeItem>> optionalRegistry = getRegistryByClass(itemClass);
        return optionalRegistry.map(ItemRegistry::getKeys).orElseGet(Set::of);
    }

    /**
     * Returns all item keys registered with the provided item type.
     *
     * @param type the item type
     * @return a set of item keys registered with the item type
     */
    public @NotNull Set<String> getKeysByType(final @NotNull String type) {
        Optional<ItemRegistry<? extends FuzeItem>> optionalRegistry = getRegistryByType(type);
        return optionalRegistry.map(ItemRegistry::getKeys).orElseGet(Set::of);
    }

    /**
     * Returns all item types registered.
     *
     * @return a set of all item types registered
     */
    public @NotNull Set<String> getItemTypes() {
        return new HashSet<>(itemTypes.keySet());
    }
}
