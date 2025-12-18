package com.aicoding.flow.graph.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/27 16:18
 **/
@Data
public class GraphBranch {

    private List<GraphBranchLine> segments;

}
