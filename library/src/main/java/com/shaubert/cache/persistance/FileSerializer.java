package com.shaubert.cache.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileSerializer {

    boolean isApplicable(Class<?> clazz);

    <T> void serialize(T data, OutputStream outputStream) throws IOException;

    <T> T deserialize(Class<T> dataClass, InputStream inputStream) throws IOException;

}
