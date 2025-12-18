package com.aicoding.flow.workflow.model;

import lombok.Data;

/**
 * @author gaoll
 * @time 2025/5/21 11:39
 **/
@Data
public class MessageModel {

    private String event;

    private String answer;

    private String nodeId;

    private String nodeName;
}
