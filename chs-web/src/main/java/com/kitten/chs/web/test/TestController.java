package com.kitten.chs.web.test;

import com.kitten.chs.common.aspect.ApiOperationLog;
import com.kitten.chs.common.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kitten
 */
@RestController
@Slf4j
public class TestController {

    @PostMapping("/test")
    @ApiOperationLog(description = "测试接口")
    public Response test(@RequestBody User user) {
        // 返参
        return Response.success(user);
    }

}
