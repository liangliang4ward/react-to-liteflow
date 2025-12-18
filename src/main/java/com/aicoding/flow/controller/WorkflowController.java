package com.aicoding.flow.controller;

import com.aicoding.flow.graph.GraphService;
import com.yomahub.liteflow.core.FlowExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaoll
 * @time 2025/5/21 11:22
 **/
@RestController
@RequestMapping("/workflow")
public class WorkflowController {


    @Autowired
    private GraphService graphService;

    @Autowired
    private FlowExecutor flowExecutor;


}

