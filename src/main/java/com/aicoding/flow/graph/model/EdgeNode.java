package com.aicoding.flow.graph.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author gaoll
 * @time 2025/5/28 10:33
 **/
@Data
public class EdgeNode {

    private String id;

    private String type;

    private Integer index;

    private List<EdgeNode> children = new ArrayList<>();

    private Set<String> childrenIds = new HashSet<>();

    private Set<String> parentIds = new HashSet<>();


    public void addChild(EdgeNode child) {
        if(childrenIds.contains(child.getId())){
            return;
        }
        child.getParentIds().add(this.getId());
        childrenIds.add(child.getId());
        children.add(child);
    }


    private void removeParent(String id) {
        this.getParentIds().remove(id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EdgeNode edgeNode = (EdgeNode) o;
        return Objects.equals(id, edgeNode.id) ;

    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public void removeChild(EdgeNode singleParent) {
        //移除节点，断开连接

        this.getChildren().remove(singleParent);
        singleParent.removeParent(this.getId());
        this.getChildrenIds().remove(singleParent.getId());
    }

    /**
     * 从当前节点开始递归简化所有merge节点
     */
    public void simplifyAllMergeNodes(Map<String, EdgeNode> nodeMap) {
        if (!CollectionUtils.isEmpty(this.children)) {
            // 先处理子节点
            for (EdgeNode child : new ArrayList<>(this.children)) {
                child.simplifyAllMergeNodes(nodeMap);
            }

            // 然后处理当前节点
            if ("merge".equals(this.type)) {
                simplifySingleChildMerge(nodeMap);
            }
        }
    }

    /**
     * 简化当前merge节点（如果只有一个子节点）
     */
    private void simplifySingleChildMerge(Map<String, EdgeNode> nodeMap) {
        if (!"merge".equals(this.type)) {
            return;
        }
        if (CollectionUtils.isEmpty(this.children)) {
            return;
        }
        if (this.children.size() != 1){
            return;
        }

        EdgeNode singleChild = this.children.get(0);

        // 1. 转移父节点关系到子节点
        singleChild.getParentIds().addAll(this.parentIds);
        singleChild.getParentIds().remove(this.id); // 移除自引用

        // 2. 更新所有父节点的子引用
        for (String parentId : this.parentIds) {
            EdgeNode parent = nodeMap.get(parentId); // 需要实现
            if (parent != null) {
                parent.getChildren().remove(this);
                parent.getChildrenIds().remove(this.id);
                parent.addChild(singleChild);
            }
        }

        nodeMap.remove(this.getId());
        // 3. 清除当前节点关系
        this.children.clear();
        this.childrenIds.clear();
        this.parentIds.clear();
    }
}
