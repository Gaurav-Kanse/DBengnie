package com.gaurav.enigine.index.tree.node.cluster;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.index.data.IndexBinaryObjectFactory;
import com.gaurav.enigine.index.data.PointerIndexBinaryObject;
import com.gaurav.enigine.index.tree.BPlusTreeUniqueTreeIndexManager;
import com.gaurav.enigine.index.tree.node.NodeFactory;
import com.gaurav.enigine.storage.index.IndexStorageManager;
import com.gaurav.enigine.storage.index.session.ImmediateCommitIndexIOSession;
import com.gaurav.enigine.storage.index.session.IndexIOSessionFactory;

public class ClusterBPlusTreeUniqueTreeIndexManager<K extends Comparable<K>> extends BPlusTreeUniqueTreeIndexManager<K, Pointer> {
    public ClusterBPlusTreeUniqueTreeIndexManager(int index, int degree, IndexStorageManager indexStorageManager, IndexIOSessionFactory indexIOSessionFactory, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory){
        super(index, degree, indexStorageManager, indexIOSessionFactory, keyIndexBinaryObjectFactory, new PointerIndexBinaryObject.Factory(), new NodeFactory.ClusterNodeFactory<>(keyIndexBinaryObjectFactory));
    }

    public ClusterBPlusTreeUniqueTreeIndexManager(int index, int degree, IndexStorageManager indexStorageManager, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory){
        this(index, degree, indexStorageManager, ImmediateCommitIndexIOSession.Factory.getInstance(), keyIndexBinaryObjectFactory);
    }

}
