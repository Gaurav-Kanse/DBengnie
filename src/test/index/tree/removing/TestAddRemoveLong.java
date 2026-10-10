package com.github.sepgh.test.index.tree.removing;

import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.index.tree.BPlusTreeUniqueTreeIndexManager;
import com.gaurav.enigine.storage.index.IndexStorageManager;
import com.gaurav.enigine.storage.index.OrganizedFileIndexStorageManager;
import com.gaurav.enigine.storage.index.header.JsonIndexHeaderManager;
import com.gaurav.enigine.storage.pool.FileHandler;
import com.gaurav.enigine.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.github.sepgh.test.TestParams.LONG_INDEX_BINARY_OBJECT_FACTORY;

public class TestAddRemoveLong extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    protected IndexStorageManager getIndexStorageManager() {
        return new OrganizedFileIndexStorageManager(
                "test",
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    private UniqueTreeIndexManager<Long, Long> getIndexManager(IndexStorageManager indexStorageManager) {
        return new BPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, LONG_INDEX_BINARY_OBJECT_FACTORY.get(), LONG_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void test() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Long> indexManager = getIndexManager(indexStorageManager);

        for (long i = 1; i < 11; i++) {
            indexManager.addIndex(i, i);
        }

        for (long i = 1; i < 11; i++) {
            indexManager.removeIndex(i);
        }

    }

}
