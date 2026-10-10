package com.gaurav.enigine.storage.index;

import com.gaurav.enigine.context.EngineConfig;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.storage.index.header.IndexHeaderManagerSingletonFactory;

public abstract class IndexStorageManagerSingletonFactory {
    protected final EngineConfig engineConfig;
    protected final IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory;

    protected IndexStorageManagerSingletonFactory(EngineConfig engineConfig, IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory) {
        this.engineConfig = engineConfig;
        this.indexHeaderManagerSingletonFactory = indexHeaderManagerSingletonFactory;
    }

    public abstract IndexStorageManager create(Scheme scheme, Scheme.Collection collection);
}
