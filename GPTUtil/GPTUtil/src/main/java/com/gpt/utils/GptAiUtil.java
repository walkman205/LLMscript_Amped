package com.gpt.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.common.util.concurrent.*;
import java.util.concurrent.*;


@Data
@Component
public class GptAiUtil {
    String gpt35Url = "https://api.openai.com/v1/chat/completions";
    String gpt35Key;
    String gpt35Model = "gpt-3.5-turbo";

    String gpt4Url = "https://api.openai.com/v1/chat/completions";
    String gpt4Key;
    String gpt4Model = "gpt-4";

    String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";

    String geminiKey;
    String geminiModel = "local-model";

    String localUrl = "http://localhost:1234/v1/chat/completions";
    String localModel = "local=model";

    String cloudUrl;

    @Autowired
    private HttpUtil httpUtil;

    public void setGpt35Key(String gpt35Key) {
        this.gpt35Key = gpt35Key;
    }

    public void setGpt4Key(String gpt4Key) {
        this.gpt4Key = gpt4Key;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public void setGeminiKey(String geminiKey) {
        this.geminiKey = geminiKey;
    }

    public void setCloudUrl(String cloudUrl) {
        this.cloudUrl = cloudUrl;
    }

    /**
     * @param type 模型类型
     */
    public String send(int type, String prompt, String preprompt, File promptFile) throws Exception {

        HashMap<String, Object> reqMap = new HashMap<>();
        ArrayList<Object> messages = new ArrayList<>();

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
            reqMap.put("model", gpt35Model);
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
            if (gpt4Key==null){
            //    throw new RuntimeException("key获取失败");
                throw new RuntimeException("failed to get key for GPT4");
            }
            reqMap.put("model", gpt4Model);
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
            //gemini
            String combinedContent = preprompt + "\n\n" + prompt;
            reqMap.put("model", "gemini-1.5-flash");
            HashMap<String, String> contentMap = new HashMap<>();
            contentMap.put("text", combinedContent);  // Add the user prompt text
            reqMap.put("content", contentMap);

            // Send Gemini API request using HttpUtil
            String sendRes = httpUtil.post(geminiUrl, geminiKey, JSON.toJSONString(reqMap));
            return sendRes;
        } else if (type == 4) {
            //local
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
            //cloud
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
