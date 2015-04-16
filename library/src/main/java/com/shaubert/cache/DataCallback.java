package com.shaubert.cache;

/**
 * Callback for asynchronous {@link Entry#getValue() Entry.getValue()} call.
 * Please note, if data is ready this callback mey be called immediately.
 * @param <DATA> returned data type
 */
public interface DataCallback<DATA> {
    /**
     * Called when asynchronous operations is finished or data is ready.
     * @param data data or null
     */
    void onDataResult(DATA data);
}
