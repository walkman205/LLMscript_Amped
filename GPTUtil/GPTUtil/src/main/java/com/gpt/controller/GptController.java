package com.gpt.controller;

import com.gpt.pojo.CheckBean;
import com.gpt.service.GptServiceImpl;
import com.gpt.utils.GptAiUtil;
import com.gpt.utils.GptHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * @author wl
 * @date 2023/11/24 10:51
 * @Description
 */
@RestController
@RequestMapping("/gpt")
public class GptController {
    @Autowired
    private GptServiceImpl gptServiceImpl;

    @PostMapping("check")
    private GptHttpResponse check(@RequestBody CheckBean checkBean) {
        gptServiceImpl.checkBean = checkBean;
        HashMap<String, ArrayList<String>> check = gptServiceImpl.check();
        if (check != null) {
            if (check.size() > 0) {
                return GptHttpResponse.success(check);
            } else {
                return GptHttpResponse.fail("No matching files were found in the destination folder\n", check);
            }
        } else {
            return GptHttpResponse.fail("Destination folder not found", check);
        }
    }

    @GetMapping("generate")
    private GptHttpResponse generate(@RequestParam(value = "prePrompt", required = true) String prePrompt) {
        try {
            gptServiceImpl.generate(null);
            return GptHttpResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return GptHttpResponse.fail("Generate Fail!", null);
        }
    }

    @GetMapping("generateSocket")
    private GptHttpResponse generateSocket(@RequestParam(value = "prePrompt", required = true) String prePrompt) {
        try {
            gptServiceImpl.generate(null);
            return GptHttpResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return GptHttpResponse.fail("Generate Fail!", null);
        }
    }
}
