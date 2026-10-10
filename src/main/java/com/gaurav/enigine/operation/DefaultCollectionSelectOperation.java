package com.gaurav.enigine.operation;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.exception.SerializationException;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.operation.query.Query;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.serialization.ModelDeserializer;
import com.gaurav.enigine.storage.db.DBObject;
import com.gaurav.enigine.storage.db.DatabaseStorageManager;
import com.gaurav.enigine.utils.IteratorUtils;
import com.gaurav.enigine.utils.LockableIterator;
import com.gaurav.enigine.utils.ReaderWriterLock;
import lombok.Getter;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DefaultCollectionSelectOperation<T extends Number & Comparable<T>> implements CollectionSelectOperation<T> {

    private final Scheme.Collection collection;
    private final ReaderWriterLock readerWriterLock;
    private final CollectionIndexProvider collectionIndexProvider;
    private final DatabaseStorageManager storageManager;
    private final UniqueTreeIndexManager<T, Pointer> clusterIndexManager;
    @Getter
    private Query query;
    private List<String> fields;

    @SuppressWarnings("unchecked")
    public DefaultCollectionSelectOperation(Scheme.Collection collection, ReaderWriterLock readerWriterLock, CollectionIndexProvider collectionIndexProvider, DatabaseStorageManager storageManager) {
        this.collection = collection;
        this.readerWriterLock = readerWriterLock;
        this.collectionIndexProvider = collectionIndexProvider;
        this.storageManager = storageManager;
        this.clusterIndexManager = (UniqueTreeIndexManager<T, Pointer>) this.collectionIndexProvider.getClusterIndexManager();
    }

    // Todo: implement the actual field limitations
    @Override
    public CollectionSelectOperation<T> fields(String... fields) {
        this.fields = Arrays.asList(fields);
        return this;
    }

    @Override
    public CollectionSelectOperation<T> query(Query query) {
        this.query = query;
        return this;
    }

    protected <V extends Number & Comparable<V>> Iterator<V> getExecutedQuery() {
        if (query == null) {
            this.query = new Query();
        }
        return query.execute(this.collectionIndexProvider);
    }

    @Override
    public LockableIterator<DBObject> execute() {
        Iterator<T> executedQuery = getExecutedQuery();

        return LockableIterator.wrapReader(IteratorUtils.modifyNext(
                executedQuery,
                i -> {
                    try {
                        Optional<Pointer> optionalPointer = clusterIndexManager.getIndex(i);
                        if (optionalPointer.isPresent()) {
                            Optional<DBObject> dbObjectOptional = this.storageManager.select(optionalPointer.get());
                            if (dbObjectOptional.isPresent()) {
                                return dbObjectOptional.get();
                            }
                        }
                        throw new InternalOperationException("Could not read object");
                    } catch (InternalOperationException e) {
                        throw new RuntimeException(e);  // Ok
                    }
                }
        ), readerWriterLock);

    }

    @Override
    public <V> LockableIterator<V> execute(Class<V> clazz) {
        ModelDeserializer<V> modelDeserializer = new ModelDeserializer<>(clazz);
        return LockableIterator.wrapReader(IteratorUtils.modifyNext(execute(), dbObject -> {
            try {
                return modelDeserializer.deserialize(dbObject.getData());
            } catch (SerializationException | DeserializationException e) {
                throw new RuntimeException(e); // Ok
            }
        }), readerWriterLock);
    }

    @Override
    public <V> List<V> asList(Class<V> clazz) {
        return this.execute(clazz).asList();
    }

    @Override
    public long count() {
        try {
            readerWriterLock.getReadLock().lock();
            long count = 0;
            Iterator<?> executedQuery = getExecutedQuery();
            while (executedQuery.hasNext()) {
                count++;
                executedQuery.next();
            }
            return count;
        } finally {
            readerWriterLock.getReadLock().unlock();
        }

    }

    @Override
    public boolean exists() {
        if (query != null) {
            query.limit(1);
        } else {
            query = new Query().limit(1);
        }
        return this.execute().hasNext();
    }
}
