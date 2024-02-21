package cz.jeme.programu.fuze.item.impl;

import cz.jeme.programu.fuze.util.Bullet;
import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.ItemManager;
import cz.jeme.programu.fuze.item.event.Subscribe;
import cz.jeme.programu.fuze.item.storage.FuzePersistentData;
import cz.jeme.programu.fuze.item.storage.PersistentData;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a gun in the Fuze plugin.
 */
public class Gun extends FuzeItem {

    /**
     * Returns a Gun registered with the provided key.
     *
     * @param key the Gun key
     * @return a Gun registered with the Gun key
     * @throws IllegalArgumentException when no Gun with the provided key exists
     */
    public static @NotNull Gun valueOf(final @NotNull String key) {
        return FuzeItem.valueOf(key, Gun.class);
    }

    /**
     * Returns a Gun parsed from an {@link ItemStack}.
     *
     * @param item the ItemStack to read the key from
     * @return a Gun parsed from the ItemStack
     * @throws IllegalArgumentException when the provided ItemStack is not a Gun or
     *                                  when the key data stored inside the ItemStack is not valid
     */
    public static @NotNull Gun valueOf(final @NotNull ItemStack item) {
        return FuzeItem.valueOf(item, Gun.class);
    }

    /**
     * Returns whether a Gun registered with the provided key exists.
     *
     * @param key the Gun key
     * @return true when the Gun exists otherwise false
     */
    public static boolean exists(final @Nullable String key) {
        return FuzeItem.exists(key, Gun.class);
    }

    /**
     * Returns whether an {@link ItemStack} is a Gun.
     *
     * @param item the ItemStack to check the key data on
     * @return true when the ItemStack is a Gun otherwise false
     */
    public static boolean exists(final @Nullable ItemStack item) {
        return FuzeItem.exists(item, Gun.class);
    }

    /**
     * Gun shoot cooldown data storage.
     */
    public static final @NotNull PersistentData<Integer, Integer> SHOOT_COOLDOWN = new FuzePersistentData<>("gun_shoot_cooldown", PersistentData.INTEGER);

    /**
     * Gun damage data storage.
     */
    public static final @NotNull PersistentData<Double, Double> DAMAGE = new FuzePersistentData<>("gun_damage", PersistentData.DOUBLE);

    /**
     * Gun velocity data storage.
     */
    public static final @NotNull PersistentData<Double, Double> VELOCITY = new FuzePersistentData<>("gun_velocity", PersistentData.DOUBLE);

    /**
     * The ammo of this Gun.
     * <p>Read from the config and then parsed using {@link Ammo#valueOf(String)}.</p>
     */
    private final @NotNull Ammo ammo;

    /**
     * The shoot cooldown of this Gun in milliseconds.
     * <p>Read from the config.</p>
     */
    private final int shootCooldown;

    /**
     * The damage of this Gun.
     * <p>1 damage = half a heart</p>
     * <p>Read from the config.</p>
     */
    private final double damage;

    /**
     * The velocity of this Gun's bullets.
     * <p>Read from the config.</p>
     */
    private final double velocity;

    /**
     * Initializes a Gun.
     * <p><b>This constructor and constructors of this classes inheritors should never be called manually!</b></p>
     * <p>Items are initialized automatically using reflection in {@link ItemManager} during item registration!</p>
     *
     * @param section the {@link ConfigurationSection} of the gun instance in config
     * @throws IllegalArgumentException when the section name (gun key) doesn't match [a-z0-9_.-],
     *                                  when no rarity with the rarity key exists
     *                                  and when no ammo with the ammo key exists
     * @throws NullPointerException     when name, rarity, ammo, shoot cooldown or damage is not set in config
     */
    protected Gun(final @NotNull ConfigurationSection section) {
        super(section);

        // Load gun data from config
        ammo = Ammo.valueOf(requireConfigString("ammo"));
        shootCooldown = requireConfigInt("shoot-cooldown");
        damage = requireConfigDouble("damage");
        velocity = requireConfigDouble("velocity");

        // Save gun data to the item
        Gun.SHOOT_COOLDOWN.write(item, shootCooldown);
        Gun.DAMAGE.write(item, damage);
        Gun.VELOCITY.write(item, velocity);

        CrossbowMeta crossbowMeta = ((CrossbowMeta) item.getItemMeta());
        crossbowMeta.addChargedProjectile(Bullet.CROSSBOW_ARROW);
        item.setItemMeta(crossbowMeta);
    }

    /**
     * Returns the Gun material.
     *
     * @return always {@link Material#CROSSBOW}
     */
    @Override
    public final @NotNull Material getMaterial() {
        return Material.CROSSBOW;
    }

    @Override
    protected final @NotNull Material getMaterial(@NotNull ConfigurationSection section) {
        return super.getMaterial(section);
    }

    /**
     * Returns the Gun type.
     *
     * @return always "gun"
     */
    @Override
    public @NotNull String getType() {
        return "gun";
    }

    /**
     * Returns the Ammo of this Gun.
     *
     * @return the Ammo
     */
    public final @NotNull Ammo getAmmo() {
        return ammo;
    }

    /**
     * Returns the damage of this Gun.
     *
     * @return the damage
     */
    public final double getDamage() {
        return damage;
    }

    /**
     * Returns the shoot cooldown of this Gun.
     *
     * @return the shoot cooldown in milliseconds
     */
    public final int getShootCooldown() {
        return shootCooldown;
    }

    @Subscribe
    private static void onPlayerInteract(final @NotNull PlayerInteractEvent event) {
        if (!event.hasItem()) return;
        ItemStack item = Objects.requireNonNull(event.getItem());
        Player player = event.getPlayer();
        if (!Gun.exists(item)) return;
        Gun gun = Gun.valueOf(item);
        switch (event.getAction()) {
            case RIGHT_CLICK_AIR -> Gun.shoot(event, gun);
            case RIGHT_CLICK_BLOCK -> {
                Block block = Objects.requireNonNull(event.getClickedBlock());
                if (!player.isSneaking() && block.getType().isInteractable()) return;
                Gun.shoot(event, gun);
            }
            case LEFT_CLICK_AIR -> Gun.zoom(event, gun);
            case LEFT_CLICK_BLOCK -> {
                if (!player.isSneaking()) return;
                Gun.zoom(event, gun);
            }
        }
    }

    private static void shoot(final @NotNull PlayerInteractEvent event, final @NotNull Gun gun) {
        event.setCancelled(true);
        ItemStack item = Objects.requireNonNull(event.getItem());
        AbstractArrow bullet = event.getPlayer().launchProjectile(Arrow.class);
        bullet.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        Bullet.GUN_KEY.write(bullet, gun.getKey());
        Bullet.GUN_DAMAGE.write(bullet, Gun.DAMAGE.read(item)
                .orElseThrow(() -> new IllegalStateException("The gun item is corrupted! Couldn't find damage!")));
        double velocity = Gun.VELOCITY.read(item)
                .orElseThrow(() -> new IllegalStateException("The gun item is corrupted! Couldn't find velocity!"));
        bullet.setVelocity(bullet.getVelocity().multiply(velocity));
    }

    private static void zoom(final @NotNull PlayerInteractEvent event, final @NotNull Gun gun) {
        event.setCancelled(true);

    }

    @Subscribe
    private static void onProjectileHit(final @NotNull ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (!Bullet.GUN_KEY.contains(projectile)) return;

    }

    @Subscribe
    private static void onEntityDamageByEntity(final @NotNull EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (!(event.getDamager() instanceof Projectile projectile) || !Bullet.GUN_KEY.contains(projectile)) {
            target.setMaximumNoDamageTicks(20);
            return;
        }
        double damage = Bullet.GUN_DAMAGE.read(projectile)
                .orElseThrow(() -> new IllegalStateException("The projectile is corrupted! Couldn't find damage!"));
        event.setDamage(damage);
        target.setMaximumNoDamageTicks(0);
    }

    @Subscribe
    private static void onEntityDamage(final @NotNull EntityDamageEvent event) {
        switch (event.getCause()) {
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK, PROJECTILE, ENTITY_EXPLOSION, THORNS, DRAGON_BREATH, SONIC_BOOM -> {
            }
            default -> {
                if (event.getEntity() instanceof LivingEntity livingEntity)
                    livingEntity.setMaximumNoDamageTicks(20);
            }
        }
    }
}
