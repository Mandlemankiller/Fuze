package cz.jeme.programu.fuze.item;

import cz.jeme.programu.fuze.item.event.EventManager;
import cz.jeme.programu.fuze.item.impl.Ammo;
import cz.jeme.programu.fuze.item.impl.Gun;
import cz.jeme.programu.fuze.item.loot.Rarity;
import cz.jeme.programu.fuze.item.registry.ItemManager;
import cz.jeme.programu.fuze.item.storage.FuzeItemData;
import cz.jeme.programu.fuze.item.storage.ItemData;
import cz.jeme.programu.fuze.util.Messages;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Represents a custom item in the Fuze plugin.
 * <p>This class is meant to be inherited by different concrete items.</p>
 * e.g. {@link Gun}, {@link Ammo}...
 * <p>Dont forget to register the item on start type using {@link ItemManager#registerItem(Class, String)}.</p>
 */
public abstract class FuzeItem implements Keyable {
    /**
     * Returns an item registered with the provided key.
     *
     * @param key       the item key
     * @param itemClass the item class
     * @param <T>       the item
     * @return an item registered with the item key
     * @throws IllegalArgumentException when no item of the itemClass with the provided key exists
     */
    public static <T extends FuzeItem> @NotNull T valueOf(final @NotNull String key, final @NotNull Class<T> itemClass) {
        return ItemManager.INSTANCE.getItemByKey(key, itemClass)
                .orElseThrow(() -> new IllegalArgumentException("Unknown item key: \"" + key + "\"!"));
    }

    /**
     * Returns whether an item registered with the provided key exists.
     *
     * @param key       the item key
     * @param itemClass the item class
     * @return true when the item exists otherwise false
     */
    public static boolean exists(final @NotNull String key, final @NotNull Class<? extends FuzeItem> itemClass) {
        return ItemManager.INSTANCE.existsItemByKey(key, itemClass);
    }

    public static final @NotNull ItemData<String, String> KEY = new FuzeItemData<>("item_key", ItemData.STRING);

    /**
     * The {@link ConfigurationSection} of the item instance in config.
     * <p>Obtained as a parameter in the constructor.</p>
     */
    protected final @NotNull ConfigurationSection section;

    /**
     * The unique key of this item.
     * <p>Read from the {@link ConfigurationSection}'s name.</p>
     */
    protected final @NotNull String key;

    /**
     * The display name of this item.
     * <p>Read from the {@link ConfigurationSection} and then deserialized ({@link Messages#deserialize(String)}).</p>
     */
    protected final @NotNull Component name;

    /**
     * The rarity of this item.
     * <p>Read from the {@link ConfigurationSection} and then parsed using {@link Rarity#valueOf(String)}.</p>
     */
    protected final @NotNull Rarity rarity;

    /**
     * The {@link ItemStack} of this item.
     * <p>This basically represents the core of this item.</p>
     */
    protected final @NotNull ItemStack item;

    /**
     * The material of this item.
     * <p>Read from the {@link FuzeItem#getMaterial(ConfigurationSection)} method.</p>
     */
    protected final @NotNull Material material;

    /**
     * Initializes an item.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the item instance in config
     * @throws IllegalArgumentException when the section name (item key) doesn't match [a-z0-9_.-]
     *                                  or when when no rarity with the rarity key exists
     * @throws NullPointerException     when name or rarity is not set in config
     * @throws IllegalStateException    when the material wasn't defined (neither {@link FuzeItem#getMaterial(ConfigurationSection)} nor {@link FuzeItem#getMaterial()} has been overridden)
     */
    protected FuzeItem(final @NotNull ConfigurationSection section) {
        this.section = section;

        final String tempKey = section.getName();
        if (!tempKey.matches("^[a-z0-9_.-]+$"))
            throw new IllegalArgumentException(
                    "Invalid item key: %s! Keys can only contain [a-z0-9_.-]!"
                            .formatted(tempKey)
            );
        key = tempKey;

        name = Messages.deserialize(requireConfigString("name"));

        rarity = Rarity.valueOf(requireConfigString("rarity"));

        material = getMaterial(section);

        item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(material);
        KEY.set(meta, key);
        meta.displayName(Messages.deserialize("<!i>").append(name));
        item.setItemMeta(meta);

        // Register all events
        EventManager.INSTANCE.registerEventsTree(getClass(), false);
    }

    /**
     * Returns a String value of the path in the config section.
     *
     * @param path the path of the String to get
     * @return requested String
     * @throws NullPointerException when the path doesn't exist or the value is not a String
     */
    protected final @NotNull String requireConfigString(final @NotNull String path) {
        return Objects.requireNonNull(
                section.getString(path),
                Messages.missing(path, this)
        );
    }

    /**
     * Returns an int value of the path in the config section.
     *
     * @param path the path of the int to get
     * @return requested int
     * @throws NullPointerException when the path doesn't exist or the value is not an int
     */
    protected final int requireConfigInt(final @NotNull String path) {
        if (!section.isInt(path))
            throw new NullPointerException(Messages.missing(path, this));
        return section.getInt(path);
    }

    /**
     * Returns a long value of the path in the config section.
     *
     * @param path the path of the long to get
     * @return requested long
     * @throws NullPointerException when the path doesn't exist or the value is not a long
     */
    protected final long requireConfigLong(final @NotNull String path) {
        if (!section.isLong(path) && !section.isInt(path))
            throw new NullPointerException(Messages.missing(path, this));
        return section.getLong(path);
    }

    /**
     * Returns a double value of the path in the config section.
     *
     * @param path the path of the double to get
     * @return requested double
     * @throws NullPointerException when the path doesn't exist or the value is not a double
     */
    protected final double requireConfigDouble(final @NotNull String path) {
        if (!section.isDouble(path) && !section.isInt(path) && !section.isLong(path))
            throw new NullPointerException(Messages.missing(path, this));
        return section.getDouble(path);
    }

    /**
     * Returns the material of this item.
     * <p>This method should be overridden by items with a single, statically set material.</p>
     * For items with multiple, dynamically loaded materials (read from a {@link ConfigurationSection}) see {@link FuzeItem#getMaterial(ConfigurationSection)}.
     *
     * @return the material of this item
     * @throws IllegalStateException when neither this method nor {@link FuzeItem#getMaterial(ConfigurationSection)} has been overridden
     */
    public @NotNull Material getMaterial() {
        if (material == null) throw new IllegalStateException("Item " + key + " did not specify a material!");
        return material;
    }

    /**
     * Returns the material of this item.
     * <p>This method should be overridden by items with a multiple, dynamically loaded materials.</p>
     * For items with a single, statically set material see {@link FuzeItem#getMaterial()}.
     *
     * @return the material of this item
     * @throws IllegalStateException when neither this method nor {@link FuzeItem#getMaterial()} has been overridden
     */
    protected @NotNull Material getMaterial(final @NotNull ConfigurationSection section) {
        return getMaterial();
    }

    /**
     * Returns the key of this item.
     *
     * @return the key
     */
    @Override
    public final @NotNull String getKey() {
        return key;
    }

    /**
     * Returns the display name of this item.
     *
     * @return the display name
     */
    @Override
    public final @NotNull Component getName() {
        return name;
    }

    /**
     * Returns the rarity of this item.
     *
     * @return the rarity
     */
    public final @NotNull Rarity getRarity() {
        return rarity;
    }

    /**
     * Returns the {@link ItemStack} of this item.
     *
     * @return the item stack
     */
    public final @NotNull ItemStack getItem() {
        return new ItemStack(item);
    }

    /**
     * Turns this item into a nice readable string.
     * The {@link FuzeItem#name} will be used for this purpose.
     *
     * @return the stripped display name of this item
     */
    @Override
    public @NotNull String toString() {
        return Messages.strip(Messages.serialize(name));
    }
}