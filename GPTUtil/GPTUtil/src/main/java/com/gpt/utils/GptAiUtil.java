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
    //input your key here
    String gpt35Key = "";
    String gpt35Model = "gpt-3.5-turbo";

    String gpt4Url = "https://api.openai.com/v1/chat/completions";
    //input your key here
    String gpt4Key = "";
    String gpt4Model = "gpt-4";

    String gpt4FileUrl = "https://api.openai.com/v1/engines/davinci/completions";
    //input your key here
    String gpt4FileKey = "";
    String gpt4FileModel = "gpt-4";

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
            prompt = (strings[1] + prompt);
        }

        if (type == 1) {
            //gpt3.5模型
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
            //gpt3.5模型
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
        } else if (type == 3) {
            String sendRes = httpUtil.postFile(gpt4FileUrl, gpt4FileKey, preprompt, promptFile);
            return sendRes;
        }
        return null;
    }
}
