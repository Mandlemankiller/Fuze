package cz.jeme.programu.fuze.util;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public final class Materials {
    private Materials() {
        throw new AssertionError();
    }

    public static boolean isGlass(final @NotNull Material material) {
        return switch (material) {
            case WHITE_STAINED_GLASS,
                    ORANGE_STAINED_GLASS,
                    MAGENTA_STAINED_GLASS,
                    LIGHT_BLUE_STAINED_GLASS,
                    YELLOW_STAINED_GLASS,
                    LIME_STAINED_GLASS,
                    PINK_STAINED_GLASS,
                    GRAY_STAINED_GLASS,
                    LIGHT_GRAY_STAINED_GLASS,
                    CYAN_STAINED_GLASS,
                    PURPLE_STAINED_GLASS,
                    BLUE_STAINED_GLASS,
                    BROWN_STAINED_GLASS,
                    GREEN_STAINED_GLASS,
                    RED_STAINED_GLASS,
                    BLACK_STAINED_GLASS,
                    WHITE_STAINED_GLASS_PANE,
                    ORANGE_STAINED_GLASS_PANE,
                    MAGENTA_STAINED_GLASS_PANE,
                    LIGHT_BLUE_STAINED_GLASS_PANE,
                    YELLOW_STAINED_GLASS_PANE,
                    LIME_STAINED_GLASS_PANE,
                    PINK_STAINED_GLASS_PANE,
                    GRAY_STAINED_GLASS_PANE,
                    LIGHT_GRAY_STAINED_GLASS_PANE,
                    CYAN_STAINED_GLASS_PANE,
                    PURPLE_STAINED_GLASS_PANE,
                    BLUE_STAINED_GLASS_PANE,
                    BROWN_STAINED_GLASS_PANE,
                    GREEN_STAINED_GLASS_PANE,
                    RED_STAINED_GLASS_PANE,
                    BLACK_STAINED_GLASS_PANE,
                    TINTED_GLASS -> true;
            default -> false;
        };
    }
}
