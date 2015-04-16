package com.shaubert.cache;


public interface EntryFactory {

    public <T> Entry<T> createEntry(String key);

}
