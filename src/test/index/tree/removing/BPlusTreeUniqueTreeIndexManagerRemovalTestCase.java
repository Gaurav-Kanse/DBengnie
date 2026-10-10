package com.github.sepgh.test.index.tree.removing;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import com.gaurav.enigine.storage.index.IndexStorageManager;
import com.gaurav.enigine.storage.index.OrganizedFileIndexStorageManager;
import com.gaurav.enigine.storage.index.header.JsonIndexHeaderManager;
import com.gaurav.enigine.storage.pool.FileHandler;
import com.gaurav.enigine.storage.pool.UnlimitedFileHandlerPool;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.github.sepgh.test.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;

public class BPlusTreeUniqueTreeIndexManagerRemovalTestCase extends BaseBPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    protected IndexStorageManager getIndexStorageManager() throws IOException, ExecutionException, InterruptedException {
        return new OrganizedFileIndexStorageManager(
                "test",
                new JsonIndexHeaderManager.SingletonFactory(),
                engineConfig,
                new UnlimitedFileHandlerPool(FileHandler.SingletonFileHandlerFactory.getInstance())
        );
    }

    protected UniqueTreeIndexManager<Long, Pointer> getIndexManager(IndexStorageManager indexStorageManager) {
        return new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    public void testRemovingLeftToRight() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingLeftToRight(uniqueTreeIndexManager, indexStorageManager);
    }

    @Test
    public void testRemovingRightToLeft() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingRightToLeft(uniqueTreeIndexManager, indexStorageManager);
    }


    @Test
    public void testRemovingRoot() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        IndexStorageManager indexStorageManager = getIndexStorageManager();
        UniqueTreeIndexManager<Long, Pointer> uniqueTreeIndexManager = getIndexManager(indexStorageManager);
        super.testRemovingRoot(uniqueTreeIndexManager, indexStorageManager);
    }

}
