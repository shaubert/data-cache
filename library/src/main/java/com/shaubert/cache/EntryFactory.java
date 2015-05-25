package com.shaubert.cache;


public interface EntryFactory {

    <T> Entry<T> createEntry(String key);

}
