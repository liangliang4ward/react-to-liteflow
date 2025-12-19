package com.aicoding.flow.graph;

import cn.hutool.json.JSONUtil;
import com.aicoding.flow.graph.model.GraphModel;
import org.springframework.stereotype.Service;

/**
 * @author gaoll
 * @time 2025/5/22 15:48
 **/
@Service
public class GraphService {
    public String createEl(String json) {
        GraphModel graph = JSONUtil.toBean(json, GraphModel.class);
        //TODO:
        return "";

    }


}
