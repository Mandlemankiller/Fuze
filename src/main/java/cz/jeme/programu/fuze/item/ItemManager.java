package cz.jeme.programu.fuze.item;

import cz.jeme.programu.fuze.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    /**
     * A map used to store {@link ItemRegistry}.
     * <p>Enforces that the generic type of a key class is the same as
     * the generic type of a value {@link ItemRegistry}.</p>
     *
     * @param <I> {@link FuzeItem}
     */
    private static final class ItemRegistryMap<I extends FuzeItem> extends HashMap<Class<? extends I>, ItemRegistry<? extends I>> {
    }

    private final @NotNull ItemRegistryMap<FuzeItem> registries = new ItemRegistryMap<>();
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
     * @param <I>         the item
     */
    public <I extends FuzeItem> void registerItem(final @NotNull Class<I> itemClass, final @NotNull String sectionName) {
        ItemRegistry<I> registry = new ItemRegistry<>(itemClass, sectionName);
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
     * @param <I>       the item
     * @return an optional item registered with the key
     */
    public <I extends FuzeItem> @NotNull Optional<I> getItemByKey(final @NotNull String key, final @NotNull Class<I> itemClass) {
        Optional<ItemRegistry<I>> optionalRegistry = getRegistryByClass(itemClass);
        return optionalRegistry.flatMap(registry -> registry.getItemByKey(key));
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
     * @param <I>       the item
     * @return an optional {@link ItemRegistry} registered with the item class
     */
    public <I extends FuzeItem> @NotNull Optional<ItemRegistry<I>> getRegistryByClass(final @NotNull Class<I> itemClass) {
        ItemRegistry<? extends FuzeItem> registry = registries.get(itemClass);
        if (registry == null) return Optional.empty();
        @SuppressWarnings("unchecked") ItemRegistry<I> iRegistry = (ItemRegistry<I>) registry;
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
     * @param <I>       the item
     * @return a list of items registered with the item class
     */
    public <I extends FuzeItem> @NotNull List<I> getItemsByClass(final @NotNull Class<I> itemClass) {
        Optional<ItemRegistry<I>> optionalRegistry = getRegistryByClass(itemClass);
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

    /**
     * Represents a registry for a specific item.
     *
     * @param <I> the item
     */
    public static final class ItemRegistry<I extends FuzeItem> {
        private final @NotNull Map<String, I> keyedItems = new HashMap<>();

        private final @NotNull Class<I> itemClass;
        private final @NotNull String sectionName;
        private @Nullable String itemType;

        /**
         * Creates the {@link ItemRegistry}.
         *
         * @param itemClass   the item class
         * @param sectionName the name of the {@link ConfigurationSection} that contains the configuration for all instances of this item
         */
        public ItemRegistry(final @NotNull Class<I> itemClass, final @NotNull String sectionName) {
            this.itemClass = itemClass;
            this.sectionName = sectionName;
        }

        /**
         * Registers all item instances using the data read from the sub-sections of the provided {@link ConfigurationSection}.
         *
         * @param section the {@link ConfigurationSection} that contains the configuration for all instances of this item
         */
        public void registerItem(final @NotNull ConfigurationSection section) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection itemSection = Objects.requireNonNull(section.getConfigurationSection(key));
                registerItemInstance(itemSection);
            }
        }

        /**
         * Registers a single item instance using the data read from {@link ConfigurationSection}.
         *
         * @param itemSection the {@link ConfigurationSection} to register the item instance from
         */
        public void registerItemInstance(final @NotNull ConfigurationSection itemSection) {
            Constructor<I> constructor;
            try {
                constructor = itemClass.getDeclaredConstructor(ConfigurationSection.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Item class \"" + itemClass.getName() + "\" does not have any constructor with ConfigurationSection!",
                        e);
            }
            constructor.setAccessible(true);
            I fuzeItem;
            try {
                fuzeItem = constructor.newInstance(itemSection);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException(
                        "Unable to register (initialize) fuze item class \"" + itemClass.getName() + "\"!",
                        e);
            }
            keyedItems.put(fuzeItem.getKey(), fuzeItem);
            if (itemType == null) {
                itemType = fuzeItem.getType();
            } else if (!itemType.equals(fuzeItem.getType()))
                throw new IllegalStateException("\"" + itemClass.getName() + "\" fuze items are not of the same type!");
        }

        /**
         * Returns an item registered with the provided key.
         *
         * @param key the item key
         * @return an optional item
         */
        public @NotNull Optional<I> getItemByKey(final @NotNull String key) {
            return Optional.ofNullable(keyedItems.get(key));
        }

        /**
         * Returns the class of this {@link ItemRegistry}'s item.
         *
         * @return the item class
         */
        public @NotNull Class<I> getItemClass() {
            return itemClass;
        }

        /**
         * Returns the type of this {@link ItemRegistry}'s item.
         * <p>At least one item instance must be registered for this method to return a non-empty {@link Optional}.</p>
         *
         * @return an optional item type
         */
        public @NotNull Optional<String> getItemType() {
            return Optional.ofNullable(itemType);
        }

        /**
         * Returns all item keys of this {@link ItemRegistry}.
         *
         * @return a set containing all the item keys of this {@link ItemRegistry}
         */
        public @NotNull Set<String> getKeys() {
            return new HashSet<>(keyedItems.keySet());
        }

        /**
         * Returns all items of this {@link ItemRegistry}.
         *
         * @return a list containing all the items of this {@link ItemRegistry}
         */
        public @NotNull List<I> getItems() {
            return new ArrayList<>(keyedItems.values());
        }

        /**
         * Returns the name of the {@link ConfigurationSection} that contains the configuration for all instances of this {@link ItemRegistry}'s item.
         *
         * @return the name of the {@link ConfigurationSection}
         */
        public @NotNull String getSectionName() {
            return sectionName;
        }
    }
}
