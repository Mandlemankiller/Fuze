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

public final class FuzeItemData<T, Z> implements ItemData<T, Z> {
    private static final @NotNull Set<String> KEYS = new HashSet<>();
    private final @NotNull NamespacedKey namespacedKey;
    private final @NotNull String key;
    private final @NotNull PersistentDataType<T, Z> type;

    public FuzeItemData(final @NotNull String key, final @NotNull PersistentDataType<T, Z> type) {
        if (KEYS.contains(key))
            throw new IllegalArgumentException("A fuze item data storage with the provided key already exists!");
        KEYS.add(key);
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

    @Override
    public @NotNull String getKey() {
        return key;
    }

    @Override
    public boolean has(final @NotNull PersistentDataContainer container) {
        return container.has(namespacedKey, this);
    }

    @Override
    public boolean has(final @NotNull PersistentDataHolder holder) {
        return has(holder.getPersistentDataContainer());
    }

    @Override
    public boolean has(final @NotNull ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return has(meta);
    }

    @Override
    public @NotNull Optional<Z> get(final @NotNull PersistentDataContainer container) {
        return Optional.ofNullable(container.get(namespacedKey, this));
    }

    @Override
    public @NotNull Optional<Z> get(final @NotNull PersistentDataHolder holder) {
        return get(holder.getPersistentDataContainer());
    }

    @Override
    public @NotNull Optional<Z> get(final @NotNull ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();
        return get(meta);
    }

    @Override
    public void set(final @NotNull PersistentDataContainer container, final @NotNull Z value) {
        container.set(namespacedKey, this, value);
    }

    @Override
    public void set(final @NotNull PersistentDataHolder holder, final @NotNull Z value) {
        set(holder.getPersistentDataContainer(), value);
    }

    @Override
    public void set(final @NotNull ItemStack item, final @NotNull Z value) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());
        set(meta, value);
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