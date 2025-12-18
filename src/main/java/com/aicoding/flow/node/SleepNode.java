package com.aicoding.flow.node;

import cn.hutool.core.thread.ThreadUtil;
import com.aicoding.flow.node.parent.CommonNodeComponent;
import com.yomahub.liteflow.annotation.LiteflowComponent;

/**
 * @author gaoll
 * @time 2025/5/21 18:16
 **/
@LiteflowComponent("sleepNode")
public class SleepNode extends CommonNodeComponent {
    @Override
    public void doProcess(){
        ThreadUtil.sleep(10000);
    }
}
