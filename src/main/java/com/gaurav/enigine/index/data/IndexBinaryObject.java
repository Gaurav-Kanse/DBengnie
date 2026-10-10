package com.gaurav.enigine.index.data;

import com.gaurav.enigine.exception.DeserializationException;

public interface IndexBinaryObject<E> {
    E asObject() throws DeserializationException;
    int size();
    byte[] getBytes();
    default E getFirst() {
        throw new UnsupportedOperationException();
    }
    default E getNext(E current) {
        throw new UnsupportedOperationException();
    }
    default E getNext() throws DeserializationException {
        throw new UnsupportedOperationException();
    }
    default boolean supportIncrements() {
        return false;
    }
}
