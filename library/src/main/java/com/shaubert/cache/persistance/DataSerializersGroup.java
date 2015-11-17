package com.shaubert.cache.persistance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataSerializersGroup implements DataSerializer {

    private List<DataSerializer> dataSerializers = new ArrayList<DataSerializer>();

    public void add(DataSerializer serializer) {
        dataSerializers.add(serializer);
    }

    public void remove(DataSerializer serializer) {
        dataSerializers.remove(serializer);
    }

    @Override
    public boolean isApplicable(Class<?> clazz) {
        return getApplicable(clazz) != null;
    }

    private DataSerializer getApplicable(Class<?> clazz) {
        for (DataSerializer serializer : dataSerializers) {
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
