package com.shaubert.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultCache implements Cache {

    private Map<String, Entry<?>> cache = new HashMap<>();
    private CacheKeyFactory keyProducer;
    private EntryFactory entryFactory;

    public DefaultCache(CacheKeyFactory keyProducer, EntryFactory entryFactory) {
        this.keyProducer = keyProducer;
        this.entryFactory = entryFactory;
    }

    @SuppressWarnings("unchecked")
    private <T> Entry<T> getOrCreateEntry(String key) {
        Entry<T> res = (Entry<T>) cache.get(key);
        if (res == null) {
            res = entryFactory.createEntry(key);
            cache.put(key, res);
        }

        return res;
    }

    protected <T> String getCacheKey(Class<T> cls, String qualifier) {
        return keyProducer.getKey(cls, qualifier);
    }

    @Override
    public <T> Entry<T> get(String entryKey) {
        return getOrCreateEntry(entryKey);
    }

    @Override
    public <T> Entry<T> get(Class<T> cls, String qualifier) {
        return getOrCreateEntry(getCacheKey(cls, qualifier));
    }

    @Override
    public <T> Entry<T> get(Class<T> cls) {
        return get(cls, null);
    }

    @Override
    public void foreach(Procedure<Entry<?>> action) {
        List<Entry<?>> temp = new ArrayList<>(cache.values());
        for (Entry<?> entry : temp) {
            action.perform(entry);
        }
    }

    @Override
    public void clear(Function<Entry<?>, Boolean> filter) {
        List<Entry> temp = new ArrayList<Entry>(cache.values());
        for (Entry entry : temp) {
            if (filter == null || filter.apply(entry)) {
                entry.clear();
                cache.remove(entry.getKey());
            }
        }
    }

    @Override
    public void clear() {
        clear(null);
    }
}
