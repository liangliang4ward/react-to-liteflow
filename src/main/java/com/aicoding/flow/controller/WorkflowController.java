package com.aicoding.flow.controller;

import com.aicoding.flow.graph.GraphService;
import com.aicoding.flow.graph.model.GraphModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


//    @PostMapping("/toEl")
//    public String toEl(@RequestBody GraphModel model) {
//        return graphService.createEl(model);
//    }
}

