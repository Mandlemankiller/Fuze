package cz.jeme.programu.fuze;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * The main Fuze plugin class.
 */
public final class Fuze extends JavaPlugin {

    /**
     * Plugin enable logic.
     */
    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config.init(this);
        Config.instance().reload();
        FuzeCommand.init(); // Initialize Fuze command
    }

    /**
     * Plugin disable logic.
     */
    @Override
    public void onDisable() {
    }

    /**
     * This method provides fast Waccess to the Fuze plugin object.
     *
     * @return the plugin object
     */
    public static @NotNull Fuze getPlugin() {
        return JavaPlugin.getPlugin(Fuze.class);
    }
}
