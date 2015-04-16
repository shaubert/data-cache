package com.shaubert.cache;

public interface Cache {

    /**
     * @param entryKey key from {@link Entry#getKey() Entry.getKey()}
     * @param <T> data type
     * @return not null {@link com.shaubert.cache.Entry Entry}
     */
    <T> Entry<T> get(String entryKey);

    /**
     * @param cls data class
     * @param qualifier optional qualifier to differ cache entries with same data class
     * @param <T> data type
     * @return not null {@link com.shaubert.cache.Entry Entry}
     */
    <T> Entry<T> get(Class<T> cls, String qualifier);

    /**
     * @param cls data class
     * @param <T> data type
     * @return not null {@link com.shaubert.cache.Entry Entry}
     */
    <T> Entry<T> get(Class<T> cls);

    /**
     * Perform {@link com.shaubert.cache.Procedure action} on every created
     * and existing now {@link com.shaubert.cache.Entry Entry} in cache;
     * @param action action to perform
     */
    void foreach(Procedure<Entry<?>> action);

    /**
     * Call {@link Entry#clear() Entry.clear()} on every matched {@link com.shaubert.cache.Entry Entry}
     * @param filter filter function. Return true to clear entry, false otherwise.
     */
    void clear(Function<Entry<?>, Boolean> filter);

    /**
     * Call {@link Entry#clear() Entry.clear()} on every {@link com.shaubert.cache.Entry Entry}
     */
    void clear();

}
