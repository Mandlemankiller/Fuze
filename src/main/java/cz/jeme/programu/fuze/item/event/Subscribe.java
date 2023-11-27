package cz.jeme.programu.fuze.item.event;

import org.bukkit.event.Event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation marking a method as an event subscriber.
 * The method must have one single parameter that inherits {@link Event}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {
}