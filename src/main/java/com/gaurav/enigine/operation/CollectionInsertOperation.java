package com.gaurav.enigine.operation;

import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.exception.SerializationException;

public interface CollectionInsertOperation<T extends Number & Comparable<T>> {
    <V> V execute(V v) throws SerializationException, InternalOperationException, DeserializationException;
    byte[] execute(byte[] bytes) throws InternalOperationException, DeserializationException, SerializationException;
}
