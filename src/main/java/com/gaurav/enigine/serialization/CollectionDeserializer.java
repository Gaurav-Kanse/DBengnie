package com.gaurav.enigine.serialization;

import com.gaurav.enigine.ds.Bitmap;
import com.gaurav.enigine.exception.DeserializationException;
import com.gaurav.enigine.operation.query.Order;
import com.gaurav.enigine.scheme.Scheme;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;

import java.util.*;


@AllArgsConstructor
public class CollectionDeserializer {
    private final Scheme.Collection collection;

    public Map<String, Object> deserialize(byte[] bytes) throws DeserializationException {
        Map<String, Object> result = new HashMap<>();

        Bitmap<Integer> nullsBitmap = CollectionSerializationUtil.getNullsBitmap(collection, bytes);
        ArrayList<Integer> nullFieldIds = Lists.newArrayList(nullsBitmap.getOnIterator(Order.ASC));

        List<Scheme.Field> fields = collection.getFields();
        fields.sort(Comparator.comparingInt(Scheme.Field::getId));

        int fieldIndex = 0;
        for (Scheme.Field field : fields) {
            if (nullFieldIds.contains(fieldIndex)) {
                result.put(field.getName(), null);
            } else {
                result.put(
                        field.getName(),
                        CollectionSerializationUtil.getValueOfFieldAsObject(collection, field, bytes)
                );
            }

            fieldIndex++;
        }
        return result;
    }

}
