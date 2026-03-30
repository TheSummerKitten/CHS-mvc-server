package com.kitten.chs.web.controller;

import com.kitten.chs.admin.model.vo.req.FindUserPageConListReqVO;
import com.kitten.chs.admin.model.vo.rsp.FindUserPageConListRespVO;
import com.kitten.chs.common.utils.PageResponse;
import com.kitten.chs.common.utils.Response;
import com.kitten.chs.web.model.req.RegisterReqVO;
import com.kitten.chs.web.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kitten
 */

@RestController
@RequestMapping("/user")
@Api(tags = "Web 用户模块")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Response<?> register(@RequestBody RegisterReqVO reqVO) {
        return userService.register(reqVO);
    }

}
