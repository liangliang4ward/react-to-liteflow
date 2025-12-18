package com.aicoding.flow.workflow;

import com.aicoding.flow.constants.VarConstant;
import com.aicoding.flow.node.dsl.CommonNodeDsl;
import com.aicoding.flow.node.model.ContextValueModel;
import com.aicoding.flow.workflow.model.InputParam;
import com.aicoding.flow.workflow.model.MessageModel;
import lombok.Data;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaoll
 * @time 2025/5/21 11:02
 **/
@Data
public class FlowContextWrapper {
    /**
     * 用户输入
     */
    private List<InputParam> userInputs;
    /**
     * 返回结果
     */
    private List<Flux<MessageModel>> responseModes = Collections.synchronizedList(new ArrayList<>());
    /**
     * 上下文值
     */
    private Map<String, ContextValueModel> contextValues =new ConcurrentHashMap<>();

    private FluxSink<MessageModel> stepSink;

    public void step(MessageModel mode){
        stepSink.next(mode);
    }

    public void step(String event){
        MessageModel model = new MessageModel();
        model.setEvent(event);
        step(model);
    }

    public void putValue(CommonNodeDsl dsl, String name, ContextValueModel model) {
        this.getContextValues().put(VarConstant.SELECTOR_START + dsl.getId() + "." + name + VarConstant.SELECTOR_END, model);
    }

    public void putValue(CommonNodeDsl dsl, ContextValueModel model){
        putValue(dsl,model.getName(),model);
    }

    public void putValue(String id,ContextValueModel model){
        this.getContextValues().put(VarConstant.SELECTOR_START+id+"."+model.getName()+VarConstant.SELECTOR_END,model);
    }

    private boolean isCancel=false;

    public void cancel(){
        isCancel = true;
    }
}
