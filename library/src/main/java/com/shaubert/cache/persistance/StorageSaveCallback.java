package com.shaubert.cache.persistance;

/**
 * Callback for storage save operation
 * @param <T>
 */
public interface StorageSaveCallback<T> {

    /**
     * Called if data is saved successfully
     */
    void onSuccess();

    /**
     * Called if data not saved because of error
     */
    void onError();
}
