package com.kitten.chs.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kitten
 */
@RestController
@Slf4j
public class TestAdminController {

    @GetMapping("/test")
    public String test() {
        log.info("测试接口");
        return "测试接口";
    }

    @GetMapping("/admin/test")
    public String test2() {
        log.info("测试接口2");
        return "测试接口2";
    }
}