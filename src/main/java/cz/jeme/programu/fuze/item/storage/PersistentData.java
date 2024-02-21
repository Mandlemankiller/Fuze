package cz.jeme.programu.fuze.item.storage;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents item data that can be stored in containers.
 * Mainly used for {@link ItemStack}, {@link ItemMeta} and {@link Entity}.
 *
 * @param <K> the primary data type stored in the container
 * @param <V> the retrieved (secondary) data type used when writing and reading
 */
public interface PersistentData<K, V> extends PersistentDataType<K, V> {
    /**
     * Returns the {@link NamespacedKey} under which this ItemData is registered.
     *
     * @return the NamespacedKey of this ItemData
     */
    @NotNull NamespacedKey getNamespacedKey();

    /**
     * Returns the type of this ItemData.
     * This determines the {@link K} and {@link V} of this ItemData.
     * For a start, you can use the enum constants in the {@link PersistentDataType} Class.
     *
     * @return the type of this ItemData
     */
    @NotNull PersistentDataType<K, V> getType();

    /**
     * Checks whether a data container contains this ItemData.
     *
     * @param container the container to check
     * @return true when the container contains this ItemData otherwise false
     */
    boolean contains(final @NotNull PersistentDataContainer container);

    /**
     * Checks whether a data holder contains this ItemData.
     *
     * @param holder the holder to check
     * @return true when the holder contains this ItemData otherwise false
     */
    boolean contains(final @NotNull PersistentDataHolder holder);

    /**
     * Checks whether an {@link ItemStack} contains this ItemData.
     *
     * @param item the ItemStack to check
     * @return true when the ItemStack contains this ItemData otherwise false
     */
    boolean contains(final @NotNull ItemStack item);

    /**
     * Reads this ItemData from a data container.
     *
     * @param container the container to read this ItemData from
     * @return this ItemData stored in the container or an empty optional when the data is not present
     */
    @NotNull Optional<V> read(final @NotNull PersistentDataContainer container);

    /**
     * Reads this ItemData from a data holder.
     *
     * @param holder the holder to read this ItemData from
     * @return this ItemData stored in the holder or an empty optional when the data is not present
     */
    @NotNull Optional<V> read(final @NotNull PersistentDataHolder holder);

    /**
     * Reads this ItemData from an {@link ItemStack}.
     *
     * @param item the ItemStack to read this ItemData from
     * @return this ItemData stored in the ItemStack or an empty optional when the data is not present
     */
    @NotNull Optional<V> read(final @NotNull ItemStack item);

    /**
     * Writes this ItemData to a data container.
     *
     * @param container the container to write this ItemData to
     * @param value     the value of this ItemData to write
     */
    void write(final @NotNull PersistentDataContainer container, final @NotNull V value);

    /**
     * Writes this ItemData to a data holder.
     *
     * @param holder the holder to write this ItemData to
     * @param value  the value of this ItemData to write
     */
    void write(final @NotNull PersistentDataHolder holder, final @NotNull V value);

    /**
     * Writes this ItemData to an {@link ItemStack}.
     *
     * @param item  the ItemStack to write this ItemData to
     * @param value the value of this ItemData to write
     */
    void write(final @NotNull ItemStack item, final @NotNull V value);
}