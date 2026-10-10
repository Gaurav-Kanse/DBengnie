package com.gaurav.enigine.storage.db;


import com.gaurav.enigine.exception.InvalidDBObjectWrapper;

public class MutableDBObjectDecorator extends DBObject {

    public MutableDBObjectDecorator(DBObject decorated) throws InvalidDBObjectWrapper {
        super(decorated.getPage(), decorated.getBegin(), decorated.getEnd());
    }

    public void modifyData(int offset, byte[] value){
        super.modifyData(offset, value);
    }

    public void setCollectionId(int collectionId) {
        super.setCollectionId(collectionId);
    }

    public void setSize(int size) {
        super.setSize(size);
    }

    public void deactivate() {
        super.deactivate();
    }

}
