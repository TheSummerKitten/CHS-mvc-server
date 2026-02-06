package com.kitten.chs.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author kitten
 */
@Slf4j
@SpringBootApplication
@ComponentScan({"com.kitten.chs.*"})
public class CHSApplication {

    public static void main(String[] args) {
        SpringApplication.run(CHSApplication.class, args);
        log.info("💉💉💉CHS Server 启动成功!!! 💉💉💉");
    }

}
