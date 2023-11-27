package cz.jeme.programu.fuze.item.storage;

import cz.jeme.programu.fuze.Fuze;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A bare bones concrete fuze implementation of {@link ItemData}.
 *
 * @param <T> the primary data type stored in the container
 * @param <Z> the retrieved (secondary) data type used when writing and reading
 */
public final class FuzeItemData<T, Z> implements ItemData<T, Z> {
    private static final @NotNull Set<String> KEYS = new HashSet<>();
    private final @NotNull NamespacedKey namespacedKey;
    private final @NotNull String key;
    private final @NotNull PersistentDataType<T, Z> type;

    /**
     * Creates a new instance of {@link FuzeItemData}.
     *
     * @param key  the unique key of this FuzeItemData; this will be later used to create a {@link NamespacedKey}
     * @param type the type of this FuzeItemData; for a start, you can use type enums from the {@link PersistentDataType} Class.
     */
    public FuzeItemData(final @NotNull String key, final @NotNull PersistentDataType<T, Z> type) {
        if (FuzeItemData.KEYS.contains(key))
            throw new IllegalArgumentException("A FuzeItemData with the provided key already exists!");
        FuzeItemData.KEYS.add(key);
        this.key = key;
        this.type = type;
        namespacedKey = new NamespacedKey(Fuze.getPlugin(), key);
    }

    @Override
    public @NotNull NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public @NotNull PersistentDataType<T, Z> getType() {
        return type;
    }

    /**
     * Returns the String key of this FuzeItemData provided in the constructor.
     *
     * @return the key of this FuzeItemData
     */
    public @NotNull String getKey() {
        return key;
    }

    @Override
    public boolean contains(final @NotNull PersistentDataContainer container) {
        return container.has(namespacedKey, this);
    }

    @Override
    public boolean contains(final @NotNull PersistentDataHolder holder) {
        return contains(holder.getPersistentDataContainer());
    }

    @Override
    public boolean contains(final @NotNull ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return contains(meta);
    }

    @Override
    public @NotNull Optional<Z> read(final @NotNull PersistentDataContainer container) {
        return Optional.ofNullable(container.get(namespacedKey, this));
    }

    @Override
    public @NotNull Optional<Z> read(final @NotNull PersistentDataHolder holder) {
        return read(holder.getPersistentDataContainer());
    }

    @Override
    public @NotNull Optional<Z> read(final @NotNull ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();
        return read(meta);
    }

    @Override
    public void write(final @NotNull PersistentDataContainer container, final @NotNull Z value) {
        container.set(namespacedKey, this, value);
    }

    @Override
    public void write(final @NotNull PersistentDataHolder holder, final @NotNull Z value) {
        write(holder.getPersistentDataContainer(), value);
    }

    @Override
    public void write(final @NotNull ItemStack item, final @NotNull Z value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        write(meta, value);
        item.setItemMeta(meta);
    }

    @Override
    public @NotNull Class<T> getPrimitiveType() {
        return type.getPrimitiveType();
    }

    @Override
    public @NotNull Class<Z> getComplexType() {
        return type.getComplexType();
    }

    @Override
    public @NotNull T toPrimitive(final @NotNull Z complex, final @NotNull PersistentDataAdapterContext context) {
        return type.toPrimitive(complex, context);
    }

    @Override
    public @NotNull Z fromPrimitive(final @NotNull T primitive, final @NotNull PersistentDataAdapterContext context) {
        return type.fromPrimitive(primitive, context);
    }
}