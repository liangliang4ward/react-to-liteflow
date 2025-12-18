package com.aicoding.flow.utils;

import com.aicoding.flow.constants.VarConstant;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableExtractor {
    private static Pattern pattern = Pattern.compile("\\{\\{#([^#]+)#}}");


    public static void main(String[] args) {

        List<RespModel> s = extractorResp("年轻人\n\n他的名字是：{{#1747878290364.name#}}");
        for (RespModel respModel : s) {
            System.out.println(respModel.getValue());
        }
    }


    public static List<RespModel> extractorResp(String lineString) {
        List<RespModel> strs = new ArrayList<>();


        Matcher matcher = pattern.matcher(lineString);
        int lastEnd = 0;

        while (matcher.find()) {
            // 添加匹配前的文本部分（包含换行符）
            if (matcher.start() > lastEnd) {
                strs.add(new RespModel(VarConstant.TEXT, lineString.substring(lastEnd, matcher.start())));
            }

            // 添加匹配的选择器部分
            strs.add(new RespModel(VarConstant.SELECTOR, matcher.group(0)));
            lastEnd = matcher.end();
        }

        // 添加剩余的文本部分（包含最后的换行符）
        if (lastEnd < lineString.length()) {
            strs.add(new RespModel(VarConstant.TEXT, lineString.substring(lastEnd)));
        }

        return strs;
    }

    @Data
    public static class RespModel {
        private String type;
        private String value;

        public RespModel(String type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "类型为：" + type + "\n" + escapeNewlines(value);
        }

        private String escapeNewlines(String s) {
            // 转义换行符以便在控制台正确显示
            return s.replace("\n", "\\n\n");
        }
    }
}