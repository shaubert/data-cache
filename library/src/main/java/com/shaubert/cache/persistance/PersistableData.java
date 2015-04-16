package com.shaubert.cache.persistance;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate your class with {@link com.shaubert.cache.persistance.PersistableData PersistableData} to put
 * cached objects into persistable storage
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface PersistableData {
    Class<? extends PersistentEntryCallback> value();
}
