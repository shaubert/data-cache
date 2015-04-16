package com.shaubert.cache;

public interface Function<IN, OUT> {
    OUT apply(IN param);
}
