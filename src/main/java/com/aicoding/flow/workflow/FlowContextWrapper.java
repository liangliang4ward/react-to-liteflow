package com.aicoding.flow.workflow;

import com.aicoding.flow.workflow.model.InputParam;
import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/21 11:02
 **/
@Data
public class FlowContextWrapper {

    private List<InputParam> userInputs;
}
