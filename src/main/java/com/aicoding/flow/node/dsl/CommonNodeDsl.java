package com.aicoding.flow.node.dsl;

import com.aicoding.flow.node.model.ParamModel;
import lombok.Data;

import java.util.List;

/**
 * 通用节点信息
 * @author gaoll
 * @time 2025/5/21 10:56
 **/
@Data
public class CommonNodeDsl {

    /**
     * 节点名称
     */
    private String name;

    /**
     * 节点id
     */
    private String id;

    private String nodeId;

    private Integer index;

    public void setId(String id){
        this.id = id;
        this.nodeId = "_"+id.replace("-","");
    }
    private boolean streamFlag=true;
    /**
     * 入参信息
     */
    private List<ParamModel> params;

    private String nodeType;

    private String nodeName;
}
