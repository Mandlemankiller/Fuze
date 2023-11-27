package cz.jeme.programu.fuze.item.event;

import cz.jeme.programu.fuze.Fuze;
import cz.jeme.programu.fuze.item.FuzeItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Manages all event registration in the Fuze plugin.
 */
public enum EventManager implements Listener {
    /**
     * The one and only {@link EventManager}.
     */
    INSTANCE;

    private record Subscription(@NotNull Method subscribeMethod, @NotNull FuzeItem fuzeItem) {
        @Override
        public @NotNull String toString() {
            return "subscription %s on %s"
                    .formatted(subscribeMethod.toString(), fuzeItem.toString());
        }
    }

    private final @NotNull Map<Class<? extends Event>, Set<Subscription>> events = new HashMap<>();
    private final @NotNull Set<Class<? extends FuzeItem>> subscribers = new HashSet<>();
    private final @NotNull RegisteredListener listener;

    EventManager() {
        listener = new RegisteredListener(
                this,
                (listener, event) -> distributeEvent(event),
                EventPriority.NORMAL,
                Fuze.getPlugin(),
                false
        );

        HandlerList.getHandlerLists()
                .forEach((handlerList) -> handlerList.register(listener));
    }

    private void distributeEvent(final @NotNull Event event) {
        Set<Subscription> subscriptions = events.get(event.getClass());
        if (subscriptions == null) return;
        subscriptions.forEach(sub -> {
            try {
                sub.subscribeMethod().invoke(sub.fuzeItem(), event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Couldn't invoke subscribe method: " + sub.subscribeMethod().getName(), e);
            }
        });
    }

    /**
     * Registers all events in a fuze item Class.
     *
     * @param itemClass the fuze item Class.
     * @param fuzeItem  the concrete implementation of the fuze item Class
     * @param force     whether to force the registration even when the Class was already registered before
     * @param <T>       the fuze item
     */
    public <T extends FuzeItem> void registerEvents(final @NotNull Class<? extends FuzeItem> itemClass, final @NotNull T fuzeItem, final boolean force) {
        if (!force && subscribers.contains(itemClass)) return;
        for (Method method : itemClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) continue;
            method.setAccessible(true);
            if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                throw new IllegalStateException(
                        "A subscribe method doesn't have only one (? extends %s) parameter: %s"
                                .formatted(Event.class.getName(), method.getName())
                );
            @SuppressWarnings("unchecked") Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
            events.computeIfAbsent(eventClass, event -> new HashSet<>());
            events.get(eventClass).add(new Subscription(method, fuzeItem));
        }
        subscribers.add(itemClass);
    }

    /**
     * Registers all events in all superclasses of the provided item going all the way up to {@link FuzeItem}.
     *
     * @param fuzeItem the concrete implementation of fuze item to register
     * @param force    whether to force the registration of all Classes even when the Class was already registered before
     * @param <T>      the fuze item
     */
    public <T extends FuzeItem> void registerEventsTree(final @NotNull T fuzeItem, final boolean force) {
        Class<? extends FuzeItem> regClass = fuzeItem.getClass();

        while (true) {
            registerEvents(regClass, fuzeItem, force);
            if (regClass == FuzeItem.class) return;
            @SuppressWarnings("unchecked") Class<? extends FuzeItem> parent = (Class<? extends FuzeItem>) regClass.getSuperclass();
            regClass = parent;
        }
    }
}
