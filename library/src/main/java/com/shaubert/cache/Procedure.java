package com.shaubert.cache;

public interface Procedure<IN> {
    void perform(IN param);
}
