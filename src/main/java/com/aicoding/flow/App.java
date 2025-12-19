package com.aicoding.flow;

import com.aicoding.flow.graph.GraphService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * Hello world!
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);
        
        // 测试 GraphService
        testGraphService(context);
    }
    
    private static void testGraphService(ApplicationContext context) {
        GraphService graphService = context.getBean(GraphService.class);
        
        System.out.println("\n==========================================");
        System.out.println("开始测试 GraphService.createEl() 方法");
        System.out.println("==========================================");
        
        // 测试基础顺序流
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
                "      \"target\": \"end1\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        try {
            String result = graphService.createEl(testJson);
            System.out.println("\n转换成功！");
            System.out.println("生成的 EL 表达式:");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("\n转换失败:");
            e.printStackTrace();
        }
    }
}
