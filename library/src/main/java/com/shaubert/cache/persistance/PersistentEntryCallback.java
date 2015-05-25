package com.shaubert.cache.persistance;

/**
 * Callback for persistent cache entry.
 * @param <DATA> cache entry value class
 */
public interface PersistentEntryCallback<DATA> {
    /**
     * Called when data is loaded from persistent storage and optionally merged with data in cache entry (if after loading
     * from storage cache entry already contains value). To perform merge <DATA> have to implement
     * {@link com.shaubert.cache.MergeableData MergableData} interface.
     * @param data read and optionally merged data
     */
    void onDataLoaded(DATA data);

    /**
     * Called if attempt to load data from storage failed. Note that this method not called if result is empty.
     */
    void onDataLoadingError();

    /**
     * Called if there is no data in storage.
     */
    void onEmptyDataLoaded();

    /**
     * Called after successful data save.
     * @param data saved data
     */
    void onDataSaved(DATA data);

    /**
     * Called if attempt to save data failed.
     */
    void onDataSavingError();
}
