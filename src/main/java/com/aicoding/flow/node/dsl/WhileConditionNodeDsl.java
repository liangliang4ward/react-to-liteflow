package com.aicoding.flow.node.dsl;

import com.aicoding.flow.node.model.ConditionModel;
import com.aicoding.flow.node.model.ParamModel;
import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/21 17:50
 **/
@Data
public class WhileConditionNodeDsl extends CommonNodeDsl{
    /**
     * 逻辑操作，and,or
     */
    private String logicalOperator;

    /**
     * 条件列表
     */
    private List<ConditionModel> conditions;

    /**
     * 最大循环次数
     */
    private Integer maxLoopCount;

    /**
     * 循环变量
     */
    private List<ParamModel> params;

}
