package com.aicoding.flow.node.dsl;

import com.aicoding.flow.node.model.ConditionModel;
import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/21 15:33
 **/
@Data
public class ConditionNodeDsl extends CommonNodeDsl {

    /**
     * 逻辑操作，and,or
     */
    private String logicalOperator;

    /**
     * 条件列表
     */
    private List<ConditionModel> conditions;

    private List<ConditionNodeDsl> children;

    private boolean isElseFlag=false;
}
