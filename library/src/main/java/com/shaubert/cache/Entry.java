package com.shaubert.cache;

public interface Entry<DATA> {

    /**
     * @return Entry unique key for mapping in {@link com.shaubert.cache.Cache Cache}
     */
    String getKey();

    /**
     * @return {@link java.lang.Class Class} associated with this cache entry — value class.
     */
    Class<DATA> getDataClass();

    /**
     * @return cached value or null
     */
    DATA getValue();

    /**
     * Put value in cache entry. If value implements {@link com.shaubert.cache.MergeableData MergeableData} than new value
     * will be merged with previous value (if exists).
     * @param value new value
     */
    void setValue(DATA value);

    /**
     * Set cache entry state
     * @param state
     */
    void setState(DataState state);

    /**
     * @return true if value not null, false otherwise
     */
    boolean hasValue();

    /**
     * @return true if current {@link com.shaubert.cache.DataState DataState} == {@link com.shaubert.cache.DataState#IDLE IDLE}
     */
    boolean isIdle();

    /**
     * @return true if current {@link com.shaubert.cache.DataState DataState} == {@link com.shaubert.cache.DataState#UPDATE UPDATE}
     */
    boolean isUpdating();

    /**
     * @return true if current {@link com.shaubert.cache.DataState DataState} == {@link com.shaubert.cache.DataState#FAIL FAIL}
     */
    boolean isFailed();

    /**
     * Reset value and state
     */
    void clear();
}
