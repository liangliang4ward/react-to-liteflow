package com.aicoding.flow.node.dsl;

import lombok.Data;

/**
 * @author gaoll
 * @time 2025/5/21 11:30
 **/
@Data
public class EndNodeDsl extends CommonNodeDsl{

    /**
     * 回复内容
     */
    private String reply;
}
