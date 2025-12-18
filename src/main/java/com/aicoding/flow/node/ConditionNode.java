package com.aicoding.flow.node;


import com.aicoding.flow.node.parent.CommonBoolNodeComponent;
import com.yomahub.liteflow.annotation.LiteflowComponent;

/**
 * 条件节点
 * @author gaoll
 * @time 2025/5/21 15:32
 **/
@LiteflowComponent("conditionNode")
public class ConditionNode extends CommonBoolNodeComponent {

    @Override
    public boolean doProcessBoolean() {

        return false;
    }

}
