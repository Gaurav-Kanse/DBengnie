package com.gaurav.enigine.index.data;

import com.gaurav.enigine.exception.IndexBinaryObjectCreationException;

public interface IndexBinaryObjectFactory<E> {
    IndexBinaryObject<E> create(E e) throws IndexBinaryObjectCreationException;
    IndexBinaryObject<E> create(byte[] bytes, int beginning);
    default IndexBinaryObject<E> create(byte[] bytes) {
        return create(bytes, 0);
    }
    int size();
    Class<E> getType();
    IndexBinaryObject<E> createEmpty();
}
