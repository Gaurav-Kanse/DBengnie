package com.gaurav.enigine.functional;

import com.gaurav.enigine.exception.InternalOperationException;
import com.gaurav.enigine.storage.db.Page;
import com.gaurav.enigine.storage.db.PageBuffer;

@FunctionalInterface
public interface PageFactory {
    Page apply(PageBuffer.PageTitle t) throws InternalOperationException;
}
