package com.shaubert.cache.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface FileSerializer {

    public boolean isApplicable(Class<?> clazz);

    public <T> void serialize(T data, OutputStream outputStream) throws IOException;

    public <T> T deserialize(Class<T> dataClass, InputStream inputStream) throws IOException;

}
