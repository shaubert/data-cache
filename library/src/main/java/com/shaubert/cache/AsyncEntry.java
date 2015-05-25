package com.shaubert.cache;

public interface AsyncEntry<DATA> extends Entry<DATA> {

    void getValue(DataCallback<DATA> callback);

}
