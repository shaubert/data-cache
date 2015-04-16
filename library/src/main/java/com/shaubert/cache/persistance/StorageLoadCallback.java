package com.shaubert.cache.persistance;

/**
 * Callback for storage load operation
 * @param <T>
 */
public interface StorageLoadCallback<T> {
    /**
     * If data is successfully loaded from storage
     * @param data loaded data
     */
    void onSuccess(T data);

    /**
     * Called if storage doesn't contains requested data
     */
    void onEmptyResult();

    /**
     * Called if loading failed
     */
    void onError();
}
