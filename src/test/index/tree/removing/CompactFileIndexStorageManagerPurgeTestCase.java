package com.github.sepgh.test.index.tree.removing;

import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.index.tree.BPlusTreeUniqueTreeIndexManager;
import com.gaurav.enigine.storage.index.CompactFileIndexStorageManager;
import com.gaurav.enigine.storage.index.IndexStorageManager;
import com.gaurav.enigine.storage.index.header.JsonIndexHeaderManager;
import com.gaurav.enigine.storage.pool.FileHandler;
import com.gaurav.enigine.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.github.sepgh.test.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;

public class CompactFileIndexStorageManagerPurgeTestCase extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    private IndexStorageManager getIndexStorageManager() {
        return new CompactFileIndexStorageManager(
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    private UniqueTreeIndexManager<Long, Long> getIndexManager(IndexStorageManager indexStorageManager) {
        return new BPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get(), DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void testPurge() throws InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();

        UniqueTreeIndexManager<Long, Long> indexManager = getIndexManager(indexStorageManager);

        for (long i = 1; i < 11; i++) {
            indexManager.addIndex(i, i);
        }

        indexManager.purgeIndex();

        for (long i = 1; i < 10L * degree * BPlusTreeUniqueTreeIndexManager.PURGE_ITERATION_MULTIPLIER; i++) {
            Assertions.assertFalse(indexManager.getIndex(i).isPresent(), "%d is present while it shouldn't be!".formatted(i));
        }

    }

}
