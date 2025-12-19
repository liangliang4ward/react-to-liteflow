package com.aicoding.flow;

import com.aicoding.flow.graph.GraphModel;
import com.aicoding.flow.graph.GraphService;

public class TestConverter {
    public static void main(String[] args) {
        try {
            GraphService graphService = new GraphService();
            
            // 创建一个简单的测试图
            GraphModel graph = new GraphModel();
            
            // 添加开始节点
            GraphModel.WorkNode startNode = new GraphModel.WorkNode();
            startNode.setId("start1");
            startNode.setType("start");
            
            // 添加代码节点
            GraphModel.WorkNode codeNode = new GraphModel.WorkNode();
            codeNode.setId("code1");
            codeNode.setType("code");
            
            // 添加HTTP请求节点
            GraphModel.WorkNode httpNode = new GraphModel.WorkNode();
            httpNode.setId("http1");
            httpNode.setType("http-request");
            
            // 添加结束节点
            GraphModel.WorkNode endNode = new GraphModel.WorkNode();
            endNode.setId("end1");
            endNode.setType("end");
            
            graph.getNodes().add(startNode);
            graph.getNodes().add(codeNode);
            graph.getNodes().add(httpNode);
            graph.getNodes().add(endNode);
            
            // 添加边
            GraphModel.EdgeNode edge1 = new GraphModel.EdgeNode();
            edge1.setId("edge1");
            edge1.setSource("start1");
            edge1.setTarget("code1");
            
            GraphModel.EdgeNode edge2 = new GraphModel.EdgeNode();
            edge2.setId("edge2");
            edge2.setSource("code1");
            edge2.setTarget("http1");
            
            GraphModel.EdgeNode edge3 = new GraphModel.EdgeNode();
            edge3.setId("edge3");
            edge3.setSource("http1");
            edge3.setTarget("end1");
            
            graph.getEdges().add(edge1);
            graph.getEdges().add(edge2);
            graph.getEdges().add(edge3);
            
            // 转换为EL表达式
            String elContent = graphService.createEl(graph);
            
            System.out.println("转换结果:");
            System.out.println("--------------------------");
            System.out.println(elContent);
            System.out.println("--------------------------");
            
            System.out.println("转换完成，结果已打印。");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}