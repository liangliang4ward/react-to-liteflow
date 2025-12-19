package com.aicoding.flow;

import com.aicoding.flow.graph.GraphService;
import com.aicoding.flow.graph.model.GraphModel;
import org.junit.Test;
import static org.junit.Assert.*;

public class GraphServiceTest {
    private final GraphService graphService = new GraphService();

    @Test
    public void testBasicSequenceFlow() {
        System.out.println("=== 测试基础顺序流转换 ===");
        
        String json = "{\n" +
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
            String result = graphService.createEl(json);
            System.out.println("转换结果:");
            System.out.println(result);
            
            // 验证节点绑定
            assertTrue("应包含开始节点绑定", result.contains("_start1=startNode.bind"));
            assertTrue("应包含代码节点绑定", result.contains("_code1=codeNode.bind"));
            assertTrue("应包含HTTP节点绑定", result.contains("_http1=httpNode.bind"));
            assertTrue("应包含结束节点绑定", result.contains("_end1=endNode.bind"));
            
            // 验证顺序流
            assertTrue("应包含顺序执行语句", result.contains("THEN(_start1,_code1,_http1,_end1)"));
            
            System.out.println("基础顺序流测试通过！");
        } catch (Exception e) {
            fail("基础顺序流转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testIfElseConditionFlow() {
        System.out.println("\n=== 测试条件流转换 ===");
        
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"start1\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"condition1\", \n" +
                "      \"type\": \"condition\", \n" +
                "      \"title\": \"条件判断\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code1\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"条件成立分支\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"code2\", \n" +
                "      \"type\": \"code\", \n" +
                "      \"title\": \"条件不成立分支\"\n" +
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
                "      \"target\": \"condition1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"condition1\", \n" +
                "      \"target\": \"code1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"condition1\", \n" +
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
            
            // 验证条件表达式
            assertTrue("应包含条件判断语句", result.contains("IF(_condition1,"));
            assertTrue("应包含条件分支", result.contains("THEN(_code1,_end1)"));
            assertTrue("应包含ELIF或ELSE分支", result.contains("THEN(_code2,_end1)"));
            
            System.out.println("条件流测试通过！");
        } catch (Exception e) {
            fail("条件流转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testParallelFlow() {
        System.out.println("\n=== 测试并行流转换 ===");
        
        String json = "{\n" +
                "  \"nodes\": [\n" +
                "    { \n" +
                "      \"id\": \"start1\", \n" +
                "      \"type\": \"start\", \n" +
                "      \"title\": \"开始节点\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"http1\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"http2\", \n" +
                "      \"type\": \"http-request\", \n" +
                "      \"title\": \"HTTP请求2\"\n" +
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
                "      \"target\": \"http1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e2\", \n" +
                "      \"source\": \"start1\", \n" +
                "      \"target\": \"http2\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e3\", \n" +
                "      \"source\": \"http1\", \n" +
                "      \"target\": \"end1\"\n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": \"e4\", \n" +
                "      \"source\": \"http2\", \n" +
                "      \"target\": \"end1\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        try {
            String result = graphService.createEl(json);
            System.out.println("转换结果:");
            System.out.println(result);
            
            // 验证并行执行
            assertTrue("应包含并行执行语句 WHEN", result.contains("WHEN("));
            assertTrue("应包含两个HTTP请求的并行执行", result.contains("THEN(_http1,_end1),THEN(_http2,_end1)"));
            
            System.out.println("并行流测试通过！");
        } catch (Exception e) {
            fail("并行流转换失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
