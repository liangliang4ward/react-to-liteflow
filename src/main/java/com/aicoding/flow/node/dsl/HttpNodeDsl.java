package com.aicoding.flow.node.dsl;

import lombok.Data;

import java.util.List;

/**
 * @author gaoll
 * @time 2025/5/22 16:44
 **/
@Data
public class HttpNodeDsl extends CommonNodeDsl{

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求体
     */
    private String body;

    /**
     * 请求头
     */
    private List<HttpHeader> headers;

    /**
     * 请求参数
     */
    private List<HttpParam> reqParams;

    @Data
    public static class HttpParam{
        private String name;
        private String value;
    }
    @Data
    public static class HttpHeader{
        private String name;
        private String value;
    }
}
