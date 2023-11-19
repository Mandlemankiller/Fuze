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
import java.lang.reflect.Modifier;
import java.util.*;

public enum EventManager implements Listener {
    INSTANCE;
    private final @NotNull Map<Class<? extends Event>, Set<Method>> events = new HashMap<>();
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
        Set<Method> methods = events.get(event.getClass());
        if (methods == null) return;
        methods.forEach(method -> {
            try {
                method.invoke(null, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Couldn't invoke subscribe method: " + method.getName(), e);
            }
        });
    }

    public void registerEvents(final @NotNull Class<? extends FuzeItem> itemClass, final boolean force) {
        if (!force && subscribers.contains(itemClass)) return;
        for (Method method : itemClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) continue;
            method.setAccessible(true);
            if (!Modifier.isStatic(method.getModifiers()))
                throw new IllegalStateException("A subscribe method is not static: " + method.getName());
            if (method.getParameterCount() != 1 || !Event.class.isAssignableFrom(method.getParameterTypes()[0]))
                throw new IllegalStateException(
                        "A subscribe method doesn't have only one (? extends %s) parameter: %s"
                                .formatted(Event.class.getName(), method.getName())
                );
            @SuppressWarnings("unchecked") Class<? extends Event> eventClass = (Class<? extends Event>) method.getParameterTypes()[0];
            events.computeIfAbsent(eventClass, event -> new HashSet<>());
            events.get(eventClass).add(method);
        }
        subscribers.add(itemClass);
    }

    public void registerEventsTree(final @NotNull Class<? extends FuzeItem> itemClass, final boolean force) {
        Class<? extends FuzeItem> regClass = itemClass;

        while (true) {
            registerEvents(regClass, force);
            if (regClass == FuzeItem.class) return;
            @SuppressWarnings("unchecked") Class<? extends FuzeItem> parent = (Class<? extends FuzeItem>) regClass.getSuperclass();
            regClass = parent;
        }
    }
}
