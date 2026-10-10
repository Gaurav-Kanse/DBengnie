package com.gaurav.enigine.operation.query;

import com.gaurav.enigine.operation.CollectionIndexProvider;

import java.util.Iterator;

public interface Condition {
    <V extends Number & Comparable<V>> Iterator<V> evaluate(CollectionIndexProvider collectionIndexProvider, Order order);
    String getField();
}
