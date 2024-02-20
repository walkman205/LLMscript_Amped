package com.gpt.pojo;

import lombok.Data;

import java.nio.file.Paths;
import java.util.HashMap;

@Data
public class CheckBean {

    private String resourcePath;

    private HashMap<String, Boolean> checkData;

    private String localName;

    private String cloudName;

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public HashMap<String, Boolean> getCheckData() {
        return checkData;
    }

    public void setCheckData(HashMap<String, Boolean> checkData) {
        this.checkData = checkData;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getQuestionPath() {
        return Paths.get(resourcePath).resolve("questions").toString();
    }

    public String getPrepromptPath() {
        return Paths.get(resourcePath).resolve("preprompts").toString();
    }
}
