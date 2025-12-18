package com.aicoding.flow.node.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 条件模型
 * @author gaoll
 * @time 2025/5/21 15:34
 **/
@Data
public class ConditionModel {

    /**
     * 操作，不同类型，不同的处理方式
     */
    private String operation;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 左侧数值选择器
     */
    private String leftValueSelector;

    /**
     * 右侧数值类型
     * constant,variable
     */
    private String rightVarType;

    /**
     * 右侧数值选择器
     */
    private String rightValueSelector;

    /**
     * 右侧真实数值
     */
    private Object rightRealValue;

    /**
     * 左侧真实数值
     */
    private Object leftRealValue;


    public boolean isTrue(){
        if(operation.equals("empty")){
            return leftRealValue==null;
        }
        if(operation.equals("not empty")){
            return leftRealValue!=null;
        }
        if(dataType.equals("number")){
            BigDecimal leftValue= new BigDecimal(leftRealValue.toString());
            BigDecimal rightValue=new BigDecimal(rightRealValue.toString());
            if(operation.equals("=")){
                return leftValue.compareTo(rightValue)==0;
            }else if(operation.equals("!=")){
                return leftValue.compareTo(rightValue)!=0;
            }else if(operation.equals("≥")){
                return leftValue.compareTo(rightValue)>=0;
            }else if(operation.equals("≤")){
                return leftValue.compareTo(rightValue)<=0;
            }else if(operation.equals("<")){
                return leftValue.compareTo(rightValue)<0;
            }else if(operation.equals(">")){
                return leftValue.compareTo(rightValue)>0;
            }
        }else if(dataType.equals("string")){
            if(operation.equals("contains")){
                return leftRealValue.toString().contains(rightRealValue.toString());
            }else if(operation.equals("!contains")){
                return !leftRealValue.toString().contains(rightRealValue.toString());
            }
        }

        return false;
    }



}
