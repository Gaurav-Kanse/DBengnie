package com.gaurav.enigine.storage.index.session;

import com.gaurav.enigine.ds.KVSize;
import com.gaurav.enigine.index.tree.node.NodeFactory;
import com.gaurav.enigine.storage.index.IndexStorageManager;

public abstract class IndexIOSessionFactory {
    public abstract <K extends Comparable<K>> IndexIOSession<K> create(IndexStorageManager indexStorageManager, int indexId, NodeFactory<K> nodeFactory, KVSize kvSize);
}