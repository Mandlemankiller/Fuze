package cz.jeme.programu.fuze.item.registry;

import cz.jeme.programu.fuze.item.FuzeItem;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Represents a registry for a specific item.
 *
 * @param <T> the item
 */
public final class ItemRegistry<T extends FuzeItem> {
    private static final @NotNull String REGISTER_ERROR_MESSAGE = "Unable to register (initialize) item: %s";
    private final @NotNull Map<String, T> items = new HashMap<>();
    private final @NotNull Class<T> itemClass;
    private final @NotNull String sectionName;
    private @Nullable String itemType;
    private @Nullable Constructor<T> constructor;

    /**
     * Creates the {@link ItemRegistry}.
     *
     * @param itemClass   the item class
     * @param sectionName the name of the {@link ConfigurationSection} that contains the configuration for all instances of this item
     */
    public ItemRegistry(final @NotNull Class<T> itemClass, final @NotNull String sectionName) {
        this.itemClass = itemClass;
        this.sectionName = sectionName;
    }

    /**
     * Registers all item instances using the data read from the sub-sections of the provided {@link ConfigurationSection}.
     *
     * @param section the {@link ConfigurationSection} that contains the configuration for all instances of this item
     * @throws IllegalStateException when an item instance couldn't be initialized (see {@link ItemRegistry#newItem(ConfigurationSection)})
     *                               or when the item instance type doesn't match the item instance first {@link ItemRegistry#itemType}
     * @throws NullPointerException  when a subsection of the provided section is not a {@link ConfigurationSection}
     */
    public void registerItem(final @NotNull ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            ConfigurationSection itemSection = Objects.requireNonNull(section.getConfigurationSection(key));
            registerItemInstance(itemSection);
        }
    }

    /**
     * Initializes the item constructor.
     * This prevents unnecessary reflection calls.
     *
     * @throws IllegalStateException when no constructor matching {@link FuzeItem(ConfigurationSection)} is found in the item class.
     */
    private void initConstructor() {
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
    }

    /**
     * Creates a new item instance.
     *
     * @param itemSection the {@link ConfigurationSection} to register the item instance from
     * @return a new item instance
     * @throws IllegalStateException when no constructor was found (see {@link ItemRegistry#initConstructor()})
     *                               or when the item couldn't be initialized (couldn't access the constructor, the constructor threw an exception etc.)
     */
    private @NotNull T newItem(final @NotNull ConfigurationSection itemSection) {
        if (constructor == null) initConstructor();
        T fuzeItem;
        try {
            fuzeItem = constructor.newInstance(itemSection);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(
                    REGISTER_ERROR_MESSAGE.formatted(itemClass.getName()),
                    e
            );
        } catch (InvocationTargetException e) {
            throw new IllegalStateException(
                    REGISTER_ERROR_MESSAGE.formatted(itemClass.getName()),
                    e.getCause()
            );
        }
        return fuzeItem;
    }

    /**
     * Registers a single item instance using the data read from {@link ConfigurationSection}.
     *
     * @param itemSection the {@link ConfigurationSection} to register the item instance from
     * @throws IllegalStateException when the item couldn't be initialized (see {@link ItemRegistry#newItem(ConfigurationSection)})
     *                               or when the item type doesn't match the first {@link ItemRegistry#itemType}
     */
    public void registerItemInstance(final @NotNull ConfigurationSection itemSection) {
        T fuzeItem = newItem(itemSection);

        items.put(fuzeItem.getKey(), fuzeItem);
        if (itemType == null) {
            itemType = fuzeItem.getType();
        } else if (!itemType.equals(fuzeItem.getType()))
            throw new IllegalStateException(
                    "Item instances are not of the same type in item: %s"
                            .formatted(itemClass.getName())
            );
    }

    /**
     * Returns an item registered with the provided key.
     *
     * @param key the item key
     * @return an optional item
     */
    public @NotNull Optional<T> getItemByKey(final @NotNull String key) {
        return Optional.ofNullable(items.get(key));
    }

    /**
     * Returns the class of this {@link ItemRegistry}'s item.
     *
     * @return the item class
     */
    public @NotNull Class<T> getItemClass() {
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
        return new HashSet<>(items.keySet());
    }

    /**
     * Returns all items of this {@link ItemRegistry}.
     *
     * @return a list containing all the items of this {@link ItemRegistry}
     */
    public @NotNull List<T> getItems() {
        return new ArrayList<>(items.values());
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
