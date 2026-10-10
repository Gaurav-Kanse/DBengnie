package com.gaurav.enigine.storage.index.session;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.tree.node.AbstractTreeNode;
import com.gaurav.enigine.storage.index.IndexStorageManager;

import java.util.Optional;

public interface IndexIOSession<K extends Comparable<K>> {
    Optional<AbstractTreeNode<K>> getRoot() throws InternalOperationException;
    IndexStorageManager.NodeData write(AbstractTreeNode<K> node) throws InternalOperationException;
    AbstractTreeNode<K> read(Pointer pointer) throws InternalOperationException;
    void update(AbstractTreeNode<K> node) throws InternalOperationException;
    void remove(AbstractTreeNode<K> node) throws InternalOperationException;
    IndexStorageManager getIndexStorageManager();
    void commit() throws InternalOperationException;
}
