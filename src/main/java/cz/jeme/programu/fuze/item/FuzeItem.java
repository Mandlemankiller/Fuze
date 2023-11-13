package cz.jeme.programu.fuze.item;

import cz.jeme.programu.fuze.Keyable;
import cz.jeme.programu.fuze.Message;
import cz.jeme.programu.fuze.item.impl.Ammo;
import cz.jeme.programu.fuze.item.impl.Gun;
import net.kyori.adventure.text.Component;
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
     * The unique key of this item.
     * <p>Read from the {@link ConfigurationSection}'s name.</p>
     */
    protected final @NotNull String key;

    /**
     * The display name of this item.
     * <p>Read from the {@link ConfigurationSection} and then deserialized ({@link Message#deserialize(String)}).</p>
     */
    protected final @NotNull Component name;

    /**
     * The {@link Rarity} of this item.
     * <p>Read from the {@link ConfigurationSection} and then parsed using {@link Rarity#valueOf(String)}.</p>
     */
    protected final @NotNull Rarity rarity;

    /**
     * The {@link ItemStack} of this item.
     * <p>This basically represents the core of this item.</p>
     */
    protected final @NotNull ItemStack item;

    /**
     * Initializes an item.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the item in config
     */
    protected FuzeItem(final @NotNull ConfigurationSection section) {
        final String tempKey = section.getName();
        if (!tempKey.matches("^[a-z0-9_.-]+$"))
            throw new IllegalArgumentException(
                    "Invalid " + getType() + " key \"" + tempKey + "\"! " +
                            "Keys can only contain [a-z0-9_.-]!"
            );
        key = tempKey;

        name = Message.deserialize(Objects.requireNonNull(
                section.getString("name"),
                Message.missing("name", this)
        ));

        rarity = Rarity.valueOf(Objects.requireNonNull(
                section.getString("rarity"),
                Message.missing("rarity", this)
        ));

        item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Message.deserialize("<!i>").append(name));
        item.setItemMeta(meta);
    }

    /**
     * Returns the item {@link Material} of this {@link FuzeItem#item}.
     *
     * @return the {@link Material} of this item
     */
    public abstract @NotNull Material getMaterial();

    /**
     * Returns the {@link FuzeItem#key} of this item.
     *
     * @return the key
     */
    @Override
    public final @NotNull String getKey() {
        return key;
    }

    /**
     * Returns the display {@link FuzeItem#name} of this item.
     *
     * @return the display name
     */
    public final @NotNull Component getName() {
        return name;
    }

    /**
     * Returns the {@link FuzeItem#rarity} of this item.
     *
     * @return the rarity
     */
    public final @NotNull Rarity getRarity() {
        return rarity;
    }

    /**
     * Returns the {@link FuzeItem#item} of this item.
     *
     * @return the item
     */
    public final @NotNull ItemStack getItem() {
        return new ItemStack(item);
    }

    /**
     * Turns this item into a nice readable string.
     * The {@link FuzeItem#name} will be used for this purpose.
     *
     * @return the stripped {@link FuzeItem#name} of this item
     */
    @Override
    public @NotNull String toString() {
        return Message.strip(Message.serialize(name));
    }

    /**
     * Returns an item registered with the provided key.
     *
     * @param key       the item key
     * @param itemClass the item class
     * @param <I>       the item
     * @return an item registered with the key
     * @throws IllegalArgumentException when the key is not a valid item key
     */
    public static <I extends FuzeItem> @NotNull I valueOf(final @NotNull String key, final @NotNull Class<I> itemClass) {
        return ItemManager.INSTANCE.getItemByKey(key, itemClass).orElseThrow(
                () -> new IllegalArgumentException("Unknown fuze item key: \"" + key + "\"!")
        );
    }
}