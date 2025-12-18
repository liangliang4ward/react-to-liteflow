package com.aicoding.flow.node.dsl;

import com.aicoding.flow.node.model.ParamModel;
import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/27 9:11
 **/
@Data
public class JsonExtractNodeDsl extends CommonNodeDsl{

    private String jsonValue;

    private String varType;
    /**
     * 入参信息
     */
    private List<ParamModel> result;
}
