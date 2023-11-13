package cz.jeme.programu.fuze;

import cz.jeme.programu.fuze.item.FuzeItem;
import cz.jeme.programu.fuze.item.FuzeItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents the main Fuze command.
 */
public final class FuzeCommand extends Command {
    private static @Nullable FuzeCommand instance;

    /**
     * Initializes Fuze command. If Fuze command was already initialized, it will fail silently.
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @return true when Fuze command was successfully initialized, otherwise false
     */
    public static synchronized boolean init() {
        if (instance == null) {
            instance = new FuzeCommand();
            return true;
        }
        return false;
    }

    /**
     * Returns the instance of the main Fuze command.
     * <p>Fuze command must be initialized before calling this method.</p>
     *
     * @return Fuze command instance
     * @throws IllegalStateException when Fuze command was not initialized before calling this method
     */
    public static synchronized @NotNull FuzeCommand instance() {
        if (instance == null)
            throw new IllegalStateException("The FuzeCommand was not initialized yet!");
        return instance;
    }

    /**
     * Parameter string used in the Fuze command to select all players online on the server.
     */
    public static final @NotNull String EVERYONE_SELECTOR = "@everyone";

    private FuzeCommand() {
        super(
                "fuze",
                "The main Fuze operation command",
                "A false statement occurred when handling the command!",
                List.of("fz")
        );
        setPermission("fuze.fuze");
        Bukkit.getCommandMap().register("fuze", this);
    }

    /**
     * Executed on tab completion for the Fuze command, returning a list of
     * options the player can tab through.
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @param sender source object which is executing this command
     * @param alias  the alias being used
     * @param args   all arguments passed to the command, split via ' '
     * @return a list of tab-completions for the specified arguments. This
     * will never be null. List may be immutable.
     */
    @Override
    public @NotNull List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String alias, final @NotNull String[] args) {
        int length = args.length;
        if (length == 1)
            return Action.toStringList(
                    Action.GIVE,
                    Action.HELP,
                    Action.RELOAD
            );

        return switch (Action.from(args[0])) {
            case GIVE -> switch (length) { // fz give ...
                case 2 -> { // fz give <player>
                    List<String> players = Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .collect(Collectors.toList());
                    players.add(EVERYONE_SELECTOR);
                    yield players;
                }

                case 3 -> new ArrayList<>(FuzeItemManager.INSTANCE.getItemTypes()); // fz give player <type>

                case 4 -> { // fz give player type <item>
                    String type = args[2];
                    if (!FuzeItemManager.INSTANCE.getItemTypes().contains(type)) yield List.of();
                    Optional<FuzeItemManager.ItemRegistry<? extends FuzeItem>> o = FuzeItemManager.INSTANCE.getRegistryByType(type);
                    if (o.isEmpty()) yield List.of();
                    yield new ArrayList<>(o.get().getKeys());
                }

                default -> List.of();
            };


            default -> List.of();
        };
    }

    /**
     * Executes the fuze command
     * <p>This method should not be called outside the Fuze API.</p>
     *
     * @param sender       source object which is executing this command
     * @param commandLabel the alias of the command used
     * @param args         all arguments passed to the command, split via ' '
     * @return always true
     */
    @Override
    public boolean execute(final @NotNull CommandSender sender, final @NotNull String commandLabel, final @NotNull String[] args) {
        execute(new Execution(sender, commandLabel, args));
        return true;
    }

    private static void execute(final @NotNull Execution execution) {
        switch (Action.from(execution.args()[0])) {
            case RELOAD -> reload(execution);
            case GIVE -> give(execution);
            case HELP -> usage(execution.sender());
            case UNKNOWN -> execution.sender()
                    .sendMessage(Message.prefix("<red>Unknown action: " + execution.args[0]));
        }
    }

    private static void reload(final @NotNull Execution execution) {
        final CommandSender sender = execution.sender();
        try {
            Config.instance().reload();
        } catch (Exception e) {
            sender.sendMessage(Message.prefix("<red>An error occurred while reloading the plugin! Please check the console!"));
            throw e;
        }
        sender.sendMessage(Message.prefix("<green>Plugin reloaded successfully!"));
    }

    private static void give(final @NotNull Execution execution) {
        final CommandSender sender = execution.sender();

        if (execution.args().length < 4) {
            sender.sendMessage(Message.prefix("<red>Not enough arguments!"));
            usage(sender);
            return;
        }

        if (execution.args().length > 5) {
            sender.sendMessage(Message.prefix("<red>Too many arguments!"));
            usage(sender);
            return;
        }

        List<Player> players = new ArrayList<>();

        String playerName = execution.args()[1];
        if (playerName.equals(EVERYONE_SELECTOR)) {
            players.addAll(Bukkit.getOnlinePlayers());
        } else {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                sender.sendMessage(Message.prefix("<red>Player \"" + playerName + "\" is not online!"));
                return;
            }
            players.add(player);
        }

        String type = execution.args()[2];

        Optional<Class<? extends FuzeItem>> optionalItemClass = FuzeItemManager.INSTANCE.typeToItemClass(type);
        if (optionalItemClass.isEmpty()) {
            sender.sendMessage(Message.prefix("<red>Unknown item type: " + type));
            return;
        }
        Class<? extends FuzeItem> itemClass = optionalItemClass.get();


        String key = execution.args()[3];
        Optional<? extends FuzeItem> optionalItem = FuzeItemManager.INSTANCE.getItemByKey(key, itemClass);
        if (optionalItem.isEmpty()) {
            sender.sendMessage(Message.prefix("<red>Unknown item name: " + key));
            return;
        }
        ItemStack item = optionalItem.get().getItem();

        int amount = 1;
        if (execution.args().length == 5) {
            String amountStr = execution.args()[4];
            boolean valid = true;
            try {
                amount = Integer.parseInt(amountStr);
                if (amount <= 0) valid = false;
            } catch (NumberFormatException ignored) {
                valid = false;
            }
            if (!valid) {
                sender.sendMessage(Message.prefix("<red>Invalid item amount: " + amountStr));
                return;
            }
        }


        for (Player player : players) {
            List<ItemStack> exceeded = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                exceeded.addAll(player.getInventory().addItem(item).values());
            }
            exceeded.forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
        }
    }

    private static void usage(final @NotNull CommandSender sender) {
        // TODO!
        sender.sendMessage(Message.deserialize("<red>This is a usage!"));
    }

    private record Execution(@NotNull CommandSender sender,
                             @NotNull String commandLabel,
                             @NotNull String[] args) {
    }

    private enum Action {
        GIVE("give"),
        RELOAD("reload"),
        HELP("help"),
        DRAGON("dragon"),
        UNKNOWN("UNKNOWN");

        private final @NotNull String name;

        Action(final @NotNull String name) {
            this.name = name;
        }


        @Override
        public @NotNull String toString() {
            return name;
        }

        public static @NotNull Action from(final @NotNull String name) {
            return Arrays.stream(values())
                    .filter(action -> action.toString().equals(name))
                    .findFirst()
                    .orElse(Action.UNKNOWN);
        }

        public static @NotNull List<String> toStringList(final @NotNull Action... actions) {
            return Arrays.stream(actions).map(Action::toString).toList();
        }
    }
}
