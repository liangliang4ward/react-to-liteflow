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

        // 构建节点映射
        Map<String, GraphModel.WorkNode> nodeMap = model.getNodes().stream()
                .collect(Collectors.toMap(GraphModel.WorkNode::getId, node -> node));

        // 构建边关系映射
        Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap = model.getEdges().stream()
                .collect(Collectors.groupingBy(GraphModel.EdgeModel::getSource));

        // 查找开始节点
        GraphModel.WorkNode startNode = findStartNode(model.getNodes());
        if (startNode == null) {
            throw new RuntimeException("未找到开始节点");
        }

        // 生成节点绑定语句
        String nodeBindings = generateNodeBindings(model.getNodes());

        // 生成流程控制语句
        String flowExpression = buildExpressionRecursive(startNode.getId(), new HashSet<>(), nodeMap, edgeBySourceMap);

        // 组合完整的 EL 表达式
        return nodeBindings + ";" + flowExpression;
    }

    /**
     * 验证模型的有效性
     * @param model 流程图模型
     */
    private void validateModel(GraphModel model) {
        if (model == null) {
            throw new RuntimeException("模型不能为空");
        }
        if (model.getNodes() == null || model.getNodes().isEmpty()) {
            throw new RuntimeException("模型中没有节点");
        }
        if (model.getEdges() == null) {
            throw new RuntimeException("模型中边信息不能为空");
        }
    }

    /**
     * 查找开始节点
     * @param nodes 节点列表
     * @return 开始节点
     */
    private GraphModel.WorkNode findStartNode(List<GraphModel.WorkNode> nodes) {
        return nodes.stream()
                .filter(node -> "start".equals(node.getType()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 生成节点绑定语句
     * @param nodes 节点列表
     * @return 节点绑定语句字符串
     */
    private String generateNodeBindings(List<GraphModel.WorkNode> nodes) {
        StringBuilder bindingsBuilder = new StringBuilder();

        for (int i = 0; i < nodes.size(); i++) {
            GraphModel.WorkNode node = nodes.get(i);
            String boundNodeId = "_" + node.getId();
            String nodeType = getLiteFlowNodeType(node.getType());

            // 构建节点参数 JSON
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("id", node.getId());
            paramMap.put("nodeId", boundNodeId);
            paramMap.put("index", i + 1);
            paramMap.put("streamFlag", true);
            paramMap.put("params", Collections.emptyList());
            paramMap.put("nodeType", node.getType());
            paramMap.put("nodeName", nodeType + "Node");

            try {
                String paramJson = objectMapper.writeValueAsString(paramMap);
                // 转义单引号
                paramJson = paramJson.replace("'", "\\'");

                // 构建绑定语句
                String binding = String.format("%s=%sNode.bind(\"param\',\'%s\')",
                        boundNodeId, nodeType, paramJson);

                if (bindingsBuilder.length() > 0) {
                    bindingsBuilder.append(";").append(binding);
                } else {
                    bindingsBuilder.append(binding);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("生成节点绑定语句失败，节点 ID: " + node.getId(), e);
            }
        }

        return bindingsBuilder.toString();
    }

    /**
     * 获取 LiteFlow 节点类型
     * @param reactFlowNodeType react-flow 节点类型
     * @return LiteFlow 节点类型
     */
    private String getLiteFlowNodeType(String reactFlowNodeType) {
        // 节点类型映射
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("start", "Start");
        typeMap.put("end", "End");
        typeMap.put("answer", "Common");
        typeMap.put("http", "Http");
        typeMap.put("if-else", "Condition");
        typeMap.put("code", "Code");
        typeMap.put("json-extract", "JsonExtract");
        typeMap.put("while-condition", "WhileCondition");

        String liteFlowType = typeMap.get(reactFlowNodeType);
        if (liteFlowType == null) {
            throw new RuntimeException("不支持的节点类型: " + reactFlowNodeType);
        }
        return liteFlowType;
    }

    /**
     * 递归构建流程表达式
     * @param currentNodeId 当前节点 ID
     * @param visited 已访问节点集合
     * @param nodeMap 节点映射
     * @param edgeBySourceMap 边关系映射
     * @return 流程表达式字符串
     */
    private String buildExpressionRecursive(String currentNodeId, Set<String> visited, 
                                            Map<String, GraphModel.WorkNode> nodeMap, 
                                            Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap) {
        if (visited.contains(currentNodeId)) {
            throw new RuntimeException("检测到循环引用: " + currentNodeId);
        }
        visited.add(currentNodeId);

        GraphModel.WorkNode currentNode = nodeMap.get(currentNodeId);
        if (currentNode == null) {
            throw new RuntimeException("节点不存在: " + currentNodeId);
        }

        String currentBoundId = "_" + currentNodeId;
        List<GraphModel.EdgeModel> outgoingEdges = edgeBySourceMap.getOrDefault(currentNodeId, Collections.emptyList());

        // 根据节点类型和边关系构建表达式
        if ("if-else".equals(currentNode.getType())) {
            return buildIfElseExpression(currentNodeId, visited, nodeMap, edgeBySourceMap, outgoingEdges);
        } else if (outgoingEdges.size() == 0) {
            // 没有后续节点，直接返回当前节点
            visited.remove(currentNodeId);
            return currentBoundId;
        } else if (outgoingEdges.size() == 1) {
            // 单个后续节点，构建 THEN 表达式
            String nextNodeId = outgoingEdges.get(0).getTarget();
            String nextExpression = buildExpressionRecursive(nextNodeId, visited, nodeMap, edgeBySourceMap);
            visited.remove(currentNodeId);
            return String.format("THEN(%s, %s)", currentBoundId, nextExpression);
        } else {
            // 多个后续节点，先执行当前节点，然后并行执行后续分支
            List<String> parallelExpressions = new ArrayList<>();
            for (GraphModel.EdgeModel edge : outgoingEdges) {
                String parallelNodeId = edge.getTarget();
                parallelExpressions.add(buildExpressionRecursive(parallelNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap));
            }
            visited.remove(currentNodeId);
            return String.format("THEN(%s, WHEN(%s))", currentBoundId, String.join(", ", parallelExpressions));
        }
    }

    /**
     * 构建 IF-ELSE 条件表达式
     * @param ifNodeId if 节点 ID
     * @param visited 已访问节点集合
     * @param nodeMap 节点映射
     * @param edgeBySourceMap 边关系映射
     * @param outgoingEdges 出边列表
     * @return IF-ELSE 表达式字符串
     */
    private String buildIfElseExpression(String ifNodeId, Set<String> visited, 
                                         Map<String, GraphModel.WorkNode> nodeMap, 
                                         Map<String, List<GraphModel.EdgeModel>> edgeBySourceMap, 
                                         List<GraphModel.EdgeModel> outgoingEdges) {
        if (outgoingEdges.size() < 1) {
            throw new RuntimeException("if-else 节点[id: " + ifNodeId + "]未配置分支目标节点");
        }

        StringBuilder ifBuilder = new StringBuilder();
        String ifBoundId = "_" + ifNodeId;

        ifBuilder.append("IF(");
        ifBuilder.append(ifBoundId);

        // 处理分支
        for (int i = 0; i < outgoingEdges.size(); i++) {
            GraphModel.EdgeModel edge = outgoingEdges.get(i);
            String targetNodeId = edge.getTarget();
            String branchExpression = buildExpressionRecursive(targetNodeId, new HashSet<>(visited), nodeMap, edgeBySourceMap);

            if (i == 0) {
                ifBuilder.append(", " + branchExpression);
            } else {
                ifBuilder.append(", " + branchExpression);
            }
        }

        ifBuilder.append(")");

        return ifBuilder.toString();
    }
}
