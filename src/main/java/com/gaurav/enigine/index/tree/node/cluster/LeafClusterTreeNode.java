package com.gaurav.enigine.index.tree.node.cluster;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.index.data.IndexBinaryObjectFactory;
import com.gaurav.enigine.index.data.PointerIndexBinaryObject;
import com.gaurav.enigine.index.tree.node.AbstractLeafTreeNode;

public class LeafClusterTreeNode<K extends Comparable<K>> extends AbstractLeafTreeNode<K, Pointer> {
    public LeafClusterTreeNode(byte[] data, IndexBinaryObjectFactory<K> keyIndexBinaryObjectFactory) {
        super(data, keyIndexBinaryObjectFactory, new PointerIndexBinaryObject.Factory());
    }
}