package com.shaubert.cache.persistance;

import android.util.Log;

import java.io.*;

public class JavaSerializer implements DataSerializer {

    public static final String TAG = JavaSerializer.class.getSimpleName();

    @Override
    public boolean isApplicable(Class<?> clazz) {
        return Serializable.class.isAssignableFrom(clazz)
                || Externalizable.class.isAssignableFrom(clazz);
    }

    @Override
    public <T> void serialize(T data, OutputStream outputStream) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(data);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(Class<T> dataClass, InputStream inputStream) throws IOException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            return (T) objectInputStream.readObject();
        } catch (Throwable e) {
            Log.e(TAG, "deserialization failed", e);
            throw new IOException("deserialization failed");
        }
    }
}
