package com.aicoding.flow;

import com.aicoding.flow.graph.GraphService;

public class TestApplication {
    public static void main(String[] args) {
        GraphService graphService = new GraphService();

        // 测试基础顺序流
        testBasicSequentialFlow(graphService);

        System.out.println("\n" + "=" .repeat(50) + "\n");

        // 测试 if-else 条件流
        testIfElseConditionFlow(graphService);

        System.out.println("\n" + "=" .repeat(50) + "\n");

        // 测试多节点并发流
        testMultiNodeConcurrentFlow(graphService);
    }

    private static void testBasicSequentialFlow(GraphService graphService) {
        System.out.println("=== 基础顺序流测试 ===");
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"1711526002155\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\", \n" +
                "      \"data\": { \n" +
                "        \"variables\": [], \n" +
                "        \"params\": [{\"name\": \"input\", \"label\": \"需要总结的文本\", \"required\": true}, {\"name\": \"summaryStyle\", \"label\": \"技术摘要 或 概述\", \"required\": true}]\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"1766123566821\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"代码节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": [{\"name\": \"arg1\", \"required\": false}]\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"1766123597284\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求\", \n" +
                "      \"data\": { \n" +
                "        \"method\": \"get\", \n" +
                "        \"url\": \"http://www.baidu.com\", \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"1766123621822\", \n" +
                "      \"type\": \"end\", \n" +
                "      \"title\": \"结束节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \n" +
                "      \"id\": \"e1\", \n" +
                "      \"source\": \"1711526002155\", \n" +
                "      \"target\": \"1766123566821\", \n" +
                "      \"sourceHandle\": \"source\", \n" +
                "      \"targetHandle\": \"target\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"1766123566821\", \n" +
                "      \"target\": \"1766123597284\", \n" +
                "      \"sourceHandle\": \"source\", \n" +
                "      \"targetHandle\": \"target\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"1766123597284\", \n" +
                "      \"target\": \"1766123621822\", \n" +
                "      \"sourceHandle\": \"source\", \n" +
                "      \"targetHandle\": \"target\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("转换结果:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testIfElseConditionFlow(GraphService graphService) {
        System.out.println("=== If-Else 条件流测试 ===");
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"start1\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"cond1\", \n" +
                "      \"type\": \"condition\", \n" +
                "      \"title\": \"条件节点\", \n" +
                "      \"data\": { \n" +
                "        \"cases\": [], \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code1\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"条件为真时执行\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code2\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"条件为假时执行\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"end1\", \n" +
                "      \"type\": \"end\", \n" +
                "      \"title\": \"结束节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \n" +
                "      \"id\": \"e1\", \n" +
                "      \"source\": \"start1\", \n" +
                "      \"target\": \"cond1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"cond1\", \n" +
                "      \"target\": \"code1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"cond1\", \n" +
                "      \"target\": \"code2\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e4\", \n" +
                "      \"source\": \"code1\", \n" +
                "      \"target\": \"end1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e5\", \n" +
                "      \"source\": \"code2\", \n" +
                "      \"target\": \"end1\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("转换结果:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testMultiNodeConcurrentFlow(GraphService graphService) {
        System.out.println("=== 多节点并发流测试 ===");
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"start2\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"http1\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求1\", \n" +
                "      \"data\": { \n" +
                "        \"method\": \"get\", \n" +
                "        \"url\": \"http://api.example.com/data1\", \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"http2\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求2\", \n" +
                "      \"data\": { \n" +
                "        \"method\": \"get\", \n" +
                "        \"url\": \"http://api.example.com/data2\", \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code1\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"数据处理\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"end2\", \n" +
                "      \"type\": \"end\", \n" +
                "      \"title\": \"结束节点\", \n" +
                "      \"data\": { \n" +
                "        \"params\": []\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \n" +
                "      \"id\": \"e1\", \n" +
                "      \"source\": \"start2\", \n" +
                "      \"target\": \"http1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"start2\", \n" +
                "      \"target\": \"http2\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"http1\", \n" +
                "      \"target\": \"code1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e4\", \n" +
                "      \"source\": \"http2\", \n" +
                "      \"target\": \"code1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e5\", \n" +
                "      \"source\": \"code1\", \n" +
                "      \"target\": \"end2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("转换结果:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
