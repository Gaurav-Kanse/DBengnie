package com.gaurav.enigine.storage.pool;

import java.io.IOException;

public interface FileHandlerFactory {
    FileHandler getFileHandler(String path) throws IOException;
}
