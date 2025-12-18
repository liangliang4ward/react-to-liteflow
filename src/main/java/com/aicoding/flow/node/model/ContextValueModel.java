package com.aicoding.flow.node.model;

import lombok.Data;

/**
 * 上下文值模型
 * @author gaoll
 * @time 2025/5/22 17:00
 **/
@Data
public class ContextValueModel {
    /**
     * 名称
     */
    private String name;
    /**
     * 值
     */
    private Object value;
    /**
     * 类型
     */
    private String type;
}
