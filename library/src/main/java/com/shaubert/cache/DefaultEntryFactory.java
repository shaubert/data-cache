package com.shaubert.cache;


import com.shaubert.cache.persistance.*;

/**
 * Cache {@link com.shaubert.cache.Entry Entry} factory
 */
public class DefaultEntryFactory implements EntryFactory {

    private EntryKeyFactory keyFactory;
    private DataStorage storage;

    /**
     * Cache {@link com.shaubert.cache.Entry Entry} factory without data storage.
     * @param keyFactory factory of {@link com.shaubert.cache.Entry Entry} keys
     */
    public DefaultEntryFactory(EntryKeyFactory keyFactory) {
        this(keyFactory, null);
    }

    /**
     * Cache {@link com.shaubert.cache.Entry Entry} factory.
     * @param keyFactory factory of {@link com.shaubert.cache.Entry Entry} keys
     * @param storage optional entry storage for data classes annotated with
     *                {@link com.shaubert.cache.persistance.PersistableData PersistableData}.
     *                Provide null to prevent storing.
     *
     */
    public DefaultEntryFactory(EntryKeyFactory keyFactory, DataStorage storage) {
        this.keyFactory = keyFactory;
        this.storage = storage;
    }

    @Override
    public <T> Entry<T> createEntry(String key) {
        EntryKeyFactory.KeyParams keyParams = keyFactory.getKeyParams(key);
        @SuppressWarnings("unchecked")
        Class<T> dataClass = (Class<T>) keyParams.getKeyClass();
        DefaultEntry<T> entry = new DefaultEntry<>(key, dataClass);

        if (storage != null) {
            PersistableData persistable = keyParams.getKeyClass().getAnnotation(PersistableData.class);
            if (persistable != null) {
                PersistentEntryCallback<T> callback = this.createPersistableDataCallback(persistable);
                return new PersistableWrapper<>(entry, callback, storage);
            }
        }

        return entry;
    }

    @SuppressWarnings({"unchecked", "TryWithIdenticalCatches"})
    private <T> PersistentEntryCallback<T> createPersistableDataCallback(PersistableData persistable) {
        try {
            return (PersistentEntryCallback<T>) persistable.value().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
