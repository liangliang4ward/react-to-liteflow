package com.aicoding.flow.node;

import com.aicoding.flow.node.dsl.CommonNodeDsl;
import com.aicoding.flow.node.model.ParamModel;
import com.aicoding.flow.node.parent.CommonNodeComponent;
import com.aicoding.flow.workflow.FlowContextWrapper;
import com.aicoding.flow.workflow.model.InputParam;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 开始节点，放置相关参数
 *
 * @author gaoll
 * @time 2025/5/21 10:54
 **/
@LiteflowComponent("startNode")
public class StartNode extends CommonNodeComponent {
    @Override
    public void doProcess(){

        //开始节点，从上下文中获取数据，并放到节点中
        CommonNodeDsl nodeDsl = getBindParam();
        FlowContextWrapper contextWrapper = getContextWrapper();
        List<InputParam> userInputs = contextWrapper.getUserInputs();
        Map<String, InputParam>
                userInputMap = userInputs.stream().collect(Collectors.toMap(InputParam::getName, Function.identity()));
        List<ParamModel> params = nodeDsl.getParams();

        if(CollectionUtils.isEmpty(params)){
            return;
        }
        for (ParamModel param : params) {
            if(param.isRequired() && !userInputMap.containsKey(param.getName())){
                throw new RuntimeException(param.getName()+"必填项");
            }
        }

    }
}
