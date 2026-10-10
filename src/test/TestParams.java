package com.github.sepgh.test;

import com.gaurav.enigine.ds.KVSize;
import com.gaurav.enigine.index.data.IndexBinaryObjectFactory;
import com.gaurav.enigine.index.data.PointerIndexBinaryObject;
import com.gaurav.enigine.scheme.Scheme;
import com.gaurav.enigine.serialization.LongSerializer;
import com.gaurav.enigine.serialization.UnsignedLongSerializer;
import com.google.common.primitives.UnsignedLong;

import java.util.function.Supplier;

public class TestParams {
    public static final KVSize DEFAULT_KV_SIZE =  new KVSize(Long.BYTES, PointerIndexBinaryObject.BYTES);
    public static final Supplier<Scheme.Field> FAKE_FIELD_SUPPLIER = () -> Scheme.Field.builder().build();
    public static final Scheme.Field FAKE_FIELD = FAKE_FIELD_SUPPLIER.get();
    public static final Supplier<IndexBinaryObjectFactory<Long>> LONG_INDEX_BINARY_OBJECT_FACTORY = () -> new LongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
    public static final Supplier<IndexBinaryObjectFactory<Long>> DEFAULT_INDEX_BINARY_OBJECT_FACTORY = () -> new LongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
    public static final Supplier<IndexBinaryObjectFactory<UnsignedLong>> ULONG_INDEX_BINARY_OBJECT_FACTORY = () -> new UnsignedLongSerializer().getIndexBinaryObjectFactory(FAKE_FIELD);
}
