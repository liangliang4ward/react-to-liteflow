package com.aicoding.flow.node.parent;

import com.aicoding.flow.node.dsl.CommonNodeDsl;
import com.aicoding.flow.workflow.FlowContextWrapper;
import com.yomahub.liteflow.core.NodeBooleanComponent;

/**
 * @author gaoll
 * @time 2025/5/23 9:09
 **/
public abstract class CommonBoolNodeComponent extends NodeBooleanComponent implements INode{

    @Override
    public boolean processBoolean() throws Exception {
        return doProcessBoolean();
    }



    public abstract boolean doProcessBoolean();
    protected FlowContextWrapper getContextWrapper() {
        return getContextWrapper(this);
    }

    protected CommonNodeDsl getBindParam() {
        return getBindParam(this);
    }

    protected <T> T getBindParam(Class<T> clazz) {
        return getBindParam(this, clazz);
    }

    @Override
    public boolean isEnd() {
        return getContextWrapper(this).isCancel();
    }


}
