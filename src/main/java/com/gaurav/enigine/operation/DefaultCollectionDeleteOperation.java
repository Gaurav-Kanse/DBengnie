package com.gaurav.enigine.operation;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.DuplicateQueryableIndex;
import com.gaurav.enigine.index.UniqueQueryableIndex;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.operation.query.Query;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.serialization.CollectionSerializationUtil;
import com.gaurav.enigine.storage.db.DBObject;
import com.gaurav.enigine.storage.db.DatabaseStorageManager;
import com.gaurav.enigine.utils.ReaderWriterLock;
import lombok.Getter;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultCollectionDeleteOperation<T extends Number & Comparable<T>> implements CollectionDeleteOperation<T> {

    private final Scheme.Collection collection;
    private final ReaderWriterLock readerWriterLock;
    private final CollectionIndexProvider collectionIndexProvider;
    private final DatabaseStorageManager storageManager;
    private final UniqueTreeIndexManager<T, Pointer> clusterIndexManager;
    @Getter
    private Query query;

    @SuppressWarnings("unchecked")
    public DefaultCollectionDeleteOperation(Scheme.Collection collection, ReaderWriterLock readerWriterLock, CollectionIndexProvider collectionIndexProvider, DatabaseStorageManager storageManager) {
        this.collection = collection;
        this.readerWriterLock = readerWriterLock;
        this.collectionIndexProvider = collectionIndexProvider;
        this.storageManager = storageManager;
        this.clusterIndexManager = (UniqueTreeIndexManager<T, Pointer>) this.collectionIndexProvider.getClusterIndexManager();
    }

    @Override
    public CollectionDeleteOperation<T> query(Query query) {
        this.query = query;
        return this;
    }

    protected Iterator<T> getExecutedQuery() {
        if (query == null) {
            this.query = new Query();
        }
        return query.execute(this.collectionIndexProvider);
    }

    @Override
    public long execute() throws InternalOperationException, DeserializationException {
        AtomicLong counter = new AtomicLong();
        try {
            this.readerWriterLock.getWriteLock().lock();
            Iterator<T> executedQuery = getExecutedQuery();

            while (executedQuery.hasNext()) {
                T clusterId = executedQuery.next();
                Optional<Pointer> optionalPointer = clusterIndexManager.getIndex(clusterId);

                if (optionalPointer.isEmpty()) {
                    throw new InternalOperationException("No pointer found for clusterId: " + clusterId);
                }

                Pointer pointer = optionalPointer.get();
                Optional<DBObject> optionalDBObject = this.storageManager.select(pointer);

                if (optionalDBObject.isEmpty()) {
                    throw new InternalOperationException("No object present at: " + pointer);
                }


                this.removeFieldIndexes(clusterId, optionalDBObject.get().getData());

                this.storageManager.remove(pointer);
                this.clusterIndexManager.removeIndex(clusterId);

                counter.incrementAndGet();
            }
        } finally {
            this.readerWriterLock.getWriteLock().unlock();
        }
        return counter.get();
    }

    private void removeFieldIndexes(T clusterId, byte[] bytes) throws DeserializationException, InternalOperationException {
        for (Scheme.Field field : collection.getFields().stream().filter(Scheme.Field::isIndexed).toList()) {
            if (field.getIndex().isUnique()) {
                UniqueQueryableIndex<?, T> uniqueIndexManager = (UniqueQueryableIndex<?, T>) collectionIndexProvider.getUniqueIndexManager(field);
                uniqueIndexManager.removeIndex(
                        CollectionSerializationUtil.getValueOfFieldAsObject(
                                collection,
                                field,
                                bytes
                        )
                );

            } else {
                DuplicateQueryableIndex<?, T> duplicateIndexManager = (DuplicateQueryableIndex<?, T>) collectionIndexProvider.getDuplicateIndexManager(field);

                duplicateIndexManager.removeIndex(
                        CollectionSerializationUtil.getValueOfFieldAsObject(
                                collection,
                                field,
                                bytes
                        ),
                        clusterId
                );
            }
        }
    }

}
