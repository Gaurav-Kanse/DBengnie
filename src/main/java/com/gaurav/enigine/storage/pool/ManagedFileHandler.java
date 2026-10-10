package com.gaurav.enigine.storage.pool;

import com.gaurav.enigine.context.EngineConfig;
import com.gaurav.enigine.exception.InternalOperationException;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;

public class ManagedFileHandler implements AutoCloseable {

    private final FileHandlerPool fileHandlerPool;
    private final Path path;
    private final EngineConfig engineConfig;

    public ManagedFileHandler(FileHandlerPool fileHandlerPool, Path path, EngineConfig engineConfig) {
        this.fileHandlerPool = fileHandlerPool;
        this.path = path;
        this.engineConfig = engineConfig;
    }

    public AsynchronousFileChannel getAsynchronousFileChannel() throws InterruptedException, IOException, InternalOperationException {
        return this.fileHandlerPool.getFileChannel(path, engineConfig.getFileAcquireTimeout(), engineConfig.getFileAcquireUnit());
    }

    @Override
    public void close() throws InternalOperationException {
        this.fileHandlerPool.releaseFileChannel(path, engineConfig.getFileCloseTimeout(), engineConfig.getFileCloseUnit());
    }
}
