package com.gpt.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

@Data
@Component
public class GptAiUtil {
    String gpt35Url = "https://api.openai.com/v1/chat/completions";
    String gpt35Key;
    String gpt35Model = "gpt-3.5-turbo";

    String gpt4Url = "https://api.openai.com/v1/chat/completions";
    String gpt4Key;
    String gpt4Model = "gpt-4";

    String gpt4FileUrl = "http://localhost:1234/v1/chat/completions";
    String gpt4FileKey;
    String gpt4FileModel = "local-model";

    String localUrl;

    String cloudUrl;

    @Autowired
    private HttpUtil httpUtil;


    /**
     * @param type 模型类型
     */
    public String send(int type, String prompt, String preprompt, File promptFile) throws Exception {

        if (preprompt.contains("%%%")) {
            //存在替换符，进行替换
            String[] strings = preprompt.split("%%%");
            preprompt = strings[0];
            if (strings.length == 1) {
                preprompt += prompt;
            } else {
                preprompt = preprompt + prompt + strings[1];
            }
        }

        if (type == 1) {
            //gpt3.5模型
            if (gpt35Key == null) {
            //    throw new RuntimeException("key获取失败");
                throw new RuntimeException("failed to get key for GPT3.5");
            }
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("model", gpt35Model);
            ArrayList<Object> messages = new ArrayList<>();
            HashMap<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", preprompt);
            messages.add(msg1);
            HashMap<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", prompt);
            messages.add(msg2);
            reqMap.put("messages", messages);
            String sendRes = httpUtil.post(gpt35Url, gpt35Key, JSON.toJSONString(reqMap));
            return sendRes;
        } else if (type == 2) {
            //gpt4模型
            if (gpt4FileKey==null){
            //    throw new RuntimeException("key获取失败");
                throw new RuntimeException("failed to get key for GPT4");
            }
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("model", gpt4Model);
            ArrayList<Object> messages = new ArrayList<>();
            HashMap<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", preprompt);
            messages.add(msg1);
            HashMap<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", prompt);
            messages.add(msg2);
            reqMap.put("messages", messages);
            String sendRes = httpUtil.post(gpt4Url, gpt4Key, JSON.toJSONString(reqMap));
            return sendRes;
        }else if (type == 3) {
            //gpt4v型
            HashMap<String, Object> reqMap = new HashMap<>();
            reqMap.put("model", gpt4FileModel);
            ArrayList<Object> messages = new ArrayList<>();
            HashMap<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", preprompt);
            messages.add(msg1);
            HashMap<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", prompt);
            messages.add(msg2);
            reqMap.put("messages", messages);
            String sendRes = httpUtil.post(gpt4FileUrl, gpt4FileKey, JSON.toJSONString(reqMap));
            return sendRes;
        } else if (type == 4) {
            //gpt4v型
            HashMap<String, Object> reqMap = new HashMap<>();
            ArrayList<Object> messages = new ArrayList<>();
            HashMap<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", preprompt);
            messages.add(msg1);
            HashMap<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", prompt);
            messages.add(msg2);
            reqMap.put("messages", messages);
            String sendRes = httpUtil.post(localUrl, null, JSON.toJSONString(reqMap));
            return sendRes;
        }else if (type == 5) {
            //gpt4v型
            HashMap<String, Object> reqMap = new HashMap<>();
            ArrayList<Object> messages = new ArrayList<>();
            HashMap<String, String> msg1 = new HashMap<>();
            msg1.put("role", "system");
            msg1.put("content", preprompt);
            messages.add(msg1);
            HashMap<String, String> msg2 = new HashMap<>();
            msg2.put("role", "user");
            msg2.put("content", prompt);
            messages.add(msg2);
            reqMap.put("messages", messages);
            String sendRes = httpUtil.post(cloudUrl, null, JSON.toJSONString(reqMap));
            return sendRes;
        }
        return null;
    }
}
