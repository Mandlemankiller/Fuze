package cz.jeme.programu.fuze.item.storage;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface ItemData<K, V> extends PersistentDataType<K, V> {
    @NotNull NamespacedKey getNamespacedKey();

    @NotNull PersistentDataType<K, V> getType();

    @NotNull String getKey();

    boolean has(final @NotNull PersistentDataContainer container);

    boolean has(final @NotNull PersistentDataHolder holder);

    boolean has(final @NotNull ItemStack item);

    @NotNull Optional<V> get(final @NotNull PersistentDataContainer container);

    @NotNull Optional<V> get(final @NotNull PersistentDataHolder holder);

    @NotNull Optional<V> get(final @NotNull ItemStack item);

    void set(final @NotNull PersistentDataContainer container, final @NotNull V value);

    void set(final @NotNull PersistentDataHolder holder, final @NotNull V value);

    void set(final @NotNull ItemStack item, final @NotNull V value);
}