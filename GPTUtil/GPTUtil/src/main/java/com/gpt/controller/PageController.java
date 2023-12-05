package com.gpt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wl
 * @date 2023/7/13 14:18
 * @Description
 */
@RequestMapping("/")
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

}
