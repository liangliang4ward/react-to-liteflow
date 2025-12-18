package com.aicoding.flow.graph.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
public class GraphModel {
    private List<EdgeModel> edges;
    private List<WorkNode> nodes;

    // Inner class representing each edge
    @Data
    public static class EdgeModel {
        private EdgeData data;
        private String id;
        private String source;
        private String sourceHandle;
        private String target;
        private String targetHandle;
        private String type;
        public String getSourceId(){
            if("source".equals(sourceHandle)){
                return source;
            }
            return source+"-"+sourceHandle;
        }
        public String getTargetId(){
            if("target".equals(targetHandle)){
                return target;
            }
            return target+"-"+targetHandle;
        }
        private int zIndex;
    }

    // Inner class representing the data field in each edge
    @Data
    public static class EdgeData {
        private boolean isInIteration;
        private boolean isInLoop;
        private String loopId;
        private String sourceType;
        private String targetType;
    }

    // New inner class representing the work node
    @Data
    public static class WorkNode {
        private String id;
        private String title;
        private String type;
        private String desc;
        private String parentId;
        private WorkNodeData data;
    }

    @Data
    public static class WorkNodeData{
        private List<BreakCondition> breakConditions;
        private String desc;
        private String errorHandleMode;
        private String logicalOperator;
        private int loopCount;
        private List<LoopVariable> loopVariables;
        private List<InputVariable> variables;
        private List<ConditionCase> cases;
        private String startNodeId;
        private String title;
        private String type;
        private String answer;
        private String url;
        private String method;
        private String requestBody;
        private List<InputVariable> params;
    }

    @Data
    public static class InputVariable {
        private String label;
        private Integer maxLength;
        private String type;
        private String variable;
        private String value;
        private boolean required;
    }

    @Data
    public static class BreakCondition {
        private String comparisonOperator;
        private String id;
        private String value;
        private String varType;
        private List<String> variableSelector;
        public String getVariableSelectorString(){
            if(CollectionUtils.isEmpty(variableSelector)){
                return "";
            }
            return "{{#"+String.join(".",variableSelector)+"#}}";
        }
        private String numberVarType;
    }

    @Data
    public static class LoopVariable {
        private String id;
        private String label;
        private String value;
        private String valueType;
    }

    @Data
    public static class ConditionCase {
        private String caseId;
        private List<BreakCondition> conditions;
        private String id;
        private String logicalOperator;
    }
}