package com.shaubert.cache.persistance;

import com.shaubert.cache.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PersistableWrapper<DATA> implements AsyncEntry<DATA> {

    private DataStorage storage;

    private boolean waitingForStorage;
    private PersistentEntryCallback<DATA> persistableCallback;

    private Lock getsLock = new ReentrantLock();
    private Set<DataCallback<DATA>> waitingAsyncGets = new HashSet<>();
    private Entry<DATA> originalEntry;
    private boolean mergeable;

    public PersistableWrapper(Entry<DATA> entry, final PersistentEntryCallback<DATA> persistentEntryCallback, DataStorage storage) {
        this.originalEntry = entry;
        this.persistableCallback = persistentEntryCallback;
        this.storage = storage;
        this.mergeable = MergeableData.class.isAssignableFrom(entry.getDataClass());

        waitingForStorage = true;
        storage.load(getDataClass(), getKey(), new StorageLoadCallback<DATA>() {
            @Override
            public void onSuccess(DATA data) {
                mergeWithCurrentData(data);
                finishLoading();
                persistentEntryCallback.onDataLoaded(data);
            }

            @Override
            public void onEmptyResult() {
                finishLoading();
                persistentEntryCallback.onEmptyDataLoaded();
            }

            @Override
            public void onError() {
                finishLoading();
                persistentEntryCallback.onDataLoadingError();
            }
        });
    }

    private void mergeWithCurrentData(DATA dataFromStorage) {
        boolean merged = mergeable && originalEntry.getValue() != null;
        setValue(dataFromStorage);
        if (merged) {
            persist(originalEntry.getValue());
        }
    }

    private void finishLoading() {
        getsLock.lock();
        try {
            waitingForStorage = false;
            if (waitingAsyncGets.isEmpty()) return;

            DATA value = getValue();
            List<DataCallback<DATA>> gets = new ArrayList<>(waitingAsyncGets);
            for (DataCallback<DATA> get : gets) {
                get.onDataResult(value);
            }

            waitingAsyncGets.clear();
        } finally {
            getsLock.unlock();
        }
    }

    @Override
    public void setValue(DATA value) {
        originalEntry.setValue(value);

        DATA mergedValue = getValue();
        if (!waitingForStorage && mergedValue != null) {
            //persist merged result
            persist(mergedValue);
        }
    }

    @Override
    public void setState(DataState state) {
        originalEntry.setState(state);
    }

    @Override
    public String getKey() {
        return originalEntry.getKey();
    }

    @Override
    public Class<DATA> getDataClass() {
        return originalEntry.getDataClass();
    }

    @Override
    public DATA getValue() {
        return waitingForStorage ? null : originalEntry.getValue();
    }

    @Override
    public void getValue(DataCallback<DATA> callback) {
        boolean waiting = false;
        getsLock.lock();
        try {
            if (waitingForStorage) {
                waitingAsyncGets.add(callback);
                waiting = true;
            }
        } finally {
            getsLock.unlock();
        }

        if (!waiting) {
            callback.onDataResult(getValue());
        }
    }

    public void persist() {
        DATA value = getValue();
        if (!waitingForStorage && value != null) {
            persist(value);
        }
    }

    private void persist(final DATA data) {
        storage.save(data, getKey(), new StorageSaveCallback<DATA>() {
            @Override
            public void onSuccess() {
                persistableCallback.onDataSaved(data);
            }

            @Override
            public void onError() {
                persistableCallback.onDataSavingError();
            }
        });
    }

    public void clearCache() {
        //ignoring negative case
        storage.delete(getDataClass(), getKey());
    }

    @Override
    public boolean hasValue() {
        return waitingForStorage || originalEntry.hasValue();
    }

    @Override
    public boolean isIdle() {
        return !waitingForStorage && originalEntry.isIdle();
    }

    @Override
    public boolean isUpdating() {
        return waitingForStorage || originalEntry.isUpdating();
    }

    @Override
    public boolean isFailed() {
        return false;
    }

    @Override
    public void clear() {

    }

}