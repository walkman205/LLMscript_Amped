package com.gpt.pojo;

import lombok.Data;

import java.util.HashMap;

@Data
public class CheckBean {

    private String questionPath;

    private String prepromptPath;

    private HashMap<String, Boolean> checkData;

    private String localName;

    private String cloudName;
}
