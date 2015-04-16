package com.shaubert.cache;

public class DefaultEntry<DATA> implements Entry<DATA> {
    private final String key;
    private final Class<DATA> dataClass;

    private DataState state;
    private DATA value;

    public DefaultEntry(String key, Class<DATA> dataClass) {
        this.key = key;
        this.dataClass = dataClass;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DATA getValue() {
        return value;
    }

    @Override
    public void setValue(DATA value) {
        this.value = value;
    }

    @Override
    public void setState(DataState state) {
        this.state = state;
    }

    @Override
    public boolean hasValue() {
        return value == null;
    }

    @Override
    public boolean isIdle() {
        return state == DataState.IDLE;
    }

    @Override
    public boolean isUpdating() {
        return state == DataState.UPDATE;
    }

    @Override
    public boolean isFailed() {
        return state == DataState.FAIL;
    }

    @Override
    public void clear() {
        value = null;
        state = DataState.IDLE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Class<DATA> getDataClass() {
        return dataClass;
    }

}
