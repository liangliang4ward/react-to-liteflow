package com.aicoding.flow.graph;

import com.aicoding.flow.graph.model.GraphModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GraphService测试类
 * @author gaoll
 * @time 2025/5/22 16:30
 **/
public class GraphServiceTest {

    private GraphService graphService;

    @BeforeEach
    public void setUp() {
        graphService = new GraphService();
    }

    /**
     * 测试基础顺序流场景（start→http→end）
     */
    @Test
    public void testBasicSequentialFlow() {
        System.out.println("Running testBasicSequentialFlow...");
        
        // 创建一个简单的顺序流图模型：start -> http -> end
        GraphModel graphModel = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start01");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加HTTP请求节点
        GraphModel.WorkNode httpNode = new GraphModel.WorkNode();
        httpNode.setId("http01");
        httpNode.setTitle("HTTP请求");
        httpNode.setType("http");
        nodes.add(httpNode);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end01");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
        edge1.setId("edge1");
        edge1.setSource("start01");
        edge1.setTarget("http01");
        edges.add(edge1);

        GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
        edge2.setId("edge2");
        edge2.setSource("http01");
        edge2.setTarget("end01");
        edges.add(edge2);

        graphModel.setNodes(nodes);
        graphModel.setEdges(edges);

        // 转换为EL表达式
        String elResult = graphService.createEl(graphModel);

        System.out.println("Generated EL for basic sequential flow:");
        System.out.println(elResult);
        assertNotNull(elResult);
        assertTrue(elResult.contains("_start01=StartNode.bind"));
        assertTrue(elResult.contains("_http01=HttpNode.bind"));
        assertTrue(elResult.contains("_end01=EndNode.bind"));
        assertTrue(elResult.contains("THEN(_start01, THEN(_http01, _end01))"));
    }

    /**
     * 测试if-else条件流场景
     */
    @Test
    public void testIfElseFlow() {
        System.out.println("Running testIfElseFlow...");
        
        GraphModel graphModel = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start01");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加if-else条件节点
        GraphModel.WorkNode ifNode = new GraphModel.WorkNode();
        ifNode.setId("if01");
        ifNode.setTitle("条件判断");
        ifNode.setType("if-else");
        nodes.add(ifNode);

        // 添加then分支节点
        GraphModel.WorkNode thenNode = new GraphModel.WorkNode();
        thenNode.setId("then01");
        thenNode.setTitle("Then分支");
        thenNode.setType("code");
        nodes.add(thenNode);

        // 添加else分支节点
        GraphModel.WorkNode elseNode = new GraphModel.WorkNode();
        elseNode.setId("else01");
        elseNode.setTitle("Else分支");
        elseNode.setType("code");
        nodes.add(elseNode);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end01");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
        edge1.setId("edge1");
        edge1.setSource("start01");
        edge1.setTarget("if01");
        edges.add(edge1);

        GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
        edge2.setId("edge2");
        edge2.setSource("if01");
        edge2.setTarget("then01");
        edges.add(edge2);

        GraphModel.EdgeModel edge3 = new GraphModel.EdgeModel();
        edge3.setId("edge3");
        edge3.setSource("if01");
        edge3.setTarget("else01");
        edges.add(edge3);

        GraphModel.EdgeModel edge4 = new GraphModel.EdgeModel();
        edge4.setId("edge4");
        edge4.setSource("then01");
        edge4.setTarget("end01");
        edges.add(edge4);

        GraphModel.EdgeModel edge5 = new GraphModel.EdgeModel();
        edge5.setId("edge5");
        edge5.setSource("else01");
        edge5.setTarget("end01");
        edges.add(edge5);

        graphModel.setNodes(nodes);
        graphModel.setEdges(edges);

        // 转换为EL表达式
        String elResult = graphService.createEl(graphModel);

        System.out.println("Generated EL for if-else flow:");
        System.out.println(elResult);
        assertNotNull(elResult);
        assertTrue(elResult.contains("_if01=ConditionNode.bind"));
        assertTrue(elResult.contains("IF(_if01, THEN(_then01, _end01), THEN(_else01, _end01))"));
    }

    /**
     * 测试多节点并发流场景
     */
    @Test
    public void testParallelFlow() {
        System.out.println("Running testParallelFlow...");
        
        GraphModel graphModel = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start01");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加并行执行的节点
        GraphModel.WorkNode parallelNode1 = new GraphModel.WorkNode();
        parallelNode1.setId("parallel01");
        parallelNode1.setTitle("并行任务1");
        parallelNode1.setType("http");
        nodes.add(parallelNode1);

        GraphModel.WorkNode parallelNode2 = new GraphModel.WorkNode();
        parallelNode2.setId("parallel02");
        parallelNode2.setTitle("并行任务2");
        parallelNode2.setType("http");
        nodes.add(parallelNode2);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end01");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
        edge1.setId("edge1");
        edge1.setSource("start01");
        edge1.setTarget("parallel01");
        edges.add(edge1);

        GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
        edge2.setId("edge2");
        edge2.setSource("start01");
        edge2.setTarget("parallel02");
        edges.add(edge2);

        GraphModel.EdgeModel edge3 = new GraphModel.EdgeModel();
        edge3.setId("edge3");
        edge3.setSource("parallel01");
        edge3.setTarget("end01");
        edges.add(edge3);

        GraphModel.EdgeModel edge4 = new GraphModel.EdgeModel();
        edge4.setId("edge4");
        edge4.setSource("parallel02");
        edge4.setTarget("end01");
        edges.add(edge4);

        graphModel.setNodes(nodes);
        graphModel.setEdges(edges);

        // 转换为EL表达式
        String elResult = graphService.createEl(graphModel);

        System.out.println("Generated EL for parallel flow:");
        System.out.println(elResult);
        assertNotNull(elResult);
        assertTrue(elResult.contains("THEN(_start01, WHEN(THEN(_parallel01, _end01), THEN(_parallel02, _end01)))"));
    }

    /**
     * 测试缺少开始节点的并行流场景
     * 验证代码是否能正确处理没有开始节点的异常情况
     */
    @Test
    public void testParallelFlowWithoutStartNode() {
        System.out.println("Running testParallelFlowWithoutStartNode...");
        
        GraphModel graphModel = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 不添加开始节点

        // 添加并行执行的节点
        GraphModel.WorkNode parallelNode1 = new GraphModel.WorkNode();
        parallelNode1.setId("parallel01");
        parallelNode1.setTitle("并行任务1");
        parallelNode1.setType("http");
        nodes.add(parallelNode1);

        GraphModel.WorkNode parallelNode2 = new GraphModel.WorkNode();
        parallelNode2.setId("parallel02");
        parallelNode2.setTitle("并行任务2");
        parallelNode2.setType("http");
        nodes.add(parallelNode2);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end01");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
        edge1.setId("edge1");
        edge1.setSource("parallel01");
        edge1.setTarget("end01");
        edges.add(edge1);

        GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
        edge2.setId("edge2");
        edge2.setSource("parallel02");
        edge2.setTarget("end01");
        edges.add(edge2);

        graphModel.setNodes(nodes);
        graphModel.setEdges(edges);

        // 预期会抛出异常，因为缺少开始节点
        assertThrows(RuntimeException.class, () -> {
            graphService.createEl(graphModel);
        });
    }
}
