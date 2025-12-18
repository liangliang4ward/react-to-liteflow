package com.aicoding.flow.node;

import com.aicoding.flow.node.parent.CommonBoolNodeComponent;
import com.yomahub.liteflow.annotation.LiteflowComponent;

/**
 * @author gaoll
 * @time 2025/5/21 17:48
 **/
@LiteflowComponent("whileConditionNode")
public class WhileConditionNode extends CommonBoolNodeComponent {
    @Override
    public boolean doProcessBoolean() {
        return true;
    }



}
