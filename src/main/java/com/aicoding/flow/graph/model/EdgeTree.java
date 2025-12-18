package com.aicoding.flow.graph.model;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author gaoll
 * @time 2025/5/27 15:37
 **/
@Data
public class EdgeTree {

    private String id;

    private List<EdgeTree> children;

    private String type;




    public List<EdgeTree> getLastChildren() {
        //如果没有子节点，那么就没有叶子节点
        if (CollectionUtils.isEmpty(children)) {
            return new ArrayList<>();
        }
        //如果只有一个子节点，那么下一个节点就是叶子节点
        if (children.size() == 1) {
            return children;
        }
        List<EdgeTree> trees = new ArrayList<>();
        for (EdgeTree child : children) {
            trees.addAll(child.getLastChildren());
        }
        return trees;

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EdgeTree edgeTree = (EdgeTree) o;
        return Objects.equals(id, edgeTree.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
