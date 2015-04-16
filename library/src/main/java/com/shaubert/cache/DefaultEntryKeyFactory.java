package com.shaubert.cache;

import android.text.TextUtils;
import android.util.Log;

public class DefaultEntryKeyFactory implements EntryKeyFactory {

    public static final String TAG = DefaultEntryKeyFactory.class.getSimpleName();

    private static final char SEPARATOR = '/';

    @Override
    public String getKey(Class<?> clazz, String qualifier) {
        if (clazz == null) throw new NullPointerException("cache key-class should not be null");
        return clazz.getName() + SEPARATOR + (qualifier != null ? qualifier : "");
    }

    @Override
    public String getKey(KeyParams keyParams) {
        return getKey(keyParams.getKeyClass(), keyParams.getKeyQualifier());
    }

    @Override
    public KeyParams getKeyParams(String key) {
        if (!TextUtils.isEmpty(key)) {
            String[] args = key.split("[" + SEPARATOR + "]");
            if (args.length == 1 || args.length == 2) {
                try {
                    return new KeyParamsBundle(Class.forName(args[0]), args.length == 2 ? args[1] : null);
                } catch (ClassNotFoundException e) {
                    Log.e(TAG, "key params parsing error", e);
                    throw new IllegalArgumentException("key params parsing error, key: " + key, e);
                }
            }
        }
        throw new IllegalArgumentException("broken key: " + key);
    }

    public static class KeyParamsBundle implements KeyParams {
        private final Class<?> keyClass;
        private final String keyQualifier;

        public KeyParamsBundle(Class<?> keyClass, String keyQualifier) {
            this.keyClass = keyClass;
            this.keyQualifier = keyQualifier;
        }

        @Override
        public Class<?> getKeyClass() {
            return keyClass;
        }

        @Override
        public String getKeyQualifier() {
            return keyQualifier;
        }
    }

}
