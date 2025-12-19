package com.aicoding.flow.controller;

import com.aicoding.flow.graph.GraphService;
import com.aicoding.flow.graph.model.GraphModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/toEl")
    public String toEl(@RequestBody GraphModel model) {
        return graphService.createEl(model);
    }

    @GetMapping("/test")
    public String test() {
        String testJson = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"start1\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code1\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"代码节点\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"http1\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"end1\", \n" +
                "      \"type\": \"end\", \n" +
                "      \"title\": \"结束节点\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \n" +
                "      \"id\": \"e1\", \n" +
                "      \"source\": \"start1\", \n" +
                "      \"target\": \"code1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"code1\", \n" +
                "      \"target\": \"http1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"http1\", \n" +
                "      \"target\": \"end1\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(testJson);
            System.out.println("=== 测试结果 ===");
            System.out.println(result);
            System.out.println("================");
            return "<pre>" + result.replaceAll("\\n", "<br/>") + "</pre>";
        } catch (Exception e) {
            e.printStackTrace();
            return "测试失败: " + e.getMessage();
        }
    }
}

