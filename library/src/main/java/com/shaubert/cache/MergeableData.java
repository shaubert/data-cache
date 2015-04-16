package com.shaubert.cache;

/**
 * Implement this interface if your cached object supports merging.
 * @param <T>
 */
public interface MergeableData<T> {

    /**
     * NOTE: do not modify current object! It may lead to undetermined results
     * in asynchronous save operations in {@link com.shaubert.cache.persistance.DataStorage DataStorage}
     * @param newData newData to merge with
     * @return new merged data object or newData parameter or current object
     */
    T merge(T newData);

}