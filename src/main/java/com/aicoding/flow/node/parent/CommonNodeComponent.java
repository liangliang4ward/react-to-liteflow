package com.aicoding.flow.node.parent;

import com.aicoding.flow.node.dsl.CommonNodeDsl;
import com.aicoding.flow.workflow.FlowContextWrapper;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * @author gaoll
 * @time 2025/5/22 19:02
 **/
public abstract class CommonNodeComponent extends NodeComponent implements INode{
    @Override
    public void process() throws Exception {
        doProcess();
    }

    public abstract void doProcess();

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
