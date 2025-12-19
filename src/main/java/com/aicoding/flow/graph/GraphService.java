package com.aicoding.flow.graph;

import cn.hutool.json.JSONUtil;
import com.aicoding.flow.graph.model.GraphModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图形服务类，负责将ReactFlow数据转换为LiteFlow EL表达式
 * @author gaoll
 * @time 2025/5/22 15:48
 **/
@Service
public class GraphService {
    // 支持的节点类型
    private static final Set<String> SUPPORTED_NODE_TYPES = new HashSet<>(Arrays.asList("start", "end", "http-request", "code", "condition"));
    // 节点类型到节点名称的映射
    private static final Map<String, String> NODE_TYPE_TO_NAME = new HashMap<>();
    static {
        NODE_TYPE_TO_NAME.put("start", "startNode");
        NODE_TYPE_TO_NAME.put("end", "endNode");
        NODE_TYPE_TO_NAME.put("http-request", "httpNode");
        NODE_TYPE_TO_NAME.put("code", "codeNode");
        NODE_TYPE_TO_NAME.put("condition", "conditionNode");
    }

    /**
     * 将ReactFlow JSON数据转换为LiteFlow EL表达式
     * @param json ReactFlow数据的JSON字符串
     * @return LiteFlow EL表达式
     * @throws IllegalArgumentException 当输入数据不符合要求时抛出
     */
    public String createEl(String json) {
        try {
            // 解析JSON为GraphModel
            GraphModel graph = JSONUtil.toBean(json, GraphModel.class);
            return createEl(graph);
        } catch (Exception e) {
            throw new IllegalArgumentException("转换失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 ReactFlow 图形转换为 LiteFlow EL 表达式
     * @param graph 图形模型
     * @return LiteFlow EL 表达式
     */
    public String createEl(GraphModel graph) {
        // 验证图形数据
        if (graph == null || graph.getNodes().isEmpty()) {
            throw new IllegalArgumentException("图形数据不能为空");
        }
        
        StringBuilder elBuilder = new StringBuilder();
        elBuilder.append("# 生成的 LiteFlow EL 表达式\n");
        
        // 生成节点绑定
        String nodeBindings = generateNodeBindings(graph);
        elBuilder.append(nodeBindings);
        
        // 生成流程控制语句
        String flowControl = generateFlowControl(graph);
        elBuilder.append(flowControl);
        
        String result = elBuilder.toString();
        System.out.println("转换完成，结果: \n" + result);
        return result;
    }

    /**
     * 验证图形数据的有效性
     * @param graph 图形模型
     * @throws IllegalArgumentException 当验证失败时抛出
     */
    private void validateGraph(GraphModel graph) {
        // 检查是否有开始节点
        long startNodeCount = graph.getNodes().stream()
                .filter(node -> "start".equals(node.getType()))
                .count();
        if (startNodeCount != 1) {
            throw new IllegalArgumentException("必须有且仅有一个开始节点");
        }

        // 检查是否有结束节点
        long endNodeCount = graph.getNodes().stream()
                .filter(node -> "end".equals(node.getType()))
                .count();
        if (endNodeCount < 1) {
            throw new IllegalArgumentException("至少需要有一个结束节点");
        }

        // 检查是否有不支持的节点类型
        List<String> unsupportedNodes = graph.getNodes().stream()
                .filter(node -> !SUPPORTED_NODE_TYPES.contains(node.getType()))
                .map(GraphModel.WorkNode::getId)
                .collect(Collectors.toList());
        if (!unsupportedNodes.isEmpty()) {
            throw new IllegalArgumentException("存在不支持的节点类型，节点ID: " + unsupportedNodes);
        }

        // 检查孤立节点
        Set<String> connectedNodes = new HashSet<>();
        for (GraphModel.EdgeModel edge : graph.getEdges()) {
            connectedNodes.add(edge.getSource());
            connectedNodes.add(edge.getTarget());
        }
        // 开始节点和结束节点允许孤立（虽然通常不会）
        List<String> isolatedNodes = graph.getNodes().stream()
                .filter(node -> !connectedNodes.contains(node.getId()) && !"start".equals(node.getType()) && !"end".equals(node.getType()))
                .map(GraphModel.WorkNode::getId)
                .collect(Collectors.toList());
        if (!isolatedNodes.isEmpty()) {
            throw new IllegalArgumentException("存在孤立节点（非开始/结束节点），节点ID: " + isolatedNodes);
        }
    }

    /**
     * 生成节点绑定语句
     * @param graph 图形模型
     * @return 节点绑定语句
     */
    private String generateNodeBindings(GraphModel graph) {
        StringBuilder bindings = new StringBuilder();
        
        for (GraphModel.WorkNode node : graph.getNodes()) {
            String nodeId = node.getId();
            String nodeType = node.getType();
            
            // 根据节点类型生成不同的绑定语句
            String binding;
            switch (nodeType) {
                case "start":
                    binding = String.format("_%s=startNode.bind(\"param\",'');\n", nodeId);
                    break;
                case "end":
                    binding = String.format("_%s=endNode.bind(\"param\",'');\n", nodeId);
                    break;
                case "code":
                    binding = String.format("_%s=codeNode.bind(\"param\",'');\n", nodeId);
                    break;
                case "http-request":
                    binding = String.format("_%s=httpNode.bind(\"param\",'');\n", nodeId);
                    break;
                case "condition":
                    binding = String.format("_%s=conditionNode.bind(\"param\",'');\n", nodeId);
                    break;
                default:
                    binding = String.format("_%s=customNode.bind(\"param\",'');\n", nodeId);
                    break;
            }
            bindings.append(binding);
        }
        
        return bindings.toString();
    }

    /**
     * 生成流程控制语句
     * @param graph 图形模型
     * @return 流程控制语句
     */
    private String generateFlowControl(GraphModel graph) {
        // 找到开始节点
        GraphModel.WorkNode startNode = null;
        for (GraphModel.WorkNode node : graph.getNodes()) {
            if ("start".equals(node.getType())) {
                startNode = node;
                break;
            }
        }
        
        if (startNode == null) {
            throw new IllegalArgumentException("图形中必须包含开始节点");
        }
        
        // 递归构建流程控制
        return "FLOW=" + buildFlow(startNode, graph, new HashSet<>()) + ";\n";
    }

    /**
     * 递归构建流程
     * @param currentNode 当前处理的节点
     * @param graph 图形模型
     * @param visited 已访问的节点集合
     * @return 流程控制语句
     */
    private String buildFlow(GraphModel.WorkNode currentNode, GraphModel graph, Set<String> visited) {
        if (visited.contains(currentNode.getId())) {
            return "";
        }
        visited.add(currentNode.getId());
        
        String currentNodeRef;
        String nodeId = currentNode.getId();
        
        // 确定当前节点的引用 - 使用下划线前缀
        currentNodeRef = "_" + nodeId;

        
        // 找到所有从当前节点出发的边
        List<GraphModel.EdgeModel> outgoingEdges = new ArrayList<>();
        for (GraphModel.EdgeModel edge : graph.getEdges()) {
            if (edge.getSource().equals(currentNode.getId())) {
                outgoingEdges.add(edge);
            }
        }
        
        // 如果没有后续节点，直接返回当前节点
        if (outgoingEdges.isEmpty()) {
            return currentNodeRef;
        }
        
        // 处理多个后续节点
        if (outgoingEdges.size() == 1) {
            // 单个后续节点 - 顺序执行
            GraphModel.EdgeModel edge = outgoingEdges.get(0);
            GraphModel.WorkNode nextNode = findNodeById(graph, edge.getTarget());
            
            if (nextNode != null) {
                String nextFlow = buildFlow(nextNode, graph, visited);
                return String.format("THEN(%s, %s)", currentNodeRef, nextFlow);
            }
        } else {
            // 多个后续节点 - 并行执行
            StringBuilder parallelNodes = new StringBuilder();
            for (GraphModel.EdgeModel edge : outgoingEdges) {
                GraphModel.WorkNode nextNode = findNodeById(graph, edge.getTarget());
                if (nextNode != null) {
                    String nextFlow = buildFlow(nextNode, graph, visited);
                    if (parallelNodes.length()!=0) {
                        parallelNodes.append(", ");
                    }
                    parallelNodes.append(nextFlow);
                }
            }
            return String.format("WHEN(%s)", parallelNodes.toString());
        }
        
        return currentNodeRef;
    }
    
    /**
     * 根据节点ID查找节点
     * @param graph 图形模型
     * @param nodeId 节点ID
     * @return 找到的节点，未找到则返回null
     */
    private GraphModel.WorkNode findNodeById(GraphModel graph, String nodeId) {
        return graph.getNodes().stream()
                .filter(node -> node.getId().equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 构建条件表达式
     * @param conditionNode 条件节点
     * @param nodeConnections 节点连接关系
     * @param graph 图形模型
     * @return 条件表达式片段
     */
    private String buildConditionExpression(GraphModel.WorkNode conditionNode, Map<String, List<String>> nodeConnections, GraphModel graph) {
        // 获取条件节点的所有目标节点
        List<String> targets = nodeConnections.getOrDefault(conditionNode.getId(), new ArrayList<>());
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("if-else 节点[id:" + conditionNode.getId() + "]未配置分支目标节点");
        }

        // 构建 IF/ELIF/ELSE 表达式
        StringBuilder conditionExpr = new StringBuilder();
        conditionExpr.append("IF(_").append(conditionNode.getId()).append(",");

        for (int i = 0; i < targets.size(); i++) {
            String target = targets.get(i);
            String targetExpr = buildFlowExpression(target, nodeConnections, graph);

            if (i == 0) {
                conditionExpr.append(targetExpr);
            } else if (i == targets.size() - 1 && targets.size() > 2) {
                conditionExpr.append(",ELSE(").append(targetExpr).append(")");
            } else {
                conditionExpr.append(",ELIF(").append(targetExpr).append(")");
            }
        }

        conditionExpr.append(")");
        return conditionExpr.toString();
    }

    public static void main(String[] args) {
        System.out.println("=== 启动 GraphService 测试 ===");
        
        try {
            // 创建测试用的 GraphModel
            GraphModel graph = new GraphModel();
            
            // 创建节点
            List<GraphModel.WorkNode> nodes = new ArrayList<>();
            
            // 开始节点
            GraphModel.WorkNode startNode = new GraphModel.WorkNode();
            startNode.setId("start-1");
            startNode.setType("start");
            nodes.add(startNode);
            
            // 代码节点
            GraphModel.WorkNode codeNode = new GraphModel.WorkNode();
            codeNode.setId("code-1");
            codeNode.setType("code");
            GraphModel.WorkNodeData codeData = new GraphModel.WorkNodeData();
            codeData.setComponentName("com.example.CodeComponent");
            codeNode.setData(codeData);
            nodes.add(codeNode);
            
            // HTTP 请求节点
            GraphModel.WorkNode httpNode = new GraphModel.WorkNode();
            httpNode.setId("http-1");
            httpNode.setType("http-request");
            GraphModel.WorkNodeData httpData = new GraphModel.WorkNodeData();
            httpData.setComponentName("com.example.HttpComponent");
            httpNode.setData(httpData);
            nodes.add(httpNode);
            
            // 结束节点
            GraphModel.WorkNode endNode = new GraphModel.WorkNode();
            endNode.setId("end-1");
            endNode.setType("end");
            nodes.add(endNode);
            
            // 创建边
            List<GraphModel.EdgeModel> edges = new ArrayList<>();
            
            GraphModel.EdgeModel edge1 = new GraphModel.EdgeModel();
            edge1.setSource("start-1");
            edge1.setTarget("code-1");
            edges.add(edge1);
            
            GraphModel.EdgeModel edge2 = new GraphModel.EdgeModel();
            edge2.setSource("code-1");
            edge2.setTarget("http-1");
            edges.add(edge2);
            
            GraphModel.EdgeModel edge3 = new GraphModel.EdgeModel();
            edge3.setSource("http-1");
            edge3.setTarget("end-1");
            edges.add(edge3);
            
            graph.setNodes(nodes);
            graph.setEdges(edges);
            
            // 创建 GraphService 实例
            GraphService service = new GraphService();
            
            // 执行转换
            String elResult = service.createEl(graph);
            
            // 将结果写入文件
            java.io.FileWriter writer = new java.io.FileWriter("test_result.txt");
            writer.write("=== 转换结果 ===\n");
            writer.write(elResult);
            writer.close();
            
            System.out.println("转换完成，结果已保存到 test_result.txt");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
            
            // 将错误信息写入文件
            try {
                java.io.FileWriter writer = new java.io.FileWriter("error_log.txt");
                writer.write("错误信息: " + e.getMessage() + "\n");
                e.printStackTrace(new java.io.PrintWriter(writer));
                writer.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
