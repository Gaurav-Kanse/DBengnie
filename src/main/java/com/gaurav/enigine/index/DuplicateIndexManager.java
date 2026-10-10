package com.gaurav.enigine.index;

import com.gaurav.enigine.ds.KeyValue;
import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.operation.query.Order;
import com.gaurav.enigine.utils.LockableIterator;

import java.util.ListIterator;
import java.util.Optional;

public interface DuplicateIndexManager<K extends Comparable<K>, V extends Number & Comparable<V>> extends NullableIndex<V> {
    boolean addIndex(K identifier, V value) throws InternalOperationException, DeserializationException;
    Optional<ListIterator<V>> getIndex(K identifier) throws InternalOperationException;
    Optional<ListIterator<V>> getIndex(K identifier, Order order) throws InternalOperationException;
    boolean removeIndex(K identifier, V value) throws InternalOperationException, DeserializationException;
    int size() throws InternalOperationException;
    LockableIterator<KeyValue<K, ListIterator<V>>> getSortedIterator(Order order) throws InternalOperationException;
    void purgeIndex() throws InternalOperationException;
    int getIndexId();
    UniqueTreeIndexManager<K, Pointer> getInnerIndexManager();
}
