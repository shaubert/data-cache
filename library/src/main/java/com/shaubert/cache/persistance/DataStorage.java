package com.shaubert.cache.persistance;

/**
 * Persistable storage for data objects annotated with
 * {@link com.shaubert.cache.persistance.PersistableData PersistableData}
 */
public interface DataStorage {

    /**
     * Save data to storage
     * @param data not null data
     * @param key cache entry key
     * @param callback optional result callback
     * @param <T> data type
     */
    <T> void save(T data, String key, StorageSaveCallback<T> callback);

    /**
     * Load data from storage
     * @param dataClass class of loading data
     * @param key cache entry key
     * @param callback result callback
     * @param <T> data type
     */
    <T> void load(Class<T> dataClass, String key, StorageLoadCallback<T> callback);

    /**
     * Delete entry from storage
     * @param dataClass data class
     * @param key cache entry key
     * @param <T> data type
     */
    <T> void delete(Class<T> dataClass, String key);

}
