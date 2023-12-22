package com.gpt.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;

@Component
@Data
public class HttpUtil {
    public String post(String url, String key, String json) {
        System.out.println("发送json："+json);
        StringBuilder res = new StringBuilder();
        String content = "";
        try {
            HttpHost proxy = new HttpHost("127.0.0.1", 7890);
            // 设置连接和套接字超时（单位：毫秒）
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(60000)
                    .setConnectTimeout(60000)  // 连接超时时间为30秒
                    .setSocketTimeout(60000)   // 套接字超时时间为30秒
//                    .setProxy(proxy) //TODO 代理
                    .build();

            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            // 创建HttpPost对象，并设置请求URL
            HttpPost httpPost = new HttpPost(url);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + key);

            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Origin", "https://platform.openai.com");
            httpPost.setHeader("Origin", "https://platform.openai.com");
            httpPost.setHeader("Referer", "https://platform.openai.com/");

            // 设置请求体
            StringEntity entity = new StringEntity(json);
            httpPost.setEntity(entity);

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);

            // 获取响应码
            int responseCode = response.getStatusLine().getStatusCode();

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            reader.close();

            // 输出响应结果
            System.out.println("Response Body: " + res.toString());

            HashMap repMap = JSON.parseObject(res.toString(), HashMap.class);
             content = ((JSONObject) (((JSONObject) ((JSONArray) repMap.get("choices")).get(0)).get("message"))).get("content").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }

    public String postFile(String url, String key, String preprompt, File promptFile) {
        System.out.println("发送 preprompt："+ preprompt);
        StringBuilder res = new StringBuilder();
        String content = "";
        try {
            HttpHost proxy = new HttpHost("127.0.0.1", 7890);
            // 设置连接和套接字超时（单位：毫秒）
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(60000)
                    .setConnectTimeout(60000)  // 连接超时时间为30秒
                    .setSocketTimeout(60000)   // 套接字超时时间为30秒
                    .setProxy(proxy)
                    .build();

            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build();

            // 创建HttpPost对象，并设置请求URL
            HttpPost httpPost = new HttpPost(url);

            // 设置请求头
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Authorization", "Bearer " + key);

            httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Origin", "https://platform.openai.com");
            httpPost.setHeader("Origin", "https://platform.openai.com");
            httpPost.setHeader("Referer", "https://platform.openai.com/");

            // 构建 multipart 请求体
            HttpEntity requestEntity = MultipartEntityBuilder.create()
                    .addTextBody("prompt", preprompt)
                    .addBinaryBody("file", promptFile, ContentType.DEFAULT_BINARY, promptFile.getName())
                    .build();

            httpPost.setEntity(requestEntity);

            // 发送请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);

            // 获取响应码
            int responseCode = response.getStatusLine().getStatusCode();

            // 读取响应内容
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line);
            }
            reader.close();

            // 输出响应结果
            System.out.println("Response Body: " + res.toString());

            HashMap repMap = JSON.parseObject(res.toString(), HashMap.class);
            content = ((JSONObject) (((JSONObject) ((JSONArray) repMap.get("choices")).get(0)).get("message"))).get("content").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;

    }
}
