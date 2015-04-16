package com.shaubert.cache.persistance;

public class SimplePersistentEntryCallback<DATA> implements PersistentEntryCallback<DATA> {
    @Override
    public void onDataLoaded(DATA data) {
    }

    @Override
    public void onDataLoadingError() {
    }

    @Override
    public void onEmptyDataLoaded() {
    }

    @Override
    public void onDataSaved(DATA data) {
    }

    @Override
    public void onDataSavingError() {
    }
}
