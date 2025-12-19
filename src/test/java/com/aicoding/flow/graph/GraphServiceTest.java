package com.aicoding.flow.graph;

import com.aicoding.flow.graph.model.GraphModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * GraphService测试类
 * 测试覆盖：基础顺序流、if-else条件流、多节点并发流
 * @author gaoll
 * @time 2025/5/22 16:30
 **/
@SpringBootTest
public class GraphServiceTest {

    @Autowired
    private GraphService graphService;

    /**
     * 测试基础顺序流转换
     * 场景：start -> http -> answer -> end
     */
    @Test
    public void testBasicSequentialFlow() {
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

        // 添加回答节点
        GraphModel.WorkNode answerNode = new GraphModel.WorkNode();
        answerNode.setId("answer1");
        answerNode.setTitle("回答节点");
        answerNode.setType("answer");
        nodes.add(answerNode);

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
        edge2.setTarget("answer1");
        edges.add(edge2);

        GraphModel.EdgeModel edge3 = new GraphModel.EdgeModel();
        edge3.setId("edge3");
        edge3.setSource("answer1");
        edge3.setTarget("end1");
        edges.add(edge3);

        model.setNodes(nodes);
        model.setEdges(edges);

        // 调用转换方法
        String elExpression = graphService.createEl(model);

        // 验证结果
        assertNotNull(elExpression);
        System.out.println("基础顺序流生成的EL表达式: ");
        System.out.println(elExpression);
        System.out.println("\n");
    }

    /**
     * 测试if-else条件流转换
     * 场景：start -> if-else -> (then分支: http1 -> end) / (else分支: answer1 -> end)
     */
    @Test
    public void testIfElseFlow() {
        GraphModel model = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start1");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加if-else条件节点
        GraphModel.WorkNode ifElseNode = new GraphModel.WorkNode();
        ifElseNode.setId("ifelse1");
        ifElseNode.setTitle("条件节点");
        ifElseNode.setType("if-else");
        nodes.add(ifElseNode);

        // 添加then分支节点
        GraphModel.WorkNode httpNode = new GraphModel.WorkNode();
        httpNode.setId("http1");
        httpNode.setTitle("HTTP请求节点");
        httpNode.setType("http");
        nodes.add(httpNode);

        // 添加else分支节点
        GraphModel.WorkNode answerNode = new GraphModel.WorkNode();
        answerNode.setId("answer1");
        answerNode.setTitle("回答节点");
        answerNode.setType("answer");
        nodes.add(answerNode);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end1");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        edges.add(createEdge("edge1", "start1", "ifelse1"));
        edges.add(createEdge("edge2", "ifelse1", "http1"));
        edges.add(createEdge("edge3", "ifelse1", "answer1"));
        edges.add(createEdge("edge4", "http1", "end1"));
        edges.add(createEdge("edge5", "answer1", "end1"));

        model.setNodes(nodes);
        model.setEdges(edges);

        String elExpression = graphService.createEl(model);
        assertNotNull(elExpression);
        System.out.println("if-else条件流生成的EL表达式: ");
        System.out.println(elExpression);
        System.out.println("\n");
    }

    /**
     * 测试多节点并发流转换
     * 场景：start -> when (http1, http2) -> end
     */
    @Test
    public void testParallelFlow() {
        GraphModel model = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加开始节点
        GraphModel.WorkNode startNode = new GraphModel.WorkNode();
        startNode.setId("start1");
        startNode.setTitle("开始节点");
        startNode.setType("start");
        nodes.add(startNode);

        // 添加并行执行的HTTP节点
        GraphModel.WorkNode httpNode1 = new GraphModel.WorkNode();
        httpNode1.setId("http1");
        httpNode1.setTitle("HTTP请求节点1");
        httpNode1.setType("http");
        nodes.add(httpNode1);

        GraphModel.WorkNode httpNode2 = new GraphModel.WorkNode();
        httpNode2.setId("http2");
        httpNode2.setTitle("HTTP请求节点2");
        httpNode2.setType("http");
        nodes.add(httpNode2);

        // 添加结束节点
        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end1");
        endNode.setTitle("结束节点");
        endNode.setType("end");
        nodes.add(endNode);

        // 添加边连接
        edges.add(createEdge("edge1", "start1", "http1"));
        edges.add(createEdge("edge2", "start1", "http2"));
        edges.add(createEdge("edge3", "http1", "end1"));
        edges.add(createEdge("edge4", "http2", "end1"));

        model.setNodes(nodes);
        model.setEdges(edges);

        String elExpression = graphService.createEl(model);
        assertNotNull(elExpression);
        System.out.println("多节点并发流生成的EL表达式: ");
        System.out.println(elExpression);
        System.out.println("\n");
    }

    /**
     * 辅助方法：创建边
     */
    private GraphModel.EdgeModel createEdge(String id, String source, String target) {
        GraphModel.EdgeModel edge = new GraphModel.EdgeModel();
        edge.setId(id);
        edge.setSource(source);
        edge.setTarget(target);
        return edge;
    }

    /**
     * 测试缺少开始节点的异常情况
     */
    @Test
    public void testMissingStartNode() {
        GraphModel model = new GraphModel();
        List<GraphModel.WorkNode> nodes = new ArrayList<>();
        List<GraphModel.EdgeModel> edges = new ArrayList<>();

        // 添加普通节点但没有开始节点
        GraphModel.WorkNode node1 = new GraphModel.WorkNode();
        node1.setId("parallel01");
        node1.setType("http");
        nodes.add(node1);

        GraphModel.WorkNode node2 = new GraphModel.WorkNode();
        node2.setId("parallel02");
        node2.setType("http");
        nodes.add(node2);

        GraphModel.WorkNode endNode = new GraphModel.WorkNode();
        endNode.setId("end01");
        endNode.setType("end");
        nodes.add(endNode);

        model.setNodes(nodes);
        model.setEdges(edges);

        // 预期会抛出异常，因为缺少开始节点
        assertThrows(RuntimeException.class, () -> {
            graphService.createEl(model);
        });
    }
}
