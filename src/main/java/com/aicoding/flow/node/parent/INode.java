package com.aicoding.flow.node.parent;

import com.aicoding.flow.constants.NodeConstant;
import com.aicoding.flow.node.dsl.CommonNodeDsl;
import com.aicoding.flow.workflow.FlowContextWrapper;
import com.yomahub.liteflow.core.NodeComponent;

/**
 * @author gaoll
 * @time 2025/5/23 9:21
 **/
public interface INode {
    default FlowContextWrapper getContextWrapper(NodeComponent node) {
        return node.getContextBean(FlowContextWrapper.class);
    }
    default <T> T getBindParam(NodeComponent node,  Class<T> clazz) {
        return node.getBindData(NodeConstant.PARAM, clazz);
    }

    default CommonNodeDsl getBindParam(NodeComponent node){
        return node.getBindData(NodeConstant.PARAM, CommonNodeDsl.class);
    }
    default boolean isEnd(NodeComponent node) {
        return true;
    }
}
