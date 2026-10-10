package com.github.sepgh.test.index.tree.removing;

import com.gaurav.enigine.ds.Pointer;
import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.index.UniqueTreeIndexManager;
import com.gaurav.enigine.index.tree.node.cluster.ClusterBPlusTreeUniqueTreeIndexManager;
import com.gaurav.enigine.storage.index.IndexStorageManager;
import com.gaurav.enigine.storage.index.session.MemorySnapshotIndexIOSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static com.github.sepgh.test.TestParams.DEFAULT_INDEX_BINARY_OBJECT_FACTORY;


/* Identical copy of BPlusTreeIndexManagerRemovalTestCase */
public class MemorySnapshotBPlusTreeUniqueTreeIndexManagerRemovalTestCase extends BPlusTreeUniqueTreeIndexManagerRemovalTestCase {

    @Override
    protected UniqueTreeIndexManager<Long, Pointer> getIndexManager(IndexStorageManager indexStorageManager) {
        return new ClusterBPlusTreeUniqueTreeIndexManager<>(1, degree, indexStorageManager, MemorySnapshotIndexIOSession.Factory.getInstance(), DEFAULT_INDEX_BINARY_OBJECT_FACTORY.get());
    }

    @Test
    @Timeout(2)
    @Override
    public void testRemovingLeftToRight() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingLeftToRight();
    }


    @Test
    @Timeout(2)
    @Override
    public void testRemovingRightToLeft() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingRightToLeft();
    }

    @Test
    @Timeout(2)
    @Override
    public void testRemovingRoot() throws IOException, ExecutionException, InterruptedException, InternalOperationException {
        super.testRemovingRoot();
    }
}
