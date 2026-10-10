package com.gaurav.enigine.operation.query;

public interface IterationCacheFactory {
    <T extends Number> IterationCache<T> create();
}
