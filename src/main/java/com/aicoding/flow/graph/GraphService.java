package com.aicoding.flow.graph;

import cn.hutool.json.JSONUtil;
import com.aicoding.flow.constants.NodeConstant;
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
            throw new IllegalArgumentException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将GraphModel转换为LiteFlow EL表达式
     * @param graph 图形模型对象
     * @return LiteFlow EL表达式
     * @throws IllegalArgumentException 当图形数据不符合要求时抛出
     */
    private String createEl(GraphModel graph) {
        validateGraph(graph);
        
        StringBuilder elBuilder = new StringBuilder();
        
        // 生成节点绑定语句
        generateNodeBindings(graph, elBuilder);
        
        // 生成流程控制语句
        generateFlowControl(graph, elBuilder);
        
        return elBuilder.toString();
    }

    /**
     * 验证图形数据的有效性
     * @param graph 图形模型对象
     * @throws IllegalArgumentException 当图形数据不符合要求时抛出
     */
    private void validateGraph(GraphModel graph) {
        // 检查是否有开始节点
        long startNodeCount = graph.getNodes().stream()
                .filter(node -> "start".equals(node.getType()))
                .count();
        if (startNodeCount == 0) {
            throw new IllegalArgumentException("必须有一个开始节点");
        }
        if (startNodeCount > 1) {
            throw new IllegalArgumentException("只能有一个开始节点");
        }

        // 检查是否有结束节点
        long endNodeCount = graph.getNodes().stream()
                .filter(node -> "end".equals(node.getType()))
                .count();
        if (endNodeCount == 0) {
            throw new IllegalArgumentException("至少有一个结束节点");
        }

        // 检查节点类型是否都支持
        for (GraphModel.WorkNode node : graph.getNodes()) {
            if (!SUPPORTED_NODE_TYPES.contains(node.getType())) {
                throw new IllegalArgumentException("不支持的节点类型: " + node.getType() + ", 节点ID: " + node.getId());
            }
        }

        // 检查孤立节点
        Set<String> connectedNodeIds = new HashSet<>();
        for (GraphModel.EdgeModel edge : graph.getEdges()) {
            connectedNodeIds.add(edge.getSource());
            connectedNodeIds.add(edge.getTarget());
        }
        
        for (GraphModel.WorkNode node : graph.getNodes()) {
            // 开始节点和结束节点允许孤立（但通常不应该）
            if (!connectedNodeIds.contains(node.getId()) && !"start".equals(node.getType()) && !"end".equals(node.getType())) {
                throw new IllegalArgumentException("孤立节点: 节点ID: " + node.getId() + ", 节点类型: " + node.getType());
            }
        }
    }

    /**
     * 生成节点绑定语句
     * @param graph 图形模型对象
     * @param elBuilder EL表达式构建器
     */
    private void generateNodeBindings(GraphModel graph, StringBuilder elBuilder) {
        int index = 1;
        for (GraphModel.WorkNode node : graph.getNodes()) {
            String nodeId = "_" + node.getId();
            String nodeName = NODE_TYPE_TO_NAME.get(node.getType());
            
            // 构建节点参数JSON
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", node.getId());
            paramMap.put("nodeId", nodeId);
            paramMap.put("index", index++);
            paramMap.put("streamFlag", true);
            paramMap.put("params", node.getData() != null && node.getData().getParams() != null ? node.getData().getParams() : new ArrayList<>());
            paramMap.put("nodeType", node.getType());
            paramMap.put("nodeName", nodeName);
            
            // 添加特定类型节点的额外参数
            if ("http-request".equals(node.getType()) && node.getData() != null) {
                paramMap.put("method", node.getData().getMethod());
                paramMap.put("url", node.getData().getUrl());
            }
            
            String paramJson = JSONUtil.toJsonStr(paramMap);
            
            // 生成绑定语句
            elBuilder.append(String.format("%s=%s.bind(\"%s\",'%s') ;\n", 
                    nodeId, nodeName, NodeConstant.PARAM, paramJson));
        }
    }

    /**
     * 生成流程控制语句
     * @param graph 图形模型对象
     * @param elBuilder EL表达式构建器
     */
    private void generateFlowControl(GraphModel graph, StringBuilder elBuilder) {
        // 查找开始节点
        GraphModel.WorkNode startNode = graph.getNodes().stream()
                .filter(node -> "start".equals(node.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到开始节点"));
        
        // 构建节点连接关系
        Map<String, List<String>> nodeConnections = new HashMap<>();
        for (GraphModel.EdgeModel edge : graph.getEdges()) {
            nodeConnections.computeIfAbsent(edge.getSource(), k -> new ArrayList<>())
                    .add(edge.getTarget());
        }
        
        // 生成流程控制语句
        String flowControl = buildFlowControl(startNode.getId(), nodeConnections, graph);
        elBuilder.append(flowControl);
    }

    /**
     * 递归构建流程控制语句
     * @param currentNodeId 当前节点ID
     * @param nodeConnections 节点连接关系
     * @param graph 图形模型对象
     * @return 流程控制语句
     */
    private String buildFlowControl(String currentNodeId, Map<String, List<String>> nodeConnections, GraphModel graph) {
        // 检查是否为条件节点
        GraphModel.WorkNode currentNode = graph.getNodes().stream()
                .filter(node -> node.getId().equals(currentNodeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("找不到节点: " + currentNodeId));
        
        if ("condition".equals(currentNode.getType())) {
            return buildConditionFlow(currentNode, nodeConnections, graph);
        }
        
        // 获取当前节点的所有目标节点
        List<String> targets = nodeConnections.getOrDefault(currentNodeId, new ArrayList<>());
        
        if (targets.isEmpty()) {
            // 没有后续节点
            return "_" + currentNodeId;
        } else if (targets.size() == 1) {
            // 单个后续节点，使用THEN
            return String.format("THEN(_%s, %s)", currentNodeId, 
                    buildFlowControl(targets.get(0), nodeConnections, graph));
        } else {
            // 多个后续节点，使用WHEN（并行）
            String parallelNodes = targets.stream()
                    .map(target -> buildFlowControl(target, nodeConnections, graph))
                    .collect(Collectors.joining(", "));
            return String.format("THEN(_%s, WHEN(%s))", currentNodeId, parallelNodes);
        }
    }

    /**
     * 构建条件节点的流程控制语句
     * @param conditionNode 条件节点
     * @param nodeConnections 节点连接关系
     * @param graph 图形模型对象
     * @return 条件流程控制语句
     */
    private String buildConditionFlow(GraphModel.WorkNode conditionNode, Map<String, List<String>> nodeConnections, GraphModel graph) {
        // 获取条件节点的所有目标节点
        List<String> targets = nodeConnections.getOrDefault(conditionNode.getId(), new ArrayList<>());
        
        if (targets.isEmpty()) {
            throw new IllegalArgumentException("if-else 节点[id: " + conditionNode.getId() + "]未配置分支目标节点");
        }
        
        // 构建IF/ELIF/ELSE语句
        StringBuilder conditionBuilder = new StringBuilder();
        conditionBuilder.append("IF(_").append(conditionNode.getId()).append(", ");
        
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.get(i);
            String targetFlow = buildFlowControl(target, nodeConnections, graph);
            
            if (i == 0) {
                conditionBuilder.append(targetFlow);
            } else if (i == targets.size() - 1) {
                conditionBuilder.append(", ELSE(").append(targetFlow).append(")");
            } else {
                conditionBuilder.append(", ELIF(_").append(conditionNode.getId()).append(", ").append(targetFlow).append(")");
            }
        }
        
        conditionBuilder.append(")");
        return conditionBuilder.toString();
    }
}
