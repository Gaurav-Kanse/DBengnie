package com.gaurav.enigine.index;

import com.gaurav.enigine.ds.KeyValue;
import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.tree.node.AbstractTreeNode;
import com.gaurav.enigine.operation.query.Order;
import com.gaurav.enigine.utils.LockableIterator;

import java.util.Optional;

public interface UniqueTreeIndexManager<K extends Comparable<K>, V> extends NullableIndex<V> {
    AbstractTreeNode<K> addIndex(K identifier, V value) throws InternalOperationException;
    AbstractTreeNode<K> addOrUpdateIndex(K identifier, V value) throws InternalOperationException;
    Optional<V> getIndex(K identifier) throws InternalOperationException;
    boolean removeIndex(K identifier) throws InternalOperationException;
    int size() throws InternalOperationException;
    LockableIterator<KeyValue<K, V>> getSortedIterator(Order order) throws InternalOperationException;
    void purgeIndex() throws InternalOperationException;
    int getIndexId();
    boolean supportIncrement();
    K nextKey() throws InternalOperationException, DeserializationException;
}
