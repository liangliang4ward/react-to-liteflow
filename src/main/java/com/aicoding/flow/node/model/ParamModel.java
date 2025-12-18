package com.aicoding.flow.node.model;

import lombok.Data;

/**
 * 参数模型
 * @author gaoll
 * @time 2025/5/21 10:58
 **/
@Data
public class ParamModel {
    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数显示名称
     */
    private String label;

    /**
     * 参数类型；number,string,boolean
     */
    private String dataType;

    /**
     * 变量类型；constant,selector
     */
    private String varType;

    /**
     * 是否必填
     */
    private boolean required;

    /**
     * 数据值
     */
    private String value;
}
