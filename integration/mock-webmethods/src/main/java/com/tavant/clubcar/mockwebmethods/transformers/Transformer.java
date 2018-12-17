package com.tavant.clubcar.mockwebmethods.transformers;

import org.mule.transformers.AbstractTransformer;

public abstract class Transformer extends AbstractTransformer {

    protected String syncType;

    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }
}
