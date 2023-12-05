package com.gpt.controller;

import com.gpt.service.GptServiceImpl;
import com.gpt.utils.GptAiUtil;
import com.gpt.utils.GptHttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
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

    @GetMapping("check")
    private GptHttpResponse check(@RequestParam("path") String path) {
        String str = new String(Base64.getDecoder().decode(path));
        List check = gptServiceImpl.check(str);
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
    private GptHttpResponse generate(@RequestParam(value = "prePrompt",required = true) String prePrompt) {
        try {
            gptServiceImpl.generate(prePrompt,null);
            return GptHttpResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return GptHttpResponse.fail("Generate Fail!",null);
        }
    }

    @GetMapping("generateSocket")
    private GptHttpResponse generateSocket(@RequestParam(value = "prePrompt",required = true) String prePrompt) {
        try {
            gptServiceImpl.generate(prePrompt,null);
            return GptHttpResponse.success(null);
        } catch (Exception e) {
            e.printStackTrace();
            return GptHttpResponse.fail("Generate Fail!",null);
        }
    }
}
