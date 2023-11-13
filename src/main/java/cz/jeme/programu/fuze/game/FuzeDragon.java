package cz.jeme.programu.fuze.game;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// TODO
public class FuzeDragon extends EnderDragon {
    public static final double HORIZONTAL_SPEED = 1;
    public static final double VERTICAL_SPEED = 1;
    private final @NotNull List<Player> players;

    public FuzeDragon(final @NotNull Level world, final @NotNull List<Player> players) {
        super(EntityType.ENDER_DRAGON, world);
        this.players = players;
        world.addFreshEntity(this);
        players.forEach(getBukkitEntity()::addPassenger);
    }

    @Override
    public void tick() {
        super.tick();
//
//        final float yaw = player.getYaw();
//        final float pitch = player.getPitch();
//
//        final double x = Math.sin(Math.toRadians(yaw)) * HORIZONTAL_SPEED * -1;
//        final double z = Math.cos(Math.toRadians(yaw)) * HORIZONTAL_SPEED;
//        final double y = VERTICAL_SPEED / -90 * pitch;
//
//        setPos(getX() + x, getY() + y, getZ() + z);
//        setRot(yaw + 180, pitch);
    }
}
