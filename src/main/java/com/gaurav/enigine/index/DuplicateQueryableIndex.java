package com.gaurav.enigine.index;

import com.gaurav.enigine.operation.query.Queryable;

public interface DuplicateQueryableIndex<K extends Comparable<K>, V extends Number & Comparable<V>>
        extends DuplicateIndexManager<K, V>, Queryable<K, V>
{}
