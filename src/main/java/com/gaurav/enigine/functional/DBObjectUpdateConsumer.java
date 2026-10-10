package com.gaurav.enigine.functional;


import com.gaurav.enigine.exception.InvalidDBObjectWrapper;

@FunctionalInterface
public interface DBObjectUpdateConsumer<T> {
    void accept(T t) throws InvalidDBObjectWrapper;
}
