package com.shaubert.cache;

/**
 * Class converting data class and optional qualifier to {@link java.lang.String String} key and reverse. Key will be
 * used as unique identifier of cache {@link com.shaubert.cache.Entry Entry}.
 */
public interface EntryKeyFactory {

    /**
     * Unique parameters of cached {@link com.shaubert.cache.Entry Entry}
     */
    interface KeyParams {
        Class<?> getKeyClass();
        String getKeyQualifier();
    }

    /**
     * @param clazz class of cached data
     * @param qualifier qualifier of cached data
     * @return unique key that can be converted back to {@link EntryKeyFactory.KeyParams KeyParams}
     */
    String getKey(Class<?> clazz, String qualifier);


    /**
     * @param keyParams cache entry parameters
     * @return unique key that can be converted back to {@link EntryKeyFactory.KeyParams KeyParams}
     */
    String getKey(KeyParams keyParams);

    /**
     * Converts {@link java.lang.String String} key back to {@link EntryKeyFactory.KeyParams KeyParams}.
     * @param key produced by this factory
     * @return {@link EntryKeyFactory.KeyParams KeyParams}
     */
    KeyParams getKeyParams(String key);

}
