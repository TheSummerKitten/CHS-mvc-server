package com.kitten.chs.web.controller;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.domain.dataObject.UserDO;
import com.kitten.chs.common.domain.mapper.UserMapper;
import com.kitten.chs.common.utils.JsonUtil;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import javax.annotation.Resource;

/**
 * @author kitten
 */
@RestController
@Slf4j
public class TestController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/test")
    @ApiOperationLog(description = "测试接口")
    public Response test(@RequestBody UserDO user) {
        // 打印入参
        log.info(JsonUtil.toJsonString(user));

        // 设置三种日期字段值
        userMapper.insert(user);


        // 返参
        return Response.success(user);
    }

}
