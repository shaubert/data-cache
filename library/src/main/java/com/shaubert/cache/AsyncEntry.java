package com.shaubert.cache;

public interface AsyncEntry<DATA> extends Entry<DATA> {

    public void getValue(DataCallback<DATA> callback);

}
