package com.gaurav.enigine.operation;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.index.DuplicateQueryableIndex;
import com.gaurav.enigine.index.UniqueQueryableIndex;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.operation.query.Queryable;
import com.gaurav.enigine.scheme.Scheme;


public interface CollectionIndexProvider {
    UniqueQueryableIndex<?, ? extends Number> getUniqueIndexManager(Scheme.Field field);
    DuplicateQueryableIndex<?, ? extends Number> getDuplicateIndexManager(Scheme.Field field);
    UniqueTreeIndexManager<?, Pointer> getClusterIndexManager();
    UniqueQueryableIndex<?, ? extends Number> getUniqueIndexManager(String fieldName);
    DuplicateQueryableIndex<?, ? extends Number> getDuplicateIndexManager(String fieldName);
    Queryable<?, ? extends Number> getQueryableIndex(Scheme.Field field);
    Queryable<?, ? extends Number> getQueryableIndex(String fieldName);
}
