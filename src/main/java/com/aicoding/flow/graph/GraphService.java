package com.aicoding.flow.graph;

import com.aicoding.flow.graph.model.GraphModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 图结构转 LiteFlow EL 表达式服务
 * 核心功能：将 JSON 格式的流程图模型转换为符合 LiteFlow 规范的 EL 表达式
 * @author gaoll
 * @time 2025/5/22 15:48
 **/
@Service
public class GraphService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 将 GraphModel 转换为 LiteFlow EL 表达式
     * @param model 流程图模型
     * @return LiteFlow EL 表达式
     * @throws RuntimeException 处理转换过程中的各种异常
     */
    public String createEl(GraphModel model) {
        validateModel(model);
        
        Map<String, GraphModel.WorkNode> nodeMap = model.getNodes().stream()
                .collect(Collectors.toMap(GraphModel.WorkNode::getId, node -> node));
        
        Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap = model.getEdges().stream()
                .collect(Collectors.groupingBy(GraphModel.EdgeModel::getSource));
        
        List<String> bindingStatements = generateBindingStatements(model.getNodes());
        String flowExpression = generateFlowExpression(model, nodeMap, edgeBySourceMap);
        
        StringBuilder elBuilder = new StringBuilder();
        bindingStatements.forEach(statement -> elBuilder.append(statement).append("\n"));
        elBuilder.append(flowExpression);
        
        return elBuilder.toString();
    }
    
    /**
     * 验证模型的完整性和正确性
     * @param model 流程图模型
     * @throws RuntimeException 验证失败时抛出异常
     */
    private void validateModel(GraphModel model) {
        if (model == null || model.getNodes() == null || model.getEdges() == null) {
            throw new RuntimeException("模型数据不完整");
        }
        
        // 验证所有节点都有唯一ID
        Set<String> nodeIds = new HashSet<>();
        for (GraphModel.WorkNode node : model.getNodes()) {
            if (node.getId() == null || node.getId().isEmpty()) {
                throw new RuntimeException("存在未设置ID的节点");
            }
            if (!nodeIds.add(node.getId())) {
                throw new RuntimeException("存在重复ID的节点: " + node.getId());
            }
        }
        
        // 验证所有边的源节点和目标节点都存在
        for (GraphModel.EdgeModel edge : model.getEdges()) {
            if (!nodeIds.contains(edge.getSource())) {
                throw new RuntimeException("边的源节点不存在: " + edge.getSource());
            }
            if (!nodeIds.contains(edge.getTarget())) {
                throw new RuntimeException("边的目标节点不存在: " + edge.getTarget());
            }
        }
        
        // 验证开始节点存在
        boolean hasStartNode = model.getNodes().stream()
                .anyMatch(node -> "start".equals(node.getType()));
        if (!hasStartNode) {
            throw new RuntimeException("缺少开始节点");
        }
        
        // 验证结束节点存在
        boolean hasEndNode = model.getNodes().stream()
                .anyMatch(node -> "end".equals(node.getType()));
        if (!hasEndNode) {
            throw new RuntimeException("缺少结束节点");
        }
        
        // 验证不存在孤立节点
        Set<String> connectedNodes = new HashSet<>();
        model.getEdges().forEach(edge -> {
            connectedNodes.add(edge.getSource());
            connectedNodes.add(edge.getTarget());
        });
        for (GraphModel.WorkNode node : model.getNodes()) {
            if (!connectedNodes.contains(node.getId())) {
                throw new RuntimeException("存在孤立节点: " + node.getId());
            }
        }
    }
    
    /**
     * 生成节点绑定语句
     * @param nodes 节点列表
     * @return 节点绑定语句列表
     */
    private List<String> generateBindingStatements(List<GraphModel.WorkNode> nodes) {
        List<String> statements = new ArrayList<>();
        
        for (int index = 0; index < nodes.size(); index++) {
            GraphModel.WorkNode node = nodes.get(index);
            String boundId = "_" + node.getId();
            String nodeType = node.getType();
            
            // 根据节点类型确定对应的 Node 类
            String nodeClassName = getNodeClassName(nodeType);
            
            // 构建参数 JSON
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", node.getId());
            paramMap.put("nodeId", boundId);
            paramMap.put("index", index);
            paramMap.put("streamFlag", true);
            paramMap.put("params", new ArrayList<>());
            paramMap.put("nodeType", nodeType);
            paramMap.put("nodeName", nodeClassName);
            
            try {
                String paramJson = objectMapper.writeValueAsString(paramMap);
                String statement = String.format("%s=%s.bind(\"param\",'%s') ;", 
                    boundId, nodeClassName, paramJson.replace("'", "\\'"));
                statements.add(statement);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("生成节点绑定语句失败: " + node.getId(), e);
            }
        }
        
        return statements;
    }
    
    /**
     * 根据节点类型获取对应的 Node 类名
     * @param nodeType 节点类型
     * @return Node 类名
     */
    private String getNodeClassName(String nodeType) {
        switch (nodeType) {
            case "start":
                return "StartNode";
            case "end":
                return "EndNode";
            case "http":
                return "HttpNode";
            case "if-else":
                return "ConditionNode";
            case "code":
                return "CodeNode";
            case "json-extract":
                return "JsonExtractNode";
            case "while-condition":
                return "WhileConditionNode";
            default:
                throw new RuntimeException("不支持的节点类型: " + nodeType);
        }
    }
    
    /**
     * 生成流程控制表达式
     * @param model 流程图模型
     * @param nodeMap 节点映射
     * @param edgeBySourceMap 边关系映射
     * @return 流程控制表达式
     */
    private String generateFlowExpression(GraphModel model, 
            Map<String, GraphModel.WorkNode> nodeMap, 
            Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap) {
        // 找到开始节点
        GraphModel.WorkNode startNode = model.getNodes().stream()
                .filter(node -> "start".equals(node.getType()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("未找到开始节点"));
        
        return buildExpressionRecursive(startNode.getId(), new HashSet<>(), nodeMap, edgeBySourceMap);
    }
    
    /**
     * 递归构建流程表达式
     * @param currentNodeId 当前节点ID
     * @param visited 已访问节点集合
     * @param nodeMap 节点映射
     * @param edgeBySourceMap 边关系映射
     * @return 流程表达式
     */
    private String buildExpressionRecursive(String currentNodeId, Set<String> visited, 
            Map<String, GraphModel.WorkNode> nodeMap, 
            Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap) {
        if (visited.contains(currentNodeId)) {
            throw new RuntimeException("检测到循环引用: " + currentNodeId);
        }
        
        visited.add(currentNodeId);
        GraphModel.WorkNode currentNode = nodeMap.get(currentNodeId);
        
        // 处理结束节点
        if ("end".equals(currentNode.getType())) {
            visited.remove(currentNodeId);
            return "_" + currentNodeId;
        }
        
        // 处理条件节点
        if ("if-else".equals(currentNode.getType())) {
            String result = buildIfExpression(currentNodeId, visited, nodeMap, edgeBySourceMap);
            visited.remove(currentNodeId);
            return result;
        }
        
        // 处理其他节点
        List<GraphModel.EdgeModel> outgoingEdges = edgeBySourceMap.getOrDefault(currentNodeId, Collections.emptyList());
        
        if (outgoingEdges.isEmpty()) {
            visited.remove(currentNodeId);
            return "_" + currentNodeId;
        }
        
        // 处理顺序节点
        if (outgoingEdges.size() == 1) {
            String nextNodeId = outgoingEdges.get(0).getTarget();
            String nextExpression = buildExpressionRecursive(nextNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap);
            
            visited.remove(currentNodeId);
            return String.format("THEN(_%s, %s)", currentNodeId, nextExpression);
        }
        
        // 处理并行节点
        List<String> parallelExpressions = new ArrayList<>();
        for (GraphModel.EdgeModel edge : outgoingEdges) {
            String nextNodeId = edge.getTarget();
            parallelExpressions.add(buildExpressionRecursive(nextNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap));
        }
        
        String parallelExpr = String.format("WHEN(%s)", String.join(", ", parallelExpressions));
        
        visited.remove(currentNodeId);
        return String.format("THEN(_%s, %s)", currentNodeId, parallelExpr);
    }
    
    /**
     * 构建 IF 条件表达式
     * @param ifNodeId IF 节点ID
     * @param visited 已访问节点集合
     * @param nodeMap 节点映射
     * @param edgeBySourceMap 边关系映射
     * @return IF 条件表达式
     */
    private String buildIfExpression(String ifNodeId, Set<String> visited, 
            Map<String, GraphModel.WorkNode> nodeMap, 
            Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap) {
        List<GraphModel.EdgeModel> ifEdges = edgeBySourceMap.getOrDefault(ifNodeId, Collections.emptyList());
        
        if (ifEdges.size() < 2) {
            throw new RuntimeException("IF 节点必须至少有两个分支");
        }
        
        // 构建 IF 表达式
        StringBuilder ifBuilder = new StringBuilder();
        ifBuilder.append("IF(_").append(ifNodeId).append(", ");
        
        // 处理 THEN 分支
        String thenNodeId = ifEdges.get(0).getTarget();
        String thenBranch = buildExpressionRecursive(thenNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap);
        ifBuilder.append(thenBranch);
        
        // 处理 ELSE 分支
        if (ifEdges.size() >= 2) {
            String elseNodeId = ifEdges.get(1).getTarget();
            String elseBranch = buildExpressionRecursive(elseNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap);
            ifBuilder.append(", " + elseBranch);
        }
        
        ifBuilder.append(")");
        
        return ifBuilder.toString();
    }
}
