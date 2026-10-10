package com.gaurav.enigine.operation;

import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.operation.query.Query;

public interface CollectionDeleteOperation<T extends Number & Comparable<T>> {
    CollectionDeleteOperation<T> query(Query query);
    Query getQuery();
    long execute() throws InternalOperationException, DeserializationException;
}
