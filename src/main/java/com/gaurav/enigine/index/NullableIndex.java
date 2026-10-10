package com.gaurav.enigine.index;

import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.operation.query.Order;

import java.util.Iterator;

public interface NullableIndex<V> {
    default boolean isNull(V value) {
        throw new UnsupportedOperationException();
    }

    default void addNull(V value) throws InternalOperationException {
        throw new UnsupportedOperationException();
    }

    default void removeNull(V value) throws InternalOperationException {
        throw new UnsupportedOperationException();
    }

    default Iterator<V> getNullIndexes(Order order) {
        throw new UnsupportedOperationException();
    }

}
