package com.aicoding.flow.graph;

import com.aicoding.flow.graph.model.GraphModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * GraphService测试类
 * @author gaoll
 * @time 2025/5/22 16:30
 **/
@SpringBootTest
public class GraphServiceTest {

    @Autowired
    private GraphService graphService;

    /**
     * 测试基本的JSON到LiteFlow EL表达式的转换
     */
    @Test
    public void testCreateEl() {
        // 创建测试用的GraphModel
        GraphModel model = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start1");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加HTTP节点
        GraphModel.WorkNode httpNode = new GraphModel.WorkNode();
        httpNode.setId("http1");
        httpNode.setTitle("HTTP请求节点");
        httpNode.setType("http");
        nodes.add(httpNode);

        // 添加条件节点
        GraphModel.WorkNode conditionNode = new GraphModel.WorkNode();
        conditionNode.setId("condition1");
        conditionNode.setTitle("条件节点");
        conditionNode.setType("condition");
        nodes.add(conditionNode);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end1");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
        edge1.setId("edge1");
        edge1.setSource("start1");
        edge1.setTarget("http1");
        edges.add(edge1);

        GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
        edge2.setId("edge2");
        edge2.setSource("http1");
        edge2.setTarget("condition1");
        edges.add(edge2);

        GraphModel.EdgeModel edge3 = new GraphModel.EdgeModel();
        edge3.setId("edge3");
        edge3.setSource("condition1");
        edge3.setTarget("end1");
        edges.add(edge3);

        model.setNodes(nodes);
        model.setEdges(edges);

        // 调用转换方法
        String elExpression = graphService.createEl(model);

        // 验证结果
        assertNotNull(elExpression);
        System.out.println("Generated EL Expression: " + elExpression);
    }

    /**
     * 测试复杂流程的转换
     */
    @Test
    public void testComplexFlow() {
        // 可以在这里添加更复杂的测试用例
        // 例如包含多个分支、循环等的流程
    }
}
