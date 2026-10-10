package com.gaurav.enigine.serialization;

import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.exception.SerializationException;
import com.gaurav.enigine.index.data.IndexBinaryObjectFactory;
import com.gaurav.enigine.scheme.Scheme;
import com.google.common.primitives.UnsignedInteger;

import java.math.BigInteger;
import java.util.List;

import static com.gaurav.enigine.utils.BinaryUtils.toByteArray;

public class UnsignedIntegerSerializer implements Serializer<UnsignedInteger> {
    @Override
    public Class<UnsignedInteger> getType() {
        return UnsignedInteger.class;
    }

    @Override
    public String typeName() {
        return FieldType.UNSIGNED_INT.getName();
    }

    @Override
    public List<String> compatibleTypes() {
        return List.of(FieldType.LONG.getName(), FieldType.INT.getName());
    }

    @Override
    public int maxSize() {
        return Integer.BYTES;
    }

    @Override
    public int minSize() {
        return Integer.BYTES;
    }

    @Override
    public byte[] serialize(UnsignedInteger unsignedInteger, Scheme.Meta meta) throws SerializationException {
        return toByteArray(unsignedInteger);
    }

    @Override
    public UnsignedInteger deserialize(byte[] bytes, Scheme.Meta meta) throws DeserializationException {
        BigInteger bigInteger = new BigInteger(1, bytes);
        return UnsignedInteger.valueOf(bigInteger);
    }

    @Override
    public int getSize(Scheme.Meta meta) {
        return Integer.BYTES;
    }

    @Override
    public byte[] serializeDefault(String defaultValue, Scheme.Meta meta) throws SerializationException {
        return new byte[Integer.BYTES]; // Todo
    }

    @Override
    public IndexBinaryObjectFactory<UnsignedInteger> getIndexBinaryObjectFactory(Scheme.Field field) {
        return new SerializerIndexBinaryObjectFactory<>(this, field, () -> UnsignedInteger.valueOf(1), i -> i.plus(UnsignedInteger.ONE));
    }
}
