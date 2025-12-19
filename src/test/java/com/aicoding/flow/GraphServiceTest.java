package com.aicoding.flow;

import cn.hutool.core.io.FileUtil;
import com.aicoding.flow.graph.GraphService;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class GraphServiceTest {
    private final GraphService graphService = new GraphService();

    /**
     * 测试基础顺序流
     * 开始 -> 代码节点 -> HTTP请求 -> 结束
     */
    @Test
    void testBasicSequentialFlow() {
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \"id\": \"1711526002155\", \"type\": \"start\", \"title\": \"开始节点\", \"data\": { \"variables\": [{\"label\": \"需要总结的文本\", \"variable\": \"input\", \"type\": \"paragraph\", \"required\": true}, {\"label\": \"技术摘要 或 概述\", \"variable\": \"summaryStyle\", \"type\": \"select\", \"required\": true}] } },\n" +
                "    { \"id\": \"1766123566821\", \"type\": \"code\", \"title\": \"代码节点\", \"data\": { \"params\": [{\"name\": \"arg1\", \"required\": false}] } },\n" +
                "    { \"id\": \"1766123597284\", \"type\": \"http-request\", \"title\": \"HTTP请求\", \"data\": { \"method\": \"get\", \"url\": \"xxxxx\" } },\n" +
                "    { \"id\": \"1766123621822\", \"type\": \"end\", \"title\": \"结束节点\" }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \"source\": \"1711526002155\", \"target\": \"1766123566821\" },\n" +
                "    { \"source\": \"1766123566821\", \"target\": \"1766123597284\" },\n" +
                "    { \"source\": \"1766123597284\", \"target\": \"1766123621822\" }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("基础顺序流测试结果:");
            System.out.println(result);
            System.out.println("----------------------------------------");
            
            // 验证结果包含所有必要的绑定语句和流程控制语句
            assertTrue(result.contains("_1711526002155=startNode.bind"));
            assertTrue(result.contains("_1766123566821=codeNode.bind"));
            assertTrue(result.contains("_1766123597284=httpNode.bind"));
            assertTrue(result.contains("_1766123621822=endNode.bind"));
            assertTrue(result.contains("THEN(_1711526002155, THEN(_1766123566821, THEN(_1766123597284, _1766123621822)))"));
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试if-else条件流
     * 开始 -> 条件节点 -> (分支1: 代码节点, 分支2: HTTP请求) -> 结束
     */
    @Test
    void testIfElseConditionFlow() {
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \"id\": \"start-1\", \"type\": \"start\", \"title\": \"开始节点\" },\n" +
                "    { \"id\": \"cond-1\", \"type\": \"condition\", \"title\": \"条件节点\" },\n" +
                "    { \"id\": \"code-1\", \"type\": \"code\", \"title\": \"代码节点\" },\n" +
                "    { \"id\": \"http-1\", \"type\": \"http-request\", \"title\": \"HTTP请求\" },\n" +
                "    { \"id\": \"end-1\", \"type\": \"end\", \"title\": \"结束节点\" }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \"source\": \"start-1\", \"target\": \"cond-1\" },\n" +
                "    { \"source\": \"cond-1\", \"target\": \"code-1\" },\n" +
                "    { \"source\": \"cond-1\", \"target\": \"http-1\" },\n" +
                "    { \"source\": \"code-1\", \"target\": \"end-1\" },\n" +
                "    { \"source\": \"http-1\", \"target\": \"end-1\" }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("if-else条件流测试结果:");
            System.out.println(result);
            System.out.println("----------------------------------------");
            
            assertTrue(result.contains("_cond-1=conditionNode.bind"));
            assertTrue(result.contains("IF(_cond-1, THEN(_code-1, _end-1), ELSE(THEN(_http-1, _end-1)))"));
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    /**
     * 测试多节点并发
     * 开始 -> (代码节点 + HTTP请求 并行执行) -> 结束
     */
    @Test
    void testMultiNodeConcurrent() {
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \"id\": \"start-2\", \"type\": \"start\", \"title\": \"开始节点\" },\n" +
                "    { \"id\": \"code-2\", \"type\": \"code\", \"title\": \"代码节点\" },\n" +
                "    { \"id\": \"http-2\", \"type\": \"http-request\", \"title\": \"HTTP请求\" },\n" +
                "    { \"id\": \"end-2\", \"type\": \"end\", \"title\": \"结束节点\" }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    { \"source\": \"start-2\", \"target\": \"code-2\" },\n" +
                "    { \"source\": \"start-2\", \"target\": \"http-2\" },\n" +
                "    { \"source\": \"code-2\", \"target\": \"end-2\" },\n" +
                "    { \"source\": \"http-2\", \"target\": \"end-2\" }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("多节点并发测试结果:");
            System.out.println(result);
            System.out.println("----------------------------------------");
            
            assertTrue(result.contains("THEN(_start-2, WHEN(THEN(_code-2, _end-2), THEN(_http-2, _end-2)))"));
        } catch (Exception e) {
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    public void test3(){
        String json="{\"edges\":[{\"data\":{\"isInIteration\":false,\"isInLoop\":false,\"sourceType\":\"start\",\"targetType\":\"code\"},\"id\":\"1711526002155-source-1766123566821-target\",\"source\":\"1711526002155\",\"sourceHandle\":\"source\",\"target\":\"1766123566821\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0},{\"data\":{\"isInIteration\":false,\"isInLoop\":false,\"sourceType\":\"code\",\"targetType\":\"http-request\"},\"id\":\"1766123566821-source-1766123597284-target\",\"source\":\"1766123566821\",\"sourceHandle\":\"source\",\"target\":\"1766123597284\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0},{\"data\":{\"isInIteration\":false,\"isInLoop\":false,\"sourceType\":\"http-request\",\"targetType\":\"end\"},\"id\":\"1766123597284-source-1766123621822-target\",\"source\":\"1766123597284\",\"sourceHandle\":\"source\",\"target\":\"1766123621822\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0},{\"data\":{\"isInLoop\":false,\"sourceType\":\"start\",\"targetType\":\"code\"},\"id\":\"1711526002155-source-1766130047341-target\",\"source\":\"1711526002155\",\"sourceHandle\":\"source\",\"target\":\"1766130047341\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0},{\"data\":{\"isInLoop\":false,\"sourceType\":\"code\",\"targetType\":\"http-request\"},\"id\":\"1766130047341-source-1766130055356-target\",\"source\":\"1766130047341\",\"sourceHandle\":\"source\",\"target\":\"1766130055356\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0},{\"data\":{\"isInLoop\":false,\"sourceType\":\"http-request\",\"targetType\":\"end\"},\"id\":\"1766130055356-source-1766123621822-target\",\"source\":\"1766130055356\",\"sourceHandle\":\"source\",\"target\":\"1766123621822\",\"targetHandle\":\"target\",\"type\":\"custom\",\"zIndex\":0}],\"nodes\":[{\"data\":{\"desc\":\"\",\"selected\":false,\"title\":\"开始\",\"type\":\"start\",\"variables\":[{\"label\":\"需要总结的文本\",\"maxLength\":null,\"options\":[],\"required\":true,\"type\":\"paragraph\",\"variable\":\"input\"},{\"label\":\"技术摘要 或 概述\",\"maxLength\":48,\"options\":[\"技术摘要\",\"概述\"],\"required\":true,\"type\":\"select\",\"variable\":\"summaryStyle\"}]},\"dragging\":false,\"height\":135,\"id\":\"1711526002155\",\"position\":{\"x\":0,\"y\":16},\"positionAbsolute\":{\"x\":0,\"y\":16},\"selected\":false,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"start\",\"width\":242},{\"data\":{\"code\":\"\\ndef main(arg1: str):\\n    return {\\n        \\\"result\\\": arg1,\\n    }\\n\",\"codeLanguage\":\"python3\",\"outputs\":{\"result\":{\"children\":null,\"type\":\"string\"}},\"selected\":false,\"title\":\"代码执行\",\"type\":\"code\",\"variables\":[{\"valueSelector\":[\"1711526002155\",\"input\"],\"valueType\":\"string\",\"variable\":\"arg1\"}]},\"dragging\":false,\"height\":52,\"id\":\"1766123566821\",\"position\":{\"x\":342,\"y\":35},\"positionAbsolute\":{\"x\":342,\"y\":35},\"selected\":false,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"code\",\"width\":242},{\"data\":{\"authorization\":{\"config\":null,\"type\":\"no-auth\"},\"body\":{\"data\":[],\"type\":\"none\"},\"headers\":\"\",\"method\":\"get\",\"params\":\"\",\"retryConfig\":{\"maxRetries\":3,\"retryEnabled\":true,\"retryInterval\":100},\"selected\":false,\"sslVerify\":true,\"timeout\":{\"maxConnectTimeout\":0,\"maxReadTimeout\":0,\"maxWriteTimeout\":0},\"title\":\"HTTP 请求\",\"type\":\"http-request\",\"url\":\"http://www.baidu.com\",\"variables\":[]},\"dragging\":false,\"height\":122,\"id\":\"1766123597284\",\"position\":{\"x\":684,\"y\":0},\"positionAbsolute\":{\"x\":684,\"y\":0},\"selected\":false,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"http-request\",\"width\":242},{\"data\":{\"outputs\":[{\"valueSelector\":[\"1766123597284\",\"body\"],\"valueType\":\"string\",\"variable\":\"body\"}],\"selected\":false,\"title\":\"输出\",\"type\":\"end\"},\"dragging\":false,\"height\":88,\"id\":\"1766123621822\",\"position\":{\"x\":1066,\"y\":32},\"positionAbsolute\":{\"x\":1066,\"y\":32},\"selected\":false,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"end\",\"width\":242},{\"data\":{\"code\":\"\\ndef main(arg1: str):\\n    return {\\n        \\\"result\\\": arg1,\\n    }\\n\",\"codeLanguage\":\"python3\",\"outputs\":{\"result\":{\"children\":null,\"type\":\"string\"}},\"selected\":false,\"title\":\"代码执行 2\",\"type\":\"code\",\"variables\":[{\"valueSelector\":[\"1711526002155\",\"input\"],\"valueType\":\"string\",\"variable\":\"arg1\"}]},\"dragging\":false,\"height\":52,\"id\":\"1766130047341\",\"position\":{\"x\":314.28571428571433,\"y\":161.42857142857144},\"positionAbsolute\":{\"x\":314.28571428571433,\"y\":161.42857142857144},\"selected\":false,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"code\",\"width\":242},{\"data\":{\"authorization\":{\"config\":null,\"type\":\"no-auth\"},\"body\":{\"data\":[],\"type\":\"none\"},\"headers\":\"\",\"method\":\"get\",\"params\":\"\",\"retryConfig\":{\"maxRetries\":3,\"retryEnabled\":true,\"retryInterval\":100},\"selected\":true,\"sslVerify\":true,\"timeout\":{\"maxConnectTimeout\":0,\"maxReadTimeout\":0,\"maxWriteTimeout\":0},\"title\":\"HTTP 请求 2\",\"type\":\"http-request\",\"url\":\"http://www.baidu.com\",\"variables\":[]},\"dragging\":false,\"height\":122,\"id\":\"1766130055356\",\"position\":{\"x\":728.5714285714287,\"y\":184.2857142857143},\"positionAbsolute\":{\"x\":728.5714285714287,\"y\":184.2857142857143},\"selected\":true,\"sourcePosition\":\"right\",\"targetPosition\":\"left\",\"type\":\"http-request\",\"width\":242}],\"viewport\":{\"x\":421,\"y\":181,\"zoom\":0.7}}";

        String ss = graphService.createEl(json);
        System.out.println(ss);
    }
}
