package com.scalecart.product.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for selective execution time tracking.
 *
 * Usage: Put @TrackExecutionTime on any method you want monitored.
 * The AOP aspect picks it up automatically.
 *
 * @Target(ElementType.METHOD) — can only be placed on methods
 * @Retention(RetentionPolicy.RUNTIME) — annotation visible at runtime
 *   (needed for AOP — if COMPILE time only, Spring can't see it)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackExecutionTime {
    // Marker annotation — no attributes needed
    // Just its presence on a method triggers the aspect
}