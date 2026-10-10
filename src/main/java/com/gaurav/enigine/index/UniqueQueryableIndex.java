package com.gaurav.enigine.index;

import com.gaurav.enigine.operation.query.Queryable;

public interface UniqueQueryableIndex<K extends Comparable<K>, V>
        extends UniqueTreeIndexManager<K, V>, Queryable<K, V>
{}
