package com.gaurav.enigine.context;

import com.gaurav.enigine.operation.CollectionOperation;
import com.gaurav.enigine.scheme.Scheme;

public interface DatabaseContext {
    Scheme getScheme();
    CollectionOperation getOperation(Scheme.Collection collection);
    CollectionOperation getOperation(String collection);
    void shutdownGracefully();
}
