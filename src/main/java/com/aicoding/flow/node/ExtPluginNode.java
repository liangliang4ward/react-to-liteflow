package com.aicoding.flow.node;

import com.aicoding.flow.node.parent.CommonNodeComponent;
import com.yomahub.liteflow.annotation.LiteflowComponent;

/**
 * 外部插件节点
 * @author gaoll
 * @time 2025/5/30 17:18
 **/
@LiteflowComponent("extPluginNode")
public class ExtPluginNode extends CommonNodeComponent {
    @Override
    public void doProcess() {

        //获取节点Id
        //根据插件ID，获取插件服务
        //调用插件接口
    }
}
