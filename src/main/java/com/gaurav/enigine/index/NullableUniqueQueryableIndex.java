package com.gaurav.enigine.index;

import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.data.IndexBinaryObjectFactory;
import com.gaurav.enigine.operation.query.Order;
import com.gaurav.enigine.storage.db.DatabaseStorageManager;
import com.gaurav.enigine.storage.index.header.IndexHeaderManager;

import java.util.Iterator;

public class NullableUniqueQueryableIndex<K extends Comparable<K>, V extends Number & Comparable<V>> extends UniqueQueryableIndexDecorator<K,V> {
    private final NullableIndexManager<V> nullableIndexManager;

    public NullableUniqueQueryableIndex(UniqueQueryableIndex<K, V> decorated, DatabaseStorageManager storageManager, IndexHeaderManager indexHeaderManager, IndexBinaryObjectFactory<V> vIndexBinaryObjectFactory) {
        super(decorated);
        this.nullableIndexManager = new NullableIndexManager<>(storageManager, indexHeaderManager, vIndexBinaryObjectFactory, getIndexId());
    }

    @Override
    public boolean isNull(V value) {
        return this.nullableIndexManager.isNull(value);
    }

    @Override
    public void addNull(V value) throws InternalOperationException {
        this.nullableIndexManager.addNull(value);
    }

    @Override
    public void removeNull(V value) throws InternalOperationException {
        this.nullableIndexManager.removeNull(value);
    }

    @Override
    public Iterator<V> getNullIndexes(Order order) {
        return this.nullableIndexManager.getNulls(order);
    }
}
