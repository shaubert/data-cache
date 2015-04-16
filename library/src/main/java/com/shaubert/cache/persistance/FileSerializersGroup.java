package com.shaubert.cache.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileSerializersGroup implements FileSerializer {

    private List<FileSerializer> fileSerializers = new ArrayList<FileSerializer>();

    public void add(FileSerializer serializer) {
        fileSerializers.add(serializer);
    }

    public void remove(FileSerializer serializer) {
        fileSerializers.remove(serializer);
    }

    @Override
    public boolean isApplicable(Class<?> clazz) {
        return getApplicable(clazz) != null;
    }

    private FileSerializer getApplicable(Class<?> clazz) {
        for (FileSerializer serializer : fileSerializers) {
            if (serializer.isApplicable(clazz)) {
                return serializer;
            }
        }
        throw new IllegalArgumentException("unable to find applicable serializer for class: " + clazz);
    }

    @Override
    public <T> void serialize(T data, OutputStream outputStream) throws IOException {
        getApplicable(data.getClass()).serialize(data, outputStream);
    }

    @Override
    public <T> T deserialize(Class<T> dataClass, InputStream inputStream) throws IOException {
        return getApplicable(dataClass).deserialize(dataClass, inputStream);
    }

}
