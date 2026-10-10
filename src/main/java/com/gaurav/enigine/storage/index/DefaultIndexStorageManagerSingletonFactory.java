package com.gaurav.enigine.storage.index;

import com.gaurav.enigine.context.EngineConfig;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.storage.db.DatabaseStorageManagerSingletonFactory;
import com.gaurav.enigine.storage.index.header.IndexHeaderManagerSingletonFactory;
import com.gaurav.enigine.storage.pool.FileHandlerPoolSingletonFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultIndexStorageManagerSingletonFactory extends IndexStorageManagerSingletonFactory {
    private final Map<String, IndexStorageManager> storageManagers = new ConcurrentHashMap<>();
    private final FileHandlerPoolSingletonFactory fileHandlerPoolSingletonFactory;
    private final DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory;

    public DefaultIndexStorageManagerSingletonFactory(EngineConfig engineConfig, IndexHeaderManagerSingletonFactory indexHeaderManagerSingletonFactory, FileHandlerPoolSingletonFactory fileHandlerPoolSingletonFactory, DatabaseStorageManagerSingletonFactory databaseStorageManagerSingletonFactory) {
        super(engineConfig, indexHeaderManagerSingletonFactory);
        this.fileHandlerPoolSingletonFactory = fileHandlerPoolSingletonFactory;
        this.databaseStorageManagerSingletonFactory = databaseStorageManagerSingletonFactory;
    }

    @Override
    public IndexStorageManager create(Scheme scheme, Scheme.Collection collection) {
        // If only a single index storage manager should be created, reuse if any is already created
        synchronized (this) {
            if (!engineConfig.isSplitIndexPerCollection()) {
                Set<String> keySet = this.storageManagers.keySet();
                if (!keySet.isEmpty()) {
                    return this.storageManagers.get(keySet.iterator().next());
                }
            }
        }

        return this.storageManagers.computeIfAbsent(scheme.getDbName(), key -> {
            String customName = null;
            if (engineConfig.isSplitIndexPerCollection()){
                customName = "col_" + collection.getId();
            }

            EngineConfig.IndexStorageManagerStrategy indexStorageManagerStrategy = engineConfig.getIndexStorageManagerStrategy();
            if (indexStorageManagerStrategy.equals(EngineConfig.IndexStorageManagerStrategy.ORGANIZED)) {
                return new OrganizedFileIndexStorageManager(customName, indexHeaderManagerSingletonFactory, engineConfig, fileHandlerPoolSingletonFactory.getInstance());
            } else if (indexStorageManagerStrategy.equals(EngineConfig.IndexStorageManagerStrategy.PAGE_BUFFER)) {
                return new DiskPageFileIndexStorageManager(engineConfig, indexHeaderManagerSingletonFactory, fileHandlerPoolSingletonFactory.getInstance(), databaseStorageManagerSingletonFactory.getInstance());
            } else {
                return new CompactFileIndexStorageManager(indexHeaderManagerSingletonFactory, engineConfig, fileHandlerPoolSingletonFactory.getInstance());
            }

        });
    }
}
