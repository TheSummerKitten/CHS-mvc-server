package com.kitten.chs.web.controller;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author kitten
 */
@Data
public class User {

    // 用户名
    private String username;
    // 性别
    private Integer sex;

    // 创建时间
    private LocalDateTime createTime;
    // 更新日期
    private LocalDate updateDate;
    // 时间
    private LocalTime time;

}
