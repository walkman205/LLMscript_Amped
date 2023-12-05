package com.gpt.utils;

import lombok.Data;

import java.util.List;

@Data
public class GptHttpResponse {
    private String code;
    private String msg;
    private Object data;

    public static GptHttpResponse success(Object data){
        GptHttpResponse gptHttpResponse = new GptHttpResponse();
        gptHttpResponse.code = "1";
        gptHttpResponse.msg = "SUCCESS";
        gptHttpResponse.data = data;
        return gptHttpResponse;
    }

    public static GptHttpResponse fail(String msg, List data) {
        GptHttpResponse gptHttpResponse = new GptHttpResponse();
        gptHttpResponse.code = "0";
        gptHttpResponse.msg = msg;
        gptHttpResponse.data = data;
        return gptHttpResponse;    }
}
