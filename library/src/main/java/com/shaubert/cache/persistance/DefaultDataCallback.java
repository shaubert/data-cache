package com.shaubert.cache.persistance;

import java.io.InputStream;

public class DefaultDataCallback implements FileStorage.DefaultDataCallback {

    @Override
    public InputStream getDefaultDataFor(Class<?> dataClass, String cacheKey, int storageVersion) {
        return null;
    }

    @Override
    public boolean hasDefaultDataFor(Class<?> dataClass, String cacheKey, int storageVersion) {
        return false;
    }

}
