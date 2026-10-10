package com.gaurav.enigine.operation;

import com.gaurav.enigine.exception.BaseSerializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.operation.query.Query;

import java.util.function.Consumer;

public interface CollectionUpdateOperation<T extends Number & Comparable<T>> {
    CollectionUpdateOperation<T> query(Query query);
    Query getQuery();
    <M> long execute(Class<M> mClass, Consumer<M> mConsumer) throws InternalOperationException, BaseSerializationException;
    long execute(Consumer<byte[]> byteArrayConsumer) throws InternalOperationException, BaseSerializationException;
}
