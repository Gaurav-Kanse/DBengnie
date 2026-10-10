package com.gaurav.enigine.storage.db;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.functional.DBObjectUpdateConsumer;

import java.util.Optional;


public interface DatabaseStorageManager {
    Pointer store(int scheme, int collectionId, int version, byte[] data) throws InternalOperationException;
    void update(Pointer pointer, DBObjectUpdateConsumer<DBObject> dbObjectConsumer) throws InternalOperationException;
    void update(Pointer pointer, byte[] bytes) throws InternalOperationException;
    Optional<DBObject> select(Pointer pointer) throws InternalOperationException;
    void remove(Pointer pointer) throws InternalOperationException;
    default void close(){}
}
