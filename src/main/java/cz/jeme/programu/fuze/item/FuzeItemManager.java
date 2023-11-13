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
public enum FuzeItemManager {
    /**
     * The one and only {@link FuzeItemManager}.
     */
    INSTANCE;

    /**
     * A map used to store {@link ItemRegistry}.
     * <p>Enforces that the generic type of a key {@link Class} is the same as
     * the generic type of a value {@link ItemRegistry}</p>
     *
     * @param <F> {@link FuzeItem}
     */
    private static final class ItemRegistryMap<F extends FuzeItem> extends HashMap<Class<? extends F>, ItemRegistry<? extends F>> {
    }

    private final @NotNull ItemRegistryMap<FuzeItem> registryMap = new ItemRegistryMap<>();
    private final @NotNull Map<String, Class<? extends FuzeItem>> itemTypes = new HashMap<>();

    /**
     * Clears all item registrations.
     * <p>After calling this method, you should always register the {@link FuzeItem}s again.</p>
     */
    public void reset() {
        registryMap.clear();
        itemTypes.clear();
    }

    /**
     * Registers an item.
     *
     * @param itemClass   the {@link Class} of the item
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
        itemTypes.put(registry.getItemType()
                        .orElseThrow(() -> new IllegalStateException("Item type is undefined after successfull registration of fuze item \"" + itemClass.getName() + "\"!")),
                itemClass);
        registryMap.put(itemClass, registry);
    }

    /**
     * Returns an item registered with the provided key.
     * <p><b>You should probably use {@link FuzeItem#valueOf(String, Class)} or the item's own valueOf method instead!</b></p>
     *
     * @param key       the key of the item
     * @param itemClass the {@link Class} of the item
     * @param <I>       the item
     * @return an optional item registered with the key
     */
    public <I extends FuzeItem> @NotNull Optional<I> getItemByKey(final @NotNull String key, final @NotNull Class<I> itemClass) {
        Optional<ItemRegistry<I>> o = getRegistryByClass(itemClass);
        return o.flatMap(registry -> registry.getItemByKey(key));
    }

    /**
     * Translates an item type to an item {@link Class}.
     *
     * @param type the type of the item
     * @return the {@link Class} of the item
     */
    public @NotNull Optional<Class<? extends FuzeItem>> typeToItemClass(final @NotNull String type) {
        return Optional.ofNullable(itemTypes.get(type));
    }

    /**
     * Returns a {@link ItemRegistry} registered with the provided item {@link Class}
     *
     * @param itemClass the {@link Class} of the item
     * @param <I>       the item
     * @return an optional {@link ItemRegistry} registered with the item {@link Class}
     */
    public <I extends FuzeItem> @NotNull Optional<ItemRegistry<I>> getRegistryByClass(final @NotNull Class<I> itemClass) {
        ItemRegistry<? extends FuzeItem> registry = registryMap.get(itemClass);
        if (registry == null) return Optional.empty();
        @SuppressWarnings("unchecked") ItemRegistry<I> iRegistry = (ItemRegistry<I>) registry;
        return Optional.of(iRegistry);
    }

    /**
     * Returns a {@link ItemRegistry} registered with the provided item type.
     *
     * @param type the item type
     * @return an optional {@link ItemRegistry} registered with the item type
     */
    public @NotNull Optional<ItemRegistry<? extends FuzeItem>> getRegistryByType(final @NotNull String type) {
        Optional<? extends ItemRegistry<? extends FuzeItem>> o = getRegistryByClass(itemTypes.get(type));
        if (o.isEmpty()) return Optional.empty();
        return Optional.of(o.get());
    }

    /**
     * Returns all items registered with the provided item {@link Class}.
     *
     * @param itemClass the {@link Class} of the item
     * @param <I>       the item
     * @return a list of items registered with the item {@link Class}
     */
    public <I extends FuzeItem> @NotNull List<I> getItemsByClass(final @NotNull Class<I> itemClass) {
        Optional<ItemRegistry<I>> o = getRegistryByClass(itemClass);
        return o.map(ItemRegistry::getItems).orElseGet(List::of);
    }

    /**
     * Returns all items registered with the provided item type.
     *
     * @param type the item type
     * @return a list of items registered with the item type
     */
    public @NotNull List<? extends FuzeItem> getItemsByType(final @NotNull String type) {
        Optional<ItemRegistry<? extends FuzeItem>> o = getRegistryByType(type);
        return o.map(ItemRegistry::getItems).orElseGet(List::of);
    }

    /**
     * Returns all item keys registered with the provided item {@link Class}.
     *
     * @param itemClass the {@link Class} of the item
     * @return a set of item keys registered with the item {@link Class}
     */
    public @NotNull Set<String> getKeysByClass(final @NotNull Class<? extends FuzeItem> itemClass) {
        Optional<? extends ItemRegistry<? extends FuzeItem>> o = getRegistryByClass(itemClass);
        return o.map(ItemRegistry::getKeys).orElseGet(Set::of);
    }

    /**
     * Returns all item keys registered with the provided item type.
     *
     * @param type the item type
     * @return a set of item keys registered with the item type
     */
    public @NotNull Set<String> getKeysByType(final @NotNull String type) {
        Optional<ItemRegistry<? extends FuzeItem>> o = getRegistryByType(type);
        return o.map(ItemRegistry::getKeys).orElseGet(Set::of);
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
         * @param itemClass   the class of the item
         * @param sectionName the name of the {@link ConfigurationSection} that contains the configuration for all instances of this item
         */
        public ItemRegistry(final @NotNull Class<I> itemClass, final @NotNull String sectionName) {
            this.itemClass = itemClass;
            this.sectionName = sectionName;
        }

        /**
         * Registers all items in the {@link ConfigurationSection} from the data provided by its sub-sections.
         *
         * @param section the {@link ConfigurationSection}, that contains the configuration for all instances of this item
         */
        public void registerItem(final @NotNull ConfigurationSection section) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection itemSection = Objects.requireNonNull(section.getConfigurationSection(key));
                registerItemInstance(itemSection);
            }
        }

        /**
         * Registers a single item from the data provided by the {@link ConfigurationSection}.
         *
         * @param itemSection the {@link ConfigurationSection} to register the item from
         */
        public void registerItemInstance(final @NotNull ConfigurationSection itemSection) {
            Constructor<I> constructor;
            try {
                constructor = itemClass.getDeclaredConstructor(ConfigurationSection.class);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "Fuze item class \"" + itemClass.getName() + "\" does not have any constructor with ConfigurationSection!",
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
         * @param key the key of this item
         * @return an optional item
         */
        public @NotNull Optional<I> getItemByKey(final @NotNull String key) {
            return Optional.ofNullable(keyedItems.get(key));
        }

        /**
         * Returns the item {@link Class} of this {@link ItemRegistry}.
         *
         * @return the item {@link Class} of this {@link ItemRegistry}
         */
        public @NotNull Class<I> getItemClass() {
            return itemClass;
        }

        /**
         * Returns the item type of this {@link ItemRegistry}.
         * <p>At least one item instance must be registered for this method to return a full {@link Optional}.</p>
         *
         * @return an optional item type of this {@link ItemRegistry}
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
         * Returns the name of the {@link ConfigurationSection} that contains the configuration for all instances of this item
         *
         * @return the name of the {@link ConfigurationSection} that contains the configuration for all instances of this item
         */
        public @NotNull String getSectionName() {
            return sectionName;
        }
    }
}
