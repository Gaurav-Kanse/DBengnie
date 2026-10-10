package com.gaurav.enigine.operation;

import com.gaurav.enigine.operation.query.Query;
import com.gaurav.enigine.storage.db.DBObject;
import com.gaurav.enigine.utils.LockableIterator;

import java.util.List;

public interface CollectionSelectOperation<T extends Number & Comparable<T>> {
    CollectionSelectOperation<T> fields(String... fields);
    CollectionSelectOperation<T> query(Query query);
    Query getQuery();
    LockableIterator<DBObject> execute();
    <V> LockableIterator<V> execute(Class<V> clazz);
    <V> List<V> asList(Class<V> clazz);
    long count();
    boolean exists();
}
