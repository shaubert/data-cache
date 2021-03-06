package com.shaubert.cache.persistance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import java.io.*;

/**
 * Persistent storage based on files
 */
public class FileStorage implements DataStorage {


    public static final String TAG = FileStorage.class.getSimpleName();

    private static final String STORAGE_VERSION = "data-storage-version";

    private static final String CACHE_DIR_FILENAME = Hashes.getSHA1("data-storage");

    private final int version;
    private volatile boolean inited;
    private Context context;
    private DataSerializer dataSerializer;
    private DefaultDataCallback defaultDataCallback;
    private SharedPreferences storagePrefs;
    private boolean debugMode = true;

    private FileStorage(Builder builder) {
        this(builder.context, builder.version, builder.debugMode, builder.dataSerializer, builder.defaultDataCallback);
    }

    /**
     * @param context context
     * @param storageVersion version of data storage. If version > current version, storage will be cleared.
     * @param debugMode true if you want log messages and exceptions on serialization errors, false otherwise
     * @param dataSerializer serializer for objects
     * @param defaultDataCallback optional callback to override data loading
     */
    public FileStorage(Context context, int storageVersion, boolean debugMode,
                       DataSerializer dataSerializer, DefaultDataCallback defaultDataCallback) {
        this.version = storageVersion;
        this.context = context.getApplicationContext();
        this.debugMode = debugMode;
        this.dataSerializer = dataSerializer;
        this.defaultDataCallback = defaultDataCallback;

        initCacheIfNeeded();
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    @SuppressLint("CommitPrefEdits")
    private void initCacheIfNeeded() {
        if (inited) {
            return;
        }

        File cacheDir = getCacheDir();
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            if (debugMode) Log.w(TAG, "unable to create cache dir");
            return;
        }

        if (!Files.createNomedia(cacheDir)) {
            return;
        }

        SharedPreferences preferences = getPreferences();
        int oldVersion = preferences.getInt(STORAGE_VERSION, 0);
        if (oldVersion < version) {
            if (!cleanUpCache()) {
                if (debugMode) Log.w(TAG, "unable to clean up cache dir");
                return;
            }
            preferences.edit().putInt(STORAGE_VERSION, version).commit();
        }

        inited = true;
    }

    private SharedPreferences getPreferences() {
        if (storagePrefs == null) {
            storagePrefs = context.getSharedPreferences("__sh_data_storage_prefs", Context.MODE_PRIVATE);
        }
        return storagePrefs;
    }

    @Override
    public <T> void save(T data, String key, final StorageSaveCallback<T> callback) {
        initCacheIfNeeded();

        removeDeletedMark(data.getClass(), key);

        new SerializationAsyncTask<>(data,
                version,
                callback,
                dataSerializer,
                convertToFileName(key),
                debugMode)
            .executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    @Override
    public <T> void load(Class<T> dataClass, String key, StorageLoadCallback<T> callback) {
        initCacheIfNeeded();
        if (isMarkedAsDeleted(dataClass, key)) {
            delete(dataClass, key);
            if (callback != null) callback.onEmptyResult();
        } else {
            new SerializationAsyncTask<>(dataClass,
                    version,
                    key,
                    callback,
                    defaultDataCallback,
                    dataSerializer,
                    convertToFileName(key),
                    debugMode)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public <T> void delete(Class<T> dataClass, String key) {
        if (!delete(convertToFileName(key))) {
            markAsDeleted(dataClass, key);
        } else {
            removeDeletedMark(dataClass, key);
        }
    }

    private <T> void markAsDeleted(Class<T> dataClass, String qualifier) {
        getPreferences().edit().putBoolean("deleted-" + dataClass.getName() + qualifier, true).commit();
    }

    private <T> boolean isMarkedAsDeleted(Class<T> dataClass, String qualifier) {
        return getPreferences().getBoolean("deleted-" + dataClass.getName() + qualifier, false);
    }

    private <T> void removeDeletedMark(Class<T> dataClass, String qualifier) {
        getPreferences().edit().remove("deleted-" + dataClass.getName() + qualifier).commit();
    }

    protected boolean delete(File file) {
        if (file.exists()) {
            return file.delete();
        } else {
            return true;
        }
    }

    protected File convertToFileName(String key) {
        return new File(getCacheDir(), Hashes.getSHA1(key));
    }

    private boolean cleanUpCache() {
        File cacheDir = getCacheDir();
        if (cacheDir.exists()) {
            return Files.deleteDir(cacheDir);
        } else {
            return true;
        }
    }

    private File getCacheDir() {
        return new File(context.getFilesDir(), CACHE_DIR_FILENAME);
    }

    private static class SerializationAsyncTask<T> extends AsyncTask<Void, Void, Boolean> {

        public final String TAG = SerializationAsyncTask.class.getSimpleName();

        private byte[] dataBytes;
        private final int storageVersion;
        private Class<T> dataClass;
        private String key;
        private StorageSaveCallback<T> saveCallback;
        private StorageLoadCallback<T> loadCallback;
        private DataSerializer serializer;
        private File dataFile;
        private T deserializedResult;
        private DefaultDataCallback defaultDataCallback;
        private boolean debugMode;

        @SuppressWarnings("unchecked")
        public SerializationAsyncTask(T data, int storageVersion, StorageSaveCallback<T> callback,
                                      DataSerializer serializer, File output, boolean debugMode) {
            this.storageVersion = storageVersion;
            this.debugMode = debugMode;
            this.dataClass = (Class<T>) data.getClass();
            this.saveCallback = callback;
            this.serializer = serializer;
            this.dataFile = output;

            makeDataBytes(data);
        }

        public SerializationAsyncTask(Class<T> dataClass, int storageVersion, String key, StorageLoadCallback<T> callback,
                                      DefaultDataCallback defaultDataCallback, DataSerializer serializer, File input,
                                      boolean debugMode) {
            this.dataClass = dataClass;
            this.storageVersion = storageVersion;
            this.loadCallback = callback;
            this.serializer = serializer;
            this.dataFile = input;
            this.key = key;
            this.defaultDataCallback = defaultDataCallback;
            this.debugMode = debugMode;
        }

        private void makeDataBytes(T data) {
            if (!serializer.isApplicable(dataClass)) return;

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(1024 * 8);
            try {
                serializer.serialize(data, outputStream);
                dataBytes = outputStream.toByteArray();
            } catch (IOException ex) {
                if (debugMode) Log.e(TAG, "failed to make copy of " + data, ex);
            } catch (Throwable ex) {
                if (debugMode) Log.e(TAG, "unknown error when copying " + data, ex);
                if (debugMode) {
                    throw new RuntimeException(ex);
                }
            } finally {
                if (dataBytes == null) {
                    cancel(false);
                }
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (isCancelled()) return false;

            if (serializer.isApplicable(dataClass)) {
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    long startTime = SystemClock.uptimeMillis();
                    if (dataBytes != null) {
                        outputStream = new FileOutputStream(dataFile);
                        outputStream.write(dataBytes);
                    } else {
                        boolean overrideLoading = defaultDataCallback != null
                                && defaultDataCallback.hasDefaultDataFor(dataClass, key, storageVersion);
                        if (!overrideLoading && dataFile.exists() && dataFile.length() > 0) {
                            inputStream = new FileInputStream(dataFile);
                        } else if (overrideLoading || defaultDataCallback != null) {
                            inputStream = defaultDataCallback.getDefaultDataFor(dataClass, key, storageVersion);
                        }
                        if (inputStream != null) {
                            deserializedResult = serializer.deserialize(dataClass, inputStream);
                        }
                    }
                    if (debugMode) Log.d(TAG, String.format("%s of %s: time = %dms, size = %db",
                            dataBytes == null ? "deserialization" : "serialization",
                            dataClass.getSimpleName(),
                            SystemClock.uptimeMillis() - startTime,
                            dataFile.exists() ? dataFile.length() : 0));
                    return true;
                } catch (FileNotFoundException e) {
                    if (debugMode) Log.e(TAG, "cache entry not found for " + dataClass, e);
                } catch (IOException ex) {
                    if (debugMode) Log.e(TAG, "failed to perform cache operation for " + dataClass, ex);
                } catch (Throwable ex) {
                    if (debugMode) Log.e(TAG, "unknown error when processing " + dataClass, ex);
                    if (debugMode) {
                        throw new RuntimeException(ex);
                    }
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (saveCallback != null || loadCallback != null) {
                if (result) {
                    if (dataBytes != null) {
                        if (saveCallback != null) saveCallback.onSuccess();
                    } else {
                        if (deserializedResult == null) {
                            if (loadCallback != null) loadCallback.onEmptyResult();
                        } else {
                            if (loadCallback != null) loadCallback.onSuccess(deserializedResult);
                        }
                    }
                } else {
                    if (dataBytes != null) {
                        if (saveCallback != null) saveCallback.onError();
                    } else {
                        if (loadCallback != null) loadCallback.onError();
                    }
                }
            }
        }

        @Override
        protected void onCancelled() {
            if (saveCallback != null) saveCallback.onError();
            if (loadCallback != null) loadCallback.onError();
        }
    }

    public interface DefaultDataCallback {

        InputStream getDefaultDataFor(Class<?> dataClass, String cacheKey, int storageVersion);

        boolean hasDefaultDataFor(Class<?> dataClass, String cacheKey, int storageVersion);

    }

    public static final class Builder {
        private int version = -1;
        private Context context;
        private DataSerializer dataSerializer;
        private DefaultDataCallback defaultDataCallback;
        private boolean debugMode;

        private Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * @param version version of data storage. If version > current version, storage will be cleared.
         * @return this
         */
        public Builder version(int version) {
            this.version = version;
            return this;
        }

        /**
         * @param dataSerializer fileSerializer serializer for objects. By default it's {@link com.shaubert.cache.persistance.JavaSerializer JavaSerializer}
         * @return this
         */
        public Builder dataSerializer(DataSerializer dataSerializer) {
            this.dataSerializer = dataSerializer;
            return this;
        }

        /**
         * @param defaultDataCallback optional callback to override data loading
         * @return this
         */
        public Builder defaultDataCallback(DefaultDataCallback defaultDataCallback) {
            this.defaultDataCallback = defaultDataCallback;
            return this;
        }

        /**
         * @param debugMode debugMode true if you want log messages and exceptions on serialization errors, false otherwise
         * @return this
         */
        public Builder debugMode(boolean debugMode) {
            this.debugMode = debugMode;
            return this;
        }

        /**
         * Build storage or throw exception if version not set
         * @return created storage
         */
        public FileStorage build() {
            if (version == -1) {
                throw new IllegalArgumentException("provide storage version");
            }
            if (dataSerializer == null) dataSerializer = new JavaSerializer();
            return new FileStorage(this);
        }
    }
}
